#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include "visual/Bitmap.h"
#include "visual/GraphPlotter.h"


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

  svec<double> x, y, z;
  svec<double> a, b, c;
  svec<double> r, e;

  svec< svec < double > > freq;
  
  bool bYes = false;
  bool bGyr = false;
  bool bExg = false;
  bool bSpec = false;

  svec<int> start;
  svec<int> stop;
  bool bAnnot = false;
  
  while (parser.ParseLine()) {
    if (parser.GetItemCount() == 0)
      continue;
    if (parser.AsString(0) == "ACCEL") {
      bYes = true;
      parser.ParseLine();
      continue;
    }
    if (parser.AsString(1) == "Data") {
      bYes = false;
      bGyr = false;
      bExg = false;
      bSpec = false;
      //continue;
    }
    if (parser.AsString(0) == "GYRO") {
      bGyr = true;
      parser.ParseLine();
      continue;
    }
    if (parser.AsString(0) == "EXG") {
      bExg = true;
      parser.ParseLine();
      continue;
    }
    if (parser.AsString(0) == "SPECTRA") {
      bSpec = true;
      parser.ParseLine();
      continue;
    }

    if (bYes) {
      x.push_back(parser.AsFloat(1));
      y.push_back(parser.AsFloat(2));
      z.push_back(parser.AsFloat(3));
    }

    if (bGyr) {
      a.push_back(parser.AsFloat(1));
      b.push_back(parser.AsFloat(2));
      c.push_back(parser.AsFloat(3));
    }
    if (bExg) {
      e.push_back(parser.AsFloat(1));
      r.push_back(parser.AsFloat(2));
      if (parser.GetItemCount() == 8) {
	bAnnot = false;
      }
    }

    if (bSpec) {
      svec<double> d;
      //cout << parser.Line() << endl;
      for (i=1; i<9; i++) {
	d.push_back(parser.AsFloat(i));
      }
      freq.push_back(d);
    }
  }
 

  Bitmap bmp;

  
  bmp.LoadFonts("fonts/Courier.bmp");

  double scale = 1.;

  bmp.SetSize(20+(int)(x.isize()*scale), 400);

  GraphPlotter plot;

  int topY = bmp.Y() - 20;

  int dist = 70;
  
  
  cout << "Start plot" << endl;
  
  plot.Plot(bmp, x, RGBPixel(0.99, 0.1, 0.1), scale, 12, 2, topY-dist);
  plot.Plot(bmp, y, RGBPixel(0.88, 0.9, 0.1), scale, 12, 2, topY-dist);
  plot.Plot(bmp, z, RGBPixel(0.5, 0.5, 0.99), scale, 12, 2, topY-dist);

  cout << a.isize() << endl;
  plot.Plot(bmp, a, RGBPixel(0.99, 0.1, 0.1), scale, 0.1, 2, topY-2*dist);
  plot.Plot(bmp, b, RGBPixel(0.88, 0.9, 0.1), scale, 0.1, 2, topY-2*dist);
  plot.Plot(bmp, c, RGBPixel(0.5, 0.5, 0.99), scale, 0.1, 2, topY-2*dist);

  svec<double> es, rs;
  plot.DownScale(es, e, 5);
  plot.DownScale(rs, r, 5);

  plot.Plot(bmp, es, RGBPixel(0.3, 0.9, 0.5), scale, 0.1, 2, topY-3*dist);
  plot.Plot(bmp, rs, RGBPixel(0.9, 0.9, 0.9), scale, 0.1, 2, topY-3*dist);
  

  if (freq.isize() > 0) {
    cout << "Plot spectra" << endl;
    plot.Spectra(bmp, freq, RGBPixel(0.99, 0.99, 0.99), 12.8, 12.8, 0.00005, 2+6.4, topY-4*dist - 80);
  }

  
  plot.SetTimeMarks(bmp, 100, 2, 2, RGBPixel(0.99, 0.99, 0.99));
  
  bmp.Text(20, 12, "Data: walking", RGBPixel(0.99, 0.99, 0.99));
  
  cout << "Save" << endl;
  bmp.WriteJPG("agraph.jpg", 95);

  cout << "done!" << endl;
  
  return 0;
}
