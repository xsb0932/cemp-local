package com.landleaf.oauth.controller;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.oauth.domain.request.EnterpriseAdminChangeRequest;
import com.landleaf.oauth.domain.request.EnterpriseUpdateRequest;
import com.landleaf.oauth.domain.response.TenantInfoResponse;
import com.landleaf.oauth.service.TenantService;
import com.landleaf.operatelog.core.annotations.OperateLog;
import com.landleaf.operatelog.core.enums.ModuleTypeEnums;
import com.landleaf.operatelog.core.enums.OperateTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 企业信息接口
 *
 * @author yue lin
 * @since 2023/6/9 13:32
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/enterprise")
@Tag(name = "企业信息接口")
public class EnterpriseController {

    private final TenantService tenantService;

    /**
     * 变更企业基本信息
     *
     * @param request 变更内容
     * @return 结果
     */
    @PutMapping("/basic")
    @Operation(summary = "变更企业基本信息")
    @OperateLog(module = ModuleTypeEnums.OAUTH, name = "变更企业基本信息", type = OperateTypeEnum.UPDATE)
    public Response<Void> updateEnterprise(@Validated @RequestBody EnterpriseUpdateRequest request) {
        tenantService.updateEnterprise(request);
        return Response.success();
    }

    /**
     * 根据租户ID查询企业的基本信息(不传参则返回当前账号所在企业的信息)
     *
     * @return 结果
     */
    @GetMapping("/basic")
    @Operation(summary = "根据租户ID查询企业的基本信息(不传参则返回当前账号所在企业的信息)")
    public Response<TenantInfoResponse> searchEnterpriseBasic(@RequestParam(required = false) Long tenantId) {
        return Response.success(tenantService.searchEnterpriseBasic(tenantId));
    }

    /**
     * 变更企业管理员
     *
     * @param request 参数
     * @return 结果
     */
    @PutMapping("/admin/change")
    @Operation(summary = "变更企业管理员")
    @OperateLog(module = ModuleTypeEnums.OAUTH, name = "变更企业管理员", type = OperateTypeEnum.UPDATE)
    public Response<Void> changeEnterpriseAdmin(@Validated @RequestBody EnterpriseAdminChangeRequest request) {
        tenantService.changeEnterpriseAdmin(request);
        return Response.success();
    }

}
