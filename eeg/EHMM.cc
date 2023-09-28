#include "eeg/EHMM.h"
#include "base/FileParser.h"
#include "base/StringUtil.h"

void Read(svec<HMMVec> & d, const string & fileName)
{
  FlatFileParser parser;
  
  parser.Open(fileName);

  while (parser.ParseLine()) {
    if (parser.GetItemCount() == 0)
      continue;
    HMMVec tmp;
    tmp.Label() = parser.AsString(0);

    for (int i=1; i<parser.GetItemCount(); i++)
      tmp.push_back(parser.AsFloat(i));
    d.push_back(tmp);
  }
}

void Write(const svec<HMMVec> & d, const string & fileName)
{
  FILE * p = fopen(fileName.c_str(), "w");

  int i, j;

  for (i=0; i<d.isize(); i++) {
    fprintf(p, "%s", d[i].Label().c_str());
    for (j=0; j<d[i].isize(); j++) {
      fprintf(p, " %f", (d[i])[j]);
    }
    fprintf(p, "\n");
  }

  fclose(p);
}

void PenaltyMatrix::Read(const string & fileName)
{
  FlatFileParser parser;
  
  parser.Open(fileName);

  while (parser.ParseLine()) {
    if (parser.GetItemCount() == 0)
      continue;
    svec<double> s;   

    for (int i=0; i<parser.GetItemCount(); i++)
      s.push_back(parser.AsFloat(i));
    m_data.push_back(s);
  }
  

}

void PenaltyMatrix::Write(const string & fileName) const
{
  FILE * p = fopen(fileName.c_str(), "r");

  int i, j;

  for (i=0; i<m_data.isize(); i++) {
    for (j=0; j<m_data[i].isize(); j++) {
      fprintf(p, " %f", (m_data[i])[j]);
    }
    fprintf(p, "\n");
  }

  fclose(p);
}

void EHMM::Setup(int frames, int dim)
{

  if (m_data.isize() == 0) {
    m_data.resize(1);
    m_data[0].resize(dim);
    m_data[0].Label() = "Self";
    m_tb.resize(1);

 
  }

  // return; ????

  for (int i=0; i<m_tb.isize(); i++) {
    // One for the start
    m_tb[i].resize(frames+1);
  }

  Reset();
}

void EHMM::Reset()
{
  for (int i=0; i<m_tb.isize(); i++) {
    (m_tb[i])[0] = 0.;
    for (int j=1; j<m_tb[i].isize(); j++) {
      (m_tb[i])[j] = -1.;
      m_tb[i].SetPrev(j, -1); // SHIFT
    }
  }
}

void EHMM::BackTrace(svec<int> & tb)
{
  int i, j;

  int state = 0;
  double min = (m_tb[0])[m_tb[0].isize()-1];
  
  for (i=0; i<m_tb.isize(); i++) {
    if ((m_tb[i])[m_tb[i].isize()-1] <= min) {
      state = i;
      min = (m_tb[i])[m_tb[i].isize()-1];
    }
  }

  //cout << "Min " << min << " state: " << state << endl;
  
  tb.clear();
  tb.resize(m_tb[0].isize(), -1);

  if (m_bStupid) {
    for (i=m_tb[0].isize()-1; i>=0; i--) {
      double local = min;
      int next = -1;
      //cout << "TB State: " << state << " index " << i << " back:" << i-1 << endl;
      for (j=0; j<m_tb.isize(); j++) {
	//cout << "  frame " << i << " score " << j << ": " << (m_tb[j])[i] << endl;
	double d = (m_tb[j])[i];
	if (d <= local) {
	  local = d;
	  next = j;
	}
      }
      
      //tb[m_tb[0].isize()-1-i] = state;   
      tb[i] = state;   
      state = next;
    }
  } else { // not stupid
    for (i=m_tb[0].isize()-1; i>=0; i--) {
      tb[i] = state;
      state = m_tb[state].Prev(i); 
    }

  }

  tb.resize(tb.isize()-1);
  /* cout << "Print TB:" << endl;

  for (i=0; i<tb.isize(); i++) {
    cout << 0.5*(double)i;
    if (i%2 == 0)
      cout << ".0";
    cout << ": " << tb[i] << endl;
  }
  cout << "TB done" << endl;*/
}


void EHMM::Feed(int frame, const HMMVec & d)
{
  for (int i=0; i<m_tb.isize(); i++) {
    //cout << "Feeding frame " << frame << " state " << i << ": " << (m_tb[i])[frame] << " - " <<  m_data[i].Dist(d) << " -> "<< (m_tb[i])[frame] + m_data[i].Dist(d) << endl;
    double plus = m_data[i].Dist(d);
    for (int j=0; j<m_tb.isize(); j++) {
      // Use values from the matrix
      double pen = m_matrix.Get(i, j);
      //pen = m_pen;
      if (j == i) // Stay as long as you like
	pen = 0.;
      
      if ((m_tb[j])[frame+1] < 0. || (m_tb[i])[frame] + plus + pen < (m_tb[j])[frame+1]) {
	(m_tb[j])[frame+1] = (m_tb[i])[frame] + plus + pen;
	(m_tb[j]).SetPrev(frame+1, i); 
      }
     
      //(m_tb[i])[frame+1] = (m_tb[i])[frame] + m_data[i].Dist(d);
    }
  }
}


void EHMM::DynProg(svec<int> & tb, const svec<HMMVec> & d)
{
  int i;

  //cout << "Reset" << endl;
  Reset();


  
  for (int i=0; i<d.isize(); i++) {
    //cout << "Feed frame " << i << endl;
    Feed(i, d[i]);
  }


  //cout << "Backtrace" << endl;
  BackTrace(tb);

  if (m_hints.isize() > 0) {
    for (i=0; i<m_hints.isize(); i++) {
      //cout << "CHECK" << endl;
      if (i >= tb.isize())
	break;
      //tb[i] = 0;
      if (m_hints[i] >= 0) {
	tb[i] = m_hints[i];
	cout << "Override w/ hint @ " << i << " -> " << tb[i] << endl; 
      }
    }
  }

}

void EHMM::Process(const svec<HMMVec> & d, bool bSplit)
{
  int i;
  svec<int> tb;

  //cout << "Backtrace" << endl;
  DynProg(tb, d);

  
  /* Moved to DynProg
  if (m_hints.isize() > 0) {
    for (i=0; i<m_hints.isize(); i++) {
      //cout << "CHECK" << endl;
      if (i >= tb.isize())
	break;
      //tb[i] = 0;
      if (m_hints[i] >= 0) {
	tb[i] = m_hints[i];
	cout << "Override w/ hint @ " << i << " -> " << tb[i] << endl; 
      }
    }
    }*/
  
  svec<double> div;
  div.resize(m_data.isize(), 1.);

  svec<ScorePair> pp;

  svec<HMMVec> orig;
  orig = m_data;
  
  //cout << "Update model" << endl;

  svec<double> mdist;
  svec<double> mcount;

  mdist.resize(m_data.isize(), 0.);
  mcount.resize(m_data.isize(), 0);
  
  for (i=0; i<d.isize(); i++) {
    m_data[tb[i]] += d[i];
    div[tb[i]] += 1.;
    ScorePair pair;

    // ONLY split from background!!!
    //if (tb[i] > 0)
    //continue;

    
    //pair.score = orig[tb[i]].Dist(d[i]);
    pair.score = orig[tb[i]].BhattDist(d[i]);

    mdist[tb[i]] += pair.score;
    mcount[tb[i]]++;
    
    pair.index = i;
    pp.push_back(pair);
  }

  // Update/normalize the models
  for (i=0; i<m_data.isize(); i++) {
    m_data[i] /= div[i];
  }

  /*
  for (int j=0; j<m_data.isize(); j++) {
    cout << "Print state " << j << endl;
    for (i=0; i<m_data[j].isize(); i++)
      cout << (m_data[j])[i] << endl;
      }*/

  Sort(m_data);
  
  if (!bSplit)
    return;

  //cout << "Checkpoint" << endl;
  
  Sort(pp);

  double max = 0.;
  int toSplit = 0;
  int minFreq = 20; // HARD CODED!!!!
 
  for (i=0; i<mdist.isize(); i++) {
    if (mcount[i] > minFreq) {
      double d = mdist[i]/(double)mcount[i];
      //cout << "Model " << i << " score: " << d << " count: " << mcount[i] << " raw: " << mdist[i];
      //cout << " amplitude: " << m_data[i].Amplitude() << endl;
      if (d > max) {
	max = d;
	toSplit = i;
      }
    }
  }

  //toSplit = 0;
  
  int half = 1 + mcount[toSplit]/4;

  //cout << "Splitting: " << toSplit << " using " << half << " frames" << endl;
  
  //for (i=0; i<pp.isize(); i++)
  // cout << "DIST " << pp[i].index << " " << pp[i].score << endl;

  
  
  HMMVec extra;
  extra.resize(m_data[0].isize(), 0.);

  cout << "Split" << endl;
  double count = 0.;
  for (i=0; i<pp.isize(); i++) {
    if (tb[pp[i].index] != toSplit) // Only count frames from toSplit 
      continue;
    //cout << "DIST " << pp[i].index << " " << pp[i].score << " -> " << tb[pp[i].index] << endl;
    extra += d[pp[i].index];
    count += 1.;
    if (count >= half)
      break;
    
  }

  extra.Label() = m_data[toSplit].Label() + "_" + Stringify(m_data.isize());

  extra /= count;

  m_data.push_back(extra);

  TraceBack add;
  add.resize(m_tb[0].isize(), 0.);

  m_tb.push_back(add);


}
