//By Verite Rendition
//Alliance sanitizer, takes raw alliance data and coverts it to a SQL statement to add any new allinaces to the DB
//Because I don't know nor care how to properly parse XML
//Yes it's ugly, but it works
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
	int alcolor;
	//char c;
	string line,ALname,temp;
	ifstream xml;
	ofstream updateFile;
	updateFile.open("updateAl.sql");
	xml.open("AllianceList.xml.aspx");
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
	//Confusing logic, but if </result> is not found, find returns npos, so we check for equality to see if we have NOT found the end
	while(line.find("</result>")==line.npos)
	{
		i=line.find("row name=");
		i+=10;
		j=line.find('"',i);
		j=j-i;
		ALname= line.substr(i,j);
		//cout<<ALname<<endl;
		i=line.find("allianceID=");
		i+=12;
		j=line.find('"',i);
		j=j-i;
		temp = line.substr(i,j);
		ALID = strtoul(temp.c_str(),NULL,10);
		//cout<<ALID<<endl;
		//cout<<temp.length();
		alcolor = strtol(temp.substr(j-6,j).c_str(),NULL,10);
		//cout<<alcolor<<endl;
		//cout<<ALID<<endl;
		updateFile<<"insert into evealliances values ("<<ALID<<","<<'"'<<ALname<<'"'<<","<<alcolor<<","<<"0);"<<endl;
		//This is terrible code, but it's the easiest way to make sure alliance names are updated should CCP ever decide to arbitrarily change BoB's name again
		//The performance hit is only a few seconds at best, and no harm is done if the name did not change
		updateFile<<"update evealliances set name = "<<'"'<<ALname<<'"'<<" where id = "<<ALID<<';'<<endl;
		getline(xml,line);
		while(line.find("</result>")==line.npos && line.find("allianceID")==line.npos)
		{
			getline(xml,line);
		}
	}
	updateFile.close();
	return 0;
}	

