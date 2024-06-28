package com.landleaf.data.api.device;

import cn.hutool.core.collection.CollectionUtil;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.data.api.device.dto.DeviceAlarmDTO;
import com.landleaf.data.api.device.dto.DeviceCurrentDTO;
import com.landleaf.data.api.device.dto.DeviceListCurrentDTO;
import com.landleaf.data.api.device.dto.DeviceListCurrentReqDTO;
import com.landleaf.data.dal.redis.CurrentDeviceRedisDao;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author Yang
 */
@RestController
@Validated
public class DeviceCurrentApiImpl implements DeviceCurrentApi {
    @Resource
    private CurrentDeviceRedisDao currentDeviceRedisDao;

    @Override
    public Response<List<DeviceCurrentDTO>> getDeviceCurrent(Collection<String> ids) {
        List<DeviceCurrentDTO> result = new ArrayList<>();
        for (String id : ids) {
            DeviceCurrentDTO device = new DeviceCurrentDTO().setBizDeviceId(id);
            device.setCurrent(currentDeviceRedisDao.getCurrent(id));
            result.add(device);
        }
        return Response.success(result);
    }

    @Override
    public Response<Map<String, Object>> getDeviceCurrentById(String bizDeviceId) {
        return Response.success(currentDeviceRedisDao.getCurrent(bizDeviceId));
    }

    @Override
    public Response<List<DeviceCurrentDTO>> getDeviceAttrCurrent(Map<String, List<String>> deviceAttrs) {
        List<DeviceCurrentDTO> result = new ArrayList<>();
        for (String id : deviceAttrs.keySet()) {
            DeviceCurrentDTO device = new DeviceCurrentDTO().setBizDeviceId(id);
            Map<String, Object> current = new HashMap<>(deviceAttrs.get(id).size());
            Map<String, Object> allCurrent = currentDeviceRedisDao.getCurrent(id);
            for (String attrCode : deviceAttrs.get(id)) {
                current.put(attrCode, allCurrent.get(attrCode));
            }
            device.setCurrent(current);
            result.add(device);
        }
        return Response.success(result);
    }

    @Override
    public Response<List<DeviceListCurrentDTO>> getDeviceCurrent(DeviceListCurrentReqDTO reqDTO) {
        List<DeviceListCurrentDTO> result = new ArrayList<>();
        if (CollectionUtil.isEmpty(reqDTO.getDeviceIds())) {
            return Response.success(result);
        }
        for (String id : reqDTO.getDeviceIds()) {
            DeviceListCurrentDTO device = new DeviceListCurrentDTO().setBizDeviceId(id).setCurrent(new HashMap<>(16));
            if (CollectionUtil.isNotEmpty(reqDTO.getAttrCodes())) {
                Map<String, Object> current = currentDeviceRedisDao.getCurrent(id);
                for (String attrCode : reqDTO.getAttrCodes()) {
                    device.getCurrent().put(attrCode, current.get(attrCode));
                }
            }
            result.add(device);
        }
        return Response.success(result);
    }

    @Override
    public Response<Map<String, Integer>> getDeviceCstMap(List<String> bizDeviceIds) {
        Map<String, Integer> result = currentDeviceRedisDao.getDeviceCstMap(bizDeviceIds);
        return Response.success(result);
    }

    @Override
    public Response<Integer> getDeviceCstStatus(String bizDeviceId) {
        Integer result = currentDeviceRedisDao.getDeviceCstStatus(bizDeviceId);
        return Response.success(result);
    }

    @Override
    public Response<List<DeviceAlarmDTO>> getDeviceCurrentAlarm(List<String> bizDeviceIds) {
        List<DeviceAlarmDTO> result = new ArrayList<>();
        DeviceAlarmDTO device;
        List<String> currentAlarmCode = new ArrayList<>();
        if (!CollectionUtil.isEmpty(bizDeviceIds)) {
            for (String bizDeviceId : bizDeviceIds) {
                device = new DeviceAlarmDTO().setBizDeviceId(bizDeviceId);
                device.setDevAlarmCode(new ArrayList<>());
                device.setRuleAlarmCode(new ArrayList<>());
                currentAlarmCode = currentDeviceRedisDao.getCurrentAlarmCode(bizDeviceId);
                for (String code : currentAlarmCode) {
                    if (code.startsWith("RU")) {
                        device.getRuleAlarmCode().add(code);
                    } else {
                        device.getDevAlarmCode().add(code);
                    }
                }
                result.add(device);
            }
        }
        return Response.success(result);
    }
}
