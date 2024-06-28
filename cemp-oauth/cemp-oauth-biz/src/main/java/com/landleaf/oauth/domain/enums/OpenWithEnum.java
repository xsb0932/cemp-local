package com.landleaf.oauth.domain.enums;

import lombok.Getter;

/**
 * 打开方式
 *
 * @author yue lin
 * @since 2023/8/1 11:33
 */
public enum OpenWithEnum {

    /**
     * 默认
     */
    DEFAULT("0", "默认"),
    /**
     * 新窗口
     */
    NEW_WINDOW("1", "新窗口")
    ;

    @Getter
    private final String value;
    @Getter
    private final String label;

    OpenWithEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }
}
