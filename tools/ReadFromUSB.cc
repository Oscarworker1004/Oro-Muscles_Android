#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include "util/mutil.h"
#include "base/StringUtil.h"
#include "signal/USBReader.h"



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
  urd.StartWrite("ctest.bin");

  cout << "Sleep" << endl;
  usleep(25000000); // 1 sec

  cout << "Stopping" << endl;
  urd.Stop();

  cout << "All done!" << endl;
  
  /*
  FILE * usb_stream = fopen("/dev/ttyUSB0", "rb");


  int k = 0;
  while(true) {
    char c;

    if (fread(&c, 1, 1, usb_stream) == 1) {
      cout << (int)c << "\tread: " << k << endl;
    } else {
      //???
    }
    k++;
    if (k == 2000)
      break;
  }

  cout << "Done!" << endl;

  fclose(usb_stream);
  */
  


  

  
  return 0;
}
