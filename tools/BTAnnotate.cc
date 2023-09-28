#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include "eeg/BTLData.h"
#include "eeg/Annotator.h"
#include "eeg/Hints.h"



int main( int argc, char** argv )
{

  commandArg<string> fileCmmd("-i","input file");
  commandArg<string> outCmmd("-o","output file prefix");
  //commandArg<string> tbCmmd("-tb","traceback file");
  //commandArg<string> Cmmd("-i","input file");
  commandLineParser P(argc,argv);
  P.SetDescription("Annotate a BTL csv file.");
  P.registerArg(fileCmmd);
  P.registerArg(outCmmd);
  //P.registerArg(tbCmmd);
 
  P.parse();
  
  string fileName = P.GetStringValueFor(fileCmmd);
  // string tbName = P.GetStringValueFor(tbCmmd);
  string resultName = P.GetStringValueFor(outCmmd);


  int frame = 128;

  BTLData d;
  d.Read(fileName);

  Annotator annot;
  //annot.SetData(tb);

  //annot.Apply(d, frame);

  Annotation a;
  annot.SetRates(500, 100);
  annot.Annotate(a, d, 500, 100);

  //a.Write(annotName);


  //cout << "CHECK " << d.EXG().isize() << endl;
  //d.Write(resultName);
  Hints h;
  cout << "Writing peak.csv" << endl;
  annot.MakeGenAnnot(d, h, resultName + ".peak.csv", false);
  cout << "Writing EMG.csv" << endl;
  annot.MakeEMGAnnot(d, resultName + ".EMG.csv");
  cout << "Writing ACC.csv" << endl;
  annot.MakeAccAnnot(d, resultName + ".ACC.csv");
  cout << "Writing GYRO.csv" << endl;
  annot.MakeGyroAnnot(d, resultName + ".GYRO.csv");
  cout << "Writing Curve.csv" << endl;
  annot.MakeCurveAnnot(d, resultName + ".Curve.csv");

  
  //annot.ReadTB(tbName);
  
  //cout << "Got it." << endl;
  //data.Write("test.out");
  
  /*
  //comment. ???
  FlatFileParser parser;
  
  parser.Open(fileName);

  while (parser.ParseLine()) {
    if (parser.GetItemCount() == 0)
      continue;
      }*/
  return 0;
}
