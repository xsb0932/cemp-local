package com.landleaf.bms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.entity.ProductEntity;
import com.landleaf.bms.domain.enums.GatewayProtocolTypeEnum;
import com.landleaf.bms.domain.request.*;
import com.landleaf.bms.domain.response.*;
import com.landleaf.bms.service.GatewayService;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.operatelog.core.annotations.OperateLog;
import com.landleaf.operatelog.core.enums.ModuleTypeEnums;
import com.landleaf.operatelog.core.enums.OperateTypeEnum;
import com.landleaf.redis.RedisUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.landleaf.redis.constance.KeyConstance.GW_LOCK_KEY;

/**
 * 网关管理相关接口
 *
 * @author 张力方
 * @since 2023/8/16
 **/
@RequiredArgsConstructor
@RestController
@RequestMapping("/gateway")
@Tag(name = "网关管理相关接口")
@Slf4j
public class GatewayController {
    private final GatewayService gatewayService;
    private final RedisUtils redisUtils;

    @PostMapping
    @Operation(summary = "新增网关")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "新增网关", type = OperateTypeEnum.CREATE)
    public Response<Void> addGateway(@Validated @RequestBody GatewayAddRequest request) {
        if (GatewayProtocolTypeEnum.MQTT.getCode().equals(request.getProtocolType())) {
            gatewayService.addMqttGateway(request);
        }
        return Response.success();
    }

    @PutMapping
    @Operation(summary = "编辑网关")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "编辑网关", type = OperateTypeEnum.UPDATE)
    public Response<Void> editGateway(@Validated @RequestBody GatewayEditRequest request) {
        if (GatewayProtocolTypeEnum.MQTT.getCode().equals(request.getProtocolType())) {
            gatewayService.editMqttGateway(request);
        }
        return Response.success();
    }

    @DeleteMapping
    @Operation(summary = "删除网关")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "删除网关", type = OperateTypeEnum.DELETE)
    public Response<Void> deleteGateway(@RequestParam("bizId") String bizId) {
        gatewayService.deleteGateway(bizId);
        return Response.success();
    }

    @GetMapping("/{bizId}")
    @Operation(summary = "查询网关详情")
    public Response<GatewayDetailsResponse> getDetails(@PathVariable("bizId") String bizId) {
        GatewayDetailsResponse details = gatewayService.getDetails(bizId);
        return Response.success(details);
    }

    @GetMapping("/page-list")
    @Operation(summary = "分页查询网关列表")
    public Response<Page<GatewayListResponse>> pageQuery(GatewayListRequest request) {
        Page<GatewayListResponse> gatewayListResponsePage = gatewayService.pageQuery(request);
        return Response.success(gatewayListResponsePage);
    }

    @GetMapping("/check-js/{bizId}")
    @Operation(summary = "启动前校验JS初始化")
    public Response<Boolean> checkJsBeforeStart(@PathVariable("bizId") String bizId) {
        Boolean result = gatewayService.checkJsBeforeStart(bizId);
        return Response.success(result);
    }

    @GetMapping("/products")
    @Operation(summary = "获取网关可选产品")
    public Response<List<ProductEntity>> gatewayProducts() {
        List<ProductEntity> result = gatewayService.gatewayProducts();
        return Response.success(result);
    }

    @GetMapping("/{bizId}/js/up")
    @Operation(summary = "获取网关上行js")
    public Response<String> getGatewayUpJs(@PathVariable("bizId") String bizId) {
        String js = gatewayService.getGatewayUpJs(bizId);
        return Response.success(js);
    }

    @GetMapping("/{bizId}/js/down")
    @Operation(summary = "获取网关下行js")
    public Response<String> getGatewayDownJs(@PathVariable("bizId") String bizId) {
        String js = gatewayService.getGatewayDownJs(bizId);
        return Response.success(js);
    }

    @PostMapping("/{bizId}/js/up")
    @Operation(summary = "保存网关上行js")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "修改网关脚本", type = OperateTypeEnum.UPDATE)
    public Response<Void> saveGatewayUpJs(@PathVariable("bizId") String bizId, @RequestBody @Validated GatewayJsRequest upJs) {
        gatewayService.saveGatewayUpJs(bizId, upJs.getFunc());
        return Response.success();
    }

    @PostMapping("/{bizId}/js/down")
    @Operation(summary = "保存网关下行js")
    public Response<Void> saveGatewayDownJs(@PathVariable("bizId") String bizId, @RequestBody @Validated GatewayJsRequest downJs) {
        gatewayService.saveGatewayDownJs(bizId, downJs.getFunc());
        return Response.success();
    }

    @PostMapping("/{bizId}/js/up/simulate")
    @Operation(summary = "模拟运行网关上行js")
    public Response<SimulateJsResponse> simulateGatewayUpJs(@PathVariable("bizId") String bizId, @Validated @RequestBody SimulateUpJsRequest request) {
        SimulateJsResponse result = gatewayService.simulateGatewayUpJs(bizId, request.getTopic(), request.getPayload());
        return Response.success(result);
    }

    @PostMapping("/{bizId}/js/down/simulate")
    @Operation(summary = "模拟运行网关下行js")
    public Response<SimulateJsResponse> simulateGatewayDownJs(@PathVariable("bizId") String bizId, @Validated @RequestBody SimulateDownJsRequest request) {
        SimulateJsResponse result = gatewayService.simulateGatewayDownJs(bizId, request.getCmd());
        return Response.success(result);
    }

    @GetMapping("/{bizId}/js/up/format")
    @Operation(summary = "网关产品上行js参考")
    public Response<List<ProductUpPayloadResponse>> formatGatewayUpJs(@PathVariable("bizId") String bizId) {
        List<ProductUpPayloadResponse> result = gatewayService.formatGatewayUpJs(bizId);
        return Response.success(result);
    }

    @GetMapping("/{bizId}/js/down/format")
    @Operation(summary = "网关产品下行js参考")
    public Response<List<ProductDownPayloadResponse>> formatGatewayDownJs(@PathVariable("bizId") String bizId) {
        List<ProductDownPayloadResponse> result = gatewayService.formatGatewayDownJs(bizId);
        return Response.success(result);
    }

    @PutMapping("/start")
    @Operation(summary = "启动网关")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "启动网关", type = OperateTypeEnum.UPDATE)
    public Response<Void> startGateway(@RequestParam("bizId") String bizId) {
        String key = GW_LOCK_KEY + bizId;
        boolean lock = redisUtils.getLock(key, 60L);
        if (!lock) {
            throw new BusinessException("网关启动中");
        }
        try {
            gatewayService.startGateway(bizId);
        } finally {
            redisUtils.del(key);
        }
        return Response.success();
    }

    @PutMapping("/stop")
    @Operation(summary = "停止网关")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "停止网关", type = OperateTypeEnum.UPDATE)
    public Response<Void> stopGateway(@RequestParam("bizId") String bizId) {
        String key = GW_LOCK_KEY + bizId;
        boolean lock = redisUtils.getLock(key, 60L);
        if (!lock) {
            throw new BusinessException("网关停止中");
        }
        try {
            gatewayService.stopGateway(bizId);
        } finally {
            redisUtils.del(key);
        }
        return Response.success();
    }
}
