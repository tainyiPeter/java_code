@echo off
chcp 65001 >nul


call mvn exec:java -q -Dexec.mainClass="com.example.demo.TransactionDemoApplication" -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8

echo.
echo ========================================
echo finish
echo ========================================
pause