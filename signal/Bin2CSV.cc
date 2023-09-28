#include "signal/Bin2CSV.h"
#include "base/StringUtil.h"
#include <math.h>
#include "signal/Filters.h"
#include "util/mutil.h"



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
  
  return conversion/1000.*(double)in; // Last factor???
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

void AllData::Sync2EMG(int emgSamp, int imuSamp)
{
  int fac = (emgSamp / imuSamp);
  int emg = m_emg[0].isize() / fac;
  int acc = m_acc[0].isize();
  int gyr = m_gyr[0].isize();

  cout << "=============================================" << endl;
  cout << "emg: " << emg << " acc: " << acc << " gyr: " << gyr << endl;
  
  int n = emg;
  if (acc < n)
    n = acc;
  if (gyr < n)
    n = gyr;

  cout << "REDUCE TO: " << n << endl;
  
  int i;

  for (i=0; i<m_emg.isize(); i++)
    m_emg[i].resize(n * fac, 0.);
  
  for (i=0; i<m_acc.isize(); i++)
    m_acc[i].resize(n, 0.);

  for (i=0; i<m_gyr.isize(); i++)
    m_gyr[i].resize(n, 0.);

}

int FixFrame(int last, int frame) {
  if (frame >= last) {
    return frame;
  }
 
  int n = last/0x10000;

  int y1 = frame + n*0x10000;
  int y2 = y1 + 0x10000;
  
  if (y2-last < last-y1)
    return y2;
  else
    return y1;
}

void AllData::AddEMG(int frame, double emg, double rms)
{
  if (m_emgFrame.isize() > 0) {
    int last = m_emgFrame[m_emgFrame.isize()-1];
    frame = FixFrame(last, frame);
    
    if (frame < last)
      return;
    if (frame > last + 10000)
      return;
    
    double lastEMG = m_emg[0][m_emgFrame.isize()-1];
    double lastRMS = m_emg[1][m_emgFrame.isize()-1];
    for (int i=last+1; i<frame; i++) {
      for (int j=0; j<m_emgPerFrame; j++) {
	m_emgFrame.push_back(i);
	m_emg[0].push_back(lastEMG);
	m_emg[1].push_back(lastRMS);

      }
    }
  }

  m_emgFrame.push_back(frame);
  m_emg[0].push_back(emg);
  m_emg[1].push_back(rms);
}


void AllData::AddAcc(int frame, double x, double y, double z)
{
  if (m_accFrame.isize() > 0) {
    int last = m_accFrame[m_accFrame.isize()-1];

    frame = FixFrame(last, frame);

    if (frame < last)
      return;
    if (frame > last + 10000)
      return;
    double lastX = m_acc[0][m_accFrame.isize()-1];
    double lastY = m_acc[1][m_accFrame.isize()-1];
    double lastZ = m_acc[2][m_accFrame.isize()-1];
    
    for (int i=last+1; i<frame; i++) {
      for (int j=0; j<m_imuPerFrame; j++) {
	m_accFrame.push_back(i);
	m_acc[0].push_back(lastX);
	m_acc[1].push_back(lastY);
	m_acc[2].push_back(lastZ);
      }
    }
  }

  m_accFrame.push_back(frame);
  m_acc[0].push_back(x);
  m_acc[1].push_back(y);
  m_acc[2].push_back(z);
 
}

void AllData::AddGyr(int frame, double x, double y, double z)
{

  if (m_gyrFrame.isize() > 0) {
    int last = m_gyrFrame[m_gyrFrame.isize()-1];

    frame = FixFrame(last, frame);

    if (frame < last)
      return;
    if (frame > last + 10000)
      return;
    double lastX = m_gyr[0][m_gyrFrame.isize()-1];
    double lastY = m_gyr[1][m_gyrFrame.isize()-1];
    double lastZ = m_gyr[2][m_gyrFrame.isize()-1];
    
    for (int i=last+1; i<frame; i++) {
      for (int j=0; j<m_imuPerFrame; j++) {
	m_gyrFrame.push_back(i);
	m_gyr[0].push_back(lastX);
	m_gyr[1].push_back(lastY);
	m_gyr[2].push_back(lastZ);
      }
    }
  }

  m_gyrFrame.push_back(frame);
  m_gyr[0].push_back(x);
  m_gyr[1].push_back(y);
  m_gyr[2].push_back(z);

}


void Bin2CSV::ReadBin(const string & fileIn)
{
  string fileName = fileIn;
  CMReadFileStream s;

  cout << "Read " << fileName.c_str() << endl;
  s.Open(fileName.c_str());
  //cout << "Yess." << endl;


  //ReadCometa(s);
  //return;
  
  
  // CLEAN THIS UP!!!!
  bool bCometa = true;
  char test;

  for (int i=0; i<4; i++) {
    s.Read(test);
    if (test != 127)
      bCometa = false;
  }

  if (bCometa) {
    cout << "Read COMETA " << fileName.c_str() << endl;
    ReadCometa(s);
  } else {
 
    s.Close();
    cout << "Read SENCURE " << fileName.c_str() << endl;
    s.Open(fileName.c_str());
    Read(s);
  }
  
  s.Close();

}

short FindNextSignature(IMReadStream & s, short & size) {
  unsigned char a = 0;
  unsigned char b = 0;

  size = 0;

  s.Read(b);

  short tmp;
  
  while (s.Read(a)) {
    if (a == 1 && b == 4) {
      s.Read(b);
      s.Read(a);
      if (b == 62) {
	size = 62;
	return 256 + 4;
      }
    }
    if (a == 1 && b == 6) {
      s.Read(b);
      s.Read(a);
      if (b == 62) {
	size = 62;
	return 256 + 6;
      }
    }
    if (a == 0 && b == 3) {
      s.Read(b);
      s.Read(a);
      if (b == 152) {
	size = 152;
	return 3;
      }
    }

    b = a;
  }

  return -1;
}

double muValue(unsigned short in) {

  double u = ((double)in-32768)*2*3300/(double)0xffff;
  return u;
}

double accValue(unsigned short in) {

  double u = ((double)in-32768)*0.00003051850947599*8;
  return u;
}

double gyrValue(unsigned short in) {

  double u = ((double)in-32768)*0.00003051850947599*1000;
  return u;
}

int Bin2CSV::ReadCometa(IMReadStream & in)
{
  int k = 0;
  //cout << "Cometa bin read" << endl;
  while (!in.IsEnd()) {

  //while (k == 0) {


  //cout << "------ BLOCK --------" << endl;
    //int signature;
    //in.Read(signature);
    //cout << k << endl;
    
    unsigned int pack;
   
    unsigned char r;
    in.Read(r);
    //cout << (int)r << " ";
    pack = (unsigned int)r;
    in.Read(r);
    //cout << (int)r << " ";
    pack += 256*(unsigned int)r;
    in.Read(r);
    //cout << (int)r << " ";
    pack += 256*256*(unsigned int)r;
    //cout << endl;

    in.Read(r);
    
    
    //in.Read(pack);
    
    int batt = r;

    int frame = (int)pack;

    cout << pack << "\t" << batt << endl;

    // 1000 Hz -> 500 Hz
    for (int i=0; i<10; i++) {
      unsigned short one;
    
      //in.Read(one);
      unsigned char c;
      in.Read(c);
      one = c*256;
      in.Read(c);
      one += c;
      
      double out1 = muValue(one);

      // Sub-samples
      in.Read(c);
      one = c*256;
      in.Read(c);
      one += c;
      double out2 = muValue(one);

      double ff = (out1 + out2)/2;
      ff /= 5.; //MGG Proper SCALING here

      double rms = m_rms.Apply(ff);
      m_data.AddEMG(frame, ff, rms);

      
      //cout << "muValue: " << out << endl;
    }
    
    for (int i=0; i<2; i++) {

      unsigned short one;
    
      unsigned char c;


      in.Read(c);
      one = c*256;
      in.Read(c);
      one += c;
      double xx = accValue(one);

      in.Read(c);
      one = c*256;
      in.Read(c);
      one += c;
      double yy = accValue(one);

      in.Read(c);
      one = c*256;
      in.Read(c);
      one += c;
      double zz = accValue(one);
     
      m_data.AddAcc(frame, xx, yy, zz);

    }
    
    for (int i=0; i<2; i++) {
      unsigned short one;
   
      unsigned char c;
      in.Read(c);
      one = c*256;
      in.Read(c);
      one += c;
      double xx = gyrValue(one);

      in.Read(c);
      one = c*256;
      in.Read(c);
      one += c;
      double yy = gyrValue(one);

      in.Read(c);
      one = c*256;
      in.Read(c);
      one += c;
      double zz = gyrValue(one);

      m_attEst.update(0.01, xx/180*M_PI, yy/180*M_PI, zz/180*M_PI, 0., 0., 0., 0., 0., 0.);
	
      xx = m_attEst.eulerYaw()/M_PI*180;
      yy = m_attEst.eulerPitch()/M_PI*180;
      zz = m_attEst.eulerRoll()/M_PI*180;

      m_data.AddGyr(frame, xx, yy, zz);
	
      
    }
    for (int i=0; i<6; i++) {
      unsigned short one;
    
      //in.Read(one);
      unsigned char c;
      in.Read(c);
      in.Read(c);
   }
    
    k++;
  }

  return 1;

}

int Bin2CSV::Read(IMReadStream & s)
{

  


  int i, j;
  int thing = 98 * 3;

  //====================================================================
    
  

  signed char c;
  int h;
  short source = 0;
 
  int k = 0;

  short num;
  short size;
  unsigned short frame;
  
  bool good = true;

  bool extraPadding = false;
  bool quiet = true;
  bool skip = true;

  bool desparate = true;

  int ret = 1;
  
  while (good) {

    //cout << "Try" << endl;
    
    if (s.IsEnd()) // quit if we're off the end.
      break;

    //cout << "Got" << endl;
    
    short x, y, z;
    if (extraPadding)
      s.Read(num);

    //unsigned char sa, sb; 

    if (skip && desparate) {
      source = FindNextSignature(s, size);
      if (source < 0)
	good = false;
      else
	good = true;
      //desparate = false;
    } else {
      good = s.Read(source);
    }


    //cout << "?? " << PrintByte(source) << endl;
    if (!good)
      break;

    //ret = 1;

    //cout << "Bulk" << endl;
 
    //cout << "Found BLOCK: " << source/256 << " " << (source & 256) << " ... " << num << endl;
    bool bIsData = false;
    
    if (source == 256 + 4) { // Accel
      bIsData = true;

      //s.Read(size);
      //cout << "size:  " << size << endl;
      s.Read(frame);
      //cout << "frame: " << frame << endl;
      if (!quiet)
	cout << "Accel: " << source << " -> size " << size << " frame " << frame << endl;

      if (skip && size != 62) {
	cout << "ERROR in Accel **************************************" << endl;
	cout << size % 256 << endl;
	cout << size / 256 << endl;
	cout << frame % 256 << endl;
	cout << frame / 256 << endl;

	
	unsigned char ccc;
	
	/*
	while (s.Read(ccc)) {
	  cout << (unsigned int)ccc << endl;
	  }*/
      
	if (!quiet) {
	  cout << "Accel - emergency skip!" << endl;
	}
	ret = 0;
	desparate = true;
	continue;
      }
      
      for (i=0; i<(size-2)/6; i++) {
	s.Read(x);	  
	//cout << AccelNorm(x) << "\t";
	s.Read(y);
	//cout << AccelNorm(y) << "\t";
	s.Read(z);
	//cout << AccelNorm(z) << endl;

	//cerr << "ADD ACCEL (before): " << x << " " << y << " " << z << endl; 

	double xx = AccelNorm(x);
	double yy = AccelNorm(y);
	double zz = AccelNorm(z);
	//cerr << "ADD ACCEL (after):  " << x << " " << y << " " << z << endl; 

	//cout << "Before" << endl;
	m_accBuf[0] = m_accBuf[0]*(1.-m_accWeight) + xx*m_accWeight;
	m_accBuf[1] = m_accBuf[1]*(1.-m_accWeight) + yy*m_accWeight;
	m_accBuf[2] = m_accBuf[2]*(1.-m_accWeight) + zz*m_accWeight;

	xx = m_accBuf[0];
	yy = m_accBuf[1];
	zz = m_accBuf[2];
	
	m_data.AddAcc(frame, xx, yy, zz);
	//cout << "After" << endl;
      }

      
      continue;
    }

    if (source == 256 + 6) { // Gyro
      bIsData = true;
      //cout << "Gyro:  " << source << " -> " << num << endl;
      //s.Read(size);
      //cout << "size:  " << size << endl;
      s.Read(frame);
      //cout << "frame: " << frame << endl;

      if (!quiet)
	cout << "Gyro: " << source << " -> size " << size << " frame " << frame << endl;

      if (skip && size != 62) {
	cout << "ERROR in Gyro **************************************" << endl;
	cout << size % 256 << endl;
	cout << size / 256 << endl;
	cout << frame % 256 << endl;
	cout << frame / 256 << endl;

	unsigned char ccc;

	/*while (s.Read(ccc)) {
	  cout << (unsigned int)ccc << endl;
	  }*/
      
	if (!quiet) {
	  cout << "Gyro - emergency skip!" << endl;
	}
	ret = 0;
	desparate = true;
	continue;
      } else {
      }
   
      for (i=0; i<(size-2)/6; i++) {
	s.Read(x);
	//cout << GyroNorm(x) << "\t";
	s.Read(y);
	//cout << GyroNorm(y) << "\t";
	s.Read(z);
	//cout << GyroNorm(z) << endl;

	double xx = GyroNorm(x);
	double yy = GyroNorm(y);
	double zz = GyroNorm(z);

	//=============================================================
	// This is NOT Ideal!!
	m_attEst.update(0.01, xx/180*M_PI, yy/180*M_PI, zz/180*M_PI, 0., 0., 0., 0., 0., 0.);
	
	xx = m_attEst.eulerYaw()/M_PI*180;
	yy = m_attEst.eulerPitch()/M_PI*180;
	zz = m_attEst.eulerRoll()/M_PI*180;

	
	//=============================================================

	m_data.AddGyr(frame, xx, yy, zz);
      }
      continue;
    }


    
   if (source == 3) { //     
      bIsData = true;
     //cout << "EXG:   " << source << " -> " << num << endl;
     //s.Read(size);
     //cout << "size:  " << size << endl;
     s.Read(frame);

     //cout << "frame: " << frame << endl;

     if (!quiet)
       cout << "Exg: " << source << " -> size " << size << " frame " << frame << endl;
     
     if (skip && size != 152) {
       cout << "ERROR in EXG **************************************" << endl;
       cout << size % 256 << endl;
       cout << size / 256 << endl;
       cout << frame % 256 << endl;
       cout << frame / 256 << endl;
       if (!quiet) {
	 cout << "Exg - emergency skip!" << endl;
       }
       ret = 0;
       desparate = true;
       continue;
     } else {
    
     }
    
     for (i=0; i<(size-2)/3; i++) {
       
       unsigned char ua, ub, uc;    
       s.Read(ua);
       s.Read(ub);
       s.Read(uc);
       double input = Bytes2Float(ua, ub, uc);
       double ff = m_filter.Apply(input);
       if (badEMG(ff)) {
	 if (!quiet)
	   cout << "Kill signal " << ff << endl;
	 ff = 0.;
       } else {
	 m_lastFF = ff;
       }
       double r = m_rms.Apply(ff);
       if (!quiet) {
	 //cout << "Frame " << i << " x = " << input << " " << ff << " rms: " << r;
	 //cout << " " << (int)ua << " " << (int)ub << " " << (int)uc << endl;
       }

       //***********************************************
       if (size < 4*152) {
	 m_data.AddEMG(frame, ff, r);
       } else {
	 //cout << "SKIP, size=" << size << endl;
       }
       
       //cout << "SCREAM: " << endl;
       //cout << "SCREAM: " << m_data.GetEMG(0).isize() << endl;
     }
     continue;
   }
   if (!quiet && !bIsData) {
     unsigned short ss = (unsigned short)source;
     cout << "SKIP " << source << " -> " << ss%256 << " " << ss/256 << endl;
   }

  }

  /*
  self.decimation_correction_filter()
    self.highpass_filter()
  */

  

  return ret;
}

void Bin2CSV::Process()
{
  m_data.Sync2EMG(m_sampEMG, m_sampIMU);
}
  
void Bin2CSV::Write(const string & fileName)
{
  int i, j;
  
  FILE * p = fopen(fileName.c_str(), "w");

  int stamp = 0;
  fprintf(p, "EXG Data\n");
  fprintf(p, "X,Y,RMS,MDF,MNF\n");

  for (i=0; i<m_data.GetEMG(0).isize(); i++) {
    double d = (double)stamp/(double)m_sampEMG; 
    fprintf(p, "%f, %f, %f, 0.0, 0.0\n", d, m_data.GetEMG(0)[i], m_data.GetEMG(1)[i]);
    stamp++;
  }

  stamp = 0;
  fprintf(p, "ACCEL Data\n");
  fprintf(p, "X,Acc1,Acc2,Acc3\n");

  for (i=0; i<m_data.GetAcc(0).isize(); i++) {
    double d = (double)stamp/(double)m_sampIMU;    
    fprintf(p, "%f, %f, %f, %f\n", d, m_data.GetAcc(0)[i], m_data.GetAcc(1)[i], m_data.GetAcc(2)[i]);
    stamp++;
  }

  stamp = 0;
  fprintf(p, "GYRO Data\n");
  fprintf(p, "X,Rot1,Rot2,Rot3\n");

  for (i=0; i<m_data.GetGyr(0).isize(); i++) {
    double d = (double)stamp/(double)m_sampIMU;    
    fprintf(p, "%f, %f, %f, %f\n", d, m_data.GetGyr(0)[i], m_data.GetGyr(1)[i], m_data.GetGyr(2)[i]);
    stamp++;
  }

  
  fclose(p);
}
 
