#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include "signal/Smooth.h"


int main( int argc, char** argv )
{

  commandArg<string> fileCmmd("-i","input file");
  commandLineParser P(argc,argv);
  P.SetDescription("Testing the file parser.");
  P.registerArg(fileCmmd);
 
  P.parse();
  
  string fileName = P.GetStringValueFor(fileCmmd);
 

  //comment. ???
  FlatFileParser parser;
  
  parser.Open(fileName);

  svec<double> d;
  while (parser.ParseLine()) {
    if (parser.GetItemCount() == 0)
      continue;
    d.push_back(parser.AsFloat(0));
      
  }

  Smooth s;
  s.SetDecay(1.);
  s.SetWin(40);
  s.ApplyZK(d, 1.);
  s.ApplyZK(d, 1.);
  s.ApplyZK(d, 1.);

  s.ApplyZK(d, 1.);
  s.ApplyZK(d, 1.);
  s.ApplyZK(d, 1.);

  for (int i=0; i<d.isize(); i++)
    cout << d[i] << endl;
  
  return 0;
}
