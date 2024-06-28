package com.landleaf.file.domain.enums;

/**
 * 存储文件类型
 *
 * @author 仔鸡
 * @since 1.0-SNAPSHOT
 */
public enum StoreFileType {

    /**
     * 图片类
     */
    IMAGE("image"),
    /**
     * 文件类
     */
    FILE("file"),
    /**
     * 其他
     */
    OTHER("other");

    private String path;

    StoreFileType(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public StoreFileType setPath(String path) {
        this.path = path;
        return this;
    }
}
