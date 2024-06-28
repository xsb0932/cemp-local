package com.landleaf.monitor.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.constance.ErrorCodeEnumConst;
import com.landleaf.comm.constance.PeriodTypeConst;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.exception.enums.GlobalErrorCodeConstants;
import com.landleaf.data.api.device.DeviceHistoryApi;
import com.landleaf.data.api.device.dto.DeviceHistoryDTO;
import com.landleaf.data.api.device.dto.HistoryQueryInnerDTO;
import com.landleaf.monitor.domain.dto.HistoryQueryDTO;
import com.landleaf.monitor.domain.entity.DeviceMonitorEntity;
import com.landleaf.monitor.domain.vo.DeviceAttrDisplayVO;
import com.landleaf.monitor.service.DeviceHistoryService;
import com.landleaf.monitor.service.DeviceMonitorService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.Struct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DeviceHistoryServiceImpl implements DeviceHistoryService {

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Resource
    private DeviceHistoryApi deviceHistoryApi;

    @Resource
    private DeviceMonitorService deviceMonitorServiceImpl;

    private static Map<String, DeviceAttrDisplayVO> map;

    static {
        List<DeviceAttrDisplayVO> list = new ArrayList();
        list.add(DeviceAttrDisplayVO.builder().attrCode("CST").attrName("通讯状态").build());
        list.add(DeviceAttrDisplayVO.builder().attrCode("Watercons").attrName("水总用量").unit("m³").build());
        list.add(DeviceAttrDisplayVO.builder().attrCode("CST").attrName("通讯状态").build());
        list.add(DeviceAttrDisplayVO.builder().attrCode("Gascons").attrName("天然气总用量").unit("m³").build());
        list.add(DeviceAttrDisplayVO.builder().attrCode("CST").attrName("通讯状态").build());
        list.add(DeviceAttrDisplayVO.builder().attrCode("Ua").attrName("a相电压").unit("V").build());
        list.add(DeviceAttrDisplayVO.builder().attrCode("Ub").attrName("b相电压").unit("V").build());
        list.add(DeviceAttrDisplayVO.builder().attrCode("Uc").attrName("c相电压").unit("V").build());
        list.add(DeviceAttrDisplayVO.builder().attrCode("Uab").attrName("ab线电压").unit("V").build());
        list.add(DeviceAttrDisplayVO.builder().attrCode("Ubc").attrName("bc线电压").unit("V").build());
        list.add(DeviceAttrDisplayVO.builder().attrCode("Uca").attrName("ca线电压").unit("V").build());
        list.add(DeviceAttrDisplayVO.builder().attrCode("Ia").attrName("a相电流").unit("A").build());
        list.add(DeviceAttrDisplayVO.builder().attrCode("Ib").attrName("b相电流").unit("A").build());
        list.add(DeviceAttrDisplayVO.builder().attrCode("Ic").attrName("c相电流").unit("A").build());
        list.add(DeviceAttrDisplayVO.builder().attrCode("F").attrName("频率").unit("Hz").build());
        list.add(DeviceAttrDisplayVO.builder().attrCode("P").attrName("有功功率").unit("KW").build());
        list.add(DeviceAttrDisplayVO.builder().attrCode("Q").attrName("无功功率").unit("kvar").build());
        list.add(DeviceAttrDisplayVO.builder().attrCode("S").attrName("视在功率").unit("kva").build());
        list.add(DeviceAttrDisplayVO.builder().attrCode("PF").attrName("功率因素").build());
        list.add(DeviceAttrDisplayVO.builder().attrCode("Epimp").attrName("正向有功总电能").unit("kWh").build());
        list.add(DeviceAttrDisplayVO.builder().attrCode("Epexp").attrName("反向有功总电能").unit("kWh").build());
        list.add(DeviceAttrDisplayVO.builder().attrCode("Eqimp").attrName("正向无功总电能").unit("kvarh").build());
        list.add(DeviceAttrDisplayVO.builder().attrCode("Eqexp").attrName("反向无功总电能").unit("kvarh").build());
        list.add(DeviceAttrDisplayVO.builder().attrCode("CST").attrName("通讯状态").build());
        list.add(DeviceAttrDisplayVO.builder().attrCode("RST").attrName("运行状态").build());
        list.add(DeviceAttrDisplayVO.builder().attrCode("Humidity").attrName("湿度").unit("%").build());
        list.add(DeviceAttrDisplayVO.builder().attrCode("Temperature").attrName("室温").unit("℃").build());
        map = list.stream().collect(Collectors.toMap(DeviceAttrDisplayVO::getAttrCode, i -> i, (v1, v2) -> v1));
    }

    /**
     * @param queryDTO
     */
    @Override
    public List<DeviceHistoryDTO> queryHistory(HistoryQueryDTO queryDTO) {
        LocalDateTime startTime = getStartTime(queryDTO.getTimes()[0], queryDTO.getPeriodType());
        LocalDateTime endTime = getEndTime(startTime, queryDTO.getPeriodType(), queryDTO.getTimes()[1]);
//        queryDTO.setStartTime(dateTimeFormatter.format(startTime));
//        queryDTO.setEndTime(dateTimeFormatter.format(endTime));
        HistoryQueryInnerDTO innerQueryDTO = BeanUtil.copyProperties(queryDTO, HistoryQueryInnerDTO.class);
        innerQueryDTO.setStartTime(dateTimeFormatter.format(startTime));
        innerQueryDTO.setEndTime(dateTimeFormatter.format(endTime));
        // 从设备列表中，查询bizProductId
        String bizDeviceId = queryDTO.getBizDeviceIds();
        DeviceMonitorEntity deviceMonitorEntity = deviceMonitorServiceImpl.selectByBizDeviceId(bizDeviceId.split(StrUtil.COMMA)[0]);
        if (null == deviceMonitorEntity) {
            // 设备编号有问题
            throw new BusinessException(GlobalErrorCodeConstants.ERROR_BIZ_DEVICE_ID_QUERY.getCode(), GlobalErrorCodeConstants.ERROR_BIZ_DEVICE_ID_QUERY.getCode());
        }
        innerQueryDTO.setBizProductId(deviceMonitorEntity.getBizProductId());
        Response<List<DeviceHistoryDTO>> resp = deviceHistoryApi.getDeviceHistory(innerQueryDTO);
        if (!resp.isSuccess()) {
            throw new BusinessException(resp.getErrorCode(), resp.getErrorMsg());
        }
        List<DeviceHistoryDTO> result = resp.getResult();
        String[] deviceIds = queryDTO.getBizDeviceIds().split(StrUtil.COMMA);
        // 查询设备信息
        List<DeviceMonitorEntity> devices = deviceMonitorServiceImpl.selectByBizDeviceIds(Arrays.asList(deviceIds));
        Map<String, String> deviceNameMap = devices.stream().collect(Collectors.toMap(DeviceMonitorEntity::getBizDeviceId, DeviceMonitorEntity::getName));

        if (!CollectionUtils.isEmpty(result)) {
            result.forEach(i -> {
                i.setDeviceName(deviceNameMap.get(i.getBizDeviceId()));
                i.setAttrName(queryNameByCode(i.getAttrCode()));
            });
        }
        return result;
    }

    @Override
    public String queryNameByCode(String attrCode) {
        return map.containsKey(attrCode) ? map.get(attrCode).getAttrName(): StrUtil.EMPTY;
    }

    /**
     * 根据用户选择的结束时间，开始时间&周期类型，选择对应的结束时间
     *
     * @param startTime
     * @param periodType
     * @param endTime
     * @return
     */
    private LocalDateTime getEndTime(LocalDateTime startTime, Integer periodType, String endTime) {
        try {
            LocalDateTime time = LocalDateTime.parse(endTime, dateTimeFormatter);
            LocalDateTime calcEndTime = null;
            if (periodType == PeriodTypeConst.DEFAULT_PERIOD.getType()) {
                // 原始的查询时间范围是一周
                calcEndTime = startTime.plusDays(7);
            } else if (periodType == PeriodTypeConst.FIVE_MINUTES.getType()) {
                // 5分钟、10分钟、30分钟查询的时间范围是1个月
                calcEndTime = startTime.plusMonths(1);
            } else if (periodType == PeriodTypeConst.TEN_MINUTES.getType()) {
                // 5分钟、10分钟、30分钟查询的时间范围是1个月
                calcEndTime = startTime.plusMonths(1);
            } else if (periodType == PeriodTypeConst.THIRTY_MINUTES.getType()) {
                // 5分钟、10分钟、30分钟查询的时间范围是1个月
                calcEndTime = startTime.plusMonths(1);
            } else if (periodType == PeriodTypeConst.ONE_HOUR.getType()) {
                // 1小时、8小时，1天，查询时间范围是1年
                calcEndTime = startTime.plusYears(1);
            } else if (periodType == PeriodTypeConst.EIGHT_HOURS.getType()) {
                // 1小时、8小时，1天，查询时间范围是1年
                calcEndTime = startTime.plusYears(1);
            } else if (periodType == PeriodTypeConst.ONE_DAY.getType()) {
                // 1小时、8小时，1天，查询时间范围是1年
                calcEndTime = startTime.plusYears(1);
            }
            return calcEndTime.isBefore(time) ? calcEndTime : time;
        } catch (DateTimeParseException e) {
            log.error("检查日期格式字符串错误，字符串为：{}", endTime);
            throw new BusinessException(ErrorCodeEnumConst.DATE_FORMAT_ERROR);
        }
    }


    /**
     * 根据用户选择的开始时间&周期类型，选择对应的开始时间
     *
     * @param startTime
     * @param periodType
     * @return
     */
    private LocalDateTime getStartTime(String startTime, Integer periodType) {
        try {
            LocalDateTime time = LocalDateTime.parse(startTime, dateTimeFormatter);
            time.withSecond(0);
            int minutes = time.getMinute();
            int hour = time.getHour();
            if (periodType == PeriodTypeConst.FIVE_MINUTES.getType()) {
                minutes = minutes / PeriodTypeConst.FIVE_MINUTES.getPeriod() * PeriodTypeConst.FIVE_MINUTES.getPeriod();
                time.withMinute(minutes).plusMinutes(PeriodTypeConst.FIVE_MINUTES.getPeriod());
            } else if (periodType == PeriodTypeConst.TEN_MINUTES.getType()) {
                minutes = minutes / PeriodTypeConst.TEN_MINUTES.getPeriod() * PeriodTypeConst.TEN_MINUTES.getPeriod();
                time.withMinute(minutes).plusMinutes(PeriodTypeConst.TEN_MINUTES.getPeriod());
            } else if (periodType == PeriodTypeConst.THIRTY_MINUTES.getType()) {
                minutes = minutes / PeriodTypeConst.THIRTY_MINUTES.getPeriod() * PeriodTypeConst.THIRTY_MINUTES.getPeriod();
                time.withMinute(minutes).plusMinutes(PeriodTypeConst.THIRTY_MINUTES.getPeriod());
            } else if (periodType == PeriodTypeConst.ONE_HOUR.getType()) {
                time.withMinute(0);
                hour = hour / PeriodTypeConst.ONE_HOUR.getPeriod() * PeriodTypeConst.ONE_HOUR.getPeriod();
                time.withHour(hour).plusHours(PeriodTypeConst.ONE_HOUR.getPeriod());
            } else if (periodType == PeriodTypeConst.EIGHT_HOURS.getType()) {
                time.withMinute(0);
                hour = hour / PeriodTypeConst.EIGHT_HOURS.getPeriod() * PeriodTypeConst.EIGHT_HOURS.getPeriod();
                time.withHour(hour).plusHours(PeriodTypeConst.ONE_HOUR.getPeriod());
            } else if (periodType == PeriodTypeConst.ONE_DAY.getType()) {
                time.withHour(0);
                time.plusDays(PeriodTypeConst.ONE_DAY.getPeriod());
            }
            return time;
        } catch (DateTimeParseException e) {
            log.error("检查日期格式字符串错误，字符串为：{}", startTime);
            throw new BusinessException(ErrorCodeEnumConst.DATE_FORMAT_ERROR);
        }
    }
}