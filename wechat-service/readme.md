1. 运行项目
# 编译打包
mvn clean package

# 运行
java -jar target/wechat-service-1.0.0.jar

# 或者使用Maven直接运行
mvn spring-boot:run

2. 使用花生壳进行本地测试


# 复制生成的https地址，如：https://https://91qj1470uc04.vicp.fun/

3. 配置微信测试号
1.登录微信测试号平台
2.配置接口信息：
- URL: https://https://91qj1470uc04.vicp.fun/webchat
- Token: abcd123456
3.配置JS接口安全域名：
91qj1470uc04.vicp.fun
4.配置网页授权域名：
91qj1470uc04.vicp.fun