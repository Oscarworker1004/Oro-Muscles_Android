#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include "eeg/BTLData.h"
#include <math.h>
#include "eeg/IMUTrans.h"


double Abs(double d)
{
  if (d < 0)
    return -d;
  return d;
}

int main( int argc, char** argv )
{

  commandArg<string> fileCmmd("-i","input file");
  commandLineParser P(argc,argv);
  P.SetDescription("Testing the file parser.");
  P.registerArg(fileCmmd);
 
  P.parse();
  
  string fileName = P.GetStringValueFor(fileCmmd);
 
  BTLData data;
  data.Read(fileName);

  int i, j;

  svec<double> d;
  d.resize(3, 0.);
  svec<double> a;
  a.resize(3, 0.);

  svec<double> ini;
  ini.resize(3, 0.);

  ini[0] = data.Acc()[0][0];
  ini[1] = data.Acc()[0][1];
  ini[2] = data.Acc()[0][2];
  
  double m;
  double last = sqrt(ini[0]*ini[0]+ini[1]*ini[1]+ini[2]*ini[2]);
  double mean = 0.;


  IMUTrans t;
  
  for (i=0; i<data.Gyro().isize(); i++) {
    //m = data.EXG()[i*5].Signal()/5;
    for (j=0; j<3; j++) {
      //d[j] = data.Acc()[i][j];
      d[j] = data.Gyro()[i][j]/100;
      if (d[j] > 180)
	d[j] = -180;
      if (d[j] < -180)
	d[j] = 180;
      //a[j] = data.Acc()[i][j];
    }

    /*
    cout << sqrt(d[0]*d[0] + d[1]*d[1] + d[2]*d[2]) << " ";
    last += sqrt(a[0]*a[0] + a[1]*a[1] + a[2]*a[2]) - 90;
   
    cout << last;
    cout << " " << m << endl;
    */

    
    
    double alpha = d[2]*3.14/2/100;
    double beta =  d[0]*3.14/2/100;
    double gamma = d[1]*3.14/2/100;
    
    
    
    double x = data.Acc()[i][0];
    double y = data.Acc()[i][1];
    double z = data.Acc()[i][2];          
    
    /*    double r = sqrt(x*x+y*y);
    double phi = atan2(x, y);
    phi -= gamma;
    x = r*sin(phi);
    y = r*cos(phi);
    
    r = sqrt(x*x+z*z);
    phi = atan2(x, z);
    phi -= beta;
    x = r*sin(phi);
    z = r*cos(phi);
    
    
    r = sqrt(y*y+z*z);
    phi = atan2(y, z);
    phi -= gamma;
    y = r*sin(phi);
    z = r*cos(phi);   
    
    
    a[0] += x/100. - ini[0]/100;
    a[1] += y/100. - ini[1]/100;
    a[2] += z/100. - ini[2]/100;
    */
    
    a[0] = x;// - ini[0];
    a[1] = y;// - ini[1];
    a[2] = z;// - ini[2];

    double all = sqrt(a[0]*a[0] + a[1]*a[1] + a[2]*a[2]);

    
    //cout << last - all << " " << sqrt(alpha*alpha + beta*beta + gamma*gamma) << endl;

    last = all;
    
    mean += all;

    t.AddGyro(data.Gyro()[i][0], data.Gyro()[i][1], data.Gyro()[i][2]);
    t.AddAcc(data.Acc()[i][0], data.Acc()[i][1], data.Acc()[i][2]);

    
    //cout << d[0] << " " << d[1] << " " << d[2] << endl;
    //cout << a[0] << " " << a[1] << " " << a[2] << endl;
  }

  t.Process();
  
  for (i=0; i<t.Gyro().isize(); i++)
    cout << t.Gyro()[i] << " " << t.Acc()[i] << endl;

  
  cerr << "Mean: " << mean/data.Gyro().isize() << endl;
  return 0;
}
