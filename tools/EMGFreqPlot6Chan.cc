#include <string>
#include "visual/Whiteboard.h"
#include "base/CommandLineParser.h"
#include "base/FileParser.h"

#include "base/SVector.h"
#include "base/Config.h"
#include "visual/Color.h"
#include "eeg/BTLData.h"

#include <iostream>
#include <math.h>



void PlotOne(ns_whiteboard::whiteboard & board, const BTLData& d, int & x_max, int & y_max, int off_total, int from)
{
  int i;

  cout << "OFF: " << off_total << endl;
  
  double scale_y = 8.;
  double scale_x = 8.;

  double x_offset = 20;
  double y_offset = 20;

  if (off_total == 0) {
    board.Add( new ns_whiteboard::rect( ns_whiteboard::xy_coords(0, 0), 
					ns_whiteboard::xy_coords(2000, 1000),
					color(0.99,0.99,0.99)) );
  }

  int t = 0;

  double off_t = 240;

  //double rate = 256;
  int exgrate = 500;
  int imurate = 100;

  double scale = 75;
  double off = 300 + off_total;

  double last_x = x_offset;
  double last_y = 0;
  last_y = off + y_offset;


  cout << "OFF: " << off << endl;
  
  //================================== RAW ===========================================

    for (i=from; i<d.EXG().isize(); i++) {
    if (i > from + exgrate*15)
      break;
    
    double x = x_offset + (double)(i-from)/(double)exgrate*scale;
    double y = y_offset + d.EXG()[i].Signal()/30. + off;
    //double y = y_offset + d.EXG()[i].RMS()/30. + off;

    //cout << x << " " << y - off - y_offset << " -> " << d.EXG()[i].RMS() << " " << d.EXG()[i].Time() << " " << i << endl;
    
    
    if (x > x_max)
      x_max = x;
    if (y > y_max)
      y_max = y;
    
  
    //color a = color(0.6, 0.6, 0.6);
    color a = color(0.99, 0.99, 0.99);
    if (d.EXG()[i].State() == 1)
      a = color(0.99, 0.99, 0.6);
    if (d.EXG()[i].State() == 2)
      a = color(0.99, 0.8, 0.6);
    if (d.EXG()[i].State() == 3)
      a = color(0.99, 0.6, 0.6);
    if (d.EXG()[i].State() >= 4)
      a = color(0.6, 0.6, 0.6);
    

    
    board.Add( new ns_whiteboard::line( ns_whiteboard::xy_coords(x, y), 
					ns_whiteboard::xy_coords(last_x, last_y),
					0.3,
					a) );

    last_x = x;
    last_y = y;
  }


  
  //=================================== RMS ==========================================
  last_x = x_offset;
  last_y = 0;
  last_y = off + y_offset;

  off = 300 + off_total;

  for (i=from; i<d.EXG().isize(); i++) {
    if (i > from + exgrate*15)
      break;
    
    double x = x_offset + (double)(i-from)/(double)exgrate*scale;
    //double y = y_offset + d.EXG()[i].Signal()/20. + off;
    double y = y_offset + d.EXG()[i].RMS()/30. + off;

    cout << x << " " << y - off - y_offset << " -> " << d.EXG()[i].RMS() << " " << d.EXG()[i].Time() << " " << i << endl;
    
    
    if (x > x_max)
      x_max = x;
    if (y > y_max)
      y_max = y;
    
  
      //color a = color(0.5, 0.5, 0.8);
    color a = color(0.99, 0.99, 0.99);
    if (d.EXG()[i].State() == 1)
      a = color(0.99, 0.99, 0.);
    if (d.EXG()[i].State() == 2)
      a = color(0.99, 0.5, 0.);
    if (d.EXG()[i].State() == 3)
      a = color(0.99, 0.1, 0.);
    if (d.EXG()[i].State() >= 4)
      a = color(0.0, 0., 0.);
    
    board.Add( new ns_whiteboard::line( ns_whiteboard::xy_coords(x, y), 
					ns_whiteboard::xy_coords(last_x, last_y),
					1.,
					a) );

    last_x = x;
    last_y = y;
  }
  board.Add( new ns_whiteboard::line( ns_whiteboard::xy_coords(x_offset, y_offset+off), 
				      ns_whiteboard::xy_coords(x_offset+x_max, y_offset+off), 
				      1.,
				      color(0., 0., 0.)) );

  //===================================== IMU ==========================================

  last_x = x_offset;

  off = 265 + off_total;
  last_y = off + y_offset;

  from = (int)from*imurate/exgrate;
  for (i=from; i<d.Gyro().isize(); i++) {
    if (i > from + imurate*15)
      break;
    
    double x = x_offset + (double)(i-from)/(double)imurate*scale;
    //double y = y_offset + d.EXG()[i].Signal()/20. + off;
    double y = y_offset + d.Gyro()[i].Processed()*130 + off;

    //cout << x << " " << y - off - y_offset << " -> " << d.EXG()[i].RMS() << " " << d.EXG()[i].Time() << " " << i << endl;
    
    
    if (x > x_max)
      x_max = x;
    if (y > y_max)
      y_max = y;
    
  
      //color a = color(0.5, 0.5, 0.8);
    color a = color(0.73, 0.0, 0.0);
    
    board.Add( new ns_whiteboard::line( ns_whiteboard::xy_coords(x, y), 
					ns_whiteboard::xy_coords(last_x, last_y),
					0.5,
					a) );

    last_x = x;
    last_y = y;
  }

  last_x = x_offset;

  //off = 265 + off_total;
  last_y = off + y_offset;

  from = (int)from*imurate/exgrate;
  for (i=from; i<d.Gyro().isize(); i++) {
    if (i > from + imurate*15)
      break;
    
    double x = x_offset + (double)(i-from)/(double)imurate*scale;
    //double y = y_offset + d.EXG()[i].Signal()/20. + off;
    double y = y_offset - d.Acc()[i].Processed()*40 + off;

    //cout << x << " " << y - off - y_offset << " -> " << d.EXG()[i].RMS() << " " << d.EXG()[i].Time() << " " << i << endl;
    
    
    if (x > x_max)
      x_max = x;
    if (y > y_max)
      y_max = y;
    
  
      //color a = color(0.5, 0.5, 0.8);
    color a = color(0.0, 0.0, 0.8);
    
    board.Add( new ns_whiteboard::line( ns_whiteboard::xy_coords(x, y), 
					ns_whiteboard::xy_coords(last_x, last_y),
					0.5,
					a) );

    last_x = x;
    last_y = y;
  }


  board.Add( new ns_whiteboard::line( ns_whiteboard::xy_coords(x_offset, y_offset+off), 
				      ns_whiteboard::xy_coords(x_offset+x_max, y_offset+off), 
				      1.,
				      color(0., 0., 0.)) );

  
  
}



int main( int argc, char** argv )
{

  commandArg<string> inCmmd("-i","input file");
  commandArg<string> aCmmd("-a","annotation file", "");
  commandArg<string> tCmmd("-t","time domain file", "");
  commandArg<string> outCmmd("-o","output file");
  commandLineParser P(argc,argv);
  P.SetDescription("Makes plots.");
  P.registerArg(inCmmd);
  P.registerArg(outCmmd);
  P.registerArg(aCmmd);
  P.registerArg(tCmmd);
  
  P.parse();

 
  
  string inName = P.GetStringValueFor(inCmmd);
  string outName = P.GetStringValueFor(outCmmd);
  string aName = P.GetStringValueFor(aCmmd);
  string timeName = P.GetStringValueFor(tCmmd);

 
  
  ns_whiteboard::whiteboard board;

  int x_max = 0;
  int y_max = 0;

 
  string name = inName;

  BTLData d;
  d.Read(inName);

  cout << d.EXG()[813].RMS() << endl;
  
  //PlotOne(board, name, timeName, x_max, y_max, 0, annot);
  PlotOne(board, d, x_max, y_max, 0, 0);
  PlotOne(board, d, x_max, y_max, 100, 2000);
  PlotOne(board, d, x_max, y_max, 200, 5000);
  PlotOne(board, d, x_max, y_max, -200, 10000);
  PlotOne(board, d, x_max, y_max, -100, 15000);
  PlotOne(board, d, x_max, y_max, 300, 20000);


  double x_offset = 20;
  double y_offset = 20;
 

  ofstream out(outName.c_str());
  
  ns_whiteboard::ps_display display(out, x_max + 2 * x_offset, y_max + 2 * y_offset);


  
  board.DisplayOn(&display);
 

  return 0;
}
