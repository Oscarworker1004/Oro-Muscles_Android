#ifndef RECORDCTRL_H
#define RECORDCTRL_H

#include "base/SVector.h"
#include "signal/USBReader.h"
#include "signal/VideoWrap.h"
#include "signal/Bin2CSV.h"

class RecordCtrl
{
 public:
  RecordCtrl() {
    m_startCount = 0;
    m_stopCount = 0;
    m_run = false;
    m_lastSize = 0;
    m_fileName = "qtest.bin";

    m_buf.Open("");  
    m_buf.SetReserve(2048);
    m_isGood = true;
    m_cometa = false;
  }

  void SetFileName(const string & fileName) {
    m_fileName = fileName;
  }

  bool IsGood() const {return m_isGood;}
  
  void StartRec();
  void StopRec();
    
  int GetData(svec<double> & d, int size);

  void Reset();

  void SetDataBuffer(svec<char> & in);

  void WriteCSV(const string & fileName);

 private:
  bool m_run;
  int m_startCount;
  int m_stopCount;

  USBReader m_urd;
  CMReadBufferStream m_buf;
  Bin2CSV m_b2c;
  int m_lastSize;
  string m_fileName;
  bool m_isGood;

  bool m_cometa;

};

#endif 
