#include "eeg/Annotator.h"
#include "eeg/BTLData.h"
#include "eeg/Hints.h"
#include "base/StringUtil.h"
#include <math.h>

void Annotation::Write(const string & fileName)
{
  FILE * p = fopen(fileName.c_str(), "w");

  int i;

  
  fprintf(p, "start\tstop\tduration\tstate\tsample\tname\tmovement\tavg_move\tmax_move\tspin\tavg_spin\tmax_spin\tintensity\tavg_intens\tmax_intens\trepetition\n");
  for (i=0; i<m_annot.isize(); i++) {
    // HARD CODED FRAME RATE
    double d = 500*(m_annot[i].Stop()-m_annot[i].Start());
    fprintf(p, "%f\t%f\t%f\t%d\t%d\t%s\t%f\t%f\t%f\t%f\t%f\t%f\t%f\t%f\t%f\t%d\n",
	    m_annot[i].Start(),
	    m_annot[i].Stop(),
	    m_annot[i].Stop()-m_annot[i].Start(),
	    m_annot[i].Idx(),
	    m_annot[i].Sample(),
	    m_annot[i].Name().c_str(),
	    m_annot[i].Move(),
	    m_annot[i].Move()/d,
	    m_annot[i].MoveMax(),
	    m_annot[i].Spin(),
	    m_annot[i].Spin()/d,
	    m_annot[i].SpinMax(),
	    m_annot[i].Intens(),
	    m_annot[i].Intens()/d,
	    m_annot[i].IntensMax(),
	    m_annot[i].Rep());
  }

  fclose(p);
}


void SplitCycle(string & name, string & num, const string & in)
{
  if (in == "") {
    num = "0";
    return;
  }
  StringParser p;
  p.SetLine(in, "_(");
  name = p.AsString(0);
  p.SetLine(p.AsString(1), ")");
  num = p.AsString(0);

}

void Annotator::Apply(BTLData & d, int frame) const
{
  int i, j;

  for (i=0; i<d.EXG().isize(); i++) {
    d.EXG()[i].State() = 0;
  }

  svec<int> tmp = m_data;
  UniqueSort(tmp);

  cout << "States: " << tmp.isize() << endl;
  
  int k = frame/4;
  for (i=0; i<m_data.isize(); i++) {
    for (j=0; j<frame/2; j++) {
      d.EXG()[k].State() = m_data[i];
      k++;
    }
  }

  

  
}

void Annotator::Annotate(Annotation & annot, BTLData & d, int e_rate, int i_rate) const
{
  int i;

  int last = -1;
  OneAnno a;

  double div = (double)e_rate/(double)i_rate;
  double last_stamp = 0;
  for (i=0; i<d.EXG().isize(); i++) {
    const EXGData & e = d.EXG()[i];
    if (e.State() != last) {
      a.Stop() = last_stamp;
      if (/*a.Idx() > 1 &&*/ last >= 0)
	annot.push_back(a);
      
      a.Clear();
      a.Start() = e.Time();
      a.Idx() = e.State();
      a.Sample() = i;
      if (e.Cycle() == "")
	a.Name() = ".";
      else
	a.Name() = e.Cycle();
    }
    last = e.State();
    last_stamp = e.Time();

    a.Intens() += e.RMS();
    if (e.RMS() > a.IntensMax())
      a.IntensMax() = e.RMS();

    
    int j = i*i_rate/e_rate;

    if (j < d.Acc().isize()) {
      a.Move() += d.Acc()[j].Processed()/div;
      if (d.Acc()[j].Processed() > a.MoveMax())
	a.MoveMax() = d.Acc()[j].Processed();
    }
    if (j < d.Gyro().isize()) {
      a.Spin() += d.Gyro()[j].Processed()/div;
      if (d.Gyro()[j].Processed() > a.SpinMax())
	a.SpinMax() = d.Gyro()[j].Processed();
    }
    
    
  }
  
}

void Annotator::FindBounds(svec<Boundaries> & out, const BTLData & d) const
{
  int i;
  out.clear();

  Boundaries bb;
  int off = 2;
  for (i=1; i<d.EXG().isize(); i++) {
    if (d.EXG()[i].State() >= off && d.EXG()[i-1].State() < off) {
      bb.start_emg = i;
      bb.start_imu = (i*rate_imu)/rate_emg;
      if (bb.start_imu >= d.Acc().isize())
	bb.start_imu = d.Acc().isize()-1;
      if (bb.start_imu >= d.Gyro().isize())
	bb.start_imu = d.Gyro().isize()-1;
    }
    if (d.EXG()[i].State() < off && d.EXG()[i-1].State() >= off) {
      bb.stop_emg = i;
      bb.stop_imu = (i*rate_imu)/rate_emg;
      if (bb.stop_imu >= d.Acc().isize())
	bb.stop_imu = d.Acc().isize()-1;
      if (bb.stop_imu >= d.Gyro().isize())
	bb.stop_imu = d.Gyro().isize()-1;
      out.push_back(bb);
    }

    
  }
}

//===================================================================================
void Annotator::FindBoundsCycle(svec<Boundaries> & out, const BTLData & d) const
{
  int i;
  out.clear();

  Boundaries bb;
  int off = 2;
  for (i=1; i<d.EXG().isize(); i++) {
    if (d.EXG()[i].Cycle() != "" && d.EXG()[i-1].Cycle() == "") {
      bb.start_emg = i;
      bb.start_imu = (i*rate_imu)/rate_emg;
      if (bb.start_imu >= d.Acc().isize())
	bb.start_imu = d.Acc().isize()-1;
      if (bb.start_imu >= d.Gyro().isize())
	bb.start_imu = d.Gyro().isize()-1;
    }
    if (d.EXG()[i].Cycle() == "" && d.EXG()[i-1].Cycle() != "") {
      bb.stop_emg = i;
      bb.stop_imu = (i*rate_imu)/rate_emg;
      if (bb.stop_imu >= d.Acc().isize())
	bb.stop_imu = d.Acc().isize()-1;
      if (bb.stop_imu >= d.Gyro().isize())
	bb.stop_imu = d.Gyro().isize()-1;
      out.push_back(bb);
    }

    
  }
}

void Annotator::MakeCSVFiles(BTLData & d, const string & prefix) const
{

}

int Annotator::Print(string & data, string & header, const svec<double> & d, const string & label, double dur) const
{
  int i, j;

  //  data = "";
  //header = "";

  header += "start_" + label + delim;
  data += Stringify(d[0]) + delim;
  header += "end_" + label + delim;
  data += Stringify(d[d.isize()-1]) + delim;

  double sum = 0.;
  double max = 0.;
  double idx = 0;
  for (i=0; i<d.isize(); i++) {
    sum += d[i];
    if (d[i] > max) {
      max = d[i];
      idx = i;
    }
  }
  sum /= (double)d.isize();
  
  header += "avg_" + label + delim;
  data += Stringify(sum) + delim;
  header += "area_" + label + delim;
  data += Stringify(sum/dur) + delim;
  
  return idx;
}
 
void Annotator::MakeEMGAnnot(BTLData & d, const string & fileName) const
{
  int i, j;
  
  svec<Boundaries> bb;
  FindBounds(bb, d);


  FILE * p = fopen(fileName.c_str(), "w");
  svec<int> peak;
  peak.resize(d.EXG().isize(), 0);
  
  for (i=0; i<bb.isize(); i++) {
    for (j=bb[i].start_emg; j<bb[i].stop_emg; j++) {
      peak[j] = i+1;
    }
  }


  fprintf(p, "peak_number\tpeak_state\tcycle_number\tcycle_type\tdot_product_perc\ttime\traw\trectify\tRMS\tRMS_epoch_window_length\tMNF\tMDF\tFFT_epoch_window_length\n");

  for (i=0; i<d.EXG().isize(); i++) {
    const EXGData & e = d.EXG()[i];

    fprintf(p, "%d\t%d", peak[i], e.State());
    
    string cyc, num;
    if (e.Cycle() != "") {
      SplitCycle(cyc, num, e.Cycle());
      fprintf(p, "\t%s\t%s", num.c_str(), cyc.c_str());
    } else {
      fprintf(p, "\t0\t.");
    }
    fprintf(p, "\tn/a"); // dot product

    fprintf(p, "\t%f", e.Time());
    
    fprintf(p, "\t%f\t%f\t%f", e.Signal(), sqrt(e.Signal()*e.Signal()), e.RMS());
    fprintf(p, "\tn/a"); // epoch win
    
    fprintf(p, "\t%f", e.MDF());
    fprintf(p, "\t%f", e.MNF());
    
    fprintf(p, "\t128/64\n"); // epoch win
   
  }

  
  fclose(p);
}


void Annotator::MakeMasterAnnot(BTLData & d, const Hints & h, const string & fileName) const
{
  int i;

  FILE * p = fopen(fileName.c_str(), "w");

  for (i=0; i<h.NumBlocks(); i++) {
    const HintBlock & s = h.Block(i);
    cout << "ADDING BLOCK " << s.Name() << endl;
    fprintf(p, "#BLOCK\t%s\t%d\t%d\n", s.Name().c_str(), s.Start(), s.End()); // In ms already!!!!
  }

  for (i=0; i<h.NumShapes(); i++) {
    const TDShape & s = h.Shape(i);
    cout << "ADDING HINT " << s.Name() << endl;
    fprintf(p, "#HINT\t%s\t%d\t%d\n", s.Name().c_str(), 2*s.From(), 2*s.To());
  }


  
  int start = -1;
  int stop = -1;
  string c;
  
  for (i=0; i<d.EXG().isize(); i++) {
    const EXGData & ex = d.EXG()[i];
    if (ex.Cycle() != "") {
      if (start == -1) {
	start = (int)(ex.Time()*1000);
	c = ex.Cycle();
      }
      stop = (int)(ex.Time()*1000);
    }
    if (ex.Cycle() == "" && start != -1) {
      fprintf(p, "%s\t%d\t%d\n", c.c_str(), start, stop);
      start = -1;
      stop = -1;
    }

    
     
  
  }

  fclose(p);

}

void Annotator::MakeAccAnnot(BTLData & d, const string & fileName) const
{

  int i, j;
  
  svec<Boundaries> bb;
  FindBounds(bb, d);


  FILE * p = fopen(fileName.c_str(), "w");
  svec<int> peak;
  peak.resize(d.Acc().isize(), 0);
  
  for (i=0; i<bb.isize(); i++) {
    for (j=bb[i].start_imu; j<bb[i].stop_imu; j++) {
      peak[j] = i+1;
    }
  }


  fprintf(p, "peak_number\tpeak_state\tcycle_number\tcycle_type\tdot_product_perc\ttime\tacc_x\tacc_y\tacc_z\tacc_mag\tacc_mag_der\n");

  double last = 0;
  for (i=0; i<d.Acc().isize(); i++) {
    const IMUData & e = d.Acc()[i];
    int idx = (i*rate_emg)/rate_imu;
    if (idx >= d.EXG().isize())
      idx = d.EXG().isize() - 1;
    
    fprintf(p, "%d\t%d", peak[i], d.EXG()[idx].State());
 
    string cyc, num;
    if (d.EXG()[idx].Cycle() != "") {
      SplitCycle(cyc, num, d.EXG()[idx].Cycle());
      fprintf(p, "\t%s\t%s", num.c_str(), cyc.c_str());
    } else {
      fprintf(p, "\t0\t.");
    }
    fprintf(p, "\tn/a"); // dot product

    fprintf(p, "\t%f\t%f\t%f\t%f", e.Time(), e[0], e[1], e[2]);

    double s = sqrt(e[0]*e[0]+e[1]*e[1]+e[2]*e[2]);
    fprintf(p, "\t%f\t%f\n", s, s-last);

    last = s;
  }
  fclose(p);
}


void Annotator::MakeGyroAnnot(BTLData & d, const string & fileName) const
{  int i, j;
  
  svec<Boundaries> bb;
  FindBounds(bb, d);


  FILE * p = fopen(fileName.c_str(), "w");
  svec<int> peak;
  peak.resize(d.Gyro().isize(), 0);
  
  for (i=0; i<bb.isize(); i++) {
    for (j=bb[i].start_imu; j<bb[i].stop_imu; j++) {
      peak[j] = i+1;
    }
  }


  fprintf(p, "peak_number\tpeak_state\tcycle_number\tcycle_type\tdot_product_perc\ttime\tgyro_x\tgyro_y\tgyro_z\tgyro_mag\n");

  
  for (i=0; i<d.Gyro().isize(); i++) {
    const IMUData & e = d.Gyro()[i];
    int idx = (i*rate_emg)/rate_imu;
    if (idx >= d.EXG().isize())
      idx = d.EXG().isize() - 1;
    fprintf(p, "%d\t%d", peak[i], d.EXG()[idx].State());
 
    string cyc, num;
    if (d.EXG()[idx].Cycle() != "") {
      SplitCycle(cyc, num, d.EXG()[idx].Cycle());
      fprintf(p, "\t%s\t%s", num.c_str(), cyc.c_str());
    } else {
      fprintf(p, "\t0\t.");
    }
    fprintf(p, "\tn/a"); // dot product

    fprintf(p, "\t%f\t%f\t%f\t%f", e.Time(), e[0], e[1], e[2]);

    double s = sqrt(e[0]*e[0]+e[1]*e[1]+e[2]*e[2]);
    fprintf(p, "\t%f\n", s);

   
  }

  fclose(p);
}


//=========================================================================
void Annotator::MakeCurveAnnot(BTLData & d, const string & fileName) const
{
  int i, j;
  
  svec<Boundaries> bb;
  FindBounds(bb, d);
  
  
  FILE * p = fopen(fileName.c_str(), "w");
  
  for (i=0; i<bb.isize(); i++) {
    svec<double> tmp;
    string data;
    string header;
    Append(data, header, i+1, "peak_number");
    Append(data, header, d.EXG()[bb[i].start_emg].State(), "peak_state");
    
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    string cyc, num;
    SplitCycle(cyc, num, d.EXG()[bb[i].start_emg].Cycle());
    //cout << "Cycle: " << d.EXG()[i].Cycle() << endl;
    Append(data, header, num, "cycle_number");
    if (d.EXG()[bb[i].start_emg].Cycle() == "")
      Append(data, header, ".", "cycle_type");
    else
      Append(data, header, cyc, "cycle_type");
    
    // Dot product HERE!!!
    
    Append(data, header, d.EXG()[bb[i].start_emg].Time(), "start_time");
    Append(data, header, d.EXG()[bb[i].stop_emg].Time(), "end_time");
    Append(data, header, d.EXG()[bb[i].stop_emg].Time()-d.EXG()[bb[i].start_emg].Time(), "duration");
    
    double dur = d.EXG()[bb[i].stop_emg].Time()-d.EXG()[bb[i].start_emg].Time();

    int k_max = 1;
    int k_min = 1;

    double max = 0.;
    for (j=bb[i].start_emg+1; j<bb[i].stop_emg-1; j++) {          
      if (d.EXG()[j].RMS() > max)
	max = d.EXG()[j].RMS();
    }


    
    int lastMax = bb[i].start_emg;
    int lastMin = bb[i].start_emg;
    double lastMaxVal = d.EXG()[bb[i].start_emg].RMS();
    double lastMinVal = d.EXG()[bb[i].start_emg].RMS();

    svec<double> duration;
    svec<double> onset_decay;
    svec<string> label;

    string dummy;

    //cout << "Peak " << i << " " << d.EXG()[bb[i].start_emg].Time() << " " << d.EXG()[bb[i].start_emg].Time() << endl;
    for (j=bb[i].start_emg+1; j<bb[i].stop_emg-1; j++) {
      int idx = j-bb[i].start_emg;
      
      if (d.EXG()[j].RMS() > d.EXG()[j-1].RMS() && d.EXG()[j].RMS() >= d.EXG()[j+1].RMS()) { // Maximum

	//cout << "Max " << j << " " << d.EXG()[j].RMS() << " " <<  d.EXG()[j-1].RMS() << " " << d.EXG()[j+1].RMS() << endl;

     
	
	//Append(data, header, "local_max_" + Stringify(k_max), "local_max_" + Stringify(k_max));
 	Append(data, header, Stringify(d.EXG()[j].Time()), "time_local_max_"+ Stringify(k_max));
	Append(data, header, Stringify(100*idx/(double)(bb[i].stop_emg-bb[i].start_emg)), "%_of_total_time_max"+ Stringify(k_max));
	Append(data, header, Stringify(d.EXG()[j].RMS()), "maxima_RMS_"+ Stringify(k_max));
	// ==================================================================
	// PROMINENCE
	Append(data, header, Stringify(1.-d.EXG()[j].RMS()/max), "prominence_max_"+ Stringify(k_max));

	
	double dd = d.EXG()[j].Time()-d.EXG()[lastMin].Time();
	double pro = d.EXG()[j].RMS()/(lastMinVal+0.00001);

	if (pro < 0.) {
	  cout << "SCREAM" << endl;
	}
	
	lastMaxVal = d.EXG()[j].RMS();

	duration.push_back(dd);
	onset_decay.push_back(pro);
	label.push_back("onset");
	
	lastMax = j;
	k_max++;
      }
      
      if ((d.EXG()[j].RMS() < d.EXG()[j-1].RMS() &&  d.EXG()[j].RMS() <= d.EXG()[j+1].RMS() && k_max > 1) /*|| j == bb[i].stop_emg-2*/) { // Minimum
	//cout << "Add min: " << k_min << endl;
	Append(data, header, Stringify(d.EXG()[j].Time()), "time_local_min_"+ Stringify(k_min));
	//cout << header << endl;
	
	Append(data, header, Stringify(100*idx/(double)(bb[i].stop_emg-bb[i].start_emg)), "%_of_total_time_min_"+ Stringify(k_min));
 	Append(data, header, Stringify(d.EXG()[j].RMS()), "minima_RMS_"+ Stringify(k_min));
	Append(data, header, Stringify(d.EXG()[j].RMS()/max), "prominence_min_"+ Stringify(k_min));

	double dd = d.EXG()[j].Time()-d.EXG()[lastMin].Time();
	double pro = (d.EXG()[j].RMS() - lastMaxVal)/(lastMaxVal+0.00001);
	//cout << lastMaxVal << " -> " << d.EXG()[j].RMS() << endl;
	lastMinVal = d.EXG()[j].RMS();
	duration.push_back(dd);
	onset_decay.push_back(pro);
	label.push_back("decay");

	lastMin;
	k_min++;

      }
      if (k_min == 6)
	break;
    }

    //cout << k_min << " " << k_max << endl;
    //cout << "BEFORE: " << header << endl;
    //cout << " -> " << k_min << endl;
    
    // Extra minimum
    if (k_min < k_max) {
      //cout << "Extra min" << endl;
      Append(data, header, "NaN", "time_local_min_"+ Stringify(k_min));
      Append(data, header, "NaN", "%_of_total_time_min_"+ Stringify(k_min));
      Append(data, header, "NaN", "minima_RMS_"+ Stringify(k_min));
      Append(data, header, "NaN", "prominence_min_"+ Stringify(k_min));
      //header += " MARKER ";
      k_min++;
    }
    // Extra maximum
    if (k_max < k_min) {
      //cout << "Extra max" << endl;
      //k_max++;
      Append(data, header, "NaN", "time_local_max_"+ Stringify(k_max));
      Append(data, header, "NaN", "%_of_total_time_max_"+ Stringify(k_max));
      Append(data, header, "NaN", "maxima_RMS_"+ Stringify(k_max));
      Append(data, header, "NaN", "prominence_max_"+ Stringify(k_max));
      k_max++;
      //header += " MARKER2 ";
    }
    
    for (; k_max<7; k_max++) {
      Append(data, header, "NaN", "time_local_max_"+ Stringify(k_max));
      Append(data, header, "NaN", "%_of_total_time_max_"+ Stringify(k_max));
      Append(data, header, "NaN", "maxima_RMS_"+ Stringify(k_max));
      Append(data, header, "NaN", "prominence_max_"+ Stringify(k_max));
      
      Append(data, header, "NaN", "time_local_min_"+ Stringify(k_max));
      Append(data, header, "NaN", "%_of_total_time_min_"+ Stringify(k_max));
      Append(data, header, "NaN", "minima_RMS_"+ Stringify(k_max));
      Append(data, header, "NaN", "prominence_min_"+ Stringify(k_max));
    }
    

    
    k_max = 1;
    k_min = 1;
    for (j=0; j<duration.isize(); j++) {      
      if (label[j] == "onset") {
	Append(data, header, Stringify(duration[j]), "duration_to_local_max_" + Stringify(k_max));
 	Append(data, header, Stringify(onset_decay[j]), "prominence_onset_" + Stringify(k_max));
 	k_max++;
      } else {
	Append(data, header, Stringify(duration[j]), "duration_to_local_min_" + Stringify(k_min));
 	Append(data, header, Stringify(onset_decay[j]), "prominence_decay_" + Stringify(k_min));
	k_min++;
      }
 
    }

    if (k_min < k_max) {
      k_min++;
      Append(data, header, "NaN", "duration_to_local_min_" + Stringify(k_min));
      Append(data, header, "NaN", "prominence_decay_" + Stringify(k_min));
    }
    if (k_max < k_min) {
      k_max++;
      Append(data, header, "NaN", "duration_to_local_max_" + Stringify(k_max));
      Append(data, header, "Nan", "prominence_decay_" + Stringify(k_max));
    }

    
    for (; k_max<7; k_max++) {
      Append(data, header, "NaN", "duration_to_local_max_" + Stringify(k_max));
      Append(data, header, "NaN", "prominence_onset_" + Stringify(k_max));
         
      Append(data, header, "NaN", "duration_to_local_min_" + Stringify(k_max));
      Append(data, header, "NaN", "prominence_decay_" + Stringify(k_max));

    }
    

    //cout << header << endl << endl;

    if (i==0)
      fprintf(p, "%s\n", header.c_str());
    else
      header = "";
    
    fprintf(p, "%s\n", data.c_str());


  }
  
  fclose(p);
}
//=========================================================================

void Annotator::Append(string & data, string & header, double d, const string & label) const
{
  data += Stringify(d) + delim;
  header += label + delim;
}
void Annotator::Append(string & data, string & header, const string & s, const string & label) const
{
  data += s + delim;
  header += label + delim;
}

void Annotator::PrintRise(string & data, string & header, const svec<double> & d,
			  const string & label, int rate, bool diff) const
{
  int i, j;

  double max = 0.;
  double max_idx = 0;
  for (i=0; i<d.isize(); i++) {
  
    if (d[i] > max) {
      max = d[i];
      max_idx = i;
    }
  }
  int frame = rate/100;

  for (i=1; i<11; i++) {
    header += label + "_rise_left_" + Stringify(i*10) + "ms" + delim;
    if (i*frame < max_idx) {
      //cout << i*frame << " " << d.isize() << " " << max << " total: " << d.isize() << endl;
      double dd = d[i*frame];
      //cout << "Val: " << dd << " vs. " << d[0] << endl; 
      if (diff)
	dd -= d[0];
      data += Stringify(dd) + delim;
    } else {
      data += "0" + delim;
    }
  }
  for (i=1; i<11; i++) {
    header += label + "_rise_right_" + Stringify(i*10) + "ms" + delim;
    int idx = d.isize()-1 - i*frame;
    if (idx > max_idx) {
      //cout << idx << " " << d.isize() << endl;
      double dd = d[idx];
      if (diff)
	dd -= d[d.isize()-1];
      data += Stringify(-dd) + delim;
    } else {
      data += "0" + delim;
    }
  }



  //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  // Decay of peak???
  /*  for (i=1; i<11; i++) {
    header += label + "_rise_right_" + Stringify(i*10) + "ms" + delim;
    int idx = max_idx + i*frame;
    if (idx < d.isize()) {
      //cout << idx << " " << d.isize() << endl;
      double dd = d[idx];
      if (diff)
	dd -= d[d.isize()-1];

      // Reports neg values but doesn't account for the top?data += Stringify(-dd) + delim;
    } else {
      data += "0" + delim;
    }
    }*/
 
}


double Abs(double d)
{
  if (d < 0)
    return -d;
  return d;
}

void DoStats(const svec<double> & d)
{
  int i;

  double max = 0.;
  int peak = 0;


  for (i=0; i<d.isize(); i++) {
    if (d[i] > max) {
      max = d[i];
      peak = i;
    }
  }

  double left = 0.;
  double left_var = 0.;
  double right = 0.;
  double right_var = 0.;

  for (i=1; i<peak; i++) {
    left += d[i];
    left_var += d[i]*d[i];
  }
  for (i=peak+1; i<d.isize(); i++) {
    right += d[i];
    right_var += d[i]*d[i];
  }

  cout << "Stats, left: " << left/(left+right) << " right: " << right/(left+right);
  cout << " left_var: " << left_var/(left_var+right_var) << " right_var: " << right_var/(left_var+right_var);
  cout << " sum: " << left + right << endl;
    
    
  
}


void Annotator::MakeGenAnnot(BTLData & d, const Hints & h, const string & fileName, bool cycle) const
{

  int i, j;
  
  svec<Boundaries> bb;
  if (cycle)
    FindBoundsCycle(bb, d);
  else
    FindBounds(bb, d);


  FILE * p = fopen(fileName.c_str(), "w");
  
  for (i=0; i<bb.isize(); i++) {
    //cout << "ENTER " << i << " of " << bb.isize() << endl;
    
    svec<double> tmp;
    string data;
    string header;

    

    Append(data, header, i+1, "peak_number");
    Append(data, header, d.EXG()[bb[i].start_emg].State(), "peak_state");

    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    string cyc, num;
    SplitCycle(cyc, num, d.EXG()[bb[i].start_emg].Cycle());
    //cout << "Cycle: " << d.EXG()[i].Cycle() << endl;
    Append(data, header, num, "cycle_number");
    if (d.EXG()[bb[i].start_emg].Cycle() == "")
      Append(data, header, ".", "cycle_type");
    else
      Append(data, header, cyc, "cycle_type");

    // BLOCK HERE
    if (cycle) {
      int facS = 2;
      string block = ".";
      for (j=0; j<h.NumBlocks(); j++) {
	if (bb[i].start_emg*facS >= h.Block(j).Start() &&
	    bb[i].stop_emg*facS <= h.Block(j).End()) {
	  block = h.Block(j).Name();
	}
      }
      Append(data, header, block, "block");
     
    }
    // END BLOCK

    
    // Dot product HERE!!!
    
    Append(data, header, d.EXG()[bb[i].start_emg].Time(), "start_time");
    Append(data, header, d.EXG()[bb[i].stop_emg].Time(), "end_time");
    Append(data, header, d.EXG()[bb[i].stop_emg].Time()-d.EXG()[bb[i].start_emg].Time(), "duration");

    double emg_sum = 0.;
    double emg_div = 0.000001;
    double rms_sum = 0.;
  
    for (j=bb[i].start_emg; j<bb[i].stop_emg; j++) {
      emg_sum += sqrt(d.EXG()[j].Signal()*d.EXG()[j].Signal());
      rms_sum += d.EXG()[j].RMS();
      emg_div += 1.;
    }

    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // MGG: change the way this is reported?
    //Append(data, header, rms_sum/emg_div, "AUC");
    double dur = d.EXG()[bb[i].stop_emg].Time()-d.EXG()[bb[i].start_emg].Time();
    Append(data, header, emg_sum/emg_div*dur, "AUC");

    emg_sum = emg_sum/emg_div;
    
    Append(data, header, emg_sum, "mean_EMG");

    
    //double dur = d.EXG()[bb[i].stop_emg].Time()-d.EXG()[bb[i].start_emg].Time();
    // Rise HERE
    //cout << "Peak num " << i+1 << endl;
    d.GetRMS(tmp, bb[i].start_emg, bb[i].stop_emg);
    PrintRise(data, header, tmp,
	      "RMS", 500);

    //DoStats(tmp);

    
    tmp.clear();
    d.GetMDF(tmp, bb[i].start_emg, bb[i].stop_emg);
    PrintRise(data, header, tmp,
	      "MDF", 500);
    tmp.clear();
    d.GetMNF(tmp, bb[i].start_emg, bb[i].stop_emg);
    PrintRise(data, header, tmp,
	      "MNF", 500);

 
    tmp.clear();
    d.GetAccXYZ(tmp, bb[i].start_imu, bb[i].stop_imu, 0);
    PrintRise(data, header, tmp,
	      "acc_x", 100, false);
    tmp.clear();
    d.GetAccXYZ(tmp, bb[i].start_imu, bb[i].stop_imu, 1);
    PrintRise(data, header, tmp,
	      "acc_y", 100, false);
    tmp.clear();
    d.GetAccXYZ(tmp, bb[i].start_imu, bb[i].stop_imu, 2);
    PrintRise(data, header, tmp,
	      "acc_z", 100, false);
  
    tmp.clear();
    d.GetAccMag(tmp, bb[i].start_imu, bb[i].stop_imu);
    PrintRise(data, header, tmp,
	      "acc_mag", 100, false);
 
 
    tmp.clear();
    d.GetGyroXYZ(tmp, bb[i].start_imu, bb[i].stop_imu, 0);
    PrintRise(data, header, tmp,
	      "gyro_x", 100, false);
    tmp.clear();
    d.GetGyroXYZ(tmp, bb[i].start_imu, bb[i].stop_imu, 1);
    PrintRise(data, header, tmp,
	      "gyro_y", 100, false);
    tmp.clear();
    d.GetGyroXYZ(tmp, bb[i].start_imu, bb[i].stop_imu, 2);
    PrintRise(data, header, tmp,
	      "gyro_z", 100, false);
    tmp.clear();
    d.GetGyro(tmp, bb[i].start_imu, bb[i].stop_imu);
    PrintRise(data, header, tmp,
	      "gyro_mag", 100, false);
   //=============================================================
 
    
    tmp.clear();
    d.GetRMS(tmp, bb[i].start_emg, bb[i].stop_emg);
    int t = bb[i].start_emg + Print(data, header, tmp, "RMS", dur);
    int imu_t = (t*rate_imu)/rate_emg;
    Append(data, header, d.EXG()[t].Time(), "time_max_RMS");
    Append(data, header, d.EXG()[t].RMS(), "max_RMS");

    //cout << "CHECK 1: " << imu_t << " " << d.Acc().isize() << endl;
    Append(data, header, (d.Acc()[imu_t])[0], "acc_x_at_max_RMS");
    //cout << "CHECK 2" << endl;
    Append(data, header, (d.Acc()[imu_t])[1], "acc_y_at_max_RMS");
    Append(data, header, (d.Acc()[imu_t])[2], "acc_z_at_max_RMS");

    double m = sqrt((d.Acc()[imu_t])[0]*(d.Acc()[imu_t])[0] + (d.Acc()[imu_t])[1]*(d.Acc()[imu_t])[1] + (d.Acc()[imu_t])[2]*(d.Acc()[imu_t])[2]);
    Append(data, header, m, "acc_mag_max_RMS");
   
    Append(data, header, (d.Gyro()[imu_t])[0], "gyro_x_at_max_RMS");
    Append(data, header, (d.Gyro()[imu_t])[1], "gyro_y_at_max_RMS");
    Append(data, header, (d.Gyro()[imu_t])[2], "gyro_z_at_max_RMS");

    m = sqrt((d.Gyro()[imu_t])[0]*(d.Gyro()[imu_t])[0] + (d.Gyro()[imu_t])[1]*(d.Gyro()[imu_t])[1] + (d.Gyro()[imu_t])[2]*(d.Gyro()[imu_t])[2]);
    Append(data, header, m, "gyro_mag_max_RMS");

    Append(data, header, d.EXG()[t].MDF(), "MDF_at_max_RMS");
    Append(data, header, d.EXG()[t].MNF(), "MNF_at_max_RMS");


    //===========================================================
    // ACC X
    tmp.clear();
    d.GetAccXYZ(tmp, bb[i].start_imu, bb[i].stop_imu, 0);
    imu_t = bb[i].start_imu + Print(data, header, tmp, "acc_x", dur);
    t = (imu_t*rate_emg)/rate_imu;
    if (t >= d.EXG().isize())
      t = d.EXG().isize()-1;
    Append(data, header, d.Acc()[imu_t].Time(), "time_max_acc_x");
    Append(data, header, (d.Acc()[imu_t])[0], "max_acc_x");

    Append(data, header, d.EXG()[t].RMS(), "RMS_at_max_acc_x");
    Append(data, header, d.EXG()[t].MDF(), "MDF_at_max_acc_x");
    Append(data, header, d.EXG()[t].MNF(), "MNF_at_max_acc_x");

 
    tmp.clear();
    d.GetAccXYZ(tmp, bb[i].start_imu, bb[i].stop_imu, 1);
    imu_t = bb[i].start_imu + Print(data, header, tmp, "acc_y", dur);
    t = (imu_t*rate_emg)/rate_imu;
    Append(data, header, d.Acc()[imu_t].Time(), "time_max_acc_y");
    Append(data, header, (d.Acc()[imu_t])[1], "max_acc_y");

    if (t >= d.EXG().isize())
      t = d.EXG().isize()-1;
    Append(data, header, d.EXG()[t].RMS(), "RMS_at_max_acc_y");
    Append(data, header, d.EXG()[t].MDF(), "MDF_at_max_acc_y");
    Append(data, header, d.EXG()[t].MNF(), "MNF_at_max_acc_y");




    tmp.clear();
    d.GetAccXYZ(tmp, bb[i].start_imu, bb[i].stop_imu, 2);
    imu_t = bb[i].start_imu + Print(data, header, tmp, "acc_z", dur);
    t = (imu_t*rate_emg)/rate_imu;
    Append(data, header, d.Acc()[imu_t].Time(), "time_max_acc_z");
    Append(data, header, (d.Acc()[imu_t])[2], "max_acc_z");

    if (t >= d.EXG().isize())
      t = d.EXG().isize()-1;

    Append(data, header, d.EXG()[t].RMS(), "RMS_at_max_acc_z");
    Append(data, header, d.EXG()[t].MDF(), "MDF_at_max_acc_z");
    Append(data, header, d.EXG()[t].MNF(), "MNF_at_max_acc_z");



   // INSERT -------------------------------------------------
    tmp.clear();
    d.GetAccMag(tmp, bb[i].start_imu, bb[i].stop_imu);
    imu_t = bb[i].start_imu + Print(data, header, tmp, "acc_mag", dur);
    t = (imu_t*rate_emg)/rate_imu;
    Append(data, header, d.Gyro()[imu_t].Time(), "time_max_acc_mag");
    Append(data, header, (d.Gyro()[imu_t]).Processed(), "max_acc_mag");
    if (t >= d.EXG().isize())
      t = d.EXG().isize()-1;
    
    
    Append(data, header, d.EXG()[t].RMS(), "RMS_at_max_acc_mag");
    Append(data, header, d.EXG()[t].MDF(), "MDF_at_max_acc_mag");
    Append(data, header, d.EXG()[t].MNF(), "MNF_at_max_acc_mag");
    //-----------------------------------------------------------


    
    //===========================================================
    // Gyro X, Y,Z
    tmp.clear();
    d.GetGyroXYZ(tmp, bb[i].start_imu, bb[i].stop_imu, 0);
    imu_t = bb[i].start_imu + Print(data, header, tmp, "gyro_x", dur);
    t = (imu_t*rate_emg)/rate_imu;
    Append(data, header, d.Gyro()[imu_t].Time(), "time_max_gyro_x");
    Append(data, header, (d.Gyro()[imu_t])[0], "max_gyro_x");
    if (t >= d.EXG().isize())
      t = d.EXG().isize()-1;

    Append(data, header, d.EXG()[t].RMS(), "RMS_at_max_gyro_x");
    Append(data, header, d.EXG()[t].MDF(), "MDF_at_max_gyro_x");
    Append(data, header, d.EXG()[t].MNF(), "MNF_at_max_gyro_x");


   tmp.clear();
    d.GetGyroXYZ(tmp, bb[i].start_imu, bb[i].stop_imu, 1);
    imu_t = bb[i].start_imu + Print(data, header, tmp, "gyro_y", dur);
    t = (imu_t*rate_emg)/rate_imu;
    Append(data, header, d.Gyro()[imu_t].Time(), "time_max_gyro_y");
    Append(data, header, (d.Gyro()[imu_t])[1], "max_gyro_y");
    if (t >= d.EXG().isize())
      t = d.EXG().isize()-1;

    Append(data, header, d.EXG()[t].RMS(), "RMS_at_max_gyro_y");
    Append(data, header, d.EXG()[t].MDF(), "MDF_at_max_gyro_y");
    Append(data, header, d.EXG()[t].MNF(), "MNF_at_max_gyro_y");



    tmp.clear();
    d.GetGyroXYZ(tmp, bb[i].start_imu, bb[i].stop_imu, 2);
    imu_t = bb[i].start_imu + Print(data, header, tmp, "gyro_z", dur);
    t = (imu_t*rate_emg)/rate_imu;
    Append(data, header, d.Gyro()[imu_t].Time(), "time_max_gyro_z");
    Append(data, header, (d.Gyro()[imu_t])[2], "max_gyro_z");
    if (t >= d.EXG().isize())
      t = d.EXG().isize()-1;

    Append(data, header, d.EXG()[t].RMS(), "RMS_at_max_gyro_z");
    Append(data, header, d.EXG()[t].MDF(), "MDF_at_max_gyro_z");
    Append(data, header, d.EXG()[t].MNF(), "MNF_at_max_gyro_z");

    
    //===========================================================


    tmp.clear();
    d.GetGyro(tmp, bb[i].start_imu, bb[i].stop_imu);
    imu_t = bb[i].start_imu + Print(data, header, tmp, "gyro_mag", dur);
    t = (imu_t*rate_emg)/rate_imu;
    Append(data, header, d.Gyro()[imu_t].Time(), "time_max_gyro_mag");
    Append(data, header, (d.Gyro()[imu_t]).Processed(), "max_gyro_mag");
    if (t >= d.EXG().isize())
      t = d.EXG().isize()-1;

    Append(data, header, d.EXG()[t].RMS(), "RMS_at_max_gyro_mag");
    Append(data, header, d.EXG()[t].MDF(), "MDF_at_max_gyro_mag");
    Append(data, header, d.EXG()[t].MNF(), "MNF_at_max_gyro_mag");


    // MOVE
    /*
    tmp.clear();
    d.GetAccMag(tmp, bb[i].start_imu, bb[i].stop_imu);
    imu_t = bb[i].start_imu + Print(data, header, tmp, "max_acc_mag", dur);
    t = (imu_t*rate_emg)/rate_imu;
    Append(data, header, d.Gyro()[imu_t].Time(), "time_max_acc_mag");
    Append(data, header, (d.Gyro()[imu_t]).Processed(), "max_acc_mag");
    if (t >= d.EXG().isize())
      t = d.EXG().isize()-1;
    
    
    Append(data, header, d.EXG()[t].RMS(), "RMS_at_max_acc_mag");
    Append(data, header, d.EXG()[t].MDF(), "MDF_at_max_acc_mag");
    Append(data, header, d.EXG()[t].MNF(), "MNF_at_max_acc_mag");

    */

    tmp.clear();
    d.GetACC(tmp, bb[i].start_imu, bb[i].stop_imu);
    imu_t = bb[i].start_imu + Print(data, header, tmp, "jerk", dur);
    t = (imu_t*rate_emg)/rate_imu;
    Append(data, header, d.Acc()[imu_t].Time(), "time_max_jerk");
    Append(data, header, (d.Acc()[imu_t]).Processed(), "max_jerk");
    if (t >= d.EXG().isize())
      t = d.EXG().isize()-1;

    Append(data, header, d.EXG()[t].RMS(), "RMS_at_max_jerk");
    Append(data, header, d.EXG()[t].MDF(), "MDF_at_max_jerk");
    Append(data, header, d.EXG()[t].MNF(), "MNF_at_max_jerk");




    //==============================================================================

    d.GetMDF(tmp, bb[i].start_emg, bb[i].stop_emg);
    t = bb[i].start_emg + Print(data, header, tmp, "MDF", dur);
    imu_t = (t*rate_imu)/rate_emg;
    Append(data, header, d.EXG()[t].Time(), "time_max_MDF");
    Append(data, header, d.EXG()[t].RMS(), "max_MDF");

    Append(data, header, (d.Acc()[imu_t])[0], "acc_x_at_max_MDF");
    Append(data, header, (d.Acc()[imu_t])[1], "acc_y_at_max_MDF");
    Append(data, header, (d.Acc()[imu_t])[2], "acc_z_at_max_MDF");
     m = sqrt((d.Acc()[imu_t])[0]*(d.Acc()[imu_t])[0] + (d.Acc()[imu_t])[1]*(d.Acc()[imu_t])[1] + (d.Acc()[imu_t])[2]*(d.Acc()[imu_t])[2]);
    Append(data, header, m, "acc_mag_max_MDF");
    
    Append(data, header, (d.Gyro()[imu_t])[0], "gyro_x_at_max_MDF");
    Append(data, header, (d.Gyro()[imu_t])[1], "gyro_y_at_max_MDF");
    Append(data, header, (d.Gyro()[imu_t])[2], "gyro_z_at_max_MDF");
    m = sqrt((d.Gyro()[imu_t])[0]*(d.Gyro()[imu_t])[0] + (d.Gyro()[imu_t])[1]*(d.Gyro()[imu_t])[1] + (d.Gyro()[imu_t])[2]*(d.Gyro()[imu_t])[2]);
    Append(data, header, m, "gyro_mag_max_MDF");

    Append(data, header, d.EXG()[t].RMS(), "RMS_at_max_MDF");
   

    d.GetMNF(tmp, bb[i].start_emg, bb[i].stop_emg);
    t = bb[i].start_emg + Print(data, header, tmp, "MNF", dur);
    imu_t = (t*rate_imu)/rate_emg;
    Append(data, header, d.EXG()[t].Time(), "time_max_MNF");
    Append(data, header, d.EXG()[t].RMS(), "max_MNF");

    Append(data, header, (d.Acc()[imu_t])[0], "acc_x_at_max_MNF");
    Append(data, header, (d.Acc()[imu_t])[1], "acc_y_at_max_MNF");
    Append(data, header, (d.Acc()[imu_t])[2], "acc_z_at_max_MNF");
    m = sqrt((d.Acc()[imu_t])[0]*(d.Acc()[imu_t])[0] + (d.Acc()[imu_t])[1]*(d.Acc()[imu_t])[1] + (d.Acc()[imu_t])[2]*(d.Acc()[imu_t])[2]);
    Append(data, header, m, "acc_mag_max_MNF");
    
    Append(data, header, (d.Gyro()[imu_t])[0], "gyro_x_at_max_MNF");
    Append(data, header, (d.Gyro()[imu_t])[1], "gyro_y_at_max_MNF");
    Append(data, header, (d.Gyro()[imu_t])[2], "gyro_z_at_max_MNF");
    m = sqrt((d.Gyro()[imu_t])[0]*(d.Gyro()[imu_t])[0] + (d.Gyro()[imu_t])[1]*(d.Gyro()[imu_t])[1] + (d.Gyro()[imu_t])[2]*(d.Gyro()[imu_t])[2]);
    Append(data, header, m, "gyro_mag_max_MNF");

    Append(data, header, d.EXG()[t].RMS(), "RMS_at_max_MNF");
   

    //=========================== RISE =====================================

    
    if (i==0)
      fprintf(p, "%s\n", header.c_str());
     
    fprintf(p, "%s\n", data.c_str());

  }

  fclose(p);
}
