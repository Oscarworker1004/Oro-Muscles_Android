#include "signal/VideoWrap.h"


void VideoWrap::SetVideoName(const string & vidName)
{
  m_vidName = vidName;
  string cmmd = "rm " + vidName;
  int r = system(cmmd.c_str());
}

void VideoWrap::Start()
{

  m_proc.MakeFifo(m_fifo);
  
  string cmmd = "ffmpeg -f v4l2 -framerate 60 -video_size 1280x720 -input_format mjpeg -i /dev/video0  -pix_fmt yuv420p " + m_vidName;

		 
  m_proc.Spawn(cmmd, true);
 

  cout << "Start process" << endl;
  m_proc.Start();

}

void VideoWrap::Stop()
{
  m_proc.SendKeys("q");
}
