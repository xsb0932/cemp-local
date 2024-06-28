package com.landleaf.bms.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.bms.dal.mapper.ManagementNodeMapper;
import com.landleaf.bms.domain.entity.ManagementNodeEntity;
import com.landleaf.bms.domain.enums.BmsConstants;
import com.landleaf.bms.domain.enums.ErrorCodeConstants;
import com.landleaf.bms.domain.enums.ManagementNodeTypeEnum;
import com.landleaf.bms.domain.enums.UserNodeTypeEnum;
import com.landleaf.bms.domain.request.*;
import com.landleaf.bms.domain.response.ManagementNodeListResponse;
import com.landleaf.bms.domain.response.TenantManagementNodeListResponse;
import com.landleaf.bms.service.ManagementNodeService;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.oauth.api.UserRoleApi;
import com.landleaf.pgsql.core.BizSequenceService;
import com.landleaf.pgsql.enums.BizSequenceEnum;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

import static com.landleaf.bms.domain.enums.ErrorCodeConstants.NODE_EXIST_CHILDREN;
import static com.landleaf.bms.domain.enums.ErrorCodeConstants.NODE_NOT_EXIST;

/**
 * ManagementNodeServiceImpl
 *
 * @author 张力方
 * @since 2023/6/5
 **/
@Service
@RequiredArgsConstructor
public class ManagementNodeServiceImpl implements ManagementNodeService {
    private final ManagementNodeMapper managementNodeMapper;
    private final BizSequenceService bizSequenceService;
    private final UserRoleApi userRoleApi;

    @Override
    public String createTenantRootNode(ManagementNodeAddRootRequest request) {
        ManagementNodeEntity managementNodeEntity = new ManagementNodeEntity();
        managementNodeEntity.setName(request.getName());
        managementNodeEntity.setCode(request.getCode());
        managementNodeEntity.setParentBizNodeId(BmsConstants.ROOT_MANAGEMENT_BIZ_NODE_ID);
        String bizNodeId = bizSequenceService.next(BizSequenceEnum.NODE);
        managementNodeEntity.setBizNodeId(bizNodeId);
        managementNodeEntity.setPath("/" + bizNodeId);
        managementNodeEntity.setType(ManagementNodeTypeEnum.ROOT.getType());
        managementNodeEntity.setSort(0);
        managementNodeMapper.insert(managementNodeEntity);
        return bizNodeId;
    }

    @Override
    public String addManagementNode(ManagementNodeAddRequest request) {
        Long tenantId = request.getTenantId();
        if (tenantId == null) {
            tenantId = LoginUserUtil.getLoginTenantId();
        }
        // 校验用户角色
        checkUserRole(tenantId);
        TenantContext.setIgnore(true);
        // 校验 code 是否唯一
        String code = request.getCode();
        if (!isUniqueCode(code, tenantId)) {
            throw new ServiceException(ErrorCodeConstants.NODE_CODE_NOT_UNIQUE);
        }
        ManagementNodeEntity managementNodeEntity = new ManagementNodeEntity();
        managementNodeEntity.setName(request.getName());
        managementNodeEntity.setCode(code);
        String parentBizNodeId = request.getParentBizNodeId();
        managementNodeEntity.setParentBizNodeId(parentBizNodeId);
        String bizNodeId = bizSequenceService.next(BizSequenceEnum.NODE);
        managementNodeEntity.setBizNodeId(bizNodeId);
        managementNodeEntity.setType(request.getType());
        int maxSort = managementNodeMapper.selectMaxSort(tenantId);
        managementNodeEntity.setSort(maxSort + 1);
        managementNodeEntity.setPath(getPath(bizNodeId, parentBizNodeId));
        managementNodeEntity.setTenantId(tenantId);
        managementNodeMapper.insert(managementNodeEntity);
        return bizNodeId;
    }

    @Override
    public void editManagementNode(ManagementNodeEditRequest request) {
        Long tenantId = request.getTenantId();
        if (tenantId == null) {
            tenantId = LoginUserUtil.getLoginTenantId();
        }
        // 校验用户角色
        checkUserRole(tenantId);
        TenantContext.setIgnore(true);
        // 校验 code 是否唯一
        String code = request.getCode();
        if (!isUniqueCode(code, request.getBizNodeId(), tenantId)) {
            throw new ServiceException(ErrorCodeConstants.NODE_CODE_NOT_UNIQUE);
        }
        String bizNodeId = request.getBizNodeId();
        ManagementNodeEntity managementNodeEntity = managementNodeMapper.selectOne(Wrappers.<ManagementNodeEntity>lambdaQuery()
                .eq(ManagementNodeEntity::getBizNodeId, bizNodeId)
                .eq(ManagementNodeEntity::getTenantId, tenantId));
        managementNodeEntity.setName(request.getName());
        managementNodeEntity.setCode(code);
        String parentBizNodeId = request.getParentBizNodeId();
        managementNodeEntity.setParentBizNodeId(parentBizNodeId);
        managementNodeEntity.setType(request.getType());
        managementNodeEntity.setPath(getPath(bizNodeId, parentBizNodeId));
        managementNodeEntity.setTenantId(tenantId);
        managementNodeMapper.updateById(managementNodeEntity);
    }

    @Override
    public boolean checkCodeUnique(ManagementNodeCodeUniqueRequest request) {
        TenantContext.setIgnore(true);
        String bizNodeId = request.getBizNodeId();
        String code = request.getCode();
        Long tenantId = request.getTenantId();
        if (StringUtils.isBlank(bizNodeId)) {
            return isUniqueCode(code, tenantId);
        } else {
            return isUniqueCode(code, bizNodeId, tenantId);
        }
    }

    @Override
    public List<ManagementNodeListResponse> getManagementNodeList(Long tenantId) {
        TenantContext.setIgnore(true);
        if (tenantId == null) {
            tenantId = LoginUserUtil.getLoginTenantId();
        }
        // 校验用户角色
        checkUserRole(tenantId);
        // 查询当前租户下所有管理节点
        List<ManagementNodeListResponse> managementNodeList = managementNodeMapper.getManagementNodeList(tenantId);
        // 转换为树结构返回前端
        return covertList2Tree(managementNodeList);
    }

    @Override
    public void modifySort(ManagementNodeSortRequest request) {
        Long nodeId = request.getNodeId();
        Long nextNodeId = request.getNextNodeId();
        Long tenantId = request.getTenantId();
        if (tenantId == null) {
            tenantId = LoginUserUtil.getLoginTenantId();
        }
        // 校验用户角色
        checkUserRole(tenantId);
        TenantContext.setIgnore(true);
        // 查询当前租户下所有管理节点
        List<ManagementNodeEntity> managementNodeEntities = managementNodeMapper.selectList(Wrappers
                .<ManagementNodeEntity>lambdaQuery()
                .eq(ManagementNodeEntity::getTenantId, tenantId)
                .orderBy(true, true, ManagementNodeEntity::getSort));
        ManagementNodeEntity currentNode = managementNodeEntities.stream().filter(item -> item.getId().equals(nodeId)).findFirst().orElseThrow(() -> new ServiceException(NODE_NOT_EXIST));
        managementNodeEntities.removeIf(item -> item.getId().equals(nodeId));
        if (nextNodeId == null) {
            managementNodeEntities.add(currentNode);
        } else {
            ManagementNodeEntity nextNode = managementNodeEntities.stream().filter(item -> item.getId().equals(nextNodeId)).findFirst().orElseThrow(() -> new ServiceException(NODE_NOT_EXIST));
            int nextNodeIndex = managementNodeEntities.indexOf(nextNode);
            managementNodeEntities.add(nextNodeIndex, currentNode);
        }
        int sort = 0;
        for (ManagementNodeEntity managementNodeEntity : managementNodeEntities) {
            managementNodeEntity.setSort(sort++);
            managementNodeMapper.updateById(managementNodeEntity);
        }
    }

    @Override
    public void deleteManagementNode(String bizNodeId) {
        List<ManagementNodeEntity> managementNodeEntities = managementNodeMapper.selectList(Wrappers.<ManagementNodeEntity>lambdaQuery()
                .eq(ManagementNodeEntity::getParentBizNodeId, bizNodeId));
        if (!CollectionUtils.isEmpty(managementNodeEntities)) {
            throw new ServiceException(NODE_EXIST_CHILDREN);
        }
        managementNodeMapper.delete(Wrappers.<ManagementNodeEntity>lambdaQuery()
                .eq(ManagementNodeEntity::getBizNodeId, bizNodeId));
    }

    @Override
    public List<TenantManagementNodeListResponse> getTenantManagementNodeList(Long tenantId, Short permissionType) {
        TenantContext.setIgnore(true);
        if (tenantId == null) {
            tenantId = LoginUserUtil.getLoginTenantId();
        }
        // 校验用户角色
        checkUserRole(tenantId);
        // 查询当前租户下所有管理节点
        List<TenantManagementNodeListResponse> tenantManagementNodeList = managementNodeMapper.getTenantManagementNodeList(tenantId);
        if (permissionType.equals(UserNodeTypeEnum.AREA.getType())) {
            tenantManagementNodeList = tenantManagementNodeList.stream().filter(item -> !item.getType().equals(ManagementNodeTypeEnum.PROJECT.getType())).toList();
        }
        // 转换为树结构返回前端
        return covertTenantList2Tree(tenantManagementNodeList);
    }

    /**
     * 获取节点权限path
     *
     * @param bizNodeId       当前节点业务id
     * @param parentNodeBizId 父业务id
     * @return path
     */
    private String getPath(String bizNodeId, String parentNodeBizId) {
        if (parentNodeBizId.equals(BmsConstants.ROOT_MANAGEMENT_BIZ_NODE_ID)) {
            return "/" + bizNodeId;
        }
        ManagementNodeEntity parentNode = managementNodeMapper.selectOne(Wrappers.<ManagementNodeEntity>lambdaQuery().eq(ManagementNodeEntity::getBizNodeId, parentNodeBizId));
        return parentNode.getPath() + "/" + bizNodeId;
    }

    private boolean isUniqueCode(String code, Long tenantId) {
        return isUniqueCode(code, null, tenantId);
    }

    private boolean isUniqueCode(String code, String bizNodeId, Long tenantId) {
        if (null == bizNodeId) {
            return !managementNodeMapper.exists(Wrappers.<ManagementNodeEntity>lambdaQuery()
                    .eq(ManagementNodeEntity::getCode, code)
                    .eq(ManagementNodeEntity::getTenantId, tenantId));
        } else {
            return !managementNodeMapper.exists(Wrappers.<ManagementNodeEntity>lambdaQuery()
                    .eq(ManagementNodeEntity::getTenantId, tenantId)
                    .eq(ManagementNodeEntity::getCode, code)
                    .ne(ManagementNodeEntity::getBizNodeId, bizNodeId)
            );
        }
    }

    private List<ManagementNodeListResponse> covertList2Tree(List<ManagementNodeListResponse> managementNodeListResponseList) {
        if (CollectionUtils.isEmpty(managementNodeListResponseList)) {
            return Collections.emptyList();
        }
        List<ManagementNodeListResponse> managementNodeListResponses = managementNodeListResponseList.stream()
                .filter(item -> item.getParentBizNodeId().equals(BmsConstants.ROOT_MANAGEMENT_BIZ_NODE_ID))
                .toList();
        if (CollectionUtils.isEmpty(managementNodeListResponses)) {
            throw new ServiceException(ErrorCodeConstants.ROOT_NODE_NOT_EXIST);
        }
        for (ManagementNodeListResponse managementNodeListResponse : managementNodeListResponses) {
            recursiveBuildTenantManagementNodeListResponse(managementNodeListResponse, managementNodeListResponseList);
        }
        return managementNodeListResponses;
    }

    private void recursiveBuildTenantManagementNodeListResponse(ManagementNodeListResponse response, List<ManagementNodeListResponse> nodeProjectTreeResponses) {
        List<ManagementNodeListResponse> childrenManagementNodeEntities = nodeProjectTreeResponses.stream()
                .filter(item -> item.getParentBizNodeId()
                        .equals(response.getBizNodeId())).toList();
        if (CollectionUtils.isEmpty(childrenManagementNodeEntities)) {
            return;
        }
        response.setChildren(childrenManagementNodeEntities);
        for (ManagementNodeListResponse child : childrenManagementNodeEntities) {
            recursiveBuildTenantManagementNodeListResponse(child, nodeProjectTreeResponses);
        }
    }

    private List<TenantManagementNodeListResponse> covertTenantList2Tree(List<TenantManagementNodeListResponse> managementNodeListResponseList) {
        if (CollectionUtils.isEmpty(managementNodeListResponseList)) {
            return Collections.emptyList();
        }
        List<TenantManagementNodeListResponse> managementNodeListResponses = managementNodeListResponseList.stream()
                .filter(item -> item.getParentBizNodeId().equals(BmsConstants.ROOT_MANAGEMENT_BIZ_NODE_ID))
                .toList();
        if (CollectionUtils.isEmpty(managementNodeListResponses)) {
            throw new ServiceException(ErrorCodeConstants.ROOT_NODE_NOT_EXIST);
        }
        for (TenantManagementNodeListResponse managementNodeListResponse : managementNodeListResponses) {
            recursiveBuildTenantManagementNodeListResponse(managementNodeListResponse, managementNodeListResponseList);
        }
        return managementNodeListResponses;
    }

    private void recursiveBuildTenantManagementNodeListResponse(TenantManagementNodeListResponse response, List<TenantManagementNodeListResponse> nodeProjectTreeResponses) {
        List<TenantManagementNodeListResponse> childrenManagementNodeEntities = nodeProjectTreeResponses.stream()
                .filter(item -> item.getParentBizNodeId()
                        .equals(response.getBizNodeId())).toList();
        if (CollectionUtils.isEmpty(childrenManagementNodeEntities)) {
            return;
        }
        response.setChildren(childrenManagementNodeEntities);
        for (TenantManagementNodeListResponse child : childrenManagementNodeEntities) {
            recursiveBuildTenantManagementNodeListResponse(child, nodeProjectTreeResponses);
        }
    }

    private void checkUserRole(Long tenantId) {
        Long userId = LoginUserUtil.getLoginUserId();
        Long loginTenantId = LoginUserUtil.getLoginTenantId();
        if (!loginTenantId.equals(tenantId)) {
            Boolean platformAdmin = userRoleApi.isPlatformAdmin(userId).getResult();
            if (platformAdmin.equals(Boolean.FALSE)) {
                throw new ServiceException(ErrorCodeConstants.USER_NODE_PERMISSION_NOT_ENOUGH);
            }
        } else {
            Boolean tenantAdmin = userRoleApi.isTenantAdmin(userId).getResult();
            if (tenantAdmin.equals(Boolean.FALSE)) {
                Boolean platformAdmin = userRoleApi.isPlatformAdmin(userId).getResult();
                if (platformAdmin.equals(Boolean.FALSE)) {
                    throw new ServiceException(ErrorCodeConstants.USER_NODE_PERMISSION_NOT_ENOUGH);
                }
            }
        }
    }
}
