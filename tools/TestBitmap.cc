#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include "visual/Bitmap.h"


int main( int argc, char** argv )
{

  /*  commandArg<string> fileCmmd("-i","input file");
  commandArg<int> bCmmd("-from","from column");
  commandArg<int> eCmmd("-to","to column");
  commandArg<bool> nCmmd("-newline","add newline", false);
  commandLineParser P(argc,argv);
  P.SetDescription("Testing the file parser.");
  P.registerArg(fileCmmd);
  P.registerArg(bCmmd);
  P.registerArg(eCmmd);
  P.registerArg(nCmmd);
 
  P.parse();
  
  string fileName = P.GetStringValueFor(fileCmmd);
  int from = P.GetIntValueFor(bCmmd);
  int to = P.GetIntValueFor(eCmmd);
  bool bN = P.GetBoolValueFor(nCmmd);
 

  //comment. ???
  FlatFileParser parser;
  
  parser.Open(fileName);

  while (parser.ParseLine()) {
    if (parser.GetItemCount() == 0)
      continue;
  }
  */

  Bitmap bmp;

  bmp.SetSize(400, 200);

  cout << "Loading" << endl;
  bmp.LoadFonts("fonts/Courier.bmp");
  cout << "Text" << endl;
  bmp.Text(20, 20, "ABCDEFGHIJKLMNOPQRSTUVWXYZ", RGBPixel(0.99, 0.99, 0.99));

  bmp.Text(20, 160, "Well hello there, some Text!!", RGBPixel(0.99, 0.99, 0.99));
  bmp.Text(20, 140, "078?AGHOPWXZaghopwxz", RGBPixel(0.99, 0.99, 0.99));
  bmp.Text(20, 120, "pqrstuvwxyz", RGBPixel(0.99, 0.99, 0.99));
  //bmp.Text(20, 120, "pqrsx", RGBPixel(0.99, 0.99, 0.99));
  
  cout << "Line" << endl;
  bmp.Line(50, 50, 300, 150, RGBPixel(0.9, 0.1, 0.2));
  bmp.Line(50, 50, 56, 50, RGBPixel(0.9, 0.9, 0.9));

  //bmp.Resample(0.5, false);
  
  cout << "Save" << endl;
  bmp.WriteJPG("animage.jpg", 95);

  cout << "done!" << endl;
  
  return 0;
}
