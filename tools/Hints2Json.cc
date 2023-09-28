#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include "eeg/BTLData.h"
#include "eeg/Hints.h"
#include <math.h>
#include "extern/json/nlohmann/json.hpp"
#include <iostream>
#include <fstream>
#include <sstream>      // std::stringstream


void ReadFile(nlohmann::json & js, const string & fileName)
{
  std::ifstream in(fileName); 
  in >> js;
}

void ReadData(nlohmann::json & js, const string & data)
{
  stringstream ss;
  ss << data << endl;

  ss >> js;

}
  
void WriteFile(nlohmann::json & js, const string & fileName)
{

  std::ofstream out(fileName); 
  out << js;

}

void WriteData(nlohmann::json & js, string & data)
{

  stringstream ss;
  ss << js << endl;

  ss >> data;

}

void AddOne(string & p, const svec<double> & data, const string & label)
{
  int i;
  p += "{ \"" + label + "\": [";
  for (i=0; i<data.isize(); i++) {   
    p += Stringify(data[i]);
    if (i < data.isize()-1)
      p += ",";
  }
  p += "]}";

}

int main( int argc, char** argv )
{

  commandArg<string> fileCmmd("-i","input file");
  commandLineParser P(argc,argv);
  P.SetDescription("Prints the contents of a .db file.");
  P.registerArg(fileCmmd);
 
  P.parse();


  nlohmann::json js;


  
  string fileName = P.GetStringValueFor(fileCmmd);
 
  Hints hints;
  hints.ReadDB(fileName);
  
  int i, j;


  

  string p = "{ \"hints\": [";

  for (i=0; i<hints.NumShapes(); i++) {
    p += "{";
    // cout << "------------------------------------------" << endl;
    if (hints.Shape(i).IsCycle())
      p += "\"type\": \"CYCLE\","; 
    else
      p += "\"type\": \"HINT\","; 


    p += "\"name\": \"" + hints.Shape(i).Name() + "\",";
    p += "\"from\": " + Stringify(hints.Shape(i).From()) + ","; 
    p += "\"to\": " + Stringify(hints.Shape(i).To()) + ","; 
    p += "\"file\": \"" + hints.Shape(i).Meta().FileName() + "\",";
    p += "\"sample\": \"" + hints.Shape(i).Meta().Sample() + "\",";
    p += "\"date\": \"" + hints.Shape(i).Meta().Date() + "\",";
    p += "\"channel\": \"" + hints.Shape(i).Meta().Channel() + "\",";
    p += "\"comment\": \"" + hints.Shape(i).Meta().Comment() + "\",";

    p += "\"exg\":";
    AddOne(p, hints.Shape(i).DData(), "data");
    p += ",";

    p += "\"gyro_x\":";
    AddOne(p, hints.Shape(i).DGyro_X(), "data");
    p += ",";

    p += "\"gyro_y\":";
    AddOne(p, hints.Shape(i).DGyro_Y(), "data");
    p += ",";

    p += "\"gyro_z\":";
    AddOne(p, hints.Shape(i).DGyro_Z(), "data");
    p += ",";

    p += "\"acc_x\":";
    AddOne(p, hints.Shape(i).DAcc_X(), "data");
    p += ",";

    p += "\"acc_y\":";
    AddOne(p, hints.Shape(i).DAcc_Y(), "data");
    p += ",";

    p += "\"acc_z\":";
    AddOne(p, hints.Shape(i).DAcc_Z(), "data");

   
    if (i < hints.NumShapes()-1)
      p += ",";
    p += "}";
 
  }
  p += "]}\n";

  //  cout << p << endl;
  
  nlohmann::json patch = nlohmann::json::parse(p);

  //js.merge_patch(patch);

  string data;
  WriteData(patch, data);
  cout << data << endl;
  return 0;
}
