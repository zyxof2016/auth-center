package com.auth.center.file.domain.enums;

/**
 * 文件分类枚举
 */
public enum FileCategory {
    
    /**
     * 头像
     */
    AVATAR("avatar", "头像"),
    
    /**
     * 文档
     */
    DOCUMENT("document", "文档"),
    
    /**
     * 图片
     */
    IMAGE("image", "图片"),
    
    /**
     * 视频
     */
    VIDEO("video", "视频"),
    
    /**
     * 其他
     */
    OTHER("other", "其他");
    
    private final String code;
    private final String description;
    
    FileCategory(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static FileCategory fromCode(String code) {
        for (FileCategory category : values()) {
            if (category.code.equals(code)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid file category code: " + code);
    }
}