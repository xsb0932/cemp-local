package com.landleaf.monitor.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.monitor.api.dto.DeviceManagerEventHistoryDTO;
import com.landleaf.monitor.domain.dto.AlarmTypeNumDTO;
import com.landleaf.monitor.domain.dto.ProjUnconfirmedAlarmCountDTO;
import com.landleaf.monitor.domain.entity.HistoryEventEntity;
import com.landleaf.monitor.domain.entity.UnconfirmedEventEntity;
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
public interface UnconfirmedEventMapper extends BaseMapper<UnconfirmedEventEntity> {
    /**
     * 获取未确认告警的数量
     *
     * @param tenantId
     * @return
     */
    List<ProjUnconfirmedAlarmCountDTO> getUnconfirmedCount(@Param("tenantId") Long tenantId, @Param("userId") Long userId);

    /**
     * 获取未确认告警的信息
     *
     * @param tenantId
     * @param currentId
     * @param userId
     * @return
     */
    AlarmListResponse getUnconfirmedInfo(@Param("tenantId") Long tenantId, @Param("currentId") Long currentId, @Param("userId") Long userId);


    void delEvent(@Param("eventId") String eventId);

    void deleteByIds(@Param("ids") List<Long> ids);
}
