#include <Windows.h>
#include <iostream>
#include <fstream>
#include <string>

using namespace std;

     void main()
     {
		 int i,fileID;
		 char month[3],day[3],year[6];
		 string smonth,sday,syear,date,line;
		 SYSTEMTIME st;
		 ofstream updateFile;
		 ifstream getFileID;

		 updateFile.open("autobot.bat");
		 getFileID.open("fileID.txt");
		 getline(getFileID,line);
		 fileID=strtol(line.substr(0,line.length()).c_str(),NULL,10);

         GetSystemTime(&st);
		 _itoa_s(st.wDay,day,10);
		 _itoa_s(st.wMonth,month,10);
		 _itoa_s(st.wYear,year,10);
		 smonth=month;
		 sday=day;
		 syear=year;
		 if (smonth.length()<2)
			 smonth.insert(0,"0");
		 if (sday.length()<2)
			 sday.insert(0,"0");
		 date=syear + smonth + sday;
		 //cout<<date<<endl;

		updateFile<<"del /q Sovereignty.xml.aspx"<<endl;
		updateFile<<"del /q AllianceList.xml.aspx"<<endl;
		updateFile<<"del /q "<<'"'<<"ConquerableStationList.xml.aspx@version=2"<<'"'<<endl;
		updateFile<<"del /q evemaps.asp"<<endl;
		updateFile<<"del /q oldsov.txt"<<endl;
		updateFile<<"move sov.txt oldsov.txt"<<endl;
		updateFile<<endl;
		//Pull sov data from Wollari as he keeps track of strategic levels. Under agreement to always credit DOTLAN as data source
		updateFile<<"wget.exe http://evemaps.dotlan.net/api/Sovereignty.xml"<<endl;
		updateFile<<"move Sovereignty.xml sovereignty.xml.aspx"<<endl;
		updateFile<<"wget.exe http://api.eve-online.com/eve/AllianceList.xml.aspx"<<endl;
		updateFile<<"wget.exe http://api.eve-online.com/eve/ConquerableStationList.xml.aspx?version=2"<<endl;
		updateFile<<"SoverigntySanitizer.exe"<<endl;
		updateFile<<"AllianceSanitizer.exe"<<endl;
		updateFile<<"SovChangeFinder.exe"<<endl;
		updateFile<<"StationParserNew.exe"<<endl;
		updateFile<<endl;
		updateFile<<"copy updateAl.sql SQLoutput"<<char(92)<<"updateAl.sql"<<endl;
		updateFile<<"copy updateSov.sql SQLoutput"<<char(92)<<"updateSov.sql"<<endl;
		updateFile<<"copy updateSovChange.sql SQLoutput"<<char(92)<<"updateSovChange.sql"<<endl;
		updateFile<<"copy updateStations.sql SQLoutput"<<char(92)<<"updateStations.sql"<<endl;
		//Following line is for daily backup of sov XML file on the Windows box that generates the back
		//updateFile<<"copy Sovereignty.xml.aspx D:"<<char(92)<<"shares"<<char(92)<<"MapData"<<char(92)<<"Oldsovdata"<<char(92)<<date<<".xml"<<endl;
		updateFile<<"del /q updateAl.sql"<<endl;
		updateFile<<"del /q updateSov.sql"<<endl;
		updateFile<<"del /q updateSovChange.sql"<<endl;
		updateFile<<"del /q updateStations.sql"<<endl;
		updateFile<<"mysql.exe"<<" -f --user=root --password=(redacted) eve < SQLoutput"<<char(92)<<"updateAl.sql"<<endl;
		updateFile<<"mysql.exe"<<" -f --user=root --password=(redacted) eve < SQLoutput"<<char(92)<<"updateSov.sql"<<endl;
		updateFile<<"mysql.exe"<<" -f --user=root --password=(redacted) eve < SQLoutput"<<char(92)<<"updateSovChange.sql"<<endl;
		updateFile<<"mysql.exe"<<" -f --user=root --password=(redacted) eve < SQLoutput"<<char(92)<<"updateStations.sql"<<endl;
		updateFile<<endl;
		updateFile<<"java"<<" -jar -Xss32m -Xmx128m EVEMap.jar -user root -password (redacted)"<<endl;
		updateFile<<endl;
		updateFile<<"pngcrush.exe -c 2 -m 114 influence.png output.png"<<endl;
		updateFile<<"advpng.exe -z -4 output.png"<<endl;
		updateFile<<"del /q influence.png"<<endl;
		updateFile<<"move output.png influence.png"<<endl;
		updateFile<<endl;
		updateFile<<"del /q C:"<<char(92)<<"Inetpub"<<char(92)<<"wwwroot"<<char(92)<<"influence.png"<<endl;
		updateFile<<"copy influence.png "<<date<<".png"<<endl;
		//updateFile<<"copy "<<date<<".png Oldmaps"<<char(92)<<date<<".png"<<endl;
		updateFile<<"copy "<<date<<".png D:"<<char(92)<<"shares"<<char(92)<<"MapData"<<char(92)<<"Oldmaps"<<char(92)<<date<<".png"<<endl;
		updateFile<<"copy influence.png C:"<<char(92)<<"Inetpub"<<char(92)<<"wwwroot"<<char(92)<<endl;
		updateFile<<"copy "<<date<<".png C:"<<char(92)<<"Inetpub"<<char(92)<<"wwwroot"<<char(92)<<endl;
		updateFile<<endl;
		updateFile<<"del /q ef.txt"<<endl;
		updateFile<<"curl -c ef.txt -d "<<'"'<<"username=(redacted)&pw=(redacted)&login=login"<<'"'<<" -e http://www.eve-files.com/ http://www.eve-files.com/login.dxd"<<endl;
		updateFile<<"curl -b ef.txt -d "<<'"'<<"btnDelete=Delete+selected+file(s)&fileID=,"<<fileID<<",&filename=&description=&order=asc&filter=custom"<<'"'<<" http://www.eve-files.com/media/corp/Verite/"<<endl;
		//updateFile<<"curl -b ef.txt -F FILE1=@"<<date<<".png -F Submit=Upload http://www.eve-files.com/media/corp/Verite/submit.dxd"<<endl;
		//updateFile<<"curl -b ef.txt -F FILE1=@influence.png -F Submit=Upload http://www.eve-files.com/media/corp/Verite/submit.dxd"<<endl;
		updateFile<<"sleep.exe 30"<<endl;
		updateFile<<"curl -b ef.txt -d "<<'"'<<"GetURL=http://(redacted)/influence.png&Submit=submit"<<'"'<<" -e http://www.eve-files.com http://www.eve-files.com/wget.dxd"<<endl;
		updateFile<<"sleep.exe 10"<<endl;
		updateFile<<"curl -b ef.txt -d "<<'"'<<"GetURL=http://(redacted)/"<<date<<".png&Submit=submit"<<'"'<<" -e http://www.eve-files.com http://www.eve-files.com/wget.dxd"<<endl;
		updateFile<<endl;
		updateFile<<"del /q "<<date<<".png";
		updateFile.close();
		//cin>>i;
     }