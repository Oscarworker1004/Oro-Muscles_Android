#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include "base/StringUtil.h"
#include "eeg/EHMM.h"


int main( int argc, char** argv )
{

  commandArg<string> fileCmmd("-i","input file");
  commandArg<string> resCmmd("-o","output file", "");
  commandArg<string> outCmmd("-mo","model output", "");
  commandArg<string> tbCmmd("-tb","traceback output", "");
  commandArg<int> iCmmd("-split","iterations with model split", 4);
  commandLineParser P(argc,argv);
  P.SetDescription("Testing the file parser.");
  P.registerArg(fileCmmd);
  //P.registerArg(resCmmd);
  P.registerArg(outCmmd);
  P.registerArg(tbCmmd);
  P.registerArg(iCmmd);
 
  P.parse();
  
  string fileName = P.GetStringValueFor(fileCmmd);
  //string resultName = P.GetStringValueFor(resCmmd);
  string outName = P.GetStringValueFor(outCmmd);
  string tbName = P.GetStringValueFor(tbCmmd);
  int split = P.GetIntValueFor(iCmmd);
 
  int i, j;

  //comment. ???
  FlatFileParser parser;
  
  parser.Open(fileName);
  svec<double> sig;

  
  svec<HMMVec> v;
  
  while (parser.ParseLine()) {
    if (parser.GetItemCount() == 0)
      continue;
    HMMVec vv;
    vv.resize(parser.GetItemCount()-1);
    vv.Label() = parser.AsString(0);
    for (i=1; i<parser.GetItemCount(); i++) {
      vv[i-1] = parser.AsFloat(i);
    }
    v.push_back(vv);
  }

  EHMM hmm;

  hmm.Setup(v.isize(), v[0].isize());
  int n = split;
  for (i=0; i<n; i++) {
    hmm.Process(v, true);
  }
  hmm.Process(v, false);

  cout << "FINAL" << endl;
  hmm.Process(v, false);
  cout << "DONE" << endl;

  svec<int> tb;
  hmm.DynProg(tb, v);

  if (tbName != "") {
    FILE * p = fopen(tbName.c_str(), "w");
    for (i=0; i<v.isize(); i++) {
      const HMMVec & vv = hmm[tb[i]];
      fprintf(p, "%s %s %d\n", v[i].Label().c_str(), vv.Label().c_str(), tb[i]);
    }
    fclose(p);
 }

  
  if (outName != "")
    hmm.Write(outName);

  
  return 0;
}
