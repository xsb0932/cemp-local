package com.landleaf.pgsql.extension;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Collection;

/**
 * 扩展通用Mapper
 *
 * @author yue lin
 * @since 2023/6/2 11:11
 */
public interface ExtensionMapper<T> extends BaseMapper<T> {

    /**
     * 批量插入
     *
     * @param entityList 批量
     */
    int insertBatchSomeColumn(Collection<T> entityList);

}
