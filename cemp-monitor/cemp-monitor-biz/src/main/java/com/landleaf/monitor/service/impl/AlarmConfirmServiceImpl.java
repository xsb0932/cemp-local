package com.landleaf.monitor.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.exception.enums.GlobalErrorCodeConstants;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.monitor.api.AlarmApiImpl;
import com.landleaf.monitor.dal.mapper.AlarmConfirmMapper;
import com.landleaf.monitor.dal.mapper.HistoryEventMapper;
import com.landleaf.monitor.dal.mapper.UnconfirmedEventMapper;
import com.landleaf.monitor.domain.entity.AlarmConfirmEntity;
import com.landleaf.monitor.domain.entity.HistoryEventEntity;
import com.landleaf.monitor.domain.entity.UnconfirmedEventEntity;
import com.landleaf.monitor.domain.enums.AlarmConfirmTypeEnum;
import com.landleaf.monitor.domain.request.AlarmConfirmRequest;
import com.landleaf.monitor.service.AlarmConfirmService;
import com.landleaf.monitor.service.UnconfirmedEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AlarmConfirmServiceImpl
 *
 * @author 张力方
 * @since 2023/8/15
 **/
@Service
@RequiredArgsConstructor
public class AlarmConfirmServiceImpl implements AlarmConfirmService {
    private final AlarmConfirmMapper alarmConfirmMapper;

    private final HistoryEventMapper historyEventMapper;

    private final UnconfirmedEventService unconfirmedEventServiceImpl;

    private final UnconfirmedEventMapper unconfirmedEventMapper;

    private final AlarmApiImpl alarmApiImpl;

    @Override
    public void confirm(AlarmConfirmRequest request) {
        List<HistoryEventEntity> eventList = historyEventMapper.selectList(new QueryWrapper<HistoryEventEntity>().lambda().eq(HistoryEventEntity::getEventId, request.getEventId()));
        if (CollectionUtils.isEmpty(eventList)) {
            throw new BusinessException(GlobalErrorCodeConstants.ALARM_NOT_EXISTS);
        }

        AlarmConfirmEntity alarmConfirmEntity = alarmConfirmMapper.selectOne(Wrappers.<AlarmConfirmEntity>lambdaQuery()
                .eq(AlarmConfirmEntity::getEventId, request.getEventId()));
        if (null != alarmConfirmEntity) {
            throw new BusinessException(GlobalErrorCodeConstants.ALARM_CONFIRM_EXISTS);
        }
        alarmConfirmEntity = new AlarmConfirmEntity();
        Long tenantId = TenantContext.getTenantId();
        alarmConfirmEntity.setAlarmConfirmType(AlarmConfirmTypeEnum.MANUAL.getCode());
        alarmConfirmEntity.setEventId(request.getEventId());
        alarmConfirmEntity.setIsConfirm(Boolean.TRUE);
        alarmConfirmEntity.setConfirmTime(LocalDateTime.now());
        alarmConfirmEntity.setConfirmUser(LoginUserUtil.getLoginUserId());
        alarmConfirmEntity.setRemark(request.getRemark());
        alarmConfirmMapper.insert(alarmConfirmEntity);

        unconfirmedEventServiceImpl.delEvent(request.getEventId());

        // 通知，修改告警数量
        alarmApiImpl.changeAlarmCount(tenantId, eventList.get(0).getProjectBizId());
    }

    /**
     *
     */
    @Override
    @Transactional
    public void sysConfirm() {
        TenantContext.setIgnore(true);
        LocalDateTime limit = LocalDateTime.now();
        limit = limit.plusMonths(1);
        List<UnconfirmedEventEntity> eventList = unconfirmedEventMapper.selectList(new QueryWrapper<UnconfirmedEventEntity>().lambda()
                .lt(UnconfirmedEventEntity::getEventTime, limit));
        if (CollectionUtils.isEmpty(eventList)) {
            // 没有要处理的，直接返回
            return;
        }
        List<AlarmConfirmEntity> alarmConfirmEntityList = alarmConfirmMapper.selectList(Wrappers.<AlarmConfirmEntity>lambdaQuery()
                .eq(AlarmConfirmEntity::getDeleted, 0)
                .in(AlarmConfirmEntity::getEventId, eventList.stream().map(UnconfirmedEventEntity::getEventId).collect(Collectors.toList())));
        Map<String, AlarmConfirmEntity> exists = new HashMap<>();
        if (!CollectionUtils.isEmpty(alarmConfirmEntityList)) {
            exists = alarmConfirmEntityList.stream().collect(Collectors.toMap(AlarmConfirmEntity::getEventId, i -> i, (v1, v2) -> v1));
        }
        AlarmConfirmEntity alarmConfirmEntity = null;
        for (UnconfirmedEventEntity entity : eventList) {
            // 添加系统自动确认
            if (exists.containsKey(entity.getEventId())) {
                continue;
            }
            alarmConfirmEntity = new AlarmConfirmEntity();
            alarmConfirmEntity.setAlarmConfirmType(AlarmConfirmTypeEnum.AUTO.getCode());
            alarmConfirmEntity.setEventId(entity.getEventId());
            alarmConfirmEntity.setIsConfirm(Boolean.TRUE);
            alarmConfirmEntity.setConfirmTime(LocalDateTime.now());
            alarmConfirmEntity.setConfirmUser(0L);
            alarmConfirmEntity.setTenantId(entity.getTenantId());
            alarmConfirmMapper.insert(alarmConfirmEntity);
        }
        // 删除对应的current
        unconfirmedEventMapper.deleteByIds(eventList.stream().map(UnconfirmedEventEntity::getId).collect(Collectors.toList()));

        // 通知数量变更
        // 获取proj和tenant的对应关系并去重
        Map<String, Long> bizProjMap = eventList.stream().collect(Collectors.toMap(UnconfirmedEventEntity::getProjectBizId, UnconfirmedEventEntity::getTenantId, (v1, v2) -> v1));
        bizProjMap.forEach((k, v) -> {
            alarmApiImpl.changeAlarmCount(v, k);
        });
    }
}
