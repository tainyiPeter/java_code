<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
  <title>用户详情</title>
  <style>
    body { font-family: Arial, sans-serif; margin: 20px; }
    .user-info { margin: 20px 0; }
    .info-row { margin-bottom: 10px; }
    .label { font-weight: bold; display: inline-block; width: 120px; }
    .value { color: #333; }
    .actions { margin-top: 20px; }
  </style>
</head>
<body>

<h1>用户详情</h1>
<hr>

<div class="user-info">
  <div class="info-row">
    <span class="label">用户ID:</span>
    <span class="value">${user.id}</span>
  </div>
  <div class="info-row">
    <span class="label">用户名:</span>
    <span class="value">${user.username}</span>
  </div>
  <div class="info-row">
    <span class="label">邮箱:</span>
    <span class="value">${user.email}</span>
  </div>
  <div class="info-row">
    <span class="label">年龄:</span>
    <span class="value">${user.age}</span>
  </div>
  <div class="info-row">
    <span class="label">创建时间:</span>
    <span class="value">
            <fmt:formatDate value="${user.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
        </span>
  </div>
  <div class="info-row">
    <span class="label">更新时间:</span>
    <span class="value">
            <fmt:formatDate value="${user.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
        </span>
  </div>
</div>

<div class="actions">
  <a href="${pageContext.request.contextPath}/user/list">返回列表</a> |
  <a href="${pageContext.request.contextPath}/user/delete/${user.id}"
     onclick="return confirm('确定要删除用户 ${user.username} 吗？')">删除用户</a> |
  <a href="${pageContext.request.contextPath}/">返回首页</a>
</div>

<hr>
<p><strong>数据库操作记录：</strong></p>
<ul>
  <li>本页面数据来自 t_user 表</li>
  <li>查询SQL: SELECT id, username, email, age, create_time, update_time FROM t_user WHERE id=?</li>
  <li>数据更新时间: <fmt:formatDate value="<%= new java.util.Date() %>" pattern="HH:mm:ss"/></li>
</ul>

</body>
</html>