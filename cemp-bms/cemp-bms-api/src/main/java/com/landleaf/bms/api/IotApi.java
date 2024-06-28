package com.landleaf.bms.api;

import com.landleaf.bms.api.enums.ApiConstants;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.monitor.dto.DeviceMonitorVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 监测平台")
public interface IotApi {

    /**
     * 物联平台设备同步 - 修改
     *
     */
    @PostMapping(ApiConstants.PREFIX + "/edit")
    @Operation(summary = "物联平台设备同步-修改")
    Response<Void> edit( @RequestBody DeviceMonitorVO deviceMonitorVO);

}
