#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include "eeg/Freq.h"

//#include "eeg/EDF.h"
#include "eeg/BTLData.h"



int main( int argc, char** argv )
{

  commandArg<string> fileCmmd("-i","input file");
  commandArg<string> outCmmd("-o","output file");
  commandArg<string> rCmmd("-t","time domain data", "try.raw");
  commandLineParser P(argc,argv);
  P.SetDescription("Process an EDF.");
  P.registerArg(fileCmmd);
  P.registerArg(outCmmd);
  P.registerArg(rCmmd);
 
  P.parse();
  
  string fileName = P.GetStringValueFor(fileCmmd);
  string outName = P.GetStringValueFor(outCmmd);
  string timeName = P.GetStringValueFor(rCmmd);
 
  int i, j;


  //FlatFileParser parser;
  
  //parser.Open(fileName);

  svec<double> data;

  FILE * p = fopen(timeName.c_str(), "w");
  /* parser.ParseLine();
  parser.ParseLine();
  
  for (i=0; i<25; i++) {
    fprintf(p, "0\n");
    parser.ParseLine();
  }
  
  while (parser.ParseLine()) {
    if (parser.GetItemCount() == 0)
      break;
    data.push_back(parser.AsFloat(1));
    fprintf(p, "%f\n", parser.AsFloat(1));
    //cout << parser.AsFloat(1) << endl;

  }
  //return 0;
  */
  BTLData d;
  d.Read(fileName);
  d.GetEXGData(data);

  for (i=0; i<data.isize(); i++)
    fprintf(p, "%f\n", data[i]);
  fclose(p);

  cout << "Data points: " << data.isize() << endl;
  
  FreqSequence seq;
  seq.SetFrame(128);
  seq.Feed(data);
  string name = outName;
    
  seq.Print(name);

  
  
  
  return 0;
}
