#ifndef IMUTRANS_H
#define IMUTRANS_H

#include "eeg/BTLData.h"
#include <math.h>


// Transformation (for display)
class IMUTrans
{
 public:
  IMUTrans() {
    m_last = 0.;
  }


  svec<double> & Gyro() {return m_gyro;}
  svec<double> & Acc()  {return m_acc;}
  const svec<double> & Gyro() const {return m_gyro;}
  const svec<double> & Acc()  const {return m_acc;}

  void AddGyro(double x, double y, double z) {
    x *= 3.1415/180./100.;
    y *= 3.1415/180./100.;
    z *= 3.1415/180./100.;
    
    double a = sqrt(x*x + y*y + z*z);
    m_gyro.push_back(a);   
  }
  
  void AddAcc(double x, double y, double z) {
    
    double a = sqrt(x*x + y*y + z*z);
    if (m_acc.isize() == 0) {
      m_acc.push_back(0.);
    } else {
      m_acc.push_back(m_last - a);
    }
    m_last = a;
  }

  void Process();

  
 private:
  svec<double> m_gyro;
  svec<double> m_acc;
  double m_last;
  
};

//================= GOOD STUFF STARTS HERE ====================



class IMUInterval
{
public:
  IMUInterval() {
    m_startMS = -1;
    m_endMS = -1;
    m_name = "<none>";
  }
  IMUInterval(int start, int end) {
    m_startMS = start;
    m_endMS = end;
  }

  void Set(int startMS, int endMS) {
    m_startMS = startMS;
    m_endMS = endMS;
  }

  int Start() const {return m_startMS;}
  int End() const {return m_endMS;}

  string & Name() {return m_name;}
  const string & Name() const {return m_name;}

  bool operator < (const IMUInterval & v) const {
    return m_startMS < v.m_startMS;
  }
  
private:
  int m_startMS;
  int m_endMS;
  string m_name;
};


class IMUSignal
{
public:
  IMUSignal() {
    m_weight = 1.;
    m_startMS = 0.;
    m_endMS = 0.;
    m_timeFac = 10;
  }

  void SetWeight(double d) {
    m_weight = d;
  }

  void SetTimeFac(int f) {
    m_timeFac = f;
  }

  void Extract(IMUSignal & out, const IMUInterval & n) const {
    int start = n.Start()/m_timeFac;
    int end = n.End()/m_timeFac;
    
    int len = end - start;

    //cout << "EXTRACT " << start << " " << end << " " << len << endl;
    //if (len > 100000 || len < 0) {
    //  throw;
    // }
    
    out.resize(len);
    out.SetWeight(m_weight);
    out.SetTimeFac(m_timeFac);

    for (int i=start; i<end; i++) {
      if (i >= 0 && i < m_data.isize())
	out[i-start] = m_data[i];
    }
    
  }

  void ZeroCenter()
  {
    int i;
    double sum = 0.;
    
    for (i=0; i<m_data.isize(); i++) {
      sum += m_data[i];
    }
    
    sum /=(double)m_data.isize();
    
    for (i=0; i<m_data.isize(); i++) {
      m_data[i] -= sum;
    } 
  }


  int isize() const {return m_data.isize();}
  double & operator [] (int i) {return m_data[i];}
  const double & operator [] (int i) const {return m_data[i];}
  void push_back(double d) {
    m_data.push_back(d);
  }
  void resize(int n, double d = 0.) {
    m_data.resize(n, d);
  }


  svec<double> & data() {return m_data;}

private:
  svec<double> m_data;
  int m_timeFac;
  string m_name;
  double m_weight;
  double m_startMS;
  double m_endMS;
};


class IMUChannel
{
public:
  IMUChannel() {
    m_weight = 1.;
  }

  string & Type() {return m_type;}
  const string & Type() const {return m_type;}

  
  int isize() const {return m_signals.isize();}
  IMUSignal & operator [] (int i) {return m_signals[i];}
  const IMUSignal & operator [] (int i) const {return m_signals[i];}
  void push_back(const IMUSignal & d) {
    m_signals.push_back(d);
  }
  void resize(int n) {
    m_signals.resize(n);
  }

  void Normalize()
  {
    int i, j;

    for (j=0; j<m_signals.isize(); j++) {
      m_signals[j].ZeroCenter();
    }
    
    double sum = 0.;
    
    for (i=0; i<m_signals[0].isize(); i++) {
      double d = 0;
      for (j=0; j<m_signals.isize(); j++) {
	d += m_signals[j][i]*m_signals[j][i];
      }
      sum += sqrt(d);
    }
    
    sum /= (double)m_signals[0].isize();

    
    for (j=0; j<m_signals.isize(); j++) {
      for (i=0; i<m_signals[j].isize(); i++) {
	m_signals[j][i] /= sum;
      }
    }
    // cerr << "SUM: " << sum << " " << m_signals[0][0] << endl; 
  }

  double & Weight() {return m_weight;}
  const double & Weight() const {return m_weight;}

  
private:
  svec<IMUSignal> m_signals;
  string m_type;
  double m_weight;
};


class IMUSensor
{
public:
  IMUSensor() {}

  void Clone(IMUSensor & out) {
    int i;
    out.Name() = m_name;
    out.resize(isize());
    for (int i=0; i<isize(); i++) {
      const IMUChannel & ch = m_channels[i];
      out[i].resize(ch.isize());
      out[i].Type() = ch.Type();
      out[i].Weight() = ch.Weight();
    }
  }
  
  void Cut(IMUSensor & out, const IMUInterval & n)
  {
    int i, j;
    
    //out = *this;
    
    //out.Name() = m_name;
    //out.resize(isize());
    
    Clone(out);
    
    //cout << "Cut - " << isize() << endl;
    for (int i=0; i<isize(); i++) {
      const IMUChannel & ch = m_channels[i];
      //out[i].resize(ch.isize());
      //out[i].Type() = ch.Type();
      //out[i].Weight() = ch.Weight();
      //cout << "  -> " << ch.isize() << endl;
      for (j=0; j<ch.isize(); j++) {
	ch[j].Extract(out[i][j], n);
      } 
    }
  }

  //IMUChannel

  int isize() const {return m_channels.isize();}
  IMUChannel & operator [] (int i) {return m_channels[i];}
  const IMUChannel & operator [] (int i) const {return m_channels[i];}
  void push_back(const IMUChannel & d) {
    m_channels.push_back(d);
  }
  void resize(int n) {
    m_channels.resize(n);
  }

  string & Name() {return m_name;}
  const string & Name() const {return m_name;}


  void Normalize() {
    for (int i=0; i<m_channels.isize(); i++)
      m_channels[i].Normalize();
  }
  
  double Self();

  double SlideEuc(int from, const IMUSensor & temp, int skip = 0);

  double ComputeMatch(svec<double> & d, const IMUSensor & temp);

  double ComputeMatchWiggle(int & start, int & stop,
			    int from,
			    const IMUSensor & temp);

  void GetActivity(svec<IMUInterval> & out);

  void GetActivitySeeded(svec<IMUInterval> & out, const svec<IMUInterval> & seed);

  void ActivityBySimilarity(svec < svec < double > > & data, const svec<IMUInterval> & in);

private:
  string m_name;
  svec<IMUChannel> m_channels;
};







inline double IMUAbs(double d) {
  if (d < 0.)
    return -d;
  return d;
}
















#endif
