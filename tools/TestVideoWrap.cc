#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include "signal/VideoWrap.h"
#include "base/Log.h"


int main( int argc, char** argv )
{


  VideoWrap vid;

  vid.SetVideoName("tmp.mp4");

  vid.Start();

  cout << "Sleeping" << endl;
  MSleep(30000);

  vid.Stop();

  return 0;
  
  //int r = system("test/doVid");
  //cout << endl << "RET: " << r << endl << endl;

  int i;
  
  ProcInfo proc;

  proc.MakeFifo("vidfifo");
  
  //proc.Spawn("ls -l");
  proc.Spawn("ffmpeg -f v4l2 -framerate 60 -video_size 1280x720 -input_format mjpeg -i /dev/video0  -pix_fmt yuv420p out.mp4", true);
  // proc.Spawn("test/doVid");

  cout << "Start process" << endl;
  proc.Start();
  //proc.SendKeys("y");

  cout << "Sleeping" << endl;
  MSleep(30000);

  cout << "Waking up" << endl;
  proc.SendKeys("q");
  
  cout << "Printing!" << endl;
  
  const svec<string> & out = proc.Output();
  for (i=0; i<out.isize(); i++) {
    cout << i << "\t" << out[i] << endl;
  }
    
  
  return 0;
}
