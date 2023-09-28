/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.methority.btbrowser2;
import java.io.File; 
import java.io.FileWriter;
import java.util.Scanner; 

/**
 *
 * @author manfred
 */
public class BTData {

    private double m_startTime;
    public BTData() {
        m_startTime = 0.;
    }

    double StartTime() {return m_startTime;}
    
    int NumRaw() {return m_raw.length;}
    double GetRaw(int i) {return m_raw[i];}
    double GetRMS(int i) {return m_rms[i];}
    int Annot(int i) {return m_annot[i];}
    
    int [] Annot() {return m_annot;}
    double [] Raw() {return m_raw;}
    double [] RMS() {return m_rms;}

    double [] Acc() {return acc_proc;}
    double [] Gyro() {return gyro_proc;}

    double [] AccX() {return acc_x;}
    double [] AccY() {return acc_y;}
    double [] AccZ() {return acc_z;}
    double [] GyroX() {return gyro_x;}
    double [] GyroY() {return gyro_y;}
    double [] GyroZ() {return gyro_z;}

    
    String [] Cycle() {return m_cycle;}
    
    String GetCycle(int i) {return m_cycle[i];}

    
    int SpecCount() {return spec_0.length;}
    double [] Spec(int i) {
        double [] r = new double[8];
        r[0] = spec_0[i];
        r[1] = spec_1[i];
        r[2] = spec_2[i];
        r[3] = spec_3[i];
        r[4] = spec_4[i];
        r[5] = spec_5[i];
        r[6] = spec_6[i];
        r[7] = spec_7[i];
        
        
        return r;
    }
    
    public void CopyInSpectra(BTData d) {
        spec_0 = new double[d.spec_0.length];
        System.arraycopy(d.spec_0, 0, spec_0, 0, d.spec_0.length);
        spec_1 = new double[d.spec_1.length];
        System.arraycopy(d.spec_1, 0, spec_1, 0, d.spec_1.length);
        spec_2 = new double[d.spec_2.length];
        System.arraycopy(d.spec_2, 0, spec_2, 0, d.spec_2.length);
        spec_3 = new double[d.spec_3.length];
        System.arraycopy(d.spec_3, 0, spec_3, 0, d.spec_3.length);
        spec_4 = new double[d.spec_4.length];
        System.arraycopy(d.spec_4, 0, spec_4, 0, d.spec_4.length);
        spec_5 = new double[d.spec_5.length];
        System.arraycopy(d.spec_5, 0, spec_5, 0, d.spec_5.length);
        spec_6 = new double[d.spec_6.length];
        System.arraycopy(d.spec_6, 0, spec_6, 0, d.spec_6.length);
        spec_7 = new double[d.spec_7.length];
        System.arraycopy(d.spec_7, 0, spec_7, 0, d.spec_7.length);
        


    }
    
    int MoveCount() {return move.length;}
    double Move(int i) {return move[i];}
    
    private double [] move;
    private int [] m_annot;
    private String [] m_cycle;
    
    private double [] m_raw;
    private double [] m_rms;

    private double [] gyro_x;
    private double [] gyro_y;
    private double [] gyro_z;

    private double [] acc_x;
    private double [] acc_y;
    private double [] acc_z;
    
    public double [] spec_0;
    private double [] spec_1;
    private double [] spec_2;
    private double [] spec_3;
    private double [] spec_4;
    private double [] spec_5;
    private double [] spec_6;
    private double [] spec_7;
    private double [] spec_8;
    
    private double [] gyro_proc;
    private double [] acc_proc;
    
    
    private double [] CheckResize(double [] d, int n) {
        if (n >= d.length) {

            System.out.println("Resize array to: " + (n + 65536));        

            double [] tmp = new double[n + 65536];
            for (int i=0; i<d.length; i++) {
                tmp[i] = d[i];
            }
            d = tmp;
        }
        return d;
    }
    private int[] CheckResize(int [] d, int n) {
        if (n >= d.length) {

            System.out.println("Resize array to: " + (n + 65536));        

            int [] tmp = new int[n + 65536];
            for (int i=0; i<d.length; i++) {
                tmp[i] = d[i];
            }
            d = tmp;
        }
        return d;
    }

    private String[] CheckResize(String [] d, int n) {
        if (n >= d.length) {

            System.out.println("Resize array to: " + (n + 65536));        

            String [] tmp = new String[n + 65536];
            for (int i=0; i<d.length; i++) {
                tmp[i] = d[i];
            }
            d = tmp;
        }
        return d;
    }
    
    private String [] Copy(String [] in, int len) {
        String [] out = new String[len];
        for (int i=0; i<len; i++)
            out[i] = in[i];
        return out;
    }
    
    private double [] Copy(double [] in, int len) {
        double [] out = new double[len];
        for (int i=0; i<len; i++)
            out[i] = in[i];
        return out;
    }
    private int [] Copy(int [] in, int len) {
        int [] out = new int[len];
        for (int i=0; i<len; i++)
            out[i] = in[i];
        return out;                
    }
    
    public static String line;
    
    
    static public void WriteCrop(String oldFile, String fileName, double from, double to) {
    
        from = 1/100.*(double)((int)(from * 100));
        to = 1/100.*(double)((int)(to * 100));
        
        try {
            File in = new File(oldFile);
            Scanner sc = new Scanner(in);
            
            File test = new File(fileName);
            if (test.exists())
                test.delete();
            
            FileWriter fw = new FileWriter(fileName);
            
            
            while (sc.hasNextLine()) {
                String stro = sc.nextLine();
                if (stro.equals(""))
                    continue;
                String str = stro;
                str.replace(",", "");
                String [] t = str.split("\\s+");
                if (t.length == 0)
                    continue;
                
                if (t.length > 1 && t[1].equals("Data")) {
                    if (t[0].equals("SPECTRA"))
                        break;
                    fw.write(stro + "\n");
                    stro = sc.nextLine();
                    fw.write(stro + "\n");
                    continue;
                }
                
                String dd = new String();
                for (int i=0; i<t[0].length(); i++) {
                    char cc = t[0].charAt(i);
                    if (cc != ',')
                        dd += cc;
                }
                
                double stamp = 0.;
                try {
                    stamp = Double.parseDouble(dd);
                } catch(Exception e) {
                    System.out.println("SCREAM!!");
                }
                if (stamp >= from && stamp <= to) {
                    fw.write(stro + "\n");                
                }

            }
            

            fw.close();
        } catch(Exception e) {
            System.out.println("ERROR: could not write to file " + fileName);
        }
    }
    
    
    public void Read(String fileName) {    
            
        double [] data = new double[65536*2];
        double [] rms = new double[65536*2];        
        int [] annot = new int[65536*2];
        String [] cycle = new String[65536*2];
        int len = 0;
        
        int i;
        
        for (i=0; i<data.length; i++)
            data[i] = 0.;
        
        m_startTime = -1.;
        
        try {
            File file = 
                new File(fileName); 
            Scanner sc = new Scanner(file); 
  
            sc.nextLine();
            sc.nextLine();
            int count = 0;
            System.out.println("Enter main");        
            while (sc.hasNextLine()) {
                String str = sc.nextLine();
                line = str;
                
                //if (count == 9636)
                  //  System.out.println(str);        

                count++;
                
                //System.out.println(str);        

                if (str.equals("")) {
                    sc.nextLine();
                    break;
                }
                
                for (i=0; i<str.length(); i++) {
                    if (str.charAt(i) == ',') {
                        if (i+1 < str.length())
                            str = str.substring(0,i)+' '+str.substring(i+1);
                        else
                            str = str.substring(0,i);
                    }
                }
                
                //String[] split = str.split("\\s+");
                String[] split = str.split("\\s+");
                //System.out.println("|" + split[1] + "|\n");
                if (split.length == 0) {
                    sc.nextLine();                
                    break;
                }
                if (split[1].equals("Data")) {
                    break;                    
                }
                        
                //if (len == 28548)
                 //   System.out.println("NOT GOOD");        
                
                 if (split.length < 7)
                     System.out.println("NOT GOOD");
                 
                if (len >= data.length) {
                    data = CheckResize(data, len);
                    annot = CheckResize(annot, len);
                    rms = CheckResize(rms, len);
                    cycle = CheckResize(cycle, len);
                }
                data[len] = Double.parseDouble(split[1]);
                annot[len] = Integer.parseInt(split[6]);
                rms[len] = Double.parseDouble(split[2]);
                
                if (m_startTime < 0.)
                    m_startTime = Double.parseDouble(split[0]);
                if (split.length > 7)
                    cycle[len] = split[7];
                len++;
            }

            m_annot = Copy(annot, len);
            m_rms = Copy(rms, len);
            m_raw = Copy(data, len);
            m_cycle = Copy(cycle, len);
            System.out.println("Length: " + m_raw.length);        
           
            
            System.out.println("Enter accel");        
            // Accel
            len = 0;
            sc.nextLine();
            //sc.nextLine();
            double [] x = new double[65536];
            double [] y = new double[65536];
            double [] z = new double[65536];
            double [] pp = new double[65536];
            while (sc.hasNextLine()) {
                String str = sc.nextLine();
                if (str.equals("")) {
                    sc.nextLine();
                    break;
                }
                for (i=0; i<str.length(); i++) {
                    if (str.charAt(i) == ',')                      
                        str = str.substring(0,i)+' '+str.substring(i+1);
                }

                //String[] split = str.split("\\s+");
                String[] split = str.split("\\s+");
                //System.out.println("|" + split[1] + "|\n");
                if (split.length == 0) {
                    sc.nextLine();                
                    break;
                }
                if (split[1].equals("Data")) {
                    break;                    
                }
                
                if (len >= x.length) {
                    x = CheckResize(x, len);
                    y = CheckResize(y, len);
                    z = CheckResize(z, len);
                    pp = CheckResize(pp, len);
                }
                
                x[len] = Double.parseDouble(split[1]);
                y[len] = Double.parseDouble(split[2]);
                z[len] = Double.parseDouble(split[3]);
                pp[len] = Double.parseDouble(split[6]);
                len++;
            }
            acc_x = Copy(x, len);
            acc_y = Copy(y, len);
            acc_z = Copy(z, len);
            acc_proc = Copy(pp, len);
 
            // Gyro
            len = 0;
            sc.nextLine();
            //sc.nextLine();
            while (sc.hasNextLine()) {
                String str = sc.nextLine();
                if (str.equals("")) {
                    sc.nextLine();
                    break;
                }
                for (i=0; i<str.length(); i++) {
                    if (str.charAt(i) == ',')                      
                        str = str.substring(0,i)+' '+str.substring(i+1);
                }

                //String[] split = str.split("\\s+");
                String[] split = str.split("\\s+");
                //System.out.println("|" + split[1] + "|\n");
                if (split.length == 0) {
                    sc.nextLine();                
                    break;
                }
                if (split[1].equals("Data")) {
                    break;                    
                }

                if (len >= x.length) {
                    x = CheckResize(x, len);
                    y = CheckResize(y, len);
                    z = CheckResize(z, len);
                    pp = CheckResize(pp, len);
                }

                x[len] = Double.parseDouble(split[1]);
                y[len] = Double.parseDouble(split[2]);
                z[len] = Double.parseDouble(split[3]);
                pp[len] = Double.parseDouble(split[6]);
                len++;
            }
            gyro_x = Copy(x, len);
            gyro_y = Copy(y, len);
            gyro_z = Copy(z, len);

            gyro_proc = Copy(pp, len);



            // Spectra
            len = 0;
            sc.nextLine();
            //sc.nextLine();
            
            spec_0 = new double[65536];
            spec_1 = new double[65536];
            spec_2 = new double[65536];
            spec_3 = new double[65536];
            spec_4 = new double[65536];
            spec_5 = new double[65536];
            spec_6 = new double[65536];
            spec_7 = new double[65536];
            
            while (sc.hasNextLine()) {
                String str = sc.nextLine();
                if (str.equals("")) {
                    sc.nextLine();
                    break;
                }
                for (i=0; i<str.length(); i++) {
                    if (str.charAt(i) == ',')                      
                        str = str.substring(0,i)+' '+str.substring(i+1);
                }

                //String[] split = str.split("\\s+");
                String[] split = str.split("\\s+");
                //System.out.println("|" + split[1] + "|\n");
                if (split.length == 0) {
                    sc.nextLine();                
                    break;
                }
                if (split[1].equals("Data")) {
                    break;                    
                }

                if (len >= spec_0.length) {
                    spec_0 = CheckResize(spec_0, len);
                    spec_1 = CheckResize(spec_1, len);
                    spec_2 = CheckResize(spec_2, len);
                    spec_3 = CheckResize(spec_3, len);
                    spec_4 = CheckResize(spec_4, len);
                    spec_5 = CheckResize(spec_5, len);
                    spec_6 = CheckResize(spec_6, len);
                    spec_7 = CheckResize(spec_7, len);
                }
                spec_0[len] = Double.parseDouble(split[1]);
                spec_1[len] = Double.parseDouble(split[2]);
                spec_2[len] = Double.parseDouble(split[3]);
                spec_3[len] = Double.parseDouble(split[4]);
                spec_4[len] = Double.parseDouble(split[5]);
                spec_5[len] = Double.parseDouble(split[6]);
                spec_6[len] = Double.parseDouble(split[7]);
                spec_7[len] = Double.parseDouble(split[8]);

                len++;
            }

            //for (int zz = 0; zz<len; zz++) {
              //  System.out.println(spec_0[zz]);
            //}

            spec_0 = Copy(spec_0, len);
            
            //for (int zz = 0; zz<spec_0.length; zz++) {
              //  System.out.println(spec_0[zz]);
            //}

            
            spec_1 = Copy(spec_1, len);
            spec_2 = Copy(spec_2, len);
            spec_3 = Copy(spec_3, len);
            spec_4 = Copy(spec_4, len);
            spec_5 = Copy(spec_5, len);
            spec_6 = Copy(spec_6, len);
            spec_7 = Copy(spec_7, len);


            
        }

        catch(Exception e) {
            System.out.println("EXCEPTION!!!!");  
        }

        System.out.println("Done loading!");  
 
    }
    
}
