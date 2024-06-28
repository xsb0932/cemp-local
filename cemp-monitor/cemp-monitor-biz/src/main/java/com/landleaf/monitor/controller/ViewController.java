package com.landleaf.monitor.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.monitor.domain.entity.ViewEntity;
import com.landleaf.monitor.domain.request.ViewDuplicateRequest;
import com.landleaf.monitor.domain.request.ViewPageRequest;
import com.landleaf.monitor.domain.request.ViewSaveRequest;
import com.landleaf.monitor.domain.request.ViewUpdateRequest;
import com.landleaf.monitor.domain.response.ViewInfoResponse;
import com.landleaf.monitor.domain.response.ViewPageResponse;
import com.landleaf.monitor.domain.response.ViewTabResponse;
import com.landleaf.monitor.service.ViewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 视图的控制层接口定义
 *
 * @author hebin
 * @since 2023-06-20
 */
@RestController
@AllArgsConstructor
@RequestMapping("/view")
@Tag(name = "视图的控制层接口定义", description = "视图的控制层接口定义")
public class ViewController {
    /**
     * 视图的相关逻辑操作句柄
     */
    private final ViewService viewService;

    @GetMapping("/tab")
    @Parameter(name = "bizProjectIds", description = "项目bizId，多个用','分隔", example = "PJ00000001", required = true)
    @Operation(summary = "获取项目监测-视图tab")
    public Response<List<ViewTabResponse>> tab(@RequestParam("bizProjectIds") String bizProjectIds) {
        List<ViewEntity> views = viewService.tab(StrUtil.split(bizProjectIds, ","));
        return Response.success(
                views.stream().map(o -> new ViewTabResponse()
                                .setId(o.getId())
                                .setName(o.getName())
                                .setViewUrl(o.getViewUrl())
                                .setViewType(o.getViewType()))
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询视图列表")
    public Response<IPage<ViewPageResponse>> selectPage(ViewPageRequest request) {
        return Response.success(viewService.selectPage(request));
    }

    @GetMapping("/info/{viewId}")
    @Operation(summary = "视图详情")
    public Response<ViewInfoResponse> info(@PathVariable("viewId") Long viewId) {
        return Response.success(viewService.info(viewId));
    }

    @PostMapping("/save")
    @Operation(summary = "新增视图")
    public Response<Void> save(@Validated @RequestBody ViewSaveRequest request) {
        viewService.save(request);
        return Response.success();
    }

    @PutMapping("/update")
    @Operation(summary = "修改视图")
    public Response<Void> update(@Validated @RequestBody ViewUpdateRequest request) {
        viewService.update(request);
        return Response.success();
    }

    @PostMapping("/duplicate")
    @Operation(summary = "复制视图")
    public Response<Void> duplicate(@Validated @RequestBody ViewDuplicateRequest request) {
        viewService.duplicate(request);
        return Response.success();
    }

    @PutMapping("/change-status/{viewId}")
    @Operation(summary = "变更发布状态")
    public Response<Void> changeStatus(@PathVariable("viewId") Long viewId) {
        viewService.changeStatus(viewId);
        return Response.success();
    }

    @DeleteMapping("/detele/{viewId}")
    @Operation(summary = "删除视图配置")
    public Response<Void> delete(@PathVariable("viewId") Long viewId) {
        viewService.delete(viewId);
        return Response.success();
    }
}