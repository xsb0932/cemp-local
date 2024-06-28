package com.landleaf.bms.controller;

import com.landleaf.bms.domain.request.ProductFunctionIdentifierUniqueRequest;
import com.landleaf.bms.service.ProductFeatureManagementService;
import com.landleaf.comm.base.pojo.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 产品-功能管理接口
 *
 * @author yue lin
 * @since 2023/6/25 13:39
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/product/feature")
@Tag(name = "功能管理接口")
public class ProductFeatureManagementController {

    private final ProductFeatureManagementService productFeatureManagementService;

    /**
     * 校验功能标识符是否唯一
     * <p>
     * true 唯一， false 不唯一
     */
    @GetMapping("/identifier/unique")
    @Operation(summary = "校验功能标识符是否唯一", description = "true 唯一， false 不唯一")
    public Response<Boolean> checkIdentifierUnique(@Validated ProductFunctionIdentifierUniqueRequest request) {
        return Response.success(productFeatureManagementService.checkIdentifierUnique(request.getIdentifier(), request.getId(), request.getProductId()));
    }

}
