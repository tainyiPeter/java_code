<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>用户列表</title>
    <link rel="stylesheet" href="/static/css/style.css">
</head>
<body>
<div class="container">
    <header>
        <h1>用户列表</h1>
        <nav>
            <a href="/user/">首页</a>
            <a href="/user/add">添加用户</a>
        </nav>
    </header>

    <main>
        <div class="user-count">
            <p>总用户数: <strong>${count}</strong></p>
        </div>

        <#if users?? && users?size gt 0>
            <table class="user-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>用户名</th>
                    <th>邮箱</th>
                    <th>创建时间</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody>
                <#list users as user>
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.username}</td>
                        <td>${user.email}</td>
                        <td>${user.createTime?datetime}</td>
                        <td>
                            <a href="/user/detail/${user.id}" class="btn btn-view">查看</a>
                        </td>
                    </tr>
                </#list>
                </tbody>
            </table>
        <#else>
            <div class="no-data">
                <p>暂无用户数据，请先添加用户。</p>
            </div>
        </#if>
    </main>

    <footer>
        <p>&copy; 2023 用户管理系统 Demo</p>
    </footer>
</div>
</body>
</html>