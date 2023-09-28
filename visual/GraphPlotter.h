#ifndef GRAPHPLOTTER_H
#define GRAPHPLOTTER_H



#include "visual/Bitmap.h"
#include "base/SVector.h"


class GraphPlotter
{
 public:
  GraphPlotter() {
    m_fontFile = "Courier.bmp";
  }

  void Plot(Bitmap & bmp,
	    const svec<double> & d,
	    const RGBPixel & color,
	    double scale_x = 1.,
	    double scale_y = 1.,
	    int x_off = 0,
	    int y_off = 50);

  void Spectra(Bitmap & bmp,
	       const svec < svec < double> >& d,
	       const RGBPixel & color,
	       double x_width = 1.,
	       double y_width = 1.,
	       double z_scale = 1.,
	       int x_off = 0,
	       int y_off = 50);

  void Rect(Bitmap & bmp, const RGBPixel & color, double x1, double y1, double x2, double y2);
  
  void SetTimeMarks(Bitmap & bmp, int perSecond, int x_off = 0, int y_from_top = 5, const RGBPixel & color = RGBPixel(0.99, 0.99, 0.99));

  void DownScale(svec<double> & out, const svec<double> & in, int fac);

  void PlotEMGIMUGraphs(const string & outName, const string & fileName);

  void PlotHeatMaps(const string & outName, svec< svec < double > > & data, const svec<string> & labels);
  
private:
  string m_fontFile;
};











#endif //GRAPHPLOTTER_H
