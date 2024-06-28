package com.landleaf.pgsql.core;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BizSequenceMapper {

    /**
     * 获取序列下一个id
     *
     * @param code 序列名
     * @return ID
     */
    @Select("SELECT nextval(#{code});")
    Long next(@Param("code") String code);
}
