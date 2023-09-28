#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include <time.h>
#include "base/RandomStuff.h"

void Randomize()
{
  unsigned int time_ui = (unsigned int)( time(NULL) );
  srand( time_ui );
}


int main( int argc, char** argv )
{

  commandArg<string> fileCmmd("-i","server main directory");
  commandArg<string> userCmmd("-u","user name (no blanks)");
  commandArg<string> projCmmd("-p","project ID (5 digits or letters)");
  commandArg<string> confCmmd("-c","output config file for client", "zeroconf.txt");
  commandLineParser P(argc,argv);
  P.SetDescription("Creates an empty server directory.");
  P.registerArg(fileCmmd);
  P.registerArg(userCmmd);
  P.registerArg(projCmmd);
 
  P.parse();
  
  string fileName = P.GetStringValueFor(fileCmmd);
  string userName = P.GetStringValueFor(userCmmd);
  string projName = P.GetStringValueFor(projCmmd);
  string confName = P.GetStringValueFor(confCmmd);

  


  return 0;
}
