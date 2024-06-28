package com.landleaf.data.api.device;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.data.api.device.dto.*;
import com.landleaf.data.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;
import java.util.List;

import static com.landleaf.data.enums.ApiConstants.PREFIX;


@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 设备历史数据")
public interface DeviceHistoryApi {

    @PostMapping(PREFIX + "/sta/latest/data")
    @Operation(summary = "获取最近一条数据")
    Response<String> getLatestData(@RequestBody StaLatestDataRequest request);

    @PostMapping(PREFIX + "/device-history")
    @Operation(summary = "获取设备历史状态")
    Response<List<DeviceHistoryDTO>> getDeviceHistory(@RequestBody HistoryQueryInnerDTO queryDTO);

    @PostMapping(PREFIX + "/sta/electricity-device-data")
    @Operation(summary = "获取设备小时电量")
    Response<List<StaDeviceElectricityResponse>> getStaElectricityDeviceData(@RequestBody StaDeviceBaseRequest request);

    @PostMapping(PREFIX + "/sta/gas-device-data")
    @Operation(summary = "获取设备小时用气量")
    Response<List<StaDeviceGasResponse>> getStaGasDeviceData(@RequestBody StaDeviceBaseRequest request);

    @PostMapping(PREFIX + "/sta/water-device-data")
    @Operation(summary = "获取设备小时用水量")
    Response<List<StaDeviceWaterResponse>> getStaWaterDeviceData(@RequestBody StaDeviceBaseRequest request);

    @PostMapping(PREFIX + "/sta/air-device-data")
    @Operation(summary = "获取设备小时空调数据")
    Response<Collection<StaDeviceAirResponse>> getStaAirDeviceData(@RequestBody StaDeviceBaseRequest request);

    @PostMapping(PREFIX + "/sta/znb-device-data")
    @Operation(summary = "获取组串逆变器数据")
    Response<List<StaDeviceZnbResponse>> getStaZnbDeviceData(@RequestBody StaDeviceBaseRequest request);

    @PostMapping(PREFIX + "/sta/gscn-device-data")
    @Operation(summary = "获取储能系统数据")
    Response<List<StaDeviceGscnResponse>> getStaGscnDeviceData(@RequestBody StaDeviceBaseRequest request);

    @PostMapping(PREFIX + "/znb/current-p-day")
    @Operation(summary = "获取组串逆变器当日功率数据")
    Response<List<ZnbPResponse>> getZnbPResponse(@RequestBody BasePRequest request);

    @PostMapping(PREFIX + "/gscn/current-p-day")
    @Operation(summary = "获取储能系统当日功率数据")
    Response<List<GscnPResponse>> getGscnPResponse(@RequestBody BasePRequest request);

    @PostMapping(PREFIX + "/charge/current-p-day")
    @Operation(summary = "获取充电桩当日功率数据")
    Response<List<ChargePResponse>> getChargePResponse(@RequestBody BasePRequest request);

}
