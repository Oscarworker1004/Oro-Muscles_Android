#include "eeg/BTLData.h"
#include "base/FileParser.h"
#include "signal/Smooth.h"
#include "base/StringUtil.h"
#include "src/UnivData.h"

void BTLData::GetEXGData(svec<double> & d)
{
  int i;
  d.resize(m_exg.isize(), 0.);

  for (i=0; i<m_exg.isize(); i++)
    d[i] = m_exg[i].Signal();
}

void BTLData::GetIMUData(svec < svec < double > > & d)
{
  int i, j;
  d.resize(m_acc.isize());

  for (i=0; i<m_acc.isize(); i++) {
    svec<double> & s = d[i];
    s.resize(m_acc[i].isize() + m_gyro[i].isize(), 0.);
    //cout << s.isize() << " " << i << endl;
    for (j=0; j<m_acc[i].isize(); j++) {
      //cout << "j: " << j << endl;
      s[j] = m_acc[i][j];
    }
    for (j=0; j<m_gyro[i].isize(); j++) {
      //cout << "j2: " << j << endl;
      s[j + m_acc[i].isize()] = m_gyro[i][j];
    }
  }
}

double BAbs(double d)
{
  if (d < 0.)
    return -d;
  return d;
}

void BTLData::FillAccData(svec<double> & d, int idx) const
{
  d.resize(m_acc.isize());
  for (int i=0; i<m_acc.isize(); i++)
    d[i] = m_acc[i][idx];
}

void BTLData::FillGyrData(svec<double> & d, int idx) const
{
  d.resize(m_gyro.isize());
  for (int i=0; i<m_gyro.isize(); i++)
    d[i] = m_gyro[i][idx];
}

//=================================================================

void BTLData::SpikeFilter()
{
  int i, j;

  int doSpike = 0;
  m_pCfg->Update("artifact-spike", doSpike);

  if (doSpike > 0) {
    for (i=1; i<m_exg.isize()-1; i++) {
      if (m_exg[i-1].Signal() * m_exg[i].Signal() < 0 && m_exg[i+1].Signal() * m_exg[i].Signal() < 0) {
	cout << "Artifact at " << i << endl;
	m_exg[i].Signal() = 0.;
	m_exg[i].RMS() = 0.;
      }      
    }
  }
  
  int start = 0;
  double thresh = 1500.;

  m_pCfg->Update("artifact-hi-thresh", thresh);

  
  
  m_exg[0].Signal() = 0.;

  int win = 10;
  for (i=win+1; i<m_exg.isize()-win-1; i++) {
    if (BAbs(m_exg[i].Signal()) > thresh) {
      if (BAbs(m_exg[i-1].Signal()) <= thresh) {
	start = i-10;
	if (start < 0)
	  start = 0;	
      }
    }
      
    if (BAbs(m_exg[i].Signal()) <= thresh) {
      if (BAbs(m_exg[i-1].Signal()) > thresh) {
	int stop = i+10;
	if (stop > m_exg.isize())
	  stop = m_exg.isize();
	for (j=start; j<stop; j++) {
	  m_exg[j].Signal() = 0.;
	  m_exg[j].RMS() = 0.;
	}
      }
    }
    /*   if (BAbs(m_exg[i].Signal()-m_exg[i-1].Signal()) > 700) {
      m_exg[i].Signal() = 0.;
      m_exg[i].RMS() = 0.;
      }*/
  
    
  }

  int artTimeout = 1000;

  m_pCfg->Update("artifact-idle-thresh", artTimeout);
  artTimeout /= 2;
  int nothing = 0;
  int kill = 0;
  for (i=0; i<m_exg.isize(); i++) {
    //m_exg[i].Signal() = 0.;
    //m_exg[i].RMS() = 0.;
    if (kill > 0) {
      cout << "Kill frame " << (double)i/500 << endl;
      m_exg[i].Signal() = 0.;
      m_exg[i].RMS() = 0.;
      kill--;
      continue;
    }
    if (m_exg[i].Signal() == 0. && m_exg[i].RMS() == 0) {
      nothing++;
    } else {
      if (nothing > 250) {
	kill = artTimeout;
	cout << "*** Kill signal: " << nothing << " at frame " << i << endl;
	m_exg[i].Signal() = 0.;
	m_exg[i].RMS() = 0.;
      }
      nothing = 0;
    }
  }

  double lowFreq = 50.;

  m_pCfg->Update("artifact-low-pass", lowFreq);
  int min = 500/(lowFreq + 0.0001);

  cout << "Freq min: " << min << endl;
  int count = 0;
  for (i=1; i<m_exg.isize(); i++) {
    if (m_exg[i].Signal()*m_exg[i-1].Signal() > 0) {
      count++;
    } else {
      if (count > min) {
	cout << "Low pass zero at: " << i-count << " to " << i << endl; 
	for (j=i-count; j<i; j++) {
	  //cout << "To 0 " << 
	  m_exg[j].Signal() = 0.;
	  m_exg[j].RMS() = 0.;
	}
      }
      count = 0;
    }
  }
  
}

//=================================================================
void BTLData::Read(const string & fileName)
{
  StringParser p;
  bool bSmooth = false;
  string pre, post;
  p.SetLine(fileName, ".Acc.");
  if (p.GetItemCount() > 1) {
    pre = p.AsString(0);
    post = p.AsString(1);
  }
  p.SetLine(fileName, ".Exg.");
  if (p.GetItemCount() > 1) {
    pre = p.AsString(0);
    post = p.AsString(1);
  }
  p.SetLine(fileName, ".Gyro.");
  if (p.GetItemCount() > 1) {
    pre = p.AsString(0);
    post = p.AsString(1);
  }

  if (pre != "") {
    string n = pre + ".Exg." + post;
    if (ReadInt(n))
      bSmooth = true;
    n = pre + ".Acc." + post;
    if (ReadInt(n))
      bSmooth = true;
    n = pre + ".Gyro." + post;
    if (ReadInt(n))
      bSmooth = true;
      
  } else {  
    bSmooth = ReadInt(fileName);    
  }

  if (bSmooth)
    PostProcess();
}

bool BTLData::ReadIntUniv(const string & fileName)
{
  return false;
}

void BTLData::WriteIntUniv(const string & fileName) const
{
  
}

bool BTLData::ReadInt(const string & fileName)
{

  UnivDataRead ur;
  if (ur.OpenFile(fileName, true)) {
    return ReadIntUniv(fileName);
  }
  
  int i;
  
  FlatFileParser parser;

  parser.AddDelimiter(" ");
  parser.AddDelimiter(",");
  
  parser.Open(fileName);

  bool bExg = false;
  bool bAcc = false;
  bool bGyro = false;
  bool bSpec = false;
  int k = 0;
  bool bSmooth = true;

  double startTime = -1.;
  
  while (parser.ParseLine()) {
    if (parser.GetItemCount() < 2)
      continue;
    if (parser.AsString(1) == "Data") {
      //cout << "Head" << endl;
      bExg = bAcc = bGyro = false;
      if (parser.AsString(0) == "EXG") {
	bExg = true;
	m_headEXG = parser.Line();
	if (parser.AsString(parser.GetItemCount()-1) == "processed") {
	  bSmooth = false;
	} else {
	  m_headEXG = parser.Line() + " processed";
	}
	m_headEXG += "\n";
	parser.ParseLine();
	m_headEXG += parser.Line();
	startTime = -1.;
      }
      if (parser.AsString(0) == "ACCEL") {
	bAcc = true;
	m_headAcc = parser.Line() + "\n";
	parser.ParseLine();
	m_headAcc += parser.Line();
	startTime = -1.;
      }
      if (parser.AsString(0) == "GYRO") {
	bGyro = true;
	m_headGyro = parser.Line() + "\n";
	parser.ParseLine();
	m_headGyro += parser.Line();
	startTime = -1.;
      }
      if (parser.AsString(0) == "SPECTRA") {
	bSpec = true;
	//m_headGyro = parser.Line() + "\n";
	parser.ParseLine();
	//m_headGyro += parser.Line();
	startTime = -1.;
      }
      continue;
    }


    if (bExg) {
      EXGData d;
      //cout << parser.Line() << endl;
      if (startTime < 0.)
	startTime = parser.AsFloat(0);
      d.Time() = parser.AsFloat(0) - startTime;
      
      if (parser.GetItemCount() > 5)
	d.Rep() = parser.AsInt(5);
      if (parser.GetItemCount() > 6) {
	d.State() = parser.AsInt(6);
	//bSmooth = false; // NO Smoothing
      }
      if (parser.GetItemCount() > 7)
	d.Cycle() = parser.AsString(7);

      k++;
      if (k < 0) {
	d.Signal() = 0.;
	d.RMS() = 0.;
      } else {
	d.Signal() = parser.AsFloat(1);
	d.RMS() = parser.AsFloat(2);
	d.MDF() = parser.AsFloat(3);
	d.MNF() = parser.AsFloat(4);
      }

      // DEBUG!!!!!!!!!!!!!!!!!!!!!!!!!
      //if (m_exg.isize() == 813) {
      //cout << "READ " << d.RMS() << " " << parser.AsFloat(2) << endl;
      //}
      
      for (i=3; i<5; i++)
	d.Raw() += parser.AsString(i) + " ";

      if (((int)(d.Time()*1000+0.5)) % 2 == 0) {      
	m_exg.push_back(d);
      } else {
	//cout << "Skipping: " << d.Time() << " " << (int)(d.Time()*1000) << endl;
      }
    }

    if (bAcc) {
      IMUData d;
      d.resize(3);
      //cout << "Acc" << endl;
      if (startTime < 0.)
	startTime = parser.AsFloat(0);
      d.Time() = parser.AsFloat(0) - startTime;
      d[0] = parser.AsFloat(1);
      d[1] = parser.AsFloat(2);
      d[2] = parser.AsFloat(3);

      if (parser.GetItemCount() > 4)
	d.Rep() = parser.AsInt(4);
      if (parser.GetItemCount() > 5)
	d.State() = parser.AsInt(5);
      if (parser.GetItemCount() > 6)
	d.Processed() = parser.AsFloat(6);
      
      m_acc.push_back(d);
    }
    
    if (bGyro) {
      IMUData d;
      d.resize(3);
      if (startTime < 0.)
	startTime = parser.AsFloat(0);
      d.Time() = parser.AsFloat(0) - startTime;
      d[0] = parser.AsFloat(1);
      d[1] = parser.AsFloat(2);
      d[2] = parser.AsFloat(3);

      if (parser.GetItemCount() > 4)
	d.Rep() = parser.AsInt(4);
      if (parser.GetItemCount() > 5)
	d.State() = parser.AsInt(5);

      if (parser.GetItemCount() > 6)
	d.Processed() = parser.AsFloat(6);
      
      m_gyro.push_back(d);
    }
    
    if (bSpec) {
      IMUData d;
      d.resize(parser.GetItemCount()-1);

      if (startTime < 0.)
	startTime = parser.AsFloat(0);
    
      d.Time() = parser.AsFloat(0) - startTime;

      for (int j=1; j<parser.GetItemCount(); j++)
	d.push_back(parser.AsFloat(i));
      
      m_spec.push_back(d);
    }
   
  }

  return bSmooth;
}
  //cout << "DEBUG " << EXG()[813].RMS() << endl;
  //SpikeFilter();

  //cout << "DEBUG " << EXG()[813].RMS() << endl;

void BTLData::PostProcess()
{  
  int i;
  
  svec<double> d;
  GetRMS(d, 0, EXG().isize());
  
  int num = 6;
  m_pCfg->Update("ZK-rounds", num);
  
  double decay = 1.;
  int win = 40;
  double range = 1;
  m_pCfg->Update("ZK-decay", decay);
  m_pCfg->Update("ZK-win", win);
  m_pCfg->Update("ZK-range", range);
  
  cout << "ZK-rounds: " << num << endl;
  cout << "ZK-decay:  " << decay << endl;
  cout << "ZK-win:    " << win << endl;
  cout << "ZK-range:  " << range << endl;
  
  Smooth s;
  s.SetDecay(decay);
  s.SetWin(win);
  
  cout << "ZK Smooth" << endl;
  
  for (i=0; i<num; i++)
    s.ApplyZK(d, range);
  
  cout << "Copy back" << endl;
  for (i=0; i<d.isize(); i++) {
    //cout << "Old: " << EXG()[i].RMS() << " new: " << d[i] << endl;
    EXG()[i].RMS() = d[i];
  }


  int doSpike = 0;
  m_pCfg->Update("apply-artifact-filter", doSpike);

  if (doSpike > 0)
    SpikeFilter();
}

void BTLData::SmoothIMU()
{
  //============================ IMU ===================================
  int i;
  Smooth s;
  svec<double> d;
  
  GetACC(d, 0, Acc().isize());
  
  int num = 1;
  m_pCfg->Update("ZK-rounds-acc", num);
  
  double decay = 1.;
  int win = 10;
  double range = 0.4;
  double mult = 1;
  
  m_pCfg->Update("ZK-decay-acc", decay);
  m_pCfg->Update("ZK-win-acc", win);
  m_pCfg->Update("ZK-range-acc", range);
  m_pCfg->Update("ZK-mult-acc", mult);
  
  cout << "ZK-rounds: " << num << endl;
  cout << "ZK-decay:  " << decay << endl;
  cout << "ZK-win:    " << win << endl;
  cout << "ZK-range:  " << range << endl;
  cout << "ZK-mult:   " << mult << endl;
  
  
  s.SetDecay(decay);
  s.SetWin(win);
  s.SetMult(mult);
  
  cout << "ZK Smooth (acc)" << endl;
  
  for (i=0; i<num; i++)
    s.ApplyZK(d, range);
  
  cout << "Copy back" << endl;
  for (i=0; i<d.isize(); i++) {
    //cout << "Old: " << EXG()[i].RMS() << " new: " << d[i] << endl;
    Acc()[i].Processed() = d[i];
  }
  
  // Gyro
  GetGyro(d, 0, Gyro().isize());
  
  num = 0;
  m_pCfg->Update("ZK-rounds-gyro", num);
  
  decay = 1.;
  win = 10;
  range = 0.5;
  num = 1;
  m_pCfg->Update("ZK-rounds-num", num);
  m_pCfg->Update("ZK-decay-gyro", decay);
  m_pCfg->Update("ZK-win-gyro", win);
  m_pCfg->Update("ZK-range-gyro", range);
  m_pCfg->Update("ZK-mult-gyro", mult);
  
  cout << "ZK-rounds: " << num << endl;
  cout << "ZK-decay:  " << decay << endl;
  cout << "ZK-win:    " << win << endl;
  cout << "ZK-range:  " << range << endl;
  cout << "ZK-mult:   " << mult << endl;
  
  
  s.SetDecay(decay);
  s.SetWin(win);
  s.SetMult(mult);
  
  cout << "ZK Smooth (gyro)" << endl;
  
  for (i=0; i<num; i++)
    s.ApplyZK(d, range);
  
  cout << "Copy back" << endl;
  for (i=0; i<d.isize(); i++) {
      //cout << "Old: " << EXG()[i].RMS() << " new: " << d[i] << endl;
    Gyro()[i].Processed() = d[i];
  }
  
}

void BTLData::AutoAnnotate()
{
  svec<int> from;
  svec<int> to;
  int i, j;
  
  for (i=1; i<m_exg.isize(); i++) {
    if (m_exg[i-1].State() <= 2 && m_exg[i].State() > 2) {
      from.push_back(i);
    }
   if (m_exg[i-1].State() > 2 && m_exg[i].State() <= 2) {
      to.push_back(i);
    }
  }

  int k = 0;
  for (i=0; i<to.isize(); i++) {
    int good = 0;
    for (j=from[i]; j<to[i]; j++) {
      if (m_exg[j].State() >= 4)
	good++;
    }

    if (good > 20) {
      string name = "auto_" + Stringify(k);
      k++;
      for (j=from[i]; j<to[i]; j++) {
	m_exg[j].Cycle() = name;
      }
    }
  }
  
}


void BTLData::Write(const string & fileName) const
{
  
  int i, j;

  FILE * p = fopen(fileName.c_str(), "w");

  fprintf(p, "%s\n", m_headEXG.c_str());
  for (i=0; i<m_exg.isize(); i++) {
    if (m_exg[i].Cycle() != "") {
      fprintf(p, "%f, %f, %f, %s%d, %d, %s\n", m_exg[i].Time(), m_exg[i].Signal(), m_exg[i].RMS(), m_exg[i].Raw().c_str(), m_exg[i].Rep(), m_exg[i].State(), m_exg[i].Cycle().c_str());
    } else {
      fprintf(p, "%f, %f, %f, %s%d, %d\n", m_exg[i].Time(), m_exg[i].Signal(), m_exg[i].RMS(), m_exg[i].Raw().c_str(), m_exg[i].Rep(), m_exg[i].State());
    }
  }

  fprintf(p, "\n%s\n", m_headAcc.c_str());
  for (i=0; i<m_acc.isize(); i++) {
    fprintf(p, "%f, %f, %f, %f, %d, %d %f\n", m_acc[i].Time(),
	    m_acc[i][0], m_acc[i][1], m_acc[i][2],
	    m_acc[i].Rep(), m_acc[i].State(), m_acc[i].Processed());
  }

  
  fprintf(p, "\n%s\n", m_headGyro.c_str());
  for (i=0; i<m_gyro.isize(); i++) {
    fprintf(p, "%f, %f, %f, %f, %d, %d %f\n", m_gyro[i].Time(),
	    m_gyro[i][0], m_gyro[i][1], m_gyro[i][2],
	    m_gyro[i].Rep(), m_gyro[i].State(), m_gyro[i].Processed());
  }

  //cout << "SPECTRA Size=" << m_spec.isize() << endl;
  if (m_spec.isize() > 0) {
    fprintf(p, "\nSPECTRA Data\n");
    fprintf(p, "X, 0, 1, 2, 3, 4, 5, 6, 7, Rep, State\n");
    for (i=0; i<m_spec.isize(); i++) {
      fprintf(p, "%f", m_spec[i].Time());
      for (j=0; j<m_spec[i].isize(); j++)      
	fprintf(p, ", %f", (m_spec[i])[j]);
      
      fprintf(p, ", %d, %d\n", m_spec[i].Rep(), m_spec[i].State());
    }
  }

  fclose(p);
}
