package com.landleaf.bms.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.bms.domain.entity.CategoryCatalogueEntity;
import com.landleaf.bms.domain.response.CategoryTreeListResponse;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 品类管理-目录
 *
 * @author yue lin
 * @since 2023/7/6 9:46
 */
public interface CategoryCatalogueMapper extends BaseMapper<CategoryCatalogueEntity> {
    /**
     * 递归向上获取目录列表
     *
     * @param ids 需要获取父级节点的id集合
     * @return 目录列表
     */
    List<CategoryTreeListResponse> recursiveUpListByIds(@Param("ids") List<Long> ids);

    /**
     * 递归向下获取所有下级目录ids
     *
     * @param id 目录id
     * @return 下级目录ids
     */
    List<Long> recursiveDownIdsById(@Param("id") Long id);

    String getLongName(@Param("id") Long id);

    /**
     * 判断是否有子集目录
     *
     * @param catalogueId 目录ID
     * @return 结果
     */
    default boolean existChildren(Long catalogueId) {
        return exists(Wrappers.<CategoryCatalogueEntity>lambdaQuery().eq(CategoryCatalogueEntity::getParentId, catalogueId));
    }

    List<CategoryCatalogueEntity> recursiveUpCatalogueByIds(@Param("ids") Collection<Long> ids);
}
