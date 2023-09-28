#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include "signal/StreamAnalyzer.h"




int main( int argc, char** argv )
{

  commandArg<string> fileCmmd("-i","input file");
  commandLineParser P(argc,argv);
  P.SetDescription("Testing the file parser.");
  P.registerArg(fileCmmd);

  P.parse();
  
  string fileName = P.GetStringValueFor(fileCmmd);
 
  int i;
  
  //comment. ???
  FlatFileParser parser;
  
  parser.Open(fileName);

  svec<double> emg, emg_raw;
  svec<double> rms, rms_raw;

  parser.ParseLine();
  parser.ParseLine();
  
  while (parser.ParseLine()) {
    if (parser.GetItemCount() == 0)
      continue;

    emg_raw.push_back(parser.AsFloat(1));
    rms_raw.push_back(parser.AsFloat(2));
    
    if (parser.AsString(1) == "Data")
      break;
  }

  NullAndCrop(emg, rms, emg_raw, rms_raw, 0);

  
  //for (i=0; i<rms.isize(); i++) {
  StreamAnalyzer sa;
  int delay = 250;
  sa.SetDelay(delay);
  
  FILE * p = fopen("peak", "w");
  
  for (i=40000; i<100000; i++) {
  //for (i=delay; i<rms.isize(); i++) {
    //cout << i << endl;
    bool b = sa.Feed(rms[i], emg[i]);

    
    
    if (b) {
      //cout << " " << emg[i] << " EVENT";
      //fprintf(p, "%f\n", rms[i-delay]);
      if (sa.GetAUC()/100000*500 < 1500)
	fprintf(p, "%f\n", sa.GetAUC()/100000*500);
      else
	fprintf(p, "1500\n");
    } else {
      if (sa.IsSilence())
	fprintf(p, "-50.\n");
      else
	fprintf(p, "0.\n");
    }
    
    cout << rms[i-delay] << endl;
    

    /*
    cout << rms[i] << endl;
    fprintf(p, "%f\n", sa.GetFloor());
    */
    
  }
  
  fclose(p);
  
  return 0;
}
