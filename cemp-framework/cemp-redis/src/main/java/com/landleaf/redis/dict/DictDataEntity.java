package com.landleaf.redis.dict;

import lombok.Data;

/**
 * 字典数据表（码值）
 *
 * @author 张力方
 * @since 2023/6/15
 **/
@Data
public class DictDataEntity {
    /**
     * 字典数据id
     */
    private Long id;

    /**
     * 字典类型id
     */
    private Long dictId;

    /**
     * 字典编码
     */
    private String dictCode;

    /**
     * 字典数据码值
     */
    private String value;

    /**
     * 字典数据中文描述
     */
    private String label;

    /**
     * 字典数据状态
     */
    private Integer status;

    /**
     * 字典数据顺序
     */
    private Integer sort;

    /**
     * 字典数据默认状态
     */
    private Integer isDefault;
}
