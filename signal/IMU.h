#ifndef IMU_H
#define IMU_H

#include "base/SVector.h"
#include "physics/Coordinates.h"


class IMUStream
{
public:
  IMUStream()
  {
    m_comp = 1;
    m_win = 0;
  }


  void SetCompWin(int comp, int win) {
    m_comp = comp;
    m_win = win;
  }


  void operator *= (double n) {
    for (int i=0; i<m_c.isize(); i++)
      m_c[i] *= n;
  }

  void Derivative() {
    svec<Coordinates> c;
    c.resize(m_c.isize());
    for (int i=1; i<m_c.isize(); i++)
      c[i] = m_c[i]-m_c[i-1];

    m_c = c;
    
    m_c[0] = m_c[1];
  }
  

  void Read(const string & fileName, const string & id) {
    FlatFileParser parser;

   
    
    parser.Open(fileName);
    bool b = false;
    while (parser.ParseLine()) {
      if (parser.GetItemCount() == 0)
	continue;
      //cout << parser.Line() << endl;
      if (parser.AsString(1) == "Data") { 
	if (parser.AsString(0) == id) {
	  b = true;
	  //cout << "ON!!" << endl;
	  parser.ParseLine();
	  continue;
	
	} else {
	  //cout << "OFF!!" << endl;
	  b = false;
	}
      }

      if (!b)
	continue;
      
      Coordinates c;
      c[0] = parser.AsFloat(1);
      c[1] = parser.AsFloat(2);
      c[2] = parser.AsFloat(3);
      //cout << "READ: " << c[2] << endl;
      m_c.push_back(c);
      m_time.push_back(parser.AsFloat(0));
    }

    svec<Coordinates> tmp;
    int sm = m_comp;
    svec<double> time;
    tmp.resize(m_c.isize()/sm-1);
    time.resize(m_c.isize()/sm-1, -1);
    int win = m_win;
    for (int i=win; i<tmp.isize()-win; i++) {
      for (int j=-win; j<=win; j++) {
	if (j >= m_c.isize())
	  break;
	tmp[i][0] += m_c[i*sm+j][0]/(2*(double)win+1.);
	tmp[i][1] += m_c[i*sm+j][1]/(2*(double)win+1.);
	tmp[i][2] += m_c[i*sm+j][2]/(2*(double)win+1.);
      }
      time[i] = m_time[i*sm];
    }
    m_c = tmp;
    m_time = time;
    
  }
  
  int isize() const {return m_c.isize();}
  const Coordinates & operator[](int i) const {return m_c[i];};
  double Time(int i) const {return m_time[i];}
  
 private:

  int m_comp;
  int m_win;
  svec<Coordinates> m_c;
  svec<double> m_time; 
};














#endif //IMU_H


