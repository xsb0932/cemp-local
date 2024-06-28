package com.landleaf.monitor.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.monitor.domain.entity.ViewEntity;
import com.landleaf.monitor.domain.response.ViewInfoResponse;
import com.landleaf.monitor.domain.response.ViewPageResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 视图的数据库操作句柄
 *
 * @author hebin
 * @since 2023-06-20
 */
public interface ViewMapper extends BaseMapper<ViewEntity> {

    IPage<ViewPageResponse> selectPage(@Param("page") Page<ViewPageResponse> page, @Param("projectName") String projectName, @Param("viewName") String viewName, @Param("bizProjectIds") List<String> bizProjectIds);

    ViewInfoResponse selectInfo(@Param("viewId") Long viewId);

    int changeStatus(@Param("viewId") Long viewId, @Param("userId") Long userId);
}