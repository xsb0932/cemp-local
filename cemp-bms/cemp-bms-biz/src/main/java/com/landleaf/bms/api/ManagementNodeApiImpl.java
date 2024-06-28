package com.landleaf.bms.api;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.bms.api.dto.AreaManageNodeResponse;
import com.landleaf.bms.api.dto.ManagementNodeRootCreateRequest;
import com.landleaf.bms.dal.mapper.ManagementNodeMapper;
import com.landleaf.bms.dal.mapper.ProjectMapper;
import com.landleaf.bms.dal.mapper.UserNodeMapper;
import com.landleaf.bms.domain.entity.ManagementNodeEntity;
import com.landleaf.bms.domain.entity.ProjectEntity;
import com.landleaf.bms.domain.entity.UserNodeEntity;
import com.landleaf.bms.domain.enums.BmsConstants;
import com.landleaf.bms.domain.enums.ManagementNodeTypeEnum;
import com.landleaf.bms.domain.enums.UserNodeTypeEnum;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.pgsql.core.BizSequenceService;
import com.landleaf.pgsql.enums.BizSequenceEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.landleaf.bms.domain.enums.ErrorCodeConstants.ROOT_NODE_EXISTED;

/**
 * 管理节点
 *
 * @author 张力方
 * @since 2023/6/7
 **/
@Slf4j
@RestController
@RequiredArgsConstructor
public class ManagementNodeApiImpl implements ManagementNodeApi {
    private final ManagementNodeMapper managementNodeMapper;
    private final UserNodeMapper userNodeMapper;
    private final ProjectMapper projectMapper;
    private final BizSequenceService bizSequenceService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Void> createTenantRootNode(ManagementNodeRootCreateRequest request) {
        // 忽略租户上下文
        TenantContext.setIgnore(true);

        // 判断根节点是否已存在
        boolean oldNodeExists = managementNodeMapper.exists(Wrappers.<ManagementNodeEntity>lambdaQuery()
                .eq(ManagementNodeEntity::getTenantId, request.getTenantId())
                .eq(ManagementNodeEntity::getType, ManagementNodeTypeEnum.ROOT.getType()));
        if (oldNodeExists) {
            throw new ServiceException(ROOT_NODE_EXISTED);
        }

        // 构建租户根节点对象
        ManagementNodeEntity managementNodeEntity = new ManagementNodeEntity();
        managementNodeEntity.setTenantId(request.getTenantId());
        managementNodeEntity.setName(request.getTenantName());
        managementNodeEntity.setCode(request.getTenantCode());
        String bizNodeId = bizSequenceService.next(BizSequenceEnum.NODE);
        managementNodeEntity.setParentBizNodeId(BmsConstants.ROOT_MANAGEMENT_BIZ_NODE_ID);
        managementNodeEntity.setBizNodeId(bizNodeId);
        managementNodeEntity.setPath("/" + bizNodeId);
        managementNodeEntity.setType(ManagementNodeTypeEnum.ROOT.getType());
        managementNodeEntity.setSort(0);
        managementNodeMapper.insert(managementNodeEntity);

        // 租户管理员具有所有租户管理节点的权限
        UserNodeEntity userNodeEntity = new UserNodeEntity();
        userNodeEntity.setNodeId(managementNodeEntity.getId());
        userNodeEntity.setUserId(request.getTenantAdminId());
        userNodeEntity.setType(UserNodeTypeEnum.AREA.getType());
        userNodeEntity.setTenantId(request.getTenantId());
        userNodeEntity.setPath(managementNodeEntity.getPath());
        userNodeMapper.insert(userNodeEntity);
        return Response.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Void> deleteTenantNode(String tenantId) {
        // 忽略租户上下文
        TenantContext.setIgnore(true);
        // 删除用户管理节点
        LambdaQueryWrapper<ManagementNodeEntity> wrapper = Wrappers.<ManagementNodeEntity>lambdaQuery().eq(ManagementNodeEntity::getTenantId, tenantId);
        List<ManagementNodeEntity> managementNodeEntities = managementNodeMapper.selectList(wrapper);
        List<Long> nodeIds = managementNodeEntities.stream().map(ManagementNodeEntity::getId).toList();
        userNodeMapper.delete(Wrappers.<UserNodeEntity>lambdaQuery().in(UserNodeEntity::getNodeId, nodeIds));
        // 删除管理节点
        managementNodeMapper.delete(wrapper);
        return Response.success();
    }

    @Override
    public Response<List<String>> getUserProjectByNode(String bizNodeId, Long userId) {
        TenantContext.setIgnore(true);
        ManagementNodeEntity selectNode = managementNodeMapper.selectOne(new LambdaQueryWrapper<ManagementNodeEntity>().eq(ManagementNodeEntity::getBizNodeId, bizNodeId));
        if (null == selectNode) {
            log.warn("bizNodeId {} userId {} 不存在", bizNodeId, userId);
            return Response.success(Collections.emptyList());
        }
        // 当前用户的节点权限
        List<UserNodeEntity> userNodeEntityList = userNodeMapper.selectList(Wrappers.<UserNodeEntity>lambdaQuery()
                .eq(UserNodeEntity::getUserId, userId));
        if (CollectionUtils.isEmpty(userNodeEntityList)) {
            return Response.success(Collections.emptyList());
        }
        boolean selectNodeIsProject = StrUtil.equals(selectNode.getType(), ManagementNodeTypeEnum.PROJECT.getType());
        // 具有权限的节点id列表
        List<Long> nodeIds = userNodeEntityList.stream().map(UserNodeEntity::getNodeId).toList();
        Short type = userNodeEntityList.get(0).getType();
        Collection<String> bizNodeIds = new ArrayList<>();
        // 满足权限的节点列表
        if (type.equals(UserNodeTypeEnum.PROJECT.getType())) {
            bizNodeIds = managementNodeMapper.selectList(new LambdaQueryWrapper<ManagementNodeEntity>()
                            .in(ManagementNodeEntity::getId, nodeIds)
                            .select(ManagementNodeEntity::getBizNodeId))
                    .stream()
                    .map(ManagementNodeEntity::getBizNodeId)
                    .toList();
        }
        if (type.equals(UserNodeTypeEnum.AREA.getType())) {
            bizNodeIds = managementNodeMapper.recursiveDownListByIds(nodeIds)
                    .stream()
                    .filter(o -> o.getType().equals(ManagementNodeTypeEnum.PROJECT.getType()))
                    .map(ManagementNodeEntity::getBizNodeId)
                    .toList();
        }
        if (!selectNodeIsProject) {
            List<String> selectNodeDownProjectNodeIds = managementNodeMapper.recursiveDownListByIds(Collections.singletonList(selectNode.getId()))
                    .stream()
                    .filter(o -> o.getType().equals(ManagementNodeTypeEnum.PROJECT.getType()))
                    .map(ManagementNodeEntity::getBizNodeId)
                    .toList();
            bizNodeIds = CollectionUtil.intersection(selectNodeDownProjectNodeIds, bizNodeIds);
        } else {
            bizNodeIds = CollectionUtil.newArrayList(bizNodeId);
        }
        if (CollectionUtil.isEmpty(bizNodeIds)) {
            return Response.success(Collections.emptyList());
        }

        List<String> bizProjectIds = projectMapper.selectList(new LambdaQueryWrapper<ProjectEntity>()
                        .in(ProjectEntity::getBizNodeId, bizNodeIds)
                        .select(ProjectEntity::getBizProjectId))
                .stream()
                .map(ProjectEntity::getBizProjectId)
                .toList();
        return Response.success(bizProjectIds);
    }

    @Override
    public Response<List<AreaManageNodeResponse>> getAreaNodes(Long tenantId) {
        TenantContext.setIgnore(true);
        LambdaQueryWrapper<ManagementNodeEntity> lw = new LambdaQueryWrapper<>();
        lw.eq(ManagementNodeEntity::getType, "02");
        lw.eq(ManagementNodeEntity::getTenantId, tenantId);
        return Response.success(managementNodeMapper.selectList(lw).stream().map(m -> new AreaManageNodeResponse(m.getBizNodeId(), m.getName())).collect(Collectors.toList()));
    }

    @Override
    public Response<List<String>> getAllProjectByNode(String bizNodeId) {
        TenantContext.setIgnore(true);
        ManagementNodeEntity selectNode = managementNodeMapper.selectOne(new LambdaQueryWrapper<ManagementNodeEntity>().eq(ManagementNodeEntity::getBizNodeId, bizNodeId));
        if (null == selectNode) {
            log.warn("bizNodeId {} 不存在", bizNodeId);
            return Response.success(Collections.emptyList());
        }

        List<String> selectNodeDownProjectNodeIds = managementNodeMapper.recursiveDownListByIds(Collections.singletonList(selectNode.getId()))
                .stream()
                .filter(o -> o.getType().equals(ManagementNodeTypeEnum.PROJECT.getType()))
                .map(ManagementNodeEntity::getBizNodeId)
                .toList();

        if (selectNodeDownProjectNodeIds.isEmpty()) {
            return Response.success(Collections.emptyList());
        }

        List<String> bizProjectIds = projectMapper.selectList(new LambdaQueryWrapper<ProjectEntity>()
                        .in(ProjectEntity::getBizNodeId, selectNodeDownProjectNodeIds)
                        .select(ProjectEntity::getBizProjectId))
                .stream()
                .map(ProjectEntity::getBizProjectId)
                .toList();
        return Response.success(bizProjectIds);
    }


}
