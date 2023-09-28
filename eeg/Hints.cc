#include "eeg/Hints.h"
#include "eeg/BTLData.h"
#include "base/CommandLineParser.h"

void HintMeta::Read(IMReadStream & s)
{
  CMString a;

  s.Read(m_version);
  s.Read(a);
  m_fileName = (const char*)a;
  s.Read(a);
  m_sample = (const char*)a;
  s.Read(a);
  m_date = (const char*)a;
  s.Read(a);
  m_channel = (const char*)a;
  s.Read(a);
  m_comment = (const char*)a;
  
}


void HintMeta::Write(IMWriteStream &s) const
{
  s.Write(m_version);
  s.Write(m_fileName.c_str());
  s.Write(m_sample.c_str());
  s.Write(m_date.c_str());
  s.Write(m_channel.c_str());
  s.Write(m_comment.c_str());

}


void TDShape::ReadMeta(IMReadStream & s)
{
  m_meta.Read(s);
}

void TDShape::WriteMeta(IMWriteStream &s) const
{
  m_meta.Write(s);
}

void TDShape::Read(IMReadStream & s)
{
  s.Read(m_from);
  s.Read(m_to);
  s.Read(m_state);
  s.Read(m_have);
  int c = 0;
  s.Read(c);
  if (c)
    m_cycle = true;
  else
    m_cycle = false;

  CMString name;
  s.Read(name);
  m_name = (const char*)name;

  ReadArray(m_data, s);
  ReadArray(m_acc_x, s);
  ReadArray(m_acc_y, s);
  ReadArray(m_acc_z, s);
  ReadArray(m_gyro_x, s);
  ReadArray(m_gyro_y, s);
  ReadArray(m_gyro_z, s);

}

void TDShape::Write(IMWriteStream &s) const
{
  s.Write(m_from);
  s.Write(m_to);
  s.Write(m_state);
  s.Write(m_have);
  int c = 0;
  if (m_cycle)
    c = 1;
  
  s.Write(c);

  CMString name = m_name.c_str();
  s.Write(name);

  WriteArray(m_data, s);
  WriteArray(m_acc_x, s);
  WriteArray(m_acc_y, s);
  WriteArray(m_acc_z, s);
  WriteArray(m_gyro_x, s);
  WriteArray(m_gyro_y, s);
  WriteArray(m_gyro_z, s);

  
}

void TDShape::ReadArray(svec<double> & d, IMReadStream & s)
{
  d.clear();
  int i;
  int n = 0;
  s.Read(n);
  d.resize(n, 0);
  for (i=0; i<n; i++)
    s.Read(d[i]);
  
}

void TDShape::WriteArray(const svec<double> & d, IMWriteStream &s) const
{
  int i;
  int n = d.isize();
  s.Write(n);
 
  for (i=0; i<n; i++)
    s.Write(d[i]);
}



void TDShape::Fill(const BTLData & d)
{
  m_have = 1;
  int i;

  m_data.resize(m_to - m_from, 0.);

  cout << "Fill shape from " << m_from << " to " << m_to << endl; 

  //************************** HARD CODED RATES *****************************
  for (i=m_from; i<m_to; i++) {
    int j = i/5;
    if (j >= d.Gyro().isize()) {
      m_to = (j-1)*5;
      break;
    }
    if (i % 5 == 0) {
      m_gyro_x.push_back(d.Gyro()[j][0]);
      m_gyro_y.push_back(d.Gyro()[j][1]);
      m_gyro_z.push_back(d.Gyro()[j][2]);

      m_acc_x.push_back(d.Acc()[j][0]);
      m_acc_y.push_back(d.Acc()[j][1]);
      m_acc_z.push_back(d.Acc()[j][2]);
     
    }
    m_data[i-m_from] = d.EXG()[i].RMS();
  }

  // Padding
  m_gyro_x.push_back(0);
  m_gyro_y.push_back(0);
  m_gyro_z.push_back(0);

  m_acc_x.push_back(0);
  m_acc_y.push_back(0);
  m_acc_z.push_back(0);
 
  
  Smooth();
  Smooth();
}


TDShape TDShape::Stretch(double d) const
{
  svec<double> data;
  svec<double> acc_x;
  svec<double> acc_y;
  svec<double> acc_z;
  svec<double> gyro_x;
  svec<double> gyro_y;
  svec<double> gyro_z;

  int i;


  int n = (int)(d*(double)m_acc_x.isize());
  
  acc_x.resize(n, 0);
  acc_y.resize(n, 0);
  acc_z.resize(n, 0);
  gyro_x.resize(n, 0);
  gyro_y.resize(n, 0);
  gyro_z.resize(n, 0);

  n *= 5;

  data.resize(n, 0);


  
  //cout << m_acc_x.isize() << endl;
  for (i=0; i<m_data.isize(); i++) {
    int j = (int)(d*(double)i);
    if (j >= data.isize())
      continue;
    data[j] = (*this)[i];
  } 
  for (i=0; i<m_acc_x.isize(); i++) {
    int j = (int)(d*(double)i);
    if (j >= acc_x.isize())
      continue;  
    acc_x[j] = Acc_X(i);
    acc_y[j] = Acc_Y(i);
    acc_z[j] = Acc_Z(i);
    gyro_x[j] = Gyro_X(i);
    gyro_y[j] = Gyro_Y(i);
    gyro_z[j] = Gyro_Z(i);
    
  }

  TDShape out = *this;

  //cout << "Copy data" << endl;
  out.DData() = data;

  
  out.DAcc_X() = acc_x;
  out.DAcc_Y() = acc_y;
  out.DAcc_Z() = acc_z;

  out.DGyro_X() = gyro_x;
  out.DGyro_Y() = gyro_y;
  out.DGyro_Z() = gyro_z;

  return out;
}


void TDShape::Smooth()
{
  int i, j;
  svec<double> tmp;
  tmp = m_data;

  m_data.clear();
  m_data.resize(tmp.isize(), 0.);
  
  int win = 2;
  for (i=win; i<tmp.isize()-win; i++) {
    for (j=i-win; j<=i+win; j++) {
      m_data[i] += tmp[j]/(double)(2*win+1);
    }
  } 
}


void TDShape::Add(const TDShape & d)
{
  int i;
  throw;
  
  m_have++;
}

//=====================================================
void Hints::ReadDB(IMReadStream & s)
{
  int i;
  int base = m_data.isize();
  s.Read(m_frame);
  s.Read(m_samplerate);
  s.Read(m_offset);
  
  int n = 0;
  s.Read(n);
  m_data.resize(n);
  for (i=0; i<n; i++)
    s.Read(m_data[i]);

  s.Read(n);
  m_shapes.resize(n + base);
  for (i=0; i<n; i++)
    m_shapes[i + base].Read(s);
}


void Hints::WriteDB(IMWriteStream &s) const
{
  int i;
  s.Write(m_frame);
  s.Write(m_samplerate);
  s.Write(m_offset);
  
  s.Write(m_data.isize());
  
  for (i=0; i<m_data.isize(); i++)
    s.Write(m_data[i]);

  s.Write(m_shapes.isize());
  for (i=0; i<m_shapes.isize(); i++)
    m_shapes[i].Write(s);
}

void Hints::ReadDB(const string & fileName)
{
  CMReadFileStream s;
  s.Open(fileName.c_str());
  ReadDB(s);
  s.Close();
}

void Hints::WriteDB(const string & fileName) const
{
  CMWriteFileStream s;
  s.Open(fileName.c_str());
  WriteDB(s);
  s.Close();

}



void Hints::FillShapes(const BTLData & b)
{
  int i;

  for (i=0; i<m_shapes.isize(); i++) {
    if (m_shapes[i].isize() == 0)
      m_shapes[i].Fill(b);
  }
}

void Hints::Read(const string & fileName)
{
  FlatFileParser parser;
  
  parser.Open(fileName);

  // Get rid of header
  parser.ParseLine();
  bool frame = false;
  bool block = false;
  if (parser.AsString(0) == "left_frame")
    frame = true;

  bool bDoHints = true;
  bool bDoBlocks = false;
  
  while (parser.ParseLine()) {
    if (parser.GetItemCount() == 0)
      continue;
    if (strstr(parser.Line().c_str(), ".hints.db") != NULL) {
      cout << "READING Hints from " << parser.Line() << endl;
      ReadDB(parser.Line());
      continue;
    }

    if (parser.AsString(0) == "left_block") {
      bDoHints = false;
      bDoBlocks = true;
      continue;
    }

    // Blocks are in ms!!!
    if (bDoBlocks) {
      AddBlock(parser.AsInt(0), parser.AsInt(1), parser.AsString(2));
    }
    

    if (!bDoHints)
      continue;
      
    if (frame) {
      //cout << "HINT NEW: from frame " << parser.AsFloat(0) << " - frame " << parser.AsFloat(1) << ", state " << parser.AsInt(2) << endl;
      int from = (int)(parser.AsFloat(0)+0.5);
      int to = (int)(parser.AsFloat(1)+0.5);

      bool bHint = false;
      if (parser.GetItemCount() > 3)
	bHint = true;

      
      if (to >= m_data.isize())
	m_data.resize(to+1, -1);

      if (!bHint) {
	for (int i=from; i<to; i++)
	  m_data[i] = parser.AsInt(2);
      }
      
      // Reserve the time domain hints
      if (bHint || parser.AsFloat(0) - from != 0 || parser.AsFloat(1) - to != 0) {
	TDShape shape;
	//double f = 2*m_samplerate/m_frame;
	double f = m_frame/2;
	cout << "Extract time hint from " << (int)(f*parser.AsFloat(0)) << " to " <<  (int)(f*parser.AsFloat(1)) << endl;
	cout << m_samplerate << " " << m_frame << " -> " << f << endl;
	shape.Set((int)(f*parser.AsFloat(0)), (int)(f*parser.AsFloat(1)), parser.AsInt(2));
	if (parser.GetItemCount() > 3)
	  shape.SetCycle(parser.AsString(3));
	m_shapes.push_back(shape);
      }
      
    } else {
      //cout << "HINT NEW: from " << parser.AsFloat(0) << "ms - " << parser.AsFloat(1) << "ms, state " << parser.AsInt(2) << endl;
      SetInterval(parser.AsFloat(0), parser.AsFloat(1), parser.AsInt(2));
    }
  }
}

void Hints::FromReps(const BTLData &d)
{
  for (int i=0; i<d.EXG().isize(); i++) {
    SetInterval(d.EXG()[i].Time(), d.EXG()[i].Time(), d.EXG()[i].Rep());
  }
}

void Hints::SetInterval(double from, double to, int state)
{
  // Does not have to start at 0!
  from -= m_offset;
  to -= m_offset;
  
  double delta = 1/m_samplerate;

  for (double t=from; t<=to; t+=delta) {
    double d = (t-delta*(double)m_frame/4);
    //cout << "HINT (1) " << d << endl;
    //d *= delta;
    //cout << "HINT (2) " << d << endl;
    d *= (double)m_samplerate/(double)(m_frame/2);
    //cout << "HINT (3) " << d << endl;
    int idx = (int)d;
    // Inefficient!!!!
    if (idx >= m_data.isize())
      m_data.resize(idx+1, -1);
    if (idx < 0)
      continue;
    m_data[idx] = state;

    //cout << "HINT " << from << " " << to << " " << " state: " << state << " idx: " << idx << endl;
  }
}
