#ifndef FORCE_DEBUG
#define NDEBUG
#endif



 
#include "signal/CrossCorr.h"
#include "extern/RealFFT/FFTReal.hpp"
#include <math.h>






void ComplexMult(float & r_o, float & i_o, float r1, float i1, float r2, float i2) 
{
  r_o = r1 * r2 - i1 * i2;
  i_o = i1 * r2 + i2 * r1;

  //  cout << "r1=" << r1 << " i1=" << i1 << " r2=" << r2 << " i2=" << i2 << " r_o=" << r_o << " i_o=" << i_o << endl;
}

double Ent(double p) 
{
  if (p < 0.001)
    return 0;
  return p * log(p) / 0.69314718056;
}


void Smooth(svec<float> & out, const svec<float> & in)
{
  int i;
  out.clear();
  out.resize(in.isize(), 0);
  for (i=1; i<in.isize()-1; i++) {
    float d = in[i] + (in[i-1] + in[i+1])/2;
    out[i] = d / 2;
  }
}





//==================================================================
CrossCorrelation::CrossCorrelation()
{
  m_pFFT = NULL;
  m_size = 0;
}

CrossCorrelation::~CrossCorrelation()
{
  if (m_pFFT != NULL)
    delete m_pFFT;
}


void CrossCorrelation::AutoCorrelate(vector<float> &out, vector<float> &in)
{
  out.clear();

  int N=FFTReal_get_next_pow2((long) in.size());

  in.resize(N,0);
  out.resize(N,0);
  
  vector<float> X(N,0),tmp(N,0);

  m_pFFT = new FFTReal<float> (N);

  m_pFFT->do_fft(&X[0],&in[0]);
  
  for (int i=0; i<=N/2; ++i )
    tmp[i] = (X[i]+X[i+N/2])*(X[i]-X[i+N/2]);

  m_pFFT->do_ifft(&tmp[0],&out[0]);
  m_pFFT->rescale(&out[0]);
}

void CrossCorrelation::FFT(svec<double> &out, const svec<double> &in_raw)
{
  int i;
  
  vector<float> in;
  in.resize(in_raw.isize(), 0.);
  for (i=0; i<in_raw.isize(); i++)
    in[i] = (float)in_raw[i];

  //cout << in.size() << endl;
  
  //int N=FFTReal_get_next_pow2((long) in.size());
  int N = in_raw.isize();
  
  in.resize(N,0);
  out.resize(N,0);
  
  vector<float> X(N,0),tmp(N,0);

  if (m_pFFT == NULL)
    m_pFFT = new FFTReal<float> (N);

  //cout << "Run fft " << m_pFFT << " N=" << N << endl;
  
  m_pFFT->do_fft(&X[0], &in[0]);

  out.resize(N/2, 0.);
  for (int i=0; i<N/2; i++)
    out[i] = sqrt(X[i]*X[i] + X[i+N/2]*X[i+N/2]);


}

void CrossCorrelation::CrossCorrelate(svec<float> & out, const svec<float> & one, const svec<float> & two)
{
  out.clear();
  
  out.resize(one.isize(), 0.);

  int i, j;

 
  DoOne(out, one, two);

}



void CrossCorrelation::DoOne(svec<float> & o, const svec<float> & in1, const svec<float> & in2)
{
  int i;
  o.resize(in1.size(), 0);

  if (m_pFFT == NULL || m_size != in1.isize()) {
    m_size = in1.isize();
    if (m_pFFT != NULL) {
      cout << "WARNING: re-instantiating FFT object!" << endl;
      delete m_pFFT;
    }
    //cout << "Initializing FFT." << endl;
    m_pFFT = new FFTReal<float>(m_size);
    //cout << "done." << endl;
  }

  svec<float> tmp1;
  tmp1.resize(in1.size(), 0.);
  svec<float> tmp2;
  tmp2.resize(in2.size(), 0.);

  float * p1 = &tmp1[0];
  float * p2 = &tmp2[0];



  m_pFFT->do_fft(p1, &in1[0]);


  m_pFFT->do_fft(p2, &in2[0]);


  int N = tmp1.isize() / 2;
  //cout << "N=" << N << endl;

  tmp1[0] *= tmp2[0];
  //tmp1[N] *= tmp2[N];
  for (i=1; i<N-1; i++) {
    float fr, fi;

    //ComplexMult(fr, fi, tmp1[i], tmp1[i+N+1], tmp2[i], -tmp2[i+N+1]);
    //tmp1[i] = fr;
    //tmp1[i+N+1] = -fi;

    ComplexMult(fr, fi, tmp1[i], tmp1[i+N], tmp2[i], -tmp2[i+N]);
    tmp1[i] = fr;
    tmp1[i+N] = -fi;
  }
  m_pFFT->do_ifft(p1, p2);
  m_pFFT->rescale(p2);

  //for (i=0; i<2*N; i++)
  //o[i] = tmp2[i];

  
  for (i=0; i<N; i++) {
    o[i] = tmp2[i+N];
  }
  for (i=N; i<2*N; i++) {
    o[i] = tmp2[i-N];
  }
 
}




