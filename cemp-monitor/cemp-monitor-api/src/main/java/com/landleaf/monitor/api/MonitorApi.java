package com.landleaf.monitor.api;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.monitor.dto.AlarmResponse;
import com.landleaf.monitor.dto.DeviceMonitorVO;
import com.landleaf.monitor.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 监测平台")
public interface MonitorApi {
    /**
     * 根据bizDeviceIdList,获取device列表信息
     */
    @PostMapping(ApiConstants.PREFIX + "/device/list")
    @Operation(summary = "根据bizDeviceIdList,获取device列表信息")
    Response<List<DeviceMonitorVO>> getDeviceListByBizIds(@Validated @RequestBody List<String> bizDeviceIdList);

    /**
     * 监控平台设备同步- 新增
     */
    @PostMapping(ApiConstants.PREFIX + "/add")
    @Operation(summary = "监控平台设备同步")
    Response<Void> add(@RequestBody DeviceMonitorVO deviceMonitorVO);

    /**
     * 监控平台设备同步 - 修改
     */
    @PostMapping(ApiConstants.PREFIX + "/edit")
    @Operation(summary = "监控平台设备同步-修改")
    Response<Void> edit(@RequestBody DeviceMonitorVO deviceMonitorVO);

    /**
     * 监控平台设备同步 - 修改
     */
    @PostMapping(ApiConstants.PREFIX + "/delete")
    @Operation(summary = "监控平台设备同步-删除")
    Response<Void> delete(@RequestParam("bizDeviceId") String bizDeviceId);

    /**
     * 监控平台设备同步 - 修改
     */
    @GetMapping(ApiConstants.PREFIX + "/getdevice/{outid}")
    @Operation(summary = "根据外部id查询")
    Response<String> getdevice(@PathVariable("outid") String outid);

    /**
     * 查询设备当前模式
     */
    @GetMapping(ApiConstants.PREFIX + "/device/mode")
    @Operation(summary = "查询设备当前模式")
    Response<String> getMode(@RequestParam("bizDeviceId") String bizDeviceId);

    /**
     * 查询设备当前模式
     */
    @GetMapping(ApiConstants.PREFIX + "/device/runningStatus")
    @Operation(summary = "查询运行状态")
    Response<String> getRunningStatus(@RequestParam("bizDeviceId") String bizDeviceId);

    /**
     * 查询告警
     */
    @GetMapping(ApiConstants.PREFIX + "/device/alarms")
    @Operation(summary = "查询告警")
    Response<List<AlarmResponse>> getAlarms();

    /**
     * 发送状态变更通知
     */
    @GetMapping(ApiConstants.PREFIX + "/device/status/notice")
    @Operation(summary = "查询告警")
    Response<Boolean> changeStatusNotice(@RequestParam("bizDeviceId") String bizDeviceId, @RequestParam("bizProdId") String bizProdId, @RequestParam("key") String key, @RequestParam("val") Object val, @RequestParam("time") Long time);

    /**
     * 发送状态变更通知
     */
    @GetMapping(ApiConstants.PREFIX + "/device/all/status/notice")
    @Operation(summary = "查询告警")
    Response<Boolean>  changeAllStatusNotice(@RequestParam("bizDeviceId") String bizDeviceId, @RequestParam("bizProdId") String bizProdId, @RequestParam("time") long time, @RequestParam Map<String, Object> valMap);
}
