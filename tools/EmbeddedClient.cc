#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include "util/mutil.h"
#include "base/StringUtil.h"
#include "signal/USBReader.h"
#include "base/Config.h"
#include "signal/Bin2CSV.h"


int main( int argc, char** argv )
{

 
  commandArg<string> fileCmmd("-i","config file");
  commandLineParser P(argc,argv);
  P.SetDescription("Embedded client.");
  P.registerArg(fileCmmd);
 
  P.parse();
  
  string fileName = P.GetStringValueFor(fileCmmd);

  Config cfg;
  cfg.Read(fileName);

  string rec = "no";
  cfg.Update("record", rec);
  string binfile = "ctest.bin";

  cfg.Update("binfile", binfile);

  string do_vid = "no";
  cfg.Update("video", do_vid);
  string video_cmmd;
  cfg.GetFullValue(video_cmmd, "ffmpeg_command");

  string vid_data;
  if (video_cmmd == "") {
    do_vid = "no";
  } else {
    StringParser p;
    p.SetLine(video_cmmd);
    vid_data = p.AsString(p.GetItemCount()-1);
  }
  
  
  string s;
  if (rec == "yes" || rec == "Yes") {
    USBReader urd;

    cout << "Hit ENTER to start recording." << endl;
    if (do_vid == "yes" || do_vid == "Yes") {
      cout << "Recording video: hit 'q' to stop." << endl;
    } else {
      cout << "Hit ENTER again to stop." << endl;
    }
    cout << "Do NOT press ctrl+c!" << endl;
    cout << ">";
    cin.get();
    

    //cout << "Sleep" << endl;
    //usleep(25000000); // 1 sec


    if (do_vid == "yes" || do_vid == "Yes") {
      string cmmd = "rm " + vid_data;
      cout << "Remove video file: " << vid_data << endl;
      int ret = system(cmmd.c_str());

      cout << "Start write" << endl;
      urd.StartWrite(binfile);

      // Capture time stamps AT THE END to sync video and EMG!
      cout << "Start video using: " << video_cmmd << endl;
      ret = system(video_cmmd.c_str());
      
    } else {
      cout << "Start write" << endl;
      urd.StartWrite(binfile);
      cin.get();
    }

    
    cout << "Stopping" << endl;
    urd.Stop();

    cout << "All done recording!" << endl;
  } else {

    cout << "Skip recording." << endl;
  }
   
  string conv = "no";
  cfg.Update("convert", conv);
  string csvfile = "ctest.csv";
  cfg.Update("csvfile", csvfile);

  if (conv == "yes" || conv == "Yes") {
    cout << "Converting file" << endl;
 
    Bin2CSV b2c;

    b2c.Convert(csvfile, binfile);

  }

  string upload = "no";
  cfg.Update("upload", upload);
  string desc = "new_recording";
  cfg.Update("desc", desc);
  string jar = "MiniClient-1.0-SNAPSHOT.jar";
  cfg.Update("jar", jar);

  string host = "localhost";
  cfg.Update("host", host);
  string port = "8000";
  cfg.Update("port", port);
  string user = "UT1PH4I1sZ1uJL1Ojup4BR6Z";
  cfg.Update("user", user);
  string project = "12345";
  cfg.Update("project", project);

  
  if (upload == "yes" || upload == "Yes") {
    cout << "Please enter a name or description (no blanks, PLEASE): " << endl;
    cout << "> ";
    cin >> s;

    if (s != "") {
      for (int i=0; i<(int)s.length(); i++) {
	if (s[i] == ' ')
	  s[i] = '_';
      }
      desc = s;
    }

    string cmmd = "java -jar ";
    cmmd += jar;
    cmmd += " " + csvfile + " " + host + " " + port + " " + user + " " + project + " " + desc;
    if (vid_data != "")
      cmmd += " " + vid_data;
    cout << "Executing: " << cmmd << endl;
    int ret = system(cmmd.c_str());
  }
  
  return 0;
}
