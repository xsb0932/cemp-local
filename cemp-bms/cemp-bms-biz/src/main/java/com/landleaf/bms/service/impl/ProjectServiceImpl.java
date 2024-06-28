package com.landleaf.bms.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.api.dto.*;
import com.landleaf.bms.dal.mapper.ManagementNodeMapper;
import com.landleaf.bms.dal.mapper.ProjectMapper;
import com.landleaf.bms.dal.mapper.ProjectSpaceMapper;
import com.landleaf.bms.dal.mapper.UserNodeMapper;
import com.landleaf.bms.domain.entity.ManagementNodeEntity;
import com.landleaf.bms.domain.entity.ProjectEntity;
import com.landleaf.bms.domain.entity.ProjectSpaceEntity;
import com.landleaf.bms.domain.entity.UserNodeEntity;
import com.landleaf.bms.domain.enums.*;
import com.landleaf.bms.domain.request.ProjectAddRequest;
import com.landleaf.bms.domain.request.ProjectTreeListRequest;
import com.landleaf.bms.domain.response.NodeProjectTreeResponse;
import com.landleaf.bms.domain.response.ScheduleProjectResponse;
import com.landleaf.bms.service.AddressService;
import com.landleaf.bms.service.ProjectService;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.exception.enums.GlobalErrorCodeConstants;
import com.landleaf.comm.license.LicenseCheck;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.oauth.api.UserRpcApi;
import com.landleaf.oauth.api.dto.UserDTO;
import com.landleaf.pgsql.core.BizSequenceService;
import com.landleaf.pgsql.enums.BizSequenceEnum;
import com.landleaf.redis.constance.DictConstance;
import com.landleaf.redis.dict.DictUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.landleaf.bms.domain.enums.ErrorCodeConstants.PROJECT_CODE_NOT_UNIQUE;
import static com.landleaf.bms.domain.enums.ErrorCodeConstants.PROJECT_NAME_NOT_UNIQUE;

/**
 * ProjectServiceImpl
 *
 * @author 张力方
 * @since 2023/6/5
 **/
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final UserNodeMapper userNodeMapper;
    private final ManagementNodeMapper managementNodeMapper;
    private final ProjectMapper projectMapper;
    private final BizSequenceService bizSequenceService;
    private final AddressService addressService;
    private final DictUtils dictUtils;
    private final ProjectSpaceMapper projectSpaceMapper;
    private final UserRpcApi userRpcApi;
    private final LicenseCheck licenseCheck;

    @Override
    public NodeProjectTreeResponse getCurrentUserProjectTree() {
        TenantContext.setIgnore(true);
        Long userId = LoginUserUtil.getLoginUserId();
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
        List<NodeProjectTreeResponse> nodeProjectTreeResponses = new ArrayList<>();
        if (type.equals(UserNodeTypeEnum.PROJECT.getType())) {
            nodeProjectTreeResponses = managementNodeMapper.recursiveUpListByIds(nodeIds);
        }
        if (type.equals(UserNodeTypeEnum.AREA.getType())) {
            List<ManagementNodeEntity> downManagementNodeEntities = managementNodeMapper.recursiveDownListByIds(nodeIds);
            nodeProjectTreeResponses = managementNodeMapper.recursiveUpListByIds(downManagementNodeEntities.stream().map(ManagementNodeEntity::getId).toList());
        }
        // 转换为树结构
        return covertList2Tree(nodeProjectTreeResponses);
    }

    @Override
    public NodeProjectTreeResponse getCurrentUserNodeTree() {
        TenantContext.setIgnore(true);
        Long userId = LoginUserUtil.getLoginUserId();
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
        List<NodeProjectTreeResponse> nodeProjectTreeResponses = new ArrayList<>();
        if (type.equals(UserNodeTypeEnum.PROJECT.getType())) {
            nodeProjectTreeResponses = managementNodeMapper.recursiveUpListByIds(nodeIds);
        }
        if (type.equals(UserNodeTypeEnum.AREA.getType())) {
            List<ManagementNodeEntity> downManagementNodeEntities = managementNodeMapper.recursiveDownListByIds(nodeIds);
            nodeProjectTreeResponses = managementNodeMapper.recursiveUpListByIds(downManagementNodeEntities.stream().map(ManagementNodeEntity::getId).toList());
        }
        // 去掉项目类型
        nodeProjectTreeResponses.removeIf(nodeProjectTree -> nodeProjectTree.getType().equals(ManagementNodeTypeEnum.PROJECT.getType()));
        // 转换为树结构
        return covertList2Tree(nodeProjectTreeResponses);
    }

    @Override
    public NodeProjectTreeResponse getCurrentUserProjectTree2() {
        TenantContext.setIgnore(true);
        Long userId = LoginUserUtil.getLoginUserId();
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
        List<NodeProjectTreeResponse> nodeProjectTreeResponses = new ArrayList<>();
        if (type.equals(UserNodeTypeEnum.PROJECT.getType())) {
            nodeProjectTreeResponses = managementNodeMapper.recursiveUpListByIds(nodeIds);
        }
        if (type.equals(UserNodeTypeEnum.AREA.getType())) {
            List<ManagementNodeEntity> downManagementNodeEntities = managementNodeMapper.recursiveDownListByIds(nodeIds);
            nodeProjectTreeResponses = managementNodeMapper.recursiveUpListByIds(downManagementNodeEntities.stream().map(ManagementNodeEntity::getId).toList());
        }
        nodeProjectTreeResponses.forEach(o -> {
            if (o.getType().equals(ManagementNodeTypeEnum.PROJECT.getType())) {
                o.setSelectable(Boolean.TRUE);
                o.setSelectId(o.getProjectBizId());
            } else {
                o.setSelectable(Boolean.FALSE);
                o.setSelectId(o.getBizNodeId());
            }
        });
        // 转换为树结构
        return covertList2Tree(nodeProjectTreeResponses);
    }

    @Override
    public List<ProjectEntity> getCurrentUserProjectList() {
        TenantContext.setIgnore(true);
        try {
            Long userId = LoginUserUtil.getLoginUserId();
            // 当前用户的节点权限
            List<UserNodeEntity> userNodeEntityList = userNodeMapper.selectList(Wrappers.<UserNodeEntity>lambdaQuery()
                    .eq(UserNodeEntity::getUserId, userId));
            if (CollectionUtils.isEmpty(userNodeEntityList)) {
                return null;
            }
            // 具有权限的节点id列表
            List<Long> nodeIds = userNodeEntityList.stream().map(UserNodeEntity::getNodeId).toList();
            Short type = userNodeEntityList.get(0).getType();
            List<String> bizNodeIds = new ArrayList<>();
            // 满足权限的节点列表
            if (type.equals(UserNodeTypeEnum.PROJECT.getType())) {
                bizNodeIds = managementNodeMapper.selectList(new LambdaQueryWrapper<ManagementNodeEntity>()
                                .in(ManagementNodeEntity::getId, nodeIds)
                                .select(ManagementNodeEntity::getBizNodeId))
                        .stream()
                        .map(ManagementNodeEntity::getBizNodeId)
                        .collect(Collectors.toList());

            }
            if (type.equals(UserNodeTypeEnum.AREA.getType())) {
                bizNodeIds = managementNodeMapper.recursiveDownListByIds(nodeIds)
                        .stream()
                        .filter(o -> o.getType().equals(ManagementNodeTypeEnum.PROJECT.getType()))
                        .map(ManagementNodeEntity::getBizNodeId)
                        .collect(Collectors.toList());
            }
            if (CollectionUtil.isEmpty(bizNodeIds)) {
                return Collections.emptyList();
            }
            return projectMapper.selectList(new LambdaQueryWrapper<ProjectEntity>()
                    .in(ProjectEntity::getBizNodeId, bizNodeIds)
                    .select(ProjectEntity::getBizProjectId, ProjectEntity::getName, ProjectEntity::getId));
        } finally {
            TenantContext.setIgnore(false);
        }
    }

    @Override
    public List<String> getUserProjectBizIds(Long userId) {
        // 获取当前用户项目权限
        boolean ignore = TenantContext.isIgnore();
        TenantContext.setIgnore(true);
        List<String> projectBizIds = new ArrayList<>();
        try {
            List<UserNodeEntity> userNodeEntities = userNodeMapper.selectList(Wrappers.<UserNodeEntity>lambdaQuery().eq(UserNodeEntity::getUserId, userId));
            if (!CollectionUtils.isEmpty(userNodeEntities)) {
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
            }
            return projectBizIds;
        } finally {
            TenantContext.setIgnore(ignore);
        }
    }

    @Override
    public List<ProjectListResponse> getProjectList(ProjectTreeListRequest request) {
        TenantContext.setIgnore(true);
        Long userId = LoginUserUtil.getLoginUserId();
        String bizNodeId = request.getBizNodeId();
        List<UserNodeEntity> userNodeEntityList = userNodeMapper.selectList(Wrappers.<UserNodeEntity>lambdaQuery()
                .eq(UserNodeEntity::getUserId, userId));
        if (CollectionUtils.isEmpty(userNodeEntityList)) {
            return Collections.emptyList();
        }
        Short type = userNodeEntityList.get(0).getType();
        if (type.equals(UserNodeTypeEnum.PROJECT.getType())) {
            return projectMapper.recursiveDownCurrentUserListByBizNodeId(bizNodeId, ManagementNodeTypeEnum.PROJECT.getType(), userId);
        }
        if (type.equals(UserNodeTypeEnum.AREA.getType())) {
            // 最终查询的权限节点列表
            // 考虑传递的节点是当前用户拥有权限的节点父节点的情况
            List<Long> nodeIds = new ArrayList<>();
            for (UserNodeEntity userNodeEntity : userNodeEntityList) {
                if (userNodeEntity.getPath().contains(bizNodeId)) {
                    nodeIds.add(userNodeEntity.getNodeId());
                }
            }
            if (CollectionUtils.isEmpty(nodeIds)) {
                return Collections.emptyList();
            }
            return projectMapper.recursiveDownCurrentUserListByNodeId(nodeIds, ManagementNodeTypeEnum.PROJECT.getType());
        }
        return Collections.emptyList();
    }

    @Override
    public ProjectDetailsResponse getDetails(Long id) {
        ProjectEntity projectEntity = projectMapper.selectById(id);
        ProjectDetailsResponse projectDetailsResponse = new ProjectDetailsResponse();
        BeanUtils.copyProperties(projectEntity, projectDetailsResponse);
        UserDTO userDTO = userRpcApi.getUserInfo(projectEntity.getDirectorUserId()).getCheckedData();
        if (null != userDTO) {
            projectDetailsResponse.setDirector(userDTO.getNickname());
        }
        // 管理节点名称
        TenantContext.setIgnore(true);
        String nodeLongName = managementNodeMapper.getLongName(projectEntity.getParentBizNodeId());
        projectDetailsResponse.setParentBizNodeName(nodeLongName);
        // 行政区域名称
        List<String> addressCode = projectDetailsResponse.getAddressCode();
        if (!CollectionUtils.isEmpty(addressCode)) {
            String addressName = addressService.getCountyNameByAddressCode(addressCode.get(addressCode.size() - 1));
            projectDetailsResponse.setAddressName(addressName);
        }
        // 项目业态名称
        String bizType = projectDetailsResponse.getBizType();
        projectDetailsResponse.setBizTypeName(dictUtils.selectDictLabel(DictConstance.PROJECT_BIZ_TYPE, bizType));
        // 项目状态名称
        String status = projectDetailsResponse.getStatus();
        projectDetailsResponse.setStatusName(dictUtils.selectDictLabel(DictConstance.PROJECT_STATUS, status));
        // 能源类型名称
        List<String> energyType = projectDetailsResponse.getEnergyType();
        projectDetailsResponse.setEnergyTypeName(dictUtils.selectDictLabel(DictConstance.ENERGY_TYPE, energyType));
        //能源子系统
        List<String> energySubSystem = projectDetailsResponse.getEnergySubSystem();
        projectDetailsResponse.setEnergySubSystemName(dictUtils.selectDictLabel(DictConstance.ENERGY_SUB_SYSTEM, energySubSystem));
        return projectDetailsResponse;
    }

    @Override
    public List<ScheduleProjectResponse> getScheduleProjectList(Long tenantId) {
        TenantContext.setIgnore(true);
        try {
            List<ScheduleProjectResponse> list = projectMapper.selectList(new LambdaQueryWrapper<ProjectEntity>()
                            .eq(ProjectEntity::getTenantId, tenantId)
                            .select(ProjectEntity::getBizProjectId, ProjectEntity::getName))
                    .stream()
                    .map(o -> {
                        ScheduleProjectResponse temp = new ScheduleProjectResponse();
                        temp.setBizProjectId(o.getBizProjectId()).setName(o.getName());
                        return temp;
                    })
                    .collect(Collectors.toList());
            ScheduleProjectResponse total = new ScheduleProjectResponse().setBizProjectId("0").setName("全部");
            list.add(0, total);
            return list;
        } finally {
            TenantContext.setIgnore(false);
        }
    }

    @Override
    public List<ProjectEntity> getTenantProjectList() {
        return projectMapper.selectList(new LambdaQueryWrapper<ProjectEntity>()
                .eq(ProjectEntity::getTenantId, TenantContext.getTenantId())
                .select(ProjectEntity::getBizProjectId, ProjectEntity::getName));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(ProjectAddRequest request) {
        TenantContext.setIgnore(false);
        // 校验名称，编码是否重复
        String name = request.getName();
        String code = request.getCode();
        Assert.isTrue(Objects.isNull(request.getMobile()) || request.getMobile().length() == 11, "手机号格式错误");
        if (!checkNameUnique(null, name)) {
            throw new ServiceException(PROJECT_NAME_NOT_UNIQUE);
        }
        if (!checkCodeUnique(null, code)) {
            throw new ServiceException(PROJECT_CODE_NOT_UNIQUE);
        }
        UserDTO userDTO = userRpcApi.getUserInfo(request.getDirectorUserId()).getCheckedData();
        if (null == userDTO) {
            throw new BusinessException("负责人用户不存在");
        }

        // check project
        if (licenseCheck.getProjLimit() > 0) {
            // 如果设置值为-1
            TenantContext.setIgnore(true);
            long count = projectMapper.selectCount(Wrappers.emptyWrapper() );
            if (count >= licenseCheck.getProjLimit()) {
                // 抛出指定异常
                throw new BusinessException(GlobalErrorCodeConstants.PROJ_LIMIT.getCode(), GlobalErrorCodeConstants.PROJ_LIMIT.getMsg());
            }
            TenantContext.setIgnore(false);
        }

        // 管理节点新建项目记录 & 校验编码是否唯一
        ManagementNodeEntity managementNodeEntity = addProjectManagementNode(name, code, request.getParentBizNodeId());
        // 保存项目
        ProjectEntity projectEntity = new ProjectEntity();
        BeanUtils.copyProperties(request, projectEntity);
        projectEntity.setDirector(userDTO.getNickname());
        projectEntity.setBizProjectId(bizSequenceService.next(BizSequenceEnum.PROJECT));
        projectEntity.setPath(managementNodeEntity.getPath());
        projectEntity.setBizNodeId(managementNodeEntity.getBizNodeId());
        projectMapper.insert(projectEntity);

        // 创建区域项目根节点
        ProjectSpaceEntity entity = new ProjectSpaceEntity();
        entity.setName(projectEntity.getName());
        entity.setBizId(bizSequenceService.next(BizSequenceEnum.AREA));
        entity.setParentId(0L);
        entity.setType(CnfSubareaTypeEnum.PARK.getValue());
        entity.setProportion(projectEntity.getArea());
        entity.setProjectId(projectEntity.getId());
        projectSpaceMapper.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ProjectEditRequest request) {
        // 校验项目名称是否重复
        Assert.isTrue(Objects.isNull(request.getMobile()) || request.getMobile().length() == 11, "手机号格式错误");
        String name = request.getName();
        if (!checkCodeUnique(request.getId(), name)) {
            throw new ServiceException(PROJECT_NAME_NOT_UNIQUE);
        }
        UserDTO userDTO = userRpcApi.getUserInfo(request.getDirectorUserId()).getCheckedData();
        if (null == userDTO) {
            throw new BusinessException("负责人用户不存在");
        }
        ProjectEntity projectEntity = projectMapper.selectById(request.getId());
        // 修改管理节点
        ManagementNodeEntity managementNodeEntity = managementNodeMapper.selectOne(Wrappers
                .<ManagementNodeEntity>lambdaQuery()
                .eq(ManagementNodeEntity::getBizNodeId, projectEntity.getBizNodeId()));
        managementNodeEntity.setParentBizNodeId(request.getParentBizNodeId());
        managementNodeEntity.setPath(getPath(projectEntity.getBizNodeId(), request.getParentBizNodeId()));
        managementNodeEntity.setName(name);
        managementNodeMapper.updateById(managementNodeEntity);
        // 修改项目
        BeanUtils.copyProperties(request, projectEntity);
        projectEntity.setDirector(userDTO.getNickname());
        projectEntity.setPath(managementNodeEntity.getPath());
        projectEntity.setParentBizNodeId(managementNodeEntity.getParentBizNodeId());
        projectMapper.updateById(projectEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long projectId) {
        ProjectEntity projectEntity = projectMapper.selectById(projectId);
        projectMapper.deleteById(projectId);
        managementNodeMapper.delete(Wrappers.<ManagementNodeEntity>lambdaQuery()
                .eq(ManagementNodeEntity::getBizNodeId, projectEntity.getBizNodeId()));
    }

    @Override
    public Page<ProjectListResponse> pageList(ProjectListRequest request) {
        // 获取当前用户项目权限
        List<String> projectBizIds = this.getUserProjectBizIds(LoginUserUtil.getLoginUserId());
        if (CollectionUtils.isEmpty(projectBizIds)) {
            return Page.of(request.getPageNo(), request.getPageSize());
        }
        TenantContext.setIgnore(false);
        Page<ProjectListResponse> projectList = projectMapper.selectPageList(Page.of(request.getPageNo(), request.getPageSize()), request, projectBizIds);
        List<ProjectListResponse> records = projectList.getRecords();
        List<Long> directorUserIdList = records.stream().map(ProjectListResponse::getDirectorUserId).distinct().toList();
        Map<Long, String> userMap = userRpcApi.getUserInfoList(directorUserIdList).getCheckedData().stream().collect(Collectors.toMap(UserDTO::getId, UserDTO::getNickname, (o1, o2) -> o1));
        for (ProjectListResponse projectListResponse : records) {
            String nickname = userMap.get(projectListResponse.getDirectorUserId());
            if (null != nickname) {
                projectListResponse.setDirector(nickname);
            }
            // 行政区域名称
            List<String> addressCode = projectListResponse.getAddressCode();
            if (!CollectionUtils.isEmpty(addressCode)) {
                String addressName = addressService.getCountyNameByAddressCode(addressCode.get(addressCode.size() - 1));
                projectListResponse.setAddressName(addressName);
            }
            // 项目业态名称
            String bizType = projectListResponse.getBizType();
            projectListResponse.setBizTypeName(dictUtils.selectDictLabel(DictConstance.PROJECT_BIZ_TYPE, bizType));
            // 项目状态名称
            String status = projectListResponse.getStatus();
            projectListResponse.setStatusName(dictUtils.selectDictLabel(DictConstance.PROJECT_STATUS, status));
            // 能源类型名称
            List<String> energyType = projectListResponse.getEnergyType();
            projectListResponse.setEnergyTypeName(dictUtils.selectDictLabel(DictConstance.ENERGY_TYPE, energyType));
            //能源子系统
            List<String> energySubSystem = projectListResponse.getEnergySubSystem();
            projectListResponse.setEnergySubSystemName(dictUtils.selectDictLabel(DictConstance.ENERGY_SUB_SYSTEM, energySubSystem));
        }
        return projectList;
    }

    @Override
    public boolean checkNameUnique(ProjectNameUniqueRequest request) {
        return checkNameUnique(request.getId(), request.getName());
    }

    private boolean checkNameUnique(Long id, String name) {
        if (null == id) {
            return !projectMapper.exists(Wrappers.<ProjectEntity>lambdaQuery()
                    .eq(ProjectEntity::getName, name));
        } else {
            return !projectMapper.exists(Wrappers.<ProjectEntity>lambdaQuery()
                    .eq(ProjectEntity::getName, name)
                    .ne(ProjectEntity::getId, id));
        }
    }

    @Override
    public boolean checkCodeUnique(ProjectCodeUniqueRequest request) {
        return checkCodeUnique(request.getId(), request.getCode());
    }

    private boolean checkCodeUnique(Long id, String code) {
        if (null == id) {
            return !projectMapper.exists(Wrappers.<ProjectEntity>lambdaQuery()
                    .eq(ProjectEntity::getCode, code))
                    &&
                    !managementNodeMapper.exists(Wrappers.<ManagementNodeEntity>lambdaQuery()
                            .eq(ManagementNodeEntity::getCode, code));
        } else {
            return !projectMapper.exists(Wrappers.<ProjectEntity>lambdaQuery()
                    .eq(ProjectEntity::getCode, code)
                    .ne(ProjectEntity::getId, id))
                    &&
                    !managementNodeMapper.exists(Wrappers.<ManagementNodeEntity>lambdaQuery()
                            .eq(ManagementNodeEntity::getCode, code));
        }

    }

    @Override
    public List<String> listAllAddressCode() {
        TenantContext.setIgnore(true);
        return projectMapper.selectList(new LambdaQueryWrapper<ProjectEntity>().select(ProjectEntity::getAddressCode))
                .stream()
                .filter(item -> CollectionUtil.isNotEmpty(item.getAddressCode()))
                .map(item -> item.getAddressCode().get(item.getAddressCode().size() - 1)).distinct().toList();
    }

    @Override
    public ProjectEntity selectByBizProjectId(String bizProjectId) {
        TenantContext.setIgnore(true);
        return projectMapper.selectOne(new LambdaQueryWrapper<ProjectEntity>().eq(ProjectEntity::getBizProjectId, bizProjectId));
    }

    private NodeProjectTreeResponse covertList2Tree(List<NodeProjectTreeResponse> nodeProjectTreeResponses) {
        if (CollectionUtils.isEmpty(nodeProjectTreeResponses)) {
            return null;
        }
        NodeProjectTreeResponse nodeProjectTreeResponse = nodeProjectTreeResponses.stream()
                .filter(item -> item.getParentBizNodeId().equals(BmsConstants.ROOT_MANAGEMENT_BIZ_NODE_ID))
                .findFirst().orElseThrow(() -> new ServiceException(ErrorCodeConstants.ROOT_NODE_NOT_EXIST));
        recursiveBuildNodeProjectTreeResponse(nodeProjectTreeResponse, nodeProjectTreeResponses);
        return nodeProjectTreeResponse;
    }

    private void recursiveBuildNodeProjectTreeResponse(NodeProjectTreeResponse response, List<NodeProjectTreeResponse> nodeProjectTreeResponses) {
        List<NodeProjectTreeResponse> childrenManagementNodeEntities = nodeProjectTreeResponses.stream().filter(item -> item.getParentBizNodeId().equals(response.getBizNodeId())).toList();
        if (CollectionUtils.isEmpty(childrenManagementNodeEntities)) {
            return;
        }
        response.setChildren(childrenManagementNodeEntities);
        for (NodeProjectTreeResponse child : childrenManagementNodeEntities) {
            recursiveBuildNodeProjectTreeResponse(child, nodeProjectTreeResponses);
        }
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

    private ManagementNodeEntity addProjectManagementNode(String name, String code, String parentBizNodeId) {
        // 校验 code 是否唯一
        if (managementNodeMapper.exists(Wrappers.<ManagementNodeEntity>lambdaQuery()
                .eq(ManagementNodeEntity::getCode, code))) {
            throw new ServiceException(ErrorCodeConstants.NODE_CODE_NOT_UNIQUE);
        }
        ManagementNodeEntity managementNodeEntity = new ManagementNodeEntity();
        managementNodeEntity.setName(name);
        managementNodeEntity.setCode(code);
        managementNodeEntity.setParentBizNodeId(parentBizNodeId);
        String bizNodeId = bizSequenceService.next(BizSequenceEnum.NODE);
        managementNodeEntity.setBizNodeId(bizNodeId);
        managementNodeEntity.setType(ManagementNodeTypeEnum.PROJECT.getType());
        int maxSort = managementNodeMapper.selectMaxSort(TenantContext.getTenantId());
        managementNodeEntity.setSort(maxSort + 1);
        managementNodeEntity.setPath(getPath(bizNodeId, parentBizNodeId));
        managementNodeMapper.insert(managementNodeEntity);
        return managementNodeEntity;
    }
}
