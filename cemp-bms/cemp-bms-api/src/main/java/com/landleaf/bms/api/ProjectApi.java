package com.landleaf.bms.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.api.dto.*;
import com.landleaf.bms.api.enums.ApiConstants;
import com.landleaf.comm.base.pojo.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 项目 - Api
 *
 * @author 张力方
 * @since 2023/6/26
 **/
@Tag(name = "Feign 服务 - 项目相关")
@FeignClient(name = ApiConstants.NAME)
public interface ProjectApi {
    /**
     * 查询项目详情
     *
     * @param bizProjectId 项目业务id
     * @return 项目详情
     */
    @GetMapping(ApiConstants.PREFIX + "/project/details")
    @Operation(summary = "查询项目详情")
    Response<ProjectDetailsResponse> getProjectDetails(@RequestParam("bizProjectId") String bizProjectId);

    /**
     * 编辑项目
     *
     * @param request 编辑项目请求参数
     */
    @PutMapping(ApiConstants.PREFIX + "/project")
    @Operation(summary = "编辑项目")
    Response<Void> editProject(@RequestBody ProjectEditRequest request);

    /**
     * 查询项目详情
     *
     * @param id 项目id
     * @return 项目详情
     */
    @GetMapping(ApiConstants.PREFIX + "/project/{id}")
    @Operation(summary = "查询项目详情")
    Response<ProjectDetailsResponse> getDetails(@PathVariable("id") Long id);

    /**
     * 分页列表查询项目
     *
     * @param request 请求参数
     * @return 项目列表
     */
    @PostMapping(ApiConstants.PREFIX + "/project/list")
    @Operation(summary = "分页列表查询项目")
    Response<Page<ProjectListResponse>> pageQuery(@RequestBody ProjectListRequest request);

    /**
     * 校验项目名称是否唯一
     * <p>
     * true 唯一， false 不唯一
     */
    @PostMapping(ApiConstants.PREFIX + "/project/check-name-unique")
    @Operation(summary = "校验项目名称是否唯一", description = "true 唯一， false 不唯一")
    Response<Boolean> checkNameUnique(@RequestBody ProjectNameUniqueRequest request);

    /**
     * 校验项目编码是否唯一
     * <p>
     * true 唯一， false 不唯一
     */
    @PostMapping(ApiConstants.PREFIX + "/project/check-code-unique")
    @Operation(summary = "校验项目编码是否唯一", description = "true 唯一， false 不唯一")
    Response<Boolean> checkCodeUnique(@RequestBody ProjectCodeUniqueRequest request);

    @PostMapping(ApiConstants.PREFIX + "/project/names")
    @Operation(summary = "获取项目名称")
    Response<Map<String, String>> getProjectNames(@RequestBody List<String> bizProjectIds);

    @GetMapping(ApiConstants.PREFIX + "/project/director-user")
    @Operation(summary = "查询项目负责人信息")
    Response<ProjectDirectorUserDTO> getDirectorUser(@RequestParam("bizProjectId") String bizProjectId);

    @GetMapping(ApiConstants.PREFIX + "/tenant/projects")
    @Operation(summary = "获取租户项目")
    Response<List<TenantProjectDTO>> getTenantProjects(@RequestParam("tenantId") Long tenantId);

    @PostMapping(ApiConstants.PREFIX + "/projects/city")
    @Operation(summary = "获取项目所在城市")
    Response<List<ProjectCityDTO>> getProjectsCity(@RequestBody List<String> bizProjectIdList);

    /**
     * 获取项目区域概览
     *
     * @param nodeId 父业务节点编号
     * @return 目区域概览
     */
    @GetMapping(ApiConstants.PREFIX + "/project/area/info")
    @Operation(summary = "查询项目详情")
    Response<ProjectAreaResponse> getAreaProjectInfo(@RequestParam("nodeId") String nodeId);

    /**
     * 获取区域下所有项目id
     *
     * @param nodeId 父业务节点编号
     * @return
     */
    @GetMapping(ApiConstants.PREFIX + "/project/area/projectIds")
    @Operation(summary = "查询项目详情")
    Response<List<ProjectAreaProjectsDetailResponse>> getAreaProjectIds(@RequestParam("nodeId") String nodeId);
}
