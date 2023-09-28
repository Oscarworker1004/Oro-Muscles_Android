#ifndef SUPERCYCLE_H
#define SUPERCYCLE_H

#include "base/SVector.h"
#include "eeg/BTLData.h"

inline double CMin(double a, double b)
{
  if (a < b)
    return a;
  return b;
}

inline double CMax(double a, double b)
{
  if (a > b)
    return a;
  return b;
}

class OneCycle
{
 public:
  OneCycle() {}


  
  bool operator < (const OneCycle & c) const {
    return (m_start + m_stop)/2. < (c.m_start + c.m_stop)/2.;
  }

  string & Name() {return m_name;}
  double & Start() {return m_start;}
  double & Stop() {return m_stop;}
  const string & Name() const {return m_name;}
  const double & Start() const {return m_start;}
  const double & Stop() const {return m_stop;}

  // In fraction
  double Lap(const OneCycle & d) const {
    if (d.m_stop < m_start || d.m_start > m_stop)
      return 0.;

    if (m_name != d.Name())
      return 0.;
    
    double avg = (m_stop - m_start + d.Stop() - d.Start())/2.;
    double s = CMax(m_start, d.m_start);
    double e = CMin(m_stop, d.m_stop);

    //cout << "Raw lap " << (e-s)/avg;
    
    return (e-s)/avg;

  }
  
 private:
  int m_channel;
  string m_name;
  double m_start;
  double m_stop;
};


class CycleLane
{
public:
  CycleLane() {}

  int isize() const {return m_cycles.isize();}
  OneCycle & operator[] (int i) {return m_cycles[i];}
  const OneCycle & operator[] (int i) const {return m_cycles[i];}
  void push_back(const OneCycle & c) {
    m_cycles.push_back(c);
  }

  double Hi() const {
    double hi = 0.;
    for (int i=0; i<m_cycles.isize(); i++) {
      if (m_cycles[i].Stop() > hi)
	hi = m_cycles[i].Stop();
    }
    return hi;
  }
  
  double Lo() const {
    double lo = 10000.;
    for (int i=0; i<m_cycles.isize(); i++) {
      if (m_cycles[i].Start() > lo)
	lo = m_cycles[i].Start();
    }
    return lo;
  }
  
  double Lap(const OneCycle & c, double off) {
    double d = 0.;

    OneCycle tmp = c;

    tmp.Start() += off;
    tmp.Stop() += off;
    
    int i;
    
    for (i=0; i<m_cycles.isize(); i++) {
      d += m_cycles[i].Lap(tmp);
    }
    return d;
  }
  
private:
  svec<OneCycle> m_cycles;
};


class SuperCycle
{
 public:
  SuperCycle() {}

   
  void Write(const string & fileName) const;

  int isize() const {return m_lanes.isize();}
  void push_back(const CycleLane & c) {m_lanes.push_back(c);}
  CycleLane & operator [] (int i) {return m_lanes[i];}
  const CycleLane & operator [] (int i) const {return m_lanes[i];}
  void resize(int i) {m_lanes.resize(i);}

  double Hi() const {
    double hi = 0.;
    for (int i=0; i<m_lanes.isize(); i++) {      
      if (m_lanes[i].Hi() > hi)
	hi = m_lanes[i].Hi();
    }

    return hi;
  }
  double Lo() const {
    double lo = 0.;
    for (int i=0; i<m_lanes.isize(); i++) {      
      if (m_lanes[i].Lo() < lo)
	lo = m_lanes[i].Lo();
    }

    return lo;
  }

  int TotalCycles() const {
    int i;

    int sum = 0;
    for (i=0; i<m_lanes.isize(); i++) {
      sum += m_lanes[i].isize();
    }
    return sum;
  }

  string & Name() {return m_name;}
  const string & Name() const {return m_name;}
  
  
 private:
  string m_name;
  
  svec<CycleLane> m_lanes;
  svec<CycleLane> m_templates;

};




class SuperCycleProc
{
public:
  // Comma-separated list of files
  void Read(const string & fileName,
	    const string & templateName);

  void Process(CycleLane & out); 
  
private:
  void ParseOne(CycleLane & l, const BTLData & d);

  void Fill(CycleLane & out, double from, double to, const string & name, const CycleLane & in);

  SuperCycle m_all;
  svec<SuperCycle> m_templates;
  
};


#endif

