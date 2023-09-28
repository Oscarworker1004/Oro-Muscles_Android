#include "base/Crypt.h"

Crypt::Crypt()
{
  m_k = 0;
  m_code.resize(11, 123);
  m_code[0] = 0;
  m_code[1] = 0x55;
  m_code[2] = 0xF3;
  m_code[3] = 0xA9;
  m_code[4] = 0x07;
  m_code[5] = 0x66;
  m_code[6] = 0x4A;
  m_code[7] = 0xC2;
  m_code[8] = 0x62;
  m_code[9] = 0x2F;
  m_code[10] = 0xCC;
  
}

void Crypt::Encrypt(const string & inFile, const string & outFile)
{
  m_k = 0;
  FILE * p = fopen(inFile.c_str(), "rb");
  FILE * o = fopen(outFile.c_str(), "wb");
  int n;

  unsigned char data[8092];
  
  while ((n=fread((void*)data, 1, sizeof(data), p)) > 0) {
    Enc(data, n);
    fwrite((void*)data, 1, n, o);
  }
  fclose(p);
  fclose(o);
}

void Crypt::Decrypt(const string & inFile, const string & outFile)
{
  m_k = 0;
  Encrypt(inFile, outFile);
}

void Crypt::Enc(unsigned char * data, int n)
{
  for (int i=0; i<n; i++) {
    int idx = m_k % m_code.isize();
    data[i] = (data[i] ^ m_code[idx]);
  }
  m_k++;
}

void Crypt::Dec(unsigned char * data, int n)
{
  Enc(data, n);
}
