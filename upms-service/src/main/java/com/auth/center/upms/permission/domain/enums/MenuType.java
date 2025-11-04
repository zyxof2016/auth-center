package com.auth.center.role.domain.enums;

/**
 * 菜单类型枚举
 */
public enum MenuType {
    
    /**
     * 目录
     */
    DIRECTORY(1, "目录"),
    
    /**
     * 菜单
     */
    MENU(2, "菜单"),
    
    /**
     * 按钮
     */
    BUTTON(3, "按钮");
    
    private final int code;
    private final String description;
    
    MenuType(int code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static MenuType fromCode(int code) {
        for (MenuType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid menu type code: " + code);
    }
}