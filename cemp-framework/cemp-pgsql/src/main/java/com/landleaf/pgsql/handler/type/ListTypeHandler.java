package com.landleaf.pgsql.handler.type;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;

import java.util.List;

/**
 * 集合Json类型处理器
 *
 * @author yue lin
 * @since 2023/6/27 9:11
 */
public abstract class ListTypeHandler<T> extends AbstractJsonTypeHandler<List<T>> {

    @Override
    protected List<T> parse(String json) {
        return JSON.parseArray(json, specificType());
    }

    @Override
    protected String toJson(List<T> obj) {
        return JSON.toJSONString(obj);
    }

    protected abstract Class<T> specificType();

}
