package com.landleaf.messaging.api.dto;

import lombok.Data;

@Data
public class FunctionParameter {

    /**
     * 字段标识符
     */
    private String identifier;

    /**
     * 数据值
     */
    private Object value;
}
