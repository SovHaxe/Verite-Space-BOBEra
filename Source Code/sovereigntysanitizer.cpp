//By Verite Rendition
//Sovereignty parser, takes raw sov data and formats it for easier comparison later on in sovchangefinder. Also spits out a SQL statement
//Yes it's ugly, but it works
//Custom version to read Wollari's API data
#include <iostream>
#include <string>
#include <fstream>

using namespace std;

int main()
{
	int i,j;
	int SSID;
	unsigned int ALID, conSov;
	int sovlevel;
	//char c;
	string line;
	ifstream xml;
	ofstream updateFile,updateFile2;
	updateFile.open("updateSov.sql");
	updateFile2.open("sov.txt");
	xml.open("Sovereignty.xml.aspx");
	if(xml.is_open())
	{
		cout<<"Sov Sanitizer Working";
	}
	else
		cout<<"Not Working";
	for (int i=0;i<5;i++) //Burn off useless lines
	{
		getline(xml,line);
		//cout<<line<<endl;
	}
	getline(xml,line);//Get first line
	while(line.find("</rowset>")==line.npos)
	{
		//cout<<line<<endl;
		i=line.find("solarSystemID=");
		i+=15;
		SSID = strtol(line.substr(i,8).c_str(),NULL,10);
		//cout<<SSID<<endl;
		i=line.find("allianceID=");
		i+=12;
		j=line.find('"',i);
		j=j-i;
		ALID = strtoul(line.substr(i,j).c_str(),NULL,10);
		/*i=line.find("constellationSovereignty=");
		i+=26;
		j=line.find('"',i);
		j=j-i;
		conSov = strtoul(line.substr(i,j).c_str(),NULL,10);*/
		//cout<<ALID<<endl;
		i=line.find("strategyLevel=");
		i+=15;
		sovlevel = strtol(line.substr(i,1).c_str(),NULL,10);
		//cout<<sovlevel<<endl;
		updateFile<<"update mapsolarsystems set allianceID = "<<ALID<<" where solarSystemID = "<<SSID<<";"<<endl;
		updateFile<<"update mapsolarsystems set sovereigntyLevel = "<<sovlevel<<" where solarSystemID = "<<SSID<<";"<<endl;
		//updateFile<<"update mapsolarsystems set constellationSov = "<<conSov<<" where solarSystemID = "<<SSID<<";"<<endl;
		updateFile2<<SSID<<","<<ALID<<endl;
		getline(xml,line);
	}
	updateFile.close();
	updateFile2.close();
	return 0;
}	

