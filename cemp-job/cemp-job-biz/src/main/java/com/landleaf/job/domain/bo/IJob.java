/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.landleaf.job.domain.bo;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.EscapeUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.util.object.SpringContextUtils;
import com.landleaf.job.api.dto.JobRpcRequest;
import com.landleaf.job.domain.entity.ScheduleJobEntity;
import com.landleaf.job.domain.entity.ScheduleJobLogEntity;
import com.landleaf.job.service.ScheduleJobLogService;
import com.landleaf.job.service.ScheduleJobService;
import com.landleaf.job.utils.ServiceClient;
import com.landleaf.pgsql.base.BaseEntity;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import static com.landleaf.comm.constance.CommonConstant.JOB_EXEC_ERROR;
import static com.landleaf.comm.constance.CommonConstant.JOB_EXEC_SUCCESS;


/**
 * 定时任务
 */
public class IJob extends QuartzJobBean implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void executeInternal(JobExecutionContext context) {
        ScheduleJobEntity scheduleJob = (ScheduleJobEntity) context.getMergedJobDataMap()
                .get(ScheduleJobEntity.JOB_PARAM_KEY);
        LocalDateTime now = LocalDateTime.now();
        //获取spring bean
        ScheduleJobLogService scheduleJobLogService = (ScheduleJobLogService) SpringContextUtils.getBean("scheduleJobLogService");
        ScheduleJobService scheduleJobService = (ScheduleJobService) SpringContextUtils.getBean("scheduleJobService");

        //数据库保存执行记录
        ScheduleJobLogEntity log = new ScheduleJobLogEntity();
        log.setJobId(scheduleJob.getId());
        log.setApiUrl(scheduleJob.getApiUrl());
        log.setCreateTime(LocalDateTime.now());

        //任务开始时间
        long startTime = System.currentTimeMillis();

        try {
            //执行任务
            logger.debug("任务准备执行，任务ID：" + scheduleJob.getId());
            JobRpcRequest request = new JobRpcRequest();
            request.setJobId(scheduleJob.getId()).setExecTime(null).setExecType(0).setExecUser(0L);

            Response result = SpringContextUtils.getBean(ServiceClient.class).callService(scheduleJob.getServiceName(), scheduleJob.getApiUrl(), request);

            long times = System.currentTimeMillis() - startTime;
            log.setTimes((int) times);
            if (result.isSuccess()) {
                log.setStatus(JOB_EXEC_SUCCESS);
            } else {
                log.setStatus(JOB_EXEC_ERROR);
                log.setError(JSONUtil.toJsonStr(result));
            }

//            log.setLogInfo(JSONUtil.toJsonStr(result));

            logger.debug("任务执行完毕，任务ID：" + scheduleJob.getId() + "  总共耗时：" + times + "毫秒");
        } catch (Exception e) {
            logger.error("任务执行失败，任务ID：" + scheduleJob.getId(), e);

            long times = System.currentTimeMillis() - startTime;
            log.setTimes((int) times);

            log.setStatus(1);
            // 转义空格和换行保存 异常栈保留2000个字符
            log.setError(EscapeUtil.escape(ExceptionUtil.stacktraceToString(e, 2000)));
        } finally {
            scheduleJobLogService.save(log);
            scheduleJobService.update(new LambdaUpdateWrapper<ScheduleJobEntity>()
                    .set(ScheduleJobEntity::getLastStatus, log.getStatus())
                    .set(ScheduleJobEntity::getLastTime, now)
                    .set(BaseEntity::getUpdateTime, now)
                    .eq(ScheduleJobEntity::getId, scheduleJob.getId()));
        }
    }
}
