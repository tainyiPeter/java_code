<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>批量操作</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .batch-section { margin: 20px 0; padding: 20px; border: 1px solid #ddd; border-radius: 5px; }
        .btn { padding: 8px 15px; margin-right: 10px; cursor: pointer; }
    </style>
</head>
<body>

<h1>批量操作</h1>
<hr>

<div class="batch-section">
    <h3>批量操作功能（开发中）</h3>
    <p>此页面用于演示批量操作功能，实际功能需要根据业务需求开发。</p>

    <div style="margin: 20px 0;">
        <button class="btn" style="background-color: #4CAF50; color: white;">批量导入</button>
        <button class="btn" style="background-color: #2196F3; color: white;">批量导出</button>
        <button class="btn" style="background-color: #ff9800; color: white;">批量更新</button>
        <button class="btn" style="background-color: #f44336; color: white;">批量删除</button>
    </div>

    <p><strong>提示：</strong> 批量操作需要谨慎使用，建议先备份数据。</p>
</div>

<hr>
<div>
    <a href="${pageContext.request.contextPath}/user/list">返回用户列表</a> |
    <a href="${pageContext.request.contextPath}/">返回首页</a>
</div>

</body>
</html>