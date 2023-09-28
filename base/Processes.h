#ifndef PROCESSES_H
#define PROCESSES_H


#include "base/SVector.h"

class ProcInfo
{
 public:
  ProcInfo() {
    m_collect = false;
  }
  
  ~ProcInfo();

  void SetCollect(bool b) {
    m_collect = b;
  }
  
  void MakeFifo(const string & name);
  
  void Spawn(const string & cmmd, bool bg = true);


  void Start();
  void SendKeys(const string & key);

  const svec<string> Output() const {
    return m_output;
  }
  
 private:
  bool m_collect;
  svec<string> m_output;
  string m_fifo;
};


#endif


