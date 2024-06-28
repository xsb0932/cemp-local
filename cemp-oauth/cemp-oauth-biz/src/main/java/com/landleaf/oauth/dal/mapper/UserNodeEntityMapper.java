package com.landleaf.oauth.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.landleaf.oauth.domain.entity.UserNodeEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户节点
 *
 * @author yue lin
 * @since 2023/6/1 9:18
 */
@Mapper
public interface UserNodeEntityMapper extends BaseMapper<UserNodeEntity> {
}
