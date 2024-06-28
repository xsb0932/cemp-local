package com.landleaf.energy.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.landleaf.bms.api.MessageApi;
import com.landleaf.bms.api.ProjectApi;
import com.landleaf.bms.api.dto.MessageAddRequest;
import com.landleaf.bms.api.dto.MsgNoticeUserDTO;
import com.landleaf.bms.api.dto.ProjectDirectorUserDTO;
import com.landleaf.bms.api.dto.TenantProjectDTO;
import com.landleaf.bms.api.enums.MsgStatusEnum;
import com.landleaf.bms.api.enums.MsgTypeEnum;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.energy.dal.mapper.PlannedElectricityMapper;
import com.landleaf.energy.dal.mapper.ProjectCnfTimePeriodMapper;
import com.landleaf.energy.dal.mapper.ProjectMapper;
import com.landleaf.energy.dal.mapper.ProjectStaSubitemMonthMapper;
import com.landleaf.energy.domain.entity.PlannedElectricityEntity;
import com.landleaf.energy.domain.entity.ProjectCnfElectricityPriceEntity;
import com.landleaf.energy.domain.entity.ProjectCnfTimePeriodEntity;
import com.landleaf.energy.domain.request.PlanElectricityRequest;
import com.landleaf.energy.domain.response.PlanElectricityTabulationResponse;
import com.landleaf.energy.enums.ElectricityPriceTypeEnum;
import com.landleaf.energy.service.PlannedElectricityService;
import com.landleaf.energy.service.ProjectCnfElectricityPriceService;
import com.landleaf.job.api.JobLogApi;
import com.landleaf.job.api.dto.JobLogSaveDTO;
import com.landleaf.job.api.dto.JobRpcRequest;
import com.landleaf.oauth.api.TenantApi;
import com.landleaf.oauth.api.dto.StaTenantDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.landleaf.comm.constance.CommonConstant.JOB_EXEC_ERROR;
import static com.landleaf.comm.constance.CommonConstant.JOB_EXEC_SUCCESS;
import static com.landleaf.energy.domain.enums.ErrorCodeConstants.*;


/**
 * 计划用电
 *
 * @author Tycoon
 * @since 2023/8/10 15:31
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class PlannedElectricityServiceImpl implements PlannedElectricityService {

    private final PlannedElectricityMapper plannedElectricityMapper;
    private final ProjectStaSubitemMonthMapper projectStaSubitemMonthMapper;
    private final ProjectMapper projectMapper;
    private final TenantApi tenantApi;
    private final JobLogApi jobLogApi;
    private final ProjectApi projectApi;
    private final ProjectCnfTimePeriodMapper projectCnfTimePeriodMapper;
    private final MessageApi messageApi;
    private final ProjectCnfElectricityPriceService projectCnfElectricityPriceServiceImpl;

    @Override
    public void initPlanElectricity(PlanElectricityRequest.Initialize request) {
        TenantContext.setIgnore(false);
        Assert.isTrue(projectMapper.existsProjectBizId(request.getProjectBizId()), () -> new ServiceException(PROJECT_NOT_EXIST));
        List<PlannedElectricityEntity> electricityEntities = plannedElectricityMapper.searchProjectYearPlans(request.getProjectBizId(), request.getYear());
        Assert.isTrue(electricityEntities.isEmpty(), () -> new ServiceException(YEAR_PLANNED_ELECTRICITY_EXISTED));
        List<PlannedElectricityEntity> entities = IntStream.range(1, 13)
                .mapToObj(it -> {
                    PlannedElectricityEntity entity = new PlannedElectricityEntity();
                    entity.setProjectBizId(request.getProjectBizId());
                    entity.setYear(request.getYear());
                    entity.setMonth(String.valueOf(it));
                    entity.setTenantId(TenantContext.getTenantId());
                    return entity;
                }).toList();
        plannedElectricityMapper.insertBatchSomeColumn(entities);
    }

    @Override
    public void updatePlanElectricity(PlanElectricityRequest.Change request) {
        TenantContext.setIgnore(false);
        Assert.notNull(plannedElectricityMapper.selectById(request.getId()), () -> new ServiceException(PLANNED_ELECTRICITY_NOT_EXIST));
        PlannedElectricityEntity electricityEntity = new PlannedElectricityEntity();
        electricityEntity.setPlanElectricityConsumption(request.getPlanElectricityConsumption());
        electricityEntity.setId(request.getId());
        plannedElectricityMapper.updateById(electricityEntity);
    }

    @Override
    public List<PlanElectricityTabulationResponse> searchElectricityTabulation(String projectBizId, Year year) {
        TenantContext.setIgnore(false);
        String yearString = String.valueOf(year);
        String lastYearString = String.valueOf(year.minusYears(1));
        Map<String, PlannedElectricityEntity> yearMonthMap = plannedElectricityMapper.searchProjectYearPlans(projectBizId, yearString)
                .stream()
                .collect(Collectors.toMap(it -> it.getYear() + it.getMonth(), Function.identity()));
        Map<String, BigDecimal> lastYearMonthPlanMap = plannedElectricityMapper.searchProjectYearPlans(projectBizId, lastYearString)
                .stream()
                .collect(HashMap::new, (map, item) -> map.put(item.getYear() + item.getMonth(), item.getPlanElectricityConsumption()), HashMap::putAll);
        Map<String, BigDecimal> lastYearMonthMap = projectStaSubitemMonthMapper.getEleYearData(projectBizId, lastYearString)
                .stream()
                .collect(HashMap::new, (map, item) -> map.put(item.getYear() + item.getMonth(), item.getProjectElectricityEnergyusageTotal()), HashMap::putAll);
        if (yearMonthMap.isEmpty()) {
            return List.of();
        }
        return IntStream.range(1, 13)
                .mapToObj(String::valueOf)
                .map(it -> {
                    PlannedElectricityEntity electricity = yearMonthMap.get(yearString + it);
                    PlanElectricityTabulationResponse tabulationResponse = new PlanElectricityTabulationResponse();
                    tabulationResponse.setId(electricity.getId());
                    tabulationResponse.setYear(yearString);
                    tabulationResponse.setMonth(it);
                    tabulationResponse.setPlanElectricityConsumption(electricity.getPlanElectricityConsumption());
                    tabulationResponse.setLastPlanElectricityConsumption(lastYearMonthPlanMap.get(lastYearString + it));
                    tabulationResponse.setLastElectricityConsumption(lastYearMonthMap.get(lastYearString + it));
                    return tabulationResponse;
                }).toList();
    }

    @Override
    public void electricityReminder(LocalDateTime now, JobRpcRequest request) {
        // 获取所有普通租户
        List<StaTenantDTO> tenantList = tenantApi.listStaJobTenant().getCheckedData();
        List<JobLogSaveDTO> jobLogList = new ArrayList<>();
        TenantContext.setIgnore(true);
        try {
            for (StaTenantDTO tenantDTO : tenantList) {
                if (null != request.getTenantId() && !Objects.equals(tenantDTO.getId(), request.getTenantId())) {
                    continue;
                }
                JobLogSaveDTO jobLog = new JobLogSaveDTO();
                jobLog.setJobId(request.getJobId())
                        .setTenantId(tenantDTO.getId())
                        .setStatus(JOB_EXEC_SUCCESS)
                        .setExecUser(request.getExecUser())
                        .setExecType(request.getExecType())
                        .setExecTime(LocalDateTime.now());

                List<TenantProjectDTO> projectList = projectApi.getTenantProjects(tenantDTO.getId()).getCheckedData();
                if (CollectionUtil.isNotEmpty(request.getProjectList())) {
                    projectList = projectList.stream().filter(o -> request.getProjectList().contains(o.getBizProjectId())).collect(Collectors.toList());
                }
                String projectIds = projectList.stream().map(TenantProjectDTO::getBizProjectId).collect(Collectors.joining(","));
                String projectNames = projectList.stream().map(TenantProjectDTO::getName).collect(Collectors.joining(","));
                jobLog.setProjectIds(projectIds).setProjectNames(projectNames);
                jobLogList.add(jobLog);

                try {
                    for (TenantProjectDTO projectDTO : projectList) {
                        ProjectDirectorUserDTO user = projectApi.getDirectorUser(projectDTO.getBizProjectId()).getCheckedData();
                        // 【ID1004305】 用电价格支持多种电价模式:新增逻辑，只有分时电价需要提醒维护
                        ProjectCnfElectricityPriceEntity electricityPriceEntity = projectCnfElectricityPriceServiceImpl.selectByBizProjId(projectDTO.getBizProjectId());
                        if (null != electricityPriceEntity && ElectricityPriceTypeEnum.TOU.getType().equals(electricityPriceEntity.getType())) {
                            // 逻辑1：每月28号至月底，提醒当月未录入电价的项目
                            // 2023-12-29 产品修改逻辑 改成提醒下个月
                            LocalDateTime nextMonth = now.plusMonths(1L);
                            if (now.getDayOfMonth() >= 28) {
                                Long count = projectCnfTimePeriodMapper.selectCount(new LambdaQueryWrapper<ProjectCnfTimePeriodEntity>()
                                        .eq(ProjectCnfTimePeriodEntity::getTenantId, tenantDTO.getId())
                                        .eq(ProjectCnfTimePeriodEntity::getProjectId, projectDTO.getBizProjectId())
                                        .eq(ProjectCnfTimePeriodEntity::getPeriodYear, String.valueOf(nextMonth.getYear()))
                                        .eq(ProjectCnfTimePeriodEntity::getPeriodMonth, String.valueOf(nextMonth.getMonthValue())));
                                if (count <= 0) {
                                    // 提醒
                                    if (null != user) {
                                        MessageAddRequest msg = new MessageAddRequest();
                                        msg.setMsgTitle("电价录入提醒");
                                        msg.setMsgContent(StrUtil.format("系统提醒：项目【{}】需录入{}年{}月电价", projectDTO.getName(), nextMonth.getYear(), nextMonth.getMonthValue()));
                                        // 消息类型：系统提醒
                                        msg.setMsgType(MsgTypeEnum.SYS.getType());
                                        // 已发布
                                        msg.setMsgStatus(MsgStatusEnum.PUBLISHED.getType());
                                        // 站内信
                                        msg.setMailFlag(1);
                                        // 邮件
                                        msg.setEmailFlag(1);
                                        // 推送人
                                        msg.setNoticeUserInfo(CollectionUtil.newArrayList(new MsgNoticeUserDTO().setTenantId(tenantDTO.getId()).setUserId(user.getId())));
                                        // 系统租户
                                        msg.setTenantId(tenantDTO.getId());
                                        messageApi.save(msg);
                                    } else {
                                        log.warn("项目 {} 电价录入提醒缺少项目负责人", projectDTO.getBizProjectId());
                                    }
                                }
                            }
                            // 逻辑2：每天提醒上月未录入电价的项目
                            // 2023-12-29 产品修改逻辑 改成提醒当月
                            Long count = projectCnfTimePeriodMapper.selectCount(new LambdaQueryWrapper<ProjectCnfTimePeriodEntity>()
                                    .eq(ProjectCnfTimePeriodEntity::getTenantId, tenantDTO.getId())
                                    .eq(ProjectCnfTimePeriodEntity::getProjectId, projectDTO.getBizProjectId())
                                    .eq(ProjectCnfTimePeriodEntity::getPeriodYear, String.valueOf(now.getYear()))
                                    .eq(ProjectCnfTimePeriodEntity::getPeriodMonth, String.valueOf(now.getMonthValue())));
                            if (count <= 0) {
                                // 提醒
                                if (null != user) {
                                    MessageAddRequest msg = new MessageAddRequest();
                                    msg.setMsgTitle("电价录入提醒");
                                    msg.setMsgContent(StrUtil.format("系统提醒：项目【{}】需录入{}年{}月电价", projectDTO.getName(), now.getYear(), now.getMonthValue()));
                                    // 消息类型：系统提醒
                                    msg.setMsgType(MsgTypeEnum.SYS.getType());
                                    // 已发布
                                    msg.setMsgStatus(MsgStatusEnum.PUBLISHED.getType());
                                    // 站内信
                                    msg.setMailFlag(1);
                                    // 邮件
                                    msg.setEmailFlag(1);
                                    // 推送人
                                    msg.setNoticeUserInfo(CollectionUtil.newArrayList(new MsgNoticeUserDTO().setTenantId(tenantDTO.getId()).setUserId(user.getId())));
                                    // 系统租户
                                    msg.setTenantId(tenantDTO.getId());
                                    messageApi.save(msg);
                                } else {
                                    log.warn("项目 {} 电价录入提醒缺少项目负责人", projectDTO.getBizProjectId());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("租户 {} 电价录入提醒任务异常", tenantDTO.getId(), e);
                    jobLog.setStatus(JOB_EXEC_ERROR);
                }
            }
        } catch (Exception e) {
            log.error("电价录入提醒任务异常", e);
        } finally {
            TenantContext.setIgnore(false);
            for (JobLogSaveDTO jobLog : jobLogList) {
                jobLogApi.saveLog(jobLog);
            }
        }
    }

    @Override
    public void yearReminder(LocalDateTime now, JobRpcRequest request) {
        // 获取所有普通租户
        List<StaTenantDTO> tenantList = tenantApi.listStaJobTenant().getCheckedData();
        List<JobLogSaveDTO> jobLogList = new ArrayList<>();
        TenantContext.setIgnore(true);
        LocalDateTime nextYear = now.plusYears(1L);
        try {
            for (StaTenantDTO tenantDTO : tenantList) {
                if (null != request.getTenantId() && !Objects.equals(tenantDTO.getId(), request.getTenantId())) {
                    continue;
                }
                JobLogSaveDTO jobLog = new JobLogSaveDTO();
                jobLog.setJobId(request.getJobId())
                        .setTenantId(tenantDTO.getId())
                        .setStatus(JOB_EXEC_SUCCESS)
                        .setExecUser(request.getExecUser())
                        .setExecType(request.getExecType())
                        .setExecTime(LocalDateTime.now());

                List<TenantProjectDTO> projectList = projectApi.getTenantProjects(tenantDTO.getId()).getCheckedData();
                if (CollectionUtil.isNotEmpty(request.getProjectList())) {
                    projectList = projectList.stream().filter(o -> request.getProjectList().contains(o.getBizProjectId())).collect(Collectors.toList());
                }
                String projectIds = projectList.stream().map(TenantProjectDTO::getBizProjectId).collect(Collectors.joining(","));
                String projectNames = projectList.stream().map(TenantProjectDTO::getName).collect(Collectors.joining(","));
                jobLog.setProjectIds(projectIds).setProjectNames(projectNames);
                jobLogList.add(jobLog);

                try {
                    for (TenantProjectDTO projectDTO : projectList) {
                        ProjectDirectorUserDTO user = projectApi.getDirectorUser(projectDTO.getBizProjectId()).getCheckedData();
                        // 逻辑 12月15号 8:30提醒 项目负责人 水电气计划 站内信+邮件
                        if (null != user) {
                            MessageAddRequest msg = new MessageAddRequest();
                            msg.setMsgTitle("用电计划录入提醒");
                            msg.setMsgContent(StrUtil.format("系统提醒：项目【{}】{}年用电计划录入", projectDTO.getName(), nextYear.getYear()));
                            // 消息类型：系统提醒
                            msg.setMsgType("1");
                            // 已发布
                            msg.setMsgStatus("02");
                            // 站内信
                            msg.setMailFlag(1);
                            // 邮件
                            msg.setEmailFlag(1);
                            // 推送人
                            msg.setNoticeUserInfo(CollectionUtil.newArrayList(new MsgNoticeUserDTO().setTenantId(tenantDTO.getId()).setUserId(user.getId())));
                            // 系统租户
                            msg.setTenantId(tenantDTO.getId());
                            messageApi.save(msg);

                            msg.setMsgTitle("用气计划录入提醒");
                            msg.setMsgContent(StrUtil.format("系统提醒：项目【{}】{}年用气计划录入", projectDTO.getName(), nextYear.getYear()));
                            messageApi.save(msg);

                            msg.setMsgTitle("用水计划录入提醒");
                            msg.setMsgContent(StrUtil.format("系统提醒：项目【{}】{}年用水计划录入", projectDTO.getName(), nextYear.getYear()));
                            messageApi.save(msg);
                        } else {
                            log.warn("项目 {} 水电气计划录入提醒缺少项目负责人", projectDTO.getBizProjectId());
                        }
                    }
                } catch (Exception e) {
                    log.error("租户 {} 水电气计划录入提醒任务异常", tenantDTO.getId(), e);
                    jobLog.setStatus(JOB_EXEC_ERROR);
                }
            }
        } catch (Exception e) {
            log.error("水电气计划录入提醒任务异常", e);
        } finally {
            TenantContext.setIgnore(false);
            for (JobLogSaveDTO jobLog : jobLogList) {
                jobLogApi.saveLog(jobLog);
            }
        }
    }
}
