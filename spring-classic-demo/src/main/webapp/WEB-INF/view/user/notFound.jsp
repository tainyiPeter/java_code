<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>用户不存在</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; text-align: center; }
        .error-box {
            border: 2px solid #f44336;
            padding: 30px;
            margin: 50px auto;
            width: 500px;
            border-radius: 10px;
            background-color: #ffebee;
        }
        .error-icon {
            font-size: 60px;
            color: #f44336;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>

<div class="error-box">
    <div class="error-icon">⚠️</div>
    <h1>用户不存在</h1>
    <p style="font-size: 18px; margin: 20px 0;">${message}</p>
    <p>可能的原因：</p>
    <ul style="text-align: left; display: inline-block; margin: 20px 0;">
        <li>用户ID不正确</li>
        <li>用户已被删除</li>
        <li>数据库连接异常</li>
    </ul>

    <div style="margin-top: 30px;">
        <a href="${pageContext.request.contextPath}/user/list"
           style="padding: 10px 20px; background-color: #2196F3; color: white; text-decoration: none; border-radius: 5px;">
            返回用户列表
        </a>
    </div>
</div>

</body>
</html>