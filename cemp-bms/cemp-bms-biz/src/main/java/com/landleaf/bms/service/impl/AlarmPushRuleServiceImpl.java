package com.landleaf.bms.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.landleaf.bms.api.ProjectApi;
import com.landleaf.bms.api.dto.TenantProjectDTO;
import com.landleaf.bms.api.enums.AlarmLevelEnum;
import com.landleaf.bms.dal.mapper.AlarmPushConditionMapper;
import com.landleaf.bms.dal.mapper.AlarmPushRuleMapper;
import com.landleaf.bms.dal.mapper.AlarmPushStatusMapper;
import com.landleaf.bms.dal.mapper.AlarmPushUserMapper;
import com.landleaf.bms.domain.entity.AlarmPushConditionEntity;
import com.landleaf.bms.domain.entity.AlarmPushRuleEntity;
import com.landleaf.bms.domain.entity.AlarmPushStatusEntity;
import com.landleaf.bms.domain.entity.AlarmPushUserEntity;
import com.landleaf.bms.domain.enums.AlarmStatusEnum;
import com.landleaf.bms.domain.enums.AlarmTypeEnum;
import com.landleaf.bms.domain.enums.RuleStatusEnum;
import com.landleaf.bms.domain.request.*;
import com.landleaf.bms.domain.response.AlarmPushRuleConfigConditionResponse;
import com.landleaf.bms.domain.response.AlarmPushRuleConfigPushResponse;
import com.landleaf.bms.domain.response.AlarmPushRuleConfigResponse;
import com.landleaf.bms.domain.response.AlarmPushRulePageResponse;
import com.landleaf.bms.service.AlarmPushRuleService;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 告警推送规则的业务逻辑接口实现
 *
 * @author hebin
 * @since 2024-05-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmPushRuleServiceImpl extends ServiceImpl<AlarmPushRuleMapper, AlarmPushRuleEntity> implements AlarmPushRuleService {
    private final AlarmPushConditionMapper alarmPushConditionMapper;
    private final AlarmPushStatusMapper alarmPushStatusMapper;
    private final AlarmPushUserMapper alarmPushUserMapper;
    private final ProjectApi projectApi;

    @Override
    public Page<AlarmPushRulePageResponse> selectPage(AlarmPushRulePageRequest request) {
        Page<AlarmPushRulePageResponse> result = super.baseMapper.pageQuery(
                Page.of(request.getPageNo(), request.getPageSize()),
                request.getRuleName(),
                request.getRuleStatus()
        );
        List<AlarmPushRulePageResponse> records = result.getRecords();
        if (records.isEmpty()) {
            return result;
        }
        List<Long> ruleIdList = new ArrayList<>();
        Map<Long, AlarmPushRulePageResponse> ruleMap = records.stream()
                .peek(o -> {
                    o.setRuleStatusName(RuleStatusEnum.codeToName(o.getRuleStatus()));
                    ruleIdList.add(o.getId());
                })
                .collect(Collectors.toMap(AlarmPushRulePageResponse::getId, o -> o));

        Map<String, String> projectNameMap = projectApi.getTenantProjects(TenantContext.getTenantId()).getCheckedData().stream()
                .collect(Collectors.toMap(TenantProjectDTO::getBizProjectId, TenantProjectDTO::getName, (o1, o2) -> o1));

        alarmPushConditionMapper.selectList(new LambdaQueryWrapper<AlarmPushConditionEntity>().in(AlarmPushConditionEntity::getRuleId, ruleIdList))
                .forEach(o -> {
                    AlarmPushRulePageResponse dto = ruleMap.get(o.getRuleId());
                    if (CollUtil.isNotEmpty(o.getBizProjectIdList())) {
                        if (o.getBizProjectIdList().size() == 1 && StrUtil.equals("0", o.getBizProjectIdList().get(0))) {
                            dto.setProjectList(CollUtil.newArrayList("全部"));
                        } else {
                            List<String> projectList = new ArrayList<>();
                            for (String bizProjectId : o.getBizProjectIdList()) {
                                projectList.add(projectNameMap.getOrDefault(bizProjectId, bizProjectId));
                            }
                            dto.setProjectList(projectList);
                        }
                    } else {
                        dto.setProjectList(Collections.emptyList());
                    }
                    List<String> alarmTypeList = new ArrayList<>();
                    if (CollUtil.isNotEmpty(o.getAlarmTypeList())) {
                        for (String code : o.getAlarmTypeList()) {
                            alarmTypeList.add(AlarmTypeEnum.getName(code));
                        }
                    }
                    dto.setAlarmTypeList(alarmTypeList);

                    List<String> alarmLevelList = new ArrayList<>();
                    if (CollUtil.isNotEmpty(o.getAlarmLevelList())) {
                        for (String code : o.getAlarmLevelList()) {
                            alarmLevelList.add(AlarmLevelEnum.getName(code));
                        }
                    }
                    dto.setAlarmLevelList(alarmLevelList);

                    List<String> alarmStatusList = new ArrayList<>();
                    if (CollUtil.isNotEmpty(o.getAlarmStatusList())) {
                        for (String code : o.getAlarmStatusList()) {
                            alarmStatusList.add(AlarmStatusEnum.codeToName(code));
                        }
                    }
                    dto.setAlarmStatusList(alarmStatusList);
                });

        alarmPushUserMapper.selectPageUserList(ruleIdList)
                .forEach(o -> {
                    AlarmPushRulePageResponse dto = ruleMap.get(o.getRuleId());
                    switch (o.getPushType()) {
                        case 0 -> {
                            List<String> emailUserList = dto.getEmailUserList();
                            emailUserList.add(o.getNickname());
                        }
                        case 1 -> {
                            List<String> messageUserList = dto.getMessageUserList();
                            messageUserList.add(o.getNickname());
                        }
                        case 2 -> {
                            List<String> dingUserList = dto.getDingUserList();
                            dingUserList.add(o.getDingUrl());
                        }
                        default -> log.error("告警推送方式类型异常 {}", o);
                    }
                });
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(AlarmPushRuleAddRequest request) {
        AlarmPushRuleEntity rule = new AlarmPushRuleEntity();
        rule.setRuleName(request.getRuleName()).setDescription(request.getDescription())
                .setRuleStatus(RuleStatusEnum.DISABLE.getCode());
        boolean save = this.save(rule);
        if (!save) {
            throw new BusinessException("新增失败");
        }
        AlarmPushConditionEntity condition = new AlarmPushConditionEntity();
        condition.setRuleId(rule.getId())
                .setBizProjectIdList(CollUtil.newArrayList("0"))
                .setAlarmTypeFlag(RuleStatusEnum.DISABLE.getCode())
                .setAlarmLevelFlag(RuleStatusEnum.DISABLE.getCode())
                .setAlarmStatusFlag(RuleStatusEnum.DISABLE.getCode());
        alarmPushConditionMapper.insert(condition);
        if (null == condition.getId()) {
            throw new BusinessException("新增失败");
        }
        AlarmPushStatusEntity status = new AlarmPushStatusEntity();
        status.setRuleId(rule.getId())
                .setEmailStatus(RuleStatusEnum.DISABLE.getCode())
                .setMessageStatus(RuleStatusEnum.DISABLE.getCode())
                .setDingStatus(RuleStatusEnum.DISABLE.getCode());
        alarmPushStatusMapper.insert(status);
        if (null == status.getId()) {
            throw new BusinessException("新增失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(AlarmPushRuleEditRequest request) {
        AlarmPushRuleEntity entity = new AlarmPushRuleEntity();
        entity.setId(request.getId()).setRuleName(request.getRuleName()).setDescription(request.getDescription());
        boolean update = this.updateById(entity);
        if (!update) {
            throw new BusinessException("更新失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        AlarmPushRuleEntity entity = getById(id);
        if (null == entity) {
            throw new BusinessException("告警推送规则不存在");
        }
        if (!StrUtil.equals(RuleStatusEnum.DISABLE.getCode(), entity.getRuleStatus())) {
            throw new BusinessException("需先停用再删除");
        }
        this.removeById(id);
        alarmPushConditionMapper.delete(new LambdaUpdateWrapper<AlarmPushConditionEntity>().eq(AlarmPushConditionEntity::getRuleId, id));
        alarmPushStatusMapper.delete(new LambdaUpdateWrapper<AlarmPushStatusEntity>().eq(AlarmPushStatusEntity::getRuleId, id));
        alarmPushUserMapper.delete(new LambdaUpdateWrapper<AlarmPushUserEntity>().eq(AlarmPushUserEntity::getRuleId, id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enable(Long id) {
        AlarmPushRuleEntity entity = getById(id);
        if (null == entity) {
            throw new BusinessException("告警推送规则不存在");
        }
        if (StrUtil.equals(RuleStatusEnum.ENABLE.getCode(), entity.getRuleStatus())) {
            throw new BusinessException("告警推送规则已经启用");
        }
        AlarmPushConditionEntity condition = alarmPushConditionMapper.selectOne(new LambdaQueryWrapper<AlarmPushConditionEntity>().eq(AlarmPushConditionEntity::getRuleId, id));
        if (null == condition || CollUtil.isEmpty(condition.getBizProjectIdList())) {
            throw new BusinessException("缺少推送条件配置");
        }
        AlarmPushStatusEntity pushStatus = alarmPushStatusMapper.selectOne(new LambdaQueryWrapper<AlarmPushStatusEntity>().eq(AlarmPushStatusEntity::getRuleId, id));
        boolean flag = null != pushStatus
                && (StrUtil.equals(RuleStatusEnum.ENABLE.getCode(), pushStatus.getEmailStatus())
                || StrUtil.equals(RuleStatusEnum.ENABLE.getCode(), pushStatus.getMessageStatus())
                || StrUtil.equals(RuleStatusEnum.ENABLE.getCode(), pushStatus.getDingStatus()));
        if (!flag) {
            throw new BusinessException("缺少推送方式配置");
        }

        entity.setRuleStatus(RuleStatusEnum.ENABLE.getCode());
        boolean update = this.updateById(entity);
        if (!update) {
            throw new BusinessException("启用失败");
        }
    }

    @Override
    public void disable(Long id) {
        AlarmPushRuleEntity entity = getById(id);
        if (null == entity) {
            throw new BusinessException("告警推送规则不存在");
        }
        if (StrUtil.equals(RuleStatusEnum.DISABLE.getCode(), entity.getRuleStatus())) {
            throw new BusinessException("告警推送规则已经停用");
        }
        entity.setRuleStatus(RuleStatusEnum.DISABLE.getCode());
        boolean update = this.updateById(entity);
        if (!update) {
            throw new BusinessException("停用失败");
        }
    }

    @Override
    public AlarmPushRuleConfigResponse config(Long id) {
        AlarmPushRuleEntity entity = getById(id);
        if (null == entity) {
            throw new BusinessException("告警推送规则不存在");
        }
        AlarmPushRuleConfigResponse result = new AlarmPushRuleConfigResponse();
        result.setId(entity.getId())
                .setRuleName(entity.getRuleName())
                .setRuleStatus(entity.getRuleStatus())
                .setRuleStatusName(RuleStatusEnum.codeToName(entity.getRuleStatus()))
                .setDescription(entity.getDescription());

        AlarmPushConditionEntity condition = alarmPushConditionMapper.selectOne(new LambdaQueryWrapper<AlarmPushConditionEntity>().eq(AlarmPushConditionEntity::getRuleId, id));
        List<String> bizProjectIdList = condition.getBizProjectIdList();
        if (CollUtil.isEmpty(bizProjectIdList) || StrUtil.equals(bizProjectIdList.get(0), "0")) {
            result.setSelectAllProject(Boolean.TRUE);
        } else {
            result.setSelectAllProject(Boolean.FALSE).setBizProjectIdList(bizProjectIdList);
        }
        if (StrUtil.equals(RuleStatusEnum.ENABLE.getCode(), condition.getAlarmTypeFlag())) {
            AlarmPushRuleConfigConditionResponse data = new AlarmPushRuleConfigConditionResponse();
            data.setType("ALARM_TYPE");
            data.setSort(condition.getAlarmTypeSort());
            data.setData(null == condition.getAlarmTypeList() ? Collections.emptyList() : condition.getAlarmTypeList());
            result.getConditionList().add(data);
        }
        if (StrUtil.equals(RuleStatusEnum.ENABLE.getCode(), condition.getAlarmLevelFlag())) {
            AlarmPushRuleConfigConditionResponse data = new AlarmPushRuleConfigConditionResponse();
            data.setType("ALARM_LEVEL");
            data.setSort(condition.getAlarmLevelSort());
            data.setData(null == condition.getAlarmLevelList() ? Collections.emptyList() : condition.getAlarmLevelList());
            result.getConditionList().add(data);
        }
        if (StrUtil.equals(RuleStatusEnum.ENABLE.getCode(), condition.getAlarmStatusFlag())) {
            AlarmPushRuleConfigConditionResponse data = new AlarmPushRuleConfigConditionResponse();
            data.setType("ALARM_STATUS");
            data.setSort(condition.getAlarmStatusSort());
            data.setData(null == condition.getAlarmStatusList() ? Collections.emptyList() : condition.getAlarmStatusList());
            result.getConditionList().add(data);
        }
        result.getConditionList().sort(Comparator.comparing(AlarmPushRuleConfigConditionResponse::getSort));

        AlarmPushStatusEntity pushStatus = alarmPushStatusMapper.selectOne(new LambdaQueryWrapper<AlarmPushStatusEntity>().eq(AlarmPushStatusEntity::getRuleId, id));
        HashMap<Integer, AlarmPushRuleConfigPushResponse> pushMap = new HashMap<>();
        if (StrUtil.equals(RuleStatusEnum.ENABLE.getCode(), pushStatus.getEmailStatus())) {
            AlarmPushRuleConfigPushResponse data = new AlarmPushRuleConfigPushResponse();
            data.setType(0);
            data.setSort(pushStatus.getEmailSort());
            data.setData(new ArrayList<>());
            result.getPushList().add(data);
            pushMap.put(0, data);
        }
        if (StrUtil.equals(RuleStatusEnum.ENABLE.getCode(), pushStatus.getMessageStatus())) {
            AlarmPushRuleConfigPushResponse data = new AlarmPushRuleConfigPushResponse();
            data.setType(1);
            data.setSort(pushStatus.getMessageSort());
            data.setData(new ArrayList<>());
            result.getPushList().add(data);
            pushMap.put(1, data);
        }
        if (StrUtil.equals(RuleStatusEnum.ENABLE.getCode(), pushStatus.getDingStatus())) {
            AlarmPushRuleConfigPushResponse data = new AlarmPushRuleConfigPushResponse();
            data.setType(2);
            data.setSort(pushStatus.getDingSort());
            data.setData(new ArrayList<>());
            result.getPushList().add(data);
            pushMap.put(2, data);
        }
        result.getPushList().sort(Comparator.comparing(AlarmPushRuleConfigPushResponse::getSort));
        alarmPushUserMapper.selectPageUserList(CollUtil.newArrayList(id))
                .forEach(o -> {
                    switch (o.getPushType()) {
                        case 0 -> pushMap.get(0).getData().add(o.getUserId().toString());
                        case 1 -> pushMap.get(1).getData().add(o.getUserId().toString());
                        case 2 -> pushMap.get(1).getData().add(o.getDingUrl());
                        default -> log.error("告警推送方式类型异常 {}", o);
                    }
                });

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void configSave(AlarmPushRuleConfigSaveRequest request) {
        Long ruleId = request.getId();
        AlarmPushRuleEntity rule = getById(ruleId);
        if (null == rule) {
            throw new BusinessException("告警推送规则不存在");
        }
        AlarmPushConditionEntity condition = alarmPushConditionMapper.selectOne(new LambdaQueryWrapper<AlarmPushConditionEntity>().eq(AlarmPushConditionEntity::getRuleId, ruleId));
        if (request.getSelectAllProject()) {
            condition.setBizProjectIdList(CollUtil.newArrayList("0"));
        } else {
            condition.setBizProjectIdList(request.getBizProjectIdList());
        }
        List<AlarmPushRuleConfigConditionRequest> conditionList = request.getConditionList();
        String alarmTypeFlag = RuleStatusEnum.DISABLE.getCode();
        String alarmLevelFlag = RuleStatusEnum.DISABLE.getCode();
        String alarmStatusFlag = RuleStatusEnum.DISABLE.getCode();
        List<String> alarmTypeList = null;
        List<String> alarmLevelList = null;
        List<String> alarmStatusList = null;
        if (CollUtil.isNotEmpty(conditionList)) {
            for (int i = 0; i < conditionList.size(); i++) {
                AlarmPushRuleConfigConditionRequest conditionRequest = conditionList.get(i);
                switch (conditionRequest.getType()) {
                    case "ALARM_TYPE" -> {
                        condition.setAlarmTypeSort(i);
                        alarmTypeFlag = RuleStatusEnum.ENABLE.getCode();
                        alarmTypeList = conditionRequest.getData();
                    }
                    case "ALARM_LEVEL" -> {
                        condition.setAlarmLevelSort(i);
                        alarmLevelFlag = RuleStatusEnum.ENABLE.getCode();
                        alarmLevelList = conditionRequest.getData();
                    }
                    case "ALARM_STATUS" -> {
                        condition.setAlarmStatusSort(i);
                        alarmStatusFlag = RuleStatusEnum.ENABLE.getCode();
                        alarmStatusList = conditionRequest.getData();
                    }
                    default -> throw new IllegalArgumentException("告警条件类型错误");
                }
            }
        }
        condition.setAlarmTypeFlag(alarmTypeFlag).setAlarmLevelFlag(alarmLevelFlag).setAlarmStatusFlag(alarmStatusFlag)
                .setAlarmTypeList(alarmTypeList).setAlarmLevelList(alarmLevelList).setAlarmStatusList(alarmStatusList);

        alarmPushConditionMapper.updateById(condition);

        AlarmPushStatusEntity pushStatus = alarmPushStatusMapper.selectOne(new LambdaQueryWrapper<AlarmPushStatusEntity>().eq(AlarmPushStatusEntity::getRuleId, ruleId));
        String emailStatus = RuleStatusEnum.DISABLE.getCode();
        String messageStatus = RuleStatusEnum.DISABLE.getCode();
        String dingStatus = RuleStatusEnum.DISABLE.getCode();
        List<String> emailUseridList = null;
        List<String> messageUseridList = null;
        List<String> dingUrlList = null;
        List<AlarmPushRuleConfigPushRequest> pushList = request.getPushList();
        if (CollUtil.isNotEmpty(pushList)) {
            for (int i = 0; i < pushList.size(); i++) {
                AlarmPushRuleConfigPushRequest pushRequest = pushList.get(i);
                switch (pushRequest.getType()) {
                    case 0 -> {
                        pushStatus.setEmailSort(i);
                        emailStatus = RuleStatusEnum.ENABLE.getCode();
                        emailUseridList = pushRequest.getData();
                        if (CollUtil.isEmpty(emailUseridList)) {
                            throw new BusinessException("邮件推送联系人不能为空");
                        }
                    }
                    case 1 -> {
                        pushStatus.setMessageSort(i);
                        messageStatus = RuleStatusEnum.ENABLE.getCode();
                        messageUseridList = pushRequest.getData();
                        if (CollUtil.isEmpty(messageUseridList)) {
                            throw new BusinessException("短信推送联系人不能为空");
                        }
                    }
                    case 2 -> {
                        pushStatus.setDingSort(i);
                        dingStatus = RuleStatusEnum.ENABLE.getCode();
                        dingUrlList = pushRequest.getData();
                        if (CollUtil.isEmpty(dingUrlList)) {
                            throw new BusinessException("钉钉推送机器人地址不能为空");
                        }
                    }
                    default -> throw new IllegalArgumentException("推送方式类型错误");
                }
            }
        }
        pushStatus.setEmailStatus(emailStatus)
                .setMessageStatus(messageStatus)
                .setDingStatus(dingStatus);
        alarmPushStatusMapper.updateById(pushStatus);

        alarmPushUserMapper.delete(new LambdaUpdateWrapper<AlarmPushUserEntity>().eq(AlarmPushUserEntity::getRuleId, ruleId));
        if (StrUtil.equals(emailStatus, RuleStatusEnum.ENABLE.getCode()) && CollUtil.isNotEmpty(emailUseridList)) {
            emailUseridList.forEach(o -> {
                AlarmPushUserEntity user = new AlarmPushUserEntity()
                        .setRuleId(ruleId).setStatusId(pushStatus.getId())
                        .setUserId(Long.valueOf(o)).setPushType(0);
                alarmPushUserMapper.insert(user);
            });
        }
        if (StrUtil.equals(messageStatus, RuleStatusEnum.ENABLE.getCode()) && CollUtil.isNotEmpty(messageUseridList)) {
            messageUseridList.forEach(o -> {
                AlarmPushUserEntity user = new AlarmPushUserEntity()
                        .setRuleId(ruleId).setStatusId(pushStatus.getId())
                        .setUserId(Long.valueOf(o)).setPushType(1);
                alarmPushUserMapper.insert(user);
            });
        }
        if (StrUtil.equals(dingStatus, RuleStatusEnum.ENABLE.getCode()) && CollUtil.isNotEmpty(dingUrlList)) {
            dingUrlList.forEach(o -> {
                AlarmPushUserEntity user = new AlarmPushUserEntity()
                        .setRuleId(ruleId).setStatusId(pushStatus.getId())
                        .setDingUrl(o).setPushType(2);
                alarmPushUserMapper.insert(user);
            });
        }
    }
}