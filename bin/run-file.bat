@echo off
echo.
echo [信息] 使用Jar命令运行file工程。
echo.

cd %~dp0
cd ../mai-file/target

set JAVA_OPTS=-Xms128m -Xmx128m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m

java -Dfile.encoding=utf-8 %JAVA_OPTS% -jar mai-file.jar

cd bin
pause
