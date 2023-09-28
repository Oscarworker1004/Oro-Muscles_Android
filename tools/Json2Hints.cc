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
  commandArg<string> outCmmd("-o","output file");
  commandLineParser P(argc,argv);
  P.SetDescription("Prints the contents of a .db file.");
  P.registerArg(fileCmmd);
  P.registerArg(outCmmd);
 
  P.parse();


  nlohmann::json js;


  
  string fileName = P.GetStringValueFor(fileCmmd);
  string outName = P.GetStringValueFor(outCmmd);
 
  Hints hints;

  ReadFile(js, fileName);
  
  int i, j;

  
  for (nlohmann::json::iterator it1 = js["hints"].begin(); it1 != js["hints"].end(); ++it1) {
    TDShape shape;
    auto xy = *it1;
    //cout << "Level " << endl;
    shape.Meta().FileName() = xy["file"];
    shape.Meta().Sample() = xy["sample"];
    shape.Meta().Date() = xy["date"];
    shape.Meta().Channel() = xy["channel"];
    shape.Meta().Comment() = xy["comment"];

    shape.Set(xy["from"], xy["to"], -1);

    //if (xy["type"] == "CYCLE")
    shape.SetCycle(xy["name"]);
    //cout << "Exg" << endl;

    //vector<double> l = xy["exg"]["data"].get<std::vector<double>>();
    //cout << "Yeah?" << endl;
    shape.DData() = xy["exg"]["data"].get<std::vector<double>>();
    shape.DGyro_X() = xy["gyro_x"]["data"].get<std::vector<double>>();
    shape.DGyro_Y() = xy["gyro_y"]["data"].get<std::vector<double>>();
    shape.DGyro_Z() = xy["gyro_z"]["data"].get<std::vector<double>>();
    shape.DAcc_X() = xy["acc_x"]["data"].get<std::vector<double>>();
    shape.DAcc_Y() = xy["acc_y"]["data"].get<std::vector<double>>();
    shape.DAcc_Z() = xy["acc_z"]["data"].get<std::vector<double>>();
 

   
    hints.AddShape(shape);
    
  }
  
  hints.WriteDB(outName);

  
  return 0;
}
