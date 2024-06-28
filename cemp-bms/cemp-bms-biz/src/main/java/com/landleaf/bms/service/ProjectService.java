package com.landleaf.bms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.api.dto.*;
import com.landleaf.bms.domain.entity.ProjectEntity;
import com.landleaf.bms.domain.request.ProjectAddRequest;
import com.landleaf.bms.domain.request.ProjectTreeListRequest;
import com.landleaf.bms.domain.response.NodeProjectTreeResponse;
import com.landleaf.bms.domain.response.ScheduleProjectResponse;

import java.util.List;

/**
 * ProjectService
 *
 * @author 张力方
 * @since 2023/6/5
 **/
public interface ProjectService {
    /**
     * 获取当前用户项目树
     *
     * @return 项目树
     */
    NodeProjectTreeResponse getCurrentUserProjectTree();

    /**
     * 获取当前用户项目树(仅项目可选)
     *
     * @return 项目树
     */
    NodeProjectTreeResponse getCurrentUserProjectTree2();


    /**
     * 获取当前用户所有项目
     *
     * @return 项目集合
     */
    List<ProjectEntity> getCurrentUserProjectList();

    List<String> getUserProjectBizIds(Long userId);

    /**
     * 获取当前用户管理节点下项目列表
     *
     * @param request 管理节点业务id
     * @return 项目列表
     */
    List<ProjectListResponse> getProjectList(ProjectTreeListRequest request);

    /**
     * 新增项目
     *
     * @param request 新增项目请求
     */
    void add(ProjectAddRequest request);

    /**
     * 编辑项目
     *
     * @param request 编辑项目请求
     */
    void update(ProjectEditRequest request);

    /**
     * 删除项目
     *
     * @param projectId 项目id
     */
    void delete(Long projectId);

    /**
     * 分页列表查询项目
     *
     * @param request 查询条件
     */
    Page<ProjectListResponse> pageList(ProjectListRequest request);

    /**
     * 校验项目名称是否唯一
     *
     * @param request 项目名称
     * @return true 唯一
     */
    boolean checkNameUnique(ProjectNameUniqueRequest request);

    /**
     * 校验项目编码是否唯一
     *
     * @param request 项目编码
     * @return true 唯一
     */
    boolean checkCodeUnique(ProjectCodeUniqueRequest request);

    /**
     * 查询所有项目的行政区域code
     *
     * @return List<ProjectEntity>
     */
    List<String> listAllAddressCode();

    /**
     * 根据项目业务id查询项目信息
     *
     * @param bizProjectId 项目业务id
     * @return ProjectEntity
     */
    ProjectEntity selectByBizProjectId(String bizProjectId);

    NodeProjectTreeResponse getCurrentUserNodeTree();

    ProjectDetailsResponse getDetails(Long id);

    List<ScheduleProjectResponse> getScheduleProjectList(Long tenantId);

    List<ProjectEntity> getTenantProjectList();
}
