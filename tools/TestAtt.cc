#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include "extern/attitude_estimator/attitude_estimator.h"
#include <math.h>
#include "math/Rotation.h"


void get_sensor_data(double * g, double * a, double * m, const svec< svec < double > > & acc, const svec< svec < double > > & gyr, int i)
{
  //cout << "DATA " << gyr[0][i]/180.*M_PI << " " << gyr[1][i]/180.*M_PI << " " << gyr[2][i]/180.*M_PI << " -> " << acc[0][i] << " " << acc[1][i] << " " << acc[2][i] << endl;
  
  g[0] = gyr[0][i]/180.*M_PI;
  g[1] = gyr[1][i]/180.*M_PI;
  g[2] = gyr[2][i]/180.*M_PI;
  //g[0] = 0.;
  //g[1] = 0.;
  //g[2] = 0.;

  a[0] = acc[0][i];
  a[1] = acc[1][i];
  a[2] = acc[2][i];

  a[0] = 0.;
  a[1] = 0.;
  a[2] = 0.;
  
  m[0] = 0.;
  m[1] = 0.;
  m[2] = 0.;
  
}


class Triangle
{
public:
  Triangle() {
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

    cout << "TRI1 " << a[0] << " " << a[1] << " " << a[2] << endl;
    cout << "TRI2 " << b[0] << " " << b[1] << " " << b[2] << endl;
    cout << "TRI3 " << c[0] << " " << c[1] << " " << c[2] << endl;
    
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



int main( int argc, char** argv )
{

  /*Triangle tr;
  svec<DVector> t;
  tr.Get(t, 0, 0, 0.7);
  tr.Get(t, 0, 0.7, 0);
  tr.Get(t, 0.7, 0, 0);
  cout << "-----------" << endl;
  tr.Get(t, 0, 0.7, 0.2);
  return 0;
  */


  DVector pr;
  pr[0] = -1;
  pr[1] = 1;
  pr[2] = -1;

  DVector oo;

  Geometry3D g3d;
  oo = g3d.Coords(pr[0], pr[2], pr[1]); // ????????????????

  cout << "IN:  " << pr[0] << "\t" << pr[1] << "\t" << pr[2] << endl;
  cout << "OUT: " << oo[0] << "\t" << oo[1] << endl;

  return 0;


  
  commandArg<string> fileCmmd("-i","input file");
  commandLineParser P(argc,argv);
  P.SetDescription("Testing the file parser.");
  P.registerArg(fileCmmd);
 
  P.parse();
  
  string fileName = P.GetStringValueFor(fileCmmd);
 

  //comment. ???
  FlatFileParser parser;
  
  parser.Open(fileName);

  svec< svec <double > > acc, gyr;

  acc.resize(3);
  gyr.resize(3);

  int what = 0;

  double timeDelta = 0.01;
  
  while (parser.ParseLine()) {
    if (parser.GetItemCount() == 0)
      continue;    
    if (parser.AsString(0) == "ACCEL") {
      what = 1;
      parser.ParseLine();
      parser.ParseLine();      
    }
    if (parser.AsString(0) == "GYRO") {
      what = 2;
      parser.ParseLine();
      parser.ParseLine();      
    }
    if (parser.AsString(0) == "SPECTRA") {
      break;
    }
    
    if (what == 1) {
      acc[0].push_back(parser.AsFloat(1));
      acc[1].push_back(parser.AsFloat(2));
      acc[2].push_back(parser.AsFloat(3));
    }
    if (what == 2) {
      gyr[0].push_back(parser.AsFloat(1));
      gyr[1].push_back(parser.AsFloat(2));
      gyr[2].push_back(parser.AsFloat(3));
    }

    
  }

  cout << "Data loaded, acc=" << acc[0].isize() << " gyr=" << gyr[0].isize() << endl;

  int i;

  svec<double> avg;
  avg.resize(3, 0);
  
  for (i=0; i<acc[0].isize(); i++) {
    avg[0] += acc[0][i];
    avg[1] += acc[1][i];
    avg[2] += acc[2][i];
  }

  avg[0] /= (double)acc[0].isize();
  avg[1] /= (double)acc[1].isize();
  avg[2] /= (double)acc[2].isize();
  
  cout << "AVERAGE " << avg[0] << " " << avg[1] << " " << avg[2] << endl << endl;
  
  stateestimation::AttitudeEstimator Est;
	
  // Initialise the estimator (e.g. in the class constructor, none of these are actually strictly required for the estimator to work, and can be set at any time)
  Est.setMagCalib(0.68, -1.32, 0.0);         // Recommended: Use if you want absolute yaw information as opposed to just relative yaw (Default: (1.0, 0.0, 0.0))
  // Est.setMagCalib(1.0, 0.0, 0.0);         // Recommended: Use if you want absolute yaw information as opposed to just relative yaw (Default: (1.0, 0.0, 0.0))
  Est.setPIGains(2.2, 2.65, 10, 1.25);       // Recommended: Use if the default gains (shown) do not provide optimal estimator performance (Note: Ki = Kp/Ti)
  Est.setQLTime(2.5);                        // Optional: Use if the default quick learning time is too fast or too slow for your application (Default: 3.0)
  Est.setAttitude(0.5, 0.5, 0.5, 0.5);       // Optional: Use if you have prior knowledge about the orientation of the robot (Default: Identity orientation)
  Est.setAttitudeEuler(M_PI, 0.0, 0.0);      // Optional: Use if you have prior knowledge about the orientation of the robot (Default: Identity orientation)
  Est.setAttitudeFused(M_PI, 0.0, 0.0, 1.0); // Optional: Use if you have prior knowledge about the orientation of the robot (Default: Identity orientation)
  Est.setGyroBias(0.152, 0.041, -0.079);     // Optional: Use if you have prior knowledge about the gyroscope bias (Default: (0.0, 0.0, 0.0))
  Est.setAccMethod(Est.ME_FUSED_YAW);        // Optional: Use if you wish to experiment with varying acc-only resolution methods
  //Est.setAccMethod(Est.ME_ZYX_YAW);        // Optional: Use if you wish to experiment with varying acc-only resolution methods
       
  // Main loop

  
  
  for (i=0; i<acc[0].isize(); i++) {
    //for (i=2000; i<12000; i++) {
      
      double g[3], a[3], m[3];
      get_sensor_data(g, a, m, acc, gyr, i);
      Est.update(timeDelta, g[0], g[1], g[2], a[0], a[1], a[2], m[0], m[1], m[2]);
      
      double q[4];
      Est.getAttitude(q);
      cout << "****** Frame " << i << endl;
      cout << "My attitude is (quaternion): (" << q[0] << "," << q[1] << "," << q[2] << "," << q[3] << ")" << endl;
      cout << "My attitude is (ZYX Euler): (" << Est.eulerYaw() << "," << Est.eulerPitch() << "," << Est.eulerRoll() << ")" << endl;
      cout << "My attitude is (fused): (" << Est.fusedYaw() << "," << Est.fusedPitch() << "," << Est.fusedRoll() << "," << (Est.fusedHemi() ? 1 : -1) << ")" << endl;

      if (Est.eulerYaw() > 1.)
	cout << "YAW   " << "1." << endl;
      else
	cout << "YAW   " << Est.eulerYaw() << endl;
      cout << "PITCH " << Est.eulerPitch() << endl;
      cout << "ROLL  " << Est.eulerRoll() << endl;

      cout << "G0   " << g[0] << endl;
      cout << "G1   " << g[1] << endl;
      cout << "G2   " << g[2] << endl;
      
      cout << "Q0 " << q[0] << endl;
      cout << "Q1 " << q[1] << endl;
      cout << "Q2 " << q[2] << endl;
      cout << "Q3 " << q[3] << endl;

      Triangle tr;
      svec<DVector> t;
      double fac = 1.5;
      tr.Get(t, Est.eulerYaw()*fac, Est.eulerPitch()*fac, Est.eulerRoll()*fac);
	

      
      /*
	if(robot_moved_manually_on_field())
	{
	Est.reset(true, false);                 // Reset into quick learning mode, but preserve the current gyro bias estimate
	Est.setAttitudeEuler(M_PI_2, 0.0, 0.0); // Optional: Use if you have prior knowledge about the new orientation of the robot
	}
      */
  }

  return 0;
}
