#include <string>
#include "visual/Whiteboard.h"
#include "base/CommandLineParser.h"
#include "base/FileParser.h"

#include "base/SVector.h"
#include "base/Config.h"
#include "visual/Color.h"

#include <iostream>
#include <math.h>





int main( int argc, char** argv )
{

  commandArg<string> inCmmd("-i","input file");
  commandLineParser P(argc,argv);
  P.SetDescription("Makes plots.");
  P.registerArg(inCmmd);
  
  P.parse();

 
  
  string fileName = P.GetStringValueFor(inCmmd);

  svec<ns_whiteboard::xy_coords> a;
  svec<ns_whiteboard::xy_coords> b;
  svec<ns_whiteboard::xy_coords> c;
  svec<double> rms;
  
  FlatFileParser parser;
  
  parser.Open(fileName);

  double scale = 100.;
  while (parser.ParseLine()) {
    if (parser.GetItemCount() == 0)
      continue;

    ns_whiteboard::xy_coords ta(parser.AsFloat(0)*scale, parser.AsFloat(1)*scale);
    ns_whiteboard::xy_coords tb(parser.AsFloat(2)*scale, parser.AsFloat(3)*scale);
    ns_whiteboard::xy_coords tc(parser.AsFloat(4)*scale, parser.AsFloat(5)*scale);
    
    a.push_back(ta);
    b.push_back(tb);
    c.push_back(tc);
    rms.push_back(parser.AsFloat(6)/20.);
  }

  int i;
  int idx = 10000;

  for (i=900; i<1500; i++) {
  //for (i=25100; i<25700; i++) {
   
    ns_whiteboard::whiteboard board;
    
    int x_max = 0;
    int y_max = 0;
  
    board.Add( new ns_whiteboard::rect( ns_whiteboard::xy_coords(0, 0), 
				      ns_whiteboard::xy_coords(9000, 2000),
				      color(0.99,0.99,0.99)) );


    
    
    
    double x_offset = 150;
    double y_offset = 50;

    double width = rms[i];
    //for (i=0; i<a.isize(); i++) {
    
    board.Add( new ns_whiteboard::line( ns_whiteboard::xy_coords(a[i].first+x_offset, a[i].second+y_offset), 
					ns_whiteboard::xy_coords(b[i].first+x_offset, b[i].second+y_offset),
					width,
					color(0.8,0.1,0)) );
    board.Add( new ns_whiteboard::line( ns_whiteboard::xy_coords(a[i].first+x_offset, a[i].second+y_offset), 
					ns_whiteboard::xy_coords(c[i].first+x_offset, c[i].second+y_offset),
					width,
					color(0.01,0.8,0)) );
    board.Add( new ns_whiteboard::line( ns_whiteboard::xy_coords(c[i].first+x_offset, c[i].second+y_offset), 
					ns_whiteboard::xy_coords(b[i].first+x_offset, b[i].second+y_offset),
					width,
					color(0.5,0.5,0.8)) );
  
    

    
    x_max = 300;
    y_max = 300;
    char file[1024];
    sprintf(file, "animate/tri%d.ps", idx);
    ofstream out(file);
    
    ns_whiteboard::ps_display display(out, x_max, y_max);
    board.DisplayOn(&display);
    idx++;
  }

  return 0;
}
