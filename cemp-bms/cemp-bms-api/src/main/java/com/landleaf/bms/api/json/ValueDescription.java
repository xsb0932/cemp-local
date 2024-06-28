package com.landleaf.bms.api.json;

import lombok.Data;

/**
 * 值描述JSON数据格式
 *
 * @author yue lin
 * @since 2023/6/25 11:11
 */
@Data
public class ValueDescription {

    /**
     * key值
     */
    private String key;

    /**
     * value值
     */
    private String value;

}
