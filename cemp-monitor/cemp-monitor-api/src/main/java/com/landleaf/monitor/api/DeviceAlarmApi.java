package com.landleaf.monitor.api;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.monitor.dto.AlarmResponse;
import com.landleaf.monitor.dto.DeviceAlarmSummaryResponse;
import com.landleaf.monitor.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 给光子牌查看设备告警用
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 设备告警")
public interface DeviceAlarmApi {
    @PostMapping(ApiConstants.PREFIX + "/summary")
    @Operation(summary = "查询告警")
    Response<List<DeviceAlarmSummaryResponse>> query(@RequestBody List<String> bizDeviceIds);

    /**
     * 查询告警
     * @param bizDeviceIds
     * @param codePrefix 如果有，仅like查询指定code，否则，查询所有告警
     * @return
     */
    @PostMapping(ApiConstants.PREFIX + "/summary-by-code")
    @Operation(summary = "查询告警")
    Response<List<DeviceAlarmSummaryResponse>> query(@RequestBody List<String> bizDeviceIds,
                                                     @RequestParam List<String> codePrefix);
}
