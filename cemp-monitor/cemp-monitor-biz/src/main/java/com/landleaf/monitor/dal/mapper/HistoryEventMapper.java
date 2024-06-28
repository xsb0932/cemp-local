package com.landleaf.monitor.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.monitor.api.dto.DeviceManagerEventHistoryDTO;
import com.landleaf.monitor.domain.dto.AlarmTypeNumDTO;
import com.landleaf.monitor.domain.dto.ProjUnconfirmedAlarmCountDTO;
import com.landleaf.monitor.domain.entity.HistoryEventEntity;
import com.landleaf.monitor.domain.request.HistoryEventListRequest;
import com.landleaf.monitor.domain.response.AlarmListResponse;
import com.landleaf.monitor.dto.AlarmResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * HistoryEventMapper
 *
 * @author 张力方
 * @since 2023/8/14
 **/
@Mapper
public interface HistoryEventMapper extends BaseMapper<HistoryEventEntity> {
    /**
     * 分页列表查询历史事件
     *
     * @param page    分页参数
     * @param request 请求参数
     * @return 历史事件列表
     */
    Page<AlarmListResponse> selectPageList(@Param("page") Page<AlarmListResponse> page,
                                           @Param("request") HistoryEventListRequest request);

    /**
     * 列表查询历史事件
     *
     * @param request 请求参数
     * @return 历史事件列表
     */
    List<AlarmListResponse> selectExcelList(@Param("request") HistoryEventListRequest request);

    List<AlarmListResponse> selectListByDeviceIds(@Param("bizDeviceIds") List<String> bizDeviceIds);

    List<AlarmResponse> getRJDAlarms(String tenantId);

    /**
     * 查询自定义的总数
     *
     * @param request
     * @return
     */
    List<AlarmTypeNumDTO> selectCusCount(@Param("request") HistoryEventListRequest request);

    Page<DeviceManagerEventHistoryDTO> deviceEventsHistory(@Param("page") Page<DeviceManagerEventHistoryDTO> page,
                                                           @Param("bizDeviceId") String bizDeviceId,
                                                           @Param("alarmType") String alarmType,
                                                           @Param("start") String start,
                                                           @Param("end") String end);

    List<DeviceManagerEventHistoryDTO> deviceEventsHistoryExport(@Param("bizDeviceId") String bizDeviceId,
                                                                 @Param("alarmType") String alarmType,
                                                                 @Param("start") String start,
                                                                 @Param("end") String end);
}
