package com.landleaf.data.api.device;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.data.api.device.dto.DeviceAlarmDTO;
import com.landleaf.data.api.device.dto.DeviceCurrentDTO;
import com.landleaf.data.api.device.dto.DeviceListCurrentDTO;
import com.landleaf.data.api.device.dto.DeviceListCurrentReqDTO;
import com.landleaf.data.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.landleaf.data.enums.ApiConstants.PREFIX;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 设备实时数据")
public interface DeviceCurrentApi {

    @GetMapping(PREFIX + "/device-current")
    @Operation(summary = "获取设备实时状态")
    @Parameter(name = "ids", description = "设备id", example = "D000000000001,D000000000002", required = true)
    Response<List<DeviceCurrentDTO>> getDeviceCurrent(@RequestParam("ids") Collection<String> ids);

    @GetMapping(PREFIX + "/device-current-by-id")
    @Operation(summary = "获取单个设备实时状态")
    Response<Map<String, Object>> getDeviceCurrentById(@RequestParam("bizDeviceId") String bizDeviceId);

    @PostMapping(PREFIX + "/device-attr-current")
    @Operation(summary = "获取设备-属性实时状态")
    Response<List<DeviceCurrentDTO>> getDeviceAttrCurrent(@RequestBody Map<String, List<String>> deviceAttrs);

    @PostMapping(PREFIX + "/device-list-current")
    @Operation(summary = "设备列表获取设备实时状态")
    Response<List<DeviceListCurrentDTO>> getDeviceCurrent(@Validated @RequestBody DeviceListCurrentReqDTO reqDTO);

    @PostMapping(PREFIX + "/device-cst-map")
    @Operation(summary = "设备列表获取设备通讯状态")
    Response<Map<String, Integer>> getDeviceCstMap(@RequestBody List<String> bizDeviceIds);

    @GetMapping(PREFIX + "/device-cst-status")
    @Operation(summary = "设备详情获取设备通讯状态")
    Response<Integer> getDeviceCstStatus(@RequestParam("bizDeviceId") String bizDeviceId);

    @PostMapping(PREFIX + "/device-alarm-by-ids")
    @Operation(summary = "设备详情获取设备通讯状态")
    Response<List<DeviceAlarmDTO>> getDeviceCurrentAlarm(@RequestBody List<String> bizDeviceIds);
}
