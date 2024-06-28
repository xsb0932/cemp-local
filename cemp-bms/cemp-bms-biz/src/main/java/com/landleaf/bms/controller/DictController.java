package com.landleaf.bms.controller;

import com.landleaf.bms.domain.request.*;
import com.landleaf.bms.domain.response.DictDataSelectiveResponse;
import com.landleaf.bms.domain.response.DictDetailsResponse;
import com.landleaf.bms.domain.response.DictTypeListResponse;
import com.landleaf.bms.service.DictService;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.bms.domain.request.DictTypeListRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据字典相关接口
 *
 * @author 张力方
 * @since 2023/6/15
 **/
@RequiredArgsConstructor
@RestController
@RequestMapping("/dict")
@Tag(name = "数据字典相关接口")
public class DictController {

    private final DictService dictService;

    /**
     * 查询字典列表
     *
     * @param request 查询条件
     * @return 字典列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询字典列表")
    public Response<List<DictTypeListResponse>> getDictTypeListResponse(DictTypeListRequest request) {
        List<DictTypeListResponse> dictTypeListResponse = dictService.getDictTypeListResponse(request);
        return Response.success(dictTypeListResponse);
    }

    /**
     * 查询字典详情
     *
     * @param dictId 字典id
     * @return 字典详情
     */
    @GetMapping("/details/{dictId}")
    @Operation(summary = "查询字典详情")
    public Response<DictDetailsResponse> getDictDetails(@PathVariable("dictId") Long dictId) {
        DictDetailsResponse dictDetails = dictService.getDictDetails(dictId);
        return Response.success(dictDetails);
    }

    /**
     * 编辑字典基本数据
     *
     * @param request 参数
     */
    @PutMapping
    @Operation(summary = "编辑字典基本数据")
    public Response<Void> modifyDictBase(@RequestBody @Validated DictBaseEditRequest request) {
        dictService.modifyDictBase(request);
        return Response.success();
    }

    /**
     * 新增字典数据（码值）
     *
     * @param request 参数
     */
    @PostMapping("/data")
    @Operation(summary = "新增字典数据（码值）")
    public Response<Void> addDictData(@RequestBody @Validated DictDataAddRequest request) {
        dictService.addDictData(request);
        return Response.success();
    }

    /**
     * 编辑字典数据（码值）
     *
     * @param request 参数
     */
    @PutMapping("/data")
    @Operation(summary = "编辑字典数据（码值）")
    public Response<Void> modifyDictData(@RequestBody @Validated DictDataEditRequest request) {
        dictService.modifyDictData(request);
        return Response.success();
    }

    /**
     * 删除字典数据（码值）
     *
     * @param dictDataId 字典数据id
     */
    @DeleteMapping("/data/{dictDataId}")
    @Operation(summary = "删除字典数据（码值）")
    public Response<Void> deleteDictData(@PathVariable Long dictDataId) {
        dictService.deleteDictData(dictDataId);
        return Response.success();
    }

    /**
     * 字典数据（码值）排序
     *
     * @param request 请求参数
     */
    @PutMapping("/data/sort")
    @Operation(summary = "字典数据（码值）排序")
    public Response<Void> modifyDictDataSort(@RequestBody @Validated DictDataSortRequest request) {
        dictService.modifyDictDataSort(request);
        return Response.success();
    }

    /**
     * 根据类型CODE查询下拉框
     *
     * @param dictTypeCode 类型
     * @return 结果
     */
    @GetMapping("/data/selective")
    @Operation(summary = "根据类型CODE查询下拉框")
    public Response<List<DictDataSelectiveResponse>> selectDictDataSelective(@RequestParam String dictTypeCode) {
        return Response.success(dictService.selectDictDataSelective(dictTypeCode));
    }

    /**
     * 校验数据字典码值是否唯一
     * <p>
     * true 唯一， false 不唯一
     */
    @PostMapping("/check-value-unique")
    @Operation(summary = "校验数据字典码值是否唯一", description = "true 唯一， false 不唯一")
    public Response<Boolean> checkCodeUnique(@RequestBody @Validated DictDataValueUniqueRequest request) {
        boolean unique = dictService.checkValueUnique(request);
        return Response.success(unique);
    }
}
