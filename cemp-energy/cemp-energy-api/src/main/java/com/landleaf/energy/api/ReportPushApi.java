package com.landleaf.energy.api;

import com.landleaf.energy.api.dto.ProjectReportPushDTO;
import com.landleaf.energy.enums.ApiConstants;
import feign.Response;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Feign 服务 - 报表推送")
@FeignClient(name = ApiConstants.NAME)
public interface ReportPushApi {

    @PostMapping(ApiConstants.PREFIX + "/project/report-push-data")
    Response projectReportPushData(@RequestBody ProjectReportPushDTO request);
}
