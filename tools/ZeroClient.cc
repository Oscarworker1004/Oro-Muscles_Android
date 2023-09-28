#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include "base/Config.h"
#include "base/Log.h"
#include <dirent.h>

void ReadFiles(svec<string> & files, const string & root)
{
  int i;
  
  DIR *dir;
  struct dirent *ent;

  svec<string> dirlist;
  
  if ((dir = opendir (root.c_str())) != NULL) {
    /* print all the files and directories within directory */
    while ((ent = readdir (dir)) != NULL) {
      string dir = ent->d_name;
      if (dir == "." || dir == "..")
	continue;
      cout << dir << endl;
      dirlist.push_back(root + "/" + dir + "/"); 
    }
    closedir (dir);
  } else {
    cout << "ERROR: could not read directory " << root << endl;
  }

  for (i=0; i<dirlist.isize(); i++) {
    if ((dir = opendir (dirlist[i].c_str())) != NULL) {
      /* print all the files and directories within directory */
      while ((ent = readdir (dir)) != NULL) {
	string dir = ent->d_name;
	if (dir == "." || dir == "..")
	  continue;
	cout << dir << endl;
	files.push_back(dirlist[i] + "/" + dir); 
      }
      closedir (dir);
    } else {
      cout << "ERROR: could not read directory " << dirlist[i] << endl;
    }
  }

  //root + 
}


class HistoryBuff
{
public:
  HistoryBuff() {}

  void Read(const string & fileName) {
    FILE * p = fopen(fileName.c_str(), "r");
    if (p == NULL)
      return;
    fclose(p);

    FlatFileParser parser;
    parser.Open(fileName);
    m_files.clear();
    
    while (parser.ParseLine()) {
      if (parser.GetItemCount() == 0)
	continue;
      m_files.push_back(parser.Line());
    }

  }

  void Write(const string & fileName) {
    FILE * p = fopen(fileName.c_str(), "w");
    for (int i=0; i<m_files.isize(); i++)
      fprintf(p, "%s\n", m_files[i].c_str());
    fclose(p);
  }

  bool IsNew(const string & fileName) {
    int i;
    for (i=0; i<m_files.isize(); i++) {
      if (m_files[i] == fileName)
	return false;
    }
    return true;
  }

  void Add(const string & fileName) {
    m_files.push_back(fileName);
  }
  
private:
  svec<string> m_files;
};


int main( int argc, char** argv )
{

  /*  commandArg<string> fileCmmd("-i","input file");
  commandLineParser P(argc,argv);
  P.SetDescription("Client for the pi zero.");
  P.registerArg(fileCmmd);
 
  P.parse();
  
  string fileName = P.GetStringValueFor(fileCmmd);
  */

  //comment. ???
  /* FlatFileParser parser;
  
  parser.Open(fileName);

  while (parser.ParseLine()) {
    if (parser.GetItemCount() == 0)
      continue;
    int tt = to;
    if (parser.GetItemCount()-1 < tt)
      tt = parser.GetItemCount()-1;
    for (int i=from; i<=tt; i++) {
      cout << parser.AsString(i) << " ";
      if (bN)
	cout << endl;
    }
    if (!bN)
      cout << endl;
      }*/


  Config cfg;
  cfg.Read("zeroconf.txt");

  string host, token, project, port;

  cfg.Update("host", host);
  cfg.Update("port", port);
  cfg.Update("token", token);
  cfg.Update("project", project);

  string data_root;
  cfg.Update("data_root", data_root);

  if (port == "") {
    cout << "Variable 'port' is not set in the config!! Fix that!!" << endl;
    return -1;
  }
  
  int i;
  svec<string> files;
  svec<string> data;

  ReadFiles(files, data_root + "/Data Collection/");
  //ReadFiles(files, "/home/manfred/ExtraDisk/Work/BTL/zero/Desktop/");

  cout << "FINAL LIST" << endl;
  for (i=0; i<files.isize(); i++) {
    cout << i << "\t" << files[i] << endl;
    StringParser p;
    p.SetLine(files[i], "/");
    data.push_back(p.AsString(p.GetItemCount()-1));
  }

  

  // Avoid re-upload!
  HistoryBuff buff;

  string hist = "zeroclient.history";


  int max_attempts = 20;
  int attempts = 0;

  while (attempts < max_attempts) {
    buff.Read(hist);

    attempts++;
    int n = 0;
    int nw = 0;
   
    for (i=0; i<files.isize(); i++) {
      if (!buff.IsNew(files[i]))
	continue;
      
      nw++;
      string binfile = "\"" + files[i] + "\"";
      string curlCmmd = "curl  --insecure --data-binary @" + binfile + " \"https://" + host + ":" + port + "/oro??upload_bin_curl&user=" + token + "&project=" + project + "&data=" + data[i] + "\"";
      
      cout << "CURL command: " << endl;
      cout << curlCmmd << endl;
      
      int r = system(curlCmmd.c_str());
      int retCode = WEXITSTATUS(r);
      cout << "CURL returned: " << r << " -> " << retCode << endl;
      bool bSucc = false;
      if (retCode == 0 || retCode == 52) {
	cout << "SUCCESS!" << endl;
	buff.Add(files[i]);
	bSucc = true;
	n++;
      }
    
      buff.Write(hist);
      usleep(1000000); // Sleep for a sec
    }

    cout << endl;
    cout << "Files to upload:       " << nw << endl;
    cout << "Successfully uploaded: " << n << endl;

    
    if (nw == n) {
      cout << "ALL DONE HERE!" << endl;
      break;
    } else {
      // Try again later
      int wait = 1000000;
      cout << "Sleeping, will try again in " << wait << endl;
      usleep(wait); // Sleep for a sec
    }
  }
  

  
  return 0;
}
