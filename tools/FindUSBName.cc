#include <string>
#include "base/CommandLineParser.h"
#include "base/FileParser.h"
#include "base/StringUtil.h"
#include "signal/USBReader.h"

int main( int argc, char** argv )
{

  USBReader usb;
  usb.FindUSBName();
  
  return 0;
}
