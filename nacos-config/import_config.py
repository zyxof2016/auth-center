import requests

# Nacos配置
nacos_url = "http://localhost:8848"
username = "nacos"
password = "nacos"

# 配置列表
configs = [
    {"dataId": "common-datasource.yml", "file": "common-datasource.yml"},
    {"dataId": "common-redis.yml", "file": "common-redis.yml"}
]

# 导入配置
for config in configs:
    with open(f"F:/AIProjects/auth-center4/nacos-config/{config['file']}", "r", encoding="utf-8") as f:
        content = f.read()
    
    # 准备请求参数
    data = {
        "dataId": config["dataId"],
        "group": "DEFAULT_GROUP",
        "namespaceId": "public",
        "content": content,
        "type": "yaml"
    }
    
    # 发送请求
    response = requests.post(f"{nacos_url}/nacos/v1/cs/configs", data=data, auth=(username, password))
    
    if response.status_code == 200:
        print(f"✓ {config['dataId']} 导入成功")
    else:
        print(f"✗ {config['dataId']} 导入失败: {response.text}")