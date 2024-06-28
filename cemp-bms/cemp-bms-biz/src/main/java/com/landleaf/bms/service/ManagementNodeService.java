package com.landleaf.bms.service;

import com.landleaf.bms.domain.request.*;
import com.landleaf.bms.domain.response.ManagementNodeListResponse;
import com.landleaf.bms.domain.response.TenantManagementNodeListResponse;

import java.util.List;

/**
 * ManagementNodeService
 *
 * @author 张力方
 * @since 2023/6/5
 **/
public interface ManagementNodeService {
    /**
     * 创建租户根节点
     *
     * @return 节点业务id
     */
    String createTenantRootNode(ManagementNodeAddRootRequest request);

    /**
     * 新增租户管理节点
     *
     * @param request 新增数据
     * @return 节点业务id
     */
    String addManagementNode(ManagementNodeAddRequest request);

    /**
     * 编辑租户管理节点
     *
     * @param request 编辑数据
     */
    void editManagementNode(ManagementNodeEditRequest request);

    /**
     * 校验编码是否唯一
     * <p>
     * true 唯一， false 不唯一
     *
     * @return true 唯一， false 不唯一
     */
    boolean checkCodeUnique(ManagementNodeCodeUniqueRequest request);

    /**
     * 获取当前租户管理员所有管理节点列表
     *
     * @return 管理节点列表
     */
    List<ManagementNodeListResponse> getManagementNodeList(Long tenantId);

    /**
     * 管理节点排序接口
     *
     * @param request 请求参数
     */
    void modifySort(ManagementNodeSortRequest request);

    /**
     * 删除管理节点
     *
     * @param bizNodeId 管理节点业务id
     */
    void deleteManagementNode(String bizNodeId);

    List<TenantManagementNodeListResponse> getTenantManagementNodeList(Long tenantId, Short permissionType);
}
