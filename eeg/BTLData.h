#ifndef BTLDATA_H
#define BTLDATA_H

#include "base/SVector.h"
#include "base/Config.h"
#include <math.h>



class EXGData
{
 public:
  EXGData() {
    m_sig = 0.;
    m_rms = 0.;
    m_time = 0.;
    m_rep = 0;
    m_state = 0;
    m_mdf = 0.;
    m_mnf = 0.;
   
  }

  double & Signal() {return m_sig;}
  const double & Signal() const {return m_sig;}
  double & RMS() {return m_rms;}
  const double & RMS() const {return m_rms;}
  string & Raw() {return m_raw;}
  const string & Raw() const {return m_raw;}
  double & Time() {return m_time;}
  const double & Time() const {return m_time;}
  double & MNF() {return m_mnf;}
  const double & MNF() const {return m_mnf;}
  double & MDF() {return m_mdf;}
  const double & MDF() const {return m_mdf;}

  int & Rep() {return m_rep;}
  const int & Rep() const {return m_rep;}
  int & State() {return m_state;}
  const int & State() const {return m_state;}
  string & Cycle() {return m_cycle;}
  const string & Cycle() const {return m_cycle;}

  
 private:
  double m_sig;
  double m_rms;
  string m_raw;
  double m_time;
  double m_mdf;
  double m_mnf;
  int m_rep;
  int m_state;
  string m_cycle;
};


class IMUData
{
 public:
  IMUData() {
    m_time = 0.;
    m_rep = 0;
    m_state = 0;
    m_proc = 0.;
  }

  double & operator [] (int i) {return m_data[i];}
  const double & operator [] (int i) const {return m_data[i];}
  int isize() const {return m_data.isize();}
  void resize(int n) {m_data.resize(n, 0.);}
  void push_back(double n) {m_data.push_back(n);}

  const double & Time() const {return m_time;}
  double & Time() {return m_time;}
  
  const double & Processed() const {return m_proc;}
  double & Processed() {return m_proc;}

  

  int & Rep() {return m_rep;}
  const int & Rep() const {return m_rep;}
  int & State() {return m_state;}
  const int & State() const {return m_state;}

  
 private:
  svec<double> m_data;
  double m_time;
  double m_proc;
  int m_rep;
  int m_state;
};


class BTLData
{
 public:
  BTLData() {
    m_pCfg = NULL;
  }

  void SetConfig(Config * p) {
    m_pCfg = p;
  }

  void AutoAnnotate();
  
  svec<EXGData> & EXG() {return m_exg;}
  svec<IMUData> & Acc() {return m_acc;}
  svec<IMUData> & Gyro() {return m_gyro;}
  const svec<EXGData> & EXG() const {return m_exg;}
  const svec<IMUData> & Acc() const {return m_acc;}
  const svec<IMUData> & Gyro() const {return m_gyro;}
  
  const svec<IMUData> & Spectra() const {return m_spec;}
  svec<IMUData> & Spectra() {return m_spec;}

  void GetEXGData(svec<double> & d);

  void GetIMUData(svec < svec <double> > & d);

  void ClearCycles() {
    for (int i=0; i<EXG().isize(); i++) {
      EXG()[i].Cycle() = "";
    }
  }

  void FillAccData(svec<double> & d, int idx) const;
  void FillGyrData(svec<double> & d, int idx) const;

  
  // Getters for feature extraction
  void GetEXG(svec<double> & d, int from, int to) {
    d.clear();
    for (int i=from; i<to; i++)
      d.push_back(m_exg[i].Signal());
  }
  void GetRMS(svec<double> & d, int from, int to) {
    d.clear();
    for (int i=from; i<to; i++)
      d.push_back(m_exg[i].RMS());
  }
  void GetMNF(svec<double> & d, int from, int to) {
    d.clear();
    for (int i=from; i<to; i++)
      d.push_back(m_exg[i].MNF());
  }
  void GetMDF(svec<double> & d, int from, int to) {
    d.clear();
    for (int i=from; i<to; i++)
      d.push_back(m_exg[i].MDF());
  }
  
  void GetACC(svec<double> & d, int from, int to) {
    d.clear();
    for (int i=from; i<to; i++)
      d.push_back(m_acc[i].Processed());
  }
  void GetGyro(svec<double> & d, int from, int to) {
    d.clear();
    for (int i=from; i<to; i++)
      d.push_back(m_gyro[i].Processed());
  }

  void GetAccXYZ(svec<double> & d, int from, int to, int idx) {
    d.clear();
    for (int i=from; i<to; i++)
      d.push_back((m_acc[i])[idx]);
  }  
  void GetAccMag(svec<double> & d, int from, int to) {
    d.clear();
    for (int i=from; i<to; i++)
      d.push_back(sqrt((m_acc[i])[0] * (m_acc[i])[0]
		       + (m_acc[i])[1] * (m_acc[i])[1]
		       + (m_acc[i])[2] * (m_acc[i])[2]));
  }  
  void GetGyroXYZ(svec<double> & d, int from, int to, int idx) {
    d.clear();
    for (int i=from; i<to; i++)
      d.push_back((m_gyro[i])[idx]);
  }
  
  void Read(const string & fileName);
  void Write(const string & fileName) const;

  bool ReadIntUniv(const string & fileName);
  void WriteIntUniv(const string & fileName) const;

  void SmoothIMU();
  
 private:
  bool ReadInt(const string & fileName);
  void PostProcess();


  void SpikeFilter();

  svec<EXGData> m_exg;
  svec<IMUData> m_acc;
  svec<IMUData> m_gyro;
  svec<IMUData> m_spec;
  string m_headEXG;
  string m_headAcc;
  string m_headGyro;
  Config * m_pCfg;
};



#endif //BTLDATA_H
