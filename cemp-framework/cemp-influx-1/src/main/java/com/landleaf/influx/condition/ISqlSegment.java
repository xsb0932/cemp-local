package com.landleaf.influx.condition;

import java.io.Serializable;

/**
 * @author lokiy
 * @date 2021/12/13
 * @description sql部分接口
 */
@FunctionalInterface
public interface ISqlSegment extends Serializable {
    /**
     * 获取sql部分拼接字符串
     * @return sql部分拼接字符串
     */
    String getSqlSegment();
}

