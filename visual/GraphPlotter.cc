#include "visual/GraphPlotter.h"
#include "base/StringUtil.h"
#include "base/FileParser.h"
#include "visual/Color.h"

void GraphPlotter::SetTimeMarks(Bitmap & bmp, int perSecond, int x_off, int y_from_top, const RGBPixel & color)
{
  int x1 = x_off;
  int x2 = bmp.X();

  y_from_top = bmp.Y() - y_from_top;

  int wide = 12;
  bmp.Line(x1, y_from_top, x2, y_from_top, color);
  int i;

  for (i=x1; i<x2; i+= perSecond) {
    bmp.Line(i, y_from_top-wide, i, y_from_top, color);
    //bmp.Line(i+perSecond/2, y_from_top-wide/4, i+perSecond/2, y_from_top, color); // 500 ms marks? 
    string label = Stringify((i-x1)/perSecond);
    label += ".0";
    bmp.Text(i+4, y_from_top-18, label, color);
  }
  
}

void GraphPlotter::Plot(Bitmap & bmp,
			 const svec<double> & d,
			 const RGBPixel & color,
			 double scale_x,
			 double scale_y,
			 int x_off,
			 int y_off)
{

  int i, j;

  int last_y = y_off;
  int last_x = x_off;
  for (i=0; i<d.isize(); i++) {
    int x = (int)(scale_x * (double)i) + x_off;
    int y = (int)(scale_y * d[i]) + y_off;

    bmp.Line(last_x, last_y, x, y, color);
    
    last_y = y;
    last_x = x;
  }

  cout << "y_off: " << y_off << endl;

  bmp.Line(x_off, y_off, last_x, y_off, RGBPixel(0.99, 0.99, 0.99));


}

void GraphPlotter::Rect(Bitmap & bmp, const RGBPixel & color, double x1, double y1, double x2, double y2)
{
  for (double x=x1; x<=x2; x += 1.) {
    for (double y=y1; y<=y2; y += 1.) {
      bmp.Dot(x, y, color);
    }
  }
}



void GraphPlotter::Spectra(Bitmap & bmp,
			   const svec < svec < double> >& d,
			   const RGBPixel & color,
			   double x_width,
			   double y_width,
			   double z_scale,
			   int x_off,
			   int y_off)
{
  int i, j, k;

  double x1 = x_off;
  for (i=0; i<d.isize(); i++) {
    double x2 = x1 + x_width;
    for (j=0; j<d[i].isize(); j++) {
      //for (j=0; j<1; j++) {
      double y1 = y_off + (double)j * y_width;
      double y2 = y_off + (double)(j+1) * y_width;
      double v = d[i][j]*z_scale;
      //if (j == 0)
      //cout << j << " " << v << endl;
      if (v > 1.)
	v = 0.99;
      RGBPixel p = color;
      p.Scale(v);
      Rect(bmp, p, x1, y1, x2, y2);
    }
    x1 += x_width;
  }

}


void GraphPlotter::DownScale(svec<double> & out, const svec<double> & in, int fac)
{
  int n = in.isize()/fac;

  out.resize(n, 0.);

  int i, j;

  for (i=0; i<n; i++) {
    for (j=i*fac; j<(i+1)*fac; j++) {
      out[i] += in[j];
    }
    out[i] /= (double)fac;
  }
}


void GraphPlotter::PlotEMGIMUGraphs(const string & outName, const string & fileName)
{
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

  
  bmp.LoadFonts(m_fontFile);

  double scale = 1.;

  bmp.SetSize(20+(int)(x.isize()*scale), 430);


  int topY = bmp.Y() - 30;

  int dist = 80;
  int offForAnnot = 30;

  cout << "Start plot" << endl;
  
  Plot(bmp, x, RGBPixel(0.99, 0.1, 0.1), scale, 12, 2, topY-dist);
  Plot(bmp, y, RGBPixel(0.88, 0.9, 0.1), scale, 12, 2, topY-dist);
  Plot(bmp, z, RGBPixel(0.5, 0.5, 0.99), scale, 12, 2, topY-dist);

  cout << a.isize() << endl;
  Plot(bmp, a, RGBPixel(0.99, 0.1, 0.1), scale, 0.1, 2, topY-2*dist);
  Plot(bmp, b, RGBPixel(0.88, 0.9, 0.1), scale, 0.1, 2, topY-2*dist);
  Plot(bmp, c, RGBPixel(0.5, 0.5, 0.99), scale, 0.1, 2, topY-2*dist);

  svec<double> es, rs;
  DownScale(es, e, 5);
  DownScale(rs, r, 5);

  Plot(bmp, es, RGBPixel(0.3, 0.9, 0.5), scale, 0.1, 2, topY-3*dist);
  Plot(bmp, rs, RGBPixel(0.9, 0.9, 0.9), scale, 0.1, 2, topY-3*dist);
  

  if (freq.isize() > 0) {
    cout << "Plot spectra" << endl;
    Spectra(bmp, freq, RGBPixel(0.99, 0.99, 0.99), 12.8, 12.8, 0.00005, 2+6.4, topY-4*dist - 80);
  }

  
  SetTimeMarks(bmp, 100, 2, 2, RGBPixel(0.99, 0.99, 0.99));
  
  //  bmp.Text(20, 12, "Data: walking", RGBPixel(0.99, 0.99, 0.99));
  
  cout << "Save " << outName << endl;
  bmp.WriteJPG(outName, 95);

  cout << "done!" << endl;
}


void GraphPlotter::PlotHeatMaps(const string & outName, svec< svec < double > > & data, const svec<string> & labels)
{
  Bitmap bmp;

  int size = 4;
  int off = 1;
  bmp.SetSize(2*off+size*data.isize(), 2*off+size*data.isize());

  int i, j;

  double max = 1.2;

  svec<string> names;
  names = labels;
  UniqueSort(names);
  
  for (i=0; i<data.isize(); i++) {
    for (j=0; j<data.isize(); j++) {
      int x1 = off + size*j;
      int x2 = x1 + size;
      int y1 = off + size*i;
      int y2 = y1 + size;
      double val = data[i][j];
      
      double v = val/max;
      if (v > 0.996)
	v = 0.996;
      color base;
      if (labels[j] == "auto_act") {
	base = color(0.996, 0.996, 0.996);
      } else {
	int idx = BinSearch(names, labels[j]);
	base = MakeUpColorUniv(idx);
      }
      
      color col = Gradient(v, base, color(0., 0., 0.));

      
      bmp.Rect(x1, y1, x2, y2, RGBPixel(col.R(), col.G(), col.B()), RGBPixel(col.R(), col.G(), col.B()));
    }
  }
  cout << "Save " << outName << endl;
  bmp.WriteJPG(outName, 95);

  cout << "done!" << endl;

  
}
