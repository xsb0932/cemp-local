package com.landleaf.bms.api.dto;

import lombok.Data;

/**
 * 字典数据-选择框返回
 *
 * @author yue lin
 * @since 2023/6/16 17:34
 */
@Data
public class DictDataResponse {

    /**
     * id
     */
    private Long id;

    /**
     * 编码
     */
    private String value;

    /**
     * 描述
     */
    private String label;

}
