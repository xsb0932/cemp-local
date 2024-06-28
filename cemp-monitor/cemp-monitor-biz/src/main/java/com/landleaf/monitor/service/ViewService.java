package com.landleaf.monitor.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.landleaf.monitor.domain.entity.ViewEntity;
import com.landleaf.monitor.domain.request.ViewDuplicateRequest;
import com.landleaf.monitor.domain.request.ViewPageRequest;
import com.landleaf.monitor.domain.request.ViewSaveRequest;
import com.landleaf.monitor.domain.request.ViewUpdateRequest;
import com.landleaf.monitor.domain.response.ViewInfoResponse;
import com.landleaf.monitor.domain.response.ViewPageResponse;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 视图的业务逻辑接口定义
 *
 * @author hebin
 * @since 2023-06-20
 */
public interface ViewService extends IService<ViewEntity> {

    /**
     * 获取项目监测-视图tab
     *
     * @param bizProjectIds 项目ids
     * @return List<ViewEntity>
     */
    List<ViewEntity> tab(List<String> bizProjectIds);

    /**
     * 分页查询视图列表
     *
     * @param request 请求参数
     * @return IPage<ViewPageResponse>
     */
    IPage<ViewPageResponse> selectPage(ViewPageRequest request);

    /**
     * 新增视图
     *
     * @param request 请求参数
     */
    void save(ViewSaveRequest request);

    /**
     * 修改视图
     *
     * @param request 请求参数
     */
    void update(@Valid ViewUpdateRequest request);

    /**
     * 复制视图
     *
     * @param request 请求参数
     */
    void duplicate(ViewDuplicateRequest request);

    /**
     * 获取视图详情
     *
     * @param viewId 视图id
     * @return ViewInfoResponse
     */
    ViewInfoResponse info(Long viewId);

    /**
     * 变更发布状态
     *
     * @param viewId 视图id
     */
    void changeStatus(Long viewId);

    /**
     * 删除视图配置
     *
     * @param viewId
     */
    void delete(Long viewId);

    String getBizProjectIdByAVueViewId(String viewId);
}