@echo off 

echo "=== Start Dubbo Services ==="
java -cp "target\classes;target\lib\*" ^
	-Ddubbo.spring.config=classpath*:META-INF/spring/*.xml ^
     com.alibaba.dubbo.container.Main ^
	 > km.txt
pause