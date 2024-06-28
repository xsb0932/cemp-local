package com.landleaf.bms.api;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.bms.api.dto.UserManageNodeIdsResponse;
import com.landleaf.bms.api.dto.UserManageNodeResponse;
import com.landleaf.bms.api.dto.UserNodeUpdateRequest;
import com.landleaf.bms.api.dto.UserProjRelationResponse;
import com.landleaf.bms.dal.mapper.ManagementNodeMapper;
import com.landleaf.bms.dal.mapper.UserNodeMapper;
import com.landleaf.bms.domain.entity.ManagementNodeEntity;
import com.landleaf.bms.domain.entity.UserNodeEntity;
import com.landleaf.bms.domain.enums.BmsConstants;
import com.landleaf.bms.domain.enums.ErrorCodeConstants;
import com.landleaf.bms.domain.enums.ManagementNodeTypeEnum;
import com.landleaf.bms.domain.enums.UserNodeTypeEnum;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 用户管理节点权限
 *
 * @author 张力方
 * @since 2023/6/6
 **/
@RestController
@RequiredArgsConstructor
public class UserManagementNodeApiImpl implements UserManagementNodeApi {
    private final ManagementNodeMapper managementNodeMapper;
    private final UserNodeMapper userNodeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Void> updateUserNode(UserNodeUpdateRequest request) {
        // 忽略租户
        TenantContext.setIgnore(true);

        // 如果 节点ids 为空则删除所有权限
        List<Long> nodeIds = request.getNodeIds();
        if (CollectionUtils.isEmpty(nodeIds)) {
            return Response.success();
        }

        // 权限类型
        Short type = request.getType();
        List<Long> resultNodeIds = new ArrayList<>();
        // 区域权限类型处理，只保留最外层权限
        if (type.equals(UserNodeTypeEnum.AREA.getType())) {
            Set<Long> allCNodeIds = new HashSet<>();
            for (Long nodeId : nodeIds) {
                List<Long> cNodeIds = new ArrayList<>(managementNodeMapper.recursiveDownListByIds(Collections.singletonList(nodeId)).stream().map(ManagementNodeEntity::getId).toList());
                cNodeIds.remove(nodeId);
                allCNodeIds.addAll(cNodeIds);
            }
            for (Long nodeId : nodeIds) {
                if (!allCNodeIds.contains(nodeId)) {
                    resultNodeIds.add(nodeId);
                }
            }
        }
        // 站点权限类型处理，只认最后一级项目权限
        if (type.equals(UserNodeTypeEnum.PROJECT.getType())) {
            List<ManagementNodeEntity> managementNodeEntities = managementNodeMapper.selectList(Wrappers.<ManagementNodeEntity>lambdaQuery()
                    .eq(ManagementNodeEntity::getType, ManagementNodeTypeEnum.PROJECT.getType())
                    .in(ManagementNodeEntity::getId, nodeIds));
            if (!CollectionUtils.isEmpty(managementNodeEntities)){
                resultNodeIds = managementNodeEntities.stream().map(ManagementNodeEntity::getId).toList();
            }
        }

        // 删除旧的权限
        Long userId = request.getUserId();
        userNodeMapper.delete(Wrappers.<UserNodeEntity>lambdaQuery().eq(UserNodeEntity::getUserId, userId));

        // 构建新的权限列表
        List<UserNodeEntity> userNodeEntities = new ArrayList<>();
        for (Long nodeId : resultNodeIds) {
            ManagementNodeEntity managementNodeEntity = managementNodeMapper.selectById(nodeId);
            UserNodeEntity userNodeEntity = new UserNodeEntity();
            userNodeEntity.setUserId(userId);
            userNodeEntity.setNodeId(nodeId);
            userNodeEntity.setTenantId(request.getTenantId());
            userNodeEntity.setType(type);
            userNodeEntity.setPath(managementNodeEntity.getPath());
            userNodeEntities.add(userNodeEntity);
        }
        userNodeMapper.insertBatchSomeColumn(userNodeEntities);
        return Response.success();
    }

    @Override
    public Response<UserManageNodeResponse> getUserManageNodes(Long userId) {
        TenantContext.setIgnore(true);
        // 当前用户的节点权限
        List<UserNodeEntity> userNodeEntityList = userNodeMapper.selectList(Wrappers.<UserNodeEntity>lambdaQuery()
                .eq(UserNodeEntity::getUserId, userId));
        if (CollectionUtils.isEmpty(userNodeEntityList)) {
            return null;
        }
        // 具有权限的节点id列表
        List<Long> nodeIds = userNodeEntityList.stream().map(UserNodeEntity::getNodeId).toList();
        Short type = userNodeEntityList.get(0).getType();
        // 满足权限的节点列表
        List<UserManageNodeResponse> userManageNodeResponseList = new ArrayList<>();
        if (type.equals(UserNodeTypeEnum.PROJECT.getType())) {
            userManageNodeResponseList = managementNodeMapper.recursiveUpListByIdsFeign(nodeIds);
        }
        if (type.equals(UserNodeTypeEnum.AREA.getType())) {
            List<ManagementNodeEntity> downManagementNodeEntities = managementNodeMapper.recursiveDownListByIds(nodeIds);
            userManageNodeResponseList = managementNodeMapper.recursiveUpListByIdsFeign(downManagementNodeEntities.stream().map(ManagementNodeEntity::getId).toList());
        }
        // 转换为树结构
        UserManageNodeResponse userManageNodeResponse = covertList2Tree(userManageNodeResponseList);
        return Response.success(userManageNodeResponse);
    }

    @Override
    public Response<UserManageNodeIdsResponse> getUserManageNodeIds(Long userId) {
        TenantContext.setIgnore(true);
        List<UserNodeEntity> userNodeEntities = userNodeMapper.selectList(Wrappers.<UserNodeEntity>lambdaQuery()
                .eq(UserNodeEntity::getUserId, userId));
        if (CollectionUtils.isEmpty(userNodeEntities)) {
            return Response.success();
        }
        List<Long> nodeIds = userNodeEntities.stream().map(UserNodeEntity::getNodeId).toList();
        UserManageNodeIdsResponse userManageNodeIdsResponse = new UserManageNodeIdsResponse();
        userManageNodeIdsResponse.setType(userNodeEntities.get(0).getType());
        userManageNodeIdsResponse.setNodeIds(nodeIds);
        return Response.success(userManageNodeIdsResponse);
    }

    @Override
    public Response<List<UserProjRelationResponse>> getUserProjRelation(Long tenantId) {
        TenantContext.setIgnore(true);
        List<UserProjRelationResponse> result = userNodeMapper.getUserProjRelation(tenantId);
        return Response.success(result);
    }

    private UserManageNodeResponse covertList2Tree(List<UserManageNodeResponse> userManageNodeResponses) {
        if (CollectionUtils.isEmpty(userManageNodeResponses)) {
            return null;
        }
        UserManageNodeResponse userManageNodeResponse = userManageNodeResponses.stream()
                .filter(item -> item.getParentBizNodeId().equals(BmsConstants.ROOT_MANAGEMENT_BIZ_NODE_ID))
                .findFirst().orElseThrow(() -> new ServiceException(ErrorCodeConstants.ROOT_NODE_NOT_EXIST));
        recursiveBuildUserManageNodeResponse(userManageNodeResponse, userManageNodeResponses);
        return userManageNodeResponse;
    }

    private void recursiveBuildUserManageNodeResponse(UserManageNodeResponse response, List<UserManageNodeResponse> userManageNodeResponses) {
        List<UserManageNodeResponse> childrenManagementNodeEntities = userManageNodeResponses.stream().filter(item -> item.getParentBizNodeId().equals(response.getBizNodeId())).toList();
        if (CollectionUtils.isEmpty(childrenManagementNodeEntities)) {
            return;
        }
        response.setChildren(childrenManagementNodeEntities);
        for (UserManageNodeResponse child : childrenManagementNodeEntities) {
            recursiveBuildUserManageNodeResponse(child, userManageNodeResponses);
        }
    }
}
