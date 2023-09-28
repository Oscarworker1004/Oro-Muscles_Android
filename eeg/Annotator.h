#ifndef ANNOTATOR_H
#define ANNOTATOR_H

#include "base/SVector.h"
#include "base/FileParser.h"

class Hints;
class BTLData;


class OneAnno
{
public:
  OneAnno() {
    Clear();
  }

  void Clear() {
    m_start = 0.;
    m_stop = 0.;
    m_idx = -1;
    m_sample = 0;
    m_move = 0.;
    m_spin = 0.;
    m_intens = 0.;
    m_moveMax = 0.;
    m_spinMax = 0.;
    m_intensMax = 0.;
    m_rep = 0;
    m_name = "<UNK>";

  }
  
  double & Start() {return m_start;}
  double & Stop() {return m_stop;}
  int & Idx() {return m_idx;}
  int & Sample() {return m_sample;}
  string & Name() {return m_name;}
  double & Move() {return m_move;}
  double & Spin() {return m_spin;}
  double & Intens() {return m_intens;}
  int &Rep() {return m_rep;}

  double & IntensMax()  {return m_intensMax;}
  double & MoveMax()  {return m_moveMax;}
  double & SpinMax()  {return m_spinMax;}

  
  const double & Start() const {return m_start;}
  const double & Stop() const {return m_stop;}
  const int & Idx() const {return m_idx;}
  const int & Sample() const {return m_sample;}
  const string & Name() const {return m_name;}
  const double & Move() const {return m_move;}
  const double & Spin() const {return m_spin;}
  const double & Intens() const {return m_intens;}
  const int &Rep() const {return m_rep;}

  
  const double & IntensMax() const {return m_intensMax;}
  const double & MoveMax() const {return m_moveMax;}
  const double & SpinMax() const {return m_spinMax;}

  
private:
  double m_start;
  double m_stop;
  int m_idx;
  int m_sample;
  string m_name;
  double m_move;
  double m_spin;
  double m_intens;
  double m_moveMax;
  double m_spinMax;
  double m_intensMax;
  int m_rep;

};

class Annotation
{
public:
  Annotation() {}

  OneAnno & operator [] (int i) {return m_annot[i];}
  const OneAnno & operator [] (int i) const {return m_annot[i];}
  int isize() const {return m_annot.isize();}
  void push_back(const OneAnno & a) {m_annot.push_back(a);}

  void Write(const string & fileName);
  
private:
  svec<OneAnno> m_annot;
};


class Boundaries
{
public:
  Boundaries() {
    start_emg = 0;
    stop_emg = 0;
    start_imu = 0;
    stop_imu = 0;
  }
  
  int start_emg;
  int stop_emg;
  int start_imu;
  int stop_imu;
};


class Annotator
{
public:
  Annotator() {
    rate_emg = 500;
    rate_imu = 100;

    delim = "\t";

   }
  
  void ReadTB(const string & fileName) {
    FlatFileParser parser;
  
    parser.Open(fileName);
    
    while (parser.ParseLine()) {
      if (parser.GetItemCount() == 0)
	continue;
      m_data.push_back(parser.AsInt(1));
    }
  }

  void SetRates(int e_rate, int i_rate) {
     rate_emg = e_rate;
     rate_imu = i_rate;
  }

  
  // exg rate, imu rate
  void Annotate(Annotation & annot, BTLData & d, int e_rate, int i_rate) const;

  void Apply(BTLData & d, int frame) const;
  
  void SetData(const svec<int> & d) {
    m_data = d;
  }

  void MakeCSVFiles(BTLData & d, const string & prefix) const;
  void MakeEMGAnnot(BTLData & d, const string & fileName) const;
  void MakeAccAnnot(BTLData & d, const string & fileName) const;
  void MakeGyroAnnot(BTLData & d, const string & fileName) const;
  void MakeCurveAnnot(BTLData & d, const string & fileName) const;
  void MakeMasterAnnot(BTLData & d, const Hints & h, const string & fileName) const;
  void MakeGenAnnot(BTLData & d, const Hints & h, const string & fileName, bool cycle) const;

private:
  void FindBounds(svec<Boundaries> & out, const BTLData & d) const;
  void FindBoundsCycle(svec<Boundaries> & out, const BTLData & d) const;
  int Print(string & data, string & header, const svec<double> & d,
	    const string & label, double dur) const;

  void PrintRise(string & data, string & header, const svec<double> & d,
		 const string & label, int rate, bool diff = true) const;
  
  void Append(string & data, string & header, double d, const string & label) const;
  void Append(string & data, string & header, const string & s, const string & label) const;
  
  svec<int> m_data;
  int rate_emg;
  int rate_imu;
  string delim;

};

#endif //ANNOTATOR_H
