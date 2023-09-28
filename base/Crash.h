/* Copyright (c) 2020 Digidactylus AB. 
   This file is distributed under the MIT license
*/

#ifndef CRASH_H
#define CRASH_H

#include "base/SVector.h"

class Crash
{
 public:
  void SegFault() {
    vector<double> bad;
    bad.resize(16);
    for (int i=0; i<0x7FFFFFFF; i++)
      bad[i] = 666;
  }
  
};



#endif

