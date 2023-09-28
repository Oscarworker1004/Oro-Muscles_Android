#ifndef VIDEOWRAP_H
#define VIDEOWRAP_H

#include "base/SVector.h"
#include "base/Processes.h"


class VideoWrap
{
 public:
  VideoWrap() {
    m_fifo = "vidfifo";
  }

  void SetVideoName(const string & vidName);

  void SetDeviceLink() {
    throw;
  }
  
  void Start();

  // Sync up the time stamps here
  void Stop();

  void SetFofoName(const string & fifo) {
    m_fifo = fifo;
  }

 private:
  string m_vidName;
  string m_fifo;
  ProcInfo m_proc;
};


#endif

