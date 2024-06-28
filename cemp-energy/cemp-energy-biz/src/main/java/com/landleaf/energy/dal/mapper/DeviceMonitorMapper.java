package com.landleaf.energy.dal.mapper;

import java.util.List;

import com.landleaf.energy.domain.entity.ProjectStaSubitemMonthEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.landleaf.energy.domain.entity.DeviceMonitorEntity;
import org.apache.ibatis.annotations.Select;

/**
 * DeviceMonitorEntity对象的数据库操作句柄
 *
 * @author hebin
 * @since 2023-06-22
 */
@Mapper
public interface DeviceMonitorMapper extends BaseMapper<DeviceMonitorEntity> {
    /**
     * 根据id的列表，修改对应信息的is_deleted字段
     *
     * @param ids       id的列表
     * @param isDeleted 修改后的值
     */
    void updateIsDeleted(@Param("ids") List<Long> ids, @Param("isDeleted") Integer isDeleted);

    @Select("SELECT * FROM tb_device_monitor t1 where t1.deleted = 0 and t1.biz_project_id = #{bizProjectId} and t1.biz_device_id = #{bizDeviceId}")
    DeviceMonitorEntity selectOne(@Param("bizProjectId") String bizProjectId,
                                  @Param("bizDeviceId") String bizDeviceId);

    List<DeviceMonitorEntity> selectDevicesByCategory(@Param("bizProjectId") String bizProjectId,
                                                      @Param("categoryId") String categoryId);
}
