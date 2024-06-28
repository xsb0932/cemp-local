package com.landleaf.pgsql.base.wrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;

import java.util.List;
import java.util.Map;

/**
 * 对象的wrapper，将entity转化为vo
 *
 * @param <V> 指定的vo
 * @param <E> 指定的entity
 * @author hebin
 */
public abstract class BaseWrapper<V, E> {
    /**
     * 将某个实体对象转为vo
     *
     * @param e 实体对象
     * @return 转化后的vo
     */
    public abstract V entity2VO(E e);

    /**
     * 将某个实体对象的集合转为vo的集合
     *
     * @param eList 实体对象的集合
     * @return 转化后的vo的集合
     */
    public abstract List<V> listEntity2VO(List<E> eList);

    /**
     * 将某个page转为对应的vo的page对象展示
     *
     * @param page 查询的po的page内容
     * @return 转化后的vo的page内容
     */
    public abstract PageDTO<V> pageEntity2VO(IPage<E> page);

    /**
     * 将某个page转为对应的vo的page对象展示
     *
     * @param page 查询的po的page内容
     * @return 转化后的vo的page内容
     */
    public abstract PageDTO<V> pageEntity2VO(IPage<E> page, Map<?, ?> map);
}
