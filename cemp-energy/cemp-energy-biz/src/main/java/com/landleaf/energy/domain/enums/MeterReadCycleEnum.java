package com.landleaf.energy.domain.enums;

import com.landleaf.comm.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 抄表周期类型
 */
@Getter
@AllArgsConstructor
public enum MeterReadCycleEnum {
    auto("0", "自动"),
    day("1", "天"),
    month("2", "月"),
    hour("3", "时");

    /**
     * 值
     */
    private final String code;
    /**
     * 描述
     */
    private final String name;

    public static MeterReadCycleEnum ofCode(String code) {
        for (MeterReadCycleEnum value : values()) {
            if (Objects.equals(value.getCode(), code)) {
                return value;
            }
        }
        throw new BusinessException("抄表周期类型错误");
    }
}
