
::MariaDB [mysql]> GRANT ALL ON bris_poblacion.* TO root@'192.168.43.155' IDENTIFIED BY 'october181986*';
::remote machine mysql -uroot -poctober181986* -h 192.168.43.155 bris_poblacion

set dt=%date:~7,2%-%date:~4,2%-%date:~10,4%_%time:~0,2%_%time:~3,2%_%time:~6,2%
set det=%date:~7,2%-%date:~4,2%-%date:~10,4%
echo Backup database.....
echo %dt%
C:
cd C:\Program Files\MariaDB 10.4\bin 

echo Creating dir if not exist
if not exist "C:\timetrack\databasebackup" mkdir C:\timetrack\databasebackup

setlocal
set LogPath=C:\timetrack\logs\
set LogFileExt=.log
set LogFileName=Daily Backup%LogFileExt%
::use set MyLogFile=%date:~4% instead to remove the day of the week
::[COLOR="DarkRed"]set MyLogFile=%date%
::set MyLogFile=%MyLogFile:/=-%[/COLOR]
set MyLogFile=%MyLogFile%
set MyLogFile=%LogPath%%MyLogFile%%LogFileName%
::Note that the quotes are REQUIRED around %MyLogFIle% in case it contains a space
IF NOT Exist "%LogPath%" mkdir %LogPath%
If NOT Exist "%MyLogFile%" goto:noseparator
Echo.>>"%MyLogFile%"
Echo.========================================================================ITALIAWorks========================================>>"%MyLogFile%"
:noseparator
::echo.%Date% >>"%MyLogFile%"
::echo.%Time% >>"%MyLogFile%"
echo.%Date% %Time% Preparing for backup... >>"%MyLogFile%"
echo.%Date% %Time% starting backup... >>"%MyLogFile%"


mysqldump.exe -e -uroot -poctober181986* -hlocalhost timetrack > C:\timetrack\databasebackup\timetrack_%det%.sql


echo.%Date% %Time% Preparing to save the backup >>"%MyLogFile%"
echo.%Date% %Time% Location: C:\timetrack\databasebackup >>"%MyLogFile%"
echo.%Date% %Time% Backup has been successfully proccessed with the file name of 'timetrack_%det%.sql' >>"%MyLogFile%"
echo.%Date% %Time% Location: C:\timetrack\databasebackup\bris_%det%.sql >>"%MyLogFile%"


echo.%Date% %Time% Ended... >>"%MyLogFile%"