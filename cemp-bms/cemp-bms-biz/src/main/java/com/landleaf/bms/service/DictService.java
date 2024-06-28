package com.landleaf.bms.service;

import com.landleaf.bms.domain.request.*;
import com.landleaf.bms.domain.response.DictDataSelectiveResponse;
import com.landleaf.bms.domain.response.DictDetailsResponse;
import com.landleaf.bms.domain.response.DictTypeListResponse;
import com.landleaf.bms.domain.request.DictTypeListRequest;

import java.util.List;

/**
 * DictService
 *
 * @author 张力方
 * @since 2023/6/15
 **/
public interface DictService {

    /**
     * 查询字典列表
     *
     * @param request 查询条件
     * @return 字典列表
     */
    List<DictTypeListResponse> getDictTypeListResponse(DictTypeListRequest request);

    /**
     * 查询字典详情
     *
     * @param dictId 字典id
     * @return 字典详情
     */
    DictDetailsResponse getDictDetails(Long dictId);

    /**
     * 编辑字典基本数据
     *
     * @param request 参数
     */
    void modifyDictBase(DictBaseEditRequest request);

    /**
     * 新增字典数据（码值）
     *
     * @param request 参数
     */
    void addDictData(DictDataAddRequest request);

    /**
     * 编辑字典数据（码值）
     *
     * @param request 参数
     */
    void modifyDictData(DictDataEditRequest request);

    /**
     * 编辑字典数据（码值）
     *
     * @param dictDataId 字典数据id
     */
    void deleteDictData(Long dictDataId);

    /**
     * 字典数据（码值）排序
     *
     * @param request 请求参数
     */
    void modifyDictDataSort(DictDataSortRequest request);

    /**
     * 根据类型CODE查询下拉框
     *
     * @param dictTypeCode 类型
     * @return 结果
     */
    List<DictDataSelectiveResponse> selectDictDataSelective(String dictTypeCode);

    /**
     * 校验数据字典码值是否唯一
     * <p>
     * true 唯一， false 不唯一
     */
    boolean checkValueUnique(DictDataValueUniqueRequest request);
}
