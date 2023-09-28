#ifndef SIGNALS_H
#define SIGNALS_H


#include "base/SVector.h"
#include "extern/filter-c/filter.h"



class BWHighPassFilter
{
private:
  BWHighPassFilter() {}
  BWHighPassFilter(const BWHighPassFilter & f) {}
  
public:
  
  BWHighPassFilter(int order, float sampFreq, float hpSampFreq);

  ~BWHighPassFilter();

  float Apply(float input);

private:
  BWHighPass * m_pFilter;
  int m_sample;
};


class RMSStream
{
public:
  RMSStream(int len = 15);

  double Apply(double input);

private:
  svec<double> m_past;
  svec<double> m_weight;
  
};



#endif

