package com.landleaf.bms.api;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.bms.api.dto.NodeProjectTreeDTO;
import com.landleaf.bms.api.dto.UserProjectDTO;
import com.landleaf.bms.dal.mapper.ProjectMapper;
import com.landleaf.bms.dal.mapper.UserNodeMapper;
import com.landleaf.bms.domain.entity.UserNodeEntity;
import com.landleaf.bms.domain.enums.ManagementNodeTypeEnum;
import com.landleaf.bms.domain.enums.UserNodeTypeEnum;
import com.landleaf.bms.domain.response.NodeProjectTreeResponse;
import com.landleaf.bms.service.ProjectService;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Feign 服务 - 用户项目相关
 *
 * @author 张力方
 * @since 2023/6/8
 **/
@RestController
@RequiredArgsConstructor
public class UserProjectApiImpl implements UserProjectApi {
    private final UserNodeMapper userNodeMapper;
    private final ProjectMapper projectMapper;
    private final ProjectService projectService;

    @Override
    public Response<List<String>> getUserProjectBizIds(Long userId) {
        TenantContext.setIgnore(true);
        List<UserNodeEntity> userNodeEntities = userNodeMapper.selectList(Wrappers.<UserNodeEntity>lambdaQuery().eq(UserNodeEntity::getUserId, userId));
        List<String> projectBizIds = new ArrayList<>();
        if (CollectionUtils.isEmpty(userNodeEntities)) {
            return Response.success(projectBizIds);
        }
        Short type = userNodeEntities.get(0).getType();
        List<Long> nodeIds = userNodeEntities.stream().map(UserNodeEntity::getNodeId).toList();
        // 管理节点授权类型为项目
        if (type.equals(UserNodeTypeEnum.PROJECT.getType())) {
            projectBizIds = projectMapper.getProjectBizIdsByProjectNodeIds(nodeIds);
        }
        // 管理节点授权类型为区域
        if (type.equals(UserNodeTypeEnum.AREA.getType())) {
            projectBizIds = projectMapper.recursiveDownBizListByNodeIds(nodeIds, ManagementNodeTypeEnum.PROJECT.getType());

        }
        return Response.success(projectBizIds);
    }

    @Override
    public Response<List<UserProjectDTO>> getUserProjectList(Long userId) {
        List<UserProjectDTO> projectList = new ArrayList<>();
        TenantContext.setIgnore(true);
        try {
            List<UserNodeEntity> userNodeEntities = userNodeMapper.selectList(Wrappers.<UserNodeEntity>lambdaQuery().eq(UserNodeEntity::getUserId, userId));
            if (CollectionUtils.isEmpty(userNodeEntities)) {
                return Response.success(projectList);
            }
            Short type = userNodeEntities.get(0).getType();
            List<Long> nodeIds = userNodeEntities.stream().map(UserNodeEntity::getNodeId).toList();
            // 管理节点授权类型为项目
            if (type.equals(UserNodeTypeEnum.PROJECT.getType())) {
                projectList = projectMapper.getProjectListByProjectNodeIds(nodeIds);
            }
            // 管理节点授权类型为区域
            if (type.equals(UserNodeTypeEnum.AREA.getType())) {
                projectList = projectMapper.recursiveDownProjectListByNodeIds(nodeIds, ManagementNodeTypeEnum.PROJECT.getType());

            }
            return Response.success(projectList);
        } finally {
            TenantContext.setIgnore(false);
        }
    }

    @Override
    public Response<NodeProjectTreeDTO> getCurrentUserProjectTree() {
        NodeProjectTreeResponse currentUserProjectTree = projectService.getCurrentUserProjectTree();
        NodeProjectTreeDTO dto = BeanUtil.copyProperties(currentUserProjectTree, NodeProjectTreeDTO.class);
        return Response.success(dto);
    }
}
