#ifndef BIN2CSV_H
#define BIN2CSV_H

#include "base/SVector.h"
#include "signal/Filters.h"
#include <math.h>
#include "extern/attitude_estimator/attitude_estimator.h"

class IMReadStream;

class AllData
{
 public:
  AllData() {
    m_emgPerFrame = 50;
    m_imuPerFrame = 10;
    Reset();
  }

  void Reset() {
    m_emg.resize(2); // EMG & RMS
    m_acc.resize(3); // Acc
    m_gyr.resize(3); // Gyro

    m_emgFrame.clear();
    m_accFrame.clear();
    m_gyrFrame.clear();

    int i;
    for (i=0; i<m_emg.isize(); i++)
      m_emg[i].clear();
    for (i=0; i<m_acc.isize(); i++)
      m_acc[i].clear();
    for (i=0; i<m_gyr.isize(); i++)
      m_gyr[i].clear();

  }

  void AddEMG(int frame, double emg, double rms);
  void AddAcc(int frame, double x, double y, double z);
  void AddGyr(int frame, double x, double y, double z);

  const svec <double> & GetEMG(int i) const {return m_emg[i];}
  const svec <double> & GetAcc(int i) const {return m_acc[i];}
  const svec <double> & GetGyr(int i) const {return m_gyr[i];}

  int isizeEMG() const {
    cout << "DEBUG " << m_emg.isize() << endl;
    return GetEMG(0).isize();
  }
  int isizeAcc() const {
    return GetAcc(0).isize();
  }
  int isizeGyr() const {
    return GetGyr(0).isize();
  }
  
  
  void Sync2EMG(int emgSamp, int imuSamp);
  
 private:
  svec< svec < double > > m_emg;
  svec< svec < double > > m_acc;
  svec< svec < double > > m_gyr;

  svec<int> m_emgFrame;
  svec<int> m_accFrame;
  svec<int> m_gyrFrame;

  int m_emgPerFrame;
  int m_imuPerFrame;
  
};

class Bin2CSV
{
 public:
  Bin2CSV() : m_filter(4, 500., 50.) {
    m_sampEMG = 500;
    m_sampIMU = 100;

    m_accDecay = 0.95;
    m_accWeight = 0.2;
    
    m_lastFF = 0.;
    m_accBuf.resize(3, 0.);
  
    // Initialise the estimator (e.g. in the class constructor, none of these are actually strictly required for the estimator to work, and can be set at any time)
    m_attEst.setMagCalib(0.68, -1.32, 0.0);         // Recommended: Use if you want absolute yaw information as opposed to just relative yaw (Default: (1.0, 0.0, 0.0))
    // Est.setMagCalib(1.0, 0.0, 0.0);         // Recommended: Use if you want absolute yaw information as opposed to just relative yaw (Default: (1.0, 0.0, 0.0))
    m_attEst.setPIGains(2.2, 2.65, 10, 1.25);       // Recommended: Use if the default gains (shown) do not provide optimal estimator performance (Note: Ki = Kp/Ti)
    m_attEst.setQLTime(2.5);                        // Optional: Use if the default quick learning time is too fast or too slow for your application (Default: 3.0)
    m_attEst.setAttitude(0.5, 0.5, 0.5, 0.5);       // Optional: Use if you have prior knowledge about the orientation of the robot (Default: Identity orientation)
    m_attEst.setAttitudeEuler(M_PI, 0.0, 0.0);      // Optional: Use if you have prior knowledge about the orientation of the robot (Default: Identity orientation)
    m_attEst.setAttitudeFused(M_PI, 0.0, 0.0, 1.0); // Optional: Use if you have prior knowledge about the orientation of the robot (Default: Identity orientation)
    m_attEst.setGyroBias(0.152, 0.041, -0.079);     // Optional: Use if you have prior knowledge about the gyroscope bias (Default: (0.0, 0.0, 0.0))
    m_attEst.setAccMethod(m_attEst.ME_FUSED_YAW);        // Optional: Use if you wish to experiment with varying acc-only resolution methods

  }

  void Convert(const string & outName, const string & inName) {
    ReadBin(inName);
    Process();
    Write(outName);
  }
  
  void ReadBin(const string & fileIn);
  // 0 = bad
  // 1 = good
  // 2 = don't know
  int Read(IMReadStream & s);

  int ReadCometa(IMReadStream & s);

  void Process();
  
  void Write(const string & fileName);

  const AllData & GetData() const {return m_data;}

  void Reset() {
    m_data.Reset();
    m_lastFF = 0.;
    for (int i=0; i<m_accBuf.isize(); i++)
      m_accBuf[i] = 0.;

  }

  bool badEMG(double ff) const {
    double diff = ff - m_lastFF;
    double thresh = 10000.;
    if (ff > thresh || ff < -thresh)
      return true;
    if (diff > thresh/4. || diff < -thresh/4.)
      return true;
    return false;
  }
  
 private:
  int m_sampEMG;
  int m_sampIMU;
  AllData m_data;
  BWHighPassFilter m_filter;
  RMSStream m_rms;

  stateestimation::AttitudeEstimator m_attEst;
	
  double m_lastFF;
  svec<double> m_accBuf;
  double m_accDecay;
  double m_accWeight;
  
};

int FixFrame(int last, int frame);

#endif

