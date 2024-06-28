package com.landleaf.job.api;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.job.api.dto.JobLogSaveDTO;
import com.landleaf.job.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Yang
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 查询告警")
public interface JobLogApi {

    @PostMapping(ApiConstants.PREFIX + "/logger/save")
    @Operation(summary = "保存定时任务日志")
    Response<Void> saveLog(@Validated @RequestBody JobLogSaveDTO request);

}
