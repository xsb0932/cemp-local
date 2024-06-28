package com.landleaf.bms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.landleaf.bms.domain.request.*;
import com.landleaf.bms.domain.response.DictDataSelectiveResponse;
import com.landleaf.bms.domain.response.DictDetailsResponse;
import com.landleaf.bms.domain.response.DictTypeListResponse;
import com.landleaf.bms.domain.response.OperateLogResponse;
import com.landleaf.bms.service.DictService;
import com.landleaf.bms.service.OperateLogService;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 操作日志查询
 *
 * @author xushibai
 * @since 2024/4/25
 **/
@RequiredArgsConstructor
@RestController
@RequestMapping("/log/opr")
@Tag(name = "操作日志查询接口")
public class LogOprController {

    private final OperateLogService operateLogService;

    @PostMapping("/list")
    @Operation(summary = "查询日志")
    public Response<IPage<OperateLogResponse>> searchUsers(@RequestBody OperateLogQueryRequest request) {
        TenantContext.setIgnore(true);
        return Response.success(operateLogService.list(request));
    }


}
