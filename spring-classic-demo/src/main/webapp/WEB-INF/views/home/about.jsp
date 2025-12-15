<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>关于</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .about-box { max-width: 800px; margin: 0 auto; }
        .tech-list { list-style-type: none; padding: 0; }
        .tech-list li { padding: 8px 0; border-bottom: 1px solid #eee; }
    </style>
</head>
<body>

<div class="about-box">
    <h1>关于 Spring 经典技术栈 Demo</h1>
    <hr>

    <h2>项目简介</h2>
    <p>这是一个基于经典 Java Web 技术栈的示例项目，展示了 Spring 框架在企业级应用开发中的应用。</p>

    <h2>技术栈</h2>
    <ul class="tech-list">
        <li><strong>Spring Framework 5.3.23</strong> - 核心框架</li>
        <li><strong>Spring MVC</strong> - Web 层框架</li>
        <li><strong>Spring JDBC</strong> - 数据访问层</li>
        <li><strong>Druid 1.2.8</strong> - 数据库连接池</li>
        <li><strong>MySQL 8.0</strong> - 数据库</li>
        <li><strong>JSP + JSTL</strong> - 视图层技术</li>
        <li><strong>Logback 1.2.11</strong> - 日志框架</li>
        <li><strong>Apache Tomcat 9.0</strong> - Web 服务器</li>
    </ul>

    <h2>功能特点</h2>
    <ul>
        <li>完整的 MVC 分层架构</li>
        <li>数据库连接池监控</li>
        <li>事务管理</li>
        <li>统一的异常处理</li>
        <li>RESTful API 支持</li>
        <li>响应式界面设计</li>
    </ul>

    <h2>数据库设计</h2>
    <pre>
CREATE TABLE t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    age INT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);</pre>

    <hr>
    <div style="text-align: center; margin-top: 30px;">
        <a href="${pageContext.request.contextPath}/">返回首页</a> |
        <a href="${pageContext.request.contextPath}/user/list">用户管理</a>
    </div>
</div>

</body>
</html>