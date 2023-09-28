#include <string>
#include "visual/Whiteboard.h"
#include "base/CommandLineParser.h"
#include "base/FileParser.h"

#include "base/SVector.h"
#include "base/Config.h"
#include "visual/Color.h"

#include <iostream>
#include <math.h>



void PlotOne(ns_whiteboard::whiteboard & board, const string & fileName, const string & timeName, int & x_max, int & y_max, int off, const svec<int> & annot)
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

  double off_t = 240;

  //double rate = 256;
  double rate = 128;
  
  if (timeName != "") {

    t = 200;
    
    FlatFileParser time;
    
    time.Open(timeName);

    int cc = 0;
    double x = 0.;
    double last_x = x_offset;
    double last = off_t;
    while (time.ParseLine()) {
      if (time.GetItemCount() == 0)
	continue;

      x = x_offset + (double)cc*16/rate;
      double y = off_t + time.AsFloat(0)/32;

      board.Add( new ns_whiteboard::line( ns_whiteboard::xy_coords(last_x, last), 
      				  ns_whiteboard::xy_coords(x, y),
      				  0.2,
      				  color(0,0,0)) );

      
      last = y;
      last_x = x;
      cc++;
    }

    board.Add( new ns_whiteboard::line( ns_whiteboard::xy_coords(x_offset, off_t), 
					ns_whiteboard::xy_coords(x+x_offset, off_t),
					1,
					color(0,0,0)) );

   
  }

  
  FlatFileParser parser;
  
  parser.Open(fileName);
  int k = 0;

  int last = -1;
  while (parser.ParseLine()) {
    if (parser.GetItemCount() == 0)
      continue;

    double x = x_offset + k*scale_x;

    for (i=1; i<parser.GetItemCount(); i++) {
      //  double x = x_offset + k*scale_x;
      double y = y_offset + (i-1)*scale_y + off;

      double d = parser.AsFloat(i)/55000;
      //double d = log(1.+parser.AsFloat(i))/25;
      //if (i == 1)
      //d/=2.;
      
      d = 1-d;
      if (d < 0.)
	d = 0.;
      
      board.Add( new ns_whiteboard::rect( ns_whiteboard::xy_coords(x, y), 
					  ns_whiteboard::xy_coords(x+scale_x, y+scale_y),
					  color(d,d,d)) );

      x_max = x+scale_x;
      y_max = y+scale_y+t;
    }

    if (annot.isize() > 0) {
      //color a = color(0.5, 0.5, 0.8);
      color a = color(0.99, 0.99, 0.99);
      if (annot[k] == 1)
	a = color(0.99, 0.99, 0.);
      if (annot[k] == 2)
	a = color(0.99, 0.5, 0.);
      if (annot[k] == 3)
	a = color(0.99, 0.1, 0.);
      if (annot[k] >= 4)
	a = color(0.5, 0., 0.);

      board.Add( new ns_whiteboard::rect( ns_whiteboard::xy_coords(x, y_offset-scale_y), 
					  ns_whiteboard::xy_coords(x+scale_x, y_offset),
					  a) );
      
    }

    if (timeName != "") {
      //if ((annot[k]+1)/2 != last && last >= 0) {
	if (annot[k] != last && (last == 4 || annot[k] == 4)) {
	board.Add( new ns_whiteboard::line( ns_whiteboard::xy_coords(x, off_t - 80), 
					    ns_whiteboard::xy_coords(x, off_t + 80),
					    0.5,
					    color(0,0.2,0.6)) );
      }
	//last = (annot[k]+1)/2;
      last = annot[k];
      
    }
    

    k++;

   }
  /*board.Add( new ns_whiteboard::line( ns_whiteboard::xy_coords(x_offset, y_offset), 
				      ns_whiteboard::xy_coords(x_offset, y_max+y_offset), 
				      1.,
				      color(0., 0., 0.)) );*/
  
 
}


void ReadAnnot(svec<int> & a, const string & fileName)
{
  FlatFileParser parser;
  
  parser.Open(fileName);
  bool b = false;
  while (parser.ParseLine()) {
    if (parser.GetItemCount() == 0)
      continue;
    if (parser.AsString(0) == "FINAL") {
      b = true;
      parser.ParseLine();
      continue;
    }
    if (parser.Line() == "TB done") {
      b = false;
      continue;
    }
   
    if (b)
      a.push_back(parser.AsInt(1));
  }
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

  svec<int> annot;

  if (aName != "")
    ReadAnnot(annot, aName);

  
  ns_whiteboard::whiteboard board;

  int x_max = 0;
  int y_max = 0;

 
  string name = inName; 
  PlotOne(board, name, timeName, x_max, y_max, 0, annot);
  

  double x_offset = 20;
  double y_offset = 20;
 

  ofstream out(outName.c_str());
  
  ns_whiteboard::ps_display display(out, x_max + 2 * x_offset, y_max + 2 * y_offset);
  board.DisplayOn(&display);
 

  return 0;
}
