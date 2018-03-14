
@echo off
echo 签名开始
for /f "tokens=* delims=" %%i in ('dir /a-d /b *.apk') do (
    echo %%i 正在签名
    java -jar signapk.jar  platform.x509.pem platform.pk8 %%i sys%%i
    echo %%i 签名完成
    del %%i
)
echo 签名完成
pause


