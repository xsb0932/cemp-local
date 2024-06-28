package com.landleaf.bms.api;

import com.landleaf.bms.api.dto.NodeProjectTreeDTO;
import com.landleaf.bms.api.dto.UserProjectDTO;
import com.landleaf.bms.api.enums.ApiConstants;
import com.landleaf.comm.base.pojo.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 用户项目 api
 *
 * @author 张力方
 * @since 2023/6/8
 **/
@Tag(name = "Feign 服务 - 用户项目")
@FeignClient(name = ApiConstants.NAME)
public interface UserProjectApi {
    /**
     * 查询用户下项目业务id列表
     *
     * @param userId 用户id
     */
    @GetMapping(ApiConstants.PREFIX + "/user-project/biz-ids")
    @Operation(summary = "查询用户下项目业务id列表")
    @Parameter(name = "userId", description = "用户id", example = "2", required = true)
    Response<List<String>> getUserProjectBizIds(@RequestParam("userId") Long userId);

    /**
     * 查询用户下项目列表
     *
     * @param userId 用户id
     */
    @GetMapping(ApiConstants.PREFIX + "/user-project-list")
    @Operation(summary = "查询用户下项目列表")
    @Parameter(name = "userId", description = "用户id", example = "2", required = true)
    Response<List<UserProjectDTO>> getUserProjectList(@RequestParam("userId") Long userId);

    /**
     * 获取当前用户管理节点项目树
     *
     * @return 用户管理节点项目树
     */
    @GetMapping(ApiConstants.PREFIX + "/current-user/tree")
    @Operation(summary = "获取当前用户管理节点项目树")
    Response<NodeProjectTreeDTO> getCurrentUserProjectTree();
}
