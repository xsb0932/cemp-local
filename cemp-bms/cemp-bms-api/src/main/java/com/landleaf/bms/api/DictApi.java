package com.landleaf.bms.api;

import com.landleaf.bms.api.dto.DictDataResponse;
import com.landleaf.bms.api.dto.DictUsedRecordEditRequest;
import com.landleaf.bms.api.dto.DictUsedRecordRequest;
import com.landleaf.bms.api.enums.ApiConstants;
import com.landleaf.comm.base.pojo.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Feign 服务 - 数据字典
 *
 * @author 张力方
 * @since 2023/6/16
 **/
@Tag(name = "Feign 服务 - 数据字典")
@FeignClient(name = ApiConstants.NAME)
public interface DictApi {

    /**
     * 新增数据字典使用记录
     *
     * @param request 数据字典使用记录
     */
    @PostMapping(ApiConstants.PREFIX + "/dict-used-record/add")
    @Operation(summary = "新增数据字典使用记录")
    Response<Void> addDictDataUsedRecord(@Validated @RequestBody DictUsedRecordRequest request);

    /**
     * 编辑数据字典使用记录
     *
     * @param request 数据字典使用记录
     */
    @PutMapping(ApiConstants.PREFIX + "/dict-used-record/edit")
    @Operation(summary = "编辑数据字典使用记录")
    Response<Void> editDictDataUsedRecord(@Validated @RequestBody DictUsedRecordEditRequest request);

    /**
     * 删除数据字典使用记录
     *
     * @param request 数据字典使用记录
     */
    @DeleteMapping(ApiConstants.PREFIX + "/dict-used-record/delete")
    @Operation(summary = "删除数据字典使用记录")
    Response<Void> deleteDictDataUsedRecord(@Validated @RequestBody DictUsedRecordRequest request);

    /**
     * 初始化租户数据字典
     *
     * @param tenantId 新租户id
     */
    @PostMapping(ApiConstants.PREFIX + "/dict/init")
    @Operation(summary = "初始化租户数据字典")
    Response<Void> initTenantDictData(@Validated @RequestParam("tenantId") Long tenantId);

    /**
     * 删除租户数据字典
     *
     * @param tenantId 新租户id
     */
    @DeleteMapping(ApiConstants.PREFIX + "/dict/delete/{tenantId}")
    @Operation(summary = "删除租户数据字典")
    Response<Void> deleteTenantDictData(@PathVariable("tenantId") Long tenantId);

    /**
     * 获取指定字典码值
     *
     * @param code 字典编码
     * @return 码值列表
     */
    @Operation(summary = "获取指定字典码值")
    @GetMapping(ApiConstants.PREFIX + "/dict/data")
    Response<List<DictDataResponse>> getDictDataList(@RequestParam("code") String code);
}
