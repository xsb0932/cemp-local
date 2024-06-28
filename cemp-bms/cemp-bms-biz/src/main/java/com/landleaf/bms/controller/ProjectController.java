package com.landleaf.bms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.api.dto.*;
import com.landleaf.bms.domain.entity.ProjectEntity;
import com.landleaf.bms.domain.request.ProjectAddRequest;
import com.landleaf.bms.domain.request.ProjectTreeListRequest;
import com.landleaf.bms.domain.response.NodeProjectTreeResponse;
import com.landleaf.bms.domain.response.ScheduleProjectResponse;
import com.landleaf.bms.service.ProjectService;
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
 * 项目相关接口
 *
 * @author 张力方
 * @since 2023/6/6
 **/
@RequiredArgsConstructor
@RestController
@RequestMapping("/project")
@Tag(name = "项目相关接口")
public class ProjectController {
    private final ProjectService projectService;

    /**
     * 获取当前用户管理节点项目树
     *
     * @return 用户管理节点项目树
     */
    @GetMapping("/current-user/tree")
    @Operation(summary = "获取当前用户管理节点项目树")
    public Response<NodeProjectTreeResponse> getCurrentUserProjectTree() {
        NodeProjectTreeResponse currentUserProjectTree = projectService.getCurrentUserProjectTree();
        return Response.success(currentUserProjectTree);
    }

    /**
     * 获取当前用户管理节点树-不包含项目
     *
     * @return 当前用户管理节点树
     */
    @GetMapping("/node/current-user/tree")
    @Operation(summary = "获取当前用户管理节点树-不包含项目")
    public Response<NodeProjectTreeResponse> getCurrentUserNodeTree() {
        NodeProjectTreeResponse currentUserProjectTree = projectService.getCurrentUserNodeTree();
        return Response.success(currentUserProjectTree);
    }

    @GetMapping("/current-user/tree2")
    @Operation(summary = "获取当前用户管理节点项目树")
    public Response<NodeProjectTreeResponse> getCurrentUserProjectTree2() {
        NodeProjectTreeResponse currentUserProjectTree = projectService.getCurrentUserProjectTree2();
        return Response.success(currentUserProjectTree);
    }

    @GetMapping("/current-user/list")
    @Operation(summary = "获取当前用户管理节点项目树")
    public Response<List<ProjectEntity>> getCurrentUserProjectList() {
        List<ProjectEntity> result = projectService.getCurrentUserProjectList();
        return Response.success(result);
    }

    @GetMapping("/tenant/project/list")
    @Operation(summary = "获取当前租户项目")
    public Response<List<ProjectEntity>> getTenantProjectList() {
        List<ProjectEntity> result = projectService.getTenantProjectList();
        return Response.success(result);
    }

    @GetMapping("/schedule/list")
    @Operation(summary = "获取租户定时任务项目")
    public Response<List<ScheduleProjectResponse>> getScheduleProjectList(@RequestParam("tenantId") Long tenantId) {
        List<ScheduleProjectResponse> result = projectService.getScheduleProjectList(tenantId);
        return Response.success(result);
    }

    /**
     * 根据管理节点获取当前用户项目列表
     *
     * @param request 管理节点业务id
     * @return 项目列表
     */
    @GetMapping("/current-user/node/list")
    @Operation(summary = "根据管理节点获取当前用户项目列表")
    public Response<List<ProjectListResponse>> getProjectList(ProjectTreeListRequest request) {
        List<ProjectListResponse> projectList = projectService.getProjectList(request);
        return Response.success(projectList);
    }

    /**
     * 新增项目
     *
     * @param request 新增项目请求参数
     */
    @PostMapping
    @Operation(summary = "新增项目")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "添加项目", type = OperateTypeEnum.CREATE)
    public Response<Void> addProject(@RequestBody @Validated ProjectAddRequest request) {
        projectService.add(request);
        return Response.success();
    }

    /**
     * 编辑项目
     *
     * @param request 编辑项目请求参数
     */
    @PutMapping
    @Operation(summary = "编辑项目")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "修改项目", type = OperateTypeEnum.UPDATE)
    public Response<Void> editProject(@RequestBody @Validated ProjectEditRequest request) {
        projectService.update(request);
        return Response.success();
    }

    /**
     * 删除项目
     *
     * @param id 项目id
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除项目")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "删除项目", type = OperateTypeEnum.DELETE)
    public Response<Void> deleteProject(@PathVariable("id") Long id) {
        projectService.delete(id);
        return Response.success();
    }

    /**
     * 查询项目详情
     *
     * @param id 项目id
     * @return 项目详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询项目详情")
    public Response<ProjectDetailsResponse> getDetails(@PathVariable("id") Long id) {
        ProjectDetailsResponse details = projectService.getDetails(id);
        return Response.success(details);
    }

    /**
     * 分页列表查询项目
     *
     * @param request 请求参数
     * @return 项目列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页列表查询项目")
    public Response<Page<ProjectListResponse>> pageQuery(@Validated ProjectListRequest request) {
        Page<ProjectListResponse> projectListResponsePage = projectService.pageList(request);
        return Response.success(projectListResponsePage);
    }

    /**
     * 校验项目名称是否唯一
     * <p>
     * true 唯一， false 不唯一
     */
    @PostMapping("/check-name-unique")
    @Operation(summary = "校验项目名称是否唯一", description = "true 唯一， false 不唯一")
    public Response<Boolean> checkNameUnique(@RequestBody @Validated ProjectNameUniqueRequest request) {
        boolean unique = projectService.checkNameUnique(request);
        return Response.success(unique);
    }

    /**
     * 校验项目编码是否唯一
     * <p>
     * true 唯一， false 不唯一
     */
    @PostMapping("/check-code-unique")
    @Operation(summary = "校验项目编码是否唯一", description = "true 唯一， false 不唯一")
    public Response<Boolean> checkCodeUnique(@RequestBody @Validated ProjectCodeUniqueRequest request) {
        boolean unique = projectService.checkCodeUnique(request);
        return Response.success(unique);
    }
}
