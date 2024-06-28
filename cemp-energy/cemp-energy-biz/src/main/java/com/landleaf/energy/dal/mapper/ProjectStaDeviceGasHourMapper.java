package com.landleaf.energy.dal.mapper;

import com.landleaf.energy.domain.entity.ProjectStaDeviceGasHourEntity;
import com.landleaf.pgsql.extension.ExtensionMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 统计表-设备指标-气类-统计小时的数据库操作句柄
 *
 * @author hebin
 * @since 2023-06-24
 */
@Mapper
public interface ProjectStaDeviceGasHourMapper extends ExtensionMapper<ProjectStaDeviceGasHourEntity> {
    /**
     * 根据id的列表，修改对应信息的is_deleted字段
     *
     * @param ids       id的列表
     * @param isDeleted 修改后的值
     */
    void updateIsDeleted(@Param("ids") List<Long> ids, @Param("isDeleted") Integer isDeleted);
}
