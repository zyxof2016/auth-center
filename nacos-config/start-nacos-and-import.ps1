# Nacos启动和配置导入脚本
# 适用于Windows环境

Write-Host "=== Nacos配置导入工具 ===" -ForegroundColor Green

# 检查Nacos是否正在运行
Write-Host "检查Nacos服务状态..." -ForegroundColor Yellow
$nacosRunning = $false

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8848/nacos/" -TimeoutSec 3 -ErrorAction SilentlyContinue
    if ($response.StatusCode -eq 200) {
        $nacosRunning = $true
        Write-Host "Nacos服务正在运行" -ForegroundColor Green
    }
} catch {
    Write-Host "Nacos服务未运行，将尝试启动..." -ForegroundColor Yellow
}

if (-not $nacosRunning) {
    Write-Host "请先手动启动Nacos服务：" -ForegroundColor Red
    Write-Host "1. 下载Nacos Server: https://github.com/alibaba/nacos/releases" -ForegroundColor Cyan
    Write-Host "2. 解压到本地目录" -ForegroundColor Cyan
    Write-Host "3. 进入bin目录，运行: startup.cmd -m standalone" -ForegroundColor Cyan
    Write-Host "4. 等待Nacos启动完成（约30秒）" -ForegroundColor Cyan
    Write-Host "5. 访问 http://localhost:8848/nacos 验证" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "启动完成后，按任意键继续导入配置..." -ForegroundColor Yellow
    $null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
}

# 导入配置到Nacos
Write-Host "开始导入配置到Nacos..." -ForegroundColor Green

$nacosUrl = "http://localhost:8848"
$username = "nacos"
$password = "nacos"
$namespace = "public"
$group = "DEFAULT_GROUP"

# 函数：导入配置到Nacos
function Import-ConfigToNacos {
    param(
        [string]$DataId,
        [string]$Content,
        [string]$Type = "yaml"
    )
    
    try {
        $body = @{
            dataId = $DataId
            group = $group
            namespaceId = $namespace
            content = $Content
            type = $Type
        }
        
        $uri = "$nacosUrl/nacos/v1/cs/configs"
        $credential = "${username}:${password}"
        $credentialBytes = [System.Text.Encoding]::UTF8.GetBytes($credential)
        $base64Credential = [System.Convert]::ToBase64String($credentialBytes)
        
        $headers = @{
            "Authorization" = "Basic $base64Credential"
            "Content-Type" = "application/x-www-form-urlencoded"
        }
        
        $response = Invoke-RestMethod -Uri $uri -Method Post -Headers $headers -Body $body
        Write-Host "✓ 配置 $DataId 导入成功" -ForegroundColor Green
        return $true
    } catch {
        Write-Host "✗ 配置 $DataId 导入失败: $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
}

# 读取配置文件内容
Write-Host "读取配置文件..." -ForegroundColor Yellow

$configFiles = @(
    @{ DataId = "common-datasource.yml"; File = "common-datasource.yml" },
    @{ DataId = "common-redis.yml"; File = "common-redis.yml" },
    @{ DataId = "auth-gateway.yml"; File = "auth-gateway.yml" },
    @{ DataId = "auth-server.yml"; File = "auth-server.yml" }
)

$successCount = 0
$totalCount = $configFiles.Count

foreach ($config in $configFiles) {
    $filePath = Join-Path $PSScriptRoot $config.File
    if (Test-Path $filePath) {
        $content = Get-Content $filePath -Raw
        if (Import-ConfigToNacos -DataId $config.DataId -Content $content) {
            $successCount++
        }
    } else {
        Write-Host "✗ 配置文件 $($config.File) 不存在" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=== 导入结果 ===" -ForegroundColor Green
Write-Host "成功导入: $successCount/$totalCount" -ForegroundColor $(if ($successCount -eq $totalCount) { "Green" } else { "Yellow" })

if ($successCount -eq $totalCount) {
    Write-Host "所有配置导入成功！" -ForegroundColor Green
    Write-Host ""
    Write-Host "访问Nacos控制台: http://localhost:8848/nacos" -ForegroundColor Cyan
    Write-Host "用户名: $username" -ForegroundColor Cyan
    Write-Host "密码: $password" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "配置导入完成，可以启动微服务了！" -ForegroundColor Green
} else {
    Write-Host "部分配置导入失败，请检查Nacos服务状态" -ForegroundColor Red
}

Write-Host ""
Write-Host "按任意键退出..." -ForegroundColor Yellow
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")