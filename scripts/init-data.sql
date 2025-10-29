-- 认证中心基础数据初始化脚本
-- 此脚本用于初始化系统运行所需的基础数据

USE `auth_center`;

-- ===============================================
-- 1. 租户数据初始化
-- ===============================================

-- 初始化默认租户
INSERT IGNORE INTO `sys_tenant` (`id`, `tenant_code`, `tenant_name`, `status`, `contact_person`, `contact_email`, `max_user_count`, `description`) 
VALUES 
(1, 'default', '默认租户', 1, '系统管理员', 'admin@auth-center.com', 1000, '系统默认租户，用于系统管理和演示'),
(2, 'demo', '演示租户', 1, '演示用户', 'demo@auth-center.com', 100, '演示用租户，包含示例数据'),
(3, 'test', '测试租户', 0, '测试用户', 'test@auth-center.com', 50, '测试用租户，默认禁用');

-- ===============================================
-- 2. 用户数据初始化
-- ===============================================

-- 初始化超级管理员用户 (密码: admin123)
INSERT IGNORE INTO `sys_user` (`id`, `tenant_id`, `username`, `password`, `email`, `phone`, `real_name`, `nickname`, `status`, `user_type`) 
VALUES 
(1, 1, 'admin', '$2a$10$r3xMLRr7s8Q7ZqB6N9Yz0.L8zV7nV8Y7aN8Y7zV6nB9Y7zV6nB9Y7zV6', 'admin@auth-center.com', '13800000001', '超级管理员', '系统管理员', 1, 1),
(2, 1, 'user_admin', '$2a$10$r3xMLRr7s8Q7ZqB6N9Yz0.L8zV7nV8Y7aN8Y7zV6nB9Y7zV6nB9Y7zV6', 'user_admin@auth-center.com', '13800000002', '用户管理员', '用户管理', 1, 1),
(3, 1, 'audit_admin', '$2a$10$r3xMLRr7s8Q7ZqB6N9Yz0.L8zV7nV8Y7aN8Y7zV6nB9Y7zV6nB9Y7zV6', 'audit_admin@auth-center.com', '13800000003', '审计管理员', '审计管理', 1, 1),
(4, 2, 'demo_user', '$2a$10$r3xMLRr7s8Q7ZqB6N9Yz0.L8zV7nV8Y7aN8Y7zV6nB9Y7zV6nB9Y7zV6', 'demo@auth-center.com', '13800000004', '演示用户', '演示用户', 1, 1),
(5, 2, 'test_user', '$2a$10$r3xMLRr7s8Q7ZqB6N9Yz0.L8zV7nV8Y7aN8Y7zV6nB9Y7zV6nB9Y7zV6', 'test@auth-center.com', '13800000005', '测试用户', '测试用户', 1, 1);

-- ===============================================
-- 3. 角色数据初始化
-- ===============================================

-- 初始化系统角色
INSERT IGNORE INTO `sys_role` (`id`, `tenant_id`, `role_code`, `role_name`, `role_type`, `data_scope`, `status`, `description`) 
VALUES 
(1, 1, 'super_admin', '超级管理员', 1, 1, 1, '系统超级管理员，拥有所有权限'),
(2, 1, 'user_admin', '用户管理员', 1, 2, 1, '用户管理相关权限，可管理用户信息'),
(3, 1, 'role_admin', '角色管理员', 1, 2, 1, '角色管理相关权限，可管理角色和权限'),
(4, 1, 'audit_admin', '审计管理员', 1, 3, 1, '日志审计相关权限，可查看系统日志'),
(5, 1, 'client_admin', '客户端管理员', 1, 2, 1, '客户端管理相关权限，可管理OAuth2客户端'),
(6, 2, 'demo_admin', '演示管理员', 2, 2, 1, '演示租户管理员角色'),
(7, 2, 'demo_user', '演示用户', 2, 4, 1, '演示租户普通用户角色');

-- ===============================================
-- 4. 菜单数据初始化
-- ===============================================

-- 初始化系统菜单
INSERT IGNORE INTO `sys_menu` (`id`, `tenant_id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort_order`) 
VALUES 
-- 系统管理菜单
(1, 1, 0, '系统管理', 1, '/system', 'Layout', NULL, 'el-icon-setting', 1),
(2, 1, 1, '用户管理', 2, 'user', 'system/user/index', 'system:user:list', 'el-icon-user', 1),
(3, 1, 1, '角色管理', 2, 'role', 'system/role/index', 'system:role:list', 'el-icon-s-custom', 2),
(4, 1, 1, '菜单管理', 2, 'menu', 'system/menu/index', 'system:menu:list', 'el-icon-menu', 3),
(5, 1, 1, '客户端管理', 2, 'client', 'system/client/index', 'system:client:list', 'el-icon-monitor', 4),

-- 日志管理菜单
(6, 1, 0, '日志管理', 1, '/log', 'Layout', NULL, 'el-icon-document', 2),
(7, 1, 6, '操作日志', 2, 'operation', 'log/operation/index', 'log:operation:list', 'el-icon-edit', 1),
(8, 1, 6, '登录日志', 2, 'login', 'log/login/index', 'log:login:list', 'el-icon-user-solid', 2),

-- 监控管理菜单
(9, 1, 0, '监控管理', 1, '/monitor', 'Layout', NULL, 'el-icon-data-analysis', 3),
(10, 1, 9, '系统监控', 2, 'system', 'monitor/system/index', 'monitor:system:list', 'el-icon-cpu', 1),
(11, 1, 9, '服务监控', 2, 'service', 'monitor/service/index', 'monitor:service:list', 'el-icon-service', 2),
(12, 1, 9, '告警管理', 2, 'alert', 'monitor/alert/index', 'monitor:alert:list', 'el-icon-warning', 3),

-- 文件管理菜单
(13, 1, 0, '文件管理', 1, '/file', 'Layout', NULL, 'el-icon-folder', 4),
(14, 1, 13, '文件列表', 2, 'list', 'file/list/index', 'file:list:list', 'el-icon-document', 1),
(15, 1, 13, '存储配置', 2, 'config', 'file/config/index', 'file:config:list', 'el-icon-setting', 2),

-- 消息管理菜单
(16, 1, 0, '消息管理', 1, '/message', 'Layout', NULL, 'el-icon-chat-dot-round', 5),
(17, 1, 16, '消息列表', 2, 'list', 'message/list/index', 'message:list:list', 'el-icon-message', 1),
(18, 1, 16, '消息模板', 2, 'template', 'message/template/index', 'message:template:list', 'el-icon-notebook-2', 2),

-- 通知管理菜单
(19, 1, 0, '通知管理', 1, '/notification', 'Layout', NULL, 'el-icon-bell', 6),
(20, 1, 19, '通知记录', 2, 'record', 'notification/record/index', 'notification:record:list', 'el-icon-time', 1),
(21, 1, 19, '通知配置', 2, 'config', 'notification/config/index', 'notification:config:list', 'el-icon-setting', 2);

-- ===============================================
-- 5. 用户角色关联初始化
-- ===============================================

-- 初始化用户角色关联
INSERT IGNORE INTO `sys_user_role` (`user_id`, `role_id`, `created_by`) 
VALUES 
-- 默认租户用户角色
(1, 1, 1), -- admin -> super_admin
(2, 2, 1), -- user_admin -> user_admin
(3, 4, 1), -- audit_admin -> audit_admin

-- 演示租户用户角色
(4, 6, 1), -- demo_user -> demo_admin
(5, 7, 1); -- test_user -> demo_user

-- ===============================================
-- 6. 角色菜单关联初始化
-- ===============================================

-- 初始化角色菜单关联
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`, `created_by`) 
VALUES 
-- 超级管理员拥有所有菜单权限
(1, 1, 1), (1, 2, 1), (1, 3, 1), (1, 4, 1), (1, 5, 1),
(1, 6, 1), (1, 7, 1), (1, 8, 1),
(1, 9, 1), (1, 10, 1), (1, 11, 1), (1, 12, 1),
(1, 13, 1), (1, 14, 1), (1, 15, 1),
(1, 16, 1), (1, 17, 1), (1, 18, 1),
(1, 19, 1), (1, 20, 1), (1, 21, 1),

-- 用户管理员拥有用户管理相关权限
(2, 1, 1), (2, 2, 1),

-- 审计管理员拥有日志管理权限
(4, 6, 1), (4, 7, 1), (4, 8, 1),

-- 演示管理员拥有演示相关权限
(6, 1, 1), (6, 2, 1), (6, 3, 1),
(6, 6, 1), (6, 7, 1), (6, 8, 1),

-- 演示用户拥有基本权限
(7, 13, 1), (7, 14, 1);

-- ===============================================
-- 7. 客户端数据初始化
-- ===============================================

-- 初始化OAuth2客户端
INSERT IGNORE INTO `sys_client` (`id`, `tenant_id`, `client_id`, `client_secret`, `client_name`, `client_type`, `authorized_grant_types`, `redirect_uris`, `scope`, `access_token_validity`, `refresh_token_validity`, `status`) 
VALUES 
(1, 1, 'web-app', '$2a$10$r3xMLRr7s8Q7ZqB6N9Yz0.L8zV7nV8Y7aN8Y7zV6nB9Y7zV6nB9Y7zV6', 'Web管理后台', 1, 'authorization_code,password,refresh_token', 'http://localhost:8080/callback,http://127.0.0.1:8080/callback', 'read,write', 7200, 604800, 1),
(2, 1, 'mobile-app', '$2a$10$r3xMLRr7s8Q7ZqB6N9Yz0.L8zV7nV8Y7aN8Y7zV6nB9Y7zV6nB9Y7zV6', '移动端应用', 2, 'password,refresh_token', NULL, 'read,write', 86400, 2592000, 1),
(3, 1, 'third-party', '$2a$10$r3xMLRr7s8Q7ZqB6N9Yz0.L8zV7nV8Y7aN8Y7zV6nB9Y7zV6nB9Y7zV6', '第三方应用', 3, 'authorization_code', 'https://third-party.com/callback', 'read', 3600, 86400, 1),
(4, 2, 'demo-app', '$2a$10$r3xMLRr7s8Q7ZqB6N9Yz0.L8zV7nV8Y7aN8Y7zV6nB9Y7zV6nB9Y7zV6', '演示应用', 1, 'authorization_code,password,refresh_token', 'http://demo.auth-center.com/callback', 'read,write', 7200, 604800, 1);

-- ===============================================
-- 8. 系统配置数据初始化
-- ===============================================

-- 初始化系统配置
INSERT IGNORE INTO `sys_config` (`id`, `tenant_id`, `config_key`, `config_value`, `config_name`, `config_type`, `description`) 
VALUES 
-- 系统基础配置
(1, 1, 'system.name', '认证中心管理系统', '系统名称', 1, '系统显示名称'),
(2, 1, 'system.version', '1.0.0', '系统版本', 1, '系统版本号'),
(3, 1, 'system.copyright', '© 2024 认证中心', '版权信息', 1, '系统版权信息'),

-- 登录安全配置
(4, 1, 'login.max_fail_count', '5', '最大登录失败次数', 1, '登录失败次数限制'),
(5, 1, 'login.lock_duration', '1800', '账户锁定时长(秒)', 1, '登录失败锁定时长'),
(6, 1, 'login.password_expire_days', '90', '密码过期天数', 1, '密码过期时间设置'),
(7, 1, 'login.session_timeout', '1800', '会话超时时长(秒)', 1, '用户会话超时时间'),

-- 文件上传配置
(8, 1, 'file.max_size', '10485760', '最大文件大小(字节)', 2, '文件上传大小限制'),
(9, 1, 'file.allowed_types', 'jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx', '允许的文件类型', 2, '允许上传的文件类型'),
(10, 1, 'file.storage.type', 'minio', '存储类型', 2, '文件存储类型'),

-- 消息通知配置
(11, 1, 'notification.email.enabled', 'true', '邮件通知启用', 2, '是否启用邮件通知'),
(12, 1, 'notification.sms.enabled', 'false', '短信通知启用', 2, '是否启用短信通知'),
(13, 1, 'notification.wechat.enabled', 'true', '微信通知启用', 2, '是否启用微信通知'),

-- 演示租户配置
(14, 2, 'system.name', '认证中心演示系统', '系统名称', 1, '演示系统名称'),
(15, 2, 'system.demo_mode', 'true', '演示模式', 2, '是否演示模式');

-- ===============================================
-- 9. 存储配置数据初始化
-- ===============================================

-- 初始化存储配置
INSERT IGNORE INTO `sys_storage_config` (`id`, `tenant_id`, `storage_type`, `config_name`, `endpoint`, `access_key`, `secret_key`, `bucket_name`, `region`, `is_default`, `max_file_size`, `allowed_types`, `status`) 
VALUES 
(1, 1, 'minio', '默认MinIO存储', 'http://localhost:9000', 'minioadmin', 'minioadmin', 'auth-center', 'us-east-1', 1, 10485760, 'jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx', 1),
(2, 1, 'oss', '阿里云OSS', 'https://oss-cn-hangzhou.aliyuncs.com', 'your-access-key', 'your-secret-key', 'auth-center-bucket', 'oss-cn-hangzhou', 0, 52428800, 'jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx,zip,rar', 0),
(3, 2, 'minio', '演示存储配置', 'http://demo-minio:9000', 'minioadmin', 'minioadmin', 'demo-auth-center', 'us-east-1', 1, 5242880, 'jpg,jpeg,png,pdf', 1);

-- ===============================================
-- 10. 告警规则数据初始化
-- ===============================================

-- 初始化告警规则
INSERT IGNORE INTO `sys_alert_rule` (`id`, `tenant_id`, `rule_name`, `rule_type`, `metric_name`, `operator`, `threshold_value`, `duration`, `alert_level`, `notification_channels`, `enabled`, `description`) 
VALUES 
(1, 1, 'CPU使用率告警', 'METRIC', 'cpu.usage', '>', 80.0, 300, 2, 'email,wechat', 1, 'CPU使用率超过80%持续5分钟告警'),
(2, 1, '内存使用率告警', 'METRIC', 'memory.usage', '>', 85.0, 300, 2, 'email,wechat', 1, '内存使用率超过85%持续5分钟告警'),
(3, 1, '磁盘使用率告警', 'METRIC', 'disk.usage', '>', 90.0, 300, 3, 'email,wechat,sms', 1, '磁盘使用率超过90%持续5分钟告警'),
(4, 1, '服务响应时间告警', 'METRIC', 'service.response_time', '>', 5000, 60, 2, 'email', 1, '服务响应时间超过5秒持续1分钟告警'),
(5, 1, '登录失败次数告警', 'LOG', 'login.fail_count', '>', 10, 300, 1, 'email', 1, '5分钟内登录失败次数超过10次告警');

-- ===============================================
-- 11. 示例消息数据初始化
-- ===============================================

-- 初始化示例消息
INSERT IGNORE INTO `sys_message` (`id`, `tenant_id`, `message_type`, `message_title`, `message_content`, `sender_id`, `sender_name`, `receiver_type`, `receiver_ids`, `priority`, `status`, `send_time`) 
VALUES 
(1, 1, 'SYSTEM', '系统维护通知', '系统将于今晚23:00-24:00进行维护，期间可能无法正常访问，请提前做好准备。', 1, '系统管理员', 1, '[2,3,4,5]', 2, 1, NOW()),
(2, 1, 'BUSINESS', '新功能上线通知', '用户管理模块新增批量导入功能，欢迎使用并提供宝贵意见。', 2, '用户管理员', 2, '[2,3]', 1, 1, NOW()),
(3, 2, 'NOTIFICATION', '欢迎使用演示系统', '欢迎使用认证中心演示系统，您可以体验系统的各项功能。', 4, '演示用户', 1, '[5]', 1, 1, NOW());

-- ===============================================
-- 12. 初始化完成确认
-- ===============================================

-- 显示初始化结果统计
SELECT '基础数据初始化完成!' AS '结果';

SELECT 
    COUNT(*) AS '用户数量',
    (SELECT COUNT(*) FROM sys_user WHERE tenant_id = 1) AS '默认租户用户',
    (SELECT COUNT(*) FROM sys_user WHERE tenant_id = 2) AS '演示租户用户'
FROM sys_user;

SELECT 
    COUNT(*) AS '角色数量',
    (SELECT COUNT(*) FROM sys_role WHERE tenant_id = 1) AS '默认租户角色',
    (SELECT COUNT(*) FROM sys_role WHERE tenant_id = 2) AS '演示租户角色'
FROM sys_role;

SELECT 
    COUNT(*) AS '菜单数量',
    (SELECT COUNT(*) FROM sys_menu WHERE tenant_id = 1) AS '默认租户菜单'
FROM sys_menu;

SELECT 
    COUNT(*) AS '客户端数量',
    (SELECT COUNT(*) FROM sys_client WHERE tenant_id = 1) AS '默认租户客户端',
    (SELECT COUNT(*) FROM sys_client WHERE tenant_id = 2) AS '演示租户客户端'
FROM sys_client;

SELECT 
    COUNT(*) AS '配置项数量',
    (SELECT COUNT(*) FROM sys_config WHERE tenant_id = 1) AS '默认租户配置',
    (SELECT COUNT(*) FROM sys_config WHERE tenant_id = 2) AS '演示租户配置'
FROM sys_config;

-- 显示初始化用户信息
SELECT 
    u.username AS '用户名',
    u.real_name AS '真实姓名',
    t.tenant_name AS '所属租户',
    r.role_name AS '角色名称'
FROM sys_user u
JOIN sys_tenant t ON u.tenant_id = t.id
JOIN sys_user_role ur ON u.id = ur.user_id
JOIN sys_role r ON ur.role_id = r.id
ORDER BY t.id, u.id;