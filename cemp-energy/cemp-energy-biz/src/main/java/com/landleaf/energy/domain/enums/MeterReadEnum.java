package com.landleaf.energy.domain.enums;

import com.landleaf.comm.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 抄表方式类型
 */
@Getter
@AllArgsConstructor
public enum MeterReadEnum {
    auto("01", "远程抄表"),
    manual("02", "手动抄表");

    /**
     * 值
     */
    private final String code;
    /**
     * 描述
     */
    private final String name;

    public static MeterReadEnum ofCode(String code) {
        for (MeterReadEnum value : values()) {
            if (Objects.equals(value.getCode(), code)) {
                return value;
            }
        }
        throw new BusinessException("抄表方式类型错误");
    }
}
