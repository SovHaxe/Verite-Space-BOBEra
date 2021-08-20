//By Verite Rendition
//Station parser, takes formatted station data and parses it out in to a SQL script so that we can add new stations to the map
//Yes it's ugly, but it works
#include <iostream>
#include <string>
#include <fstream>

using namespace std;

int main()
{
	int i,j;
	int SSID;
	//char c;
	string line;
	ifstream xml;
	ofstream updateFile;
	updateFile.open("updateStations.sql");
	xml.open("ConquerableStationList.xml.aspx@version=2");
	if(xml.is_open())
	{
		cout<<"Alliance Sanitizer Working";
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
		i=line.find("solarSystemID=");
		i+=15;
		j=line.find('"',i);
		j=j-i;
		SSID = strtol(line.substr(i,j).c_str(),NULL,10);
		//cout<<SSID<<endl;
		updateFile<<"update mapsolarsystems set stantion = 1 where solarSystemID = "<<SSID<<";"<<endl;
		getline(xml,line);
	}
	updateFile.close();
	return 0;
}	