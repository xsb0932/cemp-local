package com.landleaf.monitor;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.job.api.dto.JobRpcRequest;
import com.landleaf.kafka.sender.KafkaSender;
import com.landleaf.monitor.api.DeviceAlarmApi;
import com.landleaf.monitor.api.MonitorApiImpl;
import com.landleaf.monitor.controller.DeviceServiceController;
import com.landleaf.monitor.controller.TopicGetController;
import com.landleaf.monitor.dal.mapper.AlarmConfirmMapper;
import com.landleaf.monitor.dal.mapper.CurrentAlarmMapper;
import com.landleaf.monitor.dal.mapper.HistoryEventMapper;
import com.landleaf.monitor.domain.entity.AlarmConfirmEntity;
import com.landleaf.monitor.domain.entity.CurrentAlarmEntity;
import com.landleaf.monitor.domain.enums.*;
import com.landleaf.monitor.domain.request.AlarmConfirmRequest;
import com.landleaf.monitor.service.AlarmConfirmService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.landleaf.kafka.conf.TopicDefineConst.DEVICE_STATUS_UPLOAD_TOPIC_ACK;

/**
 * AlarmTest
 *
 * @author 张力方
 * @since 2023/8/23
 **/
@SpringBootTest
class AlarmTest {

    @Autowired
    CurrentAlarmMapper currentAlarmMapper;

    @Autowired
    HistoryEventMapper historyEventMapper;

    @Autowired
    AlarmConfirmMapper alarmConfirmMapper;

    @Autowired
    AlarmConfirmService alarmConfirmService;

    @Autowired
    DeviceServiceController deviceServiceController;

    @Autowired
    MonitorApiImpl monitorApiImpl;

    @Autowired
    private TopicGetController topicGetController;

    @Autowired
    DeviceAlarmApi deviceAlarmApiImpl;


    @Test
    void getCurrentAlarm() {
        System.out.println(JSON.toJSONString(deviceAlarmApiImpl.query(Lists.newArrayList("D000000001354"), Lists.newArrayList("supply_temp"))));
    }

    @Test
    void testExpire() {
        JobRpcRequest request = new JobRpcRequest();
        request.setJobId(1L).setExecUser(1L).setExecType(1);
        topicGetController.clearUnusedTopic(request);
    }

    @Test
    void testNotice() {
        monitorApiImpl.changeStatusNotice("D000000001251", "1", "Ua", BigDecimal.valueOf(329), null);
    }

    @Test
    void getDeviceService() {
        deviceServiceController.getDetail("D000000001249");
    }

    @Test
    void confirmAlarm() {
        TenantContext.setTenantId(2L);
        AlarmConfirmRequest req = new AlarmConfirmRequest();
        req.setEventId("E000000094");
        req.setRemark("test");
        alarmConfirmService.confirm(req);
    }

    @Test
    void initCurrentAlarm() {
        TenantContext.setIgnore(true);
        CurrentAlarmEntity currentAlarmEntity = new CurrentAlarmEntity();
        currentAlarmEntity.setTenantId(2L);
        currentAlarmEntity.setProjectBizId("PJ00000001");
        currentAlarmEntity.setAlarmType(AlarmTypeEnum.DEVICE_ALARM.getCode());
        currentAlarmEntity.setAlarmCode("xxxxtest1");
        currentAlarmEntity.setAlarmDesc("测试告警xxxxxxxx0001");
        currentAlarmEntity.setAlarmLevel(AlarmLevelEnum.ERROR.getCode());
        currentAlarmEntity.setAlarmStatus(AlarmStatusEnum.TRIGGER_RESET.getCode());
        currentAlarmEntity.setAlarmObjType(AlarmObjTypeEnum.DEVICE.getCode());
        currentAlarmEntity.setAlarmBizId("A00001");
        currentAlarmEntity.setEventId("event001");
        currentAlarmEntity.setEventTime(LocalDateTime.now());
        currentAlarmEntity.setEventType(EventTypeEnum.DEVICE_POST.getCode());
        currentAlarmEntity.setObjId("D000000001031");
        currentAlarmMapper.insert(currentAlarmEntity);

        CurrentAlarmEntity currentAlarmEntity2 = new CurrentAlarmEntity();
        currentAlarmEntity2.setTenantId(2L);
        currentAlarmEntity2.setProjectBizId("PJ00000001");
        currentAlarmEntity2.setAlarmType(AlarmTypeEnum.DEVICE_ALARM.getCode());
        currentAlarmEntity2.setAlarmCode("xxxxtest2");
        currentAlarmEntity2.setAlarmDesc("测试告警xxxxxxxx0002");
        currentAlarmEntity2.setAlarmLevel(AlarmLevelEnum.ERROR.getCode());
        currentAlarmEntity2.setAlarmStatus(AlarmStatusEnum.TRIGGER_RESET.getCode());
        currentAlarmEntity2.setAlarmObjType(AlarmObjTypeEnum.DEVICE.getCode());
        currentAlarmEntity2.setAlarmBizId("A00002");
        currentAlarmEntity2.setEventId("event002");
        currentAlarmEntity2.setEventTime(LocalDateTime.now());
        currentAlarmEntity2.setEventType(EventTypeEnum.DEVICE_POST.getCode());
        currentAlarmEntity2.setObjId("D000000001031");
        currentAlarmMapper.insert(currentAlarmEntity2);

        CurrentAlarmEntity currentAlarmEntity3 = new CurrentAlarmEntity();
        currentAlarmEntity3.setTenantId(2L);
        currentAlarmEntity3.setProjectBizId("PJ00000001");
        currentAlarmEntity3.setAlarmType(AlarmTypeEnum.DEVICE_ALARM.getCode());
        currentAlarmEntity3.setAlarmCode("xxxxtest3");
        currentAlarmEntity3.setAlarmDesc("测试告警xxxxxxxx0003");
        currentAlarmEntity3.setAlarmLevel(AlarmLevelEnum.ERROR.getCode());
        currentAlarmEntity3.setAlarmStatus(AlarmStatusEnum.TRIGGER_RESET.getCode());
        currentAlarmEntity3.setAlarmObjType(AlarmObjTypeEnum.DEVICE.getCode());
        currentAlarmEntity3.setAlarmBizId("A00003");
        currentAlarmEntity3.setEventId("event003");
        currentAlarmEntity3.setEventTime(LocalDateTime.now());
        currentAlarmEntity3.setEventType(EventTypeEnum.DEVICE_POST.getCode());
        currentAlarmEntity3.setObjId("D000000001031");
        currentAlarmMapper.insert(currentAlarmEntity3);
    }

    @Test
    void initHistoryEvent() {
//        TenantContext.setIgnore(true);
//        HistoryEventEntity historyEventEntity = new HistoryEventEntity();
//        historyEventEntity.setTenantId(2L);
//        historyEventEntity.setProjectBizId("PJ00000001");
//        historyEventEntity.setAlarmType(AlarmTypeEnum.DEVICE_ALARM.getCode());
//        historyEventEntity.setAlarmCode("xxxxtest1");
//        historyEventEntity.setAlarmDesc("测试告警xxxxxxxx0001");
//        historyEventEntity.setAlarmLevel(AlarmLevelEnum.ERROR.getCode());
//        historyEventEntity.setAlarmStatus(AlarmStatusEnum.TRIGGER_RESET.getCode());
//        historyEventEntity.setAlarmObjType(AlarmObjTypeEnum.DEVICE.getCode());
//        historyEventEntity.setAlarmBizId("A00001");
//        historyEventEntity.setEventId("event001");
//        historyEventEntity.setEventTime(LocalDateTime.now());
//        historyEventEntity.setEventType(EventTypeEnum.DEVICE_POST.getCode());
//        historyEventEntity.setObjId("D000000001031");
//        historyEventMapper.insert(historyEventEntity);
//
//        HistoryEventEntity historyEventEntity2 = new HistoryEventEntity();
//        historyEventEntity2.setTenantId(2L);
//        historyEventEntity2.setProjectBizId("PJ00000001");
//        historyEventEntity2.setAlarmType(AlarmTypeEnum.DEVICE_ALARM.getCode());
//        historyEventEntity2.setAlarmCode("xxxxtest2");
//        historyEventEntity2.setAlarmDesc("测试告警xxxxxxxx0002");
//        historyEventEntity2.setAlarmLevel(AlarmLevelEnum.ERROR.getCode());
//        historyEventEntity2.setAlarmStatus(AlarmStatusEnum.TRIGGER_RESET.getCode());
//        historyEventEntity2.setAlarmObjType(AlarmObjTypeEnum.DEVICE.getCode());
//        historyEventEntity2.setAlarmBizId("A00002");
//        historyEventEntity2.setEventId("event002");
//        historyEventEntity2.setEventTime(LocalDateTime.now());
//        historyEventEntity2.setEventType(EventTypeEnum.DEVICE_POST.getCode());
//        historyEventEntity2.setObjId("D000000001031");
//        historyEventMapper.insert(historyEventEntity2);
//
//        HistoryEventEntity historyEventEntity3 = new HistoryEventEntity();
//        historyEventEntity3.setTenantId(2L);
//        historyEventEntity3.setProjectBizId("PJ00000001");
//        historyEventEntity3.setAlarmType(AlarmTypeEnum.DEVICE_ALARM.getCode());
//        historyEventEntity3.setAlarmCode("xxxxtest3");
//        historyEventEntity3.setAlarmDesc("测试告警xxxxxxxx0003");
//        historyEventEntity3.setAlarmLevel(AlarmLevelEnum.ERROR.getCode());
//        historyEventEntity3.setAlarmStatus(AlarmStatusEnum.TRIGGER_RESET.getCode());
//        historyEventEntity3.setAlarmObjType(AlarmObjTypeEnum.DEVICE.getCode());
//        historyEventEntity3.setAlarmBizId("A00003");
//        historyEventEntity3.setEventId("event003");
//        historyEventEntity3.setEventTime(LocalDateTime.now());
//        historyEventEntity3.setEventType(EventTypeEnum.DEVICE_POST.getCode());
//        historyEventEntity3.setObjId("D000000001031");
//        historyEventMapper.insert(historyEventEntity3);


        AlarmConfirmEntity alarmConfirmEntity = new AlarmConfirmEntity();
        alarmConfirmEntity.setTenantId(2L);
        alarmConfirmEntity.setEventId("A00001");
        alarmConfirmEntity.setAlarmConfirmType(AlarmConfirmTypeEnum.MANUAL.getCode());
        alarmConfirmMapper.insert(alarmConfirmEntity);

        AlarmConfirmEntity alarmConfirmEntity2 = new AlarmConfirmEntity();
        alarmConfirmEntity2.setTenantId(2L);
        alarmConfirmEntity2.setEventId("A00002");
        alarmConfirmEntity2.setAlarmConfirmType(AlarmConfirmTypeEnum.MANUAL.getCode());
        alarmConfirmMapper.insert(alarmConfirmEntity2);

        AlarmConfirmEntity alarmConfirmEntity3 = new AlarmConfirmEntity();
        alarmConfirmEntity3.setTenantId(2L);
        alarmConfirmEntity3.setEventId("A00003");
        alarmConfirmEntity3.setAlarmConfirmType(AlarmConfirmTypeEnum.MANUAL.getCode());
        alarmConfirmMapper.insert(alarmConfirmEntity3);
    }

    @Autowired
    private KafkaSender kafkaSender;

    @Test
    public void testKafka() {
        kafkaSender.send(DEVICE_STATUS_UPLOAD_TOPIC_ACK + "GW00000009", "cloud to gateway");
    }

}
