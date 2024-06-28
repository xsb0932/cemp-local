package com.landleaf.energy.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.energy.domain.entity.ProjectEntity;
import jakarta.validation.constraints.NotBlank;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目的数据库操作句柄
 *
 * @author hebin
 * @since 2023-06-24
 */
@Mapper
public interface ProjectMapper extends BaseMapper<ProjectEntity> {
    /**
     * 根据id的列表，修改对应信息的is_deleted字段
     *
     * @param ids       id的列表
     * @param isDeleted 修改后的值
     */
    void updateIsDeleted(@Param("ids") List<Long> ids, @Param("isDeleted") Integer isDeleted);


    /**
     * 查询项目业务ID是否存在
     *
     * @param projectBizId 项目业务ID
     * @return 结果
     */
    default boolean existsProjectBizId(@NotBlank String projectBizId) {
        return exists(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, projectBizId));
    }

}
