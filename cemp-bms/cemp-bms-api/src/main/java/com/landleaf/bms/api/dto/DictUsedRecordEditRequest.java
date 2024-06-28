package com.landleaf.bms.api.dto;

import lombok.Data;

/**
 * 编辑
 *
 * @author 张力方
 * @since 2023/6/15
 **/
@Data
public class DictUsedRecordEditRequest {
    /**
     * 旧码值id
     */
    private Long oldDictDataId;

    /**
     * 新码值id
     */
    private Long newDictDataId;

    /**
     * 使用方唯一标识，比如 表名+id
     */
    private String uniqueCode;
}
