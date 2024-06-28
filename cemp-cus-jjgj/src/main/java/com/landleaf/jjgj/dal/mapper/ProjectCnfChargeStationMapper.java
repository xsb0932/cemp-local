package com.landleaf.jjgj.dal.mapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.jjgj.domain.entity.ProjectCnfChargeStationEntity;
import com.landleaf.pgsql.extension.ExtensionMapper;

/**
 *
 * 充电桩配置
 * @author yue lin
 * @since 2023/7/26 13:17
 */
public interface ProjectCnfChargeStationMapper extends ExtensionMapper<ProjectCnfChargeStationEntity> {

    /**
     * 查询项目的充电桩配置
     *
     * @param bizProjectId  项目业务ID
     * @return 结果
     */
    default ProjectCnfChargeStationEntity selectOneByProject(String bizProjectId) {
        return selectOne(Wrappers.<ProjectCnfChargeStationEntity>lambdaQuery().eq(ProjectCnfChargeStationEntity::getProjectId, bizProjectId));
    }

}
