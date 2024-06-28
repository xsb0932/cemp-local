package com.landleaf.energy.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.energy.domain.entity.ProjectCnfStorageEntity;

/**
 * 储能配置
 *
 * @author Tycoon
 * @since 2023/8/14 13:20
 **/
public interface ProjectCnfStorageMapper extends BaseMapper<ProjectCnfStorageEntity> {

    /**
     * 查询项目的充电桩配置
     *
     * @param bizProjectId  项目业务ID
     * @return 结果
     */
    default ProjectCnfStorageEntity selectOneByProject(String bizProjectId) {
        return selectOne(Wrappers.<ProjectCnfStorageEntity>lambdaQuery().eq(ProjectCnfStorageEntity::getProjectId, bizProjectId));
    }

}