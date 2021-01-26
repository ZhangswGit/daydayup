echo off
echo 开始设置PAC代理...

:choice
set choice=""
set /p choice=输入："1"-开启PAC代理, "2"-关闭PAC代理:

if /i "%choice%" == "1" goto open
if /i "%choice%" == "2" goto close
goto choice

:open
echo 开启PAC代理...
reg add "HKCU\Software\Microsoft\Windows\CurrentVersion\Internet Settings" /v AutoConfigURL /t REG_SZ /d $PAC_URL$ /f
goto exit

:close
echo 关闭PAC代理...
reg delete "HKCU\Software\Microsoft\Windows\CurrentVersion\Internet Settings" /v AutoConfigURL /f
reg add "HKCU\Software\Microsoft\Windows\CurrentVersion\Internet Settings\Connections" /v DefaultConnectionSettings /t REG_BINARY /d 46000000 /f
reg add "HKCU\Software\Microsoft\Windows\CurrentVersion\Internet Settings\Connections" /v SavedLegacySettings /t REG_BINARY /d 46000000 /f
goto exit

:exit
echo 设置PAC代理完成
pause