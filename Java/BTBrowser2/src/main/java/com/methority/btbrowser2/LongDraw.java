/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.methority.btbrowser2;

/**
 *
 * @author manfred
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import java.io.File; 
import java.util.Scanner; 

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



public class LongDraw {
    private JFrame frame;
    private Drawing drawing;
    private String m_dataDir;
    private String m_annot;

    private String [] m_fileName;
    JScrollPane scroll;
    HeatmapFrame m_heat = null;
            
    //double [] data;
    int len;
    
    int m_snap;
    int m_selected;
    
    String m_metaFile;
    BlockName m_blockName;  
    
    public void setBlockName(BlockName bl) {
        m_blockName = bl;
    }
    
    public void SetMeta(String s) {
        drawing.SetMeta(s);
    }
    
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new LongDraw()::createAndShowGui);
    }

    public void SetText(String text) {
        drawing.SetText(text);
    }
    
    public void SetMasterScale(double d) {
        drawing.SetMasterScale(d);
        
    }
    
    public void setHeatmap(HeatmapFrame f) {
        m_heat = f;

        m_listener.setCycles(m_heat.cycles());
        //m_frame.setCycles(m_heat.cycles());

    }
    
    public int GetSelected() {
        return m_selected;
    }
    
    public void SetSelected(int n) {
        m_selected = n;
        
        drawing.SetSelected(n);
    }
    
    PanelListener m_listener = null;
            
    private void createAndShowGui() {
        m_selected = 0;
        m_snap = 0;
        
        m_fileName = new String[1];
        m_fileName[0] = new String();
        //data = new double[64000];
            
        m_metaFile = new String();
        m_metaFile += "supercycles.txt";
        //m_fileName[0] = "tmp";
        
        m_exec = new File(".").getAbsolutePath();
        m_annot = m_exec + "/annot.txt";

        
        frame = new JFrame(getClass().getSimpleName());

        drawing = new Drawing();

        scroll = new JScrollPane(drawing);

        frame.add(scroll);

        drawing.SetScroll(scroll);
        //scroll.isVisible();
        
        
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        m_listener = new PanelListener();

        drawing.addMouseListener(m_listener);
        drawing.SetMouse(m_listener);
        //double [] rms = new double[64000];
        
        //int [] annot = new int[64000];
        
        len = 0;
        
        int i;
        
        //for (i=0; i<data.length; i++)
          //  data[i] = 0.;
        

        BTData bt = new BTData();
       
        BlockName.main(this);
        
        /*
        try {
            FileWriter fw = new FileWriter("annot.txt");
           
                                        
            fw.write("left_frame right_frame state\n");

            //fw.write("\n");
            fw.close();
        }
        
        catch(IOException e) {
        }*/

        
        //bt.Read("/home/manfred/ExtraDisk/Methority/BTL/dry_electrode/test.out");
        
        /*
        BTData bt = new BTData();

        JFileChooser chooser = new JFileChooser();
        
                      
        chooser.setCurrentDirectory(new File(m_dataDir));

        int userSelection = chooser.showOpenDialog(new JFrame());
        
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToRead = chooser.getSelectedFile();
            System.out.println("Reading file: " + fileToRead.getAbsolutePath());
            m_dataDir = fileToRead.getPath();
 
        
        
            //bt.Read("/home/manfred/ExtraDisk/Methority/BTL/squat_hamstring/RightVL/test.out");
            bt.Read(fileToRead.getAbsolutePath());
        }*/
            
        //bt.Read("/home/manfred/ExtraDisk/Methority/BTL/squat_hamstring/RightVL/test.out");
        //bt.Read("test.out");
        
        //m_fileName = "test.out";
        //bt.Read(m_fileName);
        
        /*
        try {
            File file = 
                new File("/home/manfred/ExtraDisk/Methority/BTL/dry_electrode/test.out"); 
            Scanner sc = new Scanner(file); 
  
            sc.nextLine();
            sc.nextLine();
            
            while (sc.hasNextLine()) {
                String str = sc.nextLine();
                if (str.equals(""))
                    break;
                
                //String[] split = str.split("\\s+");
                String[] split = str.split("\\s+");
                //System.out.println("|" + split[1] + "|\n");
                if (split.length == 0)
                    break;
                if (split[1].equals("Data")) {
                    break;                    
                }
                        

                data[len] = Double.parseDouble(split[1]);
                annot[len] = Integer.parseInt(split[6]);
                rms[len] = Double.parseDouble(split[2]);
                len++;
            }
            
            
        }
    
        catch(Exception e) {
        }
        */
       
       

        // HUD!!
        TheHud.main(this);

        HeatmapFrame.main(this); 
        
        //m_heat.readAnnot("../../out.annot");
        //m_heat.loadImage("../../out.heat.jpg");



        /*drawing.Set(bt.Raw(), bt.Raw().length);
        drawing.SetAnnot(bt.Annot(), bt.Annot().length);
        drawing.SetRMS(bt.RMS(), bt.RMS().length);
        drawing.SetAcc(bt.Acc(), bt.Acc().length);
        drawing.SetGyro(bt.Gyro(), bt.Gyro().length);
        drawing.SetCycle(bt.Cycle(), bt.Cycle().length);

        drawing.SetData(bt);*/
        
        //drawing.Set(data, data.length);
        //drawing.SetAnnot(annot, annot.length);
        //drawing.SetRMS(rms, rms.length);


        /*
        
        drawing
        */
    }
    
    public void blockSelected() {
        String name = new String();
        if (m_heat.hasSelection())
            m_blockName.setVisible(true);
    }
    
    public void blockNotSelected() {
        m_heat.clearSelection();
    }
    
    public void setAddBlock(String name) {
        m_heat.addBlock(name);  
        // CHECK THIS OUT!!!!!!!!!!!!!!!!!
        m_listener.updateHintFile();
    }
    
    public void setScrollPos(int ms) {
        int x = ms/2/8;
        scroll.getHorizontalScrollBar().setValue(x);
        scroll.updateUI();
    }
    
    public void Snap(int d) {
        m_snap = d;
    }

    public void updateHintFile() {
        m_listener.updateHintFile();
    }
    
    public void Recompute(String out, int from) {
        /*try {
            Runtime r = Runtime.getRuntime();
            String ex = m_exec + "/BTAnalyze -i " + m_fileName + " -o " + out + " -hints " + m_annot + " > tmp";
            //String ex = m_exec + "\\BTAnalyze -i " + m_fileName + " -o " + out + " -hints " + m_annot + " > tmp";
            System.out.println(ex);

            Process p = r.exec(ex);
            p.waitFor();

            String yourShellInput = ex;  // or whatever ... 
            String[] commandAndArgs = new String[]{ "/bin/sh", "-c", yourShellInput };
            Runtime.getRuntime().exec(commandAndArgs);
            
 
            //Thread.sleep(3000);
            TimeUnit.SECONDS.sleep(3);

            System.out.println("Done, exit code: " + p.exitValue());
            Read(out);
        } 
        
        catch(Exception e) {
            System.out.println("EXEC EXCEPTION!!!");
        
        }*/
        
        JNIWrap wrap = new JNIWrap();
        
        if (m_selected == m_fileName.length) {
            String allFiles = new String();
            
            for (int i=0; i<m_fileName.length; i++) {
                allFiles += m_fileName[i] + ",";
            }

            wrap.callAnalysis(allFiles, m_metaFile, "", "", "", m_annot, "tmp", 4);

            drawing.ReadMeta(m_metaFile);
                        
            drawing.updateUI();

            // Read supercycles HERE
        } else {
            
            if (from != -1) {
                try {
                    FileWriter fw = new FileWriter(m_annot);
                    fw.write("left_frame right_frame state\n");
                    //String ss = m_fileName[m_selected] + ".hints.db\n"
                    fw.write(m_fileName[from] + ".hints.db\n");
                    fw.close();
                }
                catch(IOException e) {
                }
            }

            
            
            
            String annot = m_fileName[m_selected] + ".annot.txt";
            wrap.callAnalysis(m_fileName[m_selected], m_fileName[m_selected], "", "", "", m_annot, annot, 4);
        
            
            for (int i=0; i<m_fileName.length; i++)
                drawing.ReadMult(m_fileName[i], i, 0);
        }

        //==============================================================================
        // FILE NAMES ARE DIFFERENT!!!
        System.out.println("Read from " + m_fileName[m_selected]);
        m_heat.readAnnot(m_fileName[m_selected] + ".annot");
        m_heat.loadImage(m_fileName[m_selected] + ".heat.jpg");
        m_heat.repaint();
    
    }
    
    public void Print(String name) {
        drawing.Print(name);
    }
    
    
    public void Read(String fileName) {
        drawing.Read(fileName, 1);
        try {        
            //Thread.sleep(2000);
            //TimeUnit.SECONDS.sleep(3);
                        
            //TimeUnit.SECONDS.sleep(3);

        }
        catch(Exception e) {
            System.out.println("Could not sleep!!!");
            
        }
        m_fileName = new String[1];
        m_fileName[0] = new String();
        m_fileName[0] = fileName;
    }
    
    private String m_exec;

    public void ReadRaw(String in, String out) {
        /*
        try {
            Runtime r = Runtime.getRuntime();
            //String ex = m_exec + "/BTAnalyze -i " + in + " -o " + out + " > tmp";
            String ex = m_exec + "\\BTAnalyze.exe -i " + in + " -o " + out + " > tmp";
            System.out.println(ex);

            Process p = r.exec(ex);
            p.waitFor();

            String yourShellInput = ex;  // or whatever ... 
            //String[] commandAndArgs = new String[]{ "/bin/sh", "-c", yourShellInput };
            String[] commandAndArgs = new String[]{ "/bin/sh", "-c", yourShellInput };
            Runtime.getRuntime().exec(commandAndArgs);
            
 
            //Thread.sleep(3000);
            TimeUnit.SECONDS.sleep(3);
            System.out.println("Done, exit code: " + p.exitValue());
        } 
        
        catch(Exception e) {
            System.out.println("EXEC EXCEPTION!!!");
        
        }*/
        
        JNIWrap wrap = new JNIWrap();

        String annot = out + ".annot.txt";

        if (!in.contains(".processed.txt"))
            wrap.callAnalysis(in, out, "", "", "", "", annot, 4);
        m_fileName = new String[1];
        m_fileName[0] = new String();
        m_fileName[0] = out;
        drawing.ClearMeta();
        drawing.Read(out, 1);

        String fill = new String();
        if (!in.contains(".processed.txt"))
            fill = ".processed.txt";
        m_heat.readAnnot(in + fill + ".annot");
        m_heat.loadImage(in + fill + ".heat.jpg");
        m_heat.repaint();
        //m_heat.readAnnot("../../out.annot");
        //m_heat.loadImage("../../out.heat.jpg");

        
    }

    void SetMult(int n, String path) {
        m_fileName = new String[n];
        
        for (int i=0; i<n; i++)
            m_fileName[i] = new String();
 
        drawing.SetMult(n);
        
        m_metaFile = path + "supercycles.txt";
        
    }

    public void ReadRawMult(String fileName, String outName, int channel) {
        JNIWrap wrap = new JNIWrap();

        String annot = outName + ".annot.txt";

        wrap.callAnalysis(fileName, outName, "", "", "", "", annot, 4);
        //m_fileName = outName + ".mult";
 
        
        m_fileName[channel] = outName;
 
        drawing.ClearMeta();

        drawing.ReadMult(outName, channel, 1);

        //??????????????????????????????????????????????
        //m_heat.readAnnot(m_fileName[channel] + ".annot");
        //m_heat.loadImage(m_fileName[channel] + ".heat.jpg");
        //m_heat.repaint();
       

    }

    public void ShowMag(int n) {
        drawing.ShowMag(n);
        drawing.updateUI();
    }
    
    public void ReadMult(String fileName, int channel) {
        //JNIWrap wrap = new JNIWrap();

        //String annot = outName + ".annot.txt";

        //wrap.callAnalysis(fileName, outName, "", "", "", "", annot, 4);
        //m_fileName = outName;
        m_fileName[channel] = fileName;
            
        System.out.println("Read mult: " + fileName);

        drawing.ReadMult(fileName, channel, 1);

        
                        
        File tempFile = new File(m_metaFile);
        
        boolean exists = tempFile.exists();
                
        if (exists) {
            drawing.ReadMeta(m_metaFile);
        } else {
            drawing.ClearMeta();
        }
                 
    }
    
    
    
    class Channel {
        public double [] data;
        public int [] annot;
        public double [] rms;
        public double [] acc;
        public double [] accX;
        public double [] accY;
        public double [] accZ;
        public double [] gyro;
        public double [] gyroX;
        public double [] gyroY;
        public double [] gyroZ;
        public String [] cycle;
        public String label;
        
        Channel() {
            //data = new double[15];
            label = new String();
            label = "Channel";
        }
    }

    
    
    
    class Drawing extends JPanel {
        private double [] m_data;
        private int [] m_annot;
        private double [] m_rms;
        private double [] m_acc;
        private double [] m_gyro;
        private double [] m_acc_X;
        private double [] m_acc_Y;
        private double [] m_acc_Z;
        private double [] m_gyro_X;
        private double [] m_gyro_Y;
        private double [] m_gyro_Z;
        private String [] m_cycle;
        
        private double m_master = 1.;
        
        private Channel [] m_channels;
        
        private int left;
        private int right;
        
        private BTData m_bt;
        private TheHud hud;
         
        private String m_text;
        PanelListener listener;
                
        private int m_div;
        
        private int m_update;

        JScrollPane scroll;
        int m_prefSize;

        boolean m_printAll;

        private BTData [] m_spectra_bt;

        double m_startTime;
        
        int m_prefSizeY;
        
        int m_selected;
    
        class MetaCycle {
            MetaCycle() {
                start = 0;
                stop = 0;
            }
            
            public double start;
            public double stop;
            public String name;
        }
        
        MetaCycle [] m_meta;
        
        public void SetMasterScale(double d) {
            m_master = d;

            drawing.updateUI();

        }
        
        public void SetMeta(String s) {
            listener.SetMeta(s);
        }
        
        Drawing() {
            left = -1;
            right = -1;
            m_text = new String();
            m_update = 1;
            m_prefSize = 20000;
            m_printAll = false;
            
            m_channels = new Channel[1];
            m_channels[0] = new Channel();
            //m_channels[0].data = new double[100];

            m_spectra_bt = new BTData[1];
            m_spectra_bt[0] = new BTData();
            
            m_div = 8;

            m_startTime = 0.;
            m_prefSizeY = 700;
            m_selected = 0;
        }
        
        public void ReadMeta(String fileName) {
            int count = 0;
            try {
                File file = 
                    new File(fileName); 
                Scanner sc = new Scanner(file); 
  
               
                //System.out.println("Enter main");        
                while (sc.hasNextLine()) {
                  String str = sc.nextLine();
                  count++;
                }
            }
       
            catch(Exception e) {
                System.out.println("EXCEPTION  Multi-read!!!!");  
            }

            try {
                int k = 0;
                m_meta = new MetaCycle[count];
                
                for (int i=0; i<count; i++)
                    m_meta[i] = new MetaCycle();
                
                File file = 
                    new File(fileName); 
                Scanner sc = new Scanner(file); 
  
          

                while (sc.hasNextLine()) {
                    String str = sc.nextLine();
                    if (str.equals(""))
                        continue;
                    String[] splited = str.split("\\s+");
                    
                    m_meta[k].name = splited[2];
                    
                    m_meta[k].start = Double.parseDouble(splited[0]);
                    m_meta[k].stop = Double.parseDouble(splited[1]);
                    
                    k++;
                }
            }
       
            catch(Exception e) {
                System.out.println("EXCEPTION  Multi-read 22222!!!!");  
            }

        
        }
        
        public void SetSelected(int n) {
            m_selected = n;
            this.listener.SetSelected(n);

            // Update heat map!
            m_heat.readAnnot(m_fileName[m_selected] + ".annot");
            m_heat.loadImage(m_fileName[m_selected] + ".heat.jpg");
            m_heat.repaint();

            //m_selected = n;
            //this.listener.SetSelected(n);
        }
        
        int m_mag = 0;
        
        public void ShowMag(int n) {
            m_mag = n;
        }
        
        void SetScroll(JScrollPane p) {
            scroll = p;
            //scroll.getViewport().addMouseListener(new ListenAdditionsScrolled());
            
            /*
            scroll.hvalueProperty().addListener(new ChangeListener<Number>() {
                public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
                    System.out.println(new_val.intValue());
                }
            });*/        

            
        }
        
        private int lastScrollX = 0;
        
        
        public void SetMouse(PanelListener listener) {
            this.listener = listener;
        }

        void SetMult(int n) {
            m_channels = new Channel[n];
            m_spectra_bt = new BTData[n];
            for (int i=0; i<n; i++) {
                m_channels[i] = new Channel();
                m_spectra_bt[i] = new BTData();
            }
        }
        
        public void ReadMult(String fileName, int channel, int clear) {
            m_update = 0;
            m_bt = new BTData();
            m_bt.Read(fileName);
            m_prefSize = 100;
            //m_startTime = m_bt.StartTime();
            
            Set(m_bt.Raw(), m_bt.Raw().length);
            SetAnnot(m_bt.Annot(), m_bt.Annot().length);
            SetRMS(m_bt.RMS(), m_bt.RMS().length);
            SetAcc(m_bt.Acc(), m_bt.Acc().length);
            SetGyro(m_bt.Gyro(), m_bt.Gyro().length);
            SetCycle(m_bt.Cycle(), m_bt.Cycle().length);

            SetAccX(m_bt.AccX(), m_bt.AccX().length);
            SetAccY(m_bt.AccY(), m_bt.AccY().length);
            SetAccZ(m_bt.AccZ(), m_bt.AccZ().length);
            SetGyroX(m_bt.GyroX(), m_bt.GyroX().length);
            SetGyroY(m_bt.GyroY(), m_bt.GyroY().length);
            SetGyroZ(m_bt.GyroZ(), m_bt.GyroZ().length);

            m_channels[channel].label = fileName;

            m_update = 1;

           
            m_spectra_bt[channel].CopyInSpectra(m_bt);

           
            m_channels[channel].data = new double[m_data.length];
            m_channels[channel].annot = new int[m_annot.length];
            m_channels[channel].rms = new double[m_rms.length];
            m_channels[channel].acc = new double[m_acc.length];
            m_channels[channel].gyro = new double[m_gyro.length];
            m_channels[channel].cycle = new String[m_cycle.length];

            m_channels[channel].accX = new double[m_acc_X.length];
            m_channels[channel].accY = new double[m_acc_Y.length];
            m_channels[channel].accZ = new double[m_acc_Z.length];
            m_channels[channel].gyroX = new double[m_gyro_X.length];
            m_channels[channel].gyroY = new double[m_gyro_Y.length];
            m_channels[channel].gyroZ = new double[m_gyro_Z.length];


            System.arraycopy(m_data, 0, m_channels[channel].data, 0, m_data.length);
            System.arraycopy(m_annot, 0, m_channels[channel].annot, 0, m_annot.length);
            System.arraycopy(m_rms, 0, m_channels[channel].rms, 0, m_rms.length);
            System.arraycopy(m_acc, 0, m_channels[channel].acc, 0, m_acc.length);
            System.arraycopy(m_gyro, 0, m_channels[channel].gyro, 0, m_gyro.length);
            System.arraycopy(m_cycle, 0, m_channels[channel].cycle, 0, m_cycle.length);


            System.arraycopy(m_acc_X, 0, m_channels[channel].accX, 0, m_acc_X.length);
            System.arraycopy(m_acc_Y, 0, m_channels[channel].accY, 0, m_acc_Y.length);
            System.arraycopy(m_acc_Z, 0, m_channels[channel].accZ, 0, m_acc_Z.length);
            System.arraycopy(m_gyro_X, 0, m_channels[channel].gyroX, 0, m_gyro_X.length);
            System.arraycopy(m_gyro_Y, 0, m_channels[channel].gyroY, 0, m_gyro_Y.length);
            System.arraycopy(m_gyro_Z, 0, m_channels[channel].gyroZ, 0, m_gyro_Z.length);


            
            m_data = null;
            m_annot = null;
            m_rms = null;
            m_acc = null;
            m_gyro = null;
            m_cycle = null;
            
            
            if (clear > 0)
                listener.ClearAnnot();
            updateUI();

        }
        
                
        public void Read(String fileName, int clear) {
            m_update = 0;
            m_bt = new BTData();
            m_bt.Read(fileName);

            m_prefSize = 100;

            Set(m_bt.Raw(), m_bt.Raw().length);
            SetAnnot(m_bt.Annot(), m_bt.Annot().length);
            SetRMS(m_bt.RMS(), m_bt.RMS().length);
            SetAcc(m_bt.Acc(), m_bt.Acc().length);
            SetGyro(m_bt.Gyro(), m_bt.Gyro().length);
            SetCycle(m_bt.Cycle(), m_bt.Cycle().length);
            
            SetAccX(m_bt.AccX(), m_bt.AccX().length);
            SetAccY(m_bt.AccY(), m_bt.AccY().length);
            SetAccZ(m_bt.AccZ(), m_bt.AccZ().length);
            SetGyroX(m_bt.GyroX(), m_bt.GyroX().length);
            SetGyroY(m_bt.GyroY(), m_bt.GyroY().length);
            SetGyroZ(m_bt.GyroZ(), m_bt.GyroZ().length);
            
            
            
            m_update = 1;

            m_channels = new Channel[1];
            m_channels[0] = new Channel();
            
            m_channels[0].data = new double[m_data.length];
            m_channels[0].annot = new int[m_annot.length];
            m_channels[0].rms = new double[m_rms.length];
            m_channels[0].acc = new double[m_acc.length];
            m_channels[0].gyro = new double[m_gyro.length];
            m_channels[0].cycle = new String[m_cycle.length];

            m_channels[0].accX = new double[m_acc_X.length];
            m_channels[0].accY = new double[m_acc_Y.length];
            m_channels[0].accZ = new double[m_acc_Z.length];
            m_channels[0].gyroX = new double[m_gyro_X.length];
            m_channels[0].gyroY = new double[m_gyro_Y.length];
            m_channels[0].gyroZ = new double[m_gyro_Z.length];


            System.arraycopy(m_data, 0, m_channels[0].data, 0, m_data.length);
            System.arraycopy(m_annot, 0, m_channels[0].annot, 0, m_annot.length);
            System.arraycopy(m_rms, 0, m_channels[0].rms, 0, m_rms.length);
            System.arraycopy(m_acc, 0, m_channels[0].acc, 0, m_acc.length);
            System.arraycopy(m_gyro, 0, m_channels[0].gyro, 0, m_gyro.length);
            System.arraycopy(m_cycle, 0, m_channels[0].cycle, 0, m_cycle.length);

            System.arraycopy(m_acc_X, 0, m_channels[0].accX, 0, m_acc_X.length);
            System.arraycopy(m_acc_Y, 0, m_channels[0].accY, 0, m_acc_Y.length);
            System.arraycopy(m_acc_Z, 0, m_channels[0].accZ, 0, m_acc_Z.length);
            System.arraycopy(m_gyro_X, 0, m_channels[0].gyroX, 0, m_gyro_X.length);
            System.arraycopy(m_gyro_Y, 0, m_channels[0].gyroY, 0, m_gyro_Y.length);
            System.arraycopy(m_gyro_Z, 0, m_channels[0].gyroZ, 0, m_gyro_Z.length);
            
            m_spectra_bt[0].CopyInSpectra(m_bt);

            
            m_data = null;
            m_annot = null;
            m_rms = null;
            m_acc = null;
            m_gyro = null;
            m_cycle = null;
            
            
            if (clear > 0)
                listener.ClearAnnot();
            updateUI();
            //SetData(bt);
        }
    
        
        void SetHud(TheHud h) {
            hud = h;
        }
        
        void SetData(BTData d) {
            m_bt = d;
        }
        
        int SetRight(int i) {
            right = i;

            if (m_snap == 1)
                right = 32/m_div+64/m_div*(int)((right-32/m_div)*(m_div)/64);
            return right;
        }

        int SetLeft(int i) {
            left = i;
            
            if (m_snap == 1) {
                left = 32/m_div+64/m_div*(int)((left-32/m_div)*(m_div)/64);
            }
            return left;

        }
        
        public void Set(double [] d, int len) {
            m_data = new double[len];
            for (int i=0; i<len; i++)
                m_data[i] = d[i];
        }

        public void SetRMS(double [] d, int len) {
            m_rms = new double[len];
            for (int i=0; i<len; i++)
                m_rms[i] = d[i];
        }


        public void SetAnnot(int [] d, int len) {
            m_annot = new int[len];
            for (int i=0; i<len; i++)
                m_annot[i] = d[i];
        }

        public void SetAcc(double [] d, int len) {
            m_acc = new double[len];
            for (int i=0; i<len; i++)
                m_acc[i] = d[i];
        }
        public void SetGyro(double [] d, int len) {
            m_gyro = new double[len];
            for (int i=0; i<len; i++)
                m_gyro[i] = d[i];
        }

        
        public void SetAccX(double [] d, int len) {
            m_acc_X = new double[len];
            for (int i=0; i<len; i++)
                m_acc_X[i] = d[i];
        }
        public void SetAccY(double [] d, int len) {
            m_acc_Y = new double[len];
            for (int i=0; i<len; i++)
                m_acc_Y[i] = d[i];
        }
        public void SetAccZ(double [] d, int len) {
            m_acc_Z = new double[len];
            for (int i=0; i<len; i++)
                m_acc_Z[i] = d[i];
        }
        
        
        public void SetGyroX(double [] d, int len) {
            m_gyro_X = new double[len];
            for (int i=0; i<len; i++)
                m_gyro_X[i] = d[i];
        }
        public void SetGyroY(double [] d, int len) {
            m_gyro_Y = new double[len];
            for (int i=0; i<len; i++)
                m_gyro_Y[i] = d[i];
        }
        public void SetGyroZ(double [] d, int len) {
            m_gyro_Z = new double[len];
            for (int i=0; i<len; i++)
                m_gyro_Z[i] = d[i];
        }
        
        
        public void SetCycle(String [] d, int len) {
            m_cycle = new String[len];
            for (int i=0; i<len; i++)
                m_cycle[i] = d[i];
        }

        public void SetText(String text) {
            m_text = text;
            updateUI();
        }
        
        public void Print(String name) {

            // Test printing
            m_printAll = true;
            BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            printAll(g);
            g.dispose();
            m_printAll = false;
            try { 
                ImageIO.write(image, "png", new File(name)); 
                //ImageIO.write(image, "svg", new File(name)); 
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void ClearMeta() {
            m_meta = null;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            //System.out.println("Painting\n");        

            //if (m_update == 0)
              //  return;
            
              
            Graphics2D g2d = (Graphics2D) g;
            //g2d.drawLine(10, 100, 3000, 10);
            
            /*paintChannel(g2d, m_channels[0].data, m_channels[0].annot, 
                        m_channels[0].rms, m_channels[0].acc, 
                        m_channels[0].gyro, m_channels[0].cycle, 0);*/
            int off = 200;


            if (m_meta != null) {
                for (int i=0; i<m_meta.length; i++) {
                    int x1 = (int)(500./8.*(m_meta[i].start-m_startTime));
                    int x2 = (int)(500./8.*(m_meta[i].stop-m_startTime));
                    double a = m_meta[i].start;
                    double b = m_meta[i].stop;
                    
                                                
                    g2d.setColor(Color.LIGHT_GRAY);
                    g2d.fillRect(x1, 18, x2-x1, 80);

                    g2d.setColor(Color.BLACK);
                    g2d.drawLine(x1, 18, x1, 80);
                    g2d.drawLine(x2, 18, x2, 80);
                   
            
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                         RenderingHints.VALUE_ANTIALIAS_ON);
                       
                    String str = new String();
                    str = m_meta[i].name;
                    g2d.drawString(str, x1+6, 34); 
            
                }
            }


            
            boolean meta = false;
            if (m_selected == m_channels.length)
                meta = true;
            
            for (int i=0; i<m_channels.length; i++) {
                boolean ac = false;
                if (i == m_selected)
                    ac = true;
                paintChannel(g2d, m_channels[i].data, m_channels[i].annot, 
                             m_channels[i].rms, m_channels[i].acc, 
                             m_channels[i].gyro, 
                             m_channels[i].accX, 
                             m_channels[i].accY, 
                             m_channels[i].accZ, 
                             m_channels[i].gyroX, 
                             m_channels[i].gyroY, 
                             m_channels[i].gyroZ, 
                             m_channels[i].cycle, i*off,
                             m_spectra_bt[i], ac, i, meta,
                             m_channels[i].label);


            }
            
            

            //paintChannel(g2d, m_data, m_annot, m_rms, m_acc, m_gyro, m_cycle, off*i);
        }    

        private void paintChannel(Graphics2D g2d,
                                  double [] data,
                                  int [] annot,
                                  double [] rms,
                                  double [] acc,
                                  double [] gyro,
                                  double [] accX,
                                  double [] accY,
                                  double [] accZ,
                                  double [] gyroX,
                                  double [] gyroY,
                                  double [] gyroZ,
                                  String [] cycle,
                                  int total_off,
                                  BTData spectra_bt,
                                  boolean active,
                                  int index,
                                  boolean meta,
                                  String label)
        {
        
            if (data == null)
                return;
            
            int gant_off = total_off/20 + 35;
            
            total_off += 95;
            
            int off_y = 40 + total_off;
            int off_x = 0;
            int last = off_y;
            int i, j;
            int div = 8;
            m_div = div;
            //g2d.setColor(Color.red);
            int last_state = 0;
            
            int rms_off = off_y;
            int last_rms = rms_off;
            int rep = 0;

            int acc_off = 90 + total_off;
            int gyro_off = 130 + total_off; 
            int last_acc = acc_off;
            int last_gyro = gyro_off;
            int spec_off = 190 + total_off;
            
            int last_accX = last_acc;
            int last_accY = last_acc;
            int last_accZ = last_acc;
            int last_gyroX = last_gyro;
            int last_gyroY = last_gyro;
            int last_gyroZ = last_gyro;
            
            //====================== Gyro scale ===========================
            double scale_gyro_raw = 0.8*m_master; // 0.1
            //====================== Accel scale ==========================
            double scale_acc_raw = 10.*m_master;
            
            
            int text_off = 15 + total_off;
            
            double divide = 0.;
            double sum = 0.;
            for (i=0; i<gyro.length; i++) {
                if (gyro[i] > 0.) {
                    sum += gyro[i];
                    divide += 1.;
                }
            }
            
            double gyro_scale = sum/divide/5/m_master;
//            if (gyro_scale < 1.)
//                gyro_scale = 1;
            
            String gyro_value = new String();
            gyro_value = "Gyro ";
            //gyro_value += ((double)(((int)(100*sum/divide*100)))/100);
            
            divide = 0;
            
            for (i=0; i<acc.length; i++) {
                if (acc[i] > 0.) {
                    sum += acc[i];
                    divide += 1.;
                }
            }
            double acc_scale = sum/divide/3.5/m_master;
            
//            if (acc_scale < 1.)
//                acc_scale = 1.;
            
            String acc_value = new String();
            acc_value = "Accel ";
            //acc_value += ((double)(((int)(100*sum/divide*100)))/100);
            
            
                    
                    
            
            String last_cycle = new String();
            
            int segCut = 2;
            
            // Quantize    
            if (m_snap == 1) {
                left = 32/div+64/div*(int)((left-32/div)*(div)/64);
                right = 32/div+64/div*(int)((right-32/div)*(div)/64);
            }
            
            setBackground(Color.white);
            
            int is_cycle = 0;
            int is_artifact = 0;
            int weak_cycle = 0;
            
            
            int cSize = 2*off_x + data.length/div;
                    
            if (cSize > m_prefSize)
                m_prefSize = cSize;
            
            //for (i=64; i<data.length; i++) {
            int left_bound = 0;
            
            int has_cycles = 0;
            for (i=0; i<cycle.length; i++) {
                if (cycle[i] != null && !cycle[i].contains("ARTIFACT"))
                    has_cycles = 1;
            }
            
                                   
            g2d.setColor(Color.BLACK);
            Color gantt = new Color(120, 120, 120);
            Color gantt_light = new Color(180, 180, 180);
            switch(index) {
                case 0:
                    gantt = new Color(200, 20, 30);
                    break;
                case 1:
                    gantt = new Color(32, 205, 30);
                    break;
                case 2:
                    gantt = new Color(180, 112, 10);
                    break;
                case 3:
                    gantt = new Color(30, 100, 210);
                    break;
                case 5:
                    gantt = new Color(20, 105, 40);
                    break;
                case 4:
                    gantt = new Color(210, 210, 30);
                    break;
            
            }

            /////////////////////////////////////////////////////////
/*
            if (m_meta != null) {
                for (i=0; i<m_meta.length; i++) {
                    int x1 = (int)(500./(double)div*(m_meta[i].start-m_startTime));
                    int x2 = (int)(500./(double)div*(m_meta[i].stop-m_startTime));
                    double a = m_meta[i].start;
                    double b = m_meta[i].stop;
                    
                                                
                    g2d.setColor(Color.LIGHT_GRAY);
                    g2d.fillRect(x1, 15, x2-x1, 70);

                    g2d.setColor(Color.BLACK);
                    g2d.drawLine(x1, 15, x1, 80);
                    g2d.drawLine(x2, 15, x2, 80);
                   
            
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                         RenderingHints.VALUE_ANTIALIAS_ON);
                       
                    String str = new String();
                    str = m_meta[i].name;
                    g2d.drawString(str, x1+6, 25); 
            
                }
            }
            
            
  */          
            
            
            /////////////////////////////////////////////////////////

            Rectangle view = new Rectangle();
            int xx1 = 0;
            int xx2 = 100000;
            if (getParent() instanceof JViewport) {
                JViewport vp = (JViewport) getParent();
                view = vp.getViewRect();
                xx1 = view.x;
                left_bound = xx1;
                xx2 = xx1 + view.width;
            }

            
            
            for (i=0; i<data.length; i++) {
                
                int show = 1;

                //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                // MOVE THIS OUTSIDE OF THE LOOP
                /*Rectangle view = new Rectangle();
                int show = 1;
                if (getParent() instanceof JViewport) {
                    JViewport vp = (JViewport) getParent();
                    view = vp.getViewRect();
                    int xx1 = view.x;
                    left_bound = xx1;
                    int xx2 = xx1 + view.width;
                    //int x1 = vp.getX();
                    //int x2 = x1 + vp.getWidth();*/
                    // FIX THE GLITCHES w/ last!!!
                if (xx2 < off_x+i/div || xx1 > off_x+i/div)
                    show = 0;
                //}

                if (m_printAll)
                    show = 1;
                
                /*if (scroll.isVisible()) {
                    //scroll.get
                }*/
                
                int y = off_y+(int)(data[i]/35*m_master+0.5);

                int y_rms = rms_off-(int)(rms[i]*m_master/25+0.5);

                
                //=============================================================
                //=============================================================
                //=============================================================
                //=============================================================
                //=============================================================
                int y_gyro = gyro_off;
                int y_acc = acc_off;
                int y_gyroX = gyro_off;
                int y_gyroY = gyro_off;
                int y_gyroZ = gyro_off;
                int y_accX = acc_off;
                int y_accY = acc_off;
                int y_accZ = acc_off;
                if (i/5 < gyro.length) {
                    y_gyro = gyro_off-(int)(gyro[i/5]/gyro_scale+0.5);
                    y_gyroX = gyro_off-(int)(gyroX[i/5]*scale_gyro_raw+0.5);
                    y_gyroY = gyro_off-(int)(gyroY[i/5]*scale_gyro_raw+0.5);
                    y_gyroZ = gyro_off-(int)(gyroZ[i/5]*scale_gyro_raw+0.5);
                }
                if (i/5 < acc.length) {
                    int d = (int)(acc[i/5]/acc_scale+0.5);
                    if (d > 80)
                        d = 80;
                    y_acc = acc_off - d;
                    y_accX = acc_off - (int)(accX[i/5]*scale_acc_raw+0.5);
                    y_accY = acc_off - (int)(accY[i/5]*scale_acc_raw+0.5);
                    y_accZ = acc_off - (int)(accZ[i/5]*scale_acc_raw+0.5);
                   
                }
                
                //=================================================
                // Tick marks
                if (show == 1) {
                    double time = m_startTime + ((double)i)/500.; 
                    double iTime = (int)time;
                    if (time - iTime == 0.) {
                       g2d.setColor(Color.BLACK);
                       g2d.drawLine(off_x+i/div, 2, off_x+i/div, 18);
                       String ts = new String();
                       ts += time;
                       g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                         RenderingHints.VALUE_ANTIALIAS_ON);
                       g2d.drawString(ts, off_x+i/div+3, 15); 

                    }   
                }
                
                // Gantt-type chart
                if (has_cycles == 1 && is_cycle == 1) {
                    if (weak_cycle == 0)
                        g2d.setColor(gantt);
                    else
                        g2d.setColor(gantt_light);
                        
                    g2d.drawLine(off_x+i/div, 4+gant_off, off_x+i/div, gant_off + 11);
                    
                }
                
                //if (!g2d.isvisible())
                //getHorizontalScrollBar().isVisible()
                if (is_cycle == 1 || has_cycles == 0) {
                    
                    if (is_artifact == 1) {
                        g2d.setColor(Color.LIGHT_GRAY);
                  
                    } else {
                        if (annot[i] == 0)
                            g2d.setColor(Color.LIGHT_GRAY);
                        if (annot[i] == 1)
                            g2d.setColor(Color.YELLOW);
                        if (annot[i] == 2)
                            g2d.setColor(Color.ORANGE);
                        if (annot[i] == 3)
                            g2d.setColor(Color.RED);
                        if (annot[i] == 4)
                            g2d.setColor(Color.black);
                    }
                } else {
                    if (annot[i] == 0)
                        g2d.setColor(Color.WHITE);
                    if (annot[i] == 1)
                        g2d.setColor(Color.WHITE);
                    if (annot[i] == 2)
                        g2d.setColor(new Color(255, 240, 200));
                    if (annot[i] == 3)
                        g2d.setColor(new Color(255, 200, 200));
                    if (annot[i] == 4)
                        g2d.setColor(new Color(200, 200, 200));
                
                }
                
                
                
                boolean notBlue = false;    
                if (active && left >= 0 && right >= 0) {
                    if (off_x+i/div > left && off_x+i/div < right) {
                        g2d.setColor(Color.gray);
                        notBlue = true;
                    }                    
                }
                
                if (i % div == 0) {
                    if (show == 1) {
                        g2d.setStroke(new BasicStroke(1.05f));
                        g2d.drawLine(off_x+(i-1)/div, last_rms, off_x+i/div, y_rms);
                        g2d.setStroke(new BasicStroke(1));
                    }

                
                    if (show == 1) {
                        if (!notBlue)
                            g2d.setColor(new Color(180, 180, 250));
                        
                        g2d.drawLine(off_x+(i-1)/div, last, off_x+i/div, y);
                    }
                   
                    last = y;
                
                    last_rms = y_rms;
                }
                
                
                if (i%5 == 0) {
                    if (has_cycles == 0 || is_cycle == 1)
                        g2d.setColor(Color.BLACK);
                    else
                        g2d.setColor(Color.LIGHT_GRAY);
                        
                    //=============================================================
                    //=============================================================
                    //=============================================================
                    //=============================================================
                    //=============================================================
                    if (show == 1) {
                        g2d.setColor(Color.BLACK);

                        if (m_mag == 1) {
                            g2d.setStroke(new BasicStroke(1.2f));
                            g2d.drawLine(off_x+(i-6)/div, last_gyro, off_x+i/div, y_gyro);                  
                            g2d.drawLine(off_x+(i-6)/div, last_acc, off_x+i/div, y_acc);
                            g2d.setStroke(new BasicStroke(1));
                        } else {
                        
                            g2d.setColor(Color.RED);
                            g2d.drawLine(off_x+(i-6)/div, last_gyroX, off_x+i/div, y_gyroX);                  
                            g2d.setColor(Color.GREEN);
                            g2d.drawLine(off_x+(i-6)/div, last_gyroY, off_x+i/div, y_gyroY);                  
                            g2d.setColor(Color.BLUE);
                            g2d.drawLine(off_x+(i-6)/div, last_gyroZ, off_x+i/div, y_gyroZ);                  

                            g2d.setColor(Color.RED);
                            g2d.drawLine(off_x+(i-6)/div, last_accX, off_x+i/div, y_accX);
                            g2d.setColor(Color.GREEN);
                            g2d.drawLine(off_x+(i-6)/div, last_accY, off_x+i/div, y_accY);
                            g2d.setColor(Color.BLUE);
                            g2d.drawLine(off_x+(i-6)/div, last_accZ, off_x+i/div, y_accZ);
                        }
                        g2d.setColor(Color.BLACK);

                    }
                    
                    last_acc = y_acc;
                    last_gyro = y_gyro;

                    last_accX = y_accX;
                    last_accY = y_accY;
                    last_accZ = y_accZ;
                    last_gyroX = y_gyroX;
                    last_gyroY = y_gyroY;
                    last_gyroZ = y_gyroZ;

                }

                // Spectra
                if (i%64 == 0 && show == 1) {
                    //System.out.println("Spectrum: " + i/64);        
                    if (i/64 < spectra_bt.SpecCount()) {   
                        double [] one = spectra_bt.Spec(i/64);
                        int start = (i-64)/div + 32/div + 64/div;
                        int end = i/div + 32/div + 64/div;
                        int size = end - start;
                       // System.out.println("From " + start + " to " + end);        

                        for (j=0; j<one.length; j++) {
                            double d = one[j]/55000*m_master;
        
                            d = 1-d;
                            d *= 255;
                            if (d < 0)
                                d = 0;

                            int c = (int)d;
                            Color cc = new Color(c,c,c);
                            g2d.setColor(cc);
                        
                            //g2d.drawRect(start, spec_off-j*size, size, size);
                            g2d.fillRect(start+1, spec_off-j*size, size, size);
                        }
                    }
                }
                
                
                // Text
                if (show == 1) {
                    g2d.setColor(Color.black);
                    
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                         RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.drawString(m_text, 20, 20 + total_off); 
                }
                
                if (annot[i] >=segCut && last_state < segCut && is_cycle == 0) {
                    if (has_cycles == 0)
                        g2d.setColor(Color.blue);
                    else
                        g2d.setColor(new Color(200, 200, 255));
                    
                    g2d.drawLine(off_x+i/div, total_off, off_x+i/div, spec_off+8);
                   
                    
                    if (has_cycles == 0)
                        g2d.setColor(Color.black);
                    else
                        g2d.setColor(Color.LIGHT_GRAY);
                    
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                         RenderingHints.VALUE_ANTIALIAS_ON);

                    rep++;
                    String s = new String();
                    s += rep;
                    
                    if (show == 1) {
                        g2d.drawString(s, off_x+i/div+3, text_off); 
                    }            
                    //System.out.println("Draw line at " + off_x+i/div + " sample: " + i);        


                }
                
                // Cycles
                if (cycle[i] != null && !cycle[i].equals(last_cycle)) {
                    if (cycle[i].contains("ARTIFACT"))
                        is_artifact = 1;
                    
                    is_cycle = 1;
                    if (cycle[i].contains("auto_act"))
                        weak_cycle = 1;
                    else
                        weak_cycle = 0;

                    if (show == 1) {
                        g2d.setColor(Color.black);
                        g2d.drawLine(off_x+i/div, total_off, off_x+i/div, spec_off+8);
            
                    
                        g2d.setColor(Color.black);
                    
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                         RenderingHints.VALUE_ANTIALIAS_ON);

                        //rep++;
                        //String s = new String();
                        //s += rep;
                        String s = cycle[i];
                        g2d.drawString(cycle[i], off_x+i/div+3, text_off);
                    }
                    last_cycle = cycle[i];
                } 
                
                if (cycle[i] == null) {
                    if (!last_cycle.equals("")) {
                        if (show == 1) {
                            g2d.setColor(Color.black);
                            g2d.drawLine(off_x+i/div, total_off, off_x+i/div, spec_off+8);
                        }
                    }
                        
                    last_cycle = "";
                    is_cycle = 0;
                    is_artifact = 0;

                }
                
                
                if (last_state >=segCut && annot[i] < segCut && is_cycle == 0) {
                    if (show == 1) {
                        if (has_cycles == 0)
                            g2d.setColor(Color.green);
                        else
                            g2d.setColor(new Color(200, 255, 200));
                        g2d.drawLine(off_x+i/div, total_off, off_x+i/div, spec_off+8);
                        g2d.setColor(Color.black);
                    }
                }
                last_state = annot[i];
            }
            

            g2d.setColor(Color.black);

            g2d.drawLine(off_x, 2, off_x+data.length/div, 2);

            g2d.drawLine(off_x, off_y, off_x+data.length/div, off_y);
            g2d.drawLine(off_x, rms_off, off_x+data.length/div, rms_off);
            g2d.drawLine(off_x, acc_off, off_x+data.length/div, acc_off);
            g2d.drawLine(off_x, gyro_off, off_x+data.length/div, gyro_off);

            // Axis labels
            //int the_y = (int)(1100/31);
            // Rectangles
            g2d.setColor(Color.lightGray);
            //g2d.fillRect(0, gyro_off-35, 190, 20);
            //g2d.fillRect(0, acc_off-35, 190, 20);
            //g2d.fillRect(0, acc_off-70, 290, 20);

            g2d.setColor(Color.BLACK);
            g2d.drawLine(off_x, gyro_off-25, off_x+20, gyro_off-25);
            //System.out.println("AXIS Label gyro: " + the_y);        
            g2d.drawString(gyro_value, off_x+35, gyro_off-20); 

            //the_y = (int)(400/7.5);
            g2d.drawLine(off_x, acc_off-25, off_x+20, acc_off-25);
            g2d.drawString(acc_value, off_x+35, acc_off-20);


            // LABEL

            if (!label.equals("")) {
                String[] split = label.split("/");
                if (split.length == 1)
                    split = label.split("\\\\");
                
                label = split[split.length-1];
                
                String [] split2 = label.split("\\.");
                label = split2[0];
                
                g2d.drawString(label, off_x/*+35*/, acc_off-85);
            }

            
            m_prefSizeY = spec_off + 20;
            //System.out.println("AXIS Label acc: " + the_y);        
           
            // y axis
            
/*            
            System.out.println("Left boundary: " + left_bound);

            g2d.drawLine(10+left_bound, rms_off-50, 10+left_bound, rms_off);
            // Tick marks
            g2d.drawLine(10+left_bound, rms_off-50, 15+left_bound, rms_off-50);
            g2d.drawLine(10+left_bound, rms_off-40, 15+left_bound, rms_off-40);
            g2d.drawLine(10+left_bound, rms_off-30, 15+left_bound, rms_off-30);
            g2d.drawLine(10+left_bound, rms_off-20, 15+left_bound, rms_off-20);
            g2d.drawLine(10+left_bound, rms_off-10, 15+left_bound, rms_off-10);
            g2d.drawLine(10+left_bound, rms_off-0, 15+left_bound, rms_off-0);
*/
            if (active) {
                if (right >= 0) {
                    g2d.setColor(Color.green);
                    g2d.drawLine(right, total_off, right, spec_off+8);            
                }
                if (left >= 0) {
                    g2d.setColor(Color.blue);
                    g2d.drawLine(left, total_off, left, spec_off+8);           
                }
            } 

            // Meta selector
            if (meta) {
                if (right >= 0) {
                    g2d.setColor(Color.green);
                    g2d.drawLine(right, 15, right, 80);            
                }
                if (left >= 0) {
                    g2d.setColor(Color.blue);
                    g2d.drawLine(left, 15, left, 80);           
                }
            } 

        }

        
        
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(m_prefSize, m_prefSizeY);
        }
    }

    private class PanelListener implements MouseListener {

        private int left;
        private int right;
        private LabelJFrame m_frame;
        
        public PanelListener() {
            System.out.println("MOUSE Listener!!!\n");        
            m_frame = new LabelJFrame();
            
            //???????????????????????????????????????????????????????????
            
            m_frame.setVisible(false);
            left = -1;
            right = -1;
            
        }
        
        public void updateHintFile() {
            m_frame.SetListDataAndWrite();
        }
        
        public void setCycles(Cycles c) {
            m_frame.setCycles(c);
        }
        
        public void SetMeta(String s) {
            m_frame.SetMeta(s);
        }
        
        public void SetSelected(int n) {
            m_frame.SetChannel(n);
        }
        
        public void ClearAnnot() {
            // Call to Clear() moved to SetMeta()!!!
            //m_frame.Clear();
        }
        
        @Override
        public void mouseClicked(MouseEvent event) {
                    /* source is the object that got clicked
                     * 
                     * If the source is actually a JPanel, 
                     * then will the object be parsed to JPanel 
                     * since we need the setBackground() method
                     */
                    
            //System.out.println("MOUSE clicked!!!\n");


            Object source = event.getSource();
            /*if(source instanceof JPanel){
                JPanel panelPressed = (JPanel) source;
                panelPressed.updateUI();
                //panelPressed.setBackground(Color.blue);
            }*/
            if(source instanceof Drawing){
                Drawing panelPressed = (Drawing) source;
                left = panelPressed.SetLeft(left);
                right = panelPressed.SetRight(right);
                panelPressed.updateUI();
                //panelPressed.setBackground(Color.blue);
            }
 
	    
        }

        @Override
        public void mouseEntered(MouseEvent event) {
            //System.out.println("MOUSE enter!!!\n");
            Object source = event.getSource();
            if(source instanceof Drawing){
                Drawing panelPressed = (Drawing) source;
                panelPressed.updateUI();
		
            }
            
        }

        @Override
        public void mouseExited(MouseEvent arg0) {
            //System.out.println("MOUSE exit!!!\n");

        }

        @Override
        public void mousePressed(MouseEvent arg0) {
            //System.out.println("MOUSE pressed!!!\n");

            int x = arg0.getX();
            int y = arg0.getY();

            
                            

            int b = arg0.getButton();

            //Drawing panelPressed = (Drawing) source;
            
            if (b == 1) {
                left = x;
                //left = panelPressed.SetLeft(left);
                //right = panelPressed.SetRight(right);
            }
            if (b == 2) {
                m_frame.SetDataDir(m_fileName[0]);
                String args[] = new String[5];
                //LabelJFrame.main(args);
                //LabelJFrame f = new LabelJFrame();
                //m_frame.Set((double)left/8.-0.5, (double)right/8.-0.5);
                m_frame.Set((double)left/8., (double)right/8.);
                m_frame.SetBaseCSV(m_fileName[0]);
                m_frame.setVisible(true);
                
                left = -1;
                right = -1;
            }
            if (b == 3) 
                right = x;
            
            //System.out.println("MOUSE pressed, x=" + x + " y=" + y + " button: " + b + "\n");


            
            
            
        }

        @Override
        public void mouseReleased(MouseEvent arg0) {
            //System.out.println("MOUSE released!!!\n");

        }

    }





}
