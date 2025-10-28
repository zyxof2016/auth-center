package com.auth.center.common.exception;

/**
 * 错误码枚举
 * 
 * @author auth-center
 */
public enum ErrorCode {

    // ========== 系统级错误码 ==========
    SUCCESS("00000", "成功"),
    SYSTEM_ERROR("B0001", "系统执行出错"),
    SYSTEM_TIMEOUT("B0100", "系统执行超时"),
    SYSTEM_FLOW_LIMIT("B0210", "系统限流"),
    SYSTEM_DEGRADE("B0220", "系统功能降级"),
    SYSTEM_RESOURCE_ERROR("B0300", "系统资源异常"),

    // ========== 用户端错误码 ==========
    USER_ERROR("A0001", "用户端错误"),
    USER_LOGIN_ERROR("A0200", "用户登录异常"),
    USER_NOT_EXIST("A0201", "用户不存在"),
    USER_ACCOUNT_LOCKED("A0202", "用户账户被锁定"),
    USER_ACCOUNT_INVALID("A0203", "用户账户已作废"),
    USERNAME_OR_PASSWORD_ERROR("A0210", "用户名或密码错误"),
    USER_INPUT_ERROR("A0300", "用户输入错误"),
    REQUIRED_PARAM_MISSING("A0310", "缺少必填参数"),
    PARAM_TYPE_ERROR("A0320", "参数类型错误"),
    PARAM_FORMAT_ERROR("A0330", "参数格式错误"),
    PARAM_OUT_OF_RANGE("A0340", "参数值超出允许范围"),
    USER_UNAUTHORIZED("A0400", "用户未授权"),
    USER_ACCESS_DENIED("A0401", "访问权限不足"),
    USER_TOKEN_EXPIRED("A0410", "用户令牌已过期"),
    USER_TOKEN_INVALID("A0411", "用户令牌无效"),
    USER_REQUEST_EXPIRED("A0420", "用户请求已过期"),

    // ========== 业务级错误码 ==========
    BUSINESS_ERROR("B0001", "业务执行出错"),
    DATA_NOT_EXIST("B0100", "数据不存在"),
    DATA_ALREADY_EXIST("B0101", "数据已存在"),
    DATA_ACCESS_ERROR("B0200", "数据访问异常"),
    DATA_VALIDATION_ERROR("B0300", "数据验证失败"),
    OPERATION_TOO_FREQUENT("B0400", "操作过于频繁"),
    RESOURCE_NOT_FOUND("B0500", "资源不存在"),
    RESOURCE_ALREADY_EXIST("B0501", "资源已存在"),
    RESOURCE_ACCESS_DENIED("B0502", "资源访问被拒绝"),

    // ========== 第三方服务错误码 ==========
    THIRD_PARTY_ERROR("C0001", "调用第三方服务出错"),
    THIRD_PARTY_TIMEOUT("C0100", "第三方服务调用超时"),
    THIRD_PARTY_DEGRADE("C0200", "第三方服务降级"),
    MESSAGE_SERVICE_ERROR("C0300", "消息服务异常"),

    // ========== 认证授权错误码 ==========
    AUTH_ERROR("A1000", "认证授权错误"),
    AUTH_UNAUTHORIZED("A1001", "未授权访问"),
    AUTH_FORBIDDEN("A1002", "禁止访问"),
    AUTH_TOKEN_EXPIRED("A1003", "令牌已过期"),
    AUTH_TOKEN_INVALID("A1004", "令牌无效"),
    AUTH_REFRESH_TOKEN_EXPIRED("A1005", "刷新令牌已过期"),
    AUTH_CLIENT_INVALID("A1006", "客户端无效"),
    AUTH_GRANT_TYPE_UNSUPPORTED("A1007", "不支持的授权类型"),
    AUTH_SCOPE_INVALID("A1008", "权限范围无效"),
    AUTH_REDIRECT_URI_MISMATCH("A1009", "重定向URI不匹配"),

    // ========== 用户管理错误码 ==========
    USER_MANAGEMENT_ERROR("A2000", "用户管理错误"),
    USER_CREATE_FAILED("A2001", "用户创建失败"),
    USER_UPDATE_FAILED("A2002", "用户更新失败"),
    USER_DELETE_FAILED("A2003", "用户删除失败"),
    USER_STATUS_INVALID("A2004", "用户状态无效"),

    // ========== 角色权限错误码 ==========
    ROLE_MANAGEMENT_ERROR("A3000", "角色管理错误"),
    ROLE_NOT_EXIST("A3001", "角色不存在"),
    ROLE_ALREADY_EXIST("A3002", "角色已存在"),
    PERMISSION_DENIED("A3003", "权限不足"),
    MENU_NOT_EXIST("A3004", "菜单不存在"),

    // ========== 客户端管理错误码 ==========
    CLIENT_MANAGEMENT_ERROR("A4000", "客户端管理错误"),
    CLIENT_NOT_EXIST("A4001", "客户端不存在"),
    CLIENT_ALREADY_EXIST("A4002", "客户端已存在"),
    CLIENT_SECRET_INVALID("A4003", "客户端密钥无效"),

    // ========== 租户管理错误码 ==========
    TENANT_MANAGEMENT_ERROR("A5000", "租户管理错误"),
    TENANT_NOT_EXIST("A5001", "租户不存在"),
    TENANT_ALREADY_EXIST("A5002", "租户已存在"),
    TENANT_EXPIRED("A5003", "租户已过期"),
    TENANT_DISABLED("A5004", "租户已禁用"),

    // ========== 文件上传错误码 ==========
    FILE_UPLOAD_ERROR("A6000", "文件上传错误"),
    FILE_TOO_LARGE("A6001", "文件过大"),
    FILE_TYPE_UNSUPPORTED("A6002", "不支持的文件类型"),
    FILE_UPLOAD_FAILED("A6003", "文件上传失败"),

    // ========== 验证码错误码 ==========
    CAPTCHA_ERROR("A7000", "验证码错误"),
    CAPTCHA_EXPIRED("A7001", "验证码已过期"),
    CAPTCHA_INVALID("A7002", "验证码无效"),
    CAPTCHA_REQUIRED("A7003", "验证码必填"),

    // ========== 数据库错误码 ==========
    DATABASE_ERROR("D0001", "数据库错误"),
    DATABASE_CONNECTION_ERROR("D0002", "数据库连接错误"),
    DATABASE_TIMEOUT("D0003", "数据库操作超时"),
    DATABASE_DEADLOCK("D0004", "数据库死锁"),

    // ========== 缓存错误码 ==========
    CACHE_ERROR("E0001", "缓存错误"),
    CACHE_CONNECTION_ERROR("E0002", "缓存连接错误"),
    CACHE_TIMEOUT("E0003", "缓存操作超时"),
    CACHE_KEY_NOT_EXIST("E0004", "缓存键不存在"),

    // ========== 网络错误码 ==========
    NETWORK_ERROR("F0001", "网络错误"),
    NETWORK_TIMEOUT("F0002", "网络超时"),
    NETWORK_CONNECTION_REFUSED("F0003", "网络连接被拒绝"),
    NETWORK_HOST_UNREACHABLE("F0004", "网络主机不可达"),

    // ========== 未知错误码 ==========
    UNKNOWN_ERROR("Z9999", "未知错误");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 根据错误码获取错误码枚举
     */
    public static ErrorCode getByCode(String code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode;
            }
        }
        return UNKNOWN_ERROR;
    }

    /**
     * 判断错误码是否存在
     */
    public static boolean contains(String code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
}