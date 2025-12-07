<#import "layout.ftl" as layout>
<@layout.layout title="首页">
    <div class="welcome">
        <h2>欢迎使用 ${appName!}</h2>
        <div class="stats">
            <div class="stat-item">
                <span class="stat-label">总用户数</span>
                <span class="stat-value">${userCount!0}</span>
            </div>
        </div>

        <div class="quick-actions">
            <a href="${basePath!}/user/list" class="btn btn-primary">查看用户列表</a>
            <a href="${basePath!}/user/add" class="btn btn-success">添加新用户</a>
        </div>

        <div class="features">
            <h3>系统功能</h3>
            <ul>
                <li>用户信息管理</li>
                <li>用户搜索功能</li>
                <li>响应式设计</li>
                <li>FreeMarker模板引擎</li>
            </ul>
        </div>
    </div>
</@layout.layout>