package com.landleaf.monitor.controller;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.monitor.domain.request.AlarmConfirmRequest;
import com.landleaf.monitor.domain.response.AlarmTypeNumResponse;
import com.landleaf.monitor.service.AlarmConfirmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 告警确认相关接口
 *
 * @author 张力方
 * @since 2023/8/14
 **/
@RestController
@AllArgsConstructor
@RequestMapping("/alarm/confirm")
@Tag(name = "告警确认相关接口", description = "告警确认相关接口")
public class AlarmConfirmController {

    private final AlarmConfirmService alarmConfirmService;

    /**
     * 确认告警
     *
     * @param request 请求参数
     */
    @PutMapping
    @Operation(summary = "确认告警", description = "确认告警")
    public Response<List<AlarmTypeNumResponse>> confirm(@RequestBody AlarmConfirmRequest request) {
        alarmConfirmService.confirm(request);
        return Response.success();
    }

}
