#include "eeg/Freq.h"
#include "signal/CrossCorr.h"

void FreqSequence::Feed(const svec<double> & channel)
{
  int i;
  //cout << "Feeding..." << endl;
  for (i=0; i<channel.isize(); i += m_frame/2) {
    if (i + m_frame >= channel.isize())
      break;
    //cout << "Frame: " << i << endl;
    FreqVec one;    
    OneFrame(one, channel, i);
    m_data.push_back(one);
  }
}

void FreqSequence::OneFrame(FreqVec & one, const svec<double> & channel, int from)
{
  int i;
  svec<double> frame;
  svec<double> freq;

  frame.resize(m_frame*2, 0.);
  for (i=from; i<m_frame+from; i++) {
    frame[i-from] = channel[i];
  }

  //cout << "FFT" << endl;
  m_corr.FFT(freq, frame);
  
  //cout << "Bin." << endl;
  one.Time() = from;
  Bin(one, freq);
  
}

void FreqSequence::Bin(FreqVec & out, const svec<double> & freq)
{
  int div = 16;
  out.resize(m_frame/div);
  
  for (int i=0; i<m_frame/2; i++) {
    out[2*i/div] += freq[i];
    //cout << "SIG " << freq[i] << endl;
  }
  //throw;
}



void FreqSequence::Print(const string & fileName)
{
  int i;

  //cout << "Write to: " << fileName << endl;
  FILE * pOut = fopen(fileName.c_str(), "w");
  
  for (i=0; i<isize(); i++) {
    fprintf(pOut, "[%d]", m_data[i].Time());
    for (int j=0; j<m_data[i].isize(); j++)
      fprintf(pOut, "\t%f", (m_data[i])[j]);
    fprintf(pOut, "\n");
  }
  fclose(pOut);
}
