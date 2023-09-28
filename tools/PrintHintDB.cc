
#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include "eeg/BTLData.h"
#include "eeg/Hints.h"
#include <math.h>



int main( int argc, char** argv )
{

  commandArg<string> fileCmmd("-i","input file");
  commandLineParser P(argc,argv);
  P.SetDescription("Prints the contents of a .db file.");
  P.registerArg(fileCmmd);
 
  P.parse();
  
  string fileName = P.GetStringValueFor(fileCmmd);
 
  Hints hints;
  hints.ReadDB(fileName);
  
  int i, j;

  
  cout << "Hints: " << hints.NumShapes() << endl;

  for (i=0; i<hints.NumShapes(); i++) {
    cout << "------------------------------------------" << endl;
    if (hints.Shape(i).IsCycle())
      cout << "Type: CYCLE" << endl;
    else
      cout << "Type: HINT" << endl;
    cout << "Name: " << hints.Shape(i).Name() << endl;
    cout << "From: " << hints.Shape(i).From() << endl;
    cout << "To:   " << hints.Shape(i).To() << endl;
    cout << "File:    " << hints.Shape(i).Meta().FileName()  << endl;
    cout << "Sample:  " << hints.Shape(i).Meta().Sample() << endl;
    cout << "Date:    " << hints.Shape(i).Meta().Date() << endl;
    cout << "Channel: " << hints.Shape(i).Meta().Channel() << endl;
    cout << "Comment: " << hints.Shape(i).Meta().Comment() << endl;

    cout << endl;
  }
  
  return 0;
}






























