package com.landleaf.jjgj.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.landleaf.bms.api.ProjectApi;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.energy.api.ReportPushApi;
import com.landleaf.energy.api.dto.ProjectReportPushDTO;
import com.landleaf.jjgj.dal.mapper.JjgjReportPushMapper;
import com.landleaf.jjgj.domain.entity.JjgjReportPushEntity;
import com.landleaf.jjgj.domain.request.ReportPushConfigSaveRequest;
import com.landleaf.jjgj.domain.response.ReportPushConfigResponse;
import com.landleaf.jjgj.service.JjgjReportPushService;
import com.landleaf.job.api.dto.JobLogSaveDTO;
import com.landleaf.job.api.dto.JobRpcRequest;
import com.landleaf.mail.domain.param.JjgjReportPushMail;
import com.landleaf.mail.service.MailService;
import com.landleaf.oauth.api.UserRpcApi;
import com.landleaf.oauth.api.dto.UserEmailDTO;
import feign.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 锦江报表推送配置的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-11-21
 */
@Slf4j
@Service
@AllArgsConstructor
public class JjgjReportPushServiceImpl extends ServiceImpl<JjgjReportPushMapper, JjgjReportPushEntity> implements JjgjReportPushService {
    private ReportPushApi reportPushApi;
    private MailService mailService;
    private UserRpcApi userRpcApi;
    private ProjectApi projectApi;

    @Override
    public ReportPushConfigResponse projectConfig(String bizProjectId) {
        return baseMapper.projectConfig(bizProjectId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(ReportPushConfigSaveRequest request) {
        JjgjReportPushEntity entity = baseMapper.selectOne(new LambdaQueryWrapper<JjgjReportPushEntity>()
                .eq(JjgjReportPushEntity::getBizProjectId, request.getBizProjectId()));

        if (null == entity) {
            entity = new JjgjReportPushEntity();
            BeanUtil.copyProperties(request, entity);
            baseMapper.insert(entity);
        } else {
            BeanUtil.copyProperties(request, entity);
            entity.setUpdateTime(LocalDateTime.now());
            baseMapper.updateById(entity);
        }
    }

    @Override
    public void reportPush(JobRpcRequest request, JobLogSaveDTO jobLog) {
        LocalDateTime now = null != request.getExecTime() ? request.getExecTime() : LocalDateTime.now();
        TenantContext.setIgnore(true);
        try {
            List<JjgjReportPushEntity> projectConfigList = baseMapper.selectAll();
            if (CollectionUtil.isNotEmpty(request.getProjectList())) {
                projectConfigList.removeIf(o -> !request.getProjectList().contains(o.getBizProjectId()));
            }
            if (CollectionUtil.isEmpty(projectConfigList)) {
                return;
            }
            List<String> projectIdList = projectConfigList.stream().map(JjgjReportPushEntity::getBizProjectId).collect(Collectors.toList());

            String projectIds = String.join(",", projectIdList);
            String projectNames = String.join(",", projectApi.getProjectNames(projectIdList).getCheckedData().values());
            jobLog.setProjectIds(projectIds);
            jobLog.setProjectNames(projectNames);

            for (JjgjReportPushEntity projectConfig : projectConfigList) {
                try {
                    if (projectConfig.getWeekStatus() == 1 && isDayOfWeek(now, projectConfig.getWeekPush())) {
                        pushWeek(now, projectConfig);
                    }
                    if (projectConfig.getMonthStatus() == 1 && isDayOfMonth(now, projectConfig.getMonthPush())) {
                        pushMonth(now, projectConfig);
                    }
                } catch (Exception e) {
                    log.error("{} 推送异常", projectConfig, e);
                }
            }
        } finally {
            TenantContext.setIgnore(false);
        }
    }

    private void pushWeek(LocalDateTime now, JjgjReportPushEntity projectConfig) throws IOException {
        LocalDateTime lastWeek = now.minusWeeks(1L);
        DayOfWeek dayOfWeek = lastWeek.getDayOfWeek();
        LocalDateTime end = now.minusDays(dayOfWeek.getValue());
        LocalDateTime start = now.minusDays(6 + dayOfWeek.getValue());

        if (CollectionUtil.isEmpty(projectConfig.getWeekUserIds())) {
            log.info("待推送用户为空");
            return;
        }

        Response response = reportPushApi.projectReportPushData(
                new ProjectReportPushDTO(projectConfig.getBizProjectId(),
                        LocalDateTimeUtil.format(start, "yyyy-MM-dd"),
                        LocalDateTimeUtil.format(end, "yyyy-MM-dd"),
                        projectConfig.getWeekCodes())
        );

        if (response.status() == 200) {
            ByteArrayResource resource = new ByteArrayResource(IoUtil.readBytes(response.body().asInputStream()));

            List<UserEmailDTO> userEmails = userRpcApi.getUsersEmail(projectConfig.getWeekUserIds()).getCheckedData();
            for (UserEmailDTO userEmail : userEmails) {
                if (StrUtil.isBlank(userEmail.getEmail())) {
                    log.info("用户邮箱为空 {}", userEmail);
                    continue;
                }
                mailService.sendMailAsync(JjgjReportPushMail.weekMail(userEmail.getEmail(), "项目报表.xlsx", resource));
            }
        } else {
            log.error("调用接口异常 {}", response.body());
        }
    }

    private void pushMonth(LocalDateTime now, JjgjReportPushEntity projectConfig) throws IOException {
        LocalDateTime lastMonth = now.minusMonths(1L);
        int dayOfMonth = now.getDayOfMonth();
        LocalDateTime end = now.minusDays(dayOfMonth);
        LocalDateTime start = now.minusDays(dayOfMonth + lastMonth.getMonth().maxLength() - 1);

        if (CollectionUtil.isEmpty(projectConfig.getMonthUserIds())) {
            log.info("待推送用户为空");
            return;
        }

        Response response = reportPushApi.projectReportPushData(
                new ProjectReportPushDTO(projectConfig.getBizProjectId(),
                        LocalDateTimeUtil.format(start, "yyyy-MM-dd"),
                        LocalDateTimeUtil.format(end, "yyyy-MM-dd"),
                        projectConfig.getMonthCodes())
        );

        if (response.status() == 200) {
            ByteArrayResource resource = new ByteArrayResource(IoUtil.readBytes(response.body().asInputStream()));

            List<UserEmailDTO> userEmails = userRpcApi.getUsersEmail(projectConfig.getMonthUserIds()).getCheckedData();
            for (UserEmailDTO userEmail : userEmails) {
                if (StrUtil.isBlank(userEmail.getEmail())) {
                    log.info("用户邮箱为空 {}", userEmail);
                    continue;
                }
                mailService.sendMailAsync(JjgjReportPushMail.monthMail(userEmail.getEmail(), "项目报表.xlsx", resource));
            }
        } else {
            log.error("调用接口异常 {}", response.body());
        }
    }

    private boolean isDayOfWeek(LocalDateTime now, Integer weekPush) {
        int dayOfWeek = DateUtil.dayOfWeek(DateUtil.date(now));
        return dayOfWeek == weekPush;
    }

    private boolean isDayOfMonth(LocalDateTime now, Integer monthPush) {
        int dayOfMonth = now.getDayOfMonth();
        int configDayOfMonth = Math.min(DateUtil.getLastDayOfMonth(DateUtil.date(now)), monthPush);
        return dayOfMonth == configDayOfMonth;
    }
}