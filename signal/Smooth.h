#ifndef SMOOTH_H
#define SMOOTH_H


#include "base/SVector.h"
#include <math.h>

class Smooth
{
 public:
  Smooth() {
    m_win = 5;
    m_decay = 0.;
    m_mult = 1.;
  }

  void SetDecay(double d) {m_decay = d;}
  void SetWin(int w) {m_win = w;}
  void SetMult(int d) {m_mult = d;}

  void ApplyFlat(svec<double> & x) const;
  void ApplyZK(svec<double> & x, double range) const;


  
 private:
  double Val(int dist, double decay) const {
    double d = exp(-dist*dist*decay*decay);
    return d;
  }
  
  int m_win;
  double m_decay;
  double m_mult;

};


void SmoothFlat(svec<double> & f, int win);


#endif //SMOOTH_H

