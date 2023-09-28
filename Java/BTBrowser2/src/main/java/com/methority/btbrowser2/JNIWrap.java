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
public class JNIWrap {
       
    static {        
        System.loadLibrary("btanalyze");    // loads libhulkrt.{so/dll/jnilib}
    }
    
   
    private native int callAnalysisJNI(String fileName,
                                       String resultName,
                                       String outName,
                                       String miName,
                                       String tbName,
                                       String hintName,
                                       String config,
                                       int split);


    
    public int callAnalysis(String fileName,
                            String resultName,
                            String outName,
                            String miName,
                            String tbName,
                            String hintName,
                            String config,
                            int split)
    {
        int ret = new JNIWrap().callAnalysisJNI(fileName,
                                                resultName,
                                                outName,
                                                miName,
                                                tbName,
                                                hintName,
                                                config,
                                                split);
                                                
        
        return ret;
    }

}
