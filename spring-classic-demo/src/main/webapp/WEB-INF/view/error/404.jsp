<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>404 页面未找到</title>
</head>
<body>
<h1>404 - 页面未找到</h1>
<hr>
<p><strong>请求的URL:</strong> ${pageContext.request.requestURL}</p>
<p><strong>上下文路径:</strong> ${pageContext.request.contextPath}</p>
<p><strong>Servlet路径:</strong> ${pageContext.request.servletPath}</p>
<p><strong>查询参数:</strong> ${pageContext.request.queryString}</p>
<hr>
<p><strong>可能的原因:</strong></p>
<ul>
    <li>URL拼写错误</li>
    <li>Controller映射不正确</li>
    <li>视图文件不存在</li>
    <li>项目未正确部署</li>
</ul>
<hr>
<p><a href="${pageContext.request.contextPath}/">返回首页</a></p>
</body>
</html>