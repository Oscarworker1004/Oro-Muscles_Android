#include "signal/Smooth.h"
#include <math.h>



void Smooth::ApplyFlat(svec<double> & x) const
{
  int i, j;

  svec<double> tmp;
  tmp.resize(x.isize(), 0.);


  double div = 0.;

  for (i=-m_win; i<=m_win; i++)
    div += Val(i, m_decay);
  
  for (i=0; i<x.isize(); i++) {
    if (i < m_win || i >= x.isize()-m_win) {
      tmp[i] = x[i];
      continue;
    }
    for (j=i-m_win; j<=i+m_win; i++) {
      tmp[i] += x[j]*Val(j-i, m_decay)/div;
    }
    
  }
  x = tmp;
}

void Smooth::ApplyZK(svec<double> & x, double range) const
{

  int i, j;

  svec<double> tmp;
  tmp.resize(x.isize(), 0.);


  
  for (i=0; i<x.isize(); i++) {
    //cout << i << endl;
    if (i < m_win || i >= x.isize()-m_win) {
      tmp[i] = x[i];
      continue;
    }
    double decay = range/(sqrt(m_mult*x[i])+1);
    double div = 0.;
    //cout << "Sig: " << x[i] << " decay: " << decay << endl;
    for (j=-m_win; j<=m_win; j++)
      div += Val(j, decay);

    //cout << "Hmm" << endl;
    for (j=i-m_win; j<=i+m_win; j++) {
      //cout << div << " " << Val(j-i, decay) << endl;
      tmp[i] += x[j]*Val(j-i, decay)/div;
      //cout << x[i] << " -> " << tmp[i] << endl;
    }
    
  }
  x = tmp;
}



void SmoothFlat(svec<double> & f, int win)
{

}
