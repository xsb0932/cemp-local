package com.landleaf.bms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.enums.FunctionCategoryEnum;
import com.landleaf.bms.domain.request.*;
import com.landleaf.bms.domain.response.*;
import com.landleaf.bms.service.CategoryService;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.web.validation.Create;
import com.landleaf.web.validation.Update;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 品类管理接口
 *
 * @author yue lin
 * @since 2023/7/3 15:40
 */
@SuppressWarnings("all")
@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
@Tag(name = "品类管理接口")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 目录创建
     *
     * @param request 参数
     * @return 结果
     */
    @PostMapping("/catalogue")
    public Response<Void> createCatalogue(@Validated(Create.class) @RequestBody CatalogueChangeRequest request) {
        categoryService.createCatalogue(request);
        return Response.success();
    }

    /**
     * 目录更新
     *
     * @param request 参数
     * @return 结果
     */
    @PutMapping("/catalogue")
    public Response<Void> updateCatalogue(@Validated(Update.class) @RequestBody CatalogueChangeRequest request) {
        categoryService.updateCatalogue(request);
        return Response.success();
    }

    /**
     * 目录名称校验是否可用
     *
     * @param id   id
     * @param name 名称
     * @return
     */
    @GetMapping("/catalogue/validation")
    public Response<Boolean> catalogueValidation(@RequestParam(required = false) Long id,
                                                 @RequestParam(required = true) String name) {
        return Response.success(categoryService.catalogueValidation(id, name));
    }

    /**
     * 目录删除
     *
     * @param catalogueId 目录ID
     * @return 结果
     */
    @DeleteMapping("/catalogue/{catalogueId}")
    public Response<Void> deleteCatalogue(@PathVariable Long catalogueId) {
        categoryService.deleteCatalogue(catalogueId);
        return Response.success();
    }

    /**
     * 品类名称校验是否可用
     *
     * @param id   id
     * @param name 名称
     * @return
     */
    @GetMapping("/category/validation")
    public Response<Boolean> categoryValidation(@RequestParam(required = false) Long id,
                                                @RequestParam(required = true) String name) {
        return Response.success(categoryService.categoryValidation(id, name));
    }

    /**
     * 品类创建
     *
     * @param request 参数
     * @return 品类业务id
     */
    @PostMapping("/category")
    public Response<String> createCategory(@Validated(Create.class) @RequestBody CategoryChangeRequest request) {
        return Response.success(categoryService.createCategory(request));
    }

    /**
     * 品类更新
     *
     * @param request 参数
     * @return 结果
     */
    @PutMapping("/category")
    public Response<String> updateCategory(@Validated(Update.class) @RequestBody CategoryChangeRequest request) {
        return Response.success(categoryService.updateCategory(request));
    }

    /**
     * 品类详情
     *
     * @param categoryBizId 品类BizId
     * @return 结果
     */
    @GetMapping("/category/{categoryBizId}")
    public Response<CategoryInfoResponse> searchCategoryInfo(@PathVariable String categoryBizId) {
        return Response.success(categoryService.searchCategoryInfo(categoryBizId));
    }

    /**
     * 品类删除
     *
     * @param categoryBizId 品类BizId
     * @return 结果
     */
    @DeleteMapping("/category/{categoryBizId}")
    public Response<Void> deleteCategory(@PathVariable String categoryBizId) {
        categoryService.deleteCategory(categoryBizId);
        return Response.success();
    }

    /**
     * 添加功能到品类中
     *
     * @param request 参数
     * @return 结果
     */
    @PostMapping("/function/addition")
    public Response<Void> addFeatureToCategory(@Validated @RequestBody OperateFeatureFromCategoryRequest request) {
        categoryService.addFeatureToCategory(request);
        return Response.success();
    }

    /**
     * 从品类中删除功能
     *
     * @param request 参数
     * @return 结果
     */
    @PutMapping("/function/omission")
    public Response<Void> deleteFeatureFromCategory(@Validated @RequestBody OperateFeatureFromCategoryRequest request) {
        categoryService.deleteFeatureFromCategory(request);
        return Response.success();
    }

    /**
     * 查询目录品类树状结构（目录+品类）
     *
     * @param containCategory 是否包含品类（默认为true）
     * @param containTop      是否包含顶级全部目录
     * @return 结果
     */
    @GetMapping("/category-catalogue/tree")
    public Response<List<CategoryTreeResponse>> searchCategoryCatalogueTree(@RequestParam(defaultValue = "true", required = false) Boolean containCategory,
                                                                            @RequestParam(defaultValue = "true", required = false) Boolean containTop) {
        return Response.success(categoryService.searchCategoryCatalogueTree(containCategory, containTop));
    }

    /**
     * 校验编码是否唯一（true可用，false不可用）
     *
     * @param code 编码
     * @param id   id
     * @return 结果
     */
    @GetMapping("/code/unique")
    public Response<Boolean> checkCodeUnique(@RequestParam() String code,
                                             @RequestParam(required = false) Long id) {
        return Response.success(categoryService.checkCodeUnique(code, id));
    }

    /**
     * 获取待添加的设备属性
     *
     * @param bizId 品类业务ID
     * @return 结果
     */
    @GetMapping("/candidate/device-attribute")
    public Response<Page<DeviceAttributeTabulationResponse>> searchCandidateDeviceAttribute(CategoryFeatureQueryRequest request) {
        request.setFunctionCategory(FunctionCategoryEnum.DEVICE_ATTRIBUTE.getValue());
        return Response.success((Page<DeviceAttributeTabulationResponse>) categoryService.searchCandidateData(request));
    }

    /**
     * 获取品类的设备属性
     *
     * @param request 参数
     * @return 结果
     */
    @GetMapping("/device-attribute")
    public Response<Page<DeviceAttributeTabulationResponse>> searchDeviceAttribute(CategoryFeatureQueryRequest request) {
        request.setFunctionCategory(FunctionCategoryEnum.DEVICE_ATTRIBUTE.getValue());
        return Response.success((Page<DeviceAttributeTabulationResponse>) categoryService.searchFunctionPage(request));
    }

    /**
     * 品类的设备属性变更
     *
     * @param request   参数
     * @return  结果
     */
    @PutMapping("/device-attribute")
    public Response<Void> updateDeviceAttribute(@RequestBody @Validated CategoryFeatureChangeRequest request) {
        request.setFunctionCategory(FunctionCategoryEnum.DEVICE_ATTRIBUTE.getValue());
        categoryService.functionCategoryChange(request);
        return Response.success();
    }

    /**
     * 获取待添加的设备事件
     *
     * @param bizId 品类业务ID
     * @return 结果
     */
    @GetMapping("/candidate/device-event")
    public Response<Page<DeviceEventTabulationResponse>> searchCandidateDeviceEvent(CategoryFeatureQueryRequest request) {
        request.setFunctionCategory(FunctionCategoryEnum.DEVICE_EVENT.getValue());
        return Response.success((Page<DeviceEventTabulationResponse>) categoryService.searchCandidateData(request));
    }

    /**
     * 获取品类的设备事件
     *
     * @param request 参数
     * @return 结果
     */
    @GetMapping("/device-event")
    public Response<Page<DeviceEventTabulationResponse>> searchDeviceEvent(CategoryFeatureQueryRequest request) {
        request.setFunctionCategory(FunctionCategoryEnum.DEVICE_EVENT.getValue());
        return Response.success((Page<DeviceEventTabulationResponse>) categoryService.searchFunctionPage(request));
    }

    /**
     * 品类的设备事件变更
     *
     * @param request   参数
     * @return  结果
     */
    @PutMapping("/device-event")
    public Response<Void> updateDeviceEvent(@RequestBody @Validated CategoryFeatureChangeRequest request) {
        request.setFunctionCategory(FunctionCategoryEnum.DEVICE_EVENT.getValue());
        categoryService.functionCategoryChange(request);
        return Response.success();
    }

    /**
     * 获取待添加的设备参数
     *
     * @param bizId 品类业务ID
     * @return 结果
     */
    @GetMapping("/candidate/device-parameter")
    public Response<Page<DeviceParameterTabulationResponse>> searchCandidateDeviceParameter(CategoryFeatureQueryRequest request) {
        request.setFunctionCategory(FunctionCategoryEnum.DEVICE_PARAMETER.getValue());
        return Response.success((Page<DeviceParameterTabulationResponse>) categoryService.searchCandidateData(request));
    }

    /**
     * 获取品类的设备参数
     *
     * @param request 参数
     * @return 结果
     */
    @GetMapping("/device-parameter")
    public Response<Page<DeviceParameterTabulationResponse>> searchDeviceParameter(CategoryFeatureQueryRequest request) {
        request.setFunctionCategory(FunctionCategoryEnum.DEVICE_PARAMETER.getValue());
        return Response.success((Page<DeviceParameterTabulationResponse>) categoryService.searchFunctionPage(request));
    }

    /**
     * 品类的设备参数变更
     *
     * @param request   参数
     * @return  结果
     */
    @PutMapping("/device-parameter")
    public Response<Void> updateDeviceParameter(@RequestBody @Validated CategoryFeatureChangeRequest request) {
        request.setFunctionCategory(FunctionCategoryEnum.DEVICE_PARAMETER.getValue());
        categoryService.functionCategoryChange(request);
        return Response.success();
    }

    /**
     * 获取待添加的设备服务
     *
     * @param bizId 品类业务ID
     * @return 结果
     */
    @GetMapping("/candidate/device-service")
    public Response<Page<DeviceServiceTabulationResponse>> searchCandidateDeviceService(CategoryFeatureQueryRequest request) {
        request.setFunctionCategory(FunctionCategoryEnum.DEVICE_SERVICE.getValue());
        return Response.success((Page<DeviceServiceTabulationResponse>) categoryService.searchCandidateData(request));
    }

    /**
     * 获取品类的设备服务
     *
     * @param request 参数
     * @return 结果
     */
    @GetMapping("/device-service")
    public Response<Page<DeviceServiceTabulationResponse>> searchDeviceService(CategoryFeatureQueryRequest request) {
        request.setFunctionCategory(FunctionCategoryEnum.DEVICE_SERVICE.getValue());
        return Response.success((Page<DeviceServiceTabulationResponse>) categoryService.searchFunctionPage(request));
    }

    /**
     * 品类的设备服务变更
     *
     * @param request   参数
     * @return  结果
     */
    @PutMapping("/device-service")
    public Response<Void> updateDeviceService(@RequestBody @Validated CategoryFeatureChangeRequest request) {
        request.setFunctionCategory(FunctionCategoryEnum.DEVICE_SERVICE.getValue());
        categoryService.functionCategoryChange(request);
        return Response.success();
    }

    /**
     * 获取品类的产品参数
     *
     * @param request 参数
     * @return 结果
     */
    @GetMapping("/product-parameter")
    public Response<Page<ProductParameterTabulationResponse>> searchProductParameter(CategoryFeatureQueryRequest request) {
        request.setFunctionCategory(FunctionCategoryEnum.PRODUCT_PARAMETER.getValue());
        return Response.success((Page<ProductParameterTabulationResponse>) categoryService.searchFunctionPage(request));
    }

    /**
     * 品类的产品参数变更
     *
     * @param request   参数
     * @return  结果
     */
    @PutMapping("/product-parameter")
    public Response<Void> updateProductParameter(@RequestBody @Validated CategoryFeatureChangeRequest request) {
        request.setFunctionCategory(FunctionCategoryEnum.PRODUCT_PARAMETER.getValue());
        categoryService.functionCategoryChange(request);
        return Response.success();
    }

    /**
     * 获取待添加的产品参数
     *
     * @param bizId 品类业务ID
     * @return 结果
     */
    @GetMapping("/candidate/product-parameter")
    public Response<Page<ProductParameterTabulationResponse>> searchCandidateProductParameter(CategoryFeatureQueryRequest request) {
        request.setFunctionCategory(FunctionCategoryEnum.PRODUCT_PARAMETER.getValue());
        return Response.success((Page<ProductParameterTabulationResponse>) categoryService.searchCandidateData(request));
    }

}
