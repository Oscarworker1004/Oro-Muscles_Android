#ifndef ROTATION_H
#define ROTATION_H

#include <math.h>
#include "base/SVector.h"


class DVector
{
 public:
  DVector(int dim = 3) {
    m_vec.resize(dim, 0.);
  }
  DVector(double x, double y, double z) {
    m_vec.resize(3, 0.);
    m_vec[0] = x;
    m_vec[1] = y;
    m_vec[2] = z;
  }

  double & operator[] (int i) {return m_vec[i];}
  const double & operator[] (int i) const {return m_vec[i];}
  void resize(int dim) {
    m_vec.resize(dim, 0.);
  }
  int isize() const {return m_vec.isize();}

  DVector Unity() const {
    double d = 0.;
    int i;
    for (i=0; i<m_vec.isize(); i++) {
      d += m_vec[i]*m_vec[i];
    }
    d = sqrt(d);
    DVector out(isize());
    for (i=0; i<isize(); i++)
      out[i] = m_vec[i]/d;
    return out;
  }

  DVector Cross(const DVector & d) {
    DVector out;
    out.resize(isize());
    out[0] = m_vec[1]*d[2] - m_vec[2]*d[1];
    out[1] = m_vec[2]*d[0] - m_vec[0]*d[2];
    out[2] = m_vec[0]*d[1] - m_vec[1]*d[0];

    return out.Unity();
  }

  void operator -= (const DVector & d) {
    for (int i=0; i<isize(); i++)
      m_vec[i] -= d[i];
  }
  void operator += (const DVector & d) {
    for (int i=0; i<isize(); i++)
      m_vec[i] += d[i];
  }

  void operator *= (double d) {
    for (int i=0; i<isize(); i++)
      m_vec[i] *= d;
  }

  

  DVector operator - (const DVector & d) const {
    DVector out = * this;
    out -= d;
    return out;
  }

  DVector operator + (const DVector & d) const {
    DVector out = *this;
    out += d;
    return out;
  }

  
  
  void Print() const;

 private:
  svec<double> m_vec;
};



class DMatrix
{
 public:
  DMatrix();

  DMatrix(int x, int y);

  svec<double> & operator[](int i) {return m_matrix[i];}
  const svec<double> & operator[](int i) const {return m_matrix[i];}
  double & get(int x, int y) {return (*this)[x][y];}
  const double & get(int x, int y) const {return (*this)[x][y];}

  void SetSize(int x, int y) {
    m_matrix.resize(x);
    for (int i=0; i<x; i++)
      m_matrix[i].resize(y, 0);
  }

  int X() const {return m_matrix.isize();}
  int Y() const {
    if (m_matrix.isize() == 0)
      return 0;
    return m_matrix[0].isize();
  }

  DVector operator * (const DVector & d) const;

  void Print() const;
  
 private:
  svec< svec < double > > m_matrix;
};


class DMatrix3DRot : public DMatrix
{
 public:
  DMatrix3DRot() : DMatrix() {
  } 

  DMatrix3DRot(int x, int y) : DMatrix(x, y) {
  }


  void Set(const DVector & axis, double phi);

  DVector Axis(const DVector & a, const DVector & b, const DVector & c) const;
};





#endif

