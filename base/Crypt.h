#ifndef CRYPT_H
#define CRYPT_H

#include "base/SVector.h"

class Crypt
{

 public:
  Crypt();

  void Encrypt(const string & inFile, const string & outFile);
  void Decrypt(const string & inFile, const string & outFile);

  
 private:
  void Enc(unsigned char * data, int n);
  void Dec(unsigned char * data, int n);
  svec<unsigned char> m_code;
  int m_k;
};








#endif

