package com.landleaf.bms.api;

import com.landleaf.bms.api.dto.GatewayProjectResponse;
import com.landleaf.bms.service.GatewayService;
import com.landleaf.comm.base.pojo.Response;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * Feign 服务 - 网关
 *
 * @author 张力方
 * @since 2023/6/16
 **/
@RestController
@RequiredArgsConstructor
public class GatewayApiImpl implements GatewayApi {

    @Resource
    private GatewayService gatewayService;

    @Override
    public Response<GatewayProjectResponse> getProjectInfoByBizId(String bizId) {
        return Response.success(gatewayService.getProjectInfoByBizId(bizId));
    }

    @Override
    public Response<List<String>> findBizIdByProjAndProdId(String bizProjId, String bizProdId) {
        return Response.success(gatewayService.findBizIdByProjAndProdId(bizProjId, bizProdId));
    }
}
