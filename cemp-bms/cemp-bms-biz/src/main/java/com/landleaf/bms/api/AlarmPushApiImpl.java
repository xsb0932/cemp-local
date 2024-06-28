package com.landleaf.bms.api;

import com.landleaf.bms.api.dto.AlarmPushRequest;
import com.landleaf.bms.service.impl.AlarmPushService;
import com.landleaf.comm.base.pojo.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlarmPushApiImpl implements AlarmPushApi {
    private final AlarmPushService alarmPushService;

    @Override
    public Response<Void> alarmPush(AlarmPushRequest request) {
        alarmPushService.alarmPush(request);
        return Response.success();
    }
}
