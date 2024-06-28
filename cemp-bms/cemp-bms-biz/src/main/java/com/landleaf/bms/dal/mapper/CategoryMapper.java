package com.landleaf.bms.dal.mapper;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.bms.domain.entity.CategoryEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 品类管理-品类Mapper
 *
 * @author yue lin
 * @since 2023/7/6 9:45
 */
public interface CategoryMapper extends BaseMapper<CategoryEntity> {
    default CategoryEntity selectByBizId(@NotBlank String bizId) {
        return selectOne(Wrappers.<CategoryEntity>lambdaQuery().eq(CategoryEntity::getBizId, bizId));
    }

    default List<CategoryEntity> selectBatchByBizIds(List<String> bizId) {
        return selectList(Wrappers.<CategoryEntity>lambdaQuery()
                .in(CollUtil.isNotEmpty(bizId), CategoryEntity::getBizId, bizId)
                .orderByAsc(CategoryEntity::getCreateTime)
        );
    }

    /**
     * 判断是否有子集品类
     *
     * @param catalogueId 目录ID
     * @return 结果
     */
    default boolean existChildren(@NotNull Long catalogueId) {
        return exists(Wrappers.<CategoryEntity>lambdaQuery().eq(CategoryEntity::getParentId, catalogueId));
    }

}
