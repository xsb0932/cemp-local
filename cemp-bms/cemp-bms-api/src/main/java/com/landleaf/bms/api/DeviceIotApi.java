package com.landleaf.bms.api;

import com.landleaf.bms.api.dto.DeviceConnResponse;
import com.landleaf.bms.api.dto.DeviceIoResponse;
import com.landleaf.bms.api.dto.ProductDeviceServiceListResponse;
import com.landleaf.bms.api.enums.ApiConstants;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.monitor.dto.DeviceMonitorVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 物联设备 api
 *
 * @author xushibai
 * @since 2023/7/27
 **/
@Tag(name = "Feign 服务 - 用户项目")
@FeignClient(name = ApiConstants.NAME)
public interface DeviceIotApi {

    /**
     * 物联设备修改
     *
     * @param deviceMonitorVO 设备VO
     * @return 结果集
     */
    @Operation(summary = "物联平台设备同步")
    @PostMapping(ApiConstants.PREFIX + "/iot/device/edit")
    Response<Void> edit(@RequestBody DeviceMonitorVO deviceMonitorVO);

    /**
     * 获取设备的项目、品类、产品等信息（0项目ID，1品类ID，2产品ID）
     *
     * @param bizDeviceId 设备业务ID
     * @return 结果集
     */
    @Operation(summary = "物联平台设备同步")
    @GetMapping(ApiConstants.PREFIX + "/iot/device/information")
    Response<List<String>> information(@RequestParam("bizDeviceId") String bizDeviceId);

    /**
     * 根据设备ID查询设备信息
     *
     * @param bizDeviceIds 设备业务ID
     * @return 结果
     */
    @Operation(summary = "根据设备ID查询设备信息")
    @PostMapping(ApiConstants.PREFIX + "/iot/devices")
    Response<List<DeviceIoResponse>> searchDeviceIot(@RequestBody List<String> bizDeviceIds);

    /**
     * 根据outerId，查询bizDeviceId
     *
     * @param gateId      网关编号
     * @param pkId        产品编号
     * @param sourceDevId 外部设备编号
     * @return 结果
     */
    @Operation(summary = "根据设备ID查询设备信息")
    @PostMapping(ApiConstants.PREFIX + "/iot/device-by-outer")
    Response<DeviceConnResponse> queryBizDeviceIdByOuterId(@RequestParam("gateId") String gateId, @RequestParam("pkId") String pkId, @RequestParam("sourceDevId") String sourceDevId);

    /**
     * 修改设备parameter
     */
    @PostMapping(ApiConstants.PREFIX + "/iot/device/parameter/change")
    @Operation(summary = "修改设备parameter")
    Response<Boolean>  updateDeviceParameterVal(@RequestParam("tenantId")Long tenantId, @RequestParam("projectBizId")String projectBizId, @RequestParam("bizProdId")String bizProdId, @RequestParam("bizDeviceId")String bizDeviceId, @RequestBody Map<String, String> valMap);

}
