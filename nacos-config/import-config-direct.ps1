# 直接导入配置到Nacos
# 适用于已运行的Nacos服务

Write-Host "=== Nacos配置直接导入工具 ===" -ForegroundColor Green
Write-Host "开始导入配置到Nacos..." -ForegroundColor Yellow

$nacosUrl = "http://localhost:8848"
$username = "nacos"
$password = "nacos"
$namespace = "public"
$group = "DEFAULT_GROUP"

# 检查Nacos是否运行
try {
    $response = Invoke-WebRequest -Uri "$nacosUrl/nacos/" -TimeoutSec 5
    Write-Host "✓ Nacos服务正在运行" -ForegroundColor Green
} catch {
    Write-Host "✗ Nacos服务未运行，请先启动Nacos服务" -ForegroundColor Red
    Write-Host "启动命令: startup.cmd -m standalone (在Nacos的bin目录下)" -ForegroundColor Yellow
    exit 1
}

# 导入配置函数
function Import-Nacos-Config {
    param($DataId, $Content)
    
    $body = @{
        dataId = $DataId
        group = $group
        namespaceId = $namespace
        content = $Content
        type = "yaml"
    }
    
    $uri = "$nacosUrl/nacos/v1/cs/configs"
    $credBytes = [System.Text.Encoding]::UTF8.GetBytes("${username}:${password}")
    $base64Cred = [System.Convert]::ToBase64String($credBytes)
    
    $headers = @{
        "Authorization" = "Basic $base64Cred"
        "Content-Type" = "application/x-www-form-urlencoded"
    }
    
    try {
        $result = Invoke-RestMethod -Uri $uri -Method Post -Headers $headers -Body $body
        Write-Host "✓ $DataId 导入成功" -ForegroundColor Green
        return $true
    } catch {
        Write-Host "✗ $DataId 导入失败: $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
}

# 导入所有配置
$configs = @(
    @{Id="common-datasource.yml"; File="common-datasource.yml"},
    @{Id="common-redis.yml"; File="common-redis.yml"},
    @{Id="auth-gateway.yml"; File="auth-gateway.yml"},
    @{Id="auth-server.yml"; File="auth-server.yml"}
)

$successCount = 0

foreach ($config in $configs) {
    $filePath = Join-Path $PSScriptRoot $config.File
    if (Test-Path $filePath) {
        $content = Get-Content $filePath -Raw -Encoding UTF8
        if (Import-Nacos-Config -DataId $config.Id -Content $content) {
            $successCount++
        }
    } else {
        Write-Host "✗ 文件 $($config.File) 不存在" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=== 导入完成 ===" -ForegroundColor Green
Write-Host "成功导入: $successCount/$($configs.Count) 个配置" -ForegroundColor $(if ($successCount -eq $configs.Count) { "Green" } else { "Yellow" })
Write-Host ""
Write-Host "Nacos控制台: $nacosUrl/nacos" -ForegroundColor Cyan
Write-Host "用户名: $username" -ForegroundColor Cyan
Write-Host "密码: $password" -ForegroundColor Cyan

if ($successCount -eq $configs.Count) {
    Write-Host "✓ 所有配置导入成功！可以启动微服务了。" -ForegroundColor Green
} else {
    Write-Host "⚠ 部分配置导入失败，请检查Nacos服务状态。" -ForegroundColor Yellow
}