package com.landleaf.bms.dal.mapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.bms.domain.entity.ProjectSpaceEntity;
import com.landleaf.pgsql.extension.ExtensionMapper;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Param;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目-空间管理Mapper
 *
 * @author yue lin
 * @since 2023/7/12 16:28
 */
public interface ProjectSpaceMapper extends ExtensionMapper<ProjectSpaceEntity> {

    /**
     * 向上递归查询空间
     *
     * @param spaceId 空间ID
     * @return 结果
     */
    List<ProjectSpaceEntity> recursiveUp(Long spaceId);

    /**
     * 查询项目下的区域列表
     *
     * @param projectId 项目ID
     * @return 结果
     */
    default List<ProjectSpaceEntity> selectSpacesByProject(@NotNull Long projectId) {
        return selectList(Wrappers.<ProjectSpaceEntity>lambdaQuery()
                .eq(ProjectSpaceEntity::getProjectId, projectId)
        );
    }

    /**
     * 判断区域下是否还有子集
     *
     * @param spaceId 区域ID
     * @return 结果
     */
    default boolean existsChildren(@NotNull Long spaceId) {
        return exists(Wrappers.<ProjectSpaceEntity>lambdaQuery()
                .eq(ProjectSpaceEntity::getParentId, spaceId)
        );
    }

    /**
     * 获取指定空间的名称路径
     *
     * @param spaceId 空间ID
     * @return 结果
     */
    default String spacePath(@NotNull Long spaceId) {
        return recursiveUp(spaceId).stream()
                .sorted(Comparator.comparing(ProjectSpaceEntity::getParentId))
                .map(ProjectSpaceEntity::getName)
                .collect(Collectors.joining("/"));
    }

    List<ProjectSpaceEntity> recursiveDownById(@Param("id") Long id);
}
