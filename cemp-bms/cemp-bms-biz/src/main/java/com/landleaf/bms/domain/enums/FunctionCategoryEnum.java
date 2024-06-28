package com.landleaf.bms.domain.enums;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.Getter;

import java.util.Arrays;

/**
 * 功能类别
 *
 * @author yue lin
 * @since 2023/7/6 16:16
 */
public enum FunctionCategoryEnum {

    /**
     * 产品参数
     */
    PRODUCT_PARAMETER("01", "产品参数"),
    /**
     * 设备参数
     */
    DEVICE_PARAMETER("02", "设备参数"),
    /**
     * 设备属性
     */
    DEVICE_ATTRIBUTE("03", "设备属性"),
    /**
     * 设备事件
     */
    DEVICE_EVENT("04", "设备事件"),
    /**
     * 设备服务
     */
    DEVICE_SERVICE("05", "设备服务"),
    ;

    @Getter
    private final String value;

    @Getter
    private final String label;

    FunctionCategoryEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public static FunctionCategoryEnum convertValue(String value) {
        return Arrays.stream(FunctionCategoryEnum.values())
                .filter(it -> CharSequenceUtil.equals(it.value, value))
                .findFirst()
                .orElse(null);
    }

}
