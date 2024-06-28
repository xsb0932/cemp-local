package com.landleaf.bms.api;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.api.dto.*;
import com.landleaf.bms.dal.mapper.ManagementNodeMapper;
import com.landleaf.bms.dal.mapper.ProjectMapper;
import com.landleaf.bms.domain.entity.ManagementNodeEntity;
import com.landleaf.bms.domain.entity.ProjectEntity;
import com.landleaf.bms.domain.enums.ManagementNodeTypeEnum;
import com.landleaf.bms.service.AddressService;
import com.landleaf.bms.service.ProjectService;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.redis.constance.DictConstance;
import com.landleaf.redis.dict.DictUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.landleaf.bms.domain.enums.ErrorCodeConstants.PROJECT_NOT_EXIST;

/**
 * Feign 服务 - 项目相关
 *
 * @author 张力方
 * @since 2023/6/26
 **/
@RestController
@RequiredArgsConstructor
public class ProjectApiImpl implements ProjectApi {
    private final ProjectMapper projectMapper;
    private final ProjectService projectService;
    private final AddressService addressService;
    private final DictUtils dictUtils;
    private final ManagementNodeMapper managementNodeMapper;

    /**
     * 查询项目详情
     *
     * @param bizProjectId 项目业务id
     * @return 项目详情
     */
    @Override
    public Response<ProjectDetailsResponse> getProjectDetails(String bizProjectId) {
        // 特殊要求，需要忽略租户
        TenantContext.setIgnore(true);
        ProjectDetailsResponse projectDetailsResponse = projectMapper.selectProjectDetails(bizProjectId);
        if (projectDetailsResponse == null) {
            throw new ServiceException(PROJECT_NOT_EXIST);
        }
        // 行政区域名称
        List<String> addressCode = projectDetailsResponse.getAddressCode();
        if (!CollectionUtils.isEmpty(addressCode)) {
            String addressName = addressService.getCountyNameByAddressCode(addressCode.get(addressCode.size() - 1));
            String weatherCode = addressService.getWeatherCodeByAddressCode(addressCode.get(addressCode.size() - 1));
            projectDetailsResponse.setAddressName(addressName);
            projectDetailsResponse.setWeatherCode(weatherCode);
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
        List<String> energySubSystem = projectDetailsResponse.getEnergySubSystem();
        projectDetailsResponse.setEnergySubSystemName(dictUtils.selectDictLabel(DictConstance.ENERGY_SUB_SYSTEM, energySubSystem));
        return Response.success(projectDetailsResponse);
    }

    @Override
    public Response<Void> editProject(ProjectEditRequest request) {
        projectService.update(request);
        return Response.success();
    }

    @Override
    public Response<ProjectDetailsResponse> getDetails(Long id) {
        ProjectDetailsResponse details = projectService.getDetails(id);
        return Response.success(details);
    }

    @Override
    public Response<Page<ProjectListResponse>> pageQuery(ProjectListRequest request) {
        Page<ProjectListResponse> projectListResponsePage = projectService.pageList(request);
        return Response.success(projectListResponsePage);
    }

    @Override
    public Response<Boolean> checkNameUnique(ProjectNameUniqueRequest request) {
        boolean unique = projectService.checkNameUnique(request);
        return Response.success(unique);
    }

    @Override
    public Response<Boolean> checkCodeUnique(ProjectCodeUniqueRequest request) {
        boolean unique = projectService.checkCodeUnique(request);
        return Response.success(unique);
    }

    @Override
    public Response<Map<String, String>> getProjectNames(List<String> bizProjectIds) {
        TenantContext.setIgnore(true);
        try {
            if (CollectionUtil.isEmpty(bizProjectIds)) {
                return Response.success(MapUtil.empty());
            }
            Map<String, String> map = projectMapper.selectList(new LambdaQueryWrapper<ProjectEntity>()
                            .in(ProjectEntity::getBizProjectId, bizProjectIds))
                    .stream()
                    .collect(Collectors.toMap(ProjectEntity::getBizProjectId, ProjectEntity::getName, (o1, o2) -> o1));
            return Response.success(map);
        } finally {
            TenantContext.setIgnore(false);
        }
    }

    @Override
    public Response<ProjectDirectorUserDTO> getDirectorUser(String bizProjectId) {
        TenantContext.setIgnore(true);
        try {
            ProjectEntity project = projectMapper.selectOne(new LambdaQueryWrapper<ProjectEntity>().eq(ProjectEntity::getBizProjectId, bizProjectId));
            if (null != project && null != project.getDirectorUserId()) {
                return Response.success(new ProjectDirectorUserDTO().setId(project.getDirectorUserId()));
            }
            return Response.success();
        } finally {
            TenantContext.setIgnore(false);
        }
    }

    @Override
    public Response<List<TenantProjectDTO>> getTenantProjects(Long tenantId) {
        TenantContext.setIgnore(true);
        try {
            List<TenantProjectDTO> result = projectMapper.selectList(new LambdaQueryWrapper<ProjectEntity>()
                            .eq(ProjectEntity::getTenantId, tenantId)
                            .select(ProjectEntity::getBizProjectId,
                                    ProjectEntity::getName,
                                    ProjectEntity::getBizType,
                                    ProjectEntity::getArea))
                    .stream()
                    .map(o -> {
                        TenantProjectDTO data = new TenantProjectDTO();
                        BeanUtil.copyProperties(o, data);
                        return data;
                    })
                    .collect(Collectors.toList());
            return Response.success(result);
        } finally {
            TenantContext.setIgnore(false);
        }
    }

    @Override
    public Response<List<ProjectCityDTO>> getProjectsCity(List<String> bizProjectIdList) {
        TenantContext.setIgnore(true);
        if (CollUtil.isEmpty(bizProjectIdList)) {
            return Response.success(Collections.emptyList());
        }
        return Response.success(projectMapper.getProjectsCity(bizProjectIdList));
    }

    @Override
    public Response<ProjectAreaResponse> getAreaProjectInfo(String nodeId) {
        ProjectAreaResponse result = projectMapper.getAreaProjectInfo(nodeId);
        return Response.success(result);
    }

    @Override
    public Response<List<ProjectAreaProjectsDetailResponse>> getAreaProjectIds(String nodeId) {
        TenantContext.setIgnore(true);
        ManagementNodeEntity selectNode = managementNodeMapper.selectOne(new LambdaQueryWrapper<ManagementNodeEntity>().eq(ManagementNodeEntity::getBizNodeId, nodeId));
        if (null == selectNode) {
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
        List<ProjectAreaProjectsDetailResponse> result = projectMapper.getAreaProjectDetails(bizProjectIds);
        return Response.success(result);
    }
}
