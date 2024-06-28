package com.landleaf.sdl.dal.mapper;
import com.landleaf.pgsql.extension.ExtensionMapper;
import com.landleaf.sdl.domain.entity.ProjectStaDeviceElectricityYearEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 统计表-设备指标-电表-统计年的数据库操作句柄
 *
 * @author hebin
 * @since 2023-06-24
 */
@Mapper
public interface ProjectStaDeviceElectricityYearMapper extends ExtensionMapper<ProjectStaDeviceElectricityYearEntity> {
    /**
     * 根据id的列表，修改对应信息的is_deleted字段
     *
     * @param ids       id的列表
     * @param isDeleted 修改后的值
     */
    void updateIsDeleted(@Param("ids") List<Long> ids, @Param("isDeleted") Integer isDeleted);
}
