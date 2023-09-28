/* Copyright (c) 2020 Digidactylus AB.
   See LICENSE for more information.
*/


#define USE_STB

#ifdef USE_STB
#define STB_IMAGE_IMPLEMENTATION
#define STB_IMAGE_WRITE_IMPLEMENTATION
#include "extern/stb/stb.h"
#include "extern/stb/stb_image_write.h"
#endif

#include "visual/Bitmap.h"
#include <stdio.h>
//#include "extern/lodepng/lodepng.h"
#include <math.h>
#include "util/mutil.h"
#include "base/Log.h"

Bitmap::Bitmap() {
  m_x = 0;
  m_y = 0;


  m_fileHeader.bfType = 0x4d42;
  //m_fileHeader.bfType = 0x424d;
  m_fileHeader.bfSize = 12;
  m_fileHeader.bfReserved1 = 0;
  m_fileHeader.bfReserved2 = 0;
  m_fileHeader.bfOffBits = 0x36;
	 
	 
  // Fill the bitmap info structure
  m_infoHeader.biSize = 0x28;
  m_infoHeader.biWidth = 480;
  m_infoHeader.biHeight = 480;
  m_infoHeader.biPlanes = 1;
  m_infoHeader.biBitCount = 24;          
  m_infoHeader.biCompression = 0;     
  m_infoHeader.biSizeImage = m_x * m_y * 3;   
  m_infoHeader.biXPelsPerMeter = 0x24e7;
  m_infoHeader.biYPelsPerMeter = 0x24e7;
  m_infoHeader.biClrUsed = 0;
  m_infoHeader.biClrImportant = 0;

  m_pFonts = NULL;
  m_fontXoff = 2;
  m_fontYoff = 4;
  m_fontDist = 18;
  m_fontSize = 16;
  m_fontDistY = 22;
}


void Bitmap::Read(const string & fileName)
{
  if (strstr(fileName.c_str(), ".png") != NULL ||
      strstr(fileName.c_str(), ".PNG") != NULL) {
    //log_n << "Reading PNG format." << endl;
    ReadPNG(fileName);
    return;
  }
  
  FILE * p = fopen(fileName.c_str(), "rb");

  if (p == NULL) {
    ThrowError("ERROR: File not found:", fileName.c_str());
  }

  int n;
  n = fread((void*)&m_fileHeader.bfType, sizeof(m_fileHeader.bfType), 1, p); 
  n = fread((void*)&m_fileHeader.bfSize, sizeof(m_fileHeader.bfSize), 1, p); 
  n = fread((void*)&m_fileHeader.bfReserved1, sizeof(m_fileHeader.bfReserved1), 1, p); 
  n = fread((void*)&m_fileHeader.bfReserved2, sizeof(m_fileHeader.bfReserved1), 1, p); 
  n = fread((void*)&m_fileHeader.bfOffBits, sizeof(m_fileHeader.bfOffBits), 1, p); 

  n = fread((void*)&m_infoHeader.biSize, sizeof(m_infoHeader.biSize), 1, p); 
  n = fread((void*)&m_infoHeader.biWidth, sizeof(m_infoHeader.biWidth), 1, p); 
  n = fread((void*)&m_infoHeader.biHeight, sizeof(m_infoHeader.biHeight), 1, p); 
  n = fread((void*)&m_infoHeader.biPlanes, sizeof(m_infoHeader.biPlanes), 1, p); 
  n = fread((void*)&m_infoHeader.biBitCount, sizeof(m_infoHeader.biBitCount), 1, p); 
  n = fread((void*)&m_infoHeader.biCompression, sizeof(m_infoHeader.biCompression), 1, p); 
  n = fread((void*)&m_infoHeader.biSizeImage, sizeof(m_infoHeader.biSizeImage), 1, p); 
  n = fread((void*)&m_infoHeader.biXPelsPerMeter, sizeof(m_infoHeader.biXPelsPerMeter), 1, p); 
  n = fread((void*)&m_infoHeader.biYPelsPerMeter, sizeof(m_infoHeader.biYPelsPerMeter), 1, p); 
  n = fread((void*)&m_infoHeader.biClrUsed, sizeof(m_infoHeader.biClrUsed), 1, p); 
  n = fread((void*)&m_infoHeader.biClrImportant, sizeof(m_infoHeader.biClrImportant), 1, p); 


  int i;

  char rgb[3];
  m_x = m_infoHeader.biWidth;
  m_y = m_infoHeader.biHeight;
  
  m_data.resize(m_x * m_y);

  for (i=0; i<m_data.isize(); i++) {
    n = fread((void*)rgb, sizeof(rgb), 1, p); 
    m_data[i].Set_R(rgb[2]);
    m_data[i].Set_G(rgb[1]);
    m_data[i].Set_B(rgb[0]);
  }


  fclose(p);
}

void Bitmap::Write(const string & fileName)
{
  int n = m_x * m_y * 3; 
  int rest = n % 32;
  //log_n << "n: " << n << " rest " << rest << endl;
  if (rest > 0)
    rest = 32 - rest;
  //log_n << "Adding " << rest << endl;
  m_infoHeader.biSizeImage = n + rest;   

  m_infoHeader.biWidth = m_x;
  m_infoHeader.biHeight = m_y;

  m_fileHeader.bfSize = 12 + 0x28 + 3*m_data.isize() + 4 -2;

  FILE * p = fopen(fileName.c_str(), "wb");
  if (p == NULL) {
    //log_e << "Could not open file for write: " << fileName << endl;
    ThrowError("Could not open file for write", fileName);
  }
  
  fwrite((void*)&m_fileHeader.bfType, sizeof(m_fileHeader.bfType), 1, p); 
  fwrite((void*)&m_fileHeader.bfSize, sizeof(m_fileHeader.bfSize), 1, p); 
  fwrite((void*)&m_fileHeader.bfReserved1, sizeof(m_fileHeader.bfReserved1), 1, p); 
  fwrite((void*)&m_fileHeader.bfReserved2, sizeof(m_fileHeader.bfReserved1), 1, p); 
  fwrite((void*)&m_fileHeader.bfOffBits, sizeof(m_fileHeader.bfOffBits), 1, p); 

  fwrite((void*)&m_infoHeader.biSize, sizeof(m_infoHeader.biSize), 1, p); 
  fwrite((void*)&m_infoHeader.biWidth, sizeof(m_infoHeader.biWidth), 1, p); 
  fwrite((void*)&m_infoHeader.biHeight, sizeof(m_infoHeader.biHeight), 1, p); 
  fwrite((void*)&m_infoHeader.biPlanes, sizeof(m_infoHeader.biPlanes), 1, p); 
  fwrite((void*)&m_infoHeader.biBitCount, sizeof(m_infoHeader.biBitCount), 1, p); 
  fwrite((void*)&m_infoHeader.biCompression, sizeof(m_infoHeader.biCompression), 1, p); 
  fwrite((void*)&m_infoHeader.biSizeImage, sizeof(m_infoHeader.biSizeImage), 1, p); 
  fwrite((void*)&m_infoHeader.biXPelsPerMeter, sizeof(m_infoHeader.biXPelsPerMeter), 1, p); 
  fwrite((void*)&m_infoHeader.biYPelsPerMeter, sizeof(m_infoHeader.biYPelsPerMeter), 1, p); 
  fwrite((void*)&m_infoHeader.biClrUsed, sizeof(m_infoHeader.biClrUsed), 1, p); 
  fwrite((void*)&m_infoHeader.biClrImportant, sizeof(m_infoHeader.biClrImportant), 1, p); 
 

  int i;

  char rgb[3];

  for (i=0; i<m_data.isize(); i++) {
    rgb[0] = m_data[i].B();
    rgb[1] = m_data[i].G();
    rgb[2] = m_data[i].R();
    fwrite((void*)rgb, sizeof(rgb), 1, p); 
  }


  fclose(p);
}

void Bitmap::ReadPNG(const string & fileName)
{
  /*
  int i, j;
  unsigned char * buffer;
  unsigned w = 0;
  unsigned h = 0;


  //SetSize(4824, 2300);

  //LCT_RGBA;
  // 8;

  //cout << "Check existence of " << fileName << endl;
  FILE * p = fopen(fileName.c_str(), "rb");
  if (p == NULL) {
    ThrowError("ERROR: File not found:", fileName.c_str());
  } else {
    fclose(p);
  }

  lodepng_decode_file(&buffer, &w, &h,
  		      fileName.c_str(),
  		      LCT_RGB, 8);
  
  //log_n << "w=" << w << " h=" << h << " size=" << h * w * 3 << endl;

  //w = 4821;
  //h = 2333;
  //int padX = 32 - w % 32;
  //int padY = 32 - h % 32;

  int padX = 0;
  int padY = 0;

  SetSize(w+padX, h+padY);
  
  int k = 0;
  for (i=0; i<(int)h; i++) {
    for (j=0; j<(int)w; j++) {
      RGBPixel & p = Get(j, Y()-1-i);
      unsigned char r = buffer[k];
      k++;
      unsigned char g = buffer[k];
      k++;
      unsigned char b = buffer[k];
      k++;
      //cout <<(int) r << " " << (int)g << " " << (int)b << endl;
      //k++;
      p.Set_R(r);
      p.Set_G(g);
      p.Set_B(b);
    }
  }
  free(buffer); 

  */
  
 
}


void Bitmap::WritePNG(const string & fileName)
{
  ThrowError("Bitmap::WritePNG", "Not IMPLEMENTED");

}



void Bitmap::ReadJPG(const string & fileName)
{
  ThrowError("Bitmap::ReadJPG", "Not IMPLEMENTED");
  
}


void Bitmap::WriteJPG(const string & fileName, int quality)
{
#ifdef USE_STB
  
  unsigned char * rgb_image;
  rgb_image =  (unsigned char*) malloc(X()*Y()*3);

  int i, j, k=0;
  for (i=0; i<Y(); i++) {
    for (j=0; j<X(); j++) {
     const  RGBPixel & p = Get(j, Y()-1-i);
  
      rgb_image[k] = p.R();
      k++;
      rgb_image[k] = p.G();
      k++;
      rgb_image[k] = p.B();
      k++;
    }
  }
  
  int ret = stbi_write_jpg(fileName.c_str(), X(), Y(), 3, rgb_image, quality);
  //log_n << "stbi_write_jpg() returned " << ret << endl;
  free((void*)rgb_image);
  if (ret == 0) {
    //log_e << "EXCEPTION! Could now write image file!!" << endl;
    ThrowError("Could not write image", fileName);
  }

#else
  ThrowError("Bitmap::WriteJPG", "Not IMPLEMENTED");
#endif
}

void Bitmap::Cross(int x, int y, const RGBPixel & col, int n)
{
 
  int i, j;
  
  for (i=x-n; i<=x+n; i++) {
    if (i >= 0 && i < X())
      Get(i, y) = col;
  }
  for (i=y-n; i<=y+n; i++) {
    if (i >= 0 && i < Y())
      Get(x, i) = col;
  }
  
  
  n /= 2;
  
  for (i=x-n; i<=x+n; i++) {
    for (j=y-n; j<=y+n; j++) {
      if (i >= 0 && i < X()&& j >= 0 && j < Y())
	Get(i, j) = col;
    }
   
  }
 
}


void Bitmap::Downsample(int fac)
{
  //log_n << "Downsample from " << X() << " x " << Y() << endl;
  Bitmap tmp;
  tmp.SetSize(X()/fac, Y()/fac);
  int i, j;

  double f = (double)(fac*fac);

  svec<double> r, g, b;
  r.resize(tmp.X()*tmp.Y(), 0.);
  g.resize(tmp.X()*tmp.Y(), 0.);
  b.resize(tmp.X()*tmp.Y(), 0.);
  
  for (i=0; i<X(); i++) {
    for (j=0; j<Y(); j++) {
      int x = i/fac;
      int y = j/fac;
      const RGBPixel & p = Get(i, j);
      r[x*tmp.Y()+y] += p.r()/f;
      g[x*tmp.Y()+y] += p.g()/f;
      b[x*tmp.Y()+y] += p.b()/f;
      //cout << "Add to " << x << " " << y << " " << x*tmp.Y()+y << endl;
    }
  }

  for (i=0; i<tmp.X(); i++) {
    for (j=0; j<tmp.Y(); j++) {
      RGBPixel & d = tmp.Get(i, j);
      d.Set_r(r[i*tmp.Y()+j]);
      d.Set_g(g[i*tmp.Y()+j]);
      d.Set_b(b[i*tmp.Y()+j]);
      // cout << "Set " << i << " " << j << endl;

    }
  }

  
  *this = tmp;

}

void Bitmap::Overlay(const Bitmap & b, int xoff, int yoff)
{
  int i, j;
  
  for (i=0; i<b.X(); i++) {
    for (j=0; j<b.Y(); j++) {
      const RGBPixel & p = b.Get(i, j);
      RGBPixel & m = Get(i+xoff, j+yoff);
 
      if (p.R() > 250 && p.G() > 250 && p.B() > 250)
	continue;
      m = p;
    }
  }
}

void LimitRGB(int & v)
{
  if (v < 0)
    v = 0;
  if (v > 255)
    v = 255;
}

void Bitmap::Multiply(const Bitmap & b, double scale)
{
  int i, j;

  for (i=0; i<b.X(); i++) {
    for (j=0; j<b.Y(); j++) {
      const RGBPixel & p = b.Get(i, j);
      RGBPixel & m = Get(i, j);
      int v = scale * p.extra() * (int)m.R();
      LimitRGB(v);
      //cout << "R Set " << v << " from " << (int)p.R() << endl;
      m.Set_R((char)v);

      v = scale * p.extra() * (int)m.G();
      LimitRGB(v);
      //cout << "G Set " << v << " from " << (int)p.G() << endl;
      m.Set_G((char)v);

      v = scale * p.extra() * (int)m.B();
      LimitRGB(v);
      //cout << "B Set " << v << " from " << (int)p.B() << endl;
      m.Set_B((char)v);
     
    }
  }
}

void Bitmap::Merge(const Bitmap & b, double offset)
{
  int i, j;
  int off = (int)(offset*255);

  for (i=0; i<b.X(); i++) {
    for (j=0; j<b.Y(); j++) {
      const RGBPixel & p = b.Get(i, j);
      RGBPixel & m = Get(i, j);
 
      int v = (int)p.R() - offset;
      LimitRGB(v);
      v += m.R();
      LimitRGB(v);
      m.Set_R((char)v);
      
      v = (int)p.G() - offset;
      LimitRGB(v);
      v += m.G();
      LimitRGB(v);
      m.Set_G((char)v);
      
      v = (int)p.B() - offset;
      LimitRGB(v);
      v += m.B();
      LimitRGB(v);
      m.Set_B((char)v);
    }
  }


}

void Bitmap::Subset(Bitmap & out, 
		    int x1,
		    int y1,
		    int x2,
		    int y2)
{
  out.SetSize(x2-x1, y2-y1);
  int i, j;
  

  for (i=x1; i<x2; i++) {
    for (j=y1; j<y2; j++) {
      const RGBPixel & p = Get(i, j);
      RGBPixel & m = out.Get(i-x1, j-y1);
      m = p;
    }
  }

}

double Weight(double x1, double x2, double y1, double y2)
{
  double off = 1./2048.;
  double d = (x2-x1)*(x2-x1)+(y2-y1)*(y2-y1);
  //cout << d << endl;
  d = 1./sqrt(d + off);
  return d;
}

double Lap(double a, double b)
{
  double d = a-b;
  if (d < 0.)
    d = -d;
  d = 1. - d;
 
  return d;
}

double WeightReal(double x1, double y1, double x2, double y2)
{
  double a = Lap(x1, x2);
  if (a <= 0)
    return 0.;

  double b = Lap(y1, y2);

  if (b < 0.)
    return 0.;

  double lap = a*b;

  return lap;
  
  /*
  double off = 1./2048.;
  double d = (x2-x1)*(x2-x1)+(y2-y1)*(y2-y1);
  //cout << d << endl;
  //d = 1./sqrt(d + off);
  //cout << " --> " << x1 << " " << x2 << " - " << y1 << " " << y2 << endl;
  //d = exp(-sqrt(d));
  d = exp(-d);
  return d;*/
}

void Bitmap::Dot(double x, double y, const RGBPixel & col)
{
  int xx = (int)(x+0.5);
  int yy = (int)(y+0.5);

  //Get(xx, yy).Merge(col, Weight(xx, yy, x, y);
  int i, j;
  
  for (i=xx-1; i<=xx+1; i++) {
    for (j=yy-1; j<=yy+1; j++) {
      //cout << "i=" << i << " j=" << j << " xx=" << xx << " yy=" << yy << " -> " << WeightReal(i, j, x, y) << endl;
      Get(i, j).Merge(col, WeightReal(i, j, x, y));
      //cout << xx << " " << yy << " " << x << " " << y << " ->" 
    }
  } 
}

RGBPixel Bitmap::GetD(double x, double y) const
{
  RGBPixel pix;
  if (x < 0 || y < 0 || x > X()-1 || y > Y()-1)
    return pix;

  //cout << x << " " << y << endl;

  int x1 = (int)x;
  int x2 = (int)x+1;
  int y1 = (int)y;
  int y2 = (int)y+1;

  if (x2 >= X())
    x2--;
  if (y2 >= Y())
    y2--;
  
  
  double d1 = Weight(x1, x, y1, y);
  double d2 = Weight(x1, x, y2, y);
  double d3 = Weight(x2, x, y1, y);
  double d4 = Weight(x2, x, y2, y);

  double div = d1+d2+d3+d4;
  //cout << d1 << " " << d2 << " " << d3 << " " << d4 << endl;

  double r = d1*Get(x1, y1).r() + d2*Get(x1, y2).r() + d3*Get(x2, y1).r() + d4*Get(x2, y2).r();
  r /= div;
  double g = d1*Get(x1, y1).g() + d2*Get(x1, y2).g() + d3*Get(x2, y1).g() + d4*Get(x2, y2).g();
  g /= div;
  double b = d1*Get(x1, y1).b() + d2*Get(x1, y2).b() + d3*Get(x2, y1).b() + d4*Get(x2, y2).b();
  b /= div;
  
  //cout << r << " " << g << " " << b << " -> " << div << endl; 
  pix.Set_r(r);
  pix.Set_g(g);
  pix.Set_b(b);

  return pix;
}

void Bitmap::Resample(double scale, bool resize)
{
  Bitmap o;

  int nX = (int)(X()*scale+0.5);
  int nY = (int)(Y()*scale+0.5);

  double xoff = 0;
  double yoff = 0;
  
  if (resize) {
    o.SetSize(nX, nY);
  } else {
    o.SetSize(X(), Y());
    xoff = (nX - X())/2;
    yoff = (nY - Y())/2;
  }
  //cout << "Image size: " << nX << " x " << nY << endl;
  
  int i, j;
  
  for (i=0; i<o.X(); i++) {
    double x = ((double)i)/scale+xoff;
    for (j=0; j<o.Y(); j++) {
      double y = ((double)j)/scale+yoff;
      //cout << x << " " << y << endl;
      if (x >=0 && y>=0 && x<X() && y<Y()) {
	RGBPixel pix = GetD(x, y);
	o.Get(i, j) = pix;
      }
    }
  } 

  *this = o;
  

}

double Bitmap::Distance(const Bitmap & bmp, double xoff, double yoff, double xs, double ys) const
{
  int i, j;

  double d = 0.;
  double n = 0.;
  
  for (i=0; i<X(); i++) {
    for (j=0; j<Y(); j++) {
      const RGBPixel & p1 = bmp.Get(i, j);
      double x = Adjust(xoff, xs, (double)i);
      double y = Adjust(yoff, ys, (double)j);
      //cout << x << " " << y << endl;
      RGBPixel p2 = GetD(x, y);
      d += p2.Distance(p1);
      n += 1.;
    }
  }
  return sqrt(d/n);
}

double Bitmap::Distance(const Bitmap & bmp) const
{
  int i, j;

  double d = 0.;
  double n = 0.;
  
  for (i=0; i<X(); i++) {
    for (j=0; j<Y(); j++) {
      const RGBPixel & p1 = bmp.Get(i, j);
      const RGBPixel & p2 = GetD(i, j);
      d += p2.Distance(p1);
      n += 1.;
    }
  }
  return sqrt(d/n);
}

void Bitmap::AlignTo(const Bitmap & bmp)
{
  double xoff = 0.;
  double yoff = 0;
  double xs = 1.;
  double ys = 1;

  double xoff_delta = 1;
  double yoff_delta = 1;
  double xs_delta = 0.01;
  double ys_delta = 0.01;

  int i, j, k;

  // 10 Iterations

  double dist = Distance(bmp, xoff, yoff, xs, ys);
  int n = 5;
  double distn = 0.;
   for (int l=0; l<5; l++) {
    for (k=0; k<n; k++) {
      while ((distn = Distance(bmp, xoff, yoff, xs, ys)) <= dist) {
	xoff += xoff_delta;
	dist = distn;
	//log_n << "xoff: " << xoff << " dist: " << dist << endl;
      }
      dist = distn;
      xoff_delta = -xoff_delta/2.;
    }
    for (k=0; k<n; k++) {
      while ((distn = Distance(bmp, xoff, yoff, xs, ys)) <= dist) {
	yoff += yoff_delta;
	dist = distn;
  	//log_n << "yoff: " << yoff << " dist: " << dist << endl;
    }
      dist = distn;
      yoff_delta = -yoff_delta/2.;
    }
    // stretch
    for (k=0; k<n; k++) {
      while ((distn = Distance(bmp, xoff, yoff, xs, ys)) <= dist) {
	xs += xs_delta;
	dist = distn;
	//log_n << "xs: " << xs << " dist: " << dist << endl;
       }
      dist = distn;
      xs_delta = -xs_delta/2.;
    }
    for (k=0; k<n; k++) {
      while ((distn = Distance(bmp, xoff, yoff, xs, ys)) <= dist) {
	ys += ys_delta;
	dist = distn;
 	//log_n << "ys: " << ys << " dist: " << dist << endl;
     }
      dist = distn;
      ys_delta = -ys_delta/2.;
    }
  }
  
  for (i=0; i<X(); i++) {
    for (j=0; j<Y(); j++) {
      Get(i, j) = GetD(Adjust(xoff, xs, (double)i), Adjust(yoff, ys, (double)j));      
    }
  }

  //log_n << "Final parameters:" << endl;
  //log_n << "X-off:     " << xoff << endl;
  //log_n << "Y-off:     " << yoff << endl;
  //log_n << "X-stretch: " << xs << endl;
  //log_n << "Y-stretch: " << ys << endl;

}

void Bitmap::Rect(int x1, int y1, int x2, int y2, const RGBPixel & border, const RGBPixel & fill)
{
  int i, j;
  for (i=x1; i<=x2; i++) {
    for (j=y1; j<=y2; j++) {
      if (i == x1 || i == x2 || j == y1 || j == y2)
	Get(i, j) = border;
      else
	Get(i, j) = fill;
    }
  }

}

#ifdef WU_LINE
void Bitmap::DrawWuLine (CDC *pDC, short X0, short Y0, short X1, short Y1,
			 short BaseColor, short NumLevels, unsigned short IntensityBits)
{
   unsigned short IntensityShift, ErrorAdj, ErrorAcc;
   unsigned short ErrorAccTemp, Weighting, WeightingComplementMask;
   short DeltaX, DeltaY, Temp, XDir;

   /* Make sure the line runs top to bottom */
   if (Y0 > Y1) {
      Temp = Y0; Y0 = Y1; Y1 = Temp;
      Temp = X0; X0 = X1; X1 = Temp;
   }
   /* Draw the initial pixel, which is always exactly intersected by
      the line and so needs no weighting */
   DrawPixel(pDC,X0, Y0, BaseColor);

   if ((DeltaX = X1 - X0) >= 0) {
      XDir = 1;
   } else {
      XDir = -1;
      DeltaX = -DeltaX; /* make DeltaX positive */
   }
   /* Special-case horizontal, vertical, and diagonal lines, which
      require no weighting because they go right through the center of
      every pixel */
   if ((DeltaY = Y1 - Y0) == 0) {
      /* Horizontal line */
      while (DeltaX-- != 0) {
         X0 += XDir;
         DrawPixel(pDC,X0, Y0, BaseColor);
      }
      return;
   }
   if (DeltaX == 0) {
      /* Vertical line */
      do {
         Y0++;
         DrawPixel(pDC,X0, Y0, BaseColor);
      } while (--DeltaY != 0);
      return;
   }
   if (DeltaX == DeltaY) {
      /* Diagonal line */
      do {
         X0 += XDir;
         Y0++;
         DrawPixel(pDC,X0, Y0, BaseColor);
      } while (--DeltaY != 0);
      return;
   }
   /* Line is not horizontal, diagonal, or vertical */
   ErrorAcc = 0;  /* initialize the line error accumulator to 0 */
   /* # of bits by which to shift ErrorAcc to get intensity level */
   IntensityShift = 16 - IntensityBits;
   /* Mask used to flip all bits in an intensity weighting, producing the
      result (1 - intensity weighting) */
   WeightingComplementMask = NumLevels - 1;
   /* Is this an X-major or Y-major line? */
   if (DeltaY > DeltaX) {
      /* Y-major line; calculate 16-bit fixed-point fractional part of a
         pixel that X advances each time Y advances 1 pixel, truncating the
         result so that we won't overrun the endpoint along the X axis */
      ErrorAdj = ((unsigned long) DeltaX << 16) / (unsigned long) DeltaY;
      /* Draw all pixels other than the first and last */
      while (--DeltaY) {
         ErrorAccTemp = ErrorAcc;   /* remember current accumulated error */
         ErrorAcc += ErrorAdj;      /* calculate error for next pixel */
         if (ErrorAcc <= ErrorAccTemp) {
            /* The error accumulator turned over, so advance the X coord */
            X0 += XDir;
         }
         Y0++; /* Y-major, so always advance Y */
         /* The IntensityBits most significant bits of ErrorAcc give us the
            intensity weighting for this pixel, and the complement of the
            weighting for the paired pixel */
         Weighting = ErrorAcc >> IntensityShift;
         DrawPixel(pDC,X0, Y0, BaseColor + Weighting);
         DrawPixel(pDC,X0 + XDir, Y0,
               BaseColor + (Weighting ^ WeightingComplementMask));
      }
      /* Draw the final pixel, which is 
         always exactly intersected by the line
         and so needs no weighting */
      DrawPixel(pDC,X1, Y1, BaseColor);
      return;
   }
   /* It's an X-major line; calculate 16-bit fixed-point fractional part of a
      pixel that Y advances each time X advances 1 pixel, truncating the
      result to avoid overrunning the endpoint along the X axis */
   ErrorAdj = ((unsigned long) DeltaY << 16) / (unsigned long) DeltaX;
   /* Draw all pixels other than the first and last */
   while (--DeltaX) {
      ErrorAccTemp = ErrorAcc;   /* remember current accumulated error */
      ErrorAcc += ErrorAdj;      /* calculate error for next pixel */
      if (ErrorAcc <= ErrorAccTemp) {
         /* The error accumulator turned over, so advance the Y coord */
         Y0++;
      }
      X0 += XDir; /* X-major, so always advance X */
      /* The IntensityBits most significant bits of ErrorAcc give us the
         intensity weighting for this pixel, and the complement of the
         weighting for the paired pixel */
      Weighting = ErrorAcc >> IntensityShift;
      DrawPixel(pDC,X0, Y0, BaseColor + Weighting);
      DrawPixel(pDC,X0, Y0 + 1,
            BaseColor + (Weighting ^ WeightingComplementMask));
   }
   /* Draw the final pixel, which is always exactly intersected by the line
      and so needs no weighting */
   DrawPixel(pDC,X1, Y1, BaseColor);
}
#endif // WU_LINE


void Bitmap::Line(double x1, double y1, double x2, double y2, const RGBPixel & col)
{
  double len = sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
  for (double d=0; d<=len; d+= 1.) {
    /*int x = (int)(x1+d/len*(x2-x1)+0.5);
    int y = (int)(y1+d/len*(y2-y1)+0.5);
    RGBPixel & p = Get(x, y);
    p = col;
    */
    
    double xx = x1 + d/len*(x2-x1);
    double yy = y1 + d/len*(y2-y1);
    Dot(xx, yy, col);
    
  }
}

void Bitmap::Char(int x, int y, char c, const RGBPixel & color)
{
  m_fontXoff = 3;
  m_fontYoff = 6;
  m_fontDist = 25;
  m_fontSize = 16;
  m_fontDistY = 25;
  
  if (m_pFonts == NULL) {
    cout << "Ouch, no fonts loaded!" << endl;
    throw;
  }

  svec<int> off;
  off.resize(12);

  off[0] = 11;
  off[1] = 36;
  off[2] = 60;
  off[3] = 84;
  off[4] = 108;
  off[5] = 132;
  off[6] = 157;
  off[7] = 181;
  off[8] = 206;
  off[9] = 230;
  off[10] = 252;
  off[11] = 279;

  svec<int> offX;
  offX.resize(8);

  int idx = (int)c;
  idx -= 32;

  int col = idx % 8;
  int row = 11 - idx / 8;
  
  
  //int x1 = m_fontXoff + m_fontDist * col;
  offX[0] = 3;
  offX[1] = 29;
  offX[2] = 53;
  offX[3] = 79;
  offX[4] = 105;
  offX[5] = 130;
  offX[6] = 157;
 
  offX[7] = 182;

  int x1 = offX[col];

  
  int x2 = x1 + m_fontSize;
  //int y1 = m_fontYoff + m_fontDistY * row;
  int y1 = off[row];
  int y2 = y1 + m_fontSize;


  
  int i, j;
  
  for (i=0; i<m_fontSize-1; i++) {
    for (j=0; j<m_fontSize; j++) {
      //cout << i << " " << j << endl;
      //Color col = color;
      //col.Scale(1.-m_font.Get(i, j).Grey());
      Get(i+x, j+y).Merge(color, 1.-m_pFonts->Get(i+x1, j+y1).Grey());

    }
  }
}

void Bitmap::Text(int x, int y, const string & text, const RGBPixel & color)
{
  m_fontXoff = 0;
  m_fontYoff = 6;
  m_fontDist = 22;
  //m_fontSize = 16;

  if (m_pFonts == NULL) {
    cout << "Ouch, no fonts loaded!" << endl;
    throw;
  }

  int space = 12;
  for (int i=0; i<(int)text.length(); i++) {
    Char(x+i*space, y, text[i], color);
  }
}

void Bitmap::LineHor(int x1, int x2, int y, int width, const RGBPixel & col)
{
  int i, j;
  for (i=x1; i<=x2; i++) {
    for (j=y; j<y+width; j++) {
      Get(i, j) = col;
    }
  }
  
}

void Bitmap::LineVer(int y1, int y2, int x, int width, const RGBPixel & col)
{
  int i, j;
  for (i=y1; i<=y2; i++) {
    for (j=x; j<x+width; j++) {
      Get(j, i) = col;
    }
  }
}

