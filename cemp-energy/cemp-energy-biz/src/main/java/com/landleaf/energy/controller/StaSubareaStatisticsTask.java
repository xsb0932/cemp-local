package com.landleaf.energy.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.energy.service.StaSubareaStatisticsService;
import com.landleaf.energy.util.JobUtil;
import com.landleaf.job.api.dto.JobRpcRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 分区的时间统计方法
 */
@RestController
@Tag(name = "分区的时间统计方法", description = "分区的时间统计方法")
@RequestMapping("/sta-subarea/statistics")
@Slf4j
public class StaSubareaStatisticsTask {

    @Resource
    private StaSubareaStatisticsService staSubareaStatisticsServiceImpl;

    /**
     * 按小时统计
     *
     * @return
     */
    @PostMapping("/hour")
    @Operation(summary = "按小时统计分区信息", description = "按小时统计分区信息")
    public Response<Boolean> statisticsByHour(String times, @RequestBody JobRpcRequest request) {
        log.info("执行小时定时任务，job：{}， 内容：{}", times, JSONUtil.toJsonStr(request));
        TenantContext.setIgnore(true);
        String[] time = null;
        // 2023-11-15 迭代4 定时任务手动执行逻辑修改
        if (StringUtils.hasText(request.getStartTime()) && StringUtils.hasText(request.getEndTime())) {
            // 手动执行
            DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
            List<LocalDateTime> list = JobUtil.dealTimeStr2Array(request.getStartTime(), request.getEndTime(), JobUtil.TYPE_HOUR);
            time = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                time[i] = list.get(i).format(hourFormatter);
            }
        }
        if (StringUtils.hasText(times)) {
            time = times.split(StrUtil.COMMA);
        }
        boolean result = staSubareaStatisticsServiceImpl.statisticsByHour(time, request);
        return Response.success(result);
    }

    /**
     * 按天统计
     *
     * @return
     */
    @PostMapping("/day")
    @Operation(summary = "按天统计分区信息", description = "按天统计分区信息")
    public Response<Boolean> statisticsByDay(String times, @RequestBody JobRpcRequest request) {
        log.info("执行日定时任务，job：{}， 内容：{}", times, JSONUtil.toJsonStr(request));
        TenantContext.setIgnore(true);
        String[] time = null;
        // 2023-11-15 迭代4 定时任务手动执行逻辑修改
        if (StringUtils.hasText(request.getStartTime()) && StringUtils.hasText(request.getEndTime())) {
            // 手动执行
            DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            List<LocalDateTime> list = JobUtil.dealTimeStr2Array(request.getStartTime(), request.getEndTime(), JobUtil.TYPE_DAY);
            time = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                time[i] = list.get(i).format(hourFormatter);
            }
        }
        if (StringUtils.hasText(times)) {
            time = times.split(StrUtil.COMMA);
        }
        log.info("执行日定时任务，job：{}， 内容：{}", JSONUtil.toJsonStr(time), JSONUtil.toJsonStr(request));
        boolean result = staSubareaStatisticsServiceImpl.statisticsByDay(time, request);
        return Response.success(result);
    }

    /**
     * 按月统计
     *
     * @return
     */
    @PostMapping("/month")
    @Operation(summary = "按月统计分区信息", description = "按月统计分区信息")
    public Response<Boolean> statisticsByMonth(String times, @RequestBody JobRpcRequest request) {
        log.info("执行月定时任务，job：{}， 内容：{}", times, JSONUtil.toJsonStr(request));
        TenantContext.setIgnore(true);
        String[] time = null;
        // 2023-11-15 迭代4 定时任务手动执行逻辑修改
        if (StringUtils.hasText(request.getStartTime()) && StringUtils.hasText(request.getEndTime())) {
            // 手动执行
            DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            List<LocalDateTime> list = JobUtil.dealTimeStr2Array(request.getStartTime(), request.getEndTime(), JobUtil.TYPE_MONTH);
            time = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                time[i] = list.get(i).format(hourFormatter);
            }
        } else {
            // 判断执行时间是否满足月报周期
            int day = LocalDateTime.now().getDayOfMonth();
            if ((day > 7 && day < 24) || day > 29) {
                return Response.success();
            }
        }
        if (StringUtils.hasText(times)) {
            time = times.split(StrUtil.COMMA);
        }
        boolean result = staSubareaStatisticsServiceImpl.statisticsByMonth(time, request);
        return Response.success(result);
    }

    /**
     * 按年统计
     *
     * @return
     */
    @PostMapping("/year")
    @Operation(summary = "按年统计分区信息", description = "按年统计分区信息")
    public Response<Boolean> statisticsByYear(String times, @RequestBody JobRpcRequest request) {
        log.info("执行年定时任务，job：{}， 内容：{}", times, JSONUtil.toJsonStr(request));
        TenantContext.setIgnore(true);
        String[] time = null;
        // 2023-11-15 迭代4 定时任务手动执行逻辑修改
        if (StringUtils.hasText(request.getStartTime()) && StringUtils.hasText(request.getEndTime())) {
            // 手动执行
            DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            List<LocalDateTime> list = JobUtil.dealTimeStr2Array(request.getStartTime(), request.getEndTime(), JobUtil.TYPE_YEAR);
            time = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                time[i] = list.get(i).format(hourFormatter);
            }
        } else {
            // 判断执行时间是否满足月报周期
            int day = LocalDateTime.now().getDayOfMonth();
            if ((day > 7 && day < 24) || day > 29) {
                return Response.success();
            }
        }
        if (StringUtils.hasText(times)) {
            time = times.split(StrUtil.COMMA);
        }
        boolean result = staSubareaStatisticsServiceImpl.statisticsByYear(time, request);
        return Response.success(result);
    }
}
