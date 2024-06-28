package com.landleaf.bms.api;

import com.landleaf.bms.api.dto.UserManageNodeIdsResponse;
import com.landleaf.bms.api.dto.UserManageNodeResponse;
import com.landleaf.bms.api.dto.UserNodeUpdateRequest;
import com.landleaf.bms.api.dto.UserProjRelationResponse;
import com.landleaf.bms.api.enums.ApiConstants;
import com.landleaf.comm.base.pojo.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理节点权限 api
 *
 * @author 张力方
 * @since 2023/6/6
 **/
@Tag(name = "Feign 服务 - 用户管理节点权限")
@FeignClient(name = ApiConstants.NAME)
public interface UserManagementNodeApi {

    /**
     * 更新用户管理节点权限
     *
     * @param request 用户管理节点数据
     */
    @PutMapping(ApiConstants.PREFIX + "/user-management-node/update")
    @Operation(summary = "更新用户管理节点权限")
    Response<Void> updateUserNode(@Validated @RequestBody UserNodeUpdateRequest request);

    /**
     * 查询用户拥有权限的管理节点树
     *
     * @param userId 用户id
     */
    @PostMapping(ApiConstants.PREFIX + "/user-management-node/tree")
    @Operation(summary = "查询用户拥有权限的管理节点树")
    Response<UserManageNodeResponse> getUserManageNodes(@RequestParam("userId") Long userId);

    /**
     * 查询用户拥有权限的管理节点ids
     *
     * @param userId 用户id
     */
    @PostMapping(ApiConstants.PREFIX + "/user-management-node/ids")
    @Operation(summary = "查询用户拥有权限的管理节点ids")
    Response<UserManageNodeIdsResponse> getUserManageNodeIds(@RequestParam("userId") Long userId);

    /**
     * 查询用户拥有权限的项目的bizProjIds
     *
     * @param tenantId 租户id
     */
    @GetMapping(ApiConstants.PREFIX + "/user-management-node/user-proj-relation")
    @Operation(summary = "查询用户拥有权限的项目的bizProjIds")
    Response<List<UserProjRelationResponse>> getUserProjRelation(@RequestParam("tenantId") Long tenantId);
}
