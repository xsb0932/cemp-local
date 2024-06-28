package com.landleaf.oauth.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.oauth.domain.entity.TenantEntity;
import com.landleaf.oauth.domain.request.TenantTabulationRequest;
import com.landleaf.oauth.domain.response.TenantInfoResponse;
import com.landleaf.oauth.domain.response.TenantTabulationResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 租户
 *
 * @author yue lin
 * @since 2023/6/1 9:18
 */
@Mapper
public interface TenantEntityMapper extends BaseMapper<TenantEntity> {

    /**
     * 查询租户列表
     * @param page 分页
     * @param tenantTabulationRequest  查询参数
     * @return  结果集
     */
    IPage<TenantTabulationResponse> searchTenantTabulation(@Param("page") Page<TenantTabulationResponse> page,
                                                           @Param("request") TenantTabulationRequest tenantTabulationRequest);
    /**
     * 根据ID查询租户基础信息
     * @param tenantId  id
     * @return  结果
     */
    TenantInfoResponse searchTenantBasicInfo(Long tenantId);
}
