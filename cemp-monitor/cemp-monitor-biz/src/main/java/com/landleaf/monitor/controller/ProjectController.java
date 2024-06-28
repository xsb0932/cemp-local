package com.landleaf.monitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.api.ProjectApi;
import com.landleaf.bms.api.dto.*;
import com.landleaf.comm.base.pojo.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 项目管理相关接口
 *
 * @author 张力方
 * @since 2023/6/6
 **/
@RequiredArgsConstructor
@RestController
@RequestMapping("/project")
@Tag(name = "项目管理相关接口")
public class ProjectController {
    private final ProjectApi projectApi;

    /**
     * 编辑项目
     *
     * @param request 编辑项目请求参数
     */
    @PutMapping
    @Operation(summary = "编辑项目")
    public Response<Void> editProject(@RequestBody @Validated ProjectEditRequest request) {
        return projectApi.editProject(request);
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
        return projectApi.getDetails(id);
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
        return projectApi.pageQuery(request);
    }

    /**
     * 校验项目名称是否唯一
     * <p>
     * true 唯一， false 不唯一
     */
    @PostMapping("/check-name-unique")
    @Operation(summary = "校验项目名称是否唯一", description = "true 唯一， false 不唯一")
    public Response<Boolean> checkNameUnique(@RequestBody @Validated ProjectNameUniqueRequest request) {
        return projectApi.checkNameUnique(request);
    }

    /**
     * 校验项目编码是否唯一
     * <p>
     * true 唯一， false 不唯一
     */
    @PostMapping("/check-code-unique")
    @Operation(summary = "校验项目编码是否唯一", description = "true 唯一， false 不唯一")
    public Response<Boolean> checkCodeUnique(@RequestBody @Validated ProjectCodeUniqueRequest request) {
        return projectApi.checkCodeUnique(request);
    }
}
