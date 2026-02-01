方式一：使用Maven命令运行
bash
# 1. 克隆或创建项目
mkdir spring-qrcode-demo
cd spring-qrcode-demo

# 2. 将上述文件放入对应目录
# 3. 编译并运行
mvn clean spring-boot:run
方式二：打包后运行
bash
# 1. 打包
mvn clean package

# 2. 运行
java -jar target/spring-qrcode-demo-1.0.0.jar

# 3. 或指定端口运行
java -jar target/spring-qrcode-demo-1.0.0.jar --server.port=9090
方式三：使用Docker运行
dockerfile
FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/spring-qrcode-demo-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
7. 访问应用
启动后访问以下地址：

首页：http://localhost:8080 或 http://127.0.0.1:8080

生成页面：http://localhost:8080/generate?url=https://www.baidu.com

二维码图片：http://localhost:8080/qrcode/image?content=https://github.com

Base64数据：http://localhost:8080/qrcode/base64?content=https://www.sohu.com

健康检查：http://localhost:8080/health

8. 项目特点
✅ Spring Boot 2.7.18 - 现代化的Java框架
✅ Thymeleaf模板引擎 - 优雅的前端页面
✅ ZXing二维码库 - 强大的二维码生成能力
✅ 响应式设计 - 适配各种屏幕尺寸
✅ RESTful API - 提供多种调用方式
✅ 美观的UI界面 - 现代化的用户界面
✅ 错误处理 - 完善的异常处理机制
✅ 一键部署 - 支持多种部署方式

9. 扩展功能
你可以根据需要添加以下功能：

二维码美化：添加logo、颜色、边框等

批量生成：支持一次生成多个二维码

二维码解析：上传二维码图片解析内容

访问统计：记录二维码扫描次数

短链接生成：将长URL转为短链接再生成二维码

微信JSSDK集成：在微信内更好的体验

这个Spring Boot实现相比之前的Servlet版本更加现代化，代码结构更清晰，功能更完善，用户体验也更好！