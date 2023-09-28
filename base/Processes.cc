#include "base/Processes.h"
#include <stdio.h>
#include <stdlib.h>
#include <cstring>

ProcInfo::~ProcInfo()
{
  string cmmd = "rm " + m_fifo;
  if (m_fifo != "")
    int r = system(cmmd.c_str());
}

void ProcInfo::Spawn(const string & cmmdRaw, bool bg)
{
  string cmmd = cmmdRaw;
  
  FILE *fp;
  char path[1024];

  if (m_fifo != "")
    cmmd += " < " + m_fifo;
  if (bg)
    cmmd += " &";


  cout << "RUNNING " << cmmd << endl;

  int r = system(cmmd.c_str());
  
  /*
  fp = popen(cmmd.c_str(), "r");
  if (fp == NULL) {
    cout << "ERROR: Failed to run command " << cmmd << endl;
    exit(1);
  }

  
  while (fgets(path, sizeof(path), fp) != NULL) {
    // printf("%s", path);
    if (strlen(path) > 0 && path[strlen(path)-1] == '\n')
      path[strlen(path)-1] = 0;
    if (m_collect)
      m_output.push_back(path);
  }
  pclose(fp);
  */
}

void ProcInfo::MakeFifo(const string & name)
{
  m_fifo = name;
  string cmmd = "rm " + m_fifo;
  int r = system(cmmd.c_str());

  cout << "Make fifo" << endl;
  cmmd = "mkfifo " + m_fifo;
  r = system(cmmd.c_str());
}

void ProcInfo::Start()
{
  string start = "echo  -n . > " + m_fifo;
  int r = system(start.c_str());
}


void ProcInfo::SendKeys(const string & keys)
{
  string start = "echo " + keys + " > " + m_fifo;

  cout << "Send keys " << keys << endl;
  
  int r = system(start.c_str());
}



