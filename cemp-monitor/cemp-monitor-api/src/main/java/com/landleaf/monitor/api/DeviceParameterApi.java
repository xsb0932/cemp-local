package com.landleaf.monitor.api;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.monitor.api.dto.MeterDeviceDTO;
import com.landleaf.monitor.api.request.MeterDeviceRequest;
import com.landleaf.monitor.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 查询设备参数的值
 *
 * @author Tycoon
 * @since 2023/8/18 10:15
 **/
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 查询设备参数的值")
public interface DeviceParameterApi {

    /**
     * 查询设备参数的值
     *
     * @param bizDeviceId 设备业务ID
     * @param identifier  唯一标识符
     * @return 结果
     */
    @GetMapping(ApiConstants.PREFIX + "/device-parameter/value")
    @Operation(summary = "查询设备参数的值")
    Response<String> searchDeviceParameterValue(@RequestParam("bizDeviceId") String bizDeviceId,
                                                @RequestParam("identifier") String identifier);

    @PostMapping(ApiConstants.PREFIX + "/get-devices-multiplying-factor")
    @Operation(summary = "批量获取电表倍率")
    Response<Map<String, BigDecimal>> getDevicesMultiplyingFactor(@RequestBody List<String> bizDeviceIdList);

    /**
     * 查询手动(日月)/自动（日月）电表
     *
     * @param tenantId       租户ID
     * @param meterRead      02手动或01自动
     * @param meterReadCycle 1日或2月
     * @param userId         用户ID
     * @return 结果
     */
    @GetMapping(ApiConstants.PREFIX + "/device-parameter/biz-ids")
    @Operation(summary = "查询设备参数的值")
    Response<List<String>> searchDeviceBizIds(@RequestHeader("X-Tenant-Id") Long tenantId,
                                              @RequestParam("meterRead") String meterRead,
                                              @RequestParam("meterReadCycle") String meterReadCycle,
                                              @RequestParam("userId") Long userId);

    @PostMapping(ApiConstants.PREFIX + "/get-device-by-project-category-parameter")
    @Operation(summary = "根据项目id、品类和设备参数获取抄表设备")
    Response<List<MeterDeviceDTO>> getDeviceByProjectCategoryParameter(@RequestBody MeterDeviceRequest request);
}
