#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include "base/StringUtil.h"
#include "eeg/EHMM.h"
#include "eeg/Freq.h"
#include "eeg/BTLData.h"
#include "eeg/Annotator.h"
#include "eeg/TimeRefine.h"
#include "eeg/Hints.h"
#include "eeg/IMUTrans.h"
#include "eeg/Analyze.h"



int main( int argc, char** argv )
{

  commandArg<string> fileCmmd("-i","input file (csv)", "");
  commandArg<string> listCmmd("-l","input file LIST (csv)", "");
  commandArg<string> resCmmd("-o","output file","");
  commandArg<string> outCmmd("-mo","model output", "");
  commandArg<string> miCmmd("-mi","model input", "");
  commandArg<string> tbCmmd("-tb","traceback output", "");
  commandArg<string> annotCmmd("-annot","annotation", "simpleannot.txt");
  commandArg<string> hintCmmd("-hints","hint coordinates in ms (start stop state)", "");
  commandArg<int> iCmmd("-split","iterations with model split", 4);
  commandArg<bool> hCmmd("-help","show this help screen");
  commandLineParser P(argc,argv);
  P.SetDescription("Analyzing EMG/IMU signals, main executable.");
  P.registerArg(fileCmmd);
  P.registerArg(listCmmd);
  P.registerArg(resCmmd);
  P.registerArg(outCmmd);
  P.registerArg(miCmmd);
  P.registerArg(tbCmmd);
  P.registerArg(hintCmmd);
  P.registerArg(annotCmmd);
  P.registerArg(iCmmd);
 
  P.parse();
  
  string fileName = P.GetStringValueFor(fileCmmd);
  string listName = P.GetStringValueFor(listCmmd);
  string resultName = P.GetStringValueFor(resCmmd);
  if (resultName == "")
    resultName = fileName + ".processed";
  
  string outName = P.GetStringValueFor(outCmmd);
  
  string miName = P.GetStringValueFor(miCmmd);
  string tbName = P.GetStringValueFor(tbCmmd);
  string hintName = P.GetStringValueFor(hintCmmd);
  string annotName = P.GetStringValueFor(annotCmmd);
  int split = P.GetIntValueFor(iCmmd);

  if (listName == "" && fileName == "") {
    P.registerArg(hCmmd);
    //cout << "You have to specify either -i or -l!" << endl;
    //cerr << "You have to specify either -i or -l!" << endl;
 
    P.parse();

     return -1;
  }

  if (fileName != "") {
    cout << "Write to root " << resultName << endl;
    return MainAnalyze(fileName,
		       resultName,
		       outName,
		       miName,
		       tbName,
		       hintName,
		       annotName,
		       split);
  }
  if (listName != "") {
    FlatFileParser parser;
  
    parser.Open(listName);
    cout << "Batch processing." << endl;
    while (parser.ParseLine()) {
      cout << parser.Line() << endl;
      if (parser.GetItemCount() == 0)
	continue;    
      cout << "Write to root " << parser.AsString(0) + ".processed.txt" << endl;
      MainAnalyze(parser.AsString(0),
		  parser.AsString(0) + ".processed.txt",
		  outName,
		  miName,
		  tbName,
		  hintName,
		  annotName,
		  split);

    }
  }

  
  /* 
  int i, j;



  BTLData d;
  svec<double> data;

  d.Read(fileName);
  d.GetEXGData(data);

 
  cout << "Data points: " << data.isize() << endl;
  
  FreqSequence seq;
  int frame = 128;
  seq.SetFrame(frame);
  seq.Feed(data);
    
  svec<HMMVec> v;
  v.resize(seq.isize());

  cout << "FFT frames: " << seq.isize() << endl;
  
  cout << "Copy data for HMM" << endl;
  
  for (i=0; i<seq.isize(); i++) {
    v[i].resize(seq[i].isize());
    v[i].Time() = d.EXG()[i*frame/2].Time(); 
    v[i].Label() = "[" + Stringify(i*64) + "]";

    cout << v[i].Time() << endl;
    
    for (j=0; j<seq[i].isize(); j++) {
      v[i][j] = seq[i][j];
    }
  }

  // Copy data
  for (i=0; i<v.isize(); i++) {
    IMUData m;
    m.Time() = v[i].Time();
    for (j=0; j<seq[i].isize(); j++) {
      m.push_back(seq[i][j]);
    }

    d.Spectra().push_back(m);
  
  }

  cout << "Copied data, size=" << d.Spectra().isize() << endl;

  
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

  if (hintName != "") {
    Hints h;
    // Set start time!!
    h.Offset() = d.EXG()[0].Time();
    h.Read(hintName);
    
    hmm.SetHints(h.Data());
  }

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
  annot.Annotate(d, frame);

  TimeRefine refine;
  refine.Refine(d);

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
  
  
  d.Write(resultName);

  cout << "All done!" << endl;
  
  return 0;*/
  
}
