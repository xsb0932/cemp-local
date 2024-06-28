package com.landleaf.job.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.EscapeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.landleaf.bms.api.DictApi;
import com.landleaf.bms.api.dto.DictDataResponse;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.object.SpringContextUtils;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.job.api.dto.JobRpcRequest;
import com.landleaf.job.constans.ScheduleConstant;
import com.landleaf.job.dal.mapper.ScheduleJobMapper;
import com.landleaf.job.domain.entity.ScheduleJobEntity;
import com.landleaf.job.domain.entity.ScheduleJobLogEntity;
import com.landleaf.job.domain.request.ScheduleJobPageRequest;
import com.landleaf.job.domain.request.ScheduleJobSaveRequest;
import com.landleaf.job.domain.request.ScheduleJobUpdateRequest;
import com.landleaf.job.domain.request.ScheduleManualRunRequest;
import com.landleaf.job.domain.response.ScheduleJobResponse;
import com.landleaf.job.service.ScheduleJobLogService;
import com.landleaf.job.service.ScheduleJobService;
import com.landleaf.job.utils.ScheduleUtils;
import com.landleaf.job.utils.ServiceClient;
import com.landleaf.oauth.api.TenantApi;
import com.landleaf.pgsql.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.TriggerUtils;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

import static com.landleaf.comm.constance.CommonConstant.JOB_EXEC_ERROR;
import static com.landleaf.comm.constance.CommonConstant.JOB_EXEC_SUCCESS;

@Service("scheduleJobService")
@AllArgsConstructor
@Slf4j
public class ScheduleJobServiceImpl extends ServiceImpl<ScheduleJobMapper, ScheduleJobEntity> implements ScheduleJobService {
    @Resource
    private Scheduler scheduler;
    @Resource
    private DictApi dictApi;
    @Resource
    private TenantApi tenantApi;
    @Resource
    private Executor businessExecutor;
    @Resource
    private Executor scheduleExecutor;

    /**
     * 项目启动时，初始化定时器
     */
    @PostConstruct
    public void init() {
        List<ScheduleJobEntity> scheduleJobList = this.list();
        for (ScheduleJobEntity scheduleJob : scheduleJobList) {
            CronTrigger cronTrigger = ScheduleUtils.getCronTrigger(scheduler, scheduleJob.getId(), scheduleJob.getServiceName());
            //如果不存在，则创建
            if (cronTrigger == null) {
                ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
            } else {
                ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
            }
        }
    }

    @Override
    public IPage<ScheduleJobResponse> queryPage(ScheduleJobPageRequest request) {
        Long tenantId = null;
        if (null != request.getTenantId()) {
            tenantId = request.getTenantId();
        } else {
            Long currentTenantId = TenantContext.getTenantId();
            if (null != currentTenantId) {
                Response<Boolean> response = tenantApi.tenantIsAdmin(currentTenantId);
                if (response.isSuccess() && !response.getResult()) {
                    tenantId = currentTenantId;
                }
            }
        }

        Page<ScheduleJobResponse> pageResult = baseMapper.queryPage(
                Page.of(request.getPageNo(), request.getPageSize()),
                tenantId,
                request.getJobName(),
                request.getLastStatus(),
                request.getStatisticType()
        );

        HashMap<String, String> statisticTypeMap = new HashMap<>(8);
        HashMap<String, String> lastStatusMap = new HashMap<>(8);

        Response<List<DictDataResponse>> statisticTypeResponse = dictApi.getDictDataList("JOB_STATISTIC_TYPE");
        if (statisticTypeResponse.isSuccess()) {
            statisticTypeResponse.getResult().forEach(o -> statisticTypeMap.put(o.getValue(), o.getLabel()));
        }
        Response<List<DictDataResponse>> lastStatusResponse = dictApi.getDictDataList("JOB_LAST_STATUS");
        if (lastStatusResponse.isSuccess()) {
            lastStatusResponse.getResult().forEach(o -> lastStatusMap.put(o.getValue(), o.getLabel()));
        }

        pageResult.getRecords().forEach(o -> {
            o.setStatisticTypeName(statisticTypeMap.get(String.valueOf(o.getStatisticType())));
            o.setLastStatusName(lastStatusMap.get(String.valueOf(o.getLastStatus())));
        });
        return pageResult;
    }

    @Override
    public ScheduleJobEntity saveJob(ScheduleJobSaveRequest saveRequest) {
        if (!CronExpression.isValidExpression(saveRequest.getCronExpression())) {
            throw new BusinessException("新增任务'" + saveRequest.getJobName() + "'失败，Cron表达式不正确");
        }
        ScheduleJobEntity entity = new ScheduleJobEntity();
        BeanUtil.copyProperties(saveRequest, entity);
        baseMapper.insert(entity);
        ScheduleUtils.createScheduleJob(scheduler, entity);
        return entity;
    }

    @Override
    public ScheduleJobEntity update(ScheduleJobUpdateRequest updateRequest) {
        ScheduleJobEntity entity = new ScheduleJobEntity();
        BeanUtil.copyProperties(updateRequest, entity);
        ScheduleUtils.updateScheduleJob(scheduler, entity);
        this.updateById(entity);
        return entity;
    }

    @Override
    public void deleteBatch(List<Long> jobIds) {
        List<ScheduleJobEntity> jobs = list(new LambdaQueryWrapper<ScheduleJobEntity>().in(ScheduleJobEntity::getId, jobIds));
        for (ScheduleJobEntity job : jobs) {
            ScheduleUtils.deleteScheduleJob(scheduler, job.getId(), job.getServiceName());
        }
        this.removeByIds(jobIds);
    }

    @Override
    public void run(List<Long> jobIds) {
        List<ScheduleJobEntity> jobs = list(new LambdaQueryWrapper<ScheduleJobEntity>().in(ScheduleJobEntity::getId, jobIds));
        for (ScheduleJobEntity job : jobs) {
            ScheduleUtils.run(scheduler, job);
        }
    }

    @Override
    public void pause(List<Long> jobIds) {
        List<ScheduleJobEntity> jobs = list(new LambdaQueryWrapper<ScheduleJobEntity>().in(ScheduleJobEntity::getId, jobIds));
        for (ScheduleJobEntity job : jobs) {
            Long jobId = job.getId();
            String jobGroup = job.getServiceName();
            job.setStatus(ScheduleConstant.ScheduleStatus.PAUSE.getValue());
            int rows = baseMapper.updateById(job);
            if (rows > 0) {
                ScheduleUtils.pauseJob(scheduler, jobId, jobGroup);
            }
        }
    }

    @Override
    public void resume(List<Long> jobIds) {
        List<ScheduleJobEntity> jobs = list(new LambdaQueryWrapper<ScheduleJobEntity>().in(ScheduleJobEntity::getId, jobIds));
        for (ScheduleJobEntity job : jobs) {
            Long jobId = job.getId();
            String jobGroup = job.getServiceName();
            job.setStatus(ScheduleConstant.ScheduleStatus.NORMAL.getValue());
            int rows = baseMapper.updateById(job);
            if (rows > 0) {
                ScheduleUtils.resumeJob(scheduler, jobId, jobGroup);
            }
        }
    }

    @Override
    public void manualRun(ScheduleManualRunRequest request) {
        ScheduleJobEntity job = baseMapper.selectById(request.getJobId());
        if (null == job) {
            throw new BusinessException("任务不存在");
        }
        Long userId = LoginUserUtil.getLoginUserId();

        // 按照产品要求 根据类型 由计算时间推算执行时间 增加一个统计周期
        LocalDateTime startLocalDateTime;
        LocalDateTime endLocalDateTime;

        if (0 == job.getStatisticType()) {
            LocalDateTime tempStart = LocalDateTimeUtil.parse(request.getStartTime(), "yyyy-MM-dd HH:mm:ss");
            startLocalDateTime = LocalDateTime.of(tempStart.getYear(), tempStart.getMonthValue(), tempStart.getDayOfMonth(), tempStart.getHour(), 0, 0)
                    .plusHours(1L);

            LocalDateTime tempEnd = LocalDateTimeUtil.parse(request.getEndTime(), "yyyy-MM-dd HH:mm:ss");
            endLocalDateTime = LocalDateTime.of(tempEnd.getYear(), tempEnd.getMonthValue(), tempEnd.getDayOfMonth(), tempEnd.getHour(), 59, 59)
                    .plusHours(1L);
        } else if (1 == job.getStatisticType()) {
            startLocalDateTime = LocalDateTimeUtil.parse(request.getStartTime() + " 00:00:00", "yyyy-MM-dd HH:mm:ss")
                    .plusDays(1L);

            endLocalDateTime = LocalDateTimeUtil.parse(request.getEndTime() + " 23:59:59", "yyyy-MM-dd HH:mm:ss")
                    .plusDays(1L);
        } else if (2 == job.getStatisticType()) {
            LocalDateTime tempStart = LocalDateTimeUtil.parse(request.getStartTime(), "yyyy-MM");
            startLocalDateTime = LocalDateTime.of(tempStart.getYear(), tempStart.getMonthValue(), 1, 0, 0, 0)
                    .plusMonths(1L);

            LocalDateTime tempEnd = LocalDateTimeUtil.parse(request.getEndTime(), "yyyy-MM");
            endLocalDateTime = LocalDateTime.of(tempEnd.getYear(), tempEnd.getMonthValue(), tempEnd.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth(), 23, 59, 59)
                    .plusMonths(1L);
        } else if (3 == job.getStatisticType()) {
            LocalDateTime tempStart = LocalDateTimeUtil.parse(request.getStartTime(), "yyyy");
            startLocalDateTime = LocalDateTime.of(tempStart.getYear(), 1, 1, 0, 0, 0)
                    .plusYears(1L);

            LocalDateTime tempEnd = LocalDateTimeUtil.parse(request.getEndTime(), "yyyy");
            endLocalDateTime = LocalDateTime.of(tempEnd.getYear(), 12, 31, 23, 59, 59)
                    .plusYears(1L);
        } else {
            throw new BusinessException("统计类型错误");
        }

        businessExecutor.execute(() -> {
            try {
                CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
                cronTriggerImpl.setCronExpression(job.getCronExpression());


                List<Date> dates = TriggerUtils.computeFireTimesBetween(
                        cronTriggerImpl,
                        null,
                        Date.from(startLocalDateTime.atZone(ZoneId.systemDefault()).toInstant()),
                        Date.from(endLocalDateTime.atZone(ZoneId.systemDefault()).toInstant())
                );
                for (Date date : dates) {
                    LocalDateTime execTime = LocalDateTimeUtil.of(date);
                    log.info("job {} execTime {}", job, execTime);
                    LocalDateTime now = LocalDateTime.now();

                    //获取spring bean
                    ScheduleJobLogService scheduleJobLogService = (ScheduleJobLogService) SpringContextUtils.getBean("scheduleJobLogService");
                    ScheduleJobService scheduleJobService = (ScheduleJobService) SpringContextUtils.getBean("scheduleJobService");

                    //数据库保存执行记录
                    ScheduleJobLogEntity jobLog = new ScheduleJobLogEntity();
                    jobLog.setJobId(job.getId());
                    jobLog.setApiUrl(job.getApiUrl());
                    jobLog.setCreateTime(LocalDateTime.now());

                    //任务开始时间
                    long startTime = System.currentTimeMillis();

                    try {
                        //执行任务
                        log.debug("任务手动准备执行，任务ID：" + job.getId());
                        JobRpcRequest jobRpcRequest = new JobRpcRequest();
                        jobRpcRequest.setJobId(job.getId())
                                .setTenantId(request.getTenantId())
                                .setExecUser(userId)
                                .setExecTime(execTime)
                                .setExecType(1);
                        if (StrUtil.isNotBlank(request.getBizProjectId()) && !StrUtil.equals("0", request.getBizProjectId())) {
                            jobRpcRequest.setProjectList(StrUtil.split(request.getBizProjectId(), ","));
                        }

                        Response result = SpringContextUtils.getBean(ServiceClient.class).callService(job.getServiceName(), job.getApiUrl(), jobRpcRequest);

                        long times = System.currentTimeMillis() - startTime;
                        jobLog.setTimes((int) times);
                        if (result.isSuccess()) {
                            jobLog.setStatus(JOB_EXEC_SUCCESS);
                        } else {
                            jobLog.setStatus(JOB_EXEC_ERROR);
                            jobLog.setError(JSONUtil.toJsonStr(result));
                        }

                        log.debug("任务手动执行完毕，任务ID：" + job.getId() + "  总共耗时：" + times + "毫秒");
                    } catch (Exception e) {
                        log.error("任务手动执行失败，任务ID：" + job.getId(), e);

                        long times = System.currentTimeMillis() - startTime;
                        jobLog.setTimes((int) times);

                        jobLog.setStatus(1);
                        // 转义空格和换行保存 异常栈保留2000个字符
                        jobLog.setError(EscapeUtil.escape(ExceptionUtil.stacktraceToString(e, 2000)));
                    } finally {
                        scheduleJobLogService.save(jobLog);
                        scheduleJobService.update(new LambdaUpdateWrapper<ScheduleJobEntity>()
                                .set(ScheduleJobEntity::getLastStatus, jobLog.getStatus())
                                .set(ScheduleJobEntity::getLastTime, now)
                                .set(BaseEntity::getUpdateTime, now)
                                .eq(ScheduleJobEntity::getId, job.getId()));
                    }
                }
            } catch (ParseException e) {
                log.error("cron表达式转换失败 {}", job);
            }
        });
    }

    /**
     * 迭代9 修改定时任务手动执行的实现方式
     *
     * @param request 参数
     */
    @Override
    public void manualRunV2(ScheduleManualRunRequest request) {
        ScheduleJobEntity job = baseMapper.selectById(request.getJobId());
        if (null == job) {
            throw new BusinessException("任务不存在");
        }
        Long userId = LoginUserUtil.getLoginUserId();

        // 改为直接获取页面选择的开始时间和结束时间 校验下时间格式
        if (0 == job.getStatisticType()) {
            LocalDateTime.parse(request.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime.parse(request.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } else if (1 == job.getStatisticType()) {
            LocalDate.parse(request.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate.parse(request.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } else if (2 == job.getStatisticType()) {
            YearMonth.parse(request.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM"));
            YearMonth.parse(request.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM"));
        } else if (3 == job.getStatisticType()) {
            Year.parse(request.getStartTime(), DateTimeFormatter.ofPattern("yyyy"));
            Year.parse(request.getEndTime(), DateTimeFormatter.ofPattern("yyyy"));
        } else {
            throw new BusinessException("统计类型错误");
        }
        // 2024-05-06 根据要求改为单线程线程池异步顺序执行
        scheduleExecutor.execute(() -> {
            log.info("job {} execTime {}", job, request.getStartTime() + " " + request.getEndTime());
            LocalDateTime now = LocalDateTime.now();

            //获取spring bean
            ScheduleJobLogService scheduleJobLogService = (ScheduleJobLogService) SpringContextUtils.getBean("scheduleJobLogService");
            ScheduleJobService scheduleJobService = (ScheduleJobService) SpringContextUtils.getBean("scheduleJobService");

            //数据库保存执行记录
            ScheduleJobLogEntity jobLog = new ScheduleJobLogEntity();
            jobLog.setJobId(job.getId());
            jobLog.setApiUrl(job.getApiUrl());
            jobLog.setCreateTime(LocalDateTime.now());

            //任务开始时间
            long startTime = System.currentTimeMillis();

            try {
                //执行任务
                log.debug("任务手动准备执行，任务ID：" + job.getId());
                JobRpcRequest jobRpcRequest = new JobRpcRequest();
                jobRpcRequest.setJobId(job.getId())
                        .setTenantId(request.getTenantId())
                        .setExecUser(userId)
                        .setStartTime(request.getStartTime())
                        .setEndTime(request.getEndTime())
                        .setExecType(1);
                // 区分所有项目or指定项目
                if (StrUtil.isNotBlank(request.getBizProjectId()) && !StrUtil.equals("0", request.getBizProjectId())) {
                    jobRpcRequest.setProjectList(StrUtil.split(request.getBizProjectId(), ","));
                }

                Response result = SpringContextUtils.getBean(ServiceClient.class).callService(job.getServiceName(), job.getApiUrl(), jobRpcRequest);

                long times = System.currentTimeMillis() - startTime;
                jobLog.setTimes((int) times);
                if (result.isSuccess()) {
                    jobLog.setStatus(JOB_EXEC_SUCCESS);
                } else {
                    jobLog.setStatus(JOB_EXEC_ERROR);
                    jobLog.setError(JSONUtil.toJsonStr(result));
                }

                log.debug("任务手动执行完毕，任务ID：" + job.getId() + "  总共耗时：" + times + "毫秒");
            } catch (Exception e) {
                log.error("任务手动执行失败，任务ID：" + job.getId(), e);

                long times = System.currentTimeMillis() - startTime;
                jobLog.setTimes((int) times);

                jobLog.setStatus(1);
                // 转义空格和换行保存 异常栈保留2000个字符
                jobLog.setError(EscapeUtil.escape(ExceptionUtil.stacktraceToString(e, 2000)));
            } finally {
                scheduleJobLogService.save(jobLog);
                scheduleJobService.update(new LambdaUpdateWrapper<ScheduleJobEntity>()
                        .set(ScheduleJobEntity::getLastStatus, jobLog.getStatus())
                        .set(ScheduleJobEntity::getLastTime, now)
                        .set(BaseEntity::getUpdateTime, now)
                        .eq(ScheduleJobEntity::getId, job.getId()));
            }
        });
    }
}
