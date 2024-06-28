package com.landleaf.jjgj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.landleaf.jjgj.domain.entity.JjgjReportPushEntity;
import com.landleaf.jjgj.domain.request.ReportPushConfigSaveRequest;
import com.landleaf.jjgj.domain.response.ReportPushConfigResponse;
import com.landleaf.job.api.dto.JobLogSaveDTO;
import com.landleaf.job.api.dto.JobRpcRequest;

/**
 * 锦江报表推送配置的业务逻辑接口定义
 *
 * @author hebin
 * @since 2023-11-21
 */
public interface JjgjReportPushService extends IService<JjgjReportPushEntity> {

    ReportPushConfigResponse projectConfig(String bizProjectId);

    void save(ReportPushConfigSaveRequest request);

    void reportPush(JobRpcRequest request, JobLogSaveDTO jobLog);
}