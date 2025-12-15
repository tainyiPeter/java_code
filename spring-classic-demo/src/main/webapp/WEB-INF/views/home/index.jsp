<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Spring经典技术栈Demo</title>
</head>
<body>

<h1>Spring经典技术栈Demo</h1>
<hr>

<h2>欢迎使用Spring经典技术栈示例</h2>

<p>这是一个基于以下技术的Web应用示例：</p>
<ul>
    <li>Spring Framework 5.3.23</li>
    <li>Spring MVC</li>
    <li>Druid数据库连接池</li>
    <li>MySQL数据库</li>
    <li>Spring AOP事务管理</li>
    <li>Logback日志框架</li>
</ul>

<hr>

<h3>系统信息</h3>
<table border="1" cellpadding="5">
    <tr>
        <td>应用上下文</td>
        <td>${contextPath}</td>
    </tr>
    <tr>
        <td>服务器信息</td>
        <td>${serverInfo}</td>
    </tr>
    <tr>
        <td>Java版本</td>
        <td>${javaVersion}</td>
    </tr>
    <tr>
        <td>当前时间</td>
        <td><%= new java.util.Date() %></td>
    </tr>
</table>

<hr>

<h3>功能导航</h3>
<ul>
    <li><a href="${pageContext.request.contextPath}/user/list">用户管理</a></li>
    <li><a href="${pageContext.request.contextPath}/user/add">添加用户</a></li>
    <li><a href="${pageContext.request.contextPath}/user/test">测试连接</a></li>
    <li><a href="${pageContext.request.contextPath}/druid" target="_blank">Druid监控</a></li>
</ul>

<hr>

<h3>API测试</h3>
<ul>
    <li><a href="${pageContext.request.contextPath}/user/api/list">获取用户列表(JSON)</a></li>
</ul>

</body>
</html>