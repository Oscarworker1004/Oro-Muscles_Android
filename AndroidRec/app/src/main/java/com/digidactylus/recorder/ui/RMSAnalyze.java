/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digidactylus.recorder.ui;

/**
 *
 * @author manfred
 */
public class RMSAnalyze {
    double m_floorOff = 20.;
    int m_peakMin = 50;


    public RMSAnalyze() {
        m_delay = 2;
        m_floorData = 0.;
        reset();
    }


    public void setFloorOff(int val) {
        m_floorOff = (double)val/10.;
    }

    public void setPeakMinDist(int val) {
        m_peakMin = val;
    }

    public void nullAndCrop(double [] emg_out, double[] rms_out,
		 double[] emg_in, double [] rms_in,
		 int fromIdx)
    {
        int i, j;
        int win = 150;
        double max = 5000.;

        emg_out = new double[(emg_in.length-fromIdx)];
        rms_out = new double[(emg_in.length-fromIdx)];

        for (i=0; i<emg_out.length; i++) {
            emg_out[i] = emg_in[i+fromIdx];
            rms_out[i] = rms_in[i+fromIdx];
        }

  
        for (i=0; i<emg_out.length; i++) {
            if (emg_out[i] > max || emg_out[i] < -max) {
                for (j=i-win; j<i+win; j++) {
                    if (j>0 && j<rms_out.length) {
                        rms_out[j] = 0.;
                    //emg[j] = 0.;
                    }
                }
            } 
        }
    }

  
  
  public void setDelay(int d) {
    m_delay = d;
  }
  
  public void reset() {
    m_ptr = 0;
    
    m_freq = 500.;
    m_local = 0.;
    m_lastPeak = 0;
    m_lastMax = 0.;
    m_currAUC = 0;
    m_currRMS = 0;
    m_have = 0;
    
    m_bufRMS = new double[(int)(m_freq*8+0.5)];
    m_bufEMG = new double[(int)(m_freq*8+0.5)];
    m_size = m_bufRMS.length;

    m_floor = 0.;
    m_floorData = 1.;
    m_floorRate = 0.001;
    m_nullCount = 0;
    m_nullCountDev = 0;

    

  }

  public boolean isSilence() {
    return m_nullCount > 400;
  }
  
  public double getAUC() {
    return m_currAUC;
  }

  public double getRMS() {
        return m_currRMS;
    }

  public double getFloor() {
    return m_floor;
  }
  
  public boolean feed(double rms, double emg) {
     // Keep data
        push_back(rms, emg);


        m_floorData += 0.001;
        double off = 10000;
        m_floor = m_floor*(1.-m_floorRate/m_floorData) + rms*m_floorRate/m_floorData;
        m_lastMax *= (1.-m_floorRate);


        if (rms < 15.) {
            m_nullCount++;
        } else {
            m_nullCount = 0;
        }
    
        //cout << "FEED " << rms << " " << getRMS(m_size-1) << " " << getRMS(m_size-2) << " " << getRMS(m_size-3) << " floor: " << m_floor << endl;
    
        if (m_have > 3) {
            double d = getRMS(m_size-2);
            if (d > getRMS(m_size-3) && d >= getRMS(m_size-1)) {
        	//cout << "PEAK" << endl;

	        if (d > m_floor + m_floorOff && d > m_lastMax * 0.6) {
	            m_local = d;
	            if (d > m_lastMax) {
	                m_lastMax = d;
	            }
	        }
        }
    }

    // Backwards in time
    m_lastPeak++;
    int check = 100;
    if (m_have > m_delay+1+2*check) {
        double d = getRMS(m_size-m_delay);
        if ((d > getRMS(m_size-m_delay-1) && d >= getRMS(m_size-m_delay+1)) &&
	    (d > getRMS(m_size-m_delay-check) && d >= getRMS(m_size-m_delay+check))) {
	    //cout << "PEAK" << endl;

            // ===================================================================
            // Set parameters HERE!!!
            // ===================================================================
	        if (d > m_floor + m_floorOff && d > m_lastMax * 0.4 && m_lastPeak > m_peakMin) {
	            computeAUC(d * 0.2);
	            m_local = d;
	            m_lastPeak = 0;
	            return true;
	        }
        }
    }

    return false;
  }

  
  

  private void computeAUC(double low) {
    m_currAUC = 0.;
    m_currRMS = 0.;
    int i;
    for (i=m_size-m_delay; i>=0; i--) {
        if (getRMS(i) > m_currRMS)
            m_currRMS = getRMS(i);
        if (getRMS(i) < low)
	        break;
        m_currAUC += getRMS(i);
    }
    for (i=m_size-m_delay+1; i<m_size; i++) {
        if (getRMS(i) > m_currRMS)
            m_currRMS = getRMS(i);
        if (getRMS(i) < low)
	        break;
        m_currAUC += getRMS(i);
    }
  }

  
  private void push_back(double rms, double emg) {
    m_bufRMS[m_ptr] = rms;
    m_bufEMG[m_ptr] = emg;
    m_ptr++;
    if (m_ptr == m_bufRMS.length)
      m_ptr = 0;
    m_have++;
  }

  private double getRMS(int i) {
    int idx = (i + m_ptr) % m_bufRMS.length;
    return m_bufRMS[idx];
  }
  
  private double getEMG(int i) {
    int idx = (i + m_ptr) % m_bufEMG.length;
    return m_bufEMG[idx];
  }
  
  private double m_freq;
  private double m_local;
  private double m_lastMax;
  private double m_currAUC;
  private double m_currRMS = 0;
  private double m_floor;
  private double m_floorData;
  
  private int m_nullCount;
  private int m_nullCountDev;
  private double m_floorRate;
  
  private double [] m_bufRMS;
  private double [] m_bufEMG;
  private int m_ptr;
  private int m_have;
  private int m_size;

  int m_delay;
  int m_lastPeak;
}


