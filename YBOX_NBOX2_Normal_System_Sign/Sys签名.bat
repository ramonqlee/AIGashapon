
@echo off
echo ǩ����ʼ
for /f "tokens=* delims=" %%i in ('dir /a-d /b *.apk') do (
    echo %%i ����ǩ��
    java -jar signapk.jar  platform.x509.pem platform.pk8 %%i sys%%i
    echo %%i ǩ�����
    del %%i
)
echo ǩ�����
pause


