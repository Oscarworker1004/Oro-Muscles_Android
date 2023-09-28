#include "src/Database.h"
#include "base/FileParser.h"

void Database::Read(const string & fileName)
{
  FlatFileParser parser;

  int i;
  
  parser.Open(fileName);

  parser.ParseLine();

  m_keys.clear();

  for (i=0; i<parser.GetItemCount(); i++)
    m_keys.push_back(parser.AsString(i));
  
  while (parser.ParseLine()) {
    if (parser.GetItemCount() == 0)
      continue;

    DBItem d;
    for (i=0; i<parser.GetItemCount(); i++)
      d.push_back(parser.AsString(i));
    m_data.push_back(d);
  }
}

void Database::Write(const string & fileName) const
{
  throw;
}

Database Database::Find(const string & key, const string & value)
{
  Database sub;

  sub.m_keys = m_keys;

  int i;

  int col = -1;

  for (i=0; i<m_keys.isize(); i++) {
    if (m_keys[i] == key)
      col = i;
  }

  if (col < 0)
    return sub;

  for (i=0; i<m_data.isize(); i++) {
    if (m_data[i][col] == value)
      sub.push_back(m_data[i]);
  }
  
  return sub;
}
