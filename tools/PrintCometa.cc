#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include "util/mutil.h"


int main( int argc, char** argv )
{

  commandArg<string> fileCmmd("-i","input file");
  commandLineParser P(argc,argv);
  P.SetDescription("Testing the file parser.");
  P.registerArg(fileCmmd);
 
  P.parse();
  
  string fileName = P.GetStringValueFor(fileCmmd);


  CMReadFileStream in;
  in.Open(fileName.c_str());

  short tmp;

  while (!in.IsEnd()) {
    in.Read(tmp);
    cout << tmp << endl;
  }

  return 0;
}
