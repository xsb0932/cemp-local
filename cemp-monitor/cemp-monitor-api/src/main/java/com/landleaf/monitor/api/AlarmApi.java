package com.landleaf.monitor.api;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.monitor.dto.AlarmAddRequest;
import com.landleaf.monitor.dto.AlarmResponse;
import com.landleaf.monitor.dto.CurrentAlarmResponse;
import com.landleaf.monitor.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 查询告警
 *
 * @author 张力方
 * @since 2023/8/22
 **/
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 查询告警")
public interface AlarmApi {

    @PostMapping(ApiConstants.PREFIX + "/query")
    @Operation(summary = "查询告警")
    Response<List<AlarmResponse>> query(@RequestBody List<String> deviceBizIds);

    @GetMapping(ApiConstants.PREFIX + "/projId")
    @Operation(summary = "修改告警数量")
    Response<Boolean> changeAlarmCount(@RequestParam("tenantId") Long tenantId, @RequestParam("bizProjId") String bizProjId);

    @PostMapping(ApiConstants.PREFIX + "/alarm/confirm/add")
    @Operation(summary = "添加告警确认")
    Response<Boolean> addAlarmConfirm(@RequestParam("tenantId") Long tenantId, @RequestParam("eventId") String eventId, @RequestParam("alarmAlarmType") String alarmAlarmType);

    @PostMapping(ApiConstants.PREFIX + "/alarm/current/add")
    @Operation(summary = "添加当前告警")
    Response<Boolean> addCurrentAlarm(@RequestBody AlarmAddRequest request);

    @PostMapping(ApiConstants.PREFIX + "/event/his/add")
    @Operation(summary = "添加历史事件")
    Response<Boolean> addHisEvent(@RequestBody AlarmAddRequest request);

    @DeleteMapping(ApiConstants.PREFIX + "/alarm/current/del")
    @Operation(summary = "删除当前故障")
    Response<Boolean> delCurrentAlarm(@RequestParam("tenantId") Long tenantId, @RequestParam("bizProjId") String bizProjId, @RequestParam("alarmId") Long alarmId);

    @DeleteMapping(ApiConstants.PREFIX + "/alarm/current/del-by-code")
    @Operation(summary = "根据alarmCode删除当前故障,并返回故障信息")
    Response<CurrentAlarmResponse> delCurrentAlarmByCode(@RequestParam("bizDeviceId") String bizDeviceId, @RequestParam("alarmCode") String alarmCode);

}
