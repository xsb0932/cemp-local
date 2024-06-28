package com.landleaf.bms.api;

import com.landleaf.bms.api.dto.CategoryDeviceAttributeResponse;
import com.landleaf.bms.api.dto.CategoryDeviceParameterResponse;
import com.landleaf.bms.api.dto.ProjectBizCategoryResponse;
import com.landleaf.bms.api.enums.ApiConstants;
import com.landleaf.comm.base.pojo.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 品类相关
 *
 * @author 张力方
 * @since 2023/7/20
 **/
@Tag(name = "Feign 服务 - 品类相关")
@FeignClient(name = ApiConstants.NAME)
public interface CategoryApi {
    /**
     * 查询品类业务id
     *
     * @param code 品类编码
     * @return 品类业务id
     */
    @GetMapping(ApiConstants.PREFIX + "/category/bizId")
    @Operation(summary = "查询品类业务id")
    Response<String> getBizCategoryId(@RequestParam("code") String code);

    @PostMapping(ApiConstants.PREFIX + "/category/biz-id-list")
    @Operation(summary = "查询品类业务id")
    Response<List<String>> getBizCategoryIdList(@RequestBody List<String> codes);


    /**
     * 根据品类业务ID查询品类（传递空则查询所有）
     *
     * @param bizIds 品类业务ID
     * @return 结果
     */
    @PostMapping(ApiConstants.PREFIX + "/category/categories/basic")
    @Operation(summary = "根据品类业务ID查询品类（传递空则查询所有）")
    Response<List<ProjectBizCategoryResponse>> searchCategoryByBizId(@RequestBody List<String> bizIds);

    /**
     * 根据品类业务ID查询品设备参数
     *
     * @param bizId 品类业务ID
     * @return 结果
     */
    @GetMapping(ApiConstants.PREFIX + "/category/device-parameter/basic")
    @Operation(summary = "根据品类业务ID查询品设备参数")
    Response<List<CategoryDeviceParameterResponse>> searchDeviceParameterByBizId(@RequestParam("bizId") String bizId);

    /**
     * 根据品类业务ID查询品设备参数
     *
     * @param bizId 品类业务ID
     * @return 结果
     */
    @GetMapping(ApiConstants.PREFIX + "/category/device-attribute/basic")
    @Operation(summary = "根据品类业务ID查询品设备参数")
    Response<List<CategoryDeviceAttributeResponse>> searchDeviceAttributeByBizId(@RequestParam("bizId") String bizId);

}
