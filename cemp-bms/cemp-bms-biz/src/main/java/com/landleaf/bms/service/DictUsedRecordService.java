package com.landleaf.bms.service;

import com.landleaf.bms.api.dto.DictUsedRecordEditRequest;
import com.landleaf.bms.api.dto.DictUsedRecordRequest;

/**
 * DictUsedRecordService
 *
 * @author 张力方
 * @since 2023/6/16
 **/
public interface DictUsedRecordService {

    /**
     * 判断字典码值是否在使用中
     *
     * @param dictDataId 字典码值id
     * @return true 使用中
     */
    boolean inUsed(Long dictDataId);

    /**
     * 新增一条使用记录
     *
     * @param request 码值id
     */
    void addUsedRecord(DictUsedRecordRequest request);

    /**
     * 编辑一条使用记录
     *
     * @param request 码值id
     */
    void editUsedRecord(DictUsedRecordEditRequest request);

    /**
     * 删除一条使用记录
     *
     * @param request 码值id
     */
    void deleteUsedRecord(DictUsedRecordRequest request);
}
