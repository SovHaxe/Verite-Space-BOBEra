//By Verite Rendition
//Takes formatted sov data and builds a map of it to quickly determine what systems had their sovereignty change so that we can generate little circles for them
#include <iostream>
#include <string>
#include <fstream>
#include <map>

using namespace std;

int main()
{
	int i,j;
	int SSID;
	unsigned int ALID,ALID2;
	string line,line2;
	ifstream xml;
	ifstream xml2;
	ofstream updateFile;
	updateFile.open("updateSovChange.sql");
	xml.open("sov.txt");
	xml2.open("oldsov.txt");
	map<int,unsigned int> oldsov;
	map<int,unsigned int> newsov;
	map<int,unsigned int>::iterator oldit;
	map<int,unsigned int>::iterator newit;
	if(xml.is_open())
	{
		cout<<"Working";
	}
	else
		cout<<"Not Working";
	updateFile<<"delete from sovchangelog;"<<endl;
	while(!xml.eof())
	{
		getline(xml,line);
		i=line.find(",");
		SSID = strtol(line.substr(0,i).c_str(),NULL,10);
		j=line.length()-i;
		i++;
		ALID = strtoul(line.substr(i,j).c_str(),NULL,10);
		newsov.insert(pair<int,unsigned int>(SSID,ALID));
	}
	while(!xml2.eof())
	{
		getline(xml2,line);
		i=line.find(",");
		SSID = strtol(line.substr(0,i).c_str(),NULL,10);
		j=line.length()-i;
		i++;
		ALID = strtoul(line.substr(i,j).c_str(),NULL,10);
		oldsov.insert(pair<int,unsigned int>(SSID,ALID));
	}
	newit = newsov.begin();
	while(newit!=newsov.end())
	{
		SSID=newit->first;
		ALID=newit->second;
		oldit=oldsov.find(SSID);
		ALID2=oldit->second;
		if(ALID2 != ALID)
		{
			//cout<<"FOUND CHANGE"<<endl;
			updateFile<<"insert into sovchangelog values ("<<ALID2<<","<<ALID<<","<<SSID<<","<<"0);"<<endl;
		}
		newit++;
	}
	return 0;
}	