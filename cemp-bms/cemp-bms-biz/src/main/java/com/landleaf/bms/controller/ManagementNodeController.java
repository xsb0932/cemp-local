package com.landleaf.bms.controller;

import com.landleaf.bms.domain.request.ManagementNodeAddRequest;
import com.landleaf.bms.domain.request.ManagementNodeCodeUniqueRequest;
import com.landleaf.bms.domain.request.ManagementNodeEditRequest;
import com.landleaf.bms.domain.request.ManagementNodeSortRequest;
import com.landleaf.bms.domain.response.ManagementNodeListResponse;
import com.landleaf.bms.domain.response.TenantManagementNodeListResponse;
import com.landleaf.bms.service.ManagementNodeService;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.operatelog.core.annotations.OperateLog;
import com.landleaf.operatelog.core.enums.ModuleTypeEnums;
import com.landleaf.operatelog.core.enums.OperateTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理节点相关接口
 *
 * @author 张力方
 * @since 2023/6/6
 **/
@RequiredArgsConstructor
@RestController
@RequestMapping("/management-node")
@Tag(name = "管理节点相关接口")
public class ManagementNodeController {
    private final ManagementNodeService managementNodeService;

    /**
     * 新增管理节点
     *
     * @param request 新管理节点信息
     */
    @PostMapping
    @Operation(summary = "新增管理节点")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "新增管理节点", type = OperateTypeEnum.CREATE)
    public Response<Void> addManagementNode(@RequestBody @Validated ManagementNodeAddRequest request) {
        managementNodeService.addManagementNode(request);
        return Response.success();
    }

    /**
     * 编辑管理节点
     *
     * @param request 编辑管理节点信息
     */
    @PutMapping
    @Operation(summary = "编辑管理节点")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "编辑管理节点", type = OperateTypeEnum.UPDATE)
    public Response<Void> editManagementNode(@RequestBody @Validated ManagementNodeEditRequest request) {
        managementNodeService.editManagementNode(request);
        return Response.success();
    }

    /**
     * 删除管理节点
     *
     * @param bizNodeId 管理节点业务id
     */
    @DeleteMapping("/{bizNodeId}")
    @Operation(summary = "删除管理节点")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "删除管理节点", type = OperateTypeEnum.DELETE)
    public Response<Void> deleteManagementNode(@PathVariable("bizNodeId") String bizNodeId) {
        managementNodeService.deleteManagementNode(bizNodeId);
        return Response.success();
    }

    /**
     * 校验编码是否唯一
     * <p>
     * true 唯一， false 不唯一
     *
     * @return true 唯一， false 不唯一
     */
    @PostMapping("/check-code-unique")
    @Operation(summary = "校验编码是否唯一", description = "true 唯一， false 不唯一")
    public Response<Boolean> checkCodeUnique(@RequestBody @Validated ManagementNodeCodeUniqueRequest request) {
        boolean unique = managementNodeService.checkCodeUnique(request);
        return Response.success(unique);
    }

    /**
     * 根据租户id获取管理节点列表
     *
     * @return 管理节点列表
     */
    @GetMapping("/list")
    @Operation(summary = "根据租户id获取管理节点列表")
    public Response<List<ManagementNodeListResponse>> getManagementNodeList(@RequestParam(required = false) Long tenantId) {
        List<ManagementNodeListResponse> managementNodeList = managementNodeService.getManagementNodeList(tenantId);
        return Response.success(managementNodeList);
    }

    /**
     * 根据租户id获取管理节点列表 - 简单结果集，用于用户授权
     *
     * @param tenantId       租户id
     * @param permissionType 权限类型，1 区域， 2 项目
     * @return 管理节点列表
     */
    @GetMapping("/tenant/list")
    @Operation(summary = "根据租户id获取管理节点列表 - 简单结果集，用于用户授权")
    public Response<List<TenantManagementNodeListResponse>> getTenantManagementNodeList(@RequestParam(required = false) Long tenantId, @RequestParam Short permissionType) {
        List<TenantManagementNodeListResponse> tenantManagementNodeList = managementNodeService.getTenantManagementNodeList(tenantId, permissionType);
        return Response.success(tenantManagementNodeList);
    }

    /**
     * 管理节点排序接口
     *
     * @param request 请求参数
     */
    @PutMapping("/sort")
    @Operation(summary = "管理节点排序接口")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "管理节点排序", type = OperateTypeEnum.UPDATE)
    public Response<Void> modifySort(@RequestBody @Validated ManagementNodeSortRequest request) {
        managementNodeService.modifySort(request);
        return Response.success();
    }
}
