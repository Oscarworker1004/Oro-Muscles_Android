#ifndef FREQ_H
#define FREQ_H

#include "base/SVector.h"
#include "signal/CrossCorr.h"

class FreqVec
{
 public:
  FreqVec() {}

  int isize() const {return m_data.isize();}
  double & operator [] (int i) {return m_data[i];}
  const double & operator [] (int i) const {return m_data[i];}
  void resize(int n) {m_data.resize(n, 0.);}
  
  int & Time() {return m_time;}
  const int & Time() const {return m_time;}

  
 private:
  svec<double> m_data;
  int m_time;
  
};


class FreqSequence
{
 public:
  FreqSequence() {
    m_frame = 256;
  }

  int isize() const {return m_data.isize();}
  FreqVec & operator [] (int i) {return m_data[i];}
  const FreqVec & operator [] (int i) const {return m_data[i];}

  void Feed(const svec<double> & channel);

  void Print(const string & fileName);

  void SetFrame(int n) {
    m_frame = n;
  } 
  
 private:
  void OneFrame(FreqVec & out, const svec<double> & channel, int from);
  void Bin(FreqVec & out, const svec<double> & freq);
  
  svec<FreqVec> m_data;
  CrossCorrelation m_corr;
  int m_frame;
};



#endif

