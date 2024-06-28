package com.landleaf.monitor.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.monitor.domain.dto.DevConfirmedAlarmCountDTO;
import com.landleaf.monitor.domain.entity.CurrentAlarmEntity;
import com.landleaf.monitor.domain.request.CurrentAlarmListRequest;
import com.landleaf.monitor.domain.response.AlarmListResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * CurrentAlarmMapper
 *
 * @author 张力方
 * @since 2023/8/14
 **/
@Mapper
public interface CurrentAlarmMapper extends BaseMapper<CurrentAlarmEntity> {
    /**
     * 分页列表查询当前告警
     *
     * @param page    分页参数
     * @param request 请求参数
     * @return 当前告警列表
     */
    Page<AlarmListResponse> selectPageList(@Param("page") Page<AlarmListResponse> page,
                                           @Param("bizProjIds") List<String> bizProjIds,
                                           @Param("request") CurrentAlarmListRequest request);

    /**
     * 删除current
     *
     * @param alarmId
     */
    void deleteAlarmById(@Param("alarmId") Long alarmId);

    List<DevConfirmedAlarmCountDTO> selectConfirmedCountByCode(@Param("bizDeviceIds") List<String> bizDeviceIds,
                                                               @Param("codePrefix") List<String> codePrefix);
}
