#ifndef TIMEREFINE_H
#define TIMEREFINE_H


#include "base/SVector.h"
#include "eeg/BTLData.h"
#include "eeg/Hints.h"
#include "base/Config.h"
#include <math.h>

class IMUSensor;
class IMUInterval;

class TimeRefine
{
 public:
  TimeRefine() {
    m_thresh = 5.;
    m_pCfg = NULL;
  }

  void Refine(BTLData & d) const;
  void ApplyHints(BTLData & d, const Hints & h) const;

  void ApplySets(Hints & h, const BTLData & d) const; 
  
  void SetConfig(Config * p) {
    m_pCfg = p;
  }

  void MakeHeatmap(svec< svec < double > > & data, svec<IMUInterval> & iv, const BTLData & d);

private:
  bool IsValid(int start, int stop, const BTLData & d) const; 

  void SetupSensor(IMUSensor & sens, const BTLData & d) const;
  
  double Dist(const BTLData & d, int from, const TDShape & s) const {
    int i;
    double dist = 0.;
    for (i=0; i<s.isize(); i++) {
      dist += (s[i] - d.EXG()[i+from].RMS())*(s[i] - d.EXG()[i+from].RMS());
    }
    dist = sqrt(dist/(double)s.isize());
    return dist;
  }
  
  double Dot(const BTLData & d, int from, const TDShape & s, bool bPrint = false) const;
  
  void Set(BTLData & d, int from, int to, int state, const string & cycle) const {
    int i;
    for (i=from; i<to; i++) {
      if (cycle != "")
	d.EXG()[i].Cycle() = cycle;
      else
	d.EXG()[i].State() = state;
    }
  }

  void AddCounts(BTLData & d) const;

  
  double m_thresh;
  Config * m_pCfg;

};



class Peak
{
public:
  Peak() {}

  double & Sum() {return m_sum;}
  const double & Sum() const {return m_sum;}

  int & Frame() {return m_frame;}
  const int & Frame() const {return m_frame;}

  bool operator < (const Peak & p) const {
    return -m_sum < -p.m_sum;
  }

  int & Size() {return m_size;}
  const int & Size() const {return m_size;}

  int & State() {return m_state;}
  const int & State() const {return m_state;}

  string & Name() {return m_name;}
  const string & Name() const {return m_name;}

  double & Self() {return m_self;}
  const double & Self() const {return m_self;}
  
private:
  double m_sum;
  int m_frame;
  int m_size;
  int m_state;
  string m_name;
  double m_self;
};













#endif //TIMEREFINE_H


