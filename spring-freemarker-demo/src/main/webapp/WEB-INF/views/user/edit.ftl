<#import "../layout.ftl" as layout>
<@layout.layout title="<#if user.id??>编辑<#else>添加</#if>用户">
    <div class="user-edit">
        <h2><#if user.id??>编辑用户<#else>添加用户</#if></h2>

        <form action="${basePath!}/user/save" method="post" class="edit-form">
            <#if user.id??>
                <input type="hidden" name="id" value="${user.id}">
            </#if>

            <div class="form-group">
                <label for="username">用户名：</label>
                <input type="text" id="username" name="username"
                       value="${user.username!}" required
                       placeholder="请输入用户名" class="form-control">
            </div>

            <div class="form-group">
                <label for="email">邮箱：</label>
                <input type="email" id="email" name="email"
                       value="${user.email!}" required
                       placeholder="请输入邮箱" class="form-control">
            </div>

            <div class="form-group">
                <label for="age">年龄：</label>
                <input type="number" id="age" name="age"
                       value="${user.age!}" min="0" max="150"
                       placeholder="请输入年龄" class="form-control">
            </div>

            <div class="form-group">
                <label for="gender">性别：</label>
                <select id="gender" name="gender" class="form-control">
                    <option value="">请选择性别</option>
                    <option value="男" <#if user.gender?? && user.gender == '男'>selected</#if>>男</option>
                    <option value="女" <#if user.gender?? && user.gender == '女'>selected</#if>>女</option>
                    <option value="其他" <#if user.gender?? && user.gender == '其他'>selected</#if>>其他</option>
                </select>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">保存</button>
                <a href="${basePath!}/user/list" class="btn btn-secondary">取消</a>
            </div>
        </form>
    </div>
</@layout.layout>