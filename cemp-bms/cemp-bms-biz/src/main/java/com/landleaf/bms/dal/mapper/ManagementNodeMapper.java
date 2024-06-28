package com.landleaf.bms.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.landleaf.bms.api.dto.UserManageNodeResponse;
import com.landleaf.bms.domain.dto.ManagementNodeTreeDTO;
import com.landleaf.bms.domain.entity.ManagementNodeEntity;
import com.landleaf.bms.domain.response.ManagementNodeListResponse;
import com.landleaf.bms.domain.response.NodeProjectTreeResponse;
import com.landleaf.bms.domain.response.TenantManagementNodeListResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ManagementNodeMapper
 *
 * @author 张力方
 * @since 2023/6/5
 **/
@Mapper
public interface ManagementNodeMapper extends BaseMapper<ManagementNodeEntity> {
    /**
     * 递归向上获取管理节点列表
     *
     * @param ids 需要获取父级节点的id集合
     * @return 管理节点列表
     */
    List<NodeProjectTreeResponse> recursiveUpListByIds(@Param("ids") List<Long> ids);

    /**
     * 递归向下获取管理节点列表
     *
     * @param ids 需要获取下级节点的id集合
     * @return 管理节点列表
     */
    List<ManagementNodeEntity> recursiveDownListByIds(@Param("ids") List<Long> ids);

    /**
     * 递归向上获取管理节点列表 - Feign
     *
     * @param ids 需要获取父级节点的id集合
     * @return 管理节点列表
     */
    List<UserManageNodeResponse> recursiveUpListByIdsFeign(@Param("ids") List<Long> ids);

    /**
     * 获取管理节点列表
     *
     * @return 管理节点列表
     */
    List<ManagementNodeListResponse> getManagementNodeList(@Param("tenantId") Long tenantId);

    /**
     * 获取管理节点列表
     *
     * @return 管理节点列表
     */
    List<TenantManagementNodeListResponse> getTenantManagementNodeList(@Param("tenantId") Long tenantId);

    String getLongName(@Param("bizNodeId") String bizNodeId);

    int selectMaxSort(Long tenantId);

    List<ManagementNodeTreeDTO> recursiveUpManagementNodeTreeDTOByIds(@Param("ids") List<Long> ids);

    List<String> recursiveDownBizProjectIdsByBizNodeId(@Param("bizNodeId") String bizNodeId);
}
