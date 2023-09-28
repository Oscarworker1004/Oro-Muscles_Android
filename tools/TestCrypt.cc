#include <string>
#include "base/CommandLineParser.h"
#include "base/Crypt.h"



int main( int argc, char** argv )
{

  commandArg<string> fileCmmd("-i","input file");
  commandArg<string> outCmmd("-o","output file");
  commandArg<bool> enCmmd("-e","encrypt", false);
  commandArg<bool> deCmmd("-d","decrypt", false);
  commandLineParser P(argc,argv);
  P.SetDescription("En/de-crypting a file.");
  P.registerArg(fileCmmd);
  P.registerArg(outCmmd);
  P.registerArg(enCmmd);
  P.registerArg(deCmmd);
 
  P.parse();

  
  
  string fileName = P.GetStringValueFor(fileCmmd);
  string outName = P.GetStringValueFor(outCmmd);
  bool enc = P.GetBoolValueFor(enCmmd);
  bool dec = P.GetBoolValueFor(deCmmd);

  if (!enc && !dec) {
    cout << "Specify either -e or -de!" << endl;
    return -1;
  }


  Crypt c;

  if (enc) {
    c.Encrypt(fileName, outName);
  }
  if (dec) {
    c.Decrypt(fileName, outName);
  }
  
  return 0;
}
