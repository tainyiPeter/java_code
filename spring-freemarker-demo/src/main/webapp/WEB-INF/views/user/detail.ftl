<#import "../layout.ftl" as layout>
<@layout.layout title="用户详情">
    <div class="user-detail">
        <h2>用户详情</h2>

        <div class="detail-card">
            <div class="detail-row">
                <span class="label">用户ID：</span>
                <span class="value">${user.id}</span>
            </div>
            <div class="detail-row">
                <span class="label">用户名：</span>
                <span class="value">${user.username}</span>
            </div>
            <div class="detail-row">
                <span class="label">邮箱：</span>
                <span class="value">${user.email}</span>
            </div>
            <div class="detail-row">
                <span class="label">年龄：</span>
                <span class="value">${user.age}</span>
            </div>
            <div class="detail-row">
                <span class="label">性别：</span>
                <span class="value">${user.gender}</span>
            </div>
            <div class="detail-row">
                <span class="label">注册时间：</span>
                <span class="value">${user.createTime?string('yyyy-MM-dd HH:mm:ss')}</span>
            </div>
        </div>

        <div class="action-buttons">
            <a href="${basePath!}/user/edit/${user.id}" class="btn btn-warning">编辑</a>
            <a href="${basePath!}/user/list" class="btn btn-primary">返回列表</a>
        </div>
    </div>
</@layout.layout>