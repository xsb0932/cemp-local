package com.landleaf.bms.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.bms.api.dto.ProjectSpaceTreeApiResponse;
import com.landleaf.bms.dal.mapper.DeviceIotMapper;
import com.landleaf.bms.dal.mapper.ProjectMapper;
import com.landleaf.bms.dal.mapper.ProjectSpaceMapper;
import com.landleaf.bms.domain.entity.ProjectSpaceEntity;
import com.landleaf.bms.domain.request.ProjectSpaceRequest;
import com.landleaf.bms.domain.response.ProjectSpaceTreeResponse;
import com.landleaf.bms.service.ProjectSpaceService;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.pgsql.core.BizSequenceService;
import com.landleaf.pgsql.enums.BizSequenceEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.landleaf.bms.domain.enums.ErrorCodeConstants.*;
import static java.util.stream.Collectors.toMap;

/**
 * 项目-空间管理业务实现
 *
 * @author yue lin
 * @since 2023/7/12 16:10
 */
@Service
@RequiredArgsConstructor
public class ProjectSpaceServiceImpl implements ProjectSpaceService {

    private final ProjectSpaceMapper projectSpaceMapper;
    private final ProjectMapper projectMapper;
    private final BizSequenceService bizSequenceService;
    private final DeviceIotMapper deviceIotMapper;


    @Override
    public List<ProjectSpaceTreeResponse> getPlaneSpaces(Long projectId, boolean isroot) {
        Assert.notNull(projectId, "参数异常");
        Assert.notNull(projectMapper.selectById(projectId), () -> new ServiceException(PROJECT_NOT_EXIST));
        LambdaQueryWrapper<ProjectSpaceEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ProjectSpaceEntity::getProjectId, projectId);
        if (isroot) {
            lqw.eq(ProjectSpaceEntity::getParentId, "0");
        } else {
            lqw.ne(ProjectSpaceEntity::getParentId, "0");
        }
        List<ProjectSpaceEntity> projectSpaceEntities = projectSpaceMapper.selectList(lqw);
        return projectSpaceEntities.stream().map(space -> {
            ProjectSpaceTreeResponse response = new ProjectSpaceTreeResponse();
            response.setSpaceId(space.getId());
            response.setSpaceName(space.getName());
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ProjectSpaceTreeResponse> searchSpaces(Long projectId) {
        Assert.notNull(projectId, "参数异常");
        Assert.notNull(projectMapper.selectById(projectId), () -> new ServiceException(PROJECT_NOT_EXIST));
        List<ProjectSpaceEntity> projectSpaceEntities = projectSpaceMapper.selectSpacesByProject(projectId);
        Map<Long, String> idNameMap = projectSpaceEntities.stream().collect(toMap(ProjectSpaceEntity::getId, ProjectSpaceEntity::getName));
        List<ProjectSpaceTreeResponse> treeResponses = projectSpaceEntities.stream()
                .map(it -> ProjectSpaceTreeResponse.from(it, idNameMap.getOrDefault(it.getParentId(), "")))
                .toList();
        return treeResponses.stream()
                .filter(it -> it.getParentId() == 0L)
                .peek(it -> it.setChildren(getChildren(it, treeResponses)))
                .toList();
    }

    @Override
    public Long createSpace(ProjectSpaceRequest.Create request) {
        Assert.notNull(projectMapper.selectById(request.getProjectId()),
                () -> new ServiceException(PROJECT_NOT_EXIST));
        Assert.isTrue(checkSpaceName(null, request.getProjectId(), request.getSpaceName()),
                () -> new ServiceException(PROJECT_SPACE_NAME_NOT_UNIQUE));
        ProjectSpaceEntity entity = request.toEntity();
        entity.setBizId(bizSequenceService.next(BizSequenceEnum.AREA));
        projectSpaceMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public Long updateSpace(ProjectSpaceRequest.Update request) {
        Assert.notNull(projectMapper.selectById(request.getProjectId()),
                () -> new ServiceException(PROJECT_NOT_EXIST));
        Assert.isTrue(checkSpaceName(request.getSpaceId(), request.getProjectId(), request.getSpaceName()),
                () -> new ServiceException(PROJECT_SPACE_NAME_NOT_UNIQUE));
        ProjectSpaceEntity entity = request.toEntity();
        projectSpaceMapper.updateById(entity);
        return entity.getId();
    }

    @Override
    public void deleteSpace(Long spaceId) {
        Assert.notNull(spaceId, "参数异常");
        ProjectSpaceEntity entity = projectSpaceMapper.selectById(spaceId);
        Assert.notNull(entity, () -> new ServiceException(PROJECT_SPACE_NOT_EXIST));
        Assert.isFalse(entity.getParentId() == 0L, () -> new ServiceException(PROJECT_SPACE_NOT_DELETE));
        Assert.isFalse(projectSpaceMapper.existsChildren(spaceId), () -> new ServiceException(PROJECT_SPACE_NOT_DELETE));
        // 判断区域下是否有设备
        Assert.isFalse(deviceIotMapper.existsSpace(spaceId), () -> new ServiceException(PROJECT_SPACE_NOT_DELETE));
        projectSpaceMapper.deleteById(spaceId);
    }

    @Override
    public Boolean checkSpaceName(Long spaceId, Long projectId, String spaceName) {
        return !projectSpaceMapper.exists(Wrappers.<ProjectSpaceEntity>lambdaQuery()
                .eq(ProjectSpaceEntity::getName, spaceName)
                .eq(ProjectSpaceEntity::getProjectId, projectId)
                .ne(Objects.nonNull(spaceId), ProjectSpaceEntity::getId, spaceId)
        );
    }

    @Override
    public List<ProjectSpaceEntity> getByIds(List<String> ids) {
        return projectSpaceMapper.selectBatchIds(ids);
    }

    @Override
    public Response<ProjectSpaceTreeApiResponse> getbyId(Long id) {
        ProjectSpaceEntity space = projectSpaceMapper.selectById(id);
        ProjectSpaceTreeApiResponse response = new ProjectSpaceTreeApiResponse();
        response.setSpaceId(space.getId());
        response.setSpaceName(space.getName());
        response.setBizId(space.getBizId());
        return Response.success(response);
    }

    @Override
    public String getSpaceNameById(Long id) {
        ProjectSpaceEntity space = projectSpaceMapper.selectById(id);
        return null == space ? "" : space.getName();
    }

    private List<ProjectSpaceTreeResponse> getChildren(ProjectSpaceTreeResponse response, List<ProjectSpaceTreeResponse> responses) {
        List<ProjectSpaceTreeResponse> treeResponses = responses.stream()
                .filter(it -> Objects.equals(it.getParentId(), response.getSpaceId()))
                .peek(it -> it.setChildren(getChildren(it, responses)))
                .toList();
        return CollUtil.isNotEmpty(treeResponses) ? treeResponses : null;
    }

}
