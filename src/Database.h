#ifndef DATABASE_H
#define DATABASE_H

#include "base/SVector.h"


class DBItem
{
 public:
  DBItem() {
  }

  int isize() const {return m_data.isize();}
  const string & operator[] (int i) const {return m_data[i];}
  string & operator[] (int i) {return m_data[i];}
  void push_back(const string & s) {
    m_data.push_back(s);
  }
  
 private:
  svec<string> m_data;
};


class Database
{
 public:
  Database() {
  }

  void Read(const string & fileName);
  void Write(const string & fileName) const;

  Database Find(const string & key, const string & value);

  int isize() const {return m_data.isize();}
  const DBItem & operator[] (int i) const {return m_data[i];}
  DBItem & operator[] (int i) {return m_data[i];}
  void push_back(const DBItem & d) {
    m_data.push_back(d);
  }

  string FindIn(int idx, const string & key) {
    for (int i=0; i<m_keys.isize(); i++) {
      if (m_keys[i] == key) {
	return m_data[idx][i];
      }
    }
    return "";
  }

  
 private:
  svec<DBItem> m_data;
  svec<string> m_keys;
};





#endif
