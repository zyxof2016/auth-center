# 读取配置文件内容并导入到Nacos
$nacosUrl = "http://localhost:8848"
$username = "nacos"
$password = "nacos"

# 读取配置文件内容
$datasourceContent = Get-Content "F:\AIProjects\auth-center4\nacos-config\common-datasource.yml" -Raw
$redisContent = Get-Content "F:\AIProjects\auth-center4\nacos-config\common-redis.yml" -Raw

# 导入数据源配置
Write-Host "导入 common-datasource.yml..." -ForegroundColor Yellow
$datasourceParams = @{
    Uri = "$nacosUrl/nacos/v1/cs/configs"
    Method = "POST"
    Headers = @{
        "Content-Type" = "application/x-www-form-urlencoded"
    }
    Body = @{
        dataId = "common-datasource.yml"
        group = "DEFAULT_GROUP"
        namespaceId = "public"
        content = $datasourceContent
        type = "yaml"
    }
    Credential = New-Object System.Management.Automation.PSCredential($username, (ConvertTo-SecureString $password -AsPlainText -Force))
}

try {
    $result = Invoke-RestMethod @datasourceParams
    if ($result -eq "true") {
        Write-Host "✓ common-datasource.yml 导入成功" -ForegroundColor Green
    } else {
        Write-Host "✗ common-datasource.yml 导入失败" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ common-datasource.yml 导入失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 导入Redis配置
Write-Host "导入 common-redis.yml..." -ForegroundColor Yellow
$redisParams = @{
    Uri = "$nacosUrl/nacos/v1/cs/configs"
    Method = "POST"
    Headers = @{
        "Content-Type" = "application/x-www-form-urlencoded"
    }
    Body = @{
        dataId = "common-redis.yml"
        group = "DEFAULT_GROUP"
        namespaceId = "public"
        content = $redisContent
        type = "yaml"
    }
    Credential = New-Object System.Management.Automation.PSCredential($username, (ConvertTo-SecureString $password -AsPlainText -Force))
}

try {
    $result = Invoke-RestMethod @redisParams
    if ($result -eq "true") {
        Write-Host "✓ common-redis.yml 导入成功" -ForegroundColor Green
    } else {
        Write-Host "✗ common-redis.yml 导入失败" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ common-redis.yml 导入失败: $($_.Exception.Message)" -ForegroundColor Red
}