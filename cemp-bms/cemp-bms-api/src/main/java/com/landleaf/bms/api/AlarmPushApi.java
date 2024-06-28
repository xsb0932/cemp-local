package com.landleaf.bms.api;

import com.landleaf.bms.api.dto.AlarmPushRequest;
import com.landleaf.bms.api.enums.ApiConstants;
import com.landleaf.comm.base.pojo.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Feign 服务 - 告警推送")
@FeignClient(name = ApiConstants.NAME)
public interface AlarmPushApi {
    
    @PostMapping(ApiConstants.PREFIX + "/alarm-push")
    @Operation(summary = "告警推送")
    Response<Void> alarmPush(@RequestBody @Validated AlarmPushRequest request);
}
