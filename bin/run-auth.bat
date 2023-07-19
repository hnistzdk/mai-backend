@echo off
echo.
echo [信息] 使用Jar命令运行auth工程。
echo.

cd %~dp0
cd ../mai-auth/target

set JAVA_OPTS=-Xms128m -Xmx256m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m

java -Dfile.encoding=utf-8 %JAVA_OPTS% -jar mai-auth.jar

cd bin
pause
