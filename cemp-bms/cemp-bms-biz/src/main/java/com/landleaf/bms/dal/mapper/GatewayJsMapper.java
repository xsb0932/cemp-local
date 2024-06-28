package com.landleaf.bms.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.landleaf.bms.domain.entity.GatewayJsEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * GatewayJsMapper
 *
 * @author 张力方
 * @since 2023/8/17
 **/
@Mapper
public interface GatewayJsMapper extends BaseMapper<GatewayJsEntity> {
    int insertIfNotExists(GatewayJsEntity gatewayJsEntity);
}
