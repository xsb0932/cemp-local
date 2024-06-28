package com.landleaf.bms.api;

import com.landleaf.bms.api.dto.GatewayProjectResponse;
import com.landleaf.bms.api.enums.ApiConstants;
import com.landleaf.comm.base.pojo.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Yang
 */
@Tag(name = "Feign 服务 - 软网关")
@FeignClient(name = ApiConstants.NAME)
public interface GatewayApi {

    @Operation(summary = "获取网关项目信息")
    @GetMapping(ApiConstants.PREFIX + "/project-info")
    Response<GatewayProjectResponse> getProjectInfoByBizId(@RequestParam("bizId") String bizId);

    @Operation(summary = "获取网关编号")
    @GetMapping(ApiConstants.PREFIX + "/getBizIdByProd")
    Response<List<String>> findBizIdByProjAndProdId(@RequestParam("bizProjId") String bizProjId, @RequestParam("bizProdId") String bizProdId);
}
