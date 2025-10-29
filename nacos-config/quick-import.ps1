# 快速导入配置到Nacos

Write-Host "=== Nacos配置导入 ===" -ForegroundColor Green

# Nacos配置
$nacosUrl = "http://localhost:8848"
$username = "nacos"
$password = "nacos"

# 检查Nacos是否运行
Write-Host "检查Nacos服务状态..." -ForegroundColor Yellow

try {
    $response = Invoke-WebRequest -Uri "$nacosUrl/nacos/" -TimeoutSec 5
    Write-Host "✓ Nacos服务正在运行" -ForegroundColor Green
} catch {
    Write-Host "✗ Nacos服务未运行" -ForegroundColor Red
    Write-Host "请先启动Nacos服务:" -ForegroundColor Yellow
    Write-Host "1. 下载Nacos Server: https://github.com/alibaba/nacos/releases" -ForegroundColor Cyan
    Write-Host "2. 解压到本地目录" -ForegroundColor Cyan
    Write-Host "3. 进入bin目录，运行: startup.cmd -m standalone" -ForegroundColor Cyan
    Write-Host "4. 等待Nacos启动完成（约30秒）" -ForegroundColor Cyan
    Write-Host "5. 访问 http://localhost:8848/nacos 验证" -ForegroundColor Cyan
    exit
}

# 配置列表
$configs = @(
    @{Id="common-datasource.yml"; File="common-datasource.yml"},
    @{Id="common-redis.yml"; File="common-redis.yml"},
    @{Id="auth-gateway.yml"; File="auth-gateway.yml"},
    @{Id="auth-server.yml"; File="auth-server.yml"}
)

# 导入配置
Write-Host "开始导入配置..." -ForegroundColor Yellow

$successCount = 0

foreach ($config in $configs) {
    $filePath = Join-Path $PSScriptRoot $config.File
    
    if (-not (Test-Path $filePath)) {
        Write-Host "✗ 文件 $($config.File) 不存在" -ForegroundColor Red
        continue
    }
    
    $content = Get-Content $filePath -Raw
    
    # 准备请求参数
    $body = @{
        dataId = $config.Id
        group = "DEFAULT_GROUP"
        namespaceId = "public"
        content = $content
        type = "yaml"
    }
    
    $uri = "$nacosUrl/nacos/v1/cs/configs"
    
    # 基本认证
    $credBytes = [System.Text.Encoding]::UTF8.GetBytes("${username}:${password}")
    $base64Cred = [System.Convert]::ToBase64String($credBytes)
    
    $headers = @{
        "Authorization" = "Basic $base64Cred"
        "Content-Type" = "application/x-www-form-urlencoded"
    }
    
    try {
        $result = Invoke-RestMethod -Uri $uri -Method Post -Headers $headers -Body $body
        Write-Host "✓ $($config.Id) 导入成功" -ForegroundColor Green
        $successCount++
    } catch {
        Write-Host "✗ $($config.Id) 导入失败: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# 显示结果
Write-Host ""
Write-Host "=== 导入结果 ===" -ForegroundColor Green
Write-Host "成功导入: $successCount/$($configs.Count) 个配置" -ForegroundColor $(if ($successCount -eq $configs.Count) { "Green" } else { "Yellow" })

if ($successCount -eq $configs.Count) {
    Write-Host "✓ 所有配置导入成功！" -ForegroundColor Green
    Write-Host ""
    Write-Host "访问Nacos控制台: $nacosUrl/nacos" -ForegroundColor Cyan
    Write-Host "用户名: $username" -ForegroundColor Cyan
    Write-Host "密码: $password" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "现在可以启动微服务了！" -ForegroundColor Green
} else {
    Write-Host "⚠ 部分配置导入失败，请检查Nacos服务状态。" -ForegroundColor Yellow
}