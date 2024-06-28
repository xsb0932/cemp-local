package com.landleaf.energy.api;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.energy.enums.ApiConstants;
import com.landleaf.energy.response.DeviceElectricityResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 设备充电量Api
 *
 * @author yue lin
 * @since 2023/8/3 10:07
 */
@Tag(name = "Feign 服务 - 设备用电查询")
@FeignClient(name = ApiConstants.NAME)
public interface DeviceElectricityApi {

    @Operation(summary = "查询设备当日的用电量")
    @PostMapping(ApiConstants.PREFIX + "/device/electricity/total/day")
    Response<List<DeviceElectricityResponse>> searchChargingDayTotal(@RequestBody List<String> bizDeviceIds);

    @Operation(summary = "查询设备当年的用电量")
    @PostMapping(ApiConstants.PREFIX + "/device/electricity/tear")
    Response<List<DeviceElectricityResponse>> searchChargingYearTotal(@RequestBody List<String> bizDeviceIds);

}
