package com.landleaf.bms.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.bms.domain.entity.DictDataEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * DictDataMapper
 *
 * @author 张力方
 * @since 2023/6/15
 **/
@Mapper
public interface DictDataMapper extends BaseMapper<DictDataEntity> {

    /**
     * 查询类型下的字典数据
     *
     * @param dictCode 字典类型编码
     * @return 结果集
     */
    default List<DictDataEntity> searchByDictCode(String dictCode) {
        return selectList(Wrappers.<DictDataEntity>lambdaQuery().eq(DictDataEntity::getDictCode, dictCode).orderByAsc(DictDataEntity::getSort));
    }

    default List<DictDataEntity> searchByDictId(Long dictId) {
        return selectList(Wrappers.<DictDataEntity>lambdaQuery().eq(DictDataEntity::getDictId, dictId));
    }

    int maxSort(Long dictId);
}
