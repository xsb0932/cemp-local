package com.landleaf.sdl.dal.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.pgsql.extension.ExtensionMapper;
import com.landleaf.sdl.domain.entity.ProjectStaDeviceElectricityDayEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 统计表-设备指标-电表-统计天的数据库操作句柄
 *
 * @author hebin
 * @since 2023-06-24
 */
@Mapper
public interface ProjectStaDeviceElectricityDayMapper extends ExtensionMapper<ProjectStaDeviceElectricityDayEntity> {

}
