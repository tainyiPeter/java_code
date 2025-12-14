<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>配置诊断页面</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        h1 { color: #333; }
        .section { margin: 20px 0; border: 1px solid #ddd; padding: 15px; }
        .section h2 { margin-top: 0; color: #555; }
        .success { color: green; }
        .error { color: red; }
        table { width: 100%; border-collapse: collapse; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .timestamp { color: #666; font-size: 0.9em; }
    </style>
</head>
<body>
<h1>Spring配置诊断报告</h1>
<div class="timestamp">诊断时间：${timestamp?datetime}</div>

<#list diagnostics?keys as sectionName>
    <div class="section">
        <h2>${sectionName}</h2>
        <table>
            <#list diagnostics[sectionName]?keys as key>
                <tr>
                    <td width="30%"><strong>${key}</strong></td>
                    <td>
                        <#if diagnostics[sectionName][key]?starts_with("✅")>
                            <span class="success">${diagnostics[sectionName][key]}</span>
                        <#elseif diagnostics[sectionName][key]?starts_with("❌")>
                            <span class="error">${diagnostics[sectionName][key]}</span>
                        <#else>
                            ${diagnostics[sectionName][key]}
                        </#if>
                    </td>
                </tr>
            </#list>
        </table>
    </div>
</#list>

<div class="section">
    <h2>快速修复建议</h2>
    <ol>
        <li>确保 <code>application.properties</code> 在 <code>src/main/resources/</code> 目录下</li>
        <li>检查 <code>spring-context.xml</code> 是否有：<br>
            <code>&lt;context:property-placeholder location="classpath:application.properties"/&gt;</code>
        </li>
        <li>检查文件编码是否为 UTF-8（无BOM）</li>
        <li>运行 <code>mvn clean package</code> 重新打包</li>
        <li>重启 Tomcat 服务</li>
    </ol>
</div>
</body>
</html>