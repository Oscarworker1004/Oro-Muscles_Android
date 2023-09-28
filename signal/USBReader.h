#ifndef USBREADER_H
#define USBREADER_H

#include "base/SVector.h"
#include "util/mutil.h"
#include "base/StringUtil.h"
#include <unistd.h>

void * start_thread_USB(void * str);


class USBReader
{
public:
  USBReader() {
    m_pUSB = NULL;
    pthread_mutex_init(&m_lock, NULL);
    m_bRun = false;
    m_USB = "/dev/ttyUSB0";

    
#ifdef __APPLE_CC__
    //m_USB = "/dev/cu.usbserial-AU041V64"; //Mac
     m_USB = "/dev/cu.usbserial-AU041XUN"; //Mac Eric
#endif

     //m_USB = "/dev/bus/usb/001/006";
    
    m_bDone = true;
    m_bBuffer = false;
    m_lastBufPos = 0;
  }

  ~USBReader() {
    if (m_pUSB != NULL)
      fclose(m_pUSB);
  }

  void DoBuffer(bool b) {
    m_bBuffer = b;
  }
  
  void SetUSBName(const string & s) {
    m_USB = s;
  }

  void FindUSBName();
  
  void StartWrite(const string & outName) {
    if (m_bRun)
      return;
    
    m_bRun = true;
    m_bDone = false;
    m_outName = outName;
    m_buffer.Open("");
    m_lastBufPos = 0;
   
    StartThread();
  }

  void Collect();


  void Stop() {
    pthread_mutex_lock(&m_lock);
    m_bRun = false;
    pthread_mutex_unlock(&m_lock);
    m_buffer.Close(); // Not sure we want this

    while (!IsDone()) {
      usleep(1000);
    }
  }

  void CopyBuffer(CMReadBufferStream & out) {
    pthread_mutex_lock(&m_lock);
    out.Append(&m_buffer.Get()[m_lastBufPos], m_buffer.BytesProcessed() - m_lastBufPos);
    m_lastBufPos = m_buffer.BytesProcessed();
    pthread_mutex_unlock(&m_lock);

  }


private:
  bool GetRun() {
    bool b = false;
    pthread_mutex_lock(&m_lock);
    b = m_bRun;
    pthread_mutex_unlock(&m_lock);
    return b;
  }

  void SetDone() {
    pthread_mutex_lock(&m_lock);
    m_bDone = true;
    pthread_mutex_unlock(&m_lock);
  }

  bool IsDone() {
    bool b = false;
    pthread_mutex_lock(&m_lock);
    b = m_bDone;
    pthread_mutex_unlock(&m_lock);
    return b;

  }
  
  bool StartThread() { 
    int iReturnValue1 = pthread_create(&m_iThreadId, NULL /*&attr*/, &start_thread_USB, (void *)this);
    return true;
  }


private:
  FILE * m_pUSB;
  CMWriteFileStream m_out;
  pthread_mutex_t m_lock;
  pthread_t m_iThreadId;
  bool m_bRun;
  bool m_bDone;
  string m_outName;
  string m_USB;

  CMWriteBufferStream m_buffer;
  bool m_bBuffer;
  int m_lastBufPos;
};










#endif



