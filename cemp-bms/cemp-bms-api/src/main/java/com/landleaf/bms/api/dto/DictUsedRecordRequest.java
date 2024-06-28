package com.landleaf.bms.api.dto;

import lombok.Data;

/**
 * 新增/删除
 *
 * @author 张力方
 * @since 2023/6/15
 **/
@Data
public class DictUsedRecordRequest {
    /**
     * 码值id
     */
    private Long dictDataId;

    /**
     * 使用方唯一标识，比如 表名+id
     */
    private String uniqueCode;
}
