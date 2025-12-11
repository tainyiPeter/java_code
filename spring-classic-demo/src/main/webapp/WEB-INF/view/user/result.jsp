<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>操作结果</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; text-align: center; }
        .result-box {
            border: 2px solid #ddd;
            padding: 30px;
            margin: 50px auto;
            width: 500px;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        .success { color: #4CAF50; font-size: 24px; }
        .error { color: #f44336; font-size: 24px; }
        .message { margin: 20px 0; font-size: 18px; }
        .actions { margin-top: 30px; }
        .btn {
            display: inline-block;
            padding: 10px 20px;
            margin: 0 10px;
            text-decoration: none;
            border-radius: 5px;
        }
        .btn-primary { background-color: #4CAF50; color: white; }
        .btn-secondary { background-color: #2196F3; color: white; }
    </style>
</head>
<body>

<div class="result-box">
    <c:choose>
        <c:when test="${success}">
            <div class="success">✓ 操作成功</div>
        </c:when>
        <c:otherwise>
            <div class="error">✗ 操作失败</div>
        </c:otherwise>
    </c:choose>

    <div class="message">
        ${message}
    </div>

    <div class="actions">
        <a href="${pageContext.request.contextPath}/user/list" class="btn btn-primary">查看用户列表</a>
        <a href="${pageContext.request.contextPath}/user/add" class="btn btn-secondary">继续添加用户</a>
    </div>

    <div style="margin-top: 20px; font-size: 14px; color: #666;">
        <p>页面将在 <span id="countdown">5</span> 秒后自动跳转到用户列表...</p>
    </div>
</div>

<script>
    // 倒计时跳转
    var seconds = 5;
    var countdown = document.getElementById('countdown');

    function updateCountdown() {
        countdown.textContent = seconds;
        seconds--;

        if (seconds < 0) {
            window.location.href = "${pageContext.request.contextPath}/user/list";
        } else {
            setTimeout(updateCountdown, 1000);
        }
    }

    // 5秒后自动跳转
    setTimeout(function() {
        window.location.href = "${pageContext.request.contextPath}/user/list";
    }, 5000);

    // 启动倒计时显示
    updateCountdown();
</script>

</body>
</html>