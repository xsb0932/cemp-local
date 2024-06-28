package com.landleaf.oauth.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.oauth.domain.request.TenantBasicUpdateRequest;
import com.landleaf.oauth.domain.request.TenantCreateRequest;
import com.landleaf.oauth.domain.request.TenantTabulationRequest;
import com.landleaf.oauth.domain.request.TenantValidationRequest;
import com.landleaf.oauth.domain.response.TenantInfoResponse;
import com.landleaf.oauth.domain.response.TenantSelectiveResponse;
import com.landleaf.oauth.domain.response.TenantTabulationResponse;
import com.landleaf.oauth.domain.response.TenantValidationResponse;
import com.landleaf.oauth.service.TenantService;
import com.landleaf.operatelog.core.annotations.OperateLog;
import com.landleaf.operatelog.core.enums.ModuleTypeEnums;
import com.landleaf.operatelog.core.enums.OperateTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.landleaf.comm.constance.OauthConstant.DISABLE_STATUS;
import static com.landleaf.comm.constance.OauthConstant.ENABLE_STATUS;

/**
 * 租户接口层
 *
 * @author yue lin
 * @since 2023/6/1 9:36
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/tenant")
@Tag(name = "租户接口")
public class TenantController {

    private final TenantService tenantService;

    /**
     * 租户分页查询列表
     *
     * @param tenantTabulationRequest 参数
     * @return 结果集
     */
    @Operation(summary = "租户分页查询列表")
    @GetMapping("/tenants")
    public Response<IPage<TenantTabulationResponse>> searchTenantTabulation(TenantTabulationRequest tenantTabulationRequest) {
        return Response.success(tenantService.searchTenantTabulation(tenantTabulationRequest));
    }

    /**
     * 租户详情查询
     *
     * @param tenantId 租户ID
     * @return 结果集
     */
    @Operation(summary = "租户详情查询")
    @GetMapping("/basic/{tenantId}")
    public Response<TenantInfoResponse> searchTenantTabulation(@PathVariable Long tenantId) {
        return Response.success(tenantService.searchTenantBasicInfo(tenantId));
    }

    /**
     * 租户删除
     *
     * @param tenantId 租户主键
     * @return 结果集
     */
    @Operation(summary = "租户删除")
    @Parameter(name = "tenantId", description = "租户主键")
    @DeleteMapping("/{tenantId}")
    @OperateLog(module = ModuleTypeEnums.OAUTH, name = "租户删除", type = OperateTypeEnum.DELETE)
    public Response<Void> deleteTenant(@PathVariable Long tenantId) {
        tenantService.deleteTenant(tenantId);
        return Response.success();
    }

    /**
     * 平台管理员创建租户
     *
     * @param tenantCreateRequest 参数
     * @return 结果集
     */
    @Operation(summary = "平台管理员创建租户")
    @PostMapping("/creation")
    @OperateLog(module = ModuleTypeEnums.OAUTH, name = "创建租户", type = OperateTypeEnum.CREATE)
    public Response<Void> createTenant(@Validated @RequestBody TenantCreateRequest tenantCreateRequest) {
        tenantService.createTenant(tenantCreateRequest);
        return Response.success();
    }

    /**
     * 管理员变更租户信息
     *
     * @param tenantBasicUpdateRequest 参数
     * @return 结果集
     */
    @Operation(summary = "管理员变更租户信息")
    @PutMapping("/basic/change")
    @OperateLog(module = ModuleTypeEnums.OAUTH, name = "变更租户", type = OperateTypeEnum.UPDATE)
    public Response<Void> updateTenantBasic(@Validated @RequestBody TenantBasicUpdateRequest tenantBasicUpdateRequest) {
        tenantService.updateTenantBasic(tenantBasicUpdateRequest);
        return Response.success();
    }

    /**
     * 平台管理员禁用租户
     *
     * @param tenantId 参数
     * @return 结果集
     */
    @Operation(summary = "平台管理员禁用租户")
    @PutMapping("/disable/{tenantId}")
    @OperateLog(module = ModuleTypeEnums.OAUTH, name = "禁用租户", type = OperateTypeEnum.UPDATE)
    public Response<Void> disableTenant(@PathVariable Long tenantId) {
        tenantService.updateTenantStatus(tenantId, DISABLE_STATUS);
        return Response.success();
    }

    /**
     * 平台管理员启用租户
     *
     * @param tenantId 参数
     * @return 结果集
     */
    @Operation(summary = "平台管理员启用租户")
    @PutMapping("/enable/{tenantId}")
    @OperateLog(module = ModuleTypeEnums.OAUTH, name = "启用租户", type = OperateTypeEnum.UPDATE)
    public Response<Void> enableTenant(@PathVariable Long tenantId) {
        tenantService.updateTenantStatus(tenantId, ENABLE_STATUS);
        return Response.success();
    }

    /**
     * 租户选择框列表查询（平台管理员返回所有，租户管理员只返回本租户）
     *
     * @return 结果
     */
    @GetMapping("/tenants/selective")
    @Operation(summary = "租户选择框列表查询（平台管理员返回所有，租户管理员只返回本租户）")
    public Response<List<TenantSelectiveResponse>> searchTenantSelective() {
        return Response.success(tenantService.searchTenantSelective());
    }

    /**
     * 校验参数是否可用，true通过，false已存在
     * @param request   参数
     * @return  结果
     */
    @GetMapping("/param/validation")
    public Response<TenantValidationResponse> validationParam(TenantValidationRequest request) {
        return Response.success(tenantService.validationParam(request));
    }

}
