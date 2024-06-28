package com.landleaf.messaging.api;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.landleaf.bms.api.GatewayApi;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.exception.enums.GlobalErrorCodeConstants;
import com.landleaf.kafka.conf.TopicDefineConst;
import com.landleaf.kafka.sender.KafkaSender;
import com.landleaf.messaging.api.dto.FunctionParameter;
import com.landleaf.messaging.api.dto.SendServiceRequest;
import com.landleaf.messaging.domain.bo.DeviceServiceBO;
import com.landleaf.messaging.service.DeviceServiceService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ServiceControlApiImpl implements ServiceControlApi {

    @Resource
    private GatewayApi gatewayApi;

    @Resource
    private KafkaSender kafkaSender;

    @Resource
    private DeviceServiceService deviceServiceServiceImpl;

    @Override
    public Response<Boolean> sendService(SendServiceRequest request) {
        // 根据bizProjId，找到对应的网关
        Response<List<String>> gatewayListResp = gatewayApi.findBizIdByProjAndProdId(request.getBizProjId(), request.getBizProdId());

        if (!gatewayListResp.isSuccess() || CollectionUtil.isEmpty(gatewayListResp.getResult())) {
            // 没有可用的gateway
            throw new BusinessException(GlobalErrorCodeConstants.GATEWAY_NOT_EXISTS);
        }
        DeviceServiceBO deviceService = new DeviceServiceBO().setServices(new HashMap<>());

        deviceService.setPkId(request.getBizProdId())
                .setSourceDevId(request.getSourceDeviceId())
                .setTime(request.getTime());

        HashMap<String, Object> serviceParameterMap = new HashMap<>();
        deviceService.getServices().put(request.getIdentifier(), serviceParameterMap);
        if (null != request.getServiceParameter()) {
            for (FunctionParameter parameter : request.getServiceParameter()) {
                serviceParameterMap.put(parameter.getIdentifier(), parameter.getValue());
            }
        }
        gatewayListResp.getResult().forEach(i -> {
            deviceService.setGateId(i);
            kafkaSender.send(TopicDefineConst.DEVICE_SERVICE_WRITE_GATEWAY_TOPIC + i, JSONUtil.toJsonStr(deviceService));
            // 生成对应的event信息
            if (!CollectionUtils.isEmpty(deviceService.getServices())) {
                deviceService.getServices().forEach((k, v) -> {
                    deviceServiceServiceImpl.dealServiceEventInfo(i, request.getBizProdId(), request.getBizDeviceId(), request.getTime(), k, v, request.getNickname() + "(" + request.getUsername() + ")");
                });
            }
        });
        return Response.success(true);
    }
}
