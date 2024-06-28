package com.landleaf.bms.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.api.dto.*;
import com.landleaf.bms.domain.entity.ProjectEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ProjectMapper
 *
 * @author 张力方
 * @since 2023/6/5
 **/
@Mapper
public interface ProjectMapper extends BaseMapper<ProjectEntity> {
    /**
     * 递归向下获取项目列表
     *
     * @param bizNodeId 管理节点业务id
     * @return 管理节点列表
     */
    List<ProjectListResponse> recursiveDownCurrentUserListByBizNodeId(@Param("bizNodeId") String bizNodeId, @Param("type") String type, @Param("userId") Long userId);

    /**
     * 递归向下获取项目业务ids
     *
     * @param nodeIds 管理节点ids
     * @return 项目业务ids
     */
    List<String> recursiveDownBizListByNodeIds(@Param("nodeIds") List<Long> nodeIds, @Param("type") String type);

    /**
     * 递归向下获取项目列表
     *
     * @param nodeIds 管理节点ids
     * @return 管理节点列表
     */
    List<ProjectListResponse> recursiveDownCurrentUserListByNodeId(@Param("nodeIds") List<Long> nodeIds, @Param("type") String type);

    /**
     * 获取项目业务ids -- 通过项目管理节点ids
     *
     * @param nodeIds 管理节点ids
     * @return 项目业务ids
     */
    List<String> getProjectBizIdsByProjectNodeIds(@Param("nodeIds") List<Long> nodeIds);

    List<UserProjectDTO> getProjectListByProjectNodeIds(@Param("nodeIds") List<Long> nodeIds);

    /**
     * 分页列表查询项目
     *
     * @param page          分页参数
     * @param request       请求参数
     * @param projectBizIds 用户有权限的项目id集合
     * @return 项目列表
     */
    Page<ProjectListResponse> selectPageList(@Param("page") Page<ProjectListResponse> page,
                                             @Param("request") ProjectListRequest request, @Param("projectBizIds") List<String> projectBizIds);

    /**
     * 查询项目详情
     *
     * @param bizProjectId 项目业务id
     * @return 项目详情
     */
    ProjectDetailsResponse selectProjectDetails(@Param("bizProjectId") String bizProjectId);

    List<UserProjectDTO> recursiveDownProjectListByNodeIds(@Param("nodeIds") List<Long> nodeIds, @Param("type") String type);

    List<ProjectCityDTO> getProjectsCity(@Param("bizProjectIdList") List<String> bizProjectIdList);

    @Select("select count(*) as projectNum, sum(area) as projectArea from tb_project t1 where t1.parent_biz_node_id = #{nodeId} ")
    ProjectAreaResponse getAreaProjectInfo(@Param("nodeId") String nodeId);

    //    @Select("select distinct biz_project_id as project_id , area as project_area ,name as project_name,null as consumption from tb_project  where parent_biz_node_id = #{nodeId}")
    List<ProjectAreaProjectsDetailResponse> getAreaProjectDetails(@Param("bizProjectIds") List<String> bizProjectIds);
}
