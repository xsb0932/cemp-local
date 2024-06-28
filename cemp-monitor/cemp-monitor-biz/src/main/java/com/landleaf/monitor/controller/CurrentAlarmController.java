package com.landleaf.monitor.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.monitor.domain.request.CurrentAlarmListRequest;
import com.landleaf.monitor.domain.response.AlarmListResponse;
import com.landleaf.monitor.domain.response.AlarmTypeNumResponse;
import com.landleaf.monitor.service.CurrentAlarmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * 当前告警相关接口
 *
 * @author 张力方
 * @since 2023/8/14
 **/
@RestController
@AllArgsConstructor
@RequestMapping("/alarm/current")
@Tag(name = "当前告警相关接口", description = "当前告警相关接口")
public class CurrentAlarmController {

    private final CurrentAlarmService currentAlarmService;

    /**
     * 获取告警类型数量
     *
     * @param projectBizIds 项目业务ids
     * @return 告警类型&数量
     */
    @GetMapping("/type/num")
    @Operation(summary = "获取告警类型数量", description = "获取告警类型数量")
    public Response<List<AlarmTypeNumResponse>> getAlarmTypeNum(@RequestParam @Schema(description = "bizProjIds,逗号分割", name = "projectBizIds") String projectBizIds) {
        List<AlarmTypeNumResponse> alarmTypeNum = currentAlarmService.getAlarmTypeNum(Arrays.asList(projectBizIds.split(StrUtil.COMMA)));
        return Response.success(alarmTypeNum);
    }

    /**
     * 获取告警列表
     *
     * @param request 请求条件
     * @return 告警列表
     */
    @GetMapping("/page-query")
    @Operation(summary = "获取告警列表", description = "获取告警列表")
    public Response<Page<AlarmListResponse>> getAlarmResponse(CurrentAlarmListRequest request) {
        Page<AlarmListResponse> alarmResponse = currentAlarmService.getAlarmResponse(request);
        return Response.success(alarmResponse);
    }

}
