/* Copyright (c) 2020 Digidactylus AB.
   See LICENSE for more information.
*/

#ifndef BITMAP_H
#define BITMAP_H


#include "base/SVector.h"
#include <math.h>

class RGBPixel
{
 public:
  RGBPixel() {
    m_r = 0;
    m_g = 0;
    m_b = 0;
    m_extra = 0;
  }
  
  RGBPixel(double r, double g, double b) {
    Set_r(r);
    Set_g(g);
    Set_b(b);
    m_extra = 0;
  }

  unsigned char R() const {return m_r;}
  unsigned char G() const {return m_g;}
  unsigned char B() const {return m_b;}
  unsigned char Extra() const {return m_extra;}

  double r() const {return ((double)m_r)/255.;}
  double g() const {return ((double)m_g)/255.;}
  double b() const {return ((double)m_b)/255.;}
  double extra() const {return ((double)m_extra)/255.;}


  void Set_R(unsigned char c) {m_r = c;}
  void Set_G(unsigned char c) {m_g = c;}
  void Set_B(unsigned char c) {m_b = c;}
  void Set_Extra(unsigned char c) {m_extra = c;}

  void Set_r(double c) {m_r = (unsigned char)(c*255);}
  void Set_g(double c) {m_g = (unsigned char)(c*255);}
  void Set_b(double c) {m_b = (unsigned char)(c*255);}
  void Set_extra(double c) {m_extra = (unsigned char)(c*255);}

  void Merge(const RGBPixel & p, double weight) {
    Set_r(r()*(1.-weight) + p.r()*weight);
    Set_g(g()*(1.-weight) + p.g()*weight);
    Set_b(b()*(1.-weight) + p.b()*weight);
  }
  
  bool operator == (const RGBPixel & p) const {
    if (m_r == p.m_r &&
	m_g == p.m_g &&
	m_b == p.m_b &&
	m_extra == p.m_extra)
      return true;
    return false;
  }

  bool operator < (const RGBPixel & p) const {
    int a = m_r*256*256 + m_g*256 + m_b;
    int b = p.m_r*256*256 + p.m_g*256 + p.m_b;
    return a < b;
  }

  double Grey() const {
    return (r() + g() + b())/3.;
  }

  double Distance(const RGBPixel & pix) const {
    double d = 0.;
    d += (r()-pix.r())*(r()-pix.r());
    d += (g()-pix.g())*(g()-pix.g());
    d += (b()-pix.b())*(b()-pix.b());
    return d/3;
  }


  void Scale(double s) {
    int tmp = m_r * s;
    if (tmp > 0xFF)
      tmp = 0xFF;
    m_r = (unsigned char)tmp;

    tmp = m_g * s;
    if (tmp > 0xFF)
      tmp = 0xFF;
    m_g = (unsigned char)tmp;
    
    tmp = m_b * s;
    if (tmp > 0xFF)
      tmp = 0xFF;
    m_b = (unsigned char)tmp;
  }
  
 private:

  unsigned char m_r; 
  unsigned char m_g; 
  unsigned char m_b; 
  unsigned char m_extra; 

};



typedef struct bitmapFileHeader{
	   unsigned short int bfType;              
	   unsigned int bfSize;                   
	   unsigned short int bfReserved1, bfReserved2;
	   unsigned int bfOffBits;                 
	} BITMAPFILEHEADER_X;
	 
typedef struct bitmapInfoHeader{
	   unsigned int biSize;           
	   int biWidth, biHeight;              
	   unsigned short int biPlanes;   
	   unsigned short int biBitCount;       
	   unsigned int biCompression;     
	   unsigned int biSizeImage;        
	   int biXPelsPerMeter, biYPelsPerMeter;    
	   unsigned int biClrUsed;        
	   unsigned int biClrImportant;  
	} BITMAPINFOHEADER_X;
	 


//============================================
class Bitmap
{

 public:
  Bitmap();

  ~Bitmap() {
    if (m_pFonts != NULL)
      delete m_pFonts;
  }

  
  RGBPixel & Get(int x, int y) {
    if (x < 0 || y < 0 || x >= m_x || y >= m_y) {     
      return m_oob;
    }
    int i = m_x*y + x;
    return m_data[i];
  }


  const RGBPixel & Get(int x, int y) const {
    if (x < 0 || y < 0 || x >= m_x || y >= m_y) {
      return m_oob;
    }
    int i = m_x*y + x;
    return m_data[i];
  }
  
  double GetVal(int x, int y) const {
    if (x < 0 || y < 0 || x >= m_x || y >= m_y) {     
      return 0.;
    }
    int i = m_x*y + x;
    return m_data[i].Grey();
  }

  RGBPixel GetD(double x, double y) const;

  void Read(const string & fileName);
  void Write(const string & fileName);

  void ReadPNG(const string & fileName);
  void WritePNG(const string & fileName);

  void ReadJPG(const string & fileName);
  void WriteJPG(const string & fileName, int quality);

  void SetSize(int x, int y) {
    m_x = x;
    m_y = y;
    m_data.resize(m_x * m_y);
  }

  void Cross(int x, int y, const RGBPixel & col, int size = 1);
  
  void Fill(const RGBPixel & col) {
    for (int i=0; i<m_data.isize(); i++)
      m_data[i] = col;
  }

  int X() const {return m_x;}
  int Y() const {return m_y;}

  void Overlay(const Bitmap & b, int xoff = 0, int yoff = 0);

  void Merge(const Bitmap & b, double offset = 0.);
  void Multiply(const Bitmap & b, double scale = 1.);

  void Subset(Bitmap & out, 
	      int x1,
	      int y1,
	      int x2,
	      int y2);

  void Resample(double fac, bool resize = true);

  void Downsample(int fac);

  void AlignTo(const Bitmap & bmp);
  
  double Distance(const Bitmap & bmp, double xoff, double yoff, double xs, double ys) const;
  double Distance(const Bitmap & bmp) const;

  void Rect(int x1, int y1, int x2, int y2, const RGBPixel & border, const RGBPixel & fill);
  void LineHor(int x1, int x2, int y, int width, const RGBPixel & col);
  void LineVer(int y1, int y2, int x, int width, const RGBPixel & col);
  void Line(double x1, double y1, double x2, double y2, const RGBPixel & col);
  void Dot(double x, double y, const RGBPixel & col);

  void Text(int x, int y, const string & text, const RGBPixel & color);
  void Char(int x, int y, char c, const RGBPixel & color);

  void LoadFonts(const string & fileName) {
    if (m_pFonts != NULL)
      delete m_pFonts;
    m_pFonts = new Bitmap;
    //cout << "Load fonts" << endl;
    m_pFonts->Read(fileName);
  }
  
 private:
  
  double Adjust(double off, double s, double val) const {
    return off + s*val;
  }

  Bitmap * m_pFonts;
  
  svec<RGBPixel> m_data;
  int m_x;
  int m_y;
  int m_fontXoff;
  int m_fontYoff;
  int m_fontDist;
  int m_fontDistY;
  int m_fontSize;
  BITMAPFILEHEADER_X m_fileHeader;
  BITMAPINFOHEADER_X m_infoHeader;
  RGBPixel m_oob;
};



#endif //BITMAP_H
