package com.digidactylus.recorder.ui;

import static android.content.Context.USB_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;


import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.digidactylus.recorder.JNIWrapRec;
import com.digidactylus.recorder.MainActivity;
import com.digidactylus.recorder.viewmodels.recordTimerViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DataThread {

    boolean m_run = false;
    private MainActivity m_main = null;

    boolean m_newData = false;
    byte[] m_proc;
    boolean m_busy;
    Activity activity;
    recordTimerViewModel recordTimerViewModel;

    int m_frameBLE = 0;
    long m_start_time = 0;

    public DataThread() {
    }

    public void setMain(MainActivity m) {
        m_main = m;
    }

    public void Stop() {

        m_run = false;
        m_busy = false;
    }

    private void Wait() {
        while (m_busy) {
            try {
                Thread.sleep(1);
            } catch (Exception e) {

            }
        }
    }

    private boolean allBad(byte [] dat, int n) {
        // Disable
        //if (true)
          //  return false;

        if (n < 2)
            return false;

        for (int i=0; i<n; i++) {
            if (dat[i] != 0) {
                return false;
            }
        }
        return true;
    }

    private byte[] Process(byte [] dat, int n) {
        int i;
        if (n < 2)
            return null;
        int k = 0;
        int found = 0;
        int should = 1 + n/64;
        byte[] out = new byte[n-2*should];
        int idx = 0;
        for (i=0; i<n; i++) {
            // CHECK for fillers other than 96 and 98!!!!!
            if (k % 64 == 0 && (i+1 < n && dat[i] == 1 && (dat[i+1] == 96 || dat[i+1] == 98))) {
                //System.out.println("Found");
                int x = k % 64;
                found++;
                i++;
                k += 2;
                continue;
            }
            if (k % 64 == 0 && (i+1 < n && dat[i] == 1)) {
                System.out.println("WARNING, %64=0, dat[i]=" + (int)dat[i] + " dat[i+1]=" + (int)dat[i+1]);
            }
            k++;
            out[idx] = dat[i];
            idx++;

        }

        return out;
    }


    public void processBLE(byte [] dat) {
        // Adjust FPS
        FeedView.getFeed().setSkipFrames(5);

        Wait();
        m_busy = true;
        if (m_proc == null || m_proc.length != dat.length+4) {
            m_proc = new byte[dat.length + 4];

            m_proc[0] = 127;
            m_proc[1] = 127;
            m_proc[2] = 127;
            m_proc[3] = 127;
        }
        for (int i = 0; i<dat.length; i++)
            m_proc[i+4] = dat[i];

        m_frameBLE++;
        if (m_frameBLE % 10 == 0) {
            long time= System.currentTimeMillis() - m_start_time;
            String stat = new String();
            stat += time/1000;
            Date date = new Date(time);
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            stat = formatter.format(date);
            m_main.setTime(stat);
        }

        m_busy = false;
        m_newData = true;
    }
    public void stopBLE() {
        m_run = false;
    }

    public void startBLE() {
        //if (!m_run) {
            m_run = true;
            startUpdateThread();
        //}
    }

    //private Thread m_updateThread = null;

    public void startUpdateThread() {
        //if (m_run)
          //  return;
        m_start_time = System.currentTimeMillis();
        new Thread(new Runnable() {
            public void run() {
                boolean b = false;
                // Set up JNI
                JNIWrapRec wrap = new JNIWrapRec();

                //**************************************************************
                // Reset the thing - stupid, but ah well...
                wrap.doReset();
                //=======================================================================

                while (m_run) {
                    int frame = 0;
                    if (m_newData) {
                        // Process data HERE =======================================
                        m_newData = false;

                        Wait();
                        m_busy = true;
                        byte[] proc = new byte[m_proc.length];
                        //System.arraycopy(proc, 0, m_proc, 0, m_proc.length);
                        for (int i = 0; i < m_proc.length; i++)
                            proc[i] = m_proc[i];
                        m_busy = false;

                        wrap.doFeedData(proc, proc.length);
                        int n_feat = 15;
                        // int n_feat = 9;

                        double[] out = wrap.doGetFrames(n_feat);
                        int status = wrap.doIsValid();
                        if (frame > 4)
                            m_main.setRecStatus(status);
                        frame++;
                        if (out != null && out.length > 0) {
                            double x = out[0];
                            x = out[9];
                            FeedView.getFeed().FeedData(out, n_feat);

                            if (FeedView.getFeed().isMove())
                                m_main.setMove();
                            if (FeedView.getFeed().isStart())
                                m_main.setStart();
                            if (FeedView.getFeed().isStop())
                                m_main.setStop();


                        }
                        // =========================================================

                    } else {
                        try {
                            Thread.sleep(2);
                        } catch (Exception e) {

                        }
                    }
                }
            }
        }).start();
        int x = 15;
    }

    public void DoRecord(UsbEndpoint ep, UsbDeviceConnection con, String fileName) {

        m_run = true;
        m_main.setStatus("recording...");
        long start_time= System.currentTimeMillis();
        //String fileName = dir + "/" + file;

        // Refresh rate = 100ms
        FeedView.getFeed().setSkipFrames(0);

        try {

            byte[] dat = new byte[4096];

            int i;
            for (i = 0; i < dat.length; i++)
                dat[i] = 32;

            //FileWriter logg = new FileWriter(fileName + ".log");

            OutputStream outputStream = new FileOutputStream(fileName + ".bin");

            //for (i=0; i<500; i++) {
            int c = 0;
            int frame = 0;

            startUpdateThread();


            //wrap.doSetWorkingDir(dir);

            m_main.setRecStatus(1);

            MLog.log("Start recording...");

            while (m_run) {

                int n = con.bulkTransfer(ep, dat, dat.length, 10000);
                //Log.d("kjldsqfdsfdsfds", String.valueOf(n));

                if (n > 0) {
                    if (allBad(dat, n)) {
                        m_main.setTime("** NO DATA **");
                        Thread.sleep(10);
                        continue;
                    }
                    frame++;
                    if (frame % 5 == 0) {
                        long time= System.currentTimeMillis() - start_time;
                        String stat = new String();
                        stat += time/1000;
                        Date date = new Date(time);
                        DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
                        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                        stat = formatter.format(date);
                        m_main.setTime(stat);
                    }
                }


                // Stupid way of writing...
                Wait();
                m_busy = true;
                m_proc = Process(dat, n);
                m_busy = false;
                if(m_proc != null){
                    m_newData = true;
                }



                if (m_proc != null && m_proc.length > 0) {
                    if (allBad(m_proc, m_proc.length)) {
                        m_main.setTime("** BAD DATA **");
                        Thread.sleep(10);
                        continue;
                    }

                    outputStream.write(m_proc);
                }

            }
            //con.close();
            //logg.close();
            outputStream.close();

        } catch (Exception e) {
            MLog.log("EXCEPTION in recording loop!");
            MLog.log("MESSAGE: " + e.getMessage());
            System.out.println("WARNING: Blahhhh!");
        }
        if (!m_run) {
            MLog.log("Stopped recording normally");
        } else {
            MLog.log("WARNING: Stopped recording abnormally");
        }
        m_run = false;
        m_main.setStatus("stopped");
        MLog.log("Stopped recording normally");

    }



}
