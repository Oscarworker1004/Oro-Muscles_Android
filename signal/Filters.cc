#include "signal/Filters.h"
#include <math.h>

BWHighPassFilter::BWHighPassFilter(int order, float sampFreq, float hpSampFreq)
{
  m_pFilter = create_bw_high_pass_filter(order, sampFreq, hpSampFreq);
  m_sample = 0;
}

BWHighPassFilter:: ~BWHighPassFilter()
{
  free_bw_high_pass(m_pFilter);
}

float BWHighPassFilter::Apply(float input)
{
  float out = bw_high_pass(m_pFilter, input);
  if (m_sample < 20)
    out = 0.;
  m_sample++;
  return out;
}


//===============================================

RMSStream::RMSStream(int len)
{
  m_past.resize(len, 0.);
  m_weight.resize(len, 0.);

  double s = 2./(double)len;

  int i;
  
  double sum = 0.;
  for (i=0; i<len; i++) {
    m_weight[i] = exp(-s*(double)(len - i - 1));
    sum += m_weight[i];
    //cout << "RMS weight " << i << " -> " << m_weight[i] << endl;
  }

  for (i=0; i<len; i++) {
    m_weight[i] /= sum;
  }  
}

double RMSStream::Apply(double input)
{
  int i;

  if (input < 0.)
    input = -input;
  
  // TODO: use a ring buffer!!
  double out = 0.;
  for (i=0; i<m_past.isize()-1; i++) {
    m_past[i] = m_past[i+1];
    out += m_past[i] * m_weight[i];
  }
  m_past[m_past.isize()-1] = input;
  out += input * m_weight[m_weight.isize()-1];

  return out;
}
