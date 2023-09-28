#ifndef UNIVDATA_H
#define UNIVDATA_H

#include "base/SVector.h"
#include "util/mutil.h"


enum UNIV_ID
{
 UNIV_NONE,
 UNIV_RAW_SENCURE,
 UNIV_RAW_COMETA,
 UNIV_RAW_ORO,
 UNIV_EMG,
 UNIV_ACC,
 UNIV_GYR,
 UNIV_SPEC,
 UNIV_ANNOT,
 UNIV_HINT,
 UNIV_TRI
};

const int globHead = 0x68746D01; // -> Spells "HTM" plus version
const int sectionHead = 0x0F0F0F0F; // 


class InvWriteStream : public IMWriteStream
{
 protected:
  virtual bool WriteSimpleType(const void * pData, long lenInBytes) {return false;}
  virtual bool WriteBlob(const void * pData, long lenInElements, long elSize) {return false;}
  virtual bool WriteString(const CMString & s) {return false;}
  virtual bool WriteStringLine(const CMString & s) {return false;}
  virtual bool Open(const CMString & name) {return false;}
  virtual bool Close() {return false;}
  virtual bool IsOpen() {return false;}
  virtual bool IsEnd() {return false;}
  IMWriteStream * CloneAndOpen(const CMString &name) {return NULL;}
  virtual long BytesProcessed() {return 0;}
  
};

class InvReadStream : public IMReadStream
{
 protected:
  virtual bool ReadSimpleType(void * pData, long lenInBytes) {return false;}
  virtual bool ReadBlob(void * pData, long lenInElements, long elSize) {return false;}
  virtual bool ReadString(CMString & s) {return false;}
  virtual bool ReadStringLine(CMString & s) {return false;}
  virtual bool Open(const CMString & name) {return false;}
  virtual bool Close() {return false;}
  virtual bool IsOpen() {return false;}
  virtual bool IsEnd() {return false;}
  virtual IMReadStream * CloneAndOpen(const CMString &name) {return NULL;}
  virtual long int BytesProcessed() {return 0;}
 

};

class UnivDataHeader
{
 public:
  UnivDataHeader() {
    m_data = -1;
  }
  
  UnivDataHeader(int id, int version) {
    Set(id, version);
  }

  void Set(int id, int version) {
    m_data = id * 256 + version;
  }
  
  void Set(int data) {
    m_data = data;
  }
  
  int Get() const {
    return m_data;
  }

  int ID() const {
    return m_data / 256;
  }

  int Version() {
    return m_data & 0xFF;
  }
  
 private:
  int m_data;
};


//======================================================================
class UnivDataRead
{
 public:
  UnivDataRead() {
    m_pStream = &m_null;
  }

  bool OpenFile(const string & fileName, bool bCheck = true) {
    bool b = m_file.Open(fileName.c_str());
    if (!b)
      return false;
    m_pStream = &m_file;
    if (bCheck)
      return IsUniversal();
    else
      return true;
  }


  void Close() {
    if (m_pStream != NULL) {
      m_pStream->Close();
    }
    m_pStream = &m_null;
  }

  // Caller has to destroy the stream!!
  void SetStream(IMReadStream * p) {
    m_pStream = p;
  }

  // Reads/removes the universal header
  bool IsUniversal() {
    int v;
    Read(v);
    return (v == globHead);      
  }

  bool IsUniversal(const string & fileName) {
    bool b = OpenFile(fileName);
    Close();
    return b;
  }
  
  
  UnivDataHeader ReadHeader() {
    UnivDataHeader h;
    int sig;
    Read(sig);
    if (sig != sectionHead)
      return h;
    
    int v;
    Read(v);
    h.Set(v);
    return h;
  }
  
  bool Read(long & d)             { return m_pStream->Read(d); }
  bool Read(unsigned long & d)    { return m_pStream->Read(d); }
  
  bool Read(int & d)             { return m_pStream->Read(d); }
  bool Read(unsigned int & d)    { return m_pStream->Read(d); }
  
  bool Read(short & d)            { return m_pStream->Read(d); }
  bool Read(unsigned short & d)   { return m_pStream->Read(d); }
  
  bool Read(char & d)             { return m_pStream->Read(d); }
  bool Read(unsigned char & d)    { return m_pStream->Read(d); }
  bool Read(signed char & d)      { return m_pStream-> Read(d);}
  
  bool Read(float & d)            { return m_pStream->Read(d); }
  bool Read(double & d)           { return m_pStream->Read(d); }
  
  bool Read(long long & d)             { return m_pStream->Read(d); }
  bool Read(unsigned long long & d)    { return m_pStream->Read(d); }
  
  bool Read(CMString & d)         { return m_pStream->Read(d); }
  bool ReadLine(CMString & d)     { return m_pStream->ReadLine(d); }

  bool Read(string & d)         {
    CMString s;
    bool b = m_pStream->Read(s);
    d = s;
    return b;
  }
  
  bool Read(void * p, long lenInElements, long elSize = 1)    { return m_pStream->Read(p, lenInElements, elSize); }

  IMReadStream * Stream() {return m_pStream;}

  
 protected:
  IMReadStream *m_pStream;
  CMReadFileStream m_file;
  CMReadBufferStream m_mem;
  InvReadStream m_null;
  
};

//==================================================================================================


class UnivDataWrite
{
 public:
  UnivDataWrite() {
    m_pStream = &m_null;
  }

  void OpenFile(const string & fileName) {
    m_file.Open(fileName.c_str());
    m_pStream = &m_file;
    SetUniversal();
  }

  void Close() {
    if (m_pStream != NULL) {
      m_pStream->Close();
    }
    m_pStream = &m_null;
  }

  // Caller has to destroy the stream!!
  void SetStream(IMWriteStream * p) {
    m_pStream = p;
    SetUniversal();
  }
  
  bool SetUniversal() {    
    return Write(globHead);      
  }
  
  bool WriteHeader(const UnivDataHeader & h) {
    int v = h.Get();
    Write(sectionHead);
    return Write(v);    
  }

  bool Write(const long & d)             { return m_pStream->Write(d); }
  bool Write(const unsigned long & d)    { return m_pStream->Write(d); }
  
  bool Write(const int & d)             { return m_pStream->Write(d); }
  bool Write(const unsigned int & d)    { return m_pStream->Write(d); }
  
  bool Write(const short & d)            { return m_pStream->Write(d); }
  bool Write(const unsigned short & d)   { return m_pStream->Write(d); }
  
  bool Write(const char & d)             { return m_pStream->Write(d); }
  bool Write(const unsigned char & d)    { return m_pStream->Write(d); }
  bool Write(const signed char & d)      { return m_pStream-> Write(d); }
  
  bool Write(const float & d)            { return m_pStream->Write(d); }
  bool Write(const double & d)           { return m_pStream->Write(d); }
  
  bool Write(const long long & d)             { return m_pStream->Write(d); }
  bool Write(const unsigned long long & d)    { return m_pStream->Write(d); }
  
  bool Write(const CMString & d)         { return m_pStream->Write(d); }
  bool WriteLine(const CMString & d)     { return m_pStream->WriteLine(d); }

  bool Write(const string & d)         {
    CMString s;
    s = d.c_str();
    return m_pStream->Write(s);
  }
  
  bool Write(const void * p, long lenInElements, long elSize = 1)    { return m_pStream->Write(p, lenInElements, elSize); }

  
  IMWriteStream * Stream() {return m_pStream;}

 protected:
  IMWriteStream *m_pStream;
  CMWriteFileStream m_file;
  CMWriteBufferStream m_mem;
  InvWriteStream m_null;

};


#endif //UNIVDATA_H
