package com.landleaf.operatelog.core.dal;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户
 *
 * @author xushibai
 */
@Mapper
public interface OprUserEntityMapper extends BaseMapper<OprUserEntity> {


    @Select("select * from tb_user t1 where t1.email = #{account} or t1.mobile = #{account}")
    OprUserEntity queryByAccount(String account);
}
