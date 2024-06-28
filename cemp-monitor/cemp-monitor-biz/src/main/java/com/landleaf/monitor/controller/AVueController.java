package com.landleaf.monitor.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.landleaf.bms.api.ProductApi;
import com.landleaf.bms.api.ProjectSpaceApi;
import com.landleaf.bms.api.dto.ProductDeviceAttrResponse;
import com.landleaf.bms.api.dto.ProductDeviceAttributeListResponse;
import com.landleaf.bms.api.dto.ProductDeviceParameterListResponse;
import com.landleaf.bms.api.json.ValueDescription;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.data.api.device.DeviceCurrentApi;
import com.landleaf.file.api.FileApi;
import com.landleaf.messaging.api.ServiceControlApi;
import com.landleaf.messaging.api.dto.FunctionParameter;
import com.landleaf.messaging.api.dto.SendServiceRequest;
import com.landleaf.monitor.dal.mapper.DeviceParameterMapper;
import com.landleaf.monitor.domain.entity.DeviceMonitorEntity;
import com.landleaf.monitor.domain.entity.DeviceParameterEntity;
import com.landleaf.monitor.domain.request.AVueDeviceAttrPageRequest;
import com.landleaf.monitor.domain.request.AVueDevicePageRequest;
import com.landleaf.monitor.domain.request.AVueFunctionParameterRequest;
import com.landleaf.monitor.domain.request.AVueServiceControlRequest;
import com.landleaf.monitor.domain.response.*;
import com.landleaf.monitor.service.DeviceMonitorService;
import com.landleaf.monitor.service.ViewService;
import com.landleaf.oauth.api.UserRpcApi;
import com.landleaf.oauth.api.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.landleaf.monitor.domain.enums.ValueConstance.BOOLEAN_FALSE_KEY;
import static com.landleaf.monitor.domain.enums.ValueConstance.BOOLEAN_TRUE_KEY;

@RestController
@RequiredArgsConstructor
@RequestMapping("/avue")
@Tag(name = "avue视图编辑控制层接口定义", description = "avue视图编辑控制层接口定义")
public class AVueController {
    /**
     * 字符串
     */
    public static final String STRING = "01";
    /**
     * 整形数值
     */
    public static final String INTEGER = "02";
    /**
     * 浮点数值
     */
    public static final String DOUBLE = "03";
    /**
     * 布尔值
     */
    public static final String BOOLEAN = "04";
    /**
     * 枚举值
     */
    public static final String ENUMERATE = "05";
    @Value("${cemp.avue.file-host:}")
    private String fileHost;

    private final ViewService viewService;
    private final DeviceMonitorService deviceMonitorService;
    private final ProductApi productApi;
    private final DeviceCurrentApi deviceCurrentApi;
    private final DeviceParameterMapper deviceParameterMapper;
    private final ProjectSpaceApi projectSpaceApi;
    private final UserRpcApi userRpcApi;
    private final FileApi fileApi;
    private final ServiceControlApi serviceControlApi;

    @GetMapping("/get-devices")
    @Operation(summary = "分页查询设备")
    public Response<IPage<AVueDevicePageResponse>> aVueGetDevices(@Validated AVueDevicePageRequest request) {
        String bizProjectId = viewService.getBizProjectIdByAVueViewId(request.getViewId());
        if (StrUtil.isBlank(bizProjectId)) {
            throw new BusinessException("视图与项目关联不存在");
        }
        return Response.success(deviceMonitorService.aVueGetDevices(bizProjectId, request));
    }

    @GetMapping("/get-device-all")
    @Operation(summary = "下拉查询设备")
    public Response<List<AVueDeviceListResponse>> aVueGetDeviceAll(@RequestParam("viewId") String viewId) {
        String bizProjectId = viewService.getBizProjectIdByAVueViewId(viewId);
        if (StrUtil.isBlank(bizProjectId)) {
            throw new BusinessException("视图与项目关联不存在");
        }
        return Response.success(deviceMonitorService.aVueGetDeviceAll(bizProjectId));
    }

    @GetMapping("/get-device-attrs")
    @Operation(summary = "查询设备属性")
    public Response<List<AVueDeviceAttrPageResponse>> aVueGetDeviceAttrs(@Validated AVueDeviceAttrPageRequest request) {
        TenantContext.setIgnore(true);
        try {
            DeviceMonitorEntity device = deviceMonitorService.getbyDeviceId(request.getBizDeviceId());
            if (null == device) {
                throw new BusinessException("设备不存在");
            }
            List<ProductDeviceAttrResponse> attrList = productApi.getProjectAttrs(CollectionUtil.newArrayList(device.getBizProductId()))
                    .getCheckedData()
                    .get(device.getBizProductId());

            List<AVueDeviceAttrPageResponse> result = new ArrayList<>();
            if (null != attrList) {
                for (ProductDeviceAttrResponse attr : attrList) {
                    if ((StrUtil.isBlank(request.getAttrCode()) || StrUtil.contains(attr.getAttrCode(), request.getAttrCode()))
                            && (StrUtil.isBlank(request.getAttrName()) || StrUtil.contains(attr.getAttrName(), request.getAttrName()))) {
                        AVueDeviceAttrPageResponse data = new AVueDeviceAttrPageResponse();
                        data.setAttrCode(attr.getAttrCode()).setAttrName(attr.getAttrName()).setUnit(attr.getUnit());
                        result.add(data);
                    }
                }
            }
            return Response.success(result);
        } finally {
            TenantContext.setIgnore(false);
        }
    }

    @GetMapping("/device-current")
    @Operation(summary = "获取设备当前属性&参数")
    public Response<AVueDeviceCurrentResponse> deviceCurrent(@RequestParam("bizDeviceId") String bizDeviceId) {
        AVueDeviceCurrentResponse result = new AVueDeviceCurrentResponse();
        TenantContext.setIgnore(true);
        try {
            // 获取设备
            DeviceMonitorEntity device = deviceMonitorService.getbyDeviceId(bizDeviceId);
            if (null == device) {
                return Response.success(result);
            }
            result.setName(device.getName());
            String productName = productApi.getProductDetail(device.getProductId()).getCheckedData().getName();
            String spaceName;
            if (StrUtil.isBlank(device.getBizAreaId())) {
                spaceName = "";
            } else {
                spaceName = projectSpaceApi.getSpaceNameById(Long.valueOf(device.getBizAreaId())).getCheckedData();
            }
            // 获取产品属性和设备属性
            List<ProductDeviceAttributeListResponse> prodAttrs = productApi.getAttrsByProdId(device.getProductId()).getCheckedData();
            Map<String, Object> deviceAttrs = deviceCurrentApi.getDeviceCurrentById(device.getBizDeviceId()).getCheckedData();
            for (ProductDeviceAttributeListResponse prodAttr : prodAttrs) {
                AVueDeviceCurrentAttrDTO attr = new AVueDeviceCurrentAttrDTO();
                attr.setAttrName(prodAttr.getFunctionName())
                        .setValue(convertToValue(prodAttr.getDataType(), prodAttr.getValueDescription(), deviceAttrs.get(prodAttr.getIdentifier())))
                        .setUnit(prodAttr.getValueDescription().stream()
                                .filter(it -> StrUtil.equals("UNIT", it.getKey()))
                                .findAny()
                                .map(ValueDescription::getValue)
                                .orElse(""));
                result.getAttrs().add(attr);
            }
            // 获取产品参数和设备参数
            List<ProductDeviceParameterListResponse> prodParameters = productApi.getParameterByProdId(device.getProductId()).getCheckedData();
            Map<String, String> deviceParameters = deviceParameterMapper.selectList(new LambdaQueryWrapper<DeviceParameterEntity>().eq(DeviceParameterEntity::getBizDeviceId, bizDeviceId))
                    .stream().collect(Collectors.toMap(DeviceParameterEntity::getIdentifier, DeviceParameterEntity::getValue, (o1, o2) -> o1));
            for (ProductDeviceParameterListResponse prodParameter : prodParameters) {
                AVueDeviceCurrentParameterDTO parameter = new AVueDeviceCurrentParameterDTO();
                parameter.setParameterName(prodParameter.getFunctionName())
                        .setUnit(prodParameter.getValueDescription().stream()
                                .filter(it -> StrUtil.equals("UNIT", it.getKey()))
                                .findAny()
                                .map(ValueDescription::getValue)
                                .orElse(""));
                // 目前产品设计是必选参数在设备表 其他的是在device_parameter中 所以这边有额外的逻辑要处理了
                if (StrUtil.equals("01", prodParameter.getFunctionType())) {
                    parameter.setValue(
                            switch (prodParameter.getIdentifier()) {
                                case "name" -> device.getName();
                                case "code" -> device.getCode();
                                case "sourceDeviceId" -> device.getSourceDeviceId();
                                case "bizProjectId" -> device.getProjectName();
                                case "bizProductId" -> productName;
                                case "bizAreaId" -> spaceName;
                                case "locationDesc" -> device.getLocationDesc();
                                case "deviceDesc" -> device.getDeviceDesc();
                                default -> "";
                            }
                    );
                } else {
                    parameter.setValue(convertToValue(prodParameter.getDataType(), prodParameter.getValueDescription(), deviceParameters.get(prodParameter.getIdentifier())));
                }
                result.getParameters().add(parameter);
            }
        } finally {
            TenantContext.setIgnore(false);
        }
        return Response.success(result);
    }

    public String convertToValue(String type, List<ValueDescription> values, Object value) {
        if (null == value) {
            return "";
        }
        return switch (type) {
            case STRING, INTEGER, DOUBLE -> String.valueOf(value);
            case ENUMERATE -> values.stream()
                    .filter(o -> StrUtil.equals(o.getKey(), value.toString()))
                    .findAny()
                    .map(ValueDescription::getValue)
                    .orElse("");
            case BOOLEAN -> values.stream()
                    .filter(o -> booleanEquals(o.getKey(), value.toString()))
                    .findAny()
                    .map(ValueDescription::getValue)
                    .orElse("");
            default -> "";
        };
    }

    private boolean booleanEquals(String config, String value) {
        if (NumberUtil.isDouble(value) || NumberUtil.isLong(value)) {
            if (new BigDecimal(value).intValue() == 1) {
                value = BOOLEAN_TRUE_KEY;
            } else {
                value = BOOLEAN_FALSE_KEY;
            }
        }
        return StrUtil.equals(config, value);
    }

    @PostMapping("/service-control")
    @Operation(summary = "服务控制")
    public Response<Boolean> aVueGetDevices(@Validated @RequestBody AVueServiceControlRequest request) {
        long now = System.currentTimeMillis();
        Long userId = LoginUserUtil.getLoginUserId();
        UserDTO user = userRpcApi.getUserInfo(userId).getCheckedData();
        if (null == user) {
            throw new BusinessException("当前登录用户不存在");
        }
        Boolean checkPassword = userRpcApi.checkUserPassword(userId, request.getPassword()).getCheckedData();
        if (!checkPassword) {
            throw new BusinessException("密码错误");
        }
        TenantContext.setIgnore(true);
        // 服务下发
        DeviceMonitorEntity device = deviceMonitorService.getbyDeviceId(request.getBizDeviceId());
        if (null == device) {
            throw new BusinessException("设备不存在");
        }
        SendServiceRequest ssr = new SendServiceRequest();
        ArrayList<FunctionParameter> serviceParameter = new ArrayList<>();
        ssr.setBizDeviceId(device.getBizDeviceId())
                .setBizProjId(device.getBizProjectId())
                .setBizProdId(device.getBizProductId())
                .setSourceDeviceId(device.getSourceDeviceId())
                .setTime(now).setIdentifier(request.getIdentifier())
                .setServiceParameter(serviceParameter);
        if (null != request.getFunctionParameters()) {
            for (AVueFunctionParameterRequest functionParameter : request.getFunctionParameters()) {
                FunctionParameter parameter = BeanUtil.copyProperties(functionParameter, FunctionParameter.class);
                serviceParameter.add(parameter);
            }
        }

        Boolean isSuccess = serviceControlApi.sendService(ssr).getCheckedData();
        deviceMonitorService.addServiceEvent(
                now,
                TenantContext.getTenantId(),
                user.getNickname() + "(" + user.getUsername() + ")",
                device,
                request,
                isSuccess
        );
        return Response.success(isSuccess);
    }

    @PostMapping("/upload")
    @Operation(summary = "AVue资源文件上传")
    public AVueUploadResponse upload(@RequestParam("file") MultipartFile file) {
        String filePath = fileApi.aVueUpload(file).getCheckedData();
        return AVueUploadResponse.success(fileHost, filePath, file.getOriginalFilename());
    }
}
