package com.landleaf.monitor.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.landleaf.bms.api.DeviceIotApi;
import com.landleaf.bms.api.ProductApi;
import com.landleaf.bms.api.dto.ProductDeviceAttributeListResponse;
import com.landleaf.bms.api.dto.ProductDeviceParameterListResponse;
import com.landleaf.bms.api.dto.ProductDeviceServiceListResponse;
import com.landleaf.bms.api.json.FunctionParameter;
import com.landleaf.bms.api.json.ValueDescription;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.exception.enums.GlobalErrorCodeConstants;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.monitor.domain.entity.DeviceMonitorEntity;
import com.landleaf.monitor.domain.enums.ValueConstance;
import com.landleaf.monitor.domain.response.DeviceServiceListResponse;
import com.landleaf.monitor.service.DeviceMonitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备服务接口定义
 *
 * @author hebin
 * @since 2023-06-05
 */
@RestController
@AllArgsConstructor
@RequestMapping("/device-service")
@Tag(name = "设备服务接口定义", description = "设备服务接口定义")
public class DeviceServiceController {

    @Resource
    private ProductApi productApi;

    @Resource
    private DeviceMonitorService deviceMonitorServiceImpl;

    /**
     * 获取服务详情
     *
     * @return 获取服务详情
     */
    @GetMapping("/list-detail")
    @Operation(summary = "设备树结构", description = "")
    public Response<List<DeviceServiceListResponse>> getDetail(@RequestParam(value = "bizDeviceId") String bizDeviceId) {
        TenantContext.setIgnore(true);
        DeviceMonitorEntity deviceInfo = deviceMonitorServiceImpl.selectByBizDeviceId(bizDeviceId);
        // 从bms获取对应的response
        Response<List<ProductDeviceServiceListResponse>> serviceListResp = productApi.getServiceByProdId(deviceInfo.getProductId());
        // cover 2 DeviceServiceListResponse
        if (!serviceListResp.isSuccess()) {
            throw new BusinessException(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
        }
        if (CollectionUtils.isEmpty(serviceListResp.getResult())) {
            return Response.success(new ArrayList<>());
        }
        List<DeviceServiceListResponse> resultList = new ArrayList<>();
        DeviceServiceListResponse temp;

        List<FunctionParameter> controlPointDesc = new ArrayList<>();
        for (ProductDeviceServiceListResponse productDeviceServiceListResponse : serviceListResp.getResult()) {
            temp = BeanUtil.copyProperties(productDeviceServiceListResponse, DeviceServiceListResponse.class);
            resultList.add(temp);
            // 如果是GeneralWritePoints，则修改对应的信息
            if (!temp.getIdentifier().equals("GeneralWritePoints")) {
                continue;
            }
            // 判断值里面有没有SysWritablePoint
            List<FunctionParameter> tempFunction = temp.getFunctionParameter();
            boolean hasSysWritablePoint = false;
            // check
            for (FunctionParameter i : tempFunction) {
                if (i.getIdentifier().equals("SysWritablePoint")) {
                    if (!hasSysWritablePoint) {
                        hasSysWritablePoint = true;
                        // 获取设备对应的可写的属性
                        Response<List<ProductDeviceParameterListResponse>> parameterResp = productApi.getParameterByProdId(deviceInfo.getProductId());
                        if (!parameterResp.isSuccess()) {
                            throw new BusinessException(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
                        }
                        // 那可写的 parameter
                        if (!CollectionUtils.isEmpty(parameterResp.getResult())) {
                            parameterResp.getResult().forEach(r -> {
                                if (r.getRw().equals("02")) {
                                    // 可写的可用
                                    FunctionParameter tempParam = new FunctionParameter();
                                    tempParam.setIdentifier(r.getIdentifier());
                                    tempParam.setName(r.getFunctionName());
                                    tempParam.setDataType(r.getDataType());
                                    tempParam.setValueDescription(r.getValueDescription());
                                    controlPointDesc.add(tempParam);
                                }
                            });
                        }
                        Response<List<ProductDeviceAttributeListResponse>> attrsResp = productApi.getAttrsByProdId(deviceInfo.getProductId());
                        if (!attrsResp.isSuccess()) {
                            throw new BusinessException(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
                        }
                        // 那可写的attr
                        if (!CollectionUtils.isEmpty(attrsResp.getResult())) {
                            attrsResp.getResult().forEach(r -> {
                                if (r.getRw().equals("02")) {
                                    // 可写的可用
                                    FunctionParameter tempParam = new FunctionParameter();
                                    tempParam.setIdentifier(r.getIdentifier());
                                    tempParam.setName(r.getFunctionName());
                                    tempParam.setDataType(r.getDataType());
                                    tempParam.setValueDescription(r.getValueDescription());
                                    controlPointDesc.add(tempParam);
                                }
                            });
                        }
                    }
                }
                // 此FunctionParameter类型改为枚举
                i.setDataType(ValueConstance.ENUMERATE);
                List<ValueDescription> tempValueDescription = Lists.newArrayList();
                controlPointDesc.forEach(cp -> {
                    ValueDescription vd = new ValueDescription();
                    vd.setKey(cp.getIdentifier());
                    vd.setValue(cp.getName());
                    tempValueDescription.add(vd);
                });
                i.setValueDescription(tempValueDescription);
            }
            if (hasSysWritablePoint) {
                temp.setControlPointDesc(controlPointDesc);
            }
        }
        return Response.success(resultList);
    }
}
