#include "eeg/TimeRefine.h"
#include "base/StringUtil.h"
#include "eeg/IMUTrans.h"
#include <math.h>

int Abs(int a)
{
  if (a < 0)
    return -a;
  return a;
}

double Mag(double x, double y, double z)
{
  return sqrt(x*x + y*y + z*z);
}

double DistDot(double x1, double y1, double z1, double x2, double y2, double z2, double weight)
{
  double d = x1*x2 + y1*y2 + z1 * z2;
  d *= weight;

  return d;
}

double DistEuc(double x1, double y1, double z1, double x2, double y2, double z2, double weight)
{
  double d = (x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2) + (z1 - z2)*(z1 - z2);
  d = exp(-d/weight); // no sqrt!

  return d;
}

double DistGen(double x1, double y1, double z1, double x2, double y2, double z2, double weight)
{
  return DistDot(x1, y1, z1, x2, y2, z2, weight);
  //return DistEuc(x1, y1, z1, x2, y2, z2, weight);
}

double TimeRefine::Dot(const BTLData & d, int from, const TDShape & s, bool bPrint) const
{
  int i;
  double dist = 0.;

  // Values don't matter, fix below!!!
  double weight_exg = 0.;
  double weight_acc = 100.;
  double weight_gyr = 1.;

  svec<double> w_acc;
  svec<double> w_gyr;
  //cout << weight_acc << " " << weight_gyr << endl;
  
  m_pCfg->Update("dot_weight_exg", weight_exg);
  m_pCfg->Update("dot_weight_acc", weight_acc);
  m_pCfg->Update("dot_weight_gyr", weight_gyr);
  //cout << weight_acc << " " << weight_gyr << endl;

  w_acc.resize(3, weight_acc);
  w_gyr.resize(3, weight_gyr);

  m_pCfg->Update("dot_weight_acc_X", w_acc[0]);
  m_pCfg->Update("dot_weight_acc_Y", w_acc[1]);
  m_pCfg->Update("dot_weight_acc_Z", w_acc[2]);
  m_pCfg->Update("dot_weight_gyr_X", w_gyr[0]);
  m_pCfg->Update("dot_weight_gyr_Y", w_gyr[1]);
  m_pCfg->Update("dot_weight_gyr_Z", w_gyr[2]);


  double weight_acc_mag = 0.;
  double weight_gyr_mag = 0.;

  m_pCfg->Update("dot_weight_acc_mag", weight_acc_mag);
  m_pCfg->Update("dot_weight_gyr_mag", weight_gyr_mag);

  
  if (bPrint)
    cout << "Processing " << from << endl;

  double last = 0.;
  for (i=0; i<s.isize(); i++) {
    // *************************************** HARD CODED RATE ******************

    int j = i/5;

    
    // !!!!!!!!!!!!!!!!!!!!!! -> necessary???
    if (i+from < 0 || i+from >= d.EXG().isize())
      continue;

    int jj = (i+from)/5;
    // MGG - TEST
    //dist += s[i] * d.EXG()[i+from].RMS() * weight_exg;
    dist += DistGen(s[i], 0., 0., d.EXG()[i+from].RMS(), 0., 0., weight_exg);
    
    if (bPrint)
      cout << "SHAPE " << j << " " << jj << " " << i << " " << d.Acc().isize() << " " << d.Gyro().isize() << endl;
    
    if (jj >= d.Acc().isize() || jj >= d.Gyro().isize())
      break;
    if (j >= s.isize())
      break;
    
    // MGG - TEST
    //dist += s.Acc_X(j) * d.Acc()[jj][0] * w_acc[0];
    //dist += s.Acc_Y(j) * d.Acc()[jj][1] * w_acc[1];
    //dist += s.Acc_Z(j) * d.Acc()[jj][2] * w_acc[2];
    dist += DistGen(s.Acc_X(j), s.Acc_Y(j), s.Acc_Z(j), d.Acc()[jj][0], d.Acc()[jj][1], d.Acc()[jj][2], weight_acc);
    
    // MGG - TEST
    //dist += s.Gyro_X(j) * d.Gyro()[jj][0] * w_gyr[0];
    //dist += s.Gyro_Y(j) * d.Gyro()[jj][1] * w_gyr[1];
    //dist += s.Gyro_Z(j) * d.Gyro()[jj][2] * w_gyr[2];
    dist += DistGen(s.Gyro_X(j), s.Gyro_Y(j), s.Gyro_Z(j), d.Gyro()[jj][0], d.Gyro()[jj][1], d.Gyro()[jj][2], weight_gyr);

    // MGG - TEST -> DISABLE
    //dist += Mag(s.Gyro_X(j), s.Gyro_Y(j), s.Gyro_Z(j)) * Mag(d.Gyro()[jj][0], d.Gyro()[jj][1], d.Gyro()[jj][2]) * weight_gyr_mag;
    //dist += Mag(s.Acc_X(j), s.Acc_Y(j), s.Acc_Z(j)) * Mag(d.Acc()[jj][0], d.Acc()[jj][1], d.Acc()[jj][2]) * weight_acc_mag;
    

 
    if (bPrint) {
      cout << dist - last << endl;
      last = dist;
    }
  }
 
  dist /= (double)s.isize();
  return dist;
}


void ExtractEMGAnnot(svec<IMUInterval> & iv, const svec<EXGData> & emg)
{
  int i;
  int start = 0;
  for (i=1; i<emg.isize(); i++) { 
    if (emg[i].State() >= 2 && emg[i-1].State() < 2)
      start = i*2;
    if (emg[i].State() < 2 && emg[i-1].State() >= 2) {
      IMUInterval v;
      v.Set(start, i*2);
      v.Name() = "EXG";
      iv.push_back(v);
    }
    
  }
}

bool HasAnnot(const svec<EXGData> & emg)
{
  int i;

  for (i=0; i<emg.isize(); i++) { 
    if (emg[i].Cycle() != "")
      return true;
  }
  return false;
}

void ExtractHintAnnot(svec<IMUInterval> & iv, const svec<EXGData> & emg)
{
  int i;
  int start = 0;
  string name;
  for (i=1; i<emg.isize(); i++) { 
    if (emg[i].Cycle() != "" && emg[i-1].Cycle() == "") {
      start = i*2;
      name = emg[i].Cycle();
      //cerr << "Hint: " << emg[i].Cycle() << endl;
    }
    if (emg[i].Cycle() == "" && emg[i-1].Cycle() != "") {
      IMUInterval v;
      v.Set(start, i*2);
      v.Name() = name;
      iv.push_back(v);
    }
    
  }
}


void TimeRefine::SetupSensor(IMUSensor & sens, const BTLData & d) const
{
  sens.resize(2);
  sens[0].resize(3);
  sens[1].resize(3);

  cout << "Setup sensor: " << endl;
  d.FillAccData(sens[0][0].data(), 0);
  d.FillAccData(sens[0][1].data(), 1);
  d.FillAccData(sens[0][2].data(), 2);
  d.FillGyrData(sens[1][0].data(), 0);
  d.FillGyrData(sens[1][1].data(), 1);
  d.FillGyrData(sens[1][2].data(), 2);

  
  cout << "Normalize" << endl;
  sens.Normalize();

}

bool TimeRefine::IsValid(int start, int stop, const BTLData & d) const
{

  int minBlock = 2000;
  //  if (stop - start < minBlock)
  //return false;

  int i;

  int startC = -1;
  int stopC = -1;

  int n = 0;
  string c;

  for (i=0; i<d.EXG().isize(); i++) {
    const EXGData & ex = d.EXG()[i];
    if (ex.Cycle() != "") {
      if (startC == -1) {
	startC = (int)(ex.Time()*1000);
	c = ex.Cycle();
      }
      stopC = (int)(ex.Time()*1000);
    }
    if (ex.Cycle() == "" && startC != -1) {
      if(startC >= start && stopC <= stop)
	n++;
      startC = -1;
      stopC = -1;

    }
  }

  //cout << "FOUND Cycles " << n << endl;
  
  if (n > 0)
    return true;
  return false;
}

void TimeRefine::ApplySets(Hints & h, const BTLData & d) const
{
  int i, j;

  svec<double> data;

  cout << "NUM BLOCKS: " << h.NumBlocks() << endl;

  if (h.NumBlocks() > 0) {
    //cout << "NUM BLOCKS: " << h.NumBlocks() << endl;
    return;
  }
  
  data.resize(d.EXG().isize()/5+1, 0.);
  for (i=5; i<d.EXG().isize()-5; i+=5) {
    data[i/5-1] += d.EXG()[i].RMS()/3.;
    data[i/5] += d.EXG()[i].RMS()/3.;
    data[i/5+1] += d.EXG()[i].RMS()/3.;
  }

  //cout << "DEBUG " << d.EXG().isize()/500*1000 << " " << data.isize() << endl;
  
  double thresh = 10.;
  int start = 0;
  int stop = -1;
  int n = 0;
  int min10 = 100;
  int count = 0;


  /*
  FILE * p = fopen("all", "w");

  for (i=0; i<data.isize(); i++) {
    fprintf(p, "%f\n", data[i]);
  }

  fclose(p);
  */
  
  
  for (i=0; i<data.isize()-1; i++) {
    if (data[i] > thresh) {
      //cout << "Add frame " << 10*i << " DATA " << data[i] << endl;
    } else {
      //cout << "Add frame " << 10*i << " SILENCE " << data[i] << endl;
    }
    
    if (data[i] > thresh /*&& data[i-1] > thresh && data[i-2] > thresh*/) {
      if (n > min10) {
	// More padding?
	int off = 20;
	if (off > i)
	  off = i;
	start = 10*(i-off);
      }
      //cout << "Add detect start " << 10*i << " " << start << endl;
      n = 0;
      stop = -1;
    } else {
      n++;
      
      if (n > min10 && stop == -1) {
	stop = 10*(i-min10);
	string name = "AutoSet_";
	name += Stringify(count+1);
	if (IsValid(start, stop, d)) {
	  h.AddBlock(start, stop , name);
	  count++;
	  cout << "Adding  " << start << " - " << stop << endl;
	}
      }
    }
  }

  if (stop == -1) {
    stop = 10*(i-1);
    //cout << "Adding last " << start << " - " << stop << endl;
    string name = "AutoSet_";
    name += Stringify(count+1);
    if (IsValid(start, stop, d)) {
      h.AddBlock(start, stop , name);
      // cout << "Added" << endl;
      cout << "Adding last " << start << " - " << stop << endl;
    }
  }
  cout << "Exiting" << endl;
  /*
  int max = 0;
  
  for (i=0; i<d.EXG().isize(); i++) {
    double x = d.EXG()[i].RMS();
    if (x < 200 && x > max-1)
      max = (int)x + 1;
  }

  svec<int> hist;
  hist.resize(max, 0);

  for (i=0; i<d.EXG().isize(); i++) {
    double x = d.EXG()[i].RMS();
    if (x >= 199)
      continue;
    hist[(int)x]++;
  }

  FILE * p = fopen("hist", "w");

  for (i=0; i<hist.isize(); i++) {
    fprintf(p, "%d\n", hist[i]);
    }

  fclose(p);
  */
  
  //h.AddBlock(from, to , name);
}

void TimeRefine::ApplyHints(BTLData & d, const Hints & h) const
{
  int i, j;

  int start = -1;
  int last_state = 0;
  cout << "Apply hints to " << d.EXG().isize() << endl;

 
  svec<Peak> peaks;

  // MOVE OUT OF LOOP
  double dot_frac = 0.7;
  m_pCfg->Update("dot_fraction", dot_frac);

  double euc_fraction = 0.18;
  m_pCfg->Update("euc_fraction", euc_fraction);
  
  
  double weight_exg = 0.;
  double weight_acc = 1.;
  double weight_gyr = 1.;
  
  m_pCfg->Update("dot_weight_exg", weight_exg);
  m_pCfg->Update("dot_weight_acc", weight_acc);
  m_pCfg->Update("dot_weight_gyr", weight_gyr);


  //====================================================
  IMUSensor sens;
  SetupSensor(sens, d);

  //cout << "CHECK: " << sens[1][0][100] << endl;
  //cout << "CHECK: " << sens[1][0][101] << endl;
  //cout << "CHECK: " << sens[1][0][102] << endl;


  svec<IMUInterval> sig_detect;
  //sens.GetActivity(sig_detect);


  // TRY
  if (HasAnnot(d.EXG())) {
    cout << "Has annotations!" << endl;
    ExtractHintAnnot(sig_detect, d.EXG());
  } else {
    cout << "Start automated annotations!" << endl;
    
    svec<IMUInterval> raw_emg;
    ExtractEMGAnnot(raw_emg, d.EXG());
    sens.GetActivitySeeded(sig_detect, raw_emg);
  }

  
  // Copy over
  for (i=0; i<sig_detect.isize(); i++) {
    Peak p;
    p.Sum() = 0.01; // Some low value to not compete w/ hints
    p.Frame() = sig_detect[i].Start()/2;
    p.Size() = (sig_detect[i].End() - sig_detect[i].Start())/2;
    p.State() = 0;
    p.Name() = sig_detect[i].Name();
    p.Self() = 0.5; //????????????????
    
    //cout << "PUSH activity " << beststart << " - " << bestlen << " " << p.Sum() << endl;
    		
    peaks.push_back(p);


  }
  //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
  // Auto hints!!



  //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
  
  //====================================================
  //====================================================
  //====================================================
  cout << "HINTS!!" << endl;
  
  for (j=0; j<h.NumShapes(); j++) {
    const TDShape & s = h.Shape(j);

    IMUSensor test;
    cout << "HINT: " << 2*s.From() << " " << 2*s.To() << endl;
    sens.Cut(test, IMUInterval(2*s.From(), 2*s.To()));

    
    double self = test.Self();
    
    self *= euc_fraction;
    
    bool bCycle = s.IsCycle();
    
    cout << "Apply shape, size " << s.isize() << endl;

    // Need to upsample...
    svec<double> dot;
    //dot.resize(d.EXG().isize(), 0.);

    sens.ComputeMatch(dot, test);
    
    for (i=0; i<dot.isize(); i++) {

      //cout << "dot " << i << " -> " << dot[i] << " self: " << self << endl;
      
      if (dot[i] > dot[i-1] && dot[i] >= dot[i+1] && dot[i] > self) {
	int beststart = i * 5;
	int bestlen = test[0][0].isize() * 5;
	double bestdot = dot[i];


	// =================== WIGGLE ======================================
	int w_start = -1;
	int w_stop = -1;
	
	double w_score = sens.ComputeMatchWiggle(w_start, w_stop, i, test);
	cout << "CHECK Wiggle: " << bestdot << " vs. " << w_score << endl;
	
	if (w_score > bestdot) {
	  cout << "ACCEPT " << w_start << " " << w_stop << " over " << beststart/5 << " " <<  endl;
	  bestdot = w_score;
	  beststart = w_start*5;
	  bestlen = (w_stop-w_start)*5;
	}
	// =================== WIGGLE ======================================

	
	// WIGGLE
	/*
	for (double f = 0.8; f<1.2; f+= 0.01) {
	  //cout << "Stretch loop " << f << endl;
	  TDShape stretch = s.Stretch(f);
	  //cout << "Original size: " << s.isize() << endl;
	  //cout << "Stretch size: " << stretch.isize() << endl;
	  
	  int off = (stretch.isize()-s.isize())/2;
	  //cout << "Offset: " << off << endl;
	  if (i-off + stretch.isize() >= d.EXG().isize())
	    break;
	  
	  double d3 = Dot(d, i-off, stretch);
	  if (d3 > bestdot) {
	    bestdot = d3;
	    beststart = i-off;
	    bestlen = stretch.isize();
	    cout << "FOUND BETTER MATCH: " << f << " " << d3 << " > " << dot[i] << endl;
	  }
	  }*/
	
	
	
	Peak p;
	p.Sum() = bestdot / self; // Normalize
	p.Frame() = beststart;
	p.Size() = bestlen;
	p.State() = s.State();
	p.Name() = s.Name();
	p.Self() = self;

	//cout << "PUSH peak " << beststart << " - " << bestlen << " " << p.Sum() << endl;

		
	peaks.push_back(p);
      }
    }
    
  }

  //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
  // END REFACTOR

  
  Sort(peaks);

  for (i=0; i<peaks.isize(); i++) {
    if (peaks[i].Sum() == 0)
      continue;
    cout << "PEAK " << i << endl;
   
    for (int k=i+1; k<peaks.isize(); k++) {
      //if (Abs(peaks[k].Frame() - peaks[i].Frame()) < peaks[i].Size())
      //peaks[k].Sum() = 0.;
     if (peaks[k].Frame() > peaks[i].Frame()) {
	if (peaks[i].Frame() + peaks[i].Size() + 2 > peaks[k].Frame()) {
	  //cout << "(1) Kill peak @ " << peaks[k].Frame() << " for " << peaks[i].Frame() << ", dist=" << peaks[i].Size() << endl; 
	  peaks[k].Sum() = 0.;
	}
      } else {
	if (peaks[k].Frame() + peaks[k].Size() + 2 > peaks[i].Frame()) {
	  //cout << "(2) Kill peak @ " << peaks[k].Frame() << " for " << peaks[i].Frame() << ", dist=" << peaks[i].Size() << endl; 
	  peaks[k].Sum() = 0.;
	}
 
      }
    }
  }
  
  for (i=0; i<peaks.isize(); i++) {
    //if (peaks[i].Sum() < peaks[i].Self())
    //continue;
    if (peaks[i].Sum() == 0.)
      continue;
    cout << "DEBUG Peak -> " << peaks[i].Sum() << " " << peaks[i].Frame() << endl;

    if (peaks[i].Frame() < 0) {
      cout << "NEGATIVE Frame, skipping!" << endl;
      continue;
    }
    
    /*
    if (bCycle) {
      cout << "Cycle: " << peaks[i].Name() << " " << peaks[i].Frame() << " - " << peaks[i].Frame()+ peaks[i].Size() << " state: " << peaks[i].State() << endl;
    } else {
      cout << "TD refine: " << peaks[].Frame() << " - " << peaks[i].Frame() + peaks[i].Size() << " state: " << peaks[i].State() << endl;
      }*/
    
      
    Set(d, peaks[i].Frame(), peaks[i].Frame()+peaks[i].Size(), peaks[i].State(), peaks[i].Name());
      
  }

  
  AddCounts(d);



}


void TimeRefine::MakeHeatmap(svec< svec < double > > & data, svec<IMUInterval> & iv, const BTLData & d)
{
  IMUSensor sens;
  SetupSensor(sens, d);

  //============================================================================================
  //============================================
  // Cross-matches
  // Add in hint cycles
  svec<IMUInterval> all_hints;
  ExtractHintAnnot(all_hints, d.EXG());
  cerr << "EXTRACTED HINTS: " << all_hints.isize() << endl;

  // ????????????????
  iv = all_hints;
  
  sens.ActivityBySimilarity(data, all_hints);
  //============================================

}

void TimeRefine::AddCounts(BTLData & data) const 
{
  int i, j;

  svec<string> names;
  svec<int> counts;

  int idx = -1;
  
  string last;
  for (i=0; i<data.EXG().isize(); i++) {
    EXGData & d = data.EXG()[i];
    if (d.Cycle() == "" || d.Cycle() == ".") {
      last = "";
      continue;
    }
    
    if (strstr(d.Cycle().c_str(), "(") != NULL) {
      last = "";
      continue;
    }

    if (d.Cycle() != last) {
      idx = -1;
      for (j=0; j<names.isize(); j++) {
	if (names[j] == d.Cycle()) {
	  idx = j;
	  break;
	}
      }
      if (idx == -1) {
	names.push_back(d.Cycle());
	counts.push_back(0);
	idx = names.isize()-1;
      }
      counts[idx]++;
      last = d.Cycle();
    }

    string curr = last + "_(" + Stringify(counts[idx]) + ")";
    d.Cycle() = curr;
  }


}

  
void TimeRefine::Refine(BTLData & d) const
{
  int i, j;

  int start = -1;
  int last_state = 0;
  for (i=0; i<d.EXG().isize(); i++) {
    int state = d.EXG()[i].State();

    if (state == 3 && last_state == 4) {
      start = i;
      //cout << "REFINE start at " << i << endl;
    }

    if (state < 3) {
      //cout << "REFINE clear at " << i << endl;
      start = -1;
    }

    if (start >= 0 && state == 4 && last_state == 3) {
      int n = (i+start)/2;
      cout << "REFINE set around " << n << endl;
      for (j=n-16; j<n+16; j++)
	d.EXG()[j].State() = 2;

      start = -1;
    }
    last_state = state;

  }

  for (i=0; i<d.EXG().isize(); i++) {
    int state = d.EXG()[i].State();

    if (state == 2 && last_state >= 3) {
      start = i;
      //cout << "REFINE start at " << i << endl;
    }

    if (state < 2) {
      //cout << "REFINE clear at " << i << endl;
      start = -1;
    }

    if (start >= 0 && state >= 3 && last_state == 2) {
      int n = (i+start)/2;
      cout << "REFINE set around " << n << endl;
      for (j=n-16; j<n+16; j++)
	d.EXG()[j].State() = 1;

      start = -1;
    }
    last_state = state;

  }


  
}
