package com.landleaf.bms.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.landleaf.bms.domain.entity.DeviceParameterDetailEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 设备参数明细表的数据库操作句柄
 *
 * @author hebin
 * @since 2023-07-24
 */
public interface DeviceParameterDetailMapper extends BaseMapper<DeviceParameterDetailEntity> {
    /**
     * 根据id的列表，修改对应信息的is_deleted字段
     *
     * @param ids       id的列表
     * @param isDeleted 修改后的值
     */
    void updateIsDeleted(@Param("ids") List<Long> ids, @Param("isDeleted") Integer isDeleted);

    @Select("select * from tb_device_parameter where deleted = 0 and biz_device_id = #{bizDeviceId} and identifier = #{code} limit 1")
    DeviceParameterDetailEntity getParameter(@Param("bizDeviceId") String bizDeviceId, @Param("code") String code);
}
