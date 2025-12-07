<#macro layout title="" appName="" appVersion="" basePath="" extraCss="" message="" error="">
    <!DOCTYPE html>
    <html lang="zh-CN">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title><#if title?has_content>${title} - </#if>${appName!}</title>
        <link rel="stylesheet" href="${basePath!}/static/css/style.css">
        <script src="${basePath!}/static/js/jquery.min.js"></script>
        <script src="${basePath!}/static/js/main.js"></script>
        <#if extraCss?has_content>
            <style>
                ${extraCss}
            </style>
        </#if>
    </head>
    <body>
    <div class="container">
        <header class="header">
            <h1>${appName!}</h1>
            <nav class="navbar">
                <a href="${basePath!}/user/">首页</a>
                <a href="${basePath!}/user/list">用户列表</a>
                <a href="${basePath!}/user/add">添加用户</a>
            </nav>
        </header>

        <main class="main-content">
            <#nested>
        </main>

        <footer class="footer">
            <p>© 2023 ${appName!} v${appVersion!}</p>
            <p>当前时间：${.now?string('yyyy-MM-dd HH:mm:ss')}</p>
        </footer>
    </div>

    <script>
        $(function() {
            // 全局消息提示
            <#if message?has_content>
            alert('${message}');
            </#if>

            <#if error?has_content>
            alert('错误：${error}');
            </#if>
        });
    </script>
    </body>
    </html>
</#macro>