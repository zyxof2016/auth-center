Write-Host "开始导入配置到Nacos..." -ForegroundColor Green

$nacosUrl = "http://localhost:8848"
$username = "nacos"
$password = "nacos"

# 检查Nacos是否运行
try {
    $response = Invoke-WebRequest -Uri "$nacosUrl/nacos/" -TimeoutSec 5
    Write-Host "Nacos服务正在运行" -ForegroundColor Green
} catch {
    Write-Host "Nacos服务未运行，请先启动Nacos服务" -ForegroundColor Red
    Write-Host "启动命令: startup.cmd -m standalone (在Nacos的bin目录下)" -ForegroundColor Yellow
    exit
}

# 导入配置函数
function Import-Config($DataId, $File) {
    $filePath = Join-Path $PSScriptRoot $File
    if (-not (Test-Path $filePath)) {
        Write-Host "文件 $File 不存在" -ForegroundColor Red
        return $false
    }
    
    $content = Get-Content $filePath -Raw
    
    $body = @{
        dataId = $DataId
        group = "DEFAULT_GROUP"
        namespaceId = "public"
        content = $content
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

# 导入配置
$configs = @(
    @{Id="common-datasource.yml"; File="common-datasource.yml"},
    @{Id="common-redis.yml"; File="common-redis.yml"},
    @{Id="auth-gateway.yml"; File="auth-gateway.yml"},
    @{Id="auth-server.yml"; File="auth-server.yml"}
)

$successCount = 0
foreach ($config in $configs) {
    if (Import-Config -DataId $config.Id -File $config.File) {
        $successCount++
    }
}

Write-Host ""
Write-Host "导入完成: $successCount/$($configs.Count) 个配置成功" -ForegroundColor $(if ($successCount -eq $configs.Count) { "Green" } else { "Yellow" })
Write-Host "Nacos控制台: $nacosUrl/nacos" -ForegroundColor Cyan
Write-Host "用户名: $username" -ForegroundColor Cyan
Write-Host "密码: $password" -ForegroundColor Cyan