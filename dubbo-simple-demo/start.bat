@echo off 

echo === 启动 Dubbo 服务 ===
java -cp "target\classes;target\lib\*" ^
     -Ddubbo.spring.config=classpath*:META-INF/spring/*.xml ^
     com.alibaba.dubbo.container.Main ^
     spring
	 
pause