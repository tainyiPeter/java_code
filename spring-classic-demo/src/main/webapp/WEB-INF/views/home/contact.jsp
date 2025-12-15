<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>联系我们</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .contact-box { max-width: 800px; margin: 0 auto; }
        .contact-info { margin: 20px 0; }
        .info-item { margin-bottom: 15px; }
        .label { font-weight: bold; display: inline-block; width: 100px; }
    </style>
</head>
<body>

<div class="contact-box">
    <h1>联系我们</h1>
    <hr>

    <div class="contact-info">
        <div class="info-item">
            <span class="label">项目名称：</span>
            <span>Spring 经典技术栈 Demo</span>
        </div>
        <div class="info-item">
            <span class="label">项目类型：</span>
            <span>教学示例项目</span>
        </div>
        <div class="info-item">
            <span class="label">技术栈：</span>
            <span>Spring 5 + Spring MVC + Spring JDBC + Druid + MySQL</span>
        </div>
        <div class="info-item">
            <span class="label">运行环境：</span>
            <span>Java 8 + Apache Tomcat 9.0</span>
        </div>
    </div>

    <h2>技术交流</h2>
    <p>本项目旨在展示经典 Java Web 技术栈的最佳实践，适用于学习和参考。</p>

    <h2>使用说明</h2>
    <ol>
        <li>确保 MySQL 数据库已启动并创建相应表结构</li>
        <li>修改数据库连接配置（applicationContext.xml）</li>
        <li>部署到 Tomcat 或其他 Servlet 容器</li>
        <li>访问首页查看功能演示</li>
    </ol>

    <h2>注意事项</h2>
    <ul>
        <li>本项目为示例代码，不建议直接用于生产环境</li>
        <li>数据库操作请确保有适当的事务管理</li>
        <li>生产环境建议增加安全防护措施</li>
        <li>定期备份重要数据</li>
    </ul>

    <hr>
    <div style="text-align: center; margin-top: 30px;">
        <p><strong>最后更新：</strong> 2025-12-11</p>
        <a href="${pageContext.request.contextPath}/">返回首页</a> |
        <a href="${pageContext.request.contextPath}/about">关于项目</a>
    </div>
</div>

</body>
</html>