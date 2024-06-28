package com.landleaf.bms.api;

import com.landleaf.bms.api.dto.AreaManageNodeResponse;
import com.landleaf.bms.api.dto.ManagementNodeRootCreateRequest;
import com.landleaf.bms.api.enums.ApiConstants;
import com.landleaf.comm.base.pojo.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理节点 - Api
 *
 * @author 张力方
 * @since 2023/6/7
 **/
@Tag(name = "Feign 服务 - 管理节点")
@FeignClient(name = ApiConstants.NAME)
public interface ManagementNodeApi {
    /**
     * 创建租户根管理节点
     *
     * @param request 租户信息
     */
    @PostMapping(ApiConstants.PREFIX + "/management-node/tenant/root")
    @Operation(summary = "创建租户根管理节点")
    Response<Void> createTenantRootNode(@Validated @RequestBody ManagementNodeRootCreateRequest request);

    /**
     * 删除租户下管理节点
     *
     * @param tenantId 租户id
     */
    @DeleteMapping(ApiConstants.PREFIX + "/management-node/tenant/delete/{tenantId}")
    @Operation(summary = "删除租户下管理节点")
    Response<Void> deleteTenantNode(@PathVariable(value = "tenantId") String tenantId);

    @GetMapping(ApiConstants.PREFIX + "/get-user-project-by-node")
    @Operation(summary = "根据管理节点id获取用户该节点下所有项目id")
    Response<List<String>> getUserProjectByNode(@RequestParam("bizNodeId") String bizNodeId, @RequestParam("userId") Long userId);

    @GetMapping(ApiConstants.PREFIX + "/management-node/tenant/area/list")
    @Operation(summary = "根据管理节点id获取用户该节点下所有项目id")
    Response<List<AreaManageNodeResponse>> getAreaNodes(@RequestParam("tenantId") Long tenantId);

    @GetMapping(ApiConstants.PREFIX + "/get-all-project-by-node")
    @Operation(summary = "根据管理节点id获取该节点下所有项目id")
    Response<List<String>> getAllProjectByNode(@RequestParam("bizNodeId") String bizNodeId);
}
