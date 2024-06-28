package com.landleaf.jjgj.service.impl;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.energy.api.PlanedElectricityApi;
import com.landleaf.energy.api.PlannedGasApi;
import com.landleaf.energy.api.PlannedWaterApi;
import com.landleaf.energy.api.ProjectStaSubitemDayApi;
import com.landleaf.job.api.dto.JobRpcRequest;
import com.landleaf.monitor.api.AlarmApi;
import com.landleaf.monitor.dto.AlarmAddRequest;
import com.landleaf.monitor.dto.ProductAlarmConf;
import com.landleaf.monitor.enums.AlarmObjTypeEnum;
import com.landleaf.monitor.enums.AlarmStatusEnum;
import com.landleaf.monitor.enums.AlarmTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnergyAlarmService {
    private final PlanedElectricityApi planedElectricityApi;
    private final PlannedGasApi plannedGasApi;
    private final PlannedWaterApi plannedWaterApi;
    private final ProjectStaSubitemDayApi projectStaSubitemDayApi;
    private final AlarmApi alarmApi;

    public void lastWeek(JobRpcRequest request) {
        LocalDateTime now = null != request.getExecTime() ? request.getExecTime() : LocalDateTime.now();
        // 获取上周一的LocalDate
        LocalDate lastMonday = now.toLocalDate().minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        // 获取上周日的LocalDate
        LocalDate lastSunday = lastMonday.plusDays(6);
        // 电
        BigDecimal planElectricity = planedElectricityApi.getProjectDurationTotalPlan("PJ00000001", lastMonday, lastSunday).getCheckedData();
        BigDecimal electricity = projectStaSubitemDayApi.getProjectElectricityDurationTotal("PJ00000001", lastMonday, lastSunday).getCheckedData();
        log.info("计划用电 {} 实际用电 {}", planElectricity, electricity);
        if (null != planElectricity && (electricity.compareTo(planElectricity) > 0 || electricity.compareTo(planElectricity.multiply(new BigDecimal("0.4"))) < 0)) {
            AlarmAddRequest alarmParam = new AlarmAddRequest();

            ProductAlarmConf alarmInfo = new ProductAlarmConf();
            if (electricity.compareTo(planElectricity) > 0) {
                alarmInfo.setAlarmCode("PJ_EU_001").setAlarmDesc("上周用电量超过计划值").setAlarmTriggerLevel("03");
            } else {
                alarmInfo.setAlarmCode("PJ_EU_002").setAlarmDesc("上周用电量低于计划值的40%").setAlarmTriggerLevel("03");
            }
            alarmParam.setTenantId(2L)
                    .setBizProjId("PJ00000001")
                    .setAlarmInfo(alarmInfo)
                    .setAlarmObjType(AlarmObjTypeEnum.PROJECT.getCode())
                    .setObjId("PJ00000001")
                    .setAlarmType(AlarmTypeEnum.RULE_ALARM.getCode())
                    .setAlarmStatus(AlarmStatusEnum.TRIGGER_NO_RESET.getCode())
                    .setTime(System.currentTimeMillis());
            Response<Boolean> response = alarmApi.addCurrentAlarm(alarmParam);
            log.info("产生用电告警 {}", response);
        }
        // 气
        BigDecimal planGas = plannedGasApi.getProjectDurationTotalPlan("PJ00000001", lastMonday, lastSunday).getCheckedData();
        BigDecimal gas = projectStaSubitemDayApi.getProjectGasDurationTotal("PJ00000001", lastMonday, lastSunday).getCheckedData();
        log.info("计划用气 {} 实际用气 {}", planGas, gas);
        if (null != planGas && (gas.compareTo(planGas) > 0 || gas.compareTo(planGas.multiply(new BigDecimal("0.4"))) < 0)) {
            AlarmAddRequest alarmParam = new AlarmAddRequest();

            ProductAlarmConf alarmInfo = new ProductAlarmConf();
            if (gas.compareTo(planGas) > 0) {
                alarmInfo.setAlarmCode("PJ_GU_001").setAlarmDesc("上周用气量超过计划值").setAlarmTriggerLevel("03");
            } else {
                alarmInfo.setAlarmCode("PJ_GU_002").setAlarmDesc("上周用气量低于计划值的40%").setAlarmTriggerLevel("03");
            }
            alarmParam.setTenantId(2L)
                    .setBizProjId("PJ00000001")
                    .setAlarmInfo(alarmInfo)
                    .setAlarmObjType(AlarmObjTypeEnum.PROJECT.getCode())
                    .setObjId("PJ00000001")
                    .setAlarmType(AlarmTypeEnum.RULE_ALARM.getCode())
                    .setAlarmStatus(AlarmStatusEnum.TRIGGER_NO_RESET.getCode())
                    .setTime(System.currentTimeMillis());
            Response<Boolean> response = alarmApi.addCurrentAlarm(alarmParam);
            log.info("产生用气告警 {}", response);
        }
        // 水
        BigDecimal planWater = plannedWaterApi.getProjectDurationTotalPlan("PJ00000001", lastMonday, lastSunday).getCheckedData();
        BigDecimal water = projectStaSubitemDayApi.getProjectWaterDurationTotal("PJ00000001", lastMonday, lastSunday).getCheckedData();
        log.info("计划用水 {} 实际用水 {}", planWater, water);
        if (null != planWater && (water.compareTo(planWater) > 0 || water.compareTo(planWater.multiply(new BigDecimal("0.4"))) < 0)) {
            AlarmAddRequest alarmParam = new AlarmAddRequest();

            ProductAlarmConf alarmInfo = new ProductAlarmConf();
            if (water.compareTo(planWater) > 0) {
                alarmInfo.setAlarmCode("PJ_WU_001").setAlarmDesc("上周用水量超过计划值").setAlarmTriggerLevel("03");
            } else {
                alarmInfo.setAlarmCode("PJ_WU_002").setAlarmDesc("上周用水量低于计划值的40%").setAlarmTriggerLevel("03");
            }
            alarmParam.setTenantId(2L)
                    .setBizProjId("PJ00000001")
                    .setAlarmInfo(alarmInfo)
                    .setAlarmObjType(AlarmObjTypeEnum.PROJECT.getCode())
                    .setObjId("PJ00000001")
                    .setAlarmType(AlarmTypeEnum.RULE_ALARM.getCode())
                    .setAlarmStatus(AlarmStatusEnum.TRIGGER_NO_RESET.getCode())
                    .setTime(System.currentTimeMillis());
            Response<Boolean> response = alarmApi.addCurrentAlarm(alarmParam);
            log.info("产生用水告警 {}", response);
        }
    }
}
