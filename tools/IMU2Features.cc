#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include "signal/IMU.h"



int main( int argc, char** argv )
{

  commandArg<string> fileCmmd("-i","input file");
  commandArg<string> outCmmd("-o","output file");
  commandArg<int> cCmmd("-c","compress", 1);
  commandLineParser P(argc,argv);
  P.SetDescription("Process IMU data.");
  P.registerArg(fileCmmd);
  P.registerArg(outCmmd);
 
 
  P.parse();
  
  string fileName = P.GetStringValueFor(fileCmmd);
  string outName = P.GetStringValueFor(outCmmd);
  int compress = P.GetIntValueFor(cCmmd);

 
  int i, j;

  IMUStream gyro, acc;


  FILE * p = fopen(outName.c_str(), "w");

  gyro.SetCompWin(compress, 0);
  acc.SetCompWin(compress, 0);
  
  gyro.Read(fileName, "GYRO");
  acc.Read(fileName, "ACCEL");

  gyro *= 3.1415/180;

  //gyro.Derivative();

  gyro *= 10000;
  acc *= 10000;

  
  int n = gyro.isize();
  if (acc.isize() < n)
    n = acc.isize();

  for (i=0; i<n; i++) {
    fprintf(p, "[%f] ", acc.Time(i)); 
    fprintf(p, "%f ", acc[i][0]); 
    fprintf(p, "%f ", acc[i][1]); 
    fprintf(p, "%f ", acc[i][2]);
    
    fprintf(p, "%f ", gyro[i][0]); 
    fprintf(p, "%f ", gyro[i][1]); 
    fprintf(p, "%f ", gyro[i][2]);

    fprintf(p, "\n");
  }
  
  fclose(p);
  
  
  
  return 0;
}
