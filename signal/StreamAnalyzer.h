#ifndef STREAMANALYZER_H
#define STREAMANALYZER_H

#include "base/SVector.h"

void NullAndCrop(svec<double> & emg_out, svec<double> & rms_out,
		 const svec<double> & emg_in, const svec<double> & rms_in,
		 int fromIdx = 0)
{
  int i, j;
  int win = 150;
  double max = 5000.;

  emg_out.resize(emg_in.isize()-fromIdx, 0.);
  rms_out.resize(emg_in.isize()-fromIdx, 0.);

  for (i=0; i<emg_out.isize(); i++) {
    emg_out[i] = emg_in[i+fromIdx];
    rms_out[i] = rms_in[i+fromIdx];
  }

  
  for (i=0; i<emg_out.isize(); i++) {
    if (emg_out[i] > max || emg_out[i] < -max) {
      for (j=i-win; j<i+win; j++) {
	if (j>0 && j<rms_out.isize()) {
	  rms_out[j] = 0.;
	  //emg[j] = 0.;
	}
      }
    } 
  }
}


class StreamAnalyzer
{
public:
  StreamAnalyzer() {
    m_delay = 2;
    m_floorData = 0.;
    Reset();
  }

  void SetDelay(int d) {
    m_delay = d;
  }
  
  void Reset() {
    m_ptr = 0;
    
    m_freq = 500.;
    m_local = 0.;
    m_lastPeak = 0;
    m_lastMax = 0.;
    m_currAUC = 0;
    m_have = 0;
    m_bufRMS.clear();
    m_bufEMG.clear();
    
    m_bufRMS.resize((int)(m_freq*8+0.5), 0.);
    m_bufEMG.resize((int)(m_freq*8+0.5), 0.);
    m_size = m_bufRMS.isize();

    m_floor = 0.;
    m_floorData = 1.;
    m_floorRate = 0.001;
    m_nullCount = 0;
    m_nullCountDev = 0;

    

  }

  bool IsSilence() {
    return m_nullCount > 1000;
  }
  
  double GetAUC() const {
    return m_currAUC;
  }

  double GetFloor() const {
    return m_floor;
  }
  
  bool Feed(double rms, double emg = 0.) {
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

	if (d > m_floor + 20. && d > m_lastMax * 0.6) {
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

	if (d > m_floor + 20. && d > m_lastMax * 0.6 && m_lastPeak > 150) {
	  ComputeAUC(d * 0.2);
	  m_local = d;
	  m_lastPeak = 0;
	  return true;
	}
      }
    }

    return false;
  }

  
  
private:
  void ComputeAUC(double low) {
    m_currAUC = 0.;
    int i;
    for (i=m_size-m_delay; i>=0; i--) {
      if (getRMS(i) < low)
	break;
      m_currAUC += getRMS(i);
    }
    for (i=m_size-m_delay+1; i<m_size; i++) {
      if (getRMS(i) < low)
	break;
      m_currAUC += getRMS(i);
    }
  }

  
  void push_back(double rms, double emg) {
    m_bufRMS[m_ptr] = rms;
    m_bufEMG[m_ptr] = emg;
    m_ptr++;
    if (m_ptr == m_bufRMS.isize())
      m_ptr = 0;
    m_have++;
  }

  double getRMS(int i) const {
    int idx = (i + m_ptr) % m_bufRMS.isize();
    return m_bufRMS[idx];
  }
  
  double getEMG(int i) const {
    int idx = (i + m_ptr) % m_bufEMG.isize();
    return m_bufEMG[idx];
  }
  
  double m_freq;
  double m_local;
  double m_lastMax;
  double m_currAUC;
  double m_floor;
  double m_floorData;
  
  int m_nullCount;
  int m_nullCountDev;
  double m_floorRate;
  
  svec<double> m_bufRMS;
  svec<double> m_bufEMG;
  int m_ptr;
  int m_have;
  int m_size;

  int m_delay;
  int m_lastPeak;
};

#endif
