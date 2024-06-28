package com.landleaf.jjgj.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.landleaf.jjgj.domain.entity.DeviceParameterDetailEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 设备参数明细表的数据库操作句柄
 *
 * @author hebin
 * @since 2023-07-24
 */
public interface DeviceParameterDetailMapper extends BaseMapper<DeviceParameterDetailEntity> {

    @Select("select * from tb_device_parameter where deleted = 0 and biz_device_id = #{bizDeviceId} and identifier = #{code} limit 1")
    DeviceParameterDetailEntity getParameter(@Param("bizDeviceId") String bizDeviceId, @Param("code") String code);
}
