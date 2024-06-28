package com.landleaf.energy.dal.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.energy.domain.entity.ProjectStaDeviceWaterDayEntity;
import com.landleaf.energy.domain.request.WaterMeterDetailResponse;
import com.landleaf.energy.domain.response.WaterMeterPageResponse;
import com.landleaf.pgsql.extension.ExtensionMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * 统计表-设备指标-水表-统计天的数据库操作句柄
 *
 * @author hebin
 * @since 2023-06-24
 */
@Mapper
public interface ProjectStaDeviceWaterDayMapper extends ExtensionMapper<ProjectStaDeviceWaterDayEntity> {
    /**
     * 根据id的列表，修改对应信息的is_deleted字段
     *
     * @param ids       id的列表
     * @param isDeleted 修改后的值
     */
    void updateIsDeleted(@Param("ids") List<Long> ids, @Param("isDeleted") Integer isDeleted);

    Page<WaterMeterPageResponse> selectMeterPage(@Param("page") Page<WaterMeterPageResponse> page,
                                                 @Param("bizDeviceIds") List<String> bizDeviceIds,
                                                 @Param("start") Timestamp start,
                                                 @Param("end") Timestamp end);

    WaterMeterDetailResponse detail(@Param("id") Long id);

    ProjectStaDeviceWaterDayEntity getManualInsertData(@Param("bizDeviceId") String bizDeviceId);
}
