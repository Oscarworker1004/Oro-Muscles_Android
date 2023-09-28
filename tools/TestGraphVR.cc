#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include "visual/Bitmap.h"
#include "visual/GraphPlotter.h"



void RemoveZero(svec<double> & d) {
  int i;

  double sum = 0.;
  for (i=0; i<d.isize(); i++)
    sum += d[i];
  sum /= (double)d.isize();

  for (i=0; i<d.isize(); i++)
    d[i] -= sum;
 
}

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
  svec<double> d, e, f;
  parser.ParseLine();
    
  while (parser.ParseLine()) {
    if (parser.GetItemCount() == 0)
      continue;
    x.push_back(parser.AsFloat(4));
    y.push_back(parser.AsFloat(5));
    z.push_back(parser.AsFloat(6));
    a.push_back(parser.AsFloat(7));
    b.push_back(parser.AsFloat(8));
    c.push_back(parser.AsFloat(9));
    d.push_back(parser.AsFloat(10));
    e.push_back(parser.AsFloat(11));
    f.push_back(parser.AsFloat(12));
  }
  Bitmap bmp;

  
  bmp.LoadFonts("fonts/Courier.bmp");

  double scale = 1.;

  bmp.SetSize(20+(int)(x.isize()*scale), 300);

  GraphPlotter plot;

  int topY = bmp.Y();

  int dist = 70;

  cout << "Start plot" << endl;

  RemoveZero(x);
  RemoveZero(y);
  RemoveZero(z);
  RemoveZero(a);
  RemoveZero(b);
  RemoveZero(c);
  RemoveZero(d);
  RemoveZero(e);
  RemoveZero(f);
  
  plot.Plot(bmp, x, RGBPixel(0.99, 0.1, 0.1), scale, 0.5, 2, topY-dist);
  plot.Plot(bmp, y, RGBPixel(0.88, 0.9, 0.1), scale, 0.5, 2, topY-dist);
  plot.Plot(bmp, z, RGBPixel(0.5, 0.5, 0.99), scale, 0.5, 2, topY-dist);

  cout << a.isize() << endl;
  plot.Plot(bmp, a, RGBPixel(0.99, 0.1, 0.1), scale, 0.5, 2, topY-2*dist);
  plot.Plot(bmp, b, RGBPixel(0.88, 0.9, 0.1), scale, 0.5, 2, topY-2*dist);
  plot.Plot(bmp, c, RGBPixel(0.5, 0.5, 0.99), scale, 0.5, 2, topY-2*dist);

  plot.Plot(bmp, d, RGBPixel(0.99, 0.1, 0.1), scale, 0.5, 2, topY-3*dist);
  plot.Plot(bmp, e, RGBPixel(0.88, 0.9, 0.1), scale, 0.5, 2, topY-3*dist);
  plot.Plot(bmp, f, RGBPixel(0.5, 0.5, 0.99), scale, 0.5, 2, topY-3*dist);
  


  
  plot.SetTimeMarks(bmp, 100, 2, 2, RGBPixel(0.99, 0.99, 0.99));
  
  bmp.Text(20, 20, "Data: squat, VR", RGBPixel(0.99, 0.99, 0.99));
  
  cout << "Save" << endl;
  bmp.WriteJPG("avr-graph.jpg", 95);

  cout << "done!" << endl;
  
  return 0;
}
