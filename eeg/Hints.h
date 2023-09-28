#ifndef HINTS_H
#define HINTS_H

#include "base/SVector.h"
#include "util/mutil.h"

class BTLData;


class HintMeta
{
public:
  HintMeta() {
    m_version = 1;
  }
  
  void Read(IMReadStream & s);
  void Write(IMWriteStream &s) const;

  int & Version() {return m_version;}
  const int & Version() const {return m_version;}

  string & FileName() {return m_fileName;}
  string & Sample()   {return m_sample;}
  string & Date()     {return m_date;}
  string & Channel()  {return m_channel;}
  string & Comment()  {return  m_comment;}

  const string & FileName() const {return m_fileName;}
  const string & Sample()   const {return m_sample;}
  const string & Date()     const {return m_date;}
  const string & Channel()  const {return m_channel;}
  const string & Comment()  const {return  m_comment;}

private:
  int m_version;
  string m_fileName;
  string m_sample;
  string m_date;
  string m_channel;
  string m_comment;
};


class TDShape
{
public:
  TDShape() {
    m_from = m_to = 0;
    m_state = -1;
    m_have = 0;
    m_cycle = false;
  }

  void Set(int from, int to, int state) {
    m_from = from;
    m_to = to;
    m_state = state;
  }

  HintMeta & Meta() {return m_meta;}
  const HintMeta & Meta() const {return m_meta;}

  void Fill(const BTLData & d);

  void Add(const TDShape & d);

  int isize() const {return m_data.isize();}
  double & operator[] (int i) {return m_data[i];}
  const double & operator[] (int i) const {return m_data[i];}

  int From() const {return m_from;}
  int To() const {return m_to;}
  int State() const {return m_state;}

  bool IsCycle() const {return m_cycle;}
  const string & Name() const {return m_name;}
  void SetCycle(const string & name) {
    m_name = name;
    m_cycle = true;
  }

  double Acc_X(int i) const {return m_acc_x[i];}
  double Acc_Y(int i) const {return m_acc_y[i];}
  double Acc_Z(int i) const {return m_acc_z[i];}

  double Gyro_X(int i) const {return m_gyro_x[i];}
  double Gyro_Y(int i) const {return m_gyro_y[i];}
  double Gyro_Z(int i) const {return m_gyro_z[i];}

  TDShape Stretch(double d) const;

  svec<double> & DData() {return m_data;}
  svec<double> & DAcc_X() {return m_acc_x;}
  svec<double> & DAcc_Y() {return m_acc_y;}
  svec<double> & DAcc_Z() {return m_acc_z;}
  svec<double> & DGyro_X() {return m_gyro_x;}
  svec<double> & DGyro_Y() {return m_gyro_y;}
  svec<double> & DGyro_Z() {return m_gyro_z;}

  void Read(IMReadStream & s);
  void Write(IMWriteStream &s) const;
  void ReadMeta(IMReadStream & s);
  void WriteMeta(IMWriteStream &s) const;
private:
  void Smooth();

  void ReadArray(svec<double> & d, IMReadStream & s);
  void WriteArray(const svec<double> & d, IMWriteStream & s) const;
  
  int m_from;
  int m_to;
  int m_state;
  int m_have;
  bool m_cycle;
  string m_name;
  svec<double> m_data;
  svec<double> m_acc_x;
  svec<double> m_acc_y;
  svec<double> m_acc_z;
  svec<double> m_gyro_x;
  svec<double> m_gyro_y;
  svec<double> m_gyro_z;

  HintMeta m_meta;
};


class HintBlock
{
public:
  HintBlock() {
    m_start = -1;
    m_end = -1;    
  }
  
  HintBlock(int from, int to, const string & name) {
    m_start = from;
    m_end = to;
    m_name = name;
  }

  int & Start() {return m_start;}
  int & End() {return m_end;}
  string & Name() {return m_name;}

  const int & Start() const {return m_start;}
  const int & End() const {return m_end;}
  const string & Name() const {return m_name;}

  
private:
  int m_start;
  int m_end;
  string m_name;
};

class Hints
{
 public:
  Hints() {
    m_frame = 128;
    m_samplerate = 500.;
    m_offset = 0.;
  }

  int & Frame() {return m_frame;}
  const int & Frame() const {return m_frame;}
  
  double & SampleRate() {return m_samplerate;}
  const double & SampleRate() const {return m_samplerate;}

  double & Offset() {return m_offset;}
  const double & Offset() const {return m_offset;}
 
  int & operator[] (int i) {return m_data[i];}
  const int & operator[] (int i) const {return m_data[i];}
  int isize() const {return m_data.isize();}

  void Read(const string & fileName);

  // Reads the repetitions
  void FromReps(const BTLData &d);

  void SetInterval(double from, double to, int state);

  const svec<int> & Data() const {return m_data;}

  void FillShapes(const BTLData & b);

  int NumShapes() const {return m_shapes.isize();}
  TDShape & Shape(int i) {return m_shapes[i];}
  const TDShape & Shape(int i) const {return m_shapes[i];}

  void AddShape(const TDShape & s) {
    m_shapes.push_back(s);
  }

  // WARNING: Meta info is NOT written out!!!
  void ReadDB(IMReadStream & s);
  void WriteDB(IMWriteStream &s) const;

  void ReadDB(const string & fileName);
  void WriteDB(const string & fileName) const;

  int NumBlocks() const {return m_blocks.isize();}
  const HintBlock & Block(int i) const {return m_blocks[i];}

  void AddBlock(int from, int to, const string & name) {
    m_blocks.push_back(HintBlock(from, to, name));
  }
  
 private:
  
  int m_frame;
  double m_samplerate;
  double m_offset;
  svec<int> m_data;
  svec<TDShape> m_shapes;
  svec<HintBlock> m_blocks;
};

#endif


