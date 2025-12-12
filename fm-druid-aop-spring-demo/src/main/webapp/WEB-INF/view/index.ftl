<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>用户管理系统 - 首页</title>
    <link rel="stylesheet" href="/static/css/style.css">
</head>
<body>
<div class="container">
    <header>
        <h1>用户管理系统</h1>
        <nav>
            <a href="/user/list">用户列表</a>
            <a href="/user/add">添加用户</a>
        </nav>
    </header>

    <main>
        <div class="welcome">
            <h2>${message}</h2>
            <p>当前时间: ${timestamp?datetime}</p>
            <p>技术栈: Spring + FreeMarker + Druid + MySQL + AOP</p>

            <div class="features">
                <h3>系统特性：</h3>
                <ul>
                    <li>基于经典Spring框架（非Spring Boot）</li>
                    <li>使用FreeMarker模板引擎</li>
                    <li>Druid连接池管理数据库连接</li>
                    <li>AOP实现事务管理和日志记录</li>
                    <li>Logback日志框架</li>
                </ul>
            </div>
        </div>
    </main>

    <footer>
        <p>&copy; 2023 用户管理系统 Demo</p>
    </footer>
</div>
</body>
</html>