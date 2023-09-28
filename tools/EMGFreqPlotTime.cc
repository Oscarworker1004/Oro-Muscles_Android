#include <string>
#include "visual/Whiteboard.h"
#include "base/CommandLineParser.h"
#include "base/FileParser.h"

#include "base/SVector.h"
#include "base/Config.h"
#include "visual/Color.h"
#include "visual/Bitmap.h"

#include <iostream>
#include <math.h>
#include "eeg/BTLData.h"



void Plot(ns_whiteboard::whiteboard & board, const BTLData & data, int & x_max, int & y_max)
{
  int i;

  
  double scale_y = 8.;
  double scale_x = 8.;

  double x_offset = 20;
  double y_offset = 20;

  board.Add( new ns_whiteboard::rect( ns_whiteboard::xy_coords(0, 0), 
				      ns_whiteboard::xy_coords(9000, 2000),
				      color(0.99,0.99,0.99)) );


  int t = 0;

  double off_t = 200;

  //double rate = 256;
  double rate = 128;

  y_max = 2*off_t;

  //t = 200;
    
  int cc = 0;
  double x = 0.;
  double last_x = x_offset;
  double last = off_t;

  int last_state = 0;
  int n = 1;
  int toggle = 5;
  for (i=0; i<data.EXG().isize(); i++) {
    
    x = x_offset + (double)i*16/rate;
    double y = off_t + data.EXG()[i].Signal()/20;

    //color c = color(0, 0, 0);
    int state = data.EXG()[i].State();
    double d = (double)state/6.;
    color c;
    //c = GradientMult(d, color(0.99, 0.99, 0.99), color(0., 0., 0.), color(0.99, 0.99, 0.));

    if (i % 64 == 0) {
      board.Add( new ns_whiteboard::line( ns_whiteboard::xy_coords(x, y_offset + toggle), 
					  ns_whiteboard::xy_coords(x+128*16/rate, y_offset + toggle),
					  1.0,
					  color(0., 0., 0.1)) );
      if (toggle == 5) {
	toggle = 0;
      } else {
	if (toggle == 0) {
	  toggle = -5;
	} else {
	  if (toggle == -5)
	    toggle = 5;
	}
      }
    }
 
    if (last_state != state) {
 
      if (state >=3 && last_state < 3) {
	board.Add( new ns_whiteboard::line( ns_whiteboard::xy_coords(x, off_t - 100), 
					    ns_whiteboard::xy_coords(x, off_t + 100),
					    0.6,
					    color(0., 0., 0.99)) );
 
	board.Add( new ns_whiteboard::text( ns_whiteboard::xy_coords(x+3, off_t + 80),
					    Stringify(n), color(0,0,0), 14., "Times-Roman", 0, true));
	n++;

      }
      if (last_state >=3 && state < 3) {
	board.Add( new ns_whiteboard::line( ns_whiteboard::xy_coords(x, off_t - 100), 
					    ns_whiteboard::xy_coords(x, off_t + 100),
					    0.6,
					    color(0., 0.8, 0.)) );
 

      }
      last_state = state;
    }

    // cout << state << endl;
    switch(state) {
    case 0:
      c = color(0.92, 0.92, 0.93);
      break;
    case 1:
      c = color(0.8, 0.8, 0.8);
      break;
    case 2:
      c = color(0.99, 0.8, 0.3);
      break;
    case 3:
      c = color(0.8, 0.0, 0.0);
      break;
     case 4:
      c = color(0, 0, 0);
      break;
    default:
      cout << "NOPE!!" << endl;
      throw;
      break;
    } 
    
    board.Add( new ns_whiteboard::line( ns_whiteboard::xy_coords(last_x, last), 
					ns_whiteboard::xy_coords(x, y),
					0.2,
					c) );
    
    
    last = y;
    last_x = x;
    if (x > x_max)
      x_max = x;
    cc++;
  }

  board.Add( new ns_whiteboard::line( ns_whiteboard::xy_coords(x_offset, off_t), 
				      ns_whiteboard::xy_coords(x+x_offset, off_t),
				      1,
				      color(0,0,0)) );


  // Draw ACC & GYRO data

  /*
  y_max *= 2;
  off_t =
    
  for (i=0; i<data.EXG().isize(); i++) {
    
    x = x_offset + (double)i*16/rate;
    double y = off_t + data.EXG()[i].Signal()/20;

    //color c = color(0, 0, 0);
    int state = data.EXG()[i].State();
    double d = (double)state/6.;
    color c;
    //c = GradientMult(d, color(0.99, 0.99, 0.99), color(0., 0., 0.), color(0.99, 0.99, 0.));

    if (i % 64 == 0) {
      board.Add( new ns_whiteboard::line( ns_whiteboard::xy_coords(x, y_offset + toggle), 
					  ns_whiteboard::xy_coords(x+128*16/rate, y_offset + toggle),
					  1.0,
					  color(0., 0., 0.1)) );
      if (toggle == 5) {
	toggle = 0;
      } else {
	if (toggle == 0) {
	  toggle = -5;
	} else {
	  if (toggle == -5)
	    toggle = 5;
	}
      }
    }
 
    if (last_state != state) {
 
      if (state >=3 && last_state < 3) {
	board.Add( new ns_whiteboard::line( ns_whiteboard::xy_coords(x, off_t - 100), 
					    ns_whiteboard::xy_coords(x, off_t + 100),
					    0.6,
					    color(0., 0., 0.99)) );
 
	board.Add( new ns_whiteboard::text( ns_whiteboard::xy_coords(x+3, off_t + 80),
					    Stringify(n), color(0,0,0), 14., "Times-Roman", 0, true));
	n++;

      }
      if (last_state >=3 && state < 3) {
	board.Add( new ns_whiteboard::line( ns_whiteboard::xy_coords(x, off_t - 100), 
					    ns_whiteboard::xy_coords(x, off_t + 100),
					    0.6,
					    color(0., 0.8, 0.)) );
 

      }
      last_state = state;
    }

    // cout << state << endl;
    switch(state) {
    case 0:
      c = color(0.92, 0.92, 0.93);
      break;
    case 1:
      c = color(0.8, 0.8, 0.8);
      break;
    case 2:
      c = color(0.99, 0.8, 0.3);
      break;
    case 3:
      c = color(0.8, 0.0, 0.0);
      break;
     case 4:
      c = color(0, 0, 0);
      break;
    default:
      cout << "NOPE!!" << endl;
      throw;
      break;
    } 
    
    board.Add( new ns_whiteboard::line( ns_whiteboard::xy_coords(last_x, last), 
					ns_whiteboard::xy_coords(x, y),
					0.2,
					c) );
    
    
    last = y;
    last_x = x;
    if (x > x_max)
      x_max = x;
    cc++;
  }

  board.Add( new ns_whiteboard::line( ns_whiteboard::xy_coords(x_offset, off_t), 
				      ns_whiteboard::xy_coords(x+x_offset, off_t),
				      1,
				      color(0,0,0)) );

  */
 
}




int main( int argc, char** argv )
{

  commandArg<string> inCmmd("-i","input file");
  commandArg<string> outCmmd("-o","output file");
  commandLineParser P(argc,argv);
  P.SetDescription("Makes plots.");
  P.registerArg(inCmmd);
  P.registerArg(outCmmd);
  
  P.parse();

 
  
  string inName = P.GetStringValueFor(inCmmd);
  string outName = P.GetStringValueFor(outCmmd);

  BTLData data;
  data.Read(inName);
  
  ns_whiteboard::whiteboard board;

  int x_max = 0;
  int y_max = 0;

 
  string name = inName; 
  Plot(board, data, x_max, y_max);
  

  double x_offset = 20;
  double y_offset = 20;
 

  ofstream out(outName.c_str());
  
  ns_whiteboard::ps_display display(out, x_max + 2 * x_offset, y_max + 2 * y_offset);
  board.DisplayOn(&display);
 

  return 0;
}
