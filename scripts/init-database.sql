-- 认证中心数据库初始化脚本
-- 创建数据库和用户
CREATE DATABASE IF NOT EXISTS `auth_center` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'auth_user'@'%' IDENTIFIED BY 'Auth123456!';
GRANT ALL PRIVILEGES ON `auth_center`.* TO 'auth_user'@'%';
FLUSH PRIVILEGES;

USE `auth_center`;

-- 设置SQL模式
SET FOREIGN_KEY_CHECKS = 0;
SET NAMES utf8mb4;

-- ===============================================
-- 1. 租户管理表
-- ===============================================

-- sys_tenant (租户表)
CREATE TABLE IF NOT EXISTS `sys_tenant` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '租户ID',
    `tenant_code` VARCHAR(64) NOT NULL UNIQUE COMMENT '租户编码',
    `tenant_name` VARCHAR(128) NOT NULL COMMENT '租户名称',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `contact_person` VARCHAR(64) COMMENT '联系人',
    `contact_phone` VARCHAR(20) COMMENT '联系电话',
    `contact_email` VARCHAR(128) COMMENT '联系邮箱',
    `expire_time` DATETIME COMMENT '过期时间',
    `max_user_count` INT DEFAULT 100 COMMENT '最大用户数',
    `description` VARCHAR(500) COMMENT '描述',
    `created_by` BIGINT COMMENT '创建人',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT COMMENT '更新人',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_tenant_code` (`tenant_code`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户信息表';

-- ===============================================
-- 2. 用户管理表 (user-service)
-- ===============================================

-- sys_user (用户表)
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `username` VARCHAR(64) NOT NULL COMMENT '用户名',
    `password` VARCHAR(128) COMMENT '密码（第三方登录用户可能为空）',
    `email` VARCHAR(128) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `real_name` VARCHAR(64) COMMENT '真实姓名',
    `nickname` VARCHAR(64) COMMENT '昵称',
    `avatar` VARCHAR(500) COMMENT '头像',
    `gender` TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    `birthday` DATE COMMENT '生日',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `user_type` TINYINT DEFAULT 1 COMMENT '用户类型：1-普通用户，2-第三方用户',
    `last_login_time` DATETIME COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(64) COMMENT '最后登录IP',
    `login_fail_count` INT DEFAULT 0 COMMENT '登录失败次数',
    `pwd_update_time` DATETIME COMMENT '密码修改时间',
    `created_by` BIGINT COMMENT '创建人',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT COMMENT '更新人',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_tenant_username` (`tenant_id`, `username`),
    UNIQUE KEY `uk_tenant_email` (`tenant_id`, `email`),
    UNIQUE KEY `uk_tenant_phone` (`tenant_id`, `phone`),
    INDEX `idx_tenant_id` (`tenant_id`),
    INDEX `idx_username` (`username`),
    INDEX `idx_email` (`email`),
    INDEX `idx_phone` (`phone`),
    INDEX `idx_status` (`status`),
    INDEX `idx_user_type` (`user_type`),
    INDEX `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- sys_user_third_auth (用户第三方认证表)
CREATE TABLE IF NOT EXISTS `sys_user_third_auth` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `third_type` VARCHAR(20) NOT NULL COMMENT '第三方类型：WECHAT/QQ/ALIPAY/GITHUB',
    `third_id` VARCHAR(128) NOT NULL COMMENT '第三方用户唯一标识',
    `third_union_id` VARCHAR(128) COMMENT '第三方UnionID（微信专用）',
    `third_nickname` VARCHAR(128) COMMENT '第三方昵称',
    `third_avatar` VARCHAR(500) COMMENT '第三方头像',
    `access_token` VARCHAR(500) COMMENT '访问令牌',
    `refresh_token` VARCHAR(500) COMMENT '刷新令牌',
    `expire_time` DATETIME COMMENT '令牌过期时间',
    `bind_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-解绑，1-绑定',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_third_user` (`tenant_id`, `third_type`, `third_id`),
    UNIQUE KEY `uk_user_third` (`tenant_id`, `user_id`, `third_type`),
    INDEX `idx_tenant_id` (`tenant_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_third_type` (`third_type`),
    INDEX `idx_third_id` (`third_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户第三方认证表';

-- ===============================================
-- 3. 角色权限表 (role-service)
-- ===============================================

-- sys_role (角色表)
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `role_code` VARCHAR(64) NOT NULL COMMENT '角色编码',
    `role_name` VARCHAR(128) NOT NULL COMMENT '角色名称',
    `role_type` TINYINT DEFAULT 1 COMMENT '角色类型：1-系统角色，2-自定义角色',
    `data_scope` TINYINT DEFAULT 1 COMMENT '数据权限范围：1-全部，2-本部门，3-本部门及子部门，4-仅本人',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `description` VARCHAR(500) COMMENT '描述',
    `created_by` BIGINT COMMENT '创建人',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT COMMENT '更新人',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_tenant_role_code` (`tenant_id`, `role_code`),
    INDEX `idx_tenant_id` (`tenant_id`),
    INDEX `idx_role_type` (`role_type`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';

-- sys_menu (菜单表)
CREATE TABLE IF NOT EXISTS `sys_menu` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '菜单ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父菜单ID',
    `menu_name` VARCHAR(64) NOT NULL COMMENT '菜单名称',
    `menu_type` TINYINT DEFAULT 1 COMMENT '菜单类型：1-目录，2-菜单，3-按钮',
    `path` VARCHAR(200) COMMENT '路由路径',
    `component` VARCHAR(200) COMMENT '组件路径',
    `perms` VARCHAR(100) COMMENT '权限标识',
    `icon` VARCHAR(100) COMMENT '菜单图标',
    `sort_order` INT DEFAULT 0 COMMENT '显示顺序',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `is_external` TINYINT DEFAULT 0 COMMENT '是否外链：0-否，1-是',
    `created_by` BIGINT COMMENT '创建人',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT COMMENT '更新人',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_tenant_id` (`tenant_id`),
    INDEX `idx_parent_id` (`parent_id`),
    INDEX `idx_menu_type` (`menu_type`),
    INDEX `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- sys_role_menu (角色菜单关联表)
CREATE TABLE IF NOT EXISTS `sys_role_menu` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
    `created_by` BIGINT COMMENT '创建人',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`),
    INDEX `idx_role_id` (`role_id`),
    INDEX `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- sys_user_role (用户角色关联表)
CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `created_by` BIGINT COMMENT '创建人',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ===============================================
-- 4. 客户端管理表 (client-service)
-- ===============================================

-- sys_client (客户端表)
CREATE TABLE IF NOT EXISTS `sys_client` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '客户端ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `client_id` VARCHAR(64) NOT NULL UNIQUE COMMENT '客户端ID',
    `client_secret` VARCHAR(128) NOT NULL COMMENT '客户端密钥',
    `client_name` VARCHAR(128) NOT NULL COMMENT '客户端名称',
    `client_type` TINYINT DEFAULT 1 COMMENT '客户端类型：1-Web应用，2-移动应用，3-第三方应用',
    `authorized_grant_types` VARCHAR(200) COMMENT '授权类型',
    `redirect_uris` VARCHAR(1000) COMMENT '重定向URI',
    `scope` VARCHAR(200) COMMENT '权限范围',
    `access_token_validity` INT DEFAULT 7200 COMMENT '访问令牌有效期(秒)',
    `refresh_token_validity` INT DEFAULT 604800 COMMENT '刷新令牌有效期(秒)',
    `auto_approve` TINYINT DEFAULT 0 COMMENT '自动授权：0-否，1-是',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `description` VARCHAR(500) COMMENT '描述',
    `created_by` BIGINT COMMENT '创建人',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT COMMENT '更新人',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_tenant_id` (`tenant_id`),
    INDEX `idx_client_id` (`client_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OAuth2客户端表';

-- ===============================================
-- 5. 日志管理表 (log-service)
-- ===============================================

-- sys_operation_log (操作日志表)
CREATE TABLE IF NOT EXISTS `sys_operation_log` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `user_id` BIGINT COMMENT '用户ID',
    `username` VARCHAR(64) COMMENT '用户名',
    `operation_type` VARCHAR(50) COMMENT '操作类型',
    `operation_module` VARCHAR(100) COMMENT '操作模块',
    `operation_desc` VARCHAR(500) COMMENT '操作描述',
    `request_method` VARCHAR(10) COMMENT '请求方法',
    `request_url` VARCHAR(500) COMMENT '请求URL',
    `request_params` TEXT COMMENT '请求参数',
    `response_result` TEXT COMMENT '响应结果',
    `ip_address` VARCHAR(64) COMMENT 'IP地址',
    `user_agent` VARCHAR(500) COMMENT '用户代理',
    `execute_time` BIGINT COMMENT '执行时间(毫秒)',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-失败，1-成功',
    `error_message` TEXT COMMENT '错误信息',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_tenant_id` (`tenant_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_operation_type` (`operation_type`),
    INDEX `idx_created_time` (`created_time`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- sys_login_log (登录日志表)
CREATE TABLE IF NOT EXISTS `sys_login_log` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `user_id` BIGINT COMMENT '用户ID',
    `username` VARCHAR(64) COMMENT '用户名',
    `login_type` VARCHAR(20) COMMENT '登录类型：PASSWORD/EMAIL_PASSWORD/PHONE_PASSWORD/PHONE_CODE/THIRD_WECHAT/THIRD_QQ',
    `login_ip` VARCHAR(64) COMMENT '登录IP',
    `login_location` VARCHAR(200) COMMENT '登录地点',
    `user_agent` VARCHAR(500) COMMENT '用户代理',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-失败，1-成功',
    `fail_reason` VARCHAR(200) COMMENT '失败原因',
    `login_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    INDEX `idx_tenant_id` (`tenant_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_username` (`username`),
    INDEX `idx_login_time` (`login_time`),
    INDEX `idx_status` (`status`),
    INDEX `idx_login_type` (`login_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- sys_verification_code (验证码表)
CREATE TABLE IF NOT EXISTS `sys_verification_code` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '验证码ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `receiver` VARCHAR(128) NOT NULL COMMENT '接收者（手机号/邮箱）',
    `code_type` VARCHAR(20) NOT NULL COMMENT '验证码类型：LOGIN/REGISTER/RESET_PASSWORD/BIND_PHONE/BIND_EMAIL',
    `code` VARCHAR(20) NOT NULL COMMENT '验证码',
    `biz_id` VARCHAR(128) COMMENT '业务ID（短信服务商返回）',
    `expire_time` DATETIME NOT NULL COMMENT '过期时间',
    `used` TINYINT DEFAULT 0 COMMENT '是否已使用：0-未使用，1-已使用',
    `used_time` DATETIME COMMENT '使用时间',
    `ip_address` VARCHAR(64) COMMENT '请求IP',
    `send_status` TINYINT DEFAULT 1 COMMENT '发送状态：0-失败，1-成功',
    `send_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_tenant_id` (`tenant_id`),
    INDEX `idx_receiver` (`receiver`),
    INDEX `idx_code_type` (`code_type`),
    INDEX `idx_expire_time` (`expire_time`),
    INDEX `idx_used` (`used`),
    INDEX `idx_send_status` (`send_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='验证码表';

-- ===============================================
-- 6. 系统配置表
-- ===============================================

-- sys_config (系统配置表)
CREATE TABLE IF NOT EXISTS `sys_config` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
    `config_value` TEXT COMMENT '配置值',
    `config_name` VARCHAR(100) COMMENT '配置名称',
    `config_type` TINYINT DEFAULT 1 COMMENT '配置类型：1-系统配置，2-业务配置',
    `is_encrypted` TINYINT DEFAULT 0 COMMENT '是否加密：0-否，1-是',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `description` VARCHAR(500) COMMENT '描述',
    `created_by` BIGINT COMMENT '创建人',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT COMMENT '更新人',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_tenant_config_key` (`tenant_id`, `config_key`),
    INDEX `idx_tenant_id` (`tenant_id`),
    INDEX `idx_config_type` (`config_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- ===============================================
-- 7. 文件存储表 (file-service)
-- ===============================================

-- sys_file (文件信息表)
CREATE TABLE IF NOT EXISTS `sys_file` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文件ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `file_id` VARCHAR(64) NOT NULL COMMENT '文件唯一标识',
    `file_name` VARCHAR(255) NOT NULL COMMENT '文件名',
    `file_size` BIGINT NOT NULL COMMENT '文件大小(字节)',
    `file_type` VARCHAR(128) COMMENT '文件类型',
    `file_extension` VARCHAR(20) COMMENT '文件扩展名',
    `storage_type` VARCHAR(20) NOT NULL COMMENT '存储类型：oss/minio/s3/google',
    `bucket_name` VARCHAR(100) NOT NULL COMMENT '存储桶名称',
    `object_key` VARCHAR(500) NOT NULL COMMENT '对象键',
    `file_url` VARCHAR(1000) COMMENT '文件访问URL',
    `category` VARCHAR(50) COMMENT '文件分类：avatar/document/image/video/other',
    `is_public` TINYINT DEFAULT 0 COMMENT '是否公开：0-私有，1-公开',
    `upload_user_id` BIGINT NOT NULL COMMENT '上传用户ID',
    `upload_username` VARCHAR(64) COMMENT '上传用户名',
    `metadata` JSON COMMENT '文件元数据',
    `description` VARCHAR(500) COMMENT '文件描述',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-删除，1-正常',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_file_id` (`file_id`),
    UNIQUE KEY `uk_object_key` (`storage_type`, `bucket_name`, `object_key`),
    INDEX `idx_tenant_id` (`tenant_id`),
    INDEX `idx_upload_user_id` (`upload_user_id`),
    INDEX `idx_category` (`category`),
    INDEX `idx_storage_type` (`storage_type`),
    INDEX `idx_created_time` (`created_time`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件信息表';

-- sys_file_preview (文件预览记录表)
CREATE TABLE IF NOT EXISTS `sys_file_preview` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `file_id` VARCHAR(64) NOT NULL COMMENT '文件ID',
    `preview_token` VARCHAR(128) NOT NULL COMMENT '预览令牌',
    `preview_url` VARCHAR(1000) COMMENT '预览URL',
    `expire_time` DATETIME NOT NULL COMMENT '过期时间',
    `access_count` INT DEFAULT 0 COMMENT '访问次数',
    `last_access_time` DATETIME COMMENT '最后访问时间',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-失效，1-有效',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_tenant_id` (`tenant_id`),
    INDEX `idx_file_id` (`file_id`),
    INDEX `idx_preview_token` (`preview_token`),
    INDEX `idx_expire_time` (`expire_time`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件预览记录表';

-- sys_storage_config (存储配置表)
CREATE TABLE IF NOT EXISTS `sys_storage_config` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `storage_type` VARCHAR(20) NOT NULL COMMENT '存储类型：oss/minio/s3/google',
    `config_name` VARCHAR(100) NOT NULL COMMENT '配置名称',
    `endpoint` VARCHAR(500) COMMENT '服务端点',
    `access_key` VARCHAR(255) COMMENT '访问密钥',
    `secret_key` VARCHAR(255) COMMENT '秘密密钥',
    `bucket_name` VARCHAR(100) NOT NULL COMMENT '存储桶名称',
    `region` VARCHAR(50) COMMENT '区域',
    `is_default` TINYINT DEFAULT 0 COMMENT '是否默认：0-否，1-是',
    `max_file_size` BIGINT DEFAULT 10485760 COMMENT '最大文件大小(字节)',
    `allowed_types` VARCHAR(500) COMMENT '允许的文件类型',
    `description` VARCHAR(500) COMMENT '描述',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `created_by` BIGINT COMMENT '创建人',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT COMMENT '更新人',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_tenant_storage` (`tenant_id`, `storage_type`, `config_name`),
    INDEX `idx_tenant_id` (`tenant_id`),
    INDEX `idx_storage_type` (`storage_type`),
    INDEX `idx_is_default` (`is_default`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='存储配置表';

-- ===============================================
-- 8. 消息服务表 (message-service)
-- ===============================================

-- sys_message (消息表)
CREATE TABLE IF NOT EXISTS `sys_message` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `message_type` VARCHAR(50) NOT NULL COMMENT '消息类型：SYSTEM/BUSINESS/NOTIFICATION',
    `message_title` VARCHAR(200) COMMENT '消息标题',
    `message_content` TEXT COMMENT '消息内容',
    `sender_id` BIGINT COMMENT '发送者ID',
    `sender_name` VARCHAR(64) COMMENT '发送者名称',
    `receiver_type` TINYINT DEFAULT 1 COMMENT '接收者类型：1-用户，2-角色，3-部门',
    `receiver_ids` TEXT COMMENT '接收者ID列表',
    `priority` TINYINT DEFAULT 1 COMMENT '优先级：1-低，2-中，3-高',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-草稿，1-已发送，2-已读，3-删除',
    `send_time` DATETIME COMMENT '发送时间',
    `expire_time` DATETIME COMMENT '过期时间',
    `read_count` INT DEFAULT 0 COMMENT '已读人数',
    `total_count` INT DEFAULT 0 COMMENT '总人数',
    `created_by` BIGINT COMMENT '创建人',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_tenant_id` (`tenant_id`),
    INDEX `idx_message_type` (`message_type`),
    INDEX `idx_sender_id` (`sender_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_send_time` (`send_time`),
    INDEX `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

-- sys_message_user (用户消息关联表)
CREATE TABLE IF NOT EXISTS `sys_message_user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `message_id` BIGINT NOT NULL COMMENT '消息ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `read_status` TINYINT DEFAULT 0 COMMENT '阅读状态：0-未读，1-已读',
    `read_time` DATETIME COMMENT '阅读时间',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_message_user` (`message_id`, `user_id`),
    INDEX `idx_message_id` (`message_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_read_status` (`read_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户消息关联表';

-- ===============================================
-- 9. 通知服务表 (notification-service)
-- ===============================================

-- sys_notification (通知表)
CREATE TABLE IF NOT EXISTS `sys_notification` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `notification_type` VARCHAR(50) NOT NULL COMMENT '通知类型：EMAIL/SMS/WECHAT/IN_APP',
    `template_id` VARCHAR(64) COMMENT '模板ID',
    `template_content` TEXT COMMENT '模板内容',
    `receiver` VARCHAR(500) NOT NULL COMMENT '接收者',
    `subject` VARCHAR(200) COMMENT '主题',
    `content` TEXT COMMENT '内容',
    `params` JSON COMMENT '模板参数',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-待发送，1-发送中，2-发送成功，3-发送失败',
    `send_count` INT DEFAULT 0 COMMENT '发送次数',
    `max_retry_count` INT DEFAULT 3 COMMENT '最大重试次数',
    `next_retry_time` DATETIME COMMENT '下次重试时间',
    `send_time` DATETIME COMMENT '发送时间',
    `error_message` TEXT COMMENT '错误信息',
    `created_by` BIGINT COMMENT '创建人',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_tenant_id` (`tenant_id`),
    INDEX `idx_notification_type` (`notification_type`),
    INDEX `idx_receiver` (`receiver`),
    INDEX `idx_status` (`status`),
    INDEX `idx_send_time` (`send_time`),
    INDEX `idx_next_retry_time` (`next_retry_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- ===============================================
-- 10. 监控服务表 (monitor-service)
-- ===============================================

-- sys_system_metrics (系统指标表)
CREATE TABLE IF NOT EXISTS `sys_system_metrics` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '指标ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `service_name` VARCHAR(100) NOT NULL COMMENT '服务名称',
    `metric_type` VARCHAR(50) NOT NULL COMMENT '指标类型：CPU/MEMORY/DISK/NETWORK',
    `metric_name` VARCHAR(100) NOT NULL COMMENT '指标名称',
    `metric_value` DECIMAL(10,4) NOT NULL COMMENT '指标值',
    `metric_unit` VARCHAR(20) COMMENT '指标单位',
    `collect_time` DATETIME NOT NULL COMMENT '采集时间',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_tenant_id` (`tenant_id`),
    INDEX `idx_service_name` (`service_name`),
    INDEX `idx_metric_type` (`metric_type`),
    INDEX `idx_collect_time` (`collect_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统指标表';

-- sys_service_health (服务健康状态表)
CREATE TABLE IF NOT EXISTS `sys_service_health` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `service_name` VARCHAR(100) NOT NULL COMMENT '服务名称',
    `service_url` VARCHAR(500) COMMENT '服务URL',
    `health_status` TINYINT DEFAULT 1 COMMENT '健康状态：0-异常，1-正常',
    `response_time` INT COMMENT '响应时间(毫秒)',
    `last_check_time` DATETIME NOT NULL COMMENT '最后检查时间',
    `error_message` TEXT COMMENT '错误信息',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_tenant_service` (`tenant_id`, `service_name`),
    INDEX `idx_tenant_id` (`tenant_id`),
    INDEX `idx_service_name` (`service_name`),
    INDEX `idx_health_status` (`health_status`),
    INDEX `idx_last_check_time` (`last_check_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务健康状态表';

-- sys_alert_rule (告警规则表)
CREATE TABLE IF NOT EXISTS `sys_alert_rule` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '规则ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `rule_name` VARCHAR(100) NOT NULL COMMENT '规则名称',
    `rule_type` VARCHAR(50) NOT NULL COMMENT '规则类型：METRIC/LOG/EVENT',
    `metric_name` VARCHAR(100) COMMENT '指标名称',
    `operator` VARCHAR(10) COMMENT '操作符：>、<、=、>=、<=',
    `threshold_value` DECIMAL(10,4) COMMENT '阈值',
    `duration` INT COMMENT '持续时间(秒)',
    `alert_level` TINYINT DEFAULT 1 COMMENT '告警级别：1-低，2-中，3-高',
    `notification_channels` VARCHAR(200) COMMENT '通知渠道',
    `enabled` TINYINT DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    `description` VARCHAR(500) COMMENT '描述',
    `created_by` BIGINT COMMENT '创建人',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT COMMENT '更新人',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_tenant_id` (`tenant_id`),
    INDEX `idx_rule_type` (`rule_type`),
    INDEX `idx_metric_name` (`metric_name`),
    INDEX `idx_alert_level` (`alert_level`),
    INDEX `idx_enabled` (`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警规则表';

-- sys_alert_record (告警记录表)
CREATE TABLE IF NOT EXISTS `sys_alert_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `rule_id` BIGINT NOT NULL COMMENT '规则ID',
    `alert_title` VARCHAR(200) NOT NULL COMMENT '告警标题',
    `alert_content` TEXT COMMENT '告警内容',
    `alert_level` TINYINT DEFAULT 1 COMMENT '告警级别：1-低，2-中，3-高',
    `metric_value` DECIMAL(10,4) COMMENT '指标值',
    `threshold_value` DECIMAL(10,4) COMMENT '阈值',
    `alert_time` DATETIME NOT NULL COMMENT '告警时间',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-未处理，1-已处理',
    `handler_id` BIGINT COMMENT '处理人ID',
    `handler_name` VARCHAR(64) COMMENT '处理人名称',
    `handle_time` DATETIME COMMENT '处理时间',
    `handle_remark` VARCHAR(500) COMMENT '处理备注',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_tenant_id` (`tenant_id`),
    INDEX `idx_rule_id` (`rule_id`),
    INDEX `idx_alert_level` (`alert_level`),
    INDEX `idx_alert_time` (`alert_time`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警记录表';

-- ===============================================
-- 11. 基础数据初始化
-- ===============================================

-- 初始化默认租户
INSERT IGNORE INTO `sys_tenant` (`id`, `tenant_code`, `tenant_name`, `status`, `description`) 
VALUES (1, 'default', '默认租户', 1, '系统默认租户');

-- 初始化超级管理员用户 (密码: admin123)
INSERT IGNORE INTO `sys_user` (`id`, `tenant_id`, `username`, `password`, `real_name`, `status`, `user_type`) 
VALUES (1, 1, 'admin', '$2a$10$r3xMLRr7s8Q7ZqB6N9Yz0.L8zV7nV8Y7aN8Y7zV6nB9Y7zV6nB9Y7zV6', '超级管理员', 1, 1);

-- 初始化系统角色
INSERT IGNORE INTO `sys_role` (`id`, `tenant_id`, `role_code`, `role_name`, `role_type`, `status`, `description`) 
VALUES 
(1, 1, 'super_admin', '超级管理员', 1, 1, '系统超级管理员，拥有所有权限'),
(2, 1, 'user_admin', '用户管理员', 1, 1, '用户管理相关权限'),
(3, 1, 'audit_admin', '审计管理员', 1, 1, '日志审计相关权限');

-- 初始化用户角色关联
INSERT IGNORE INTO `sys_user_role` (`user_id`, `role_id`, `created_by`) 
VALUES (1, 1, 1);

-- 初始化系统菜单
INSERT IGNORE INTO `sys_menu` (`id`, `tenant_id`, `parent_id`, `menu_name`, `menu_type`, `path`, `perms`, `icon`, `sort_order`) 
VALUES 
(1, 1, 0, '系统管理', 1, '/system', NULL, 'el-icon-setting', 1),
(2, 1, 1, '用户管理', 2, '/system/user', 'system:user:list', 'el-icon-user', 1),
(3, 1, 1, '角色管理', 2, '/system/role', 'system:role:list', 'el-icon-s-custom', 2),
(4, 1, 1, '菜单管理', 2, '/system/menu', 'system:menu:list', 'el-icon-menu', 3),
(5, 1, 0, '日志管理', 1, '/log', NULL, 'el-icon-document', 2),
(6, 1, 5, '操作日志', 2, '/log/operation', 'log:operation:list', 'el-icon-edit', 1),
(7, 1, 5, '登录日志', 2, '/log/login', 'log:login:list', 'el-icon-user-solid', 2);

-- 初始化角色菜单关联
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`, `created_by`) 
VALUES 
(1, 1, 1), (1, 2, 1), (1, 3, 1), (1, 4, 1), (1, 5, 1), (1, 6, 1), (1, 7, 1),
(2, 1, 1), (2, 2, 1),
(3, 5, 1), (3, 6, 1), (3, 7, 1);

-- 初始化默认客户端
INSERT IGNORE INTO `sys_client` (`id`, `tenant_id`, `client_id`, `client_secret`, `client_name`, `client_type`, `authorized_grant_types`, `redirect_uris`, `scope`, `status`) 
VALUES 
(1, 1, 'web-app', '$2a$10$r3xMLRr7s8Q7ZqB6N9Yz0.L8zV7nV8Y7aN8Y7zV6nB9Y7zV6nB9Y7zV6', 'Web应用', 1, 'authorization_code,password,refresh_token', 'http://localhost:8080/callback', 'read,write', 1),
(2, 1, 'mobile-app', '$2a$10$r3xMLRr7s8Q7ZqB6N9Yz0.L8zV7nV8Y7aN8Y7zV6nB9Y7zV6nB9Y7zV6', '移动应用', 2, 'password,refresh_token', NULL, 'read,write', 1);

-- 初始化系统配置
INSERT IGNORE INTO `sys_config` (`id`, `tenant_id`, `config_key`, `config_value`, `config_name`, `config_type`, `description`) 
VALUES 
(1, 1, 'system.name', '认证中心', '系统名称', 1, '系统显示名称'),
(2, 1, 'system.version', '1.0.0', '系统版本', 1, '系统版本号'),
(3, 1, 'login.max_fail_count', '5', '最大登录失败次数', 1, '登录失败次数限制'),
(4, 1, 'login.lock_duration', '1800', '账户锁定时长(秒)', 1, '登录失败锁定时长');

-- 初始化存储配置
INSERT IGNORE INTO `sys_storage_config` (`id`, `tenant_id`, `storage_type`, `config_name`, `endpoint`, `bucket_name`, `is_default`, `max_file_size`, `allowed_types`, `status`) 
VALUES 
(1, 1, 'minio', '默认存储', 'http://localhost:9000', 'auth-center', 1, 10485760, 'jpg,png,pdf,doc,docx', 1);

-- 初始化告警规则
INSERT IGNORE INTO `sys_alert_rule` (`id`, `tenant_id`, `rule_name`, `rule_type`, `metric_name`, `operator`, `threshold_value`, `alert_level`, `enabled`, `description`) 
VALUES 
(1, 1, 'CPU使用率告警', 'METRIC', 'cpu.usage', '>', 80.0, 2, 1, 'CPU使用率超过80%告警'),
(2, 1, '内存使用率告警', 'METRIC', 'memory.usage', '>', 85.0, 2, 1, '内存使用率超过85%告警'),
(3, 1, '磁盘使用率告警', 'METRIC', 'disk.usage', '>', 90.0, 3, 1, '磁盘使用率超过90%告警');

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 显示表创建结果
SELECT '数据库初始化完成!' AS '结果';
SELECT COUNT(*) AS '表数量' FROM information_schema.tables WHERE table_schema = 'auth_center';
SELECT table_name AS '表名', table_comment AS '表说明' FROM information_schema.tables WHERE table_schema = 'auth_center' ORDER BY table_name;