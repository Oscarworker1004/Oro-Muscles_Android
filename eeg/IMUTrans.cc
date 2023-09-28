#include "eeg/IMUTrans.h"
#include <math.h>


void Smooth(svec<double> & s)
{
  int i, j;

  svec<double> d;
  d.resize(s.isize(), 0.);
  
  for (i=2; i<s.isize()-2; i++) {
    d[i] += s[i]*1./3.8;
    d[i-1] += s[i]*0.8/3.8;
    d[i-2] += s[i]*0.6/3.8;
    d[i+1] += s[i]*0.8/3.8;
    d[i+2] += s[i]*0.6/3.8;    
  } 

  s = d;
}


void IMUTrans::Process()
{

  int i;

  for (i=0; i<m_acc.isize(); i++) {
    if (m_acc[i] < 0.)
      m_acc[i] = -m_acc[i];
  }

  Smooth(m_acc);
  //Smooth(m_acc);

  /*
  for (i=0; i<m_acc.isize(); i++) {
    m_acc[i] = log(1.+100*m_acc[i]);
    
    }*/


}

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
void IMUSensor::ActivityBySimilarity(svec < svec < double > > & data, const svec<IMUInterval> & in)
{
  int i, j;

  cout << "BY SIMILARITY, intervals: " << in.isize() << endl;
  
  
  svec<IMUSensor> test;
  test.resize(in.isize());

  svec<IMUSensor> target;
  target.resize(in.isize());

  svec<double> self;
  self.resize(in.isize(), 0.);

  int slackInSamples = 400;

  data.resize(in.isize());

  
  /*for (i=0; i<in.isize(); i++) {
    cout << "Set up: " << in[i].Name() << " -> " << in[i].Start() << " " << in[i].End() << endl;
    //cout << "Cut 1" << endl;
    Cut(test[i], IMUInterval(in[i].Start(), in[i].End()));
    //cout << "Copy" << endl;
    self[i] = test[i].Self();
    //cout << "Cut 2: " << in[i].Start() - slackInSamples << " " << in[i].End() + slackInSamples << endl;
    Cut(target[i], IMUInterval(in[i].Start() - slackInSamples, in[i].End() + slackInSamples));
    }*/


  cout << "EXTRACT" << endl;
  for (i=0; i<in.isize(); i++) {
    cout << "Set up: " << in[i].Name() << " -> " << in[i].Start() << " " << in[i].End() << endl;

    cout << "TEST:   " << in[i].End() - in[i].Start() << endl;
    Cut(test[i], IMUInterval(in[i].Start(), in[i].End()));
   
    self[i] = test[i].Self();
    cout << "TARGET: " << in[i].End() + 2* slackInSamples - in[i].Start() << endl;
    Cut(target[i], IMUInterval(in[i].Start() - slackInSamples, in[i].End() + slackInSamples));
  }
  cout << "DONE EXTRACT" << endl;

  double bestSim = 0.;
  string bestPair;
  
  for (i=0; i<test.isize(); i++) {
    //cout << "MATRIX" << endl;
    //cout << "ANNOT: " << i << " len = " << in[i].End() - in[i].Start()  << endl;
    cout << "Process " << in[i].Name() << " -> " << in[i].Start() << " " << in[i].End() << endl;
    
    //**************************************************
    // Do it slowly, but not run out of memory:
    //IMUSensor test_sensor;
    //Cut(test_sensor, IMUInterval(in[i].Start(), in[i].End()));
   
    
    for (j=0; j<test.isize(); j++) {
      if (i == j) {
	//cout << " 0.0";
	data[i].push_back(0.);
      } else {
	//**************************************************
	// Do it slowly, but not run out of memory:
	//IMUSensor target_sensor;
	//Cut(target_sensor, IMUInterval(in[j].Start() - slackInSamples, in[j].End() + slackInSamples));
	
	svec<double> dot;
	// Fast
	double match = target[j].ComputeMatch(dot, test[i]);
	
	// Slow
	//double match = target_sensor.ComputeMatch(dot, test_sensor);

	data[i].push_back(match);

	if (match > bestSim) {
	  bestSim = match;
	  bestPair = in[i].Name() + " " + in[j].Name();
	}
	  
	//=======================================================
	//match = 0.5*(match + target[i].ComputeMatch(dot, test[j]));
	//cout << " vs " << j << " -> " << match << endl;
	//cout << " " << match;
      }
    }
    //cout << endl;
  }


  cout << "BEST MATRIX SCRE: " << bestPair << endl;
  
}

//====================================================================
double IMUSensor::SlideEuc(int from, const IMUSensor & temp, int skip)
{
  int i, j, k;

  double diff = 0.;
  double diffZero = 0.;

  int pos = 0;
  int neg = 0;

  double realDiv = 0.;
  
  for (k=0; k<isize(); k++) { // channels
    for (j=0; j<m_channels[k].isize(); j++) { //signals
      const IMUSignal & me = m_channels[k][j];
      const IMUSignal & other = temp[k][j];
      
      for (i=from; i<from+other.isize(); i++) { // position
	if (skip > 0) {
	  if ((i+1-from) % skip == 0)
	    pos++;
	}
	if(skip < 0) {
	  if ((i+1-from) % (-skip) == 0)
	    neg++;	
	}

	if (pos+i-from >= other.isize()) {
	  //cerr << "Return temp: " << pos+i-from << " " << temp[j].isize() << " pos: " << pos << endl; 
	  break;
	}
	if (i+neg >= me.isize()) {
	  //cerr << "Return sign: " << i+neg << " " << all[j].isize() << endl; 
	  break;
	}
	
	diff += (other[pos+i-from] - me[i+neg]) * (other[pos+i-from] - me[i+neg]);
	diffZero += (other[pos+i-from]) * (other[pos+i-from]); // Could be more efficient to do this once...
	realDiv += 1.;
	//cerr << " -> " << i << " " << pos << " " << neg << " " << other[pos+i-from] << " " << me[i+neg] << " " << diff << endl;
	
      }
    }
  }

  //cerr << "DIFF " << diff << " " << diffZero << endl;

  
  diff = sqrt(diff/(double)temp[0][0].isize());
  diffZero = sqrt(diffZero/(double)temp[0][0].isize());
  //cerr << "zero: " << diffZero << endl;
  return diff - diffZero;
  
}


double IMUSensor::Self()
{
  int i, j, k;

  double diffZero = 0.;

  for (k=0; k<isize(); k++) { // channels
    for (j=0; j<m_channels[k].isize(); j++) { //signals
      const IMUSignal & me = m_channels[k][j];
      
      for (i=0; i<me.isize(); i++) { // position
	diffZero += me[i] * me[i]; // Could be more efficient to do this once...
	//cerr << " -> " << i << " " << pos << " " << neg << " " << other[pos+i-from] << " " << me[i+neg] << " " << diff << endl;
	
      }
    }
  }

  //cerr << "DIFF " << diff << " " << diffZero << endl;

  
  
  diffZero = sqrt(diffZero/(double)m_channels[0][0].isize());
 
  return diffZero;
  
}


double IMUSensor::ComputeMatch(svec<double> & d, const IMUSensor & temp)
{
  int i;
  
  d.clear();
  d.resize(m_channels[0][0].isize(), 0.);
  double max = 0.;

  if (d.isize() - temp[0][0].isize() < 0) {
    //cerr << "NO SLIDE: " << d.isize() - temp[0][0].isize() << endl;
    double x = -SlideEuc(0, temp);
    if (x > max) {
      max = x;
      //cerr << "NEW MAX: " << max << endl;
    }
  }

  
  
  for (i=0; i<d.isize() - temp[0][0].isize(); i++) {
    d[i] = -SlideEuc(i, temp);
    if (d[i] > max)
      max = d[i];
  }

  return max;
}

double IMUSensor::ComputeMatchWiggle(int & start, int & stop,
				     int from,
				     const IMUSensor & temp)
{
  int i;
  
  double max = 0.;

  double maxWig = 0.2;
  int maxLen =  (int)(0.5+(double)temp[0][0].isize()*(1. + maxWig));
  if (from + maxLen >= m_channels[0][0].isize() || from - maxLen < 0) {
    start = -1;
    stop = -1;
    // cout << "Skip wiggle " << from << " " <<  maxLen << " " << m_channels[0][0].isize() << endl;
    return 0;
  }

  //IMUSensor ref;
  //Cut(ref, IMUInterval(10*from, 10*(from+maxLen)));

  for (double d = -0.2; d<maxWig; d += 0.01) {
    int diff = (int)((double)temp[0][0].isize()*d);
    int skip = diff;
    int newLen = temp[0][0].isize() - skip;
    //cout << " -> Process skip " << skip << " ref=" << ref[0][0].isize() << endl;
    double localMax = 0.;

    // Pick better boundaries!!
    for (i=from-maxLen/2; i<=from + maxLen; i++) {
      double s = -SlideEuc(i, temp, skip);
      //cout << "  local " << i << " = " << s << endl;
      if (s > max) {
	max = s;
	start = i;
	stop = i + newLen;
	//cout << "FOUND Wiggle" << i << " " << newLen << endl;
      }
    }
  }

  return max;

}

void IMUSensor::GetActivitySeeded(svec<IMUInterval> & out, const svec<IMUInterval> & seed)
{
  cout << "ENTER Auto activity SEEDED" << endl;

  int i, j, k, l;

  double avg = 0.;
  for (i=0; i<m_channels[0][0].isize(); i++) {
    double sum = 0.;
    double num = 0.;
    for (j=0; j<m_channels.isize(); j++) {
      for (k=0; k<m_channels[j].isize(); k++) {
	double d = m_channels[j][k][i] * m_channels[j][k][i];
	sum += d;
	num += 1.;
      }
    }

    sum = sqrt(sum/num);
    avg += sum;
  }

  avg /= (double)m_channels[0][0].isize(); // average activity

  for (l=0; l<seed.isize(); l++) {
    double ind = 0.;

    if (seed[l].End() - seed[l].Start() < 250) // ???????????????????????????
      continue;

    // ARGGHHHH!!!!
    double div = 0.;
    for (i=seed[l].Start()/10; i<seed[l].End()/10; i++) {
      double sum = 0.;
      double num = 0.;
      for (j=0; j<m_channels.isize(); j++) {
	for (k=0; k<m_channels[j].isize(); k++) {
	  double d = m_channels[j][k][i] * m_channels[j][k][i];
	  sum += d;
	  num += 1.;
	}
      }

      if (num == 0.)
	cout << "SQEAL!!!!" << endl;
      // cout << "TEST " << sum << " " << num << endl;
      sum = sqrt(sum/num);
      ind += sum;
      div += 1.;
    }

    cout << " -> test : " << ind << " / " << avg << " " << seed[l].End() << " " << seed[l].Start() << endl;

    ind /= div;

    if (ind > avg) {
      IMUInterval on = seed[l];
      //on.Set(start, stop);
      on.Name() = "auto_act";
      out.push_back(on);
      cout << "ADD auto detect activity: " << ind << " / " << avg << endl;
    }
  }
}

void IMUSensor::GetActivity(svec<IMUInterval> & out)
{
  cout << "ENTER Auto activity" << endl;
  int i, j, k;
  
  int start = -1;
  int stop = -1;
  int up = 0;
  int down = 0;
  int thresh = 5;

  out.clear();
  
  for (i=0; i<m_channels[0][0].isize(); i++) {
    double sum = 0.;
    double num = 0.;
    for (j=0; j<m_channels.isize(); j++) {
      for (k=0; k<m_channels[j].isize(); k++) {
	double d = m_channels[j][k][i] * m_channels[j][k][i];
	sum += d;
	num += 1.;
      }
    }

    sum = sqrt(sum/num);
 
    if (sum > 0.7) {      
      if (start == -1)
	start = i*10;
      up++;
      down = 0;
    }
    if (sum <= 0.7) {      
      if (stop == -1)
	stop = i*10;
      down++;
      if (down > thresh) {
	if (up > thresh && stop - start > 400) {
	  //cout << "Detect: " << start << " - " << stop << endl;
	  IMUInterval on;
	  on.Set(start, stop);
	  on.Name() = "auto_act";
	  out.push_back(on);
	  cout << "ADD auto detect activity: " << start << " - " << stop << endl;
	}
	
	up = 0;
	start = -1;
	stop = -1;
      }
    }

    
  }



}
