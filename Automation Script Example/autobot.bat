del /q Sovereignty.xml.aspx
del /q AllianceList.xml.aspx
del /q "ConquerableStationList.xml.aspx@version=2"
del /q evemaps.asp
del /q oldsov.txt
move sov.txt oldsov.txt

wget.exe http://evemaps.dotlan.net/api/Sovereignty.xml
move Sovereignty.xml sovereignty.xml.aspx
wget.exe http://api.eve-online.com/eve/AllianceList.xml.aspx
wget.exe http://api.eve-online.com/eve/ConquerableStationList.xml.aspx?version=2
SoverigntySanitizer.exe
AllianceSanitizer.exe
SovChangeFinder.exe
StationParserNew.exe

copy updateAl.sql SQLoutput\updateAl.sql
copy updateSov.sql SQLoutput\updateSov.sql
copy updateSovChange.sql SQLoutput\updateSovChange.sql
copy updateStations.sql SQLoutput\updateStations.sql
copy Sovereignty.xml.aspx D:\shares\MapData\Oldsovdata\20110328.xml
del /q updateAl.sql
del /q updateSov.sql
del /q updateSovChange.sql
del /q updateStations.sql
mysql.exe -f --user=root --password=(redacted) eve < SQLoutput\updateAl.sql
mysql.exe -f --user=root --password=(redacted) eve < SQLoutput\updateSov.sql
mysql.exe -f --user=root --password=(redacted) eve < SQLoutput\updateSovChange.sql
mysql.exe -f --user=root --password=(redacted) eve < SQLoutput\updateStations.sql

java -jar -Xss32m -Xmx128m EVEMap.jar -user root -password (redacted)

pngcrush.exe -c 2 -m 114 influence.png output.png
advpng.exe -z -4 output.png
del /q influence.png
move output.png influence.png

del /q C:\Inetpub\wwwroot\influence.png
copy influence.png 20110328.png
copy 20110328.png D:\shares\MapData\Oldmaps\20110328.png
copy influence.png C:\Inetpub\wwwroot\
copy 20110328.png C:\Inetpub\wwwroot\

del /q ef.txt
curl -c ef.txt -d "username=(redacted)&pw=(redacted)&login=login" -e http://www.eve-files.com/ http://www.eve-files.com/login.dxd
curl -b ef.txt -d "btnDelete=Delete+selected+file(s)&fileID=,203838,&filename=&description=&order=asc&filter=custom" http://www.eve-files.com/media/corp/Verite/
sleep.exe 30
curl -b ef.txt -d "GetURL=http://(redacted)/influence.png&Submit=submit" -e http://www.eve-files.com http://www.eve-files.com/wget.dxd
sleep.exe 10
curl -b ef.txt -d "GetURL=http://(redacted)/20110328.png&Submit=submit" -e http://www.eve-files.com http://www.eve-files.com/wget.dxd

del /q 20110328.png