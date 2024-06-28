package com.landleaf.bms.api;

import cn.hutool.core.bean.BeanUtil;
import com.landleaf.bms.api.dto.ProjectSpaceApiRequest;
import com.landleaf.bms.api.dto.ProjectSpaceTreeApiResponse;
import com.landleaf.bms.domain.entity.ProjectSpaceEntity;
import com.landleaf.bms.domain.request.ProjectSpaceRequest;
import com.landleaf.bms.domain.response.ProjectSpaceTreeResponse;
import com.landleaf.bms.service.ProjectSpaceService;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ProjectSpaceApiImpl
 *
 * @author 张力方
 * @since 2023/7/19
 **/
@RestController
@RequiredArgsConstructor
public class ProjectSpaceApiImpl implements ProjectSpaceApi {
    private final ProjectSpaceService projectSpaceService;

    @Override
    public Response<List<ProjectSpaceTreeApiResponse>> searchSpaces(Long projectId) {
        List<ProjectSpaceTreeResponse> projectSpaceTreeResponses = projectSpaceService.searchSpaces(projectId);
        List<ProjectSpaceTreeApiResponse> projectSpaceTreeApiResponses = BeanUtil.copyToList(projectSpaceTreeResponses, ProjectSpaceTreeApiResponse.class);
        return Response.success(projectSpaceTreeApiResponses);
    }

    @Override
    public Response<List<ProjectSpaceTreeApiResponse>> getPlaneSpaces(Long projectId, boolean isroot) {
        List<ProjectSpaceTreeResponse> projectSpaceTreeResponses = projectSpaceService.getPlaneSpaces(projectId, isroot);
        List<ProjectSpaceTreeApiResponse> projectSpaceTreeApiResponses = BeanUtil.copyToList(projectSpaceTreeResponses, ProjectSpaceTreeApiResponse.class);
        return Response.success(projectSpaceTreeApiResponses);
    }

    @Override
    public Response<Long> createSpace(ProjectSpaceApiRequest.Create request) {
        ProjectSpaceRequest.Create create = BeanUtil.copyProperties(request, ProjectSpaceRequest.Create.class);
        Long space = projectSpaceService.createSpace(create);
        return Response.success(space);
    }

    @Override
    public Response<Long> updateSpace(ProjectSpaceApiRequest.Update request) {
        ProjectSpaceRequest.Update update = BeanUtil.copyProperties(request, ProjectSpaceRequest.Update.class);
        Long space = projectSpaceService.updateSpace(update);
        return Response.success(space);
    }

    @Override
    public Response<Void> deleteSpace(Long spaceId) {
        projectSpaceService.deleteSpace(spaceId);
        return Response.success();
    }

    @Override
    public Response<Boolean> checkSpaceName(Long spaceId, Long projectId, String spaceName) {
        return Response.success(projectSpaceService.checkSpaceName(spaceId, projectId, spaceName));
    }

    @Override
    public Response<List<ProjectSpaceTreeApiResponse>> getByIds(List<String> ids) {
        List<ProjectSpaceEntity> result = projectSpaceService.getByIds(ids);
        List<ProjectSpaceTreeApiResponse> response = result.stream().map(space -> {
            ProjectSpaceTreeApiResponse res = new ProjectSpaceTreeApiResponse();
            res.setSpaceId(space.getId());
            res.setSpaceName(space.getName());
            return res;
        }).collect(Collectors.toList());
        return Response.success(response);
    }

    @Override
    public Response<ProjectSpaceTreeApiResponse> getById(Long id) {
        return projectSpaceService.getbyId(id);

    }

    @Override
    public Response<String> getSpaceNameById(Long id) {
        TenantContext.setIgnore(true);
        try {
            return Response.success(projectSpaceService.getSpaceNameById(id));
        } finally {
            TenantContext.setIgnore(false);
        }
    }
}
