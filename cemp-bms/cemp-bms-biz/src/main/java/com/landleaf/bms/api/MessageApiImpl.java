package com.landleaf.bms.api;

import com.landleaf.bms.api.dto.MessageAddRequest;
import com.landleaf.bms.service.MessageService;
import com.landleaf.comm.base.pojo.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageApiImpl implements MessageApi {

    private final MessageService messageService;

    @Override
    public Response<MessageAddRequest> save(MessageAddRequest addInfo) {
        return Response.success(messageService.save(addInfo));
    }
}
