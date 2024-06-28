package com.landleaf.bms.service;

import com.landleaf.bms.api.dto.ProjectSpaceTreeApiResponse;
import com.landleaf.bms.domain.entity.ProjectSpaceEntity;
import com.landleaf.bms.domain.request.ProjectSpaceRequest;
import com.landleaf.bms.domain.response.ProjectSpaceTreeResponse;
import com.landleaf.comm.base.pojo.Response;

import java.util.List;

/**
 * 空间管理业务层
 *
 * @author yue lin
 * @since 2023/7/12 16:09
 */
public interface ProjectSpaceService {

    /**
     * 查询项目下的区域树状结构数据
     *
     * @param projectId 项目ID
     * @return 结果集
     */
    List<ProjectSpaceTreeResponse> searchSpaces(Long projectId);

    /**
     * 查询项目下的区域平铺结构数据
     *
     * @param projectId 项目ID
     * @return 结果集
     */
    List<ProjectSpaceTreeResponse> getPlaneSpaces(Long projectId, boolean isroot);

    /**
     * 创建区域
     *
     * @param request 参数
     * @return 结果集
     */
    Long createSpace(ProjectSpaceRequest.Create request);

    /**
     * 更新区域
     *
     * @param request 参数
     * @return 结果集
     */
    Long updateSpace(ProjectSpaceRequest.Update request);

    /**
     * 删除区域
     *
     * @param spaceId 区域ID
     */
    void deleteSpace(Long spaceId);

    /**
     * 验证区域名称是否可用（true可用false不可）
     *
     * @param spaceId   区域ID
     * @param projectId 区域ID
     * @param spaceName 名称
     * @return 结果集
     */
    Boolean checkSpaceName(Long spaceId, Long projectId, String spaceName);

    List<ProjectSpaceEntity> getByIds(List<String> ids);

    Response<ProjectSpaceTreeApiResponse> getbyId(Long id);

    String getSpaceNameById(Long id);
}
