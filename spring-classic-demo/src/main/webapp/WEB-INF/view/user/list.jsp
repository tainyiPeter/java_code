<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>用户管理</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        table { border-collapse: collapse; width: 100%; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .success { color: green; }
        .error { color: red; }
        .actions a { margin-right: 10px; }
    </style>
</head>
<body>

<h1>用户管理</h1>
<hr>

<div style="margin: 20px 0;">
    <a href="${pageContext.request.contextPath}/user/add">添加用户</a> |
    <a href="${pageContext.request.contextPath}/">返回首页</a>
</div>

<c:if test="${not empty message}">
    <div class="${success ? 'success' : 'error'}">
        <strong>${message}</strong>
    </div>
</c:if>

<p>当前共有 <strong>${userCount}</strong> 位用户</p>

<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>用户名</th>
        <th>邮箱</th>
        <th>年龄</th>
        <th>创建时间</th>
        <th>更新时间</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <c:choose>
        <c:when test="${not empty users}">
            <c:forEach var="user" items="${users}">
                <tr>
                    <td>${user.id}</td>
                    <td>${user.username}</td>
                    <td>${user.email}</td>
                    <td>${user.age}</td>
                    <td>
                        <fmt:formatDate value="${user.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                    </td>
                    <td>
                        <fmt:formatDate value="${user.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                    </td>
                    <td class="actions">
                        <a href="${pageContext.request.contextPath}/user/detail/${user.id}">查看</a>
                        <a href="${pageContext.request.contextPath}/user/delete/${user.id}"
                           onclick="return confirm('确定要删除用户 ${user.username} 吗？')">删除</a>
                    </td>
                </tr>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <tr>
                <td colspan="7" style="text-align: center;">暂无用户数据</td>
            </tr>
        </c:otherwise>
    </c:choose>
    </tbody>
</table>

<hr>
<div>
    <p>
        <a href="${pageContext.request.contextPath}/user/api/list" target="_blank">查看JSON数据</a> |
        <a href="${pageContext.request.contextPath}/user/test" target="_blank">测试连接</a>
    </p>
    <p>系统时间: <fmt:formatDate value="<%= new java.util.Date() %>" pattern="yyyy-MM-dd HH:mm:ss"/></p>
</div>

</body>
</html>