/* Copyright (c) 2020 Digidactylus AB. 
   This file is distributed under the MIT license
*/


#ifndef FILEPARSER_H_
#define FILEPARSER_H_


#include "util/mutil.h"
#include <string>
#include <vector>
#include <iostream>
#include <fstream>
#include "base/SVector.h"
#include <set>

using namespace std;

class StringParser
{
 public:
  StringParser();

  virtual ~StringParser();

  void SetLine(const string & line);
  void SetLine(const string & line, const string & delimiter);
  void SetLineDelim(const string & line);

  void KeepEmpty(bool b);
  
  int GetItemCount();

  bool IsString(int index);
  bool IsInt(int index);
  bool IsFloat(int index);


  const string & AsString(int index);
  char AsChar(int index);
  int AsInt(int index);
  double AsFloat(int index);

  void AddDelimiter(const string & s) {
    m_delim.push_back(s);
  }

 private:
  CMAsciiReadFileStream m_file;
  vector<string> m_items;
  bool m_bNoMult;

protected:
  svec<string> m_delim;
};






class FlatFileParser : public StringParser
{
 public:
  FlatFileParser();
  FlatFileParser(const string & fileName);

  virtual ~FlatFileParser();

  void Open(const string & fileName);
  bool Exists(const string &fileName);

  bool ParseLine(const string & delim = ""); // whitespace by default
  bool IsEndOfFile();

  bool GetLine(string & line);
  const string & Line() const {return m_line;}

  void LoadVector(string &filename, vector<string> &elements);
  void LoadSet(string &filename, set<string> &elements);

 private:
  CMAsciiReadFileStream m_file;
  string m_line;
  bool m_fromRedir;
};



#endif

