#ifndef _CROSSCORR_H_
#define _CROSSCORR_H_

#include "base/SVector.h"
#include "extern/RealFFT/FFTReal.h"









class CrossCorrelation
{
 public:
  CrossCorrelation();
  ~CrossCorrelation();
  void CrossCorrelate(svec<float> & out, const svec<float> & one, const svec<float> & two);
  
  void AutoCorrelate(vector<float> &out, vector<float> &in);

  void FFT(svec<double> &out, const svec<double> &in);

  void DoOne(svec<float> & o, const svec<float> & in1, const svec<float> & in2);

 private:
  FFTReal<float> * m_pFFT;
  int m_size;

};





#endif

