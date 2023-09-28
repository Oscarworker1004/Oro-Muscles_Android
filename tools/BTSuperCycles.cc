#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include "base/StringUtil.h"
#include "eeg/Analyze.h"

int main( int argc, char** argv )
{

  commandArg<string> fileCmmd("-i","input file (csv)");
  commandArg<string> resCmmd("-o","output file");
  commandArg<string> hintCmmd("-hints","hint coordinates in ms (start stop state)", "");
   commandLineParser P(argc,argv);
  P.SetDescription("Testing the file parser.");
  P.registerArg(fileCmmd);
  P.registerArg(resCmmd);
  P.registerArg(hintCmmd);
 
  P.parse();
  
  string fileName = P.GetStringValueFor(fileCmmd);
  string resultName = P.GetStringValueFor(resCmmd);
  string hintName = P.GetStringValueFor(hintCmmd);


  FlatFileParser parser;
  
  parser.Open(fileName);

  string fileList;

  parser.ParseLine();
  while (parser.ParseLine()) {
    if (parser.GetItemCount() == 0)
      continue;
    fileList += parser.Line();
    fileList += ",";
  }

  
  return MainAnalyze(fileList,
		     resultName,
		     "out.txt",
		     "tmp.txt",
		     "tb.txt",
		     hintName,
		     "simpleannot.txt",
		     4);


  
  
}
