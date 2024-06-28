package com.landleaf.monitor.api;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.monitor.api.dto.DeviceManagerEventHistoryDTO;
import com.landleaf.monitor.api.dto.DeviceManagerEventHistoryPageDTO;
import com.landleaf.monitor.api.dto.DeviceServiceEventAddDTO;
import com.landleaf.monitor.api.request.DeviceManagerEventExportRequest;
import com.landleaf.monitor.api.request.DeviceManagerEventPageRequest;
import com.landleaf.monitor.api.request.DeviceServiceEventAddRequest;
import com.landleaf.monitor.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 历史事件")
public interface HistoryEventApi {

    @PostMapping(ApiConstants.PREFIX + "/device-events/history")
    @Operation(summary = "历史事件", description = "历史事件")
    Response<DeviceManagerEventHistoryPageDTO> deviceEventsHistory(@Validated @RequestBody DeviceManagerEventPageRequest request);

    @PostMapping(ApiConstants.PREFIX + "/device-events/history/export")
    @Operation(summary = "历史事件", description = "历史事件")
    Response<List<DeviceManagerEventHistoryDTO>> deviceEventsHistoryExport(@Validated @RequestBody DeviceManagerEventExportRequest request);

    @PostMapping(ApiConstants.PREFIX + "/device-events/service-event/add")
    @Operation(summary = "新增设备服务控制历史事件", description = "新增设备服务控制历史事件")
    Response<DeviceServiceEventAddDTO> addDeviceServiceEvent(@Validated @RequestBody DeviceServiceEventAddRequest request);
}
