@echo off
echo.
echo [信息] 使用Jar命令运行post工程。
echo.

cd %~dp0
cd ../mai-post/target

set JAVA_OPTS=-Xms128m -Xmx256m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m

java -Dfile.encoding=utf-8 %JAVA_OPTS% -jar mai-post.jar

cd bin
pause
