package com.landleaf.sdl.dal.mapper;


import com.landleaf.pgsql.extension.ExtensionMapper;
import com.landleaf.sdl.domain.entity.ProjectStaDeviceElectricityHourEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 统计表-设备指标-电表-统计小时的数据库操作句柄
 *
 * @author hebin
 * @since 2023-06-24
 */
@Mapper
public interface ProjectStaDeviceElectricityHourMapper extends ExtensionMapper<ProjectStaDeviceElectricityHourEntity> {
    /**
     * 根据id的列表，修改对应信息的is_deleted字段
     *
     * @param ids       id的列表
     * @param isDeleted 修改后的值
     */
    void updateIsDeleted(@Param("ids") List<Long> ids, @Param("isDeleted") Integer isDeleted);

    @Select("SELECT * FROM tb_project_sta_device_electricity_hour where deleted = 0 and biz_project_id = #{bizProjectId} and biz_device_id = #{bizDeviceId} and  sta_time >= #{begin} and sta_time  < #{end} order by sta_time")
    List<ProjectStaDeviceElectricityHourEntity> list(@Param("bizDeviceId") String bizDeviceId,
                                                     @Param("bizProjectId") String bizProjectId,
                                                     @Param("begin") String begin,
                                                     @Param("end") String end);

    List<ProjectStaDeviceElectricityHourEntity> getDailyEle(@Param("devices") List<String> devices, @Param("begin") String begin, @Param("end") String end);
}
