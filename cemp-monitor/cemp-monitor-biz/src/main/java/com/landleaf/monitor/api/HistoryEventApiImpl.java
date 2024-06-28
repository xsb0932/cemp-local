package com.landleaf.monitor.api;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.monitor.api.dto.DeviceManagerEventHistoryDTO;
import com.landleaf.monitor.api.dto.DeviceManagerEventHistoryPageDTO;
import com.landleaf.monitor.api.dto.DeviceServiceEventAddDTO;
import com.landleaf.monitor.api.request.DeviceManagerEventExportRequest;
import com.landleaf.monitor.api.request.DeviceManagerEventPageRequest;
import com.landleaf.monitor.api.request.DeviceServiceEventAddRequest;
import com.landleaf.monitor.dal.mapper.AlarmConfirmMapper;
import com.landleaf.monitor.dal.mapper.HistoryEventMapper;
import com.landleaf.monitor.domain.entity.AlarmConfirmEntity;
import com.landleaf.monitor.domain.entity.HistoryEventEntity;
import com.landleaf.monitor.domain.enums.AlarmConfirmTypeEnum;
import com.landleaf.monitor.domain.enums.AlarmLevelEnum;
import com.landleaf.monitor.domain.enums.AlarmStatusEnum;
import com.landleaf.monitor.domain.enums.AlarmTypeEnum;
import com.landleaf.monitor.enums.AlarmObjTypeEnum;
import com.landleaf.monitor.enums.EventTypeEnum;
import com.landleaf.pgsql.core.BizSequenceService;
import com.landleaf.pgsql.enums.BizSequenceEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class HistoryEventApiImpl implements HistoryEventApi {
    private final HistoryEventMapper historyEventMapper;
    private final BizSequenceService bizSequenceService;
    private final AlarmConfirmMapper alarmConfirmMapper;

    @Override
    public Response<DeviceManagerEventHistoryPageDTO> deviceEventsHistory(DeviceManagerEventPageRequest request) {
        Page<DeviceManagerEventHistoryDTO> page = historyEventMapper.deviceEventsHistory(
                Page.of(request.getPageNo(), request.getPageSize()),
                request.getBizDeviceId(),
                request.getAlarmType(),
                request.getStartTime(),
                request.getEndTime()
        );
        page.getRecords()
                .forEach(o -> o.setAlarmTypeName(AlarmTypeEnum.getName(o.getAlarmType()))
                        .setAlarmLevelName(AlarmLevelEnum.getName(o.getAlarmLevel()))
                        .setAlarmStatusName(AlarmStatusEnum.getName(o.getAlarmStatus())));
        return Response.success(
                new DeviceManagerEventHistoryPageDTO()
                        .setSize(page.getSize())
                        .setCurrent(page.getCurrent())
                        .setTotal(page.getTotal())
                        .setRecords(page.getRecords())
        );
    }

    @Override
    public Response<List<DeviceManagerEventHistoryDTO>> deviceEventsHistoryExport(DeviceManagerEventExportRequest request) {
        List<DeviceManagerEventHistoryDTO> result = historyEventMapper.deviceEventsHistoryExport(
                request.getBizDeviceId(),
                request.getAlarmType(),
                request.getStartTime(),
                request.getEndTime()
        );
        result.forEach(o -> o.setAlarmTypeName(AlarmTypeEnum.getName(o.getAlarmType()))
                .setAlarmLevelName(AlarmLevelEnum.getName(o.getAlarmLevel()))
                .setAlarmStatusName(AlarmStatusEnum.getName(o.getAlarmStatus())));
        return Response.success(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<DeviceServiceEventAddDTO> addDeviceServiceEvent(DeviceServiceEventAddRequest request) {
        TenantContext.setIgnore(true);
        DeviceServiceEventAddDTO result = new DeviceServiceEventAddDTO();
        HistoryEventEntity historyEventEntity = new HistoryEventEntity();
        historyEventEntity.setTenantId(request.getTenantId());
        historyEventEntity.setProjectBizId(request.getBizProjectId());
        historyEventEntity.setAlarmType(AlarmTypeEnum.SERVICE_EVENT.getCode());
        historyEventEntity.setAlarmObjType(AlarmObjTypeEnum.DEVICE.getCode());
        historyEventEntity.setAlarmLevel(AlarmLevelEnum.INFO.getCode());
        historyEventEntity.setObjId(request.getBizDeviceId());
        historyEventEntity.setEventTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(request.getTime()), ZoneId.systemDefault()));
        historyEventEntity.setEventType(EventTypeEnum.COMMON_EVENT.getCode());
        historyEventEntity.setAlarmStatus(AlarmStatusEnum.RESET.getCode());
        historyEventEntity.setAlarmCode(request.getServiceId());
        historyEventEntity.setAlarmDesc(request.getServiceDesc());

        historyEventEntity.setAlarmBizId(bizSequenceService.next(BizSequenceEnum.ALARM));
        historyEventEntity.setEventId(bizSequenceService.next(BizSequenceEnum.EVENT));

        historyEventMapper.insert(historyEventEntity);
        BeanUtil.copyProperties(historyEventEntity, result);

        AlarmConfirmEntity alarmConfirmEntity = new AlarmConfirmEntity();
        alarmConfirmEntity.setTenantId(request.getTenantId());
        alarmConfirmEntity.setEventId(historyEventEntity.getEventId());
        alarmConfirmEntity.setAlarmConfirmType(AlarmConfirmTypeEnum.AUTO.getCode());
        alarmConfirmEntity.setIsConfirm(true);
        alarmConfirmEntity.setConfirmUser(0L);
        alarmConfirmEntity.setConfirmTime(LocalDateTime.now());
        alarmConfirmMapper.insert(alarmConfirmEntity);
        return Response.success(result);
    }
}
