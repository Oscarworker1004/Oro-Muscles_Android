#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"



string Clean(const string & in)
{
  StringParser p;
  p.SetLine(in, ",");
  return p.AsString(0);
}

void DoOne(const string & fileName)
{
  FlatFileParser parser;
  
  parser.Open(fileName);

  StringParser p;
  p.SetLine(fileName, "Foot");
  //cout << fileName << endl;
  string ref = p.AsString(0) + "Events" + p.AsString(1);
  p.SetLine(ref, ".processed.txt");
  ref = p.AsString(0);
  cout << ref << endl;
  
  parser.ParseLine();
  parser.ParseLine();
  int last = 0;

  cout << fileName << endl;
  svec<double> events;
  while (parser.ParseLine()) {
    if (parser.GetItemCount() == 0)
      continue;

    if (parser.AsString(1) == "Data")
      break;
    if (parser.GetItemCount() == 8 && last < 8) {
      cout << parser.AsString(7) << " start " << Clean(parser.AsString(0)) << endl;
      events.push_back(parser.AsFloat(0));
    }
    if (parser.GetItemCount() < 8 && last == 8) {
      cout << parser.AsString(7) << " stop  " << Clean(parser.AsString(0)) << endl;    
      events.push_back(parser.AsFloat(0));
    }

    
    last = parser.GetItemCount();
    
  }

  FlatFileParser parserRef;
  
  parserRef.Open(ref);

  parserRef.ParseLine();
  parserRef.ParseLine(",");

  int i, j;

  cout << "\tsample\tref1\tbest1\tdiff1\tref2\tbest2\tdiff2\tref3\tbest3\tdiff3" << endl;
  cout << "REF\t" << ref;
  for (i=0; i<parserRef.GetItemCount(); i++) {
    double d = parserRef.AsFloat(i);
    double best = -1.;
    double dist = 9999999.;
    for (j=0; j<events.isize(); j++) {
      double a = d-events[j];
      if (a < 0.)
	a = -a;
      if (a < dist) {
	dist = a;
	best = events[j];
      }
    }
    cout << "\t" << d << "\t" << best << "\t" << dist;
  }
  cout << endl;
}

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

  while (parser.ParseLine()) {
    if (parser.GetItemCount() == 0)
      continue;

    string name = parser.AsString(0);
    name += ".processed.txt";

    DoOne(name);
  }
  return 0;
}
