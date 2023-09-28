#include "eeg/Analyze.h"
#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include "base/Config.h"
#include "base/StringUtil.h"
#include "eeg/EHMM.h"
#include "eeg/Freq.h"
#include "eeg/BTLData.h"
#include "eeg/Annotator.h"
#include "eeg/TimeRefine.h"
#include "eeg/Hints.h"
#include "eeg/IMUTrans.h"
#include "eeg/SuperCycle.h"
#include "signal/Bin2CSV.h"

#include "visual/Bitmap.h"
#include "visual/GraphPlotter.h"


#include <locale>
#include <clocale>


int SuperCycles(const string & fileName,
		const string & outName,
		const string & hintName)
{
  int i;

  cout << "Read " << fileName << endl;
  cout << "Hints " << hintName << endl;
  cout << "Out " << outName << endl;
  
  SuperCycleProc r;
  r.Read(fileName, hintName);

  //sc.Process(hintName);
  CycleLane supers;
  r.Process(supers);
  
  //all.Write(outName);

  FILE * p = fopen(outName.c_str(), "w");

  for (i=0; i<supers.isize(); i++) {
    fprintf(p, "%f\t%f\t%s\n", supers[i].Start(), supers[i].Stop(), supers[i].Name().c_str());
  }

  fclose(p);


  return 0;
}



int MainAnalyze(const string & rawFileName,
		const string & resultName,
		const string & outName,
		const string & miName,
		const string & tbName,
		const string & hintName,
		const string & annotName,
		int split)
{
  //   std::setlocale(LC_ALL, "en_US.utf8"); // for C and C++ where synced with stdio


  std::setlocale(LC_ALL, "en_US.utf8"); // for C and C++ where synced with stdio
  std::locale::global(std::locale("en_US.utf8")); // for C++
  std::cout.imbue(std::locale("C"));


  string fileName = rawFileName;
  
  //td::cout.imbue(std::locale("C"));
  int i, j;

  Config cfg;

  string cfgname = "btl_config.cfg";
  FILE * p = fopen(cfgname.c_str(), "r");
  if (p != NULL) {
    fclose(p);
    cfg.Read(cfgname);
  }

  if (strstr(fileName.c_str(), ",") != NULL) {
    cout << "Computing super-cycles" << endl;
    return SuperCycles(fileName, resultName, hintName);
    cfg.Print("parameters_meta.txt");
  }

  
  cout << "ENTER" << endl;

  BTLData d;

  d.SetConfig(&cfg);
  
  svec<double> data;

  //=================================================
  if (strstr(fileName.c_str(), ".csv") == NULL && strstr(fileName.c_str(), ".processed") == NULL) {
    string convName = fileName + ".csv";
    cout << "WARNING: converting binary to .csv: " << fileName << endl;
    
    Bin2CSV b2c;
    b2c.Convert(convName, fileName);
    fileName = convName;
  }

  
  d.Read(fileName);
  d.GetEXGData(data);

  // cout << "CHECK " << d.EXG().isize() << endl;

  //cout << "Data points: " << data.isize() << endl;
  
  FreqSequence seq;
  int frame = 128;
  seq.SetFrame(frame);
  seq.Feed(data);
    
  svec<HMMVec> v;
 
  //cout << "FFT frames: " << seq.isize() << endl;
  
  cout << "Copy data for HMM" << endl;

  // Use EMG data
  bool bUseEMG = true;

  if (bUseEMG) {
    v.resize(seq.isize());
    for (i=0; i<seq.isize(); i++) {
      v[i].resize(seq[i].isize());
      v[i].Time() = d.EXG()[i*frame/2].Time(); 
      v[i].Label() = "[" + Stringify(i*64) + "]";
      
      for (j=0; j<seq[i].isize(); j++) {
	v[i][j] = seq[i][j];
      }
    }
  } else {
    svec < svec < double > > imu;
    d.GetIMUData(imu);
    v.resize(imu.isize());
    
    for (i=0; i<imu.isize(); i++) {
      v[i].resize(imu[i].isize());
      v[i].Time() = d.Acc()[i].Time(); 
      v[i].Label() = "[" + Stringify(i) + "]";
      
      for (j=0; j<imu[i].isize(); j++) {
	v[i][j] = imu[i][j];
      }
    }
  }


  

  // Copy data, clear spectra
  d.Spectra().clear();
 
  for (i=0; i<v.isize(); i++) {
    IMUData m;
    m.Time() = v[i].Time();
    for (j=0; j<seq[i].isize(); j++) {
      m.push_back(seq[i][j]);
    }
    
    d.Spectra().push_back(m);
  
  }


  
  // ===================================================
  // Clear cycles
  d.ClearCycles();

  
  //cout << "Copied data, size=" << d.Spectra().isize() << endl;

  
  EHMM hmm;


  if (miName != "") {
    cout << "Read model: " << miName << endl;
    hmm.Read(miName);
  }

  hmm.Setup(v.isize(), v[0].isize());



  cout << "Start HMM." << endl;
  
  int n = split;
  for (i=0; i<n; i++) {
    hmm.Process(v, true);
  }

  Hints h;

  // Set start time!!
  h.Offset() = d.EXG()[0].Time();

  if (hintName != "") {
    h.Read(hintName);
    h.FillShapes(d);
    
    hmm.SetHints(h.Data());
  }
  

  hmm.BeStupid(false);
  
  hmm.Process(v, false);

  // Just in case... ?
  //hmm.ClearHints();
  
  cout << "FINAL" << endl;
  hmm.Process(v, false);
  cout << "DONE" << endl;
  
  
  svec<int> tb;
  hmm.DynProg(tb, v);

  if (tbName != "") {
    FILE * p = fopen(tbName.c_str(), "w");
    for (i=0; i<v.isize(); i++) {
      const HMMVec & vv = hmm[tb[i]];
      fprintf(p, "%s %s %d\n", v[i].Label().c_str(), vv.Label().c_str(), tb[i]);
    }
    fclose(p);
  }
  
  if (outName != "")
    hmm.Write(outName);

  // Annotate the original data
  cout << "Annotating data" << endl; 
  
  Annotator annot;
  annot.SetData(tb);

  // Set frame correctly!!!!
  if (!bUseEMG)
    frame = 10;
  annot.Apply(d, frame);
  
  // ORIGINAL PLACE OF TimeRefine!

  

  IMUTrans t;
  for (i=0; i<d.Acc().isize(); i++)
    t.AddAcc(d.Acc()[i][0], d.Acc()[i][1], d.Acc()[i][2]);

  for (i=0; i<d.Gyro().isize(); i++)
    t.AddGyro(d.Gyro()[i][0], d.Gyro()[i][1], d.Gyro()[i][2]);

  t.Process();

  for (i=0; i<t.Acc().isize(); i++)
    d.Acc()[i].Processed() = t.Acc()[i];

  for (i=0; i<t.Gyro().isize(); i++)
    d.Gyro()[i].Processed() = t.Gyro()[i];

  
  d.SmoothIMU();

  // NEW PLACE
  TimeRefine refine;
  refine.SetConfig(&cfg);
  refine.Refine(d);
  refine.ApplyHints(d, h);
 

  
  
  Annotation a;
  annot.SetRates(500, 100);
  annot.Annotate(a, d, 500, 100);

  cout << "SETS 0 " << h.NumBlocks() << endl;
  refine.ApplySets(h, d);
  cout << "SETS 1 " << h.NumBlocks() << endl;
  
  a.Write(annotName);

  cout << "Writing peak.csv" << endl;
  annot.MakeGenAnnot(d, h, resultName + ".peak.csv", false);
  cout << "Writing cycle.csv" << endl;
  annot.MakeGenAnnot(d, h, resultName + ".cycle.csv", true);
  cout << "Writing EMG.csv" << endl;
  annot.MakeEMGAnnot(d, resultName + ".EMG.csv");
  cout << "Writing ACC.csv" << endl;
  annot.MakeAccAnnot(d, resultName + ".ACC.csv");
  cout << "Writing GYRO.csv" << endl;
  annot.MakeGyroAnnot(d, resultName + ".GYRO.csv");
  cout << "Writing Curve.csv" << endl;
  annot.MakeCurveAnnot(d, resultName + ".Curve.csv");
  cout << "Writing annot" << endl;
  annot.MakeMasterAnnot(d, h, resultName + ".annot");

  h.WriteDB(resultName + ".hints.db");

  cout << "CHECK 2 " << h.NumShapes() << endl;

  // MGG!!!!!!!!!!!!!!!!!!!!!!
  int doAuto = 0;
  cfg.Update("do_auto_cycle", doAuto);
  if (doAuto > 0) { 
    cout << "Auto-annotate" << endl;
    d.AutoAnnotate();
  }

  cout << "CHECK " << d.EXG().isize() << endl;
  d.Write(resultName);
  

  //=================================================================================
  //=================================================================================
  //=================================================================================
  //=================================================================================
  //=================================================================================
  // RE-FACTOR THIS!!!
  GraphPlotter plot;
  plot.PlotEMGIMUGraphs(resultName + ".jpg", resultName);

  svec < svec < double > > heatmap;

  // Make Heatmap
  svec<IMUInterval> for_heat;
  refine.MakeHeatmap(heatmap, for_heat, d);

  svec<string> h_labels;
  for (i=0; i<for_heat.isize(); i++) {
    StringParser pp;
    pp.SetLine(for_heat[i].Name(), "_(");
    h_labels.push_back(pp.AsString(0));
  }

  plot.PlotHeatMaps(resultName + ".heat.jpg", heatmap, h_labels); 
  
  /*
  if (false) {
  int i;
  //comment. ???
  FlatFileParser parser;
  
  parser.Open(resultName);

  svec<double> x, y, z;
  svec<double> a, b, c;
  svec<double> r, e;

  svec< svec < double > > freq;
  
  bool bYes = false;
  bool bGyr = false;
  bool bExg = false;
  bool bSpec = false;
  
  while (parser.ParseLine()) {
    if (parser.GetItemCount() == 0)
      continue;
    if (parser.AsString(0) == "ACCEL") {
      bYes = true;
      parser.ParseLine();
      continue;
    }
    if (parser.AsString(1) == "Data") {
      bYes = false;
      bGyr = false;
      bExg = false;
      bSpec = false;
      //continue;
    }
    if (parser.AsString(0) == "GYRO") {
      bGyr = true;
      parser.ParseLine();
      continue;
    }
    if (parser.AsString(0) == "EXG") {
      bExg = true;
      parser.ParseLine();
      continue;
    }
    if (parser.AsString(0) == "SPECTRA") {
      bSpec = true;
      parser.ParseLine();
      continue;
    }

    if (bYes) {
      x.push_back(parser.AsFloat(1));
      y.push_back(parser.AsFloat(2));
      z.push_back(parser.AsFloat(3));
    }

    if (bGyr) {
      a.push_back(parser.AsFloat(1));
      b.push_back(parser.AsFloat(2));
      c.push_back(parser.AsFloat(3));
    }
    if (bExg) {
      e.push_back(parser.AsFloat(1));
      r.push_back(parser.AsFloat(2));
    }

    if (bSpec) {
      svec<double> d;
      //cout << parser.Line() << endl;
      for (i=1; i<9; i++) {
	d.push_back(parser.AsFloat(i));
      }
      freq.push_back(d);
    }
  }
 

  Bitmap bmp;

  
  bmp.LoadFonts("Courier.bmp");

  double scale = 1.;

  bmp.SetSize(20+(int)(x.isize()*scale), 430);

  GraphPlotter plot;

  int topY = bmp.Y() - 30;

  int dist = 80;
  int offForAnnot = 30;

  cout << "Start plot" << endl;
  
  plot.Plot(bmp, x, RGBPixel(0.99, 0.1, 0.1), scale, 12, 2, topY-dist);
  plot.Plot(bmp, y, RGBPixel(0.88, 0.9, 0.1), scale, 12, 2, topY-dist);
  plot.Plot(bmp, z, RGBPixel(0.5, 0.5, 0.99), scale, 12, 2, topY-dist);

  cout << a.isize() << endl;
  plot.Plot(bmp, a, RGBPixel(0.99, 0.1, 0.1), scale, 0.1, 2, topY-2*dist);
  plot.Plot(bmp, b, RGBPixel(0.88, 0.9, 0.1), scale, 0.1, 2, topY-2*dist);
  plot.Plot(bmp, c, RGBPixel(0.5, 0.5, 0.99), scale, 0.1, 2, topY-2*dist);

  svec<double> es, rs;
  plot.DownScale(es, e, 5);
  plot.DownScale(rs, r, 5);

  plot.Plot(bmp, es, RGBPixel(0.3, 0.9, 0.5), scale, 0.1, 2, topY-3*dist);
  plot.Plot(bmp, rs, RGBPixel(0.9, 0.9, 0.9), scale, 0.1, 2, topY-3*dist);
  

  if (freq.isize() > 0) {
    cout << "Plot spectra" << endl;
    plot.Spectra(bmp, freq, RGBPixel(0.99, 0.99, 0.99), 12.8, 12.8, 0.00005, 2+6.4, topY-4*dist - 80);
  }

  
  plot.SetTimeMarks(bmp, 100, 2, 2, RGBPixel(0.99, 0.99, 0.99));
  
  //  bmp.Text(20, 12, "Data: walking", RGBPixel(0.99, 0.99, 0.99));
  
  cout << "Save" << endl;
  bmp.WriteJPG(resultName + ".jpg", 95);

  cout << "done!" << endl;
  }*/


  //=================================================================================
  //=================================================================================
  //=================================================================================

  
  cout << "All done!" << endl;

  int test = 22365;
  cout << "Locale test: " << test << endl;

  cfg.Print("parameters.txt");
  
  cout << "VERSION CHECK!" << endl;
  return 0;

}

