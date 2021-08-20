//By Verite Rendition
//EF parser, takes EVE-Files page data and parses out the fileID of influence.png
//Yes it's ugly, but it works
#include <iostream>
#include <string>
#include <fstream>

using namespace std;

int main()
{
	bool flag = true;
	int i,j;
	int fileID;
	string line;
	ifstream xml;
	ofstream updateFile;
	updateFile.open("fileID.txt");
	xml.open("index.html");
	if(xml.is_open())
	{
		cout<<"EF Parser Working";
	}
	else
		cout<<"Not Working";
	while(flag)
	{
		getline(xml,line);
		i= line.find("influence.png</a></td>");
		if (i!=(-1))
		{
			getline(xml,line);
			getline(xml,line);
			getline(xml,line);
			getline(xml,line);
			//cout<<"Finding on line: "<<line<<endl;
			j=line.find(");");
			j-=6;
			fileID = strtol(line.substr(j,6).c_str(),NULL,10);
			updateFile<<fileID;
			flag = false;
		}
	}
	return 0;
}	

