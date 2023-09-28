#include "src/RecordCtrl.h"
#include <math.h>
#include "math/Rotation.h"



class Triangle
{
public:
  Triangle() {
  }


  // Assumes a triangle a/x - b/x - c/y!
  void Move(svec<DVector> & cc, double x, double y, double z)
  {
    const DVector & a = cc[0];
    const DVector & b = cc[1];
    const DVector & c = cc[2];
   
    DVector dx = b - a;
    DVector dy = a + b;
    dy *= 0.5;
    dy = c - dy;

    DMatrix3DRot rot;
    DVector dz = rot.Axis(a, b, c);

    dx = dx.Unity();
    dy = dy.Unity();
    dz = dz.Unity();

    dx *= x;
    dy *= y;
    dz *= z;

    for (int i=0; i<cc.isize(); i++) {
      cc[i] += dx;
      cc[i] += dy;
      cc[i] += dz;
   }
   
  }
  
  void Get(svec<DVector> & out, double yaw, double pitch, double roll) {


    DVector a(-1, 0, 0);
    DVector b(1, 0, 0);
    DVector c(0, 2, 0);

    DMatrix3DRot rot;

    // Pitch
    rot.Set(DVector(1, 0, 0), pitch);

    a = rot * a;
    b = rot * b;
    c = rot * c;

    // Roll
    DVector r = rot.Axis(a, b, c);
    rot.Set(r, roll);

    a = rot * a;
    b = rot * b;
    c = rot * c;

    // Yaw
    DVector y = c;
    rot.Set(y, yaw);

    a = rot * a;
    b = rot * b;
    c = rot * c;
   
    out.resize(3);
    out[0] = a;
    out[1] = b;
    out[2] = c;

    //cout << "TRI1 " << a[0] << " " << a[1] << " " << a[2] << endl;
    //cout << "TRI2 " << b[0] << " " << b[1] << " " << b[2] << endl;
    //cout << "TRI3 " << c[0] << " " << c[1] << " " << c[2] << endl;
    
  }


private:
 
  
};

class Geometry3D
{
 public:
  Geometry3D() {
    m_dist = 10.;
    m_phi = 0.;
    m_theta = 0.;
    m_offset = 0;
  }

  void SetDist(double d) {
    m_dist = d;
  }

  void SetRotation(double phi, double theta) {
    m_phi = phi;
    m_theta = theta;
  }

  void SetOffset(double d) {
    m_offset = d;
  }

  // Note: the z axis is vertical by default
  DVector Coords(double xr, double yr, double zr) {
    //cout << "x=" << xr << " y=" << yr << " z=" << zr << " -> " ;
    double x = xr*cos(m_theta)*cos(m_phi) + yr*(cos(m_theta)*sin(m_phi)+sin(m_phi)*sin(m_theta)*cos(m_phi)) + zr*(sin(m_phi)*sin(m_theta) - cos(m_phi)*sin(m_theta)*cos(m_phi));

    double y = xr*cos(m_theta)*sin(m_phi) + yr*(cos(m_phi)*cos(m_theta)-sin(m_phi)*sin(m_theta)*sin(m_phi)) + zr*(sin(m_phi)*cos(m_theta)+cos(m_phi)*sin(m_theta)*sin(m_phi));

    double z = xr*sin(m_theta) - yr*sin(m_phi)*cos(m_theta) + zr*cos(m_phi)*cos(m_theta);

    //cout << "x=" << x << " y=" << y << " z=" << z << endl;

    double d = sqrt(x*x + z*z + m_dist*m_dist);
    //cout << "d=" << d << endl;
    double x1 = x*m_dist/(y+d);
    double y1 = z*m_dist/(y+d);

    //cout << "x=" << x << " y=" << y << " z=" << z << " -> " ;
    //cout << "Project x=" << x1 + m_offset << " y=" << y1 + m_offset << endl;
    return DVector(x1+m_offset, y1+m_offset, 0);
  } 

 private:
  double m_phi;
  double m_theta;
  double m_dist;
  double m_offset;

};

//===================================================================
void RecordCtrl::StartRec()
{
  if (m_run)
    return;
  m_startCount++;
  cout << "START: " << m_startCount << endl;

  m_buf.SetReserve(2048);
  m_buf.Open("");
  m_lastSize = 0;
  m_urd.DoBuffer(true);
  m_run = true;
  m_urd.StartWrite(m_fileName);

}

void RecordCtrl::StopRec()
{
  if (!m_run)
    return;

  m_stopCount++;
  cout << "STOP: " << m_stopCount << endl;

  m_urd.Stop();

  m_buf.Close();
  m_run = false;
}

void RecordCtrl::Reset()
{
  m_buf.Close();
  m_buf.Open("");
  m_b2c.Reset();
  m_buf.SetReserve(2048);
  m_lastSize = 0;
  m_isGood = true;

}


void RecordCtrl::SetDataBuffer(svec<char> & in)
{
  m_cometa = false;
  m_buf.SetReserve(2048);
  char sig = 127; //?????
  int off = 0;
  if (in.isize() == 84) {
    // Signature?
    if (in[0] == sig && in[1] == sig && in[2] == sig && in[3] == sig) {
      m_cometa = true;
      m_buf.SetReserve(0); // Get stuff in real-time
      off = 4;
    }
  }

  //m_buf.Set(const char * pBuffer, int sizeInBytes); // Clear the buffer...??
  //cout << "Append" << endl;
  // Take header off
  m_buf.Append(&in[off], in.isize()-off);

  
  //cout << "Done" << endl;
  //m_b2c.Read(m_buf);
}

int RecordCtrl::GetData(svec<double> & d, int size)
{
  if (m_run) { // Not sure this is needed?
    //cout << "Copy??" << endl;
    m_urd.CopyBuffer(m_buf);
  }

  int rr = 0;

  if (!m_cometa)
    rr = m_b2c.Read(m_buf);
  else
    rr = m_b2c.ReadCometa(m_buf);

  //==============================================
  m_buf.Compact();
  //==============================================
  
  if (rr == 1)
    m_isGood = true;
  if (rr == 0)
    m_isGood = false;
  
  int i;

  
  //cout << "Getting data" << endl;
  const AllData & data = m_b2c.GetData();
  //cout << "done" << endl;
  // Min data length
  int n = data.isizeEMG()/5;
  //cout << "nE=" << n << endl;
  if (data.isizeAcc() < n)
    n = data.isizeAcc();
  //cout << "nA=" << n << endl;
  if (data.isizeGyr() < n)
    n = data.isizeGyr();
  //cout << "nG=" << n << endl;

  int plus = n - m_lastSize;

  d.resize(plus * size, 0.);
  //cout << "new=" << plus <<  " size=" << size << " array=" << d.isize() << endl;
  //cout << "Last size: " << m_lastSize << " n=" << n << endl;

  double rad = 3.1415926/180.;
  
  int k = 0;
  int sn = 20;
  for (i=m_lastSize; i<n; i++) {
    //cout << i << " <- emg" << endl;

    //cout << k*size << endl;
    
    d[k*size] = i; // Frame!!!!!!!!!!
    d[k*size + 1] = data.GetEMG(0)[i*5];
    
    d[k*size + 2] = data.GetEMG(1)[i*5];

    // Smooth EMG
    if (i>=sn) {
      d[k*size + 2] = 0.;
      for (int j=0; j<sn; j++)
	d[k*size + 2] += data.GetEMG(1)[i*5-j]/(double)sn;

    }
    
    //cout << k << " <- acc" << endl;
    d[k*size + 3] = data.GetAcc(0)[i];
    d[k*size + 4] = data.GetAcc(1)[i];
    d[k*size + 5] = data.GetAcc(2)[i];

    //cout << k*size + 6 << " <- gyr" << endl;
    d[k*size + 6] = data.GetGyr(0)[i];
    //cout << k*size + 7 << " <- gyr" << endl;
    d[k*size + 7] = data.GetGyr(1)[i];
    //cout << k*size + 8 << " <- gyr" << endl;
    d[k*size + 8] = data.GetGyr(2)[i];

    if (size == 15) {
      Triangle tr;
      svec<DVector> t;
      //double fac = 1.5;

      // Add in accel here!!!
      tr.Get(t, data.GetGyr(0)[i]*rad, data.GetGyr(1)[i]*rad, data.GetGyr(2)[i]*rad);
      tr.Move(t, data.GetAcc(0)[i], data.GetAcc(1)[i], data.GetAcc(2)[i]);
	
      Geometry3D g3d;
      DVector t1 = g3d.Coords(t[0][0], t[0][2], t[0][1]); 
      DVector t2 = g3d.Coords(t[1][0], t[1][2], t[1][1]); 
      DVector t3 = g3d.Coords(t[2][0], t[2][2], t[2][1]); 

      d[k*size + 9]  = t1[0];
      d[k*size + 10] = t1[1];
      
      d[k*size + 11] = t2[0];
      d[k*size + 12] = t2[1];

      d[k*size + 13] = t3[0];
      d[k*size + 14] = t3[1];
    }
    
    k++;

    //cout << i << "\t";
    //cout << data.GetEMG(0)[i*5] << "\t" << data.GetEMG(1)[i*5] << "\t";
    //cout << data.GetAcc(0)[i] << "\t" << data.GetAcc(1)[i] << "\t" << data.GetAcc(2)[i] << "\t";
    //cout << data.GetGyr(0)[i] << "\t" << data.GetGyr(1)[i] << "\t" << data.GetGyr(2)[i];
    //cout << endl;
  }
  //cout << "DONE" << endl;
  m_lastSize = n;

  return plus;
}

void RecordCtrl::WriteCSV(const string & fileName)
{
  m_b2c.Write(fileName);
}


