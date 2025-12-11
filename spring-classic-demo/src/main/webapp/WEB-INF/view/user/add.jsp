<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>添加用户</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .form-group { margin-bottom: 15px; }
        label { display: inline-block; width: 100px; }
        input[type="text"], input[type="number"] {
            padding: 5px;
            width: 250px;
            border: 1px solid #ddd;
            border-radius: 3px;
        }
        .btn {
            padding: 8px 15px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 3px;
            cursor: pointer;
        }
        .btn:hover { background-color: #45a049; }
        .error { color: red; font-size: 12px; }
    </style>
</head>
<body>

<h1>添加用户</h1>
<hr>

<form action="${pageContext.request.contextPath}/user/save" method="post">

    <div class="form-group">
        <label for="username">用户名:</label>
        <input type="text" id="username" name="username" required
               placeholder="请输入用户名" maxlength="50">
        <span class="error">${errors.username}</span>
    </div>

    <div class="form-group">
        <label for="email">邮箱:</label>
        <input type="text" id="email" name="email" required
               placeholder="请输入邮箱地址" maxlength="100">
        <span class="error">${errors.email}</span>
    </div>

    <div class="form-group">
        <label for="age">年龄:</label>
        <input type="number" id="age" name="age" min="1" max="150"
               placeholder="请输入年龄" value="18">
        <span class="error">${errors.age}</span>
    </div>

    <div class="form-group">
        <input type="submit" value="保存" class="btn">
        <input type="reset" value="重置" class="btn" style="background-color: #f44336;">
        <a href="${pageContext.request.contextPath}/user/list" style="margin-left: 20px;">返回列表</a>
    </div>

</form>

<hr>
<div>
    <p><strong>说明：</strong></p>
    <ul>
        <li>用户名和邮箱为必填项</li>
        <li>年龄范围为1-150岁</li>
        <li>创建时间和更新时间由系统自动生成</li>
    </ul>
</div>

</body>
</html>