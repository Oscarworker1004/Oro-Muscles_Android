#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"



int main( int argc, char** argv )
{

  commandArg<string> fileCmmd("-i","input file list");
  commandLineParser P(argc,argv);
  P.SetDescription("Runs EMG analysis.");
  P.registerArg(fileCmmd);
 
  P.parse();
  
  string fileName = P.GetStringValueFor(fileCmmd);
 

  //comment. ???
  FlatFileParser parser;
  
  parser.Open(fileName);

  while (parser.ParseLine()) {
    if (parser.GetItemCount() == 0)
      continue;

    string name = parser.Line();
    string cmmd = "~/Software/blacktop/BTAnalyze -i " + name + " -o out.txt > tmp";
    cout << "Running: " << cmmd << endl;
    int r = system(cmmd.c_str());

    //cmmd = "~/Software/blacktop/BTAnalyzeCore -i try.freq -mo try.models -tb try.traceback > tmp";
    
    //cout << "Running: " << cmmd << endl;
    //r = system(cmmd.c_str());
    
    //cout << "Running: " << cmmd << endl;
    r = system("~/Software/blacktop/EMGFreqPlotTime -i out.txt -o tmp.ps");

    cmmd = "convert tmp.ps " + name + ".png";
    r = system(cmmd.c_str());

    
  }
  return 0;
}
