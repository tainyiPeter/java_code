<#import "layout.ftl" as layout>
<@layout.layout title="错误页面">
    <div class="error-page">
        <div class="error-icon">
            <span>❌</span>
        </div>
        <h2>系统错误</h2>
        <div class="error-message">
            <p>${error!}</p>
        </div>
        <div class="error-actions">
            <a href="javascript:history.back()" class="btn btn-primary">返回上一页</a>
            <a href="${basePath!}/user/" class="btn btn-secondary">返回首页</a>
        </div>
    </div>
</@layout.layout>