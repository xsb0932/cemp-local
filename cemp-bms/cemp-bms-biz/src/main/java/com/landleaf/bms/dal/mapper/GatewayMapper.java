package com.landleaf.bms.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.entity.GatewayEntity;
import com.landleaf.bms.domain.request.GatewayListRequest;
import com.landleaf.bms.domain.response.GatewayDetailsResponse;
import com.landleaf.bms.domain.response.GatewayListResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * GatewayMapper
 *
 * @author 张力方
 * @since 2023/8/15
 **/
@Mapper
public interface GatewayMapper extends BaseMapper<GatewayEntity> {
    Page<GatewayListResponse> pageQuery(@Param("page") Page<GatewayListResponse> page,
                                        @Param("request") GatewayListRequest request,
                                        @Param("projectBizIds") List<String> projectBizIds);

    GatewayDetailsResponse getDetailByBizId(@Param("bizId") String bizId);

    GatewayEntity selectByBizId(@Param("bizId") String bizId);

    GatewayEntity getById(@Param("id") Long id);
}
