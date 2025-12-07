<#import "../layout.ftl" as layout>
<@layout.layout title="用户列表">
    <div class="user-list">
        <h2>用户列表</h2>

        <!-- 搜索框 -->
        <form action="${basePath!}/user/list" method="get" class="search-form">
            <input type="text" name="keyword" placeholder="输入用户名或邮箱搜索"
                   value="${keyword!}" class="search-input">
            <button type="submit" class="btn btn-primary">搜索</button>
            <#if keyword??>
                <a href="${basePath!}/user/list" class="btn btn-secondary">清除</a>
            </#if>
        </form>

        <#if keyword?? && searchCount??>
            <p class="search-info">找到 ${searchCount} 条相关记录</p>
        </#if>

        <!-- 用户列表表格 -->
        <table class="user-table">
            <thead>
            <tr>
                <th>ID</th>
                <th>用户名</th>
                <th>邮箱</th>
                <th>年龄</th>
                <th>性别</th>
                <th>注册时间</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <#if users?? && users?size gt 0>
                <#list users as user>
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.username}</td>
                        <td>${user.email}</td>
                        <td>${user.age}</td>
                        <td>${user.gender}</td>
                        <td>${user.createTime?string('yyyy-MM-dd HH:mm')}</td>
                        <td class="actions">
                            <a href="${basePath!}/user/detail/${user.id}" class="btn btn-info btn-sm">查看</a>
                            <a href="${basePath!}/user/edit/${user.id}" class="btn btn-warning btn-sm">编辑</a>
                            <a href="${basePath!}/user/delete/${user.id}"
                               class="btn btn-danger btn-sm"
                               onclick="return confirm('确定要删除用户 ${user.username} 吗？')">删除</a>
                        </td>
                    </tr>
                </#list>
            <#else>
                <tr>
                    <td colspan="7" class="no-data">暂无用户数据</td>
                </tr>
            </#if>
            </tbody>
        </table>

        <div class="table-footer">
            <p>共 ${userCount!0} 个用户</p>
            <a href="${basePath!}/user/add" class="btn btn-success">添加新用户</a>
        </div>
    </div>
</@layout.layout>