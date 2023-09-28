#include "math/Rotation.h"


void DVector::Print() const
{
  int j;
  for (j=0; j<isize(); j++) {
    cout << m_vec[j] << "\t";
  }
  cout << endl;

}

DMatrix::DMatrix()
{
}

DMatrix::DMatrix(int x, int y)
{
  SetSize(x, y);
}


DVector DMatrix::operator * (const DVector & d) const
{
  int i, j;
  DVector out(Y());

  for (j=0; j<Y(); j++) {
    for (i=0; i<X(); i++) {
      out[j] += (*this)[i][j] * d[i];
    }
  }
  return out;
}

void DMatrix::Print() const
{
  int i, j;
  for (i=0; i<Y(); i++) {
    for (j=0; j<X(); j++) {
      cout << get(i, j) << "\t";
    }
    cout << endl;
  }

}


void DMatrix3DRot::Set(const DVector & axis, double phi)
{
  SetSize(3, 3);
  DVector u = axis.Unity();
  get(0, 0) = cos(phi) + u[0]*u[0]*(1-cos(phi));
  get(1, 0) = u[0]*u[1]*(1-cos(phi)) - u[2]*sin(phi);
  get(2, 0) = u[0]*u[2]*(1-cos(phi)) + u[1]*sin(phi);

  get(0, 1) = u[1]*u[0]*(1-cos(phi)) + u[2]*sin(phi);
  get(1, 1) = cos(phi) + u[1]*u[1]*(1-cos(phi));
  get(2, 1) = u[1]*u[2]*(1-cos(phi)) - u[0]*sin(phi);

  get(0, 2) = u[2]*u[0]*(1-cos(phi)) - u[1]*sin(phi);
  get(1, 2) = u[2]*u[1]*(1-cos(phi)) + u[0]*sin(phi);
  get(2, 2) = cos(phi) + u[2]*u[2]*(1-cos(phi));
  
}


DVector DMatrix3DRot::Axis(const DVector & a, const DVector & b, const DVector & c) const
{
  DVector u = (a-b).Cross(a-c);
  return u;
}
