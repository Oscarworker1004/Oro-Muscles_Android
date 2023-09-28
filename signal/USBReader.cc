#include "signal/USBReader.h"
// Linux headers
#include <fcntl.h> // Contains file controls like O_RDWR
#include <errno.h> // Error integer and strerror() function
#include <termios.h> // Contains POSIX terminal control definitions
#include <unistd.h> // write(), read(), close()

#ifdef __APPLE_CC__
#include "base/FileParser.h"
#endif

void * start_thread_USB(void * str) {
  USBReader * pMe = (USBReader*)str;
  pMe->Collect();
  return NULL;
}

void USBReader::FindUSBName()
{
#ifdef __APPLE_CC__
  int r = system("ls /dev/ > tmpDev");
   FlatFileParser parser;
  
  parser.Open("tmpDev");
  while (parser.ParseLine()) {
    if (parser.GetItemCount() == 0)
      continue;
    if (strstr(parser.AsString(0).c_str(), "cu.usbserial-A") != NULL) {
      cout << "Found device: " << parser.AsString(0) << endl;
      m_USB = parser.AsString(0);
    }

  }  
#endif
}


void USBReader::Collect() {
  m_out.Open(m_outName.c_str());

  FindUSBName();
  
  int serial_port = open(m_USB.c_str(), O_RDONLY);
  cout << "Returned: " << serial_port << endl;
  struct termios tty;
  tcgetattr(serial_port, &tty);


  tty.c_cflag &= ~PARENB; // Clear parity bit, disabling parity (most common)
  tty.c_cflag &= ~CSTOPB; // Clear stop field, only one stop bit used in communication (most common)
  tty.c_cflag &= ~CSIZE; // Clear all bits that set the data size 
  tty.c_cflag |= CS8; // 8 bits per byte (most common)
  tty.c_cflag &= ~CRTSCTS; // Disable RTS/CTS hardware flow control (most common)
  tty.c_cflag |= CREAD | CLOCAL; // Turn on READ & ignore ctrl lines (CLOCAL = 1)

  tty.c_lflag &= ~ICANON;
  tty.c_lflag &= ~ECHO; // Disable echo
  tty.c_lflag &= ~ECHOE; // Disable erasure
  tty.c_lflag &= ~ECHONL; // Disable new-line echo
  tty.c_lflag &= ~ISIG; // Disable interpretation of INTR, QUIT and SUSP
  tty.c_iflag &= ~(IXON | IXOFF | IXANY); // Turn off s/w flow ctrl
  tty.c_iflag &= ~(IGNBRK|BRKINT|PARMRK|ISTRIP|INLCR|IGNCR|ICRNL); // Disable any special handling of received bytes

  tty.c_oflag &= ~OPOST; // Prevent special interpretation of output bytes (e.g. newline chars)
  tty.c_oflag &= ~ONLCR; // Prevent conversion of newline to carriage return/line feed
  // tty.c_oflag &= ~OXTABS; // Prevent conversion of tabs to spaces (NOT PRESENT ON LINUX)
  // tty.c_oflag &= ~ONOEOT; // Prevent removal of C-d chars (0x004) in output (NOT PRESENT ON LINUX)

  tty.c_cc[VTIME] = 10;    // Wait for up to 1s (10 deciseconds), returning as soon as any data is received.
  tty.c_cc[VMIN] = 0;






  
  
  cfsetispeed(&tty, B230400);
  cfsetospeed(&tty, B230400);
  int rr = tcsetattr(serial_port, TCSANOW, &tty);
  cout << "Set returned: " << rr << endl;
  
  //close(serial_port);
  

  
  //m_pUSB = fopen(m_USB.c_str(), "rb");

  //fflush(m_pUSB);
  // SET BAUD RATE!!!!!

  /*
  if (m_pUSB == NULL) {
    cout << "ERROR, could not open USB for read!" << endl;
    }*/
  
  int i;
  
  while (GetRun()) {
    char c[4096];
    int n = 0;
    //n = fread(&c, 1, sizeof(c), m_pUSB);
    n = read(serial_port, &c, sizeof(c));

    //=========================================================
    // Extra safety measure!!!!
    if (n > 0 && (n % 2 == 0 || (n == 294 || n % 98 == 0))) {
      cout << "Writing bytes: " << n << endl;
      for (i=0; i<n; i++) {
	m_out.Write(c[i]);    
      }

      // =============== buffer save =========================
      if (m_bBuffer) { 
	pthread_mutex_lock(&m_lock); // Protect!
	for (i=0; i<n; i++) {
	  m_buffer.Write(c[i]);
	}
	pthread_mutex_unlock(&m_lock);
      }
      
    } else {
      usleep(10);
    }
  }
  
  close(serial_port);
 
  //fclose(m_pUSB);
  m_pUSB = NULL;
  m_out.Close();
  
  SetDone();
}
