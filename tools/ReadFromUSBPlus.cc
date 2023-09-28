#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include "util/mutil.h"
#include "base/StringUtil.h"
#include "signal/USBReader.h"
#include "signal/Bin2CSV.h"



int main( int argc, char** argv )
{

  /*  
  commandArg<string> fileCmmd("-i","input file");
  commandLineParser P(argc,argv);
  P.SetDescription("Testing the file parser.");
  P.registerArg(fileCmmd);
 
  P.parse();
  
  string fileName = P.GetStringValueFor(fileCmmd);
  */
 
  USBReader urd;

  cout << "Start write" << endl;
  urd.DoBuffer(true);
  urd.StartWrite("ctest.bin");

  //cout << "Sleep" << endl;

  int k = 0;
  int i, j;
  
  Bin2CSV b2c;

  CMReadBufferStream buf;
  buf.SetReserve(2048);
  buf.Open("");

  int lastSize = 0;
  
  while (k<25000000) {
    urd.CopyBuffer(buf);
    b2c.Read(buf);

    const AllData & data = b2c.GetData();

    // Min data length
    int n = data.isizeEMG()/5;
    cout << "nE=" << n << endl;
    if (data.isizeAcc() < n)
      n = data.isizeAcc();
    cout << "nA=" << n << endl;
    if (data.isizeGyr() < n)
      n = data.isizeGyr();
    
    cout << "n=" << n << endl;
    for (i=lastSize; i<n; i++) {
      cout << i << "\t";
      cout << data.GetEMG(0)[i*5] << "\t" << data.GetEMG(1)[i*5] << "\t";
      cout << data.GetAcc(0)[i] << "\t" << data.GetAcc(1)[i] << "\t" << data.GetAcc(2)[i] << "\t";
      cout << data.GetGyr(0)[i] << "\t" << data.GetGyr(1)[i] << "\t" << data.GetGyr(2)[i];
      cout << endl;
    }

    lastSize = n;
    
    
    int sl = 250000;
    k += sl;
    usleep(sl);
  }

  cout << "Stopping" << endl;
  urd.Stop();

  buf.Close();
  
  cout << "All done!" << endl;
  
  
  return 0;
}
