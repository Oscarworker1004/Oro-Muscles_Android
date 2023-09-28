#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include "util/mutil.h"
#include "base/StringUtil.h"
#include "signal/Bin2CSV.h"

/*
double Bytes2Float(unsigned char a, unsigned char b, unsigned char c)
{
  int word = (c << 16) + (b << 8) + a;
  // mask variable for 2's complement
  int mask = 0x7fffff;

  if (word > mask)
    word |= 0xff000000;

  //cout << (int)a << " " << (int)b << " " << (int)c << " -> " << word << endl;
  double d = (double)word;

  double exg_bits = (double)mask;
  double v_ref = 2.5;
  double afe_gain = 9.986;
  double conversion = ((1.0 / exg_bits) * v_ref) / afe_gain * 1000000.0;

  d *= conversion;

  return d;
   
}

double AccelNorm(short in)
{
  double acc_bits = 2048.;
  double acc_scale = 2;
  double acc_g_to_mg = 1000;
  double conversion = (acc_scale * acc_g_to_mg) / acc_bits;

  return conversion/1000.*(double)in;
}

double GyroNorm(short in)
{
  double gyro_bits = 32768;
  double gyro_scale = 2000;
  double gyro_dps_to_mdps = 1000;
  double conversion = gyro_dps_to_mdps / (gyro_bits / gyro_scale);

  return conversion/1000.*(double)in;
}



string PrintByte(int n) {
  string ret;

  ret += Stringify(n >> 24);
  ret += " ";
  ret += Stringify((n & 0xff0000) >> 16);
  ret += " ";
  ret += Stringify((n & 0x00ff00) >> 8);
  ret += " ";
  ret += Stringify(n & 0x0000ff);
  ret += " ";
  
  
  return ret;
}
*/

int main( int argc, char** argv )
{

  
  commandArg<string> fileCmmd("-i","input file (bin dump)");
  commandArg<string> outCmmd("-o","output file (CSV)");
  commandLineParser P(argc,argv);
  P.SetDescription("Convert bin dump to csv.");
  P.registerArg(fileCmmd);
  P.registerArg(outCmmd);
 
  P.parse();
  
  string fileName = P.GetStringValueFor(fileCmmd);
  string outName = P.GetStringValueFor(outCmmd);

  Bin2CSV b2c;

  b2c.Convert(outName, fileName);
  
    
  /*
  int i, j;
  int thing = 98 * 3;

  //====================================================================
  BWHighPassFilter filter(4, 500., 50.);
  RMSStream rms;
    
  CMReadFileStream s;

  s.Open(fileName.c_str());

  signed char c;
  int h;
  short source = 0;
 
  int k = 0;

  short num;
  short size;
  unsigned short frame;
  
  bool good = true;
  while (good) {
    s.Read(num);
    good = s.Read(source);
    cout << "?? " << PrintByte(source) << endl;
    if (!good)
      break;
    
    cout << "Found BLOCK: " << source/256 << " " << (source & 256) << " ... " << num << endl;
 
    if (source == 256 + 4) { // Accel
      cout << "Accel: " << source << " -> " << num << endl;

      s.Read(size);
      cout << "size:  " << size << endl;
      s.Read(frame);
      cout << "frame: " << frame << endl;
      
      
      for (i=0; i<(size-2)/6; i++) {
	s.Read(num);
	cout << AccelNorm(num) << "\t";
	s.Read(num);
	cout << AccelNorm(num) << "\t";
	s.Read(num);
	cout << AccelNorm(num) << endl;
      }
      continue;
    }

    if (source == 256 + 6) { // Gyro
      cout << "Gyro:  " << source << " -> " << num << endl;
      s.Read(size);
      cout << "size:  " << size << endl;
      s.Read(frame);
      cout << "frame: " << frame << endl;
      
      
      for (i=0; i<(size-2)/6; i++) {
	s.Read(num);
	cout << GyroNorm(num) << "\t";
	s.Read(num);
	cout << GyroNorm(num) << "\t";
	s.Read(num);
	cout << GyroNorm(num) << endl;
      }
      continue;
    }


    
   if (source == 3) { //     
     cout << "EXG:   " << source << " -> " << num << endl;
     s.Read(size);
     cout << "size:  " << size << endl;
     s.Read(frame);
     cout << "frame: " << frame << endl;
     
     for (i=0; i<(size-2)/3; i++) {
       
       unsigned char ua, ub, uc;    
       s.Read(ua);
       s.Read(ub);
       s.Read(uc);
       double input = Bytes2Float(ua, ub, uc);
       double ff = filter.Apply(input);
       double r = rms.Apply(ff);
       cout << "x = " << input << " " << ff << " rms: " << r << endl;
     }
     continue;
   }

   }*/

  /*
  self.decimation_correction_filter()
    self.highpass_filter()
  */

  
  //s.Close();
  
  return 0;
}
