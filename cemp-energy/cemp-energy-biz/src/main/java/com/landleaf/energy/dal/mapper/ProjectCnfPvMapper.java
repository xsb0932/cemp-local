package com.landleaf.energy.dal.mapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.energy.domain.entity.ProjectCnfPvEntity;
import com.landleaf.pgsql.extension.ExtensionMapper;

/**
 * 光伏配置
 *
 * @author yue lin
 * @since 2023/7/26 13:19
 */
public interface ProjectCnfPvMapper extends ExtensionMapper<ProjectCnfPvEntity> {

    /**
     * 查询项目的光伏配置
     *
     * @param bizProjectId  项目业务ID
     * @return 结果
     */
    default ProjectCnfPvEntity selectOneByProject(String bizProjectId) {
        return selectOne(Wrappers.<ProjectCnfPvEntity>lambdaQuery().eq(ProjectCnfPvEntity::getProjectId, bizProjectId));
    }

}