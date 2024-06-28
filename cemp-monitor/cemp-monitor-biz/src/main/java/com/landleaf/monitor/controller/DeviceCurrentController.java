package com.landleaf.monitor.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.data.api.device.DeviceCurrentApi;
import com.landleaf.data.api.device.dto.DeviceCurrentDTO;
import com.landleaf.monitor.dal.redis.TemporaryConfigRedisDAO;
import com.landleaf.monitor.domain.entity.DeviceMonitorEntity;
import com.landleaf.monitor.domain.request.DeviceAttrCurrentRequest;
import com.landleaf.monitor.domain.vo.DeviceCurrentAttrVO;
import com.landleaf.monitor.domain.vo.DeviceCurrentVO;
import com.landleaf.monitor.service.DeviceMonitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/data")
@Tag(name = "设备-实时数据的控制层接口定义", description = "设备-实时数据的控制层接口定义")
public class DeviceCurrentController {
    @Resource
    private DeviceMonitorService deviceMonitorService;
    @Resource
    private DeviceCurrentApi deviceCurrentApi;
    @Resource
    private TemporaryConfigRedisDAO temporaryConfigRedisDAO;

    @GetMapping("/device-current")
    @Operation(summary = "获取设备实时状态")
    @Parameter(name = "ids", description = "设备id", example = "D000000000001,D000000000002", required = true)
    public Response<List<DeviceCurrentVO>> getDeviceCurrent(@RequestParam("ids") Collection<String> ids) {
        //TODO 前期查表填充其他id属性 查缓存填充attr属性 后期理论上来说要根据产品分组 走物模型配置缓存
        List<DeviceCurrentVO> result = new ArrayList<>(ids.size());
        List<DeviceMonitorEntity> deviceList = deviceMonitorService.list(new LambdaQueryWrapper<DeviceMonitorEntity>().in(DeviceMonitorEntity::getBizDeviceId, ids));
        Map<String, Map<String, Object>> deviceCurrentMap = deviceCurrentApi.getDeviceCurrent(ids).getCheckedData()
                .stream().collect(Collectors.toMap(DeviceCurrentDTO::getBizDeviceId, DeviceCurrentDTO::getCurrent));

        for (DeviceMonitorEntity deviceMonitorEntity : deviceList) {
            DeviceCurrentVO deviceCurrentVO = new DeviceCurrentVO();
            BeanUtil.copyProperties(deviceMonitorEntity, deviceCurrentVO);
            Set<String> attrCodes = temporaryConfigRedisDAO.getAttrCodes(deviceCurrentVO.getBizProductId());
            Map<String, Object> currentMap = deviceCurrentMap.get(deviceCurrentVO.getBizDeviceId());
            List<DeviceCurrentAttrVO> attrs = new ArrayList<>(attrCodes.size());
            for (String attrCode : attrCodes) {
                attrs.add(new DeviceCurrentAttrVO().setCode(attrCode).setValue(currentMap.get(attrCode)));
            }
            deviceCurrentVO.setAttrs(attrs);
            result.add(deviceCurrentVO);
        }
        return Response.success(result);
    }

    @PostMapping("/device-attr-current")
    @Operation(summary = "获取设备-属性实时状态")
    public Response<List<DeviceCurrentVO>> getDeviceAttrCurrent(@RequestBody DeviceAttrCurrentRequest request) {
        Map<String, List<String>> deviceAttrs = request.getDeviceAttrs();
        //TODO 前期查表填充其他id属性 查缓存填充attr属性 后期理论上来说要根据产品分组 走物模型配置缓存
        List<DeviceCurrentVO> result = new ArrayList<>(deviceAttrs.size());
        List<DeviceMonitorEntity> deviceList = deviceMonitorService.list(new LambdaQueryWrapper<DeviceMonitorEntity>().in(DeviceMonitorEntity::getBizDeviceId, deviceAttrs.keySet()));
        Map<String, Map<String, Object>> deviceCurrentMap = deviceCurrentApi.getDeviceAttrCurrent(deviceAttrs).getCheckedData()
                .stream().collect(Collectors.toMap(DeviceCurrentDTO::getBizDeviceId, DeviceCurrentDTO::getCurrent));

        for (DeviceMonitorEntity deviceMonitorEntity : deviceList) {
            DeviceCurrentVO deviceCurrentVO = new DeviceCurrentVO();
            BeanUtil.copyProperties(deviceMonitorEntity, deviceCurrentVO);
            List<String> attrCodes = deviceAttrs.get(deviceMonitorEntity.getBizDeviceId());
            Map<String, Object> currentMap = deviceCurrentMap.get(deviceCurrentVO.getBizDeviceId());
            List<DeviceCurrentAttrVO> attrs = new ArrayList<>(attrCodes.size());
            for (String attrCode : attrCodes) {
                attrs.add(new DeviceCurrentAttrVO().setCode(attrCode).setValue(currentMap.get(attrCode)));
            }
            deviceCurrentVO.setAttrs(attrs);
            result.add(deviceCurrentVO);
        }
        return Response.success(result);
    }
}
