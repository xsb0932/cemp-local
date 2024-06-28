package com.landleaf.jjgj.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.landleaf.jjgj.domain.entity.JjgjReportPushEntity;
import com.landleaf.jjgj.domain.response.ReportPushConfigResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 锦江报表推送配置的数据库操作句柄
 *
 * @author hebin
 * @since 2023-11-21
 */
public interface JjgjReportPushMapper extends BaseMapper<JjgjReportPushEntity> {

    ReportPushConfigResponse projectConfig(@Param("bizProjectId") String bizProjectId);

    List<JjgjReportPushEntity> selectAll();
}