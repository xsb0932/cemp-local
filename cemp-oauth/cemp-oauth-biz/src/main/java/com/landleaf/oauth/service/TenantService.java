package com.landleaf.oauth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.landleaf.oauth.domain.request.*;
import com.landleaf.oauth.domain.response.TenantInfoResponse;
import com.landleaf.oauth.domain.response.TenantSelectiveResponse;
import com.landleaf.oauth.domain.response.TenantTabulationResponse;
import com.landleaf.oauth.domain.response.TenantValidationResponse;

import java.util.List;

/**
 * 租户业务接口
 *
 * @author yue lin
 * @since 2023/6/1 9:36
 */
public interface TenantService {

    /**
     * 租户分页查询列表
     * @param tenantTabulationRequest 查询参数
     * @return 结果集
     */
    IPage<TenantTabulationResponse> searchTenantTabulation(TenantTabulationRequest tenantTabulationRequest);

    /**
     * 租户删除
     * @param tenantId 租户主键
     */
    void deleteTenant(Long tenantId);

    /**
     * 平台管理员创建租户
     * @param tenantCreateRequest 参数
     */
    void createTenant(TenantCreateRequest tenantCreateRequest);

    /**
     * 变更租户信息
     * @param tenantBasicUpdateRequest 参数
     */
    void updateTenantBasic(TenantBasicUpdateRequest tenantBasicUpdateRequest);

    /**
     * 平台管理变更租户状态
     * @param tenantId  租户ID
     * @param disableStatus 目标状态
     */
    void updateTenantStatus(Long tenantId, Short disableStatus);

    /**
     * 根据ID查询租户基础信息
     * @param tenantId  id
     * @return  结果
     */
    TenantInfoResponse searchTenantBasicInfo(Long tenantId);

    /**
     * 租户选择框列表查询（平台管理员返回所有，租户管理员只返回本租户）
     * @return  结果
     */
    List<TenantSelectiveResponse> searchTenantSelective();

    /**
     * 根据租户ID查询企业的基本信息
     * @param tenantId 租户ID
     * @return  结果
     */
    TenantInfoResponse searchEnterpriseBasic(Long tenantId);

    /**
     * 变更企业基本信息
     * @param request   变更内容
     */
    void updateEnterprise(EnterpriseUpdateRequest request);

    /**
     * 变更企业管理员
     * @param request   参数
     */
    void changeEnterpriseAdmin(EnterpriseAdminChangeRequest request);

    /**
     * 校验参数是否已经存在
     * @param request   参数
     * @return  结果
     */
    TenantValidationResponse validationParam(TenantValidationRequest request);
}
