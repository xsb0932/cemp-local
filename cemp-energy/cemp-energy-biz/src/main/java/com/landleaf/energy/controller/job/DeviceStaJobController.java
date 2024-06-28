package com.landleaf.energy.controller.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.constance.DeviceStaCategoryEnum;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.energy.service.job.DeviceStaDayService;
import com.landleaf.energy.service.job.DeviceStaHourService;
import com.landleaf.energy.service.job.DeviceStaMonthService;
import com.landleaf.energy.service.job.DeviceStaYearService;
import com.landleaf.job.api.JobLogApi;
import com.landleaf.job.api.dto.JobLogSaveDTO;
import com.landleaf.job.api.dto.JobRpcRequest;
import com.landleaf.monitor.api.DeviceStaApi;
import com.landleaf.monitor.api.dto.ProjectStaDTO;
import com.landleaf.oauth.api.TenantApi;
import com.landleaf.oauth.api.dto.StaTenantDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import static com.landleaf.comm.constance.CommonConstant.JOB_EXEC_ERROR;
import static com.landleaf.comm.constance.CommonConstant.JOB_EXEC_SUCCESS;

/**
 * @author Yang
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/job/device")
@Tag(name = "设备统计 Job接口定义", description = "设备统计 Job接口定义")
public class DeviceStaJobController {
    private TenantApi tenantApi;
    private DeviceStaApi deviceStaApi;
    private JobLogApi jobLogApi;
    private DeviceStaHourService deviceStaHourService;
    private DeviceStaDayService deviceStaDayService;
    private DeviceStaMonthService deviceStaMonthService;
    private DeviceStaYearService deviceStaYearService;
    private Executor businessExecutor;
    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostMapping("/sta-hour")
    @Operation(summary = "设备小时统计任务")
    public Response<Void> staHour(@RequestBody JobRpcRequest request) {
        LocalDateTime now;
        if (!StringUtils.hasText(request.getStartTime()) || !StringUtils.hasText(request.getEndTime())) {
            // 定时执行
            now = LocalDateTime.now();
            staHour(now, request);
        } else {
            // 手动执行
            LocalDateTime start = LocalDateTime.parse(request.getStartTime(), dateTimeFormatter).plusHours(1L);
            LocalDateTime end = LocalDateTime.parse(request.getEndTime(), dateTimeFormatter).plusHours(1L);
            List<LocalDateTime> list = new ArrayList<>();
            // 分时
            list.add(start);
            while (start.isBefore(end)) {
                start = start.plusHours(1);
                list.add(start);
            }
            // 拆分start和end,拆分后多次执行该任务
            list.forEach(i -> staHour(i, request));
        }

        return Response.success();
    }

    @PostMapping("/sta-day")
    @Operation(summary = "设备日统计任务")
    public Response<Void> staDay(@RequestBody JobRpcRequest request) {
        LocalDateTime now;
        if (!StringUtils.hasText(request.getStartTime()) || !StringUtils.hasText(request.getEndTime())) {
            // 定时执行
            now = LocalDateTime.now();
            staDay(now, request);
        } else {
            // 手动执行
            LocalDateTime start = LocalDateTime.parse(request.getStartTime() + " 00:00:00", dateTimeFormatter).plusDays(1L);
            LocalDateTime end = LocalDateTime.parse(request.getEndTime() + " 00:00:00", dateTimeFormatter).plusDays(1L);
            List<LocalDateTime> list = new ArrayList<>();
            // 分日
            list.add(start);
            while (start.isBefore(end)) {
                start = start.plusDays(1);
                list.add(start);
            }
            // 拆分start和end,拆分后多次执行该任务
            list.forEach(i -> staDay(i, request));
        }
        return Response.success();
    }

    @PostMapping("/sta-month")
    @Operation(summary = "设备月统计任务")
    public Response<Void> staMonth(@RequestBody JobRpcRequest request) {
        LocalDateTime now;
        if (!StringUtils.hasText(request.getStartTime()) || !StringUtils.hasText(request.getEndTime())) {
            // 定时执行
            now = LocalDateTime.now();
            // 判断执行时间是否满足月报周期
            int day = now.getDayOfMonth();
            if ((day > 7 && day < 24) || day > 29) {
                return Response.success();
            }
            staMonth(now, request);
        } else {
            // 手动执行
            LocalDateTime start = LocalDateTime.parse(request.getStartTime() + "-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime end = LocalDateTime.parse(request.getEndTime() + "-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            List<LocalDateTime> list = new ArrayList<>();
            // 分月
            do {
                LocalDateTime time = LocalDateTime.of(start.getYear(), start.getMonthValue(), 23, 0, 0, 0);
                for (int i = 0; i < 6; i++) {
                    time = time.plusDays(1L);
                    list.add(time);
                }
                if (time.getDayOfMonth() != 1) {
                    LocalDateTime nextMonth = start.plusMonths(1L);
                    time = LocalDateTime.of(nextMonth.getYear(), nextMonth.getMonthValue(), 1, 0, 0, 0);
                    list.add(time);
                }
                for (int i = 0; i < 6; i++) {
                    time = time.plusDays(1L);
                    list.add(time);
                }
                start = start.plusMonths(1L);
            } while (!start.isAfter(end));
            // 拆分start和end,拆分后多次执行该任务
            list.forEach(i -> staMonth(i, request));
        }
        return Response.success();
    }

    @PostMapping("/sta-year")
    @Operation(summary = "设备年统计任务")
    public Response<Void> staYear(@RequestBody JobRpcRequest request) {
        LocalDateTime now;
        if (!StringUtils.hasText(request.getStartTime()) || !StringUtils.hasText(request.getEndTime())) {
            // 定时执行
            now = LocalDateTime.now();
            // 判断执行时间是否满足月报周期
            int day = now.getDayOfMonth();
            if ((day > 7 && day < 24) || day > 29) {
                return Response.success();
            }
            staYear(now, request);
        } else {
            // 手动执行
            LocalDateTime start = LocalDateTime.parse(request.getStartTime() + "-12-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime end = LocalDateTime.parse(request.getEndTime() + "-12-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            List<LocalDateTime> list = new ArrayList<>();
            // 分年
            do {
                LocalDateTime time = LocalDateTime.of(start.getYear(), start.getMonthValue(), 23, 0, 0, 0);
                for (int i = 0; i < 6; i++) {
                    time = time.plusDays(1L);
                    list.add(time);
                }
                if (time.getDayOfMonth() != 1) {
                    LocalDateTime nextMonth = start.plusMonths(1L);
                    time = LocalDateTime.of(nextMonth.getYear(), nextMonth.getMonthValue(), 1, 0, 0, 0);
                    list.add(time);
                }
                for (int i = 0; i < 6; i++) {
                    time = time.plusDays(1L);
                    list.add(time);
                }
                start = start.plusYears(1L);
            } while (!start.isAfter(end));
            // 拆分start和end,拆分后多次执行该任务
            list.forEach(i -> staYear(i, request));
        }
        return Response.success();
    }

    @Deprecated
    @GetMapping("/sta-hour/manual")
    @Parameter(name = "time", description = "时间戳", example = "2023-06-27 01:00:00")
    @Operation(summary = "手动执行设备小时统计任务")
    public Response<Map<String, List<String>>> staHour(@RequestParam("time") String time) {
        LocalDateTime now = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        JobRpcRequest request = new JobRpcRequest();
        request.setJobId(1L).setExecUser(1L).setExecType(1);
        return Response.success(staHour(now, request));
    }

    @Deprecated
    @GetMapping("/sta-day/manual")
    @Parameter(name = "time", description = "时间戳", example = "2023-06-27 01:00:00")
    @Operation(summary = "手动执行设备日统计任务")
    public Response<Map<String, List<String>>> staDay(@RequestParam("time") String time) {
        LocalDateTime now = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        JobRpcRequest request = new JobRpcRequest();
        request.setJobId(1L).setExecUser(1L).setExecType(1);
        return Response.success(staDay(now, request));
    }

    @Deprecated
    @GetMapping("/sta-month/manual")
    @Parameter(name = "time", description = "时间戳", example = "2023-06-27 01:00:00")
    @Operation(summary = "手动执行设备月统计任务")
    public Response<Map<String, List<String>>> staMonth(@RequestParam("time") String time) {
        LocalDateTime now = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        JobRpcRequest request = new JobRpcRequest();
        request.setJobId(1L).setExecUser(1L).setExecType(1);
        return Response.success(staMonth(now, request));
    }

    @Deprecated
    @GetMapping("/sta-year/manual")
    @Parameter(name = "time", description = "时间戳", example = "2023-06-27 01:00:00")
    @Operation(summary = "手动执行设备年统计任务")
    public Response<Map<String, List<String>>> staYear(@RequestParam("time") String time) {
        LocalDateTime now = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        JobRpcRequest request = new JobRpcRequest();
        request.setJobId(1L).setExecUser(1L).setExecType(1);
        return Response.success(staYear(now, request));
    }

    public Map<String, List<String>> staHour(LocalDateTime staTime, JobRpcRequest request) {
        log.info("开始执行[设备小时统计任务]定时任务-{}", staTime);
        Map<String, List<String>> resultMap = new ConcurrentHashMap<>(16);
        // 获取所有普通租户
        List<StaTenantDTO> tenantList = tenantApi.listStaJobTenant().getCheckedData();
        // 循环租户 分品类统计设备
        CountDownLatch cdl = new CountDownLatch(tenantList.size() * 6);
        List<JobLogSaveDTO> jobLogList = new ArrayList<>();
        for (StaTenantDTO tenantDTO : tenantList) {
            if (null != request.getTenantId() && !Objects.equals(tenantDTO.getId(), request.getTenantId())) {
                for (int i = 0; i < 6; i++) {
                    cdl.countDown();
                }
                continue;
            }
            resultMap.put(tenantDTO.getCode(), new ArrayList<>());
            JobLogSaveDTO jobLog = new JobLogSaveDTO();
            jobLog.setJobId(request.getJobId())
                    .setTenantId(tenantDTO.getId())
                    .setStatus(JOB_EXEC_SUCCESS)
                    .setExecUser(request.getExecUser())
                    .setExecType(request.getExecType())
                    .setExecTime(LocalDateTime.now());

            List<ProjectStaDTO> projectList = deviceStaApi.listStaProject(tenantDTO.getId()).getCheckedData();
            if (CollectionUtil.isNotEmpty(request.getProjectList())) {
                projectList = projectList.stream().filter(o -> request.getProjectList().contains(o.getBizProjectId())).toList();
            }
            String projectIds = projectList.stream().map(ProjectStaDTO::getBizProjectId).collect(Collectors.joining(","));
            String projectNames = projectList.stream().map(ProjectStaDTO::getName).collect(Collectors.joining(","));
            jobLog.setProjectIds(projectIds).setProjectNames(projectNames);
            jobLogList.add(jobLog);

            businessExecutor.execute(() -> runHour(tenantDTO, staTime, DeviceStaCategoryEnum.KTYKQ, resultMap, cdl, jobLog));
            businessExecutor.execute(() -> runHour(tenantDTO, staTime, DeviceStaCategoryEnum.DB3PH, resultMap, cdl, jobLog));
            businessExecutor.execute(() -> runHour(tenantDTO, staTime, DeviceStaCategoryEnum.RQB, resultMap, cdl, jobLog));
            businessExecutor.execute(() -> runHour(tenantDTO, staTime, DeviceStaCategoryEnum.ZNSB, resultMap, cdl, jobLog));
            businessExecutor.execute(() -> runHour(tenantDTO, staTime, DeviceStaCategoryEnum.ZNB, resultMap, cdl, jobLog));
            businessExecutor.execute(() -> runHour(tenantDTO, staTime, DeviceStaCategoryEnum.GSCN, resultMap, cdl, jobLog));
        }
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (JobLogSaveDTO jobLog : jobLogList) {
            jobLogApi.saveLog(jobLog);
        }
        return resultMap;
    }

    public void runHour(StaTenantDTO tenantDTO, LocalDateTime staTime, DeviceStaCategoryEnum type, Map<String, List<String>> resultMap, CountDownLatch cdl, JobLogSaveDTO jobLog) {
        TenantContext.setIgnore(true);
        String msg;
        try {
            msg = deviceStaHourService.execute(tenantDTO.getId(), tenantDTO.getCode(), type, staTime, jobLog.getProjectIds());
        } catch (Exception e) {
            log.error("任务异常", e);
            msg = "品类 " + type.getCode() + " 执行异常";
            jobLog.setStatus(JOB_EXEC_ERROR);
        } finally {
            cdl.countDown();
            TenantContext.release();
        }
        resultMap.get(tenantDTO.getCode()).add(msg);
    }

    public Map<String, List<String>> staDay(LocalDateTime staTime, JobRpcRequest request) {
        log.info("开始执行[设备日统计任务]定时任务-{}", staTime);
        Map<String, List<String>> resultMap = new ConcurrentHashMap<>(16);
        // 获取所有普通租户
        List<StaTenantDTO> tenantList = tenantApi.listStaJobTenant().getCheckedData();
        // 循环租户 分品类统计设备
        CountDownLatch cdl = new CountDownLatch(tenantList.size() * 6);
        List<JobLogSaveDTO> jobLogList = new ArrayList<>();
        for (StaTenantDTO tenantDTO : tenantList) {
            if (null != request.getTenantId() && !Objects.equals(tenantDTO.getId(), request.getTenantId())) {
                for (int i = 0; i < 6; i++) {
                    cdl.countDown();
                }
                continue;
            }
            resultMap.put(tenantDTO.getCode(), new ArrayList<>());
            JobLogSaveDTO jobLog = new JobLogSaveDTO();
            jobLog.setJobId(request.getJobId())
                    .setTenantId(tenantDTO.getId())
                    .setStatus(JOB_EXEC_SUCCESS)
                    .setExecUser(request.getExecUser())
                    .setExecType(request.getExecType())
                    .setExecTime(LocalDateTime.now());

            List<ProjectStaDTO> projectList = deviceStaApi.listStaProject(tenantDTO.getId()).getCheckedData();
            if (CollectionUtil.isNotEmpty(request.getProjectList())) {
                projectList = projectList.stream().filter(o -> request.getProjectList().contains(o.getBizProjectId())).toList();
            }
            String projectIds = projectList.stream().map(ProjectStaDTO::getBizProjectId).collect(Collectors.joining(","));
            String projectNames = projectList.stream().map(ProjectStaDTO::getName).collect(Collectors.joining(","));
            jobLog.setProjectIds(projectIds).setProjectNames(projectNames);
            jobLogList.add(jobLog);

            businessExecutor.execute(() -> runDay(tenantDTO, staTime, DeviceStaCategoryEnum.KTYKQ, resultMap, cdl, jobLog));
            businessExecutor.execute(() -> runDay(tenantDTO, staTime, DeviceStaCategoryEnum.DB3PH, resultMap, cdl, jobLog));
            businessExecutor.execute(() -> runDay(tenantDTO, staTime, DeviceStaCategoryEnum.RQB, resultMap, cdl, jobLog));
            businessExecutor.execute(() -> runDay(tenantDTO, staTime, DeviceStaCategoryEnum.ZNSB, resultMap, cdl, jobLog));
            businessExecutor.execute(() -> runDay(tenantDTO, staTime, DeviceStaCategoryEnum.ZNB, resultMap, cdl, jobLog));
            businessExecutor.execute(() -> runDay(tenantDTO, staTime, DeviceStaCategoryEnum.GSCN, resultMap, cdl, jobLog));
        }
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (JobLogSaveDTO jobLog : jobLogList) {
            jobLogApi.saveLog(jobLog);
        }
        return resultMap;
    }

    public void runDay(StaTenantDTO tenantDTO, LocalDateTime staTime, DeviceStaCategoryEnum type, Map<String, List<String>> resultMap, CountDownLatch cdl, JobLogSaveDTO jobLog) {
        TenantContext.setIgnore(true);
        String msg;
        try {
            msg = deviceStaDayService.execute(tenantDTO.getId(), tenantDTO.getCode(), type, staTime, jobLog.getProjectIds());
        } catch (Exception e) {
            log.error("任务异常", e);
            msg = "品类 " + type.getCode() + " 执行异常";
            jobLog.setStatus(JOB_EXEC_ERROR);
        } finally {
            cdl.countDown();
            TenantContext.release();
        }
        resultMap.get(tenantDTO.getCode()).add(msg);
    }

    public Map<String, List<String>> staMonth(LocalDateTime staTime, JobRpcRequest request) {
        log.info("开始执行[设备月统计任务]定时任务-{}", staTime);
        Map<String, List<String>> resultMap = new ConcurrentHashMap<>(16);
        // 获取所有普通租户
        List<StaTenantDTO> tenantList = tenantApi.listStaJobTenant().getCheckedData();
        CountDownLatch cdl = new CountDownLatch(tenantList.size() * 6);
        // 循环租户 分品类统计设备
        List<JobLogSaveDTO> jobLogList = new ArrayList<>();
        for (StaTenantDTO tenantDTO : tenantList) {
            if (null != request.getTenantId() && !Objects.equals(tenantDTO.getId(), request.getTenantId())) {
                for (int i = 0; i < 6; i++) {
                    cdl.countDown();
                }
                continue;
            }
            // 判断租户月报周期
            if (StrUtil.equals("0", tenantDTO.getReportingCycle())) {
                // 自然月
                if (staTime.getDayOfMonth() != 1) {
                    log.info("跳过该触发时间 {} 租户月报周期 {}", staTime, tenantDTO.getReportingCycle());
                    for (int i = 0; i < 6; i++) {
                        cdl.countDown();
                    }
                    continue;
                }
            } else {
                if (staTime.minusDays(1L).getDayOfMonth() != Integer.parseInt(tenantDTO.getReportingCycle())) {
                    log.info("跳过该触发时间 {} 租户月报周期 {}", staTime, tenantDTO.getReportingCycle());
                    for (int i = 0; i < 6; i++) {
                        cdl.countDown();
                    }
                    continue;
                }
            }
            resultMap.put(tenantDTO.getCode(), new ArrayList<>());
            JobLogSaveDTO jobLog = new JobLogSaveDTO();
            jobLog.setJobId(request.getJobId())
                    .setTenantId(tenantDTO.getId())
                    .setStatus(JOB_EXEC_SUCCESS)
                    .setExecUser(request.getExecUser())
                    .setExecType(request.getExecType())
                    .setExecTime(LocalDateTime.now());

            List<ProjectStaDTO> projectList = deviceStaApi.listStaProject(tenantDTO.getId()).getCheckedData();
            if (CollectionUtil.isNotEmpty(request.getProjectList())) {
                projectList = projectList.stream().filter(o -> request.getProjectList().contains(o.getBizProjectId())).toList();
            }
            String projectIds = projectList.stream().map(ProjectStaDTO::getBizProjectId).collect(Collectors.joining(","));
            String projectNames = projectList.stream().map(ProjectStaDTO::getName).collect(Collectors.joining(","));
            jobLog.setProjectIds(projectIds).setProjectNames(projectNames);
            jobLogList.add(jobLog);

            businessExecutor.execute(() -> runMonth(tenantDTO, staTime, DeviceStaCategoryEnum.KTYKQ, resultMap, cdl, jobLog));
            businessExecutor.execute(() -> runMonth(tenantDTO, staTime, DeviceStaCategoryEnum.DB3PH, resultMap, cdl, jobLog));
            businessExecutor.execute(() -> runMonth(tenantDTO, staTime, DeviceStaCategoryEnum.RQB, resultMap, cdl, jobLog));
            businessExecutor.execute(() -> runMonth(tenantDTO, staTime, DeviceStaCategoryEnum.ZNSB, resultMap, cdl, jobLog));
            businessExecutor.execute(() -> runMonth(tenantDTO, staTime, DeviceStaCategoryEnum.ZNB, resultMap, cdl, jobLog));
            businessExecutor.execute(() -> runMonth(tenantDTO, staTime, DeviceStaCategoryEnum.GSCN, resultMap, cdl, jobLog));
        }
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (JobLogSaveDTO jobLog : jobLogList) {
            jobLogApi.saveLog(jobLog);
        }
        return resultMap;
    }

    public void runMonth(StaTenantDTO tenantDTO, LocalDateTime staTime, DeviceStaCategoryEnum type, Map<String, List<String>> resultMap, CountDownLatch cdl, JobLogSaveDTO jobLog) {
        TenantContext.setIgnore(true);
        String msg;
        try {
            msg = deviceStaMonthService.execute(tenantDTO.getId(), tenantDTO.getCode(), tenantDTO.getReportingCycle(), type, staTime, jobLog.getProjectIds());
        } catch (Exception e) {
            log.error("任务异常", e);
            msg = "品类 " + type.getCode() + " 执行异常";
            jobLog.setStatus(JOB_EXEC_ERROR);
        } finally {
            cdl.countDown();
            TenantContext.release();
        }
        resultMap.get(tenantDTO.getCode()).add(msg);
    }

    public Map<String, List<String>> staYear(LocalDateTime staTime, JobRpcRequest request) {
        log.info("开始执行[设备年统计任务]定时任务-{}", staTime);
        Map<String, List<String>> resultMap = new ConcurrentHashMap<>(16);
        // 获取所有普通租户
        List<StaTenantDTO> tenantList = tenantApi.listStaJobTenant().getCheckedData();
        CountDownLatch cdl = new CountDownLatch(tenantList.size() * 6);
        // 循环租户 分品类统计设备
        List<JobLogSaveDTO> jobLogList = new ArrayList<>();
        for (StaTenantDTO tenantDTO : tenantList) {
            if (null != request.getTenantId() && !Objects.equals(tenantDTO.getId(), request.getTenantId())) {
                for (int i = 0; i < 6; i++) {
                    cdl.countDown();
                }
                continue;
            }
            // 判断租户月报周期
            if (StrUtil.equals("0", tenantDTO.getReportingCycle())) {
                // 自然月
                if (staTime.getDayOfMonth() != 1) {
                    log.info("跳过该触发时间 {} 租户月报周期 {}", staTime, tenantDTO.getReportingCycle());
                    for (int i = 0; i < 6; i++) {
                        cdl.countDown();
                    }
                    continue;
                }
            } else {
                if (staTime.minusDays(1L).getDayOfMonth() != Integer.parseInt(tenantDTO.getReportingCycle())) {
                    log.info("跳过该触发时间 {} 租户月报周期 {}", staTime, tenantDTO.getReportingCycle());
                    for (int i = 0; i < 6; i++) {
                        cdl.countDown();
                    }
                    continue;
                }
            }
            resultMap.put(tenantDTO.getCode(), new ArrayList<>());
            JobLogSaveDTO jobLog = new JobLogSaveDTO();
            jobLog.setJobId(request.getJobId())
                    .setTenantId(tenantDTO.getId())
                    .setStatus(JOB_EXEC_SUCCESS)
                    .setExecUser(request.getExecUser())
                    .setExecType(request.getExecType())
                    .setExecTime(LocalDateTime.now());

            List<ProjectStaDTO> projectList = deviceStaApi.listStaProject(tenantDTO.getId()).getCheckedData();
            if (CollectionUtil.isNotEmpty(request.getProjectList())) {
                projectList = projectList.stream().filter(o -> request.getProjectList().contains(o.getBizProjectId())).toList();
            }
            String projectIds = projectList.stream().map(ProjectStaDTO::getBizProjectId).collect(Collectors.joining(","));
            String projectNames = projectList.stream().map(ProjectStaDTO::getName).collect(Collectors.joining(","));
            jobLog.setProjectIds(projectIds).setProjectNames(projectNames);
            jobLogList.add(jobLog);

            businessExecutor.execute(() -> runYear(tenantDTO, staTime, DeviceStaCategoryEnum.KTYKQ, resultMap, cdl, jobLog));
            businessExecutor.execute(() -> runYear(tenantDTO, staTime, DeviceStaCategoryEnum.DB3PH, resultMap, cdl, jobLog));
            businessExecutor.execute(() -> runYear(tenantDTO, staTime, DeviceStaCategoryEnum.RQB, resultMap, cdl, jobLog));
            businessExecutor.execute(() -> runYear(tenantDTO, staTime, DeviceStaCategoryEnum.ZNSB, resultMap, cdl, jobLog));
            businessExecutor.execute(() -> runYear(tenantDTO, staTime, DeviceStaCategoryEnum.ZNB, resultMap, cdl, jobLog));
            businessExecutor.execute(() -> runYear(tenantDTO, staTime, DeviceStaCategoryEnum.GSCN, resultMap, cdl, jobLog));
        }
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            TenantContext.release();
        }

        for (JobLogSaveDTO jobLog : jobLogList) {
            jobLogApi.saveLog(jobLog);
        }
        return resultMap;
    }

    public void runYear(StaTenantDTO tenantDTO, LocalDateTime staTime, DeviceStaCategoryEnum type, Map<String, List<String>> resultMap, CountDownLatch cdl, JobLogSaveDTO jobLog) {
        TenantContext.setIgnore(true);
        String msg;
        try {
            msg = deviceStaYearService.execute(tenantDTO.getId(), tenantDTO.getCode(), tenantDTO.getReportingCycle(), type, staTime, jobLog.getProjectIds());
        } catch (Exception e) {
            log.error("任务异常", e);
            msg = "品类 " + type.getCode() + " 执行异常";
            jobLog.setStatus(JOB_EXEC_ERROR);
        } finally {
            cdl.countDown();
            TenantContext.release();
        }
        resultMap.get(tenantDTO.getCode()).add(msg);
    }
}
