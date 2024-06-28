/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.landleaf.job.utils;

import com.landleaf.comm.exception.BusinessException;
import com.landleaf.job.constans.ScheduleConstant;
import com.landleaf.job.domain.bo.IJob;
import com.landleaf.job.domain.entity.ScheduleJobEntity;
import org.quartz.*;

/**
 * 定时任务工具类
 *
 * @author Mark sunlightcs@gmail.com
 */
public class ScheduleUtils {
    private final static String JOB_NAME = "TASK_";

    /**
     * 构建任务触发对象
     */
    public static TriggerKey getTriggerKey(Long jobId, String jobGroup) {
        return TriggerKey.triggerKey(JOB_NAME + jobId, jobGroup);
    }

    /**
     * 构建任务键对象
     */
    public static JobKey getJobKey(Long jobId, String jobGroup) {
        return JobKey.jobKey(JOB_NAME + jobId, jobGroup);
    }

    /**
     * 获取表达式触发器
     */
    public static CronTrigger getCronTrigger(Scheduler scheduler, Long jobId, String jobGroup) {
        try {
            return (CronTrigger) scheduler.getTrigger(getTriggerKey(jobId, jobGroup));
        } catch (SchedulerException e) {
            throw new BusinessException("获取定时任务CronTrigger出现异常", e);
        }
    }

    /**
     * 创建定时任务
     */
    public static void createScheduleJob(Scheduler scheduler, ScheduleJobEntity scheduleJob) {
        try {
            //构建job信息
            JobDetail jobDetail = JobBuilder.newJob(IJob.class).withIdentity(getJobKey(scheduleJob.getId(), scheduleJob.getServiceName())).build();

            //表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression()).withMisfireHandlingInstructionDoNothing();

            //按新的cronExpression表达式构建一个新的trigger
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(scheduleJob.getId(), scheduleJob.getServiceName())).withSchedule(scheduleBuilder).build();

            //放入参数，运行时的方法可以获取
            jobDetail.getJobDataMap().put(ScheduleJobEntity.JOB_PARAM_KEY, scheduleJob);

            scheduler.scheduleJob(jobDetail, trigger);

            //暂停任务
            if (scheduleJob.getStatus() == ScheduleConstant.ScheduleStatus.PAUSE.getValue()) {
                pauseJob(scheduler, scheduleJob.getId(), scheduleJob.getServiceName());
            }
        } catch (SchedulerException e) {
            throw new BusinessException("创建定时任务失败", e);
        }
    }

    /**
     * 更新定时任务
     */
    public static void updateScheduleJob(Scheduler scheduler, ScheduleJobEntity scheduleJob) {
        try {
            TriggerKey triggerKey = getTriggerKey(scheduleJob.getId(), scheduleJob.getServiceName());

            //表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression()).withMisfireHandlingInstructionDoNothing();

            CronTrigger trigger = getCronTrigger(scheduler, scheduleJob.getId(), scheduleJob.getServiceName());

            //按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

            //参数
            trigger.getJobDataMap().put(ScheduleJobEntity.JOB_PARAM_KEY, scheduleJob);

            scheduler.rescheduleJob(triggerKey, trigger);

            //暂停任务
            if (scheduleJob.getStatus() == ScheduleConstant.ScheduleStatus.PAUSE.getValue()) {
                pauseJob(scheduler, scheduleJob.getId(), scheduleJob.getServiceName());
            }

        } catch (SchedulerException e) {
            throw new BusinessException("更新定时任务失败", e);
        }
    }

    /**
     * 立即执行任务
     */
    public static void run(Scheduler scheduler, ScheduleJobEntity scheduleJob) {
        try {
            //参数
            JobDataMap dataMap = new JobDataMap();
            dataMap.put(ScheduleJobEntity.JOB_PARAM_KEY, scheduleJob);

            scheduler.triggerJob(getJobKey(scheduleJob.getId(), scheduleJob.getServiceName()), dataMap);
        } catch (SchedulerException e) {
            throw new BusinessException("立即执行定时任务失败", e);
        }
    }

    /**
     * 暂停任务
     */
    public static void pauseJob(Scheduler scheduler, Long jobId, String jobGroup) {
        try {
            scheduler.pauseJob(getJobKey(jobId, jobGroup));
        } catch (SchedulerException e) {
            throw new BusinessException("暂停定时任务失败", e);
        }
    }

    /**
     * 恢复任务
     */
    public static void resumeJob(Scheduler scheduler, Long jobId, String jobGroup) {
        try {
            scheduler.resumeJob(getJobKey(jobId, jobGroup));
        } catch (SchedulerException e) {
            throw new BusinessException("暂停定时任务失败", e);
        }
    }

    /**
     * 删除定时任务
     */
    public static void deleteScheduleJob(Scheduler scheduler, Long jobId, String jobGroup) {
        try {
            scheduler.deleteJob(getJobKey(jobId, jobGroup));
        } catch (SchedulerException e) {
            throw new BusinessException("删除定时任务失败", e);
        }
    }
}
