#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"



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
  parser.ParseLine();
  parser.ParseLine();
  string name;
  int from = -1;
  int to = -1;
  while (parser.ParseLine()) {
    if (parser.GetItemCount() == 0)
      continue;
    if (parser.AsString(1) == "Data")
      break;

    if (parser.GetItemCount() == 8) {
      name = parser.AsString(7);
      if (from == -1)
	from = (int)(1000.*parser.AsFloat(0));
      to = (int)(1000.*parser.AsFloat(0));
    } else {
      if (from >= 0) {
	cout << name << "\t" << from << "\t" << to << endl;
	from = -1;
      }
    }        
  }
  return 0;
}
