#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include "src/Database.h"

int main( int argc, char** argv )
{

  /*
  commandArg<string> fileCmmd("-i","input file");
  commandArg<int> bCmmd("-from","from column");
  commandArg<int> eCmmd("-to","to column");
  commandArg<bool> nCmmd("-newline","add newline", false);
  commandLineParser P(argc,argv);
  P.SetDescription("Testing the file parser.");
  P.registerArg(fileCmmd);
  P.registerArg(bCmmd);
  P.registerArg(eCmmd);
  P.registerArg(nCmmd);
 
  P.parse();
  
  string fileName = P.GetStringValueFor(fileCmmd);
  int from = P.GetIntValueFor(bCmmd);
  int to = P.GetIntValueFor(eCmmd);
  bool bN = P.GetBoolValueFor(nCmmd);
  */
  Database users;

  users.Read("data/users.csv");

  Database one = users.Find("user", "hobey");

  cout << "PWD for hobey: " << one.FindIn(0, "password") << endl;

  Database db;

  db.Read("data/index.csv");

  int i, j;

  Database query = db.Find("user", "hobey");

  cout << query.isize() << endl;
  
  return 0;
}
