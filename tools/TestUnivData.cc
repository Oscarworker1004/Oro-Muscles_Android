#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include "src/UnivData.h"


int main( int argc, char** argv )
{

  commandArg<string> fileCmmd("-i","input file");
  commandArg<string> outCmmd("-o","output file");
  commandLineParser P(argc,argv);
  P.SetDescription("Testing the file parser.");
  P.registerArg(fileCmmd);
  P.registerArg(outCmmd);

  P.parse();
  
  string fileName = P.GetStringValueFor(fileCmmd);
  string outName = P.GetStringValueFor(outCmmd);

  UnivDataRead in;
  UnivDataWrite out;

  in.OpenFile(fileName);
  out.OpenFile(outName);
  
 
  return 0;
}
