echo off
echo ��ʼ����PAC����...

:choice
set choice=""
set /p choice=���룺"1"-����PAC����, "2"-�ر�PAC����:

if /i "%choice%" == "1" goto open
if /i "%choice%" == "2" goto close
goto choice

:open
echo ����PAC����...
reg add "HKCU\Software\Microsoft\Windows\CurrentVersion\Internet Settings" /v AutoConfigURL /t REG_SZ /d $PAC_URL$ /f
goto exit

:close
echo �ر�PAC����...
reg delete "HKCU\Software\Microsoft\Windows\CurrentVersion\Internet Settings" /v AutoConfigURL /f
reg add "HKCU\Software\Microsoft\Windows\CurrentVersion\Internet Settings\Connections" /v DefaultConnectionSettings /t REG_BINARY /d 46000000 /f
reg add "HKCU\Software\Microsoft\Windows\CurrentVersion\Internet Settings\Connections" /v SavedLegacySettings /t REG_BINARY /d 46000000 /f
goto exit

:exit
echo ����PAC�������
pause