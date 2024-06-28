package com.landleaf.messaging.api;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.messaging.api.dto.SendServiceRequest;
import com.landleaf.messaging.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 服务控制")
public interface ServiceControlApi {

    @PostMapping(ApiConstants.PREFIX + "/send-service")
    @Operation(summary = "下发服务")
    Response<Boolean> sendService(@RequestBody SendServiceRequest request);
}
