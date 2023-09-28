/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digidactylus.recorder;

/**
 *
 * @author manfred
 */
public class JNIWrapRec {
    static {     
        System.out.println("Loading library");
        System.loadLibrary("recorder");
        System.out.println("Done");
    }
    

    // Not used in this context
    private native int callStartRecUSB(String info);

    private native int callStopRecUSB(String info);

    private native int callStartVideo(String device, String url, String fileName, String info);

    private native int callStopVideo(String info);

    private native double [] callGetFrames(int size);


    // Relevant functions
    private native void callReset();

    private native int callIsDataValid();
    
    private native void callSetWorkingDir(String dir);

    private native void callSaveAsCSV(String name);

    private native void callProcessData(String name);

    private native void callFeedData(byte [] binData, int size);

    // ************************************************************************
    // Stuff to display
    // n = number of data channels:
    // 0= Frame
    // 1=EMG
    // 2=RMS
    // 3=ACC-0
    // 4=ACC-1
    // 5=ACC-2
    // 6=GYR-0
    // 7=GYR-1
    // 8=GYR-2

    // 9-11  = Triangle point 0
    // 12-14 = Triangle point 1
    // 15-17 = Triangle point 2

    // NOTE: data comes in 100ms frames!
    private native double [] callGetProcessed(int n);
    // ************************************************************************

    //==================================================================================
    public void doReset() {
	new JNIWrapRec().callReset();
    }
    
    public int doIsValid() {
	return new JNIWrapRec().callIsDataValid();
    }

    public int doStartRec(String info)
    {
       // System.out.println("Call native... doStartRec");
        int ret = new JNIWrapRec().callStartRecUSB(info);
        return ret;
    }
    
    public int doStopRec(String info)
    {
        //System.out.println("Call native... doStopRec");
        int ret = new JNIWrapRec().callStopRecUSB(info);
        return ret;
    }

    public int doStartVideo(String device, String url, String fileName, String info)
    {
        //System.out.println("Call native... doStartVideo");
        int ret = new JNIWrapRec().callStartVideo(device, url, fileName, info);
        return ret;
    }
    
    public int doStopVideo(String info)
    {
        //System.out.println("Call native... doStopVideo");
        int ret = new JNIWrapRec().callStopVideo(info);
        return ret;
    }
    
    public double [] doGetFrames(int size)
    {
        //System.out.println("Call native... doGetFrames");
        double [] f = new JNIWrapRec().callGetFrames(size);
        return f;
    }

    //==============================================================================
    public void doSetWorkingDir(String dir) {
        new JNIWrapRec().callSetWorkingDir(dir);
    }

    public void doSaveAsCSV(String name) {
        new JNIWrapRec().callSaveAsCSV(name);
    }

    public void doProcessData(String name) {
        new JNIWrapRec().callProcessData(name);
    }

    public void doFeedData(byte [] binData, int size) {
        new JNIWrapRec().callFeedData(binData, size);
    }

    // Stuff to display
    public double [] doGetProcessed(int n) {
        return new JNIWrapRec().callGetProcessed(n);
    }


}
