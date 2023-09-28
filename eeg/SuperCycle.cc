#include "eeg/SuperCycle.h"
#include "base/FileParser.h"
#include "base/StringUtil.h"
#include "eeg/BTLData.h"


void SuperCycleProc::Read(const string & fileName,
			  const string & templateName)
{
  int i, j;

  cout << "Enter" << endl;
  svec<double> start;
  svec<double> stop;
  svec<string> name;

  FlatFileParser parserTemp;
  parserTemp.Open(templateName);
  parserTemp.ParseLine();
  
  while (parserTemp.ParseLine()) {
    if (parserTemp.GetItemCount() == 0)
      continue;
    start.push_back(parserTemp.AsFloat(0)*64/500);
    stop.push_back(parserTemp.AsFloat(1)*64/500);
    name.push_back(parserTemp.AsString(3));
    cout << "Read " << parserTemp.Line() << endl;
  }

  m_templates.resize(start.isize());
  
  StringParser parser;

  
  parser.SetLine(fileName, ",");

  m_all.resize(parser.GetItemCount());

  for (i=0; i<m_templates.isize(); i++)
    m_templates[i].resize(parser.GetItemCount());

  cout << "Starting" << endl;
  
  for (i=0; i<parser.GetItemCount(); i++) {
    BTLData d;
    cout << "Reading " << parser.AsString(i) << endl;
    d.Read(parser.AsString(i));
    cout << "Parsing " << endl;
    ParseOne(m_all[i], d);
    cout << "Filling" << endl;
    for (j=0; j<m_templates.isize(); j++) {
      Fill((m_templates[j])[i], start[j], stop[j], name[j], m_all[i]);
      m_templates[j].Name() = name[j];
    }
    
  }
}

void SuperCycleProc::Fill(CycleLane & out, double from, double to, const string & name, const CycleLane & in)
{
  int i;

  cout << "Fill " << from << " - " << to << " " << name << endl;
  
  for (i=0; i<in.isize(); i++) {
    cout << in[i].Start() << " " << in[i].Stop() << endl;
    if (in[i].Start() >= from && in[i].Stop() <= to) {
      OneCycle tmp = in[i];
      tmp.Start() -= from;
      tmp.Stop() -= from;
      cout << "Filling " << tmp.Start() << " to " << tmp.Stop() << endl;
      out.push_back(tmp);
    }
  }      
}

void SuperCycleProc::ParseOne(CycleLane & l, const BTLData & d)
{
  //cout << "Enter parse" << endl;
  
  int i;
  string last;
  double start = 0.;
  for (i=0; i<d.EXG().isize(); i++) {
    //cout << i << endl;
    const string & s = d.EXG()[i].Cycle();
    if (s != "" && last == "") {
      start = d.EXG()[i].Time();
    }
    if (s == "" && last != "") {
      double stop = d.EXG()[i-1].Time();
      OneCycle cc;
      cc.Start() = start;
      cc.Stop() = stop;

      //cout << "Found " << last << endl;
      StringParser pp;
      pp.SetLine(last, "_");
      string clean;
      for (int j=0; j<pp.GetItemCount()-1; j++) {
	if (j > 0)
	  clean += "_";
	clean += pp.AsString(j);
      }
      //cout << "Cleaned to: " << clean << endl;
      //cout << "Adding cycle " << start << " to " << stop << endl;
      cc.Name() = clean;
      l.push_back(cc);
    }

    last = s;
  }
}

void SuperCycleProc::Process(CycleLane & out)
{
  int i, j, k;

  double max = m_all.Hi();

  cout << "Max pos: " << max << endl;
  
  for (i=0; i<m_templates.isize(); i++) {
    // Over all lanes
    const SuperCycle & t = m_templates[i];

    cout << "Template " << i << " has " << t.TotalCycles() << " cycles." << endl; 
    double best = 0.;
    double bpos = 0.;

    double last = -1;
    double lastlast = -1;

    double lo = t.Lo();
    double hi = t.Hi();

    int count = 0;
    
    for (double x=0; x<max; x += 0.002) {
      double lap = 0.;
      double full = 0.;

      // Over all lanes
      for (k=0; k<m_all.isize(); k++) {
	// Over all cycles in template
	//cout << t[k].isize() << endl;
	for (j=0; j<t[k].isize(); j++) {
	  lap += m_all[k].Lap((t[k])[j], x);
	  full += 1.;
	}
      }
      lap /= full;

      //cout << x << " lap " << lap << " " << full << endl;

      
      if (lastlast > 0. && last > lastlast && last > lap && last > 0.8) {
	cout << "Peak at: " << x << " height: " << last << endl;
	OneCycle super;
	super.Name() = t.Name() + "_(" + Stringify(count+1) + ")";
	super.Start() = x + lo;
	super.Stop() = x + hi;
	out.push_back(super);
	count++;
      }
      
      if (lap > best) {
	best = lap;
	bpos = x;
      }


      lastlast = last;
      last = lap;
      
      //cout << "Template " << i << " lap: " << lap << " pos: " << x << endl;
    }

    cout << "Best match: " << best << " at: " << bpos << endl;
  }
}

void SuperCycle::Write(const string & fileName) const
{

}
