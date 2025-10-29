#!/bin/bash

# Nacos配置导入脚本
# 使用前请确保Nacos服务已启动，地址为 localhost:8848

NACOS_URL="http://localhost:8848"
NACOS_USERNAME="nacos"
NACOS_PASSWORD="nacos"
NAMESPACE="public"
GROUP="DEFAULT_GROUP"

# 导入公共配置
echo "导入公共数据源配置..."
curl -X POST "$NACOS_URL/nacos/v1/cs/configs" \
  -u "$NACOS_USERNAME:$NACOS_PASSWORD" \
  -d "dataId=common-datasource.yml" \
  -d "group=$GROUP" \
  -d "namespaceId=$NAMESPACE" \
  -d "content=$(cat common-datasource.yml)" \
  -d "type=yaml"

echo "导入公共Redis配置..."
curl -X POST "$NACOS_URL/nacos/v1/cs/configs" \
  -u "$NACOS_USERNAME:$NACOS_PASSWORD" \
  -d "dataId=common-redis.yml" \
  -d "group=$GROUP" \
  -d "namespaceId=$NAMESPACE" \
  -d "content=$(cat common-redis.yml)" \
  -d "type=yaml"

# 导入各服务配置
echo "导入网关服务配置..."
curl -X POST "$NACOS_URL/nacos/v1/cs/configs" \
  -u "$NACOS_USERNAME:$NACOS_PASSWORD" \
  -d "dataId=auth-gateway.yml" \
  -d "group=$GROUP" \
  -d "namespaceId=$NAMESPACE" \
  -d "content=$(cat auth-gateway.yml)" \
  -d "type=yaml"

echo "导入认证服务配置..."
curl -X POST "$NACOS_URL/nacos/v1/cs/configs" \
  -u "$NACOS_USERNAME:$NACOS_PASSWORD" \
  -d "dataId=auth-server.yml" \
  -d "group=$GROUP" \
  -d "namespaceId=$NAMESPACE" \
  -d "content=$(cat auth-server.yml)" \
  -d "type=yaml"

echo "所有配置导入完成！"
echo "Nacos控制台地址: $NACOS_URL/nacos"
echo "用户名: $NACOS_USERNAME"
echo "密码: $NACOS_PASSWORD"