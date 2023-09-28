#ifndef EHMM_H
#define EHMM_H

#include "base/SVector.h"
#include <math.h>

class HMMVec
{
 public:
  HMMVec() {
    m_time = 0.;
  }


  double & operator[] (int i) {return m_data[i];}
  const double & operator[] (int i) const {return m_data[i];}
  int isize() const {return m_data.isize();}
  void resize(int n, double d = 0.) {m_data.resize(n, d);}
  void push_back(double d) {m_data.push_back(d);}

  void operator += (const HMMVec & h) {
    for (int i=0; i<isize(); i++)
      m_data[i] += h[i];
  }

  void operator /= (double d) {
    for (int i=0; i<isize(); i++)
      m_data[i] /= d;
  }

  double Dist(const HMMVec & h) const {
    double d = 0.;
    for (int i=0; i<m_data.isize(); i++) {
      d += (m_data[i]-h[i])*(m_data[i]-h[i]);
    }
    d = sqrt(d);
    return d;
  }


  // Bhattacharyya distance
  double BhattDist(const HMMVec & h) const {
    double d = 0.;
    for (int i=0; i<m_data.isize(); i++) {
      d += sqrt(m_data[i]*h[i]);
    }
    d = -log(d+1.);
    return d;
    
  }

  double Amplitude() const {
    int i;
    double d = 0.;
    for (i=0; i<m_data.isize(); i++) {
      d += (m_data[i]*m_data[i]);
    }
    return sqrt(d);
  }


  bool operator < (const HMMVec & d) const {
    return Amplitude() < d.Amplitude();
  }

  const string & Label() const {return m_label;}
  string & Label() {return m_label;}

  const double & Time() const {return m_time;}
  double & Time() {return m_time;}
  
 private:
  svec<double> m_data;
  string m_label;
  double m_time;
};


class HMMVecStream
{
 public:

  HMMVec& operator[] (int i) {return m_data[i];}
  const HMMVec & operator[] (int i) const {return m_data[i];}
  int isize() const {return m_data.isize();}
  void resize(int n) {m_data.resize(n);}
  void push_back(const HMMVec & d) {m_data.push_back(d);}

  
 private:
  svec<HMMVec> m_data;
};


class TraceBack
{
 public:
  TraceBack() {}
  
  double & operator[] (int i) {return m_data[i];}
  const double & operator[] (int i) const {return m_data[i];}
  int isize() const {return m_data.isize();}
  void resize(int n, double d = 0.) {
    m_data.resize(n, d);
    m_prev.clear();
    m_prev.resize(n, -1);

  }
  void push_back(double d) {m_data.push_back(d);}

  int Prev(int i) const {return m_prev[i];}
  void SetPrev(int i, int s) {m_prev[i] = s;}

 private:
  svec<double> m_data;
  svec<int> m_prev;

};

class ScorePair
{
 public:
  double score;
  int index;
  bool operator < (const ScorePair & s) const {
    return -score < -s.score;
  }
  
};


void Read(svec<HMMVec> & d, const string & fileName);
void Write(const svec<HMMVec> & d, const string & fileName);


const double HMM_INFINITY = 999999999.;

class PenaltyMatrix
{
public:
  PenaltyMatrix() {
    m_def = 200.;
  }

  void SetDefault(double d) {
    m_def = d;
  }

  void Read(const string & fileName);
  void Write(const string & fileName) const;

  void Set(int from, int to, double d) {
    if (from >= m_data.isize()) {
      m_data.resize(from+1);
    }
    if (to >= m_data[from].isize())
      m_data[from].resize(to+1, m_def);
    (m_data[from])[to] = d;
  }
  
  double Get(int from, int to) const {
    if (from >= m_data.isize()) {
      return m_def;
    }
    if (to >= m_data[from].isize())
      return m_def;
    
    return (m_data[from])[to];
  }
  
  
private:
  double m_def;

  svec < svec < double > > m_data;
};


class EHMM
{
 public:
  EHMM() {
    m_pen = 200.;
    m_matrix.SetDefault(m_pen);
    
    m_bStupid = true;
  }

  void ReadPenMatrix(const string & fileName) {
    m_matrix.Read(fileName);
  }
  
  void WritePenMatrix(const string & fileName) const {
    m_matrix.Write(fileName);
  }
  
  void Setup(int frames, int dim);

  void Process(const svec<HMMVec> & d, bool bSplit);

  void DynProg(svec<int> & traceback, const svec<HMMVec> & d);
  
  void Reset();

  void Feed(int frame, const HMMVec & d);

  void BackTrace(svec<int> & tb);

  void Read(const string & fileName) {
    ::Read(m_data, fileName);
    m_tb.resize(m_data.isize());
  }
  
  void Write(const string & fileName) const {
    ::Write(m_data, fileName);
    
  }

  // Use -1 for no hint!
  void SetHints(const svec<int> & hints) {
    m_hints = hints;
  }
  
  void ClearHints() {
    m_hints.clear();
  }

  int isize() const {return m_data.isize();}
  void resize(int n) {m_data.resize(n);}

  const HMMVec & operator[] (int i) const {return m_data[i];}
  HMMVec & operator[] (int i) {return m_data[i];}

  void push_back(const HMMVec & v) {
    m_data.push_back(v);
  }

  void BeStupid(bool b) {
    m_bStupid = b;
  }

 private:
  double Dist(const HMMVec & d, int state)
  {
    return m_data[state].Dist(d);
  }
  
  double MinDist(const HMMVec & d)
  {
    double min = m_data[0].Dist(d);
    for (int i=1; i<m_data.isize(); i++) {
      double dd = m_data[i].Dist(d);
      if (dd < min)
	min = dd;
    }
    return min;
  }

  double m_pen;
  
  svec<HMMVec> m_data;
  svec<TraceBack> m_tb;
  svec<int> m_hints;

  bool m_bStupid;

  PenaltyMatrix m_matrix;
  
};



#endif

