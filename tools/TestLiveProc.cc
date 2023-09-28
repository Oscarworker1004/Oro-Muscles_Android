#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include <math.h>
#include "src/RecordCtrl.h"



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
  
  RecordCtrl gRecCtrl;

  int len = 256;
  int n = 15;
  int bytes = 0;

  FILE * p2 = fopen("2dcoords.txt", "w");
  
  while (!in.IsEnd()) {
  
    svec<char> buf;
    buf.resize(len, 0);
    int i, j;
  
    for (i=0; i<len; i++) {
      in.Read(buf[i]);
    }

    gRecCtrl.SetDataBuffer(buf);

    bytes += len;
    
    svec<double> out;
    cout << "Get buffer" << endl;
    int chunks = gRecCtrl.GetData(out, n);
    int k = 0;
    cout << "===== FRAME, bytes=" << bytes << " data size=" << out.isize() << " chunks=" << chunks << " ===================" << endl;
    for (i=0; i<chunks; i++) {
      for (j=0; j<n; j++) {
	//cout << i*n+j << endl;
	cout << out[i*n + j] << "\t";
      }
      if (n >= 15) {
	fprintf(p2, "%f\t%f\t", out[i*n + 9], out[i*n + 10]);
	fprintf(p2, "%f\t%f\t", out[i*n + 11], out[i*n + 12]);
	fprintf(p2, "%f\t%f\t%f\n", out[i*n + 13], out[i*n + 14], out[i*n + 2]);
      }
      cout << endl;
    }

  }

  fclose(p2);
  
  return 0;
}
