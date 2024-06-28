package com.landleaf.bms.api;

import com.landleaf.bms.api.dto.*;
import com.landleaf.bms.api.enums.ApiConstants;
import com.landleaf.comm.base.pojo.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Feign 服务 - 产品
 *
 * @author 张力方
 * @since 2023/7/26
 **/
@Tag(name = "Feign 服务 - 产品")
@FeignClient(name = ApiConstants.NAME)
public interface ProductApi {
    /**
     * 查询产品设备属性
     *
     * @param bizProductIds 产品ids
     * @return 产品设备属性
     */
    @GetMapping(ApiConstants.PREFIX + "/product/device-attr")
    @Operation(summary = "查询产品设备属性")
    Response<Map<String, List<ProductDeviceAttrResponse>>> getProjectAttrs(@RequestParam("bizProductIds") List<String> bizProductIds);

    /**
     * 产品属性枚举
     *
     * @param categoryId 品类id
     * @return 产品属性枚举
     */
    @GetMapping(ApiConstants.PREFIX + "/product/device-attr-map")
    @Operation(summary = "查询产品设备属性")
    Response<List<ProductDeviceAttrMapResponse>> getProductAttrsMap(@RequestParam("categoryId") String categoryId);

    /**
     * 产品属性枚举
     *
     * @param bizProductIds 品类ids
     * @return 产品属性枚举
     */
    @GetMapping(ApiConstants.PREFIX + "/product/device-attr-by-prod-id")
    @Operation(summary = "查询产品设备属性")
    Response<Map<String, List<ProductDeviceAttrMapResponse>>> getProductAttrsMapByProdId(@RequestParam("bizProductId") List<String> bizProductIds);

    /**
     * 根据产品id查询所有枚举属性
     *
     * @param productId 产品id
     * @return 产品属性枚举
     */
    @GetMapping(ApiConstants.PREFIX + "/product/enum/attr")
    @Operation(summary = "根据产品id查询所有枚举属性")
    Response<List<String>> getProductEnumAttrs(@RequestParam("productId") Long productId);

    /**
     * 根据品类查询所有枚举属性
     *
     * @param bizCategoryId 品类业务id
     * @return 产品属性枚举
     */
    @GetMapping(ApiConstants.PREFIX + "/category/enum/attr")
    @Operation(summary = "根据品类查询所有枚举属性")
    Response<List<String>> getCategoryEnumAttrs(@RequestParam("bizCategoryId") String bizCategoryId);

    /**
     * 查询产品告警
     *
     * @param bizProductId 产品id
     * @return 产品设备告警
     */
    @GetMapping(ApiConstants.PREFIX + "/product/device-alarm")
    @Operation(summary = "查询产品设备属性")
    Response<List<ProductAlarmConfListResponse>> getProductAlarm(@RequestParam("bizProductId") String bizProductId);

    /**
     * 根据ID查询产品
     *
     * @param id 产品id
     * @return
     */
    @GetMapping(ApiConstants.PREFIX + "/product/detail")
    @Operation(summary = "根据ID查询产品")
    Response<ProductDetailResponse> getProductDetail(@RequestParam("bizProductIds") Long id);

    /**
     * @param bizProductIds 产品id
     * @return
     */
    @GetMapping(ApiConstants.PREFIX + "/product/getByBizids")
    @Operation(summary = "根据产品业务id查询")
    Response<List<ProductDetailResponse>> getByBizids(@RequestParam("bizProductIds") List<String> bizProductIds);


    /**
     * @param productId  产品id
     * @param identifier code
     * @return
     */
    @GetMapping(ApiConstants.PREFIX + "/product/getParameter")
    @Operation(summary = "查询产品参数")
    Response<ProductDeviceParameterResponse> getParameter(@RequestParam("productId") Long productId, @RequestParam("identifier") String identifier);

    /**
     * @param productId 产品id
     * @return
     */
    @GetMapping(ApiConstants.PREFIX + "/product/getParameterByProdId")
    @Operation(summary = "查询产品参数")
    Response<List<ProductDeviceParameterListResponse>> getParameterByProdId(@RequestParam("productId") Long productId);

    /**
     * 查询产品设备属性
     *
     * @param productId 产品id
     * @return 产品设备属性
     */
    @GetMapping(ApiConstants.PREFIX + "/product/getAttrsByProdId")
    @Operation(summary = "查询产品设备属性")
    Response<List<ProductDeviceAttributeListResponse>> getAttrsByProdId(@RequestParam("productId") Long productId);

    /**
     * 根据prodId，获取对应的服务信息
     *
     * @param productId 产品编号
     * @return 结果
     */
    @Operation(summary = "根据设备ID查询设备信息")
    @PostMapping(ApiConstants.PREFIX + "/product/getServiceByProdId")
    Response<List<ProductDeviceServiceListResponse>> getServiceByProdId(@RequestParam("productId") Long productId);

    /**
     * 根据bizProdId，获取对应的服务信息
     *
     * @param bizProdId 产品编号
     * @return 结果
     */
    @Operation(summary = "根据设备ID查询设备信息")
    @PostMapping(ApiConstants.PREFIX + "/product/getServiceByBizProdId")
    Response<List<ProductDeviceServiceListResponse>> getServiceByBizProdId(@RequestParam("bizProdId") String bizProdId);

    /**
     * 根据productId，获取对应的事件信息
     *
     * @return 结果
     */
    @Operation(summary = "根据productId，获取对应的事件信息")
    @PostMapping(ApiConstants.PREFIX + "/product/getEventByProdId")
    Response<List<ProductDeviceEventListResponse>> getEventByProdId(@RequestParam("productId") String bizProductId);
}
