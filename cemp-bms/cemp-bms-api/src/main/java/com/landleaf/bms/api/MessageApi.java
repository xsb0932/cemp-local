package com.landleaf.bms.api;

import com.landleaf.bms.api.dto.MessageAddRequest;
import com.landleaf.bms.api.enums.ApiConstants;
import com.landleaf.comm.base.pojo.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 消息")
public interface MessageApi {
    /**
     * 新增消息
     *
     * @param addInfo
     * @return
     */
    @PostMapping(ApiConstants.PREFIX + "/msg/save")
    @Operation(summary = "新增消息")
    Response<MessageAddRequest> save(@RequestBody MessageAddRequest addInfo);
}
