#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include "base/StringUtil.h"
#include <unistd.h>

int main( int argc, char** argv )
{
  char cwd[1024];
  string exec;
  if (getcwd(cwd, sizeof(cwd)) != NULL) {
    exec = cwd;
    exec += "/";
  } else {
   
  }

  cout << "Exec dir: " << exec << endl;

  
  string cmmd = "crontab -l | grep startOroZero > tmpC";
  int ret = system(cmmd.c_str());
  
  // Write start script
  FILE * pS = fopen("startOroZero.sh", "w");
  fprintf(pS, "cd %s\n", exec.c_str());
  fprintf(pS, "%sZeroClient &\n", exec.c_str());
  fclose(pS);
  //------------------------

  // Make executable
  ret = system("chmod +x startOroZero.sh");
  ret = system("chmod +x ZeroClient");
  ret = system("chmod +x ZeroUpdate");

  // add to cron
  FlatFileParser p;
  p.Open("tmpC");
  p.ParseLine();
  if (p.GetItemCount() > 0) {
    cout << "Found " << p.Line() << " - not modifying." << endl;
  } else {
    FILE * p = fopen("line", "w");
    fprintf(p, "@reboot %sstartOroZero.sh\n", exec.c_str());
    fclose(p);
    
    string add = "crontab -l | cat - line | crontab -";
    cout << "Running " << add << endl;
    int r = system(add.c_str());
  }
  //----------------------
  
  return 0;
}
