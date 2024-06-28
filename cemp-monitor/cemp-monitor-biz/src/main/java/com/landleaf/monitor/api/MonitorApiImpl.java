package com.landleaf.monitor.api;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.landleaf.bms.api.ProductApi;
import com.landleaf.bms.api.dto.ProductDeviceAttrMapResponse;
import com.landleaf.bms.api.dto.ValueDescriptionResponse;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.exception.enums.GlobalErrorCodeConstants;
import com.landleaf.comm.license.LicenseCheck;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.monitor.domain.entity.DeviceMonitorEntity;
import com.landleaf.monitor.domain.enums.ValueConstance;
import com.landleaf.monitor.dto.AlarmResponse;
import com.landleaf.monitor.dto.DeviceMonitorVO;
import com.landleaf.monitor.service.DeviceModeService;
import com.landleaf.monitor.service.DeviceMonitorService;
import com.landleaf.monitor.service.HistoryEventService;
import com.landleaf.monitor.service.impl.ModeJobService;
import com.landleaf.mqtt.core.MqttTemplate;
import com.landleaf.mqtt.enums.MqttQoS;
import com.landleaf.redis.RedisUtils;
import com.landleaf.redis.constance.KeyConstance;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class MonitorApiImpl implements MonitorApi {

    @Resource
    private ModeJobService modeJobService;

    @Resource
    private DeviceMonitorService deviceMonitorServiceImpl;

    @Resource
    private DeviceModeService deviceModeService;

    @Resource
    private HistoryEventService historyEventService;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private MqttTemplate mqttTemplate;

    @Resource
    private ProductApi productApi;

    @Resource
    private LicenseCheck licenseCheck;

    @Override
    public Response<List<DeviceMonitorVO>> getDeviceListByBizIds(List<String> bizDeviceIdList) {
        TenantContext.setIgnore(true);
        List<DeviceMonitorEntity> list = deviceMonitorServiceImpl.list(new QueryWrapper<DeviceMonitorEntity>().lambda().in(DeviceMonitorEntity::getBizDeviceId, bizDeviceIdList));
        if (CollectionUtils.isEmpty(list)) {
            return Response.success(new ArrayList<>());
        }
        return Response.success(list.stream().map(i -> BeanUtil.copyProperties(i, DeviceMonitorVO.class)).collect(Collectors.toList()));
    }

    @Override
    public Response<Void> add(DeviceMonitorVO deviceMonitorVO) {
        DeviceMonitorEntity entity = new DeviceMonitorEntity();
        BeanUtils.copyProperties(deviceMonitorVO, entity);
        entity.setId(null);
        // check license
        if (licenseCheck.getDeviceLimit() > 0) {
            // 如果设置值为-1
            long count = deviceMonitorServiceImpl.count(Wrappers.emptyWrapper() );
            if (count >= licenseCheck.getDeviceLimit()) {
                // 抛出指定异常
                throw new BusinessException(GlobalErrorCodeConstants.DEVICE_LIMIT.getCode(), GlobalErrorCodeConstants.DEVICE_LIMIT.getMsg());
            }
        }
        deviceMonitorServiceImpl.save(entity);
        return Response.success();
    }

    @Override
    public Response<Void> edit(DeviceMonitorVO deviceMonitorVO) {
        TenantContext.setIgnore(true);
        DeviceMonitorEntity entity = new DeviceMonitorEntity();
        BeanUtils.copyProperties(deviceMonitorVO, entity);
        Long id = deviceMonitorServiceImpl.getbyDeviceId(deviceMonitorVO.getBizDeviceId()).getId();
        entity.setId(id);
        deviceMonitorServiceImpl.updateById(entity);
        return Response.success();
    }

    @Override
    public Response<Void> delete(String bizDeviceId) {
        TenantContext.setIgnore(true);
        deviceMonitorServiceImpl.delete(bizDeviceId);
        return Response.success();
    }

    @Override
    public Response<String> getdevice(String outid) {
        TenantContext.setIgnore(true);
        DeviceMonitorEntity entity = deviceMonitorServiceImpl.getbyOutId(outid);
        return Response.success(entity == null ? "" : entity.getBizDeviceId());
    }

    @Override
    public Response<String> getMode(String bizDeviceId) {
        TenantContext.setIgnore(true);
        return Response.success(deviceModeService.getMode(bizDeviceId).getModeCode());
    }

    @Override
    public Response<String> getRunningStatus(String bizDeviceId) {
        return Response.success(String.valueOf(modeJobService.getRunningStatus(bizDeviceId)));
    }

    @Override
    public Response<List<AlarmResponse>> getAlarms() {
        List<AlarmResponse> list = historyEventService.getRJDAlarms("2");
        return Response.success(list);
    }

    @Override
    public Response<Boolean> changeStatusNotice(String bizDeviceId, String bizProdId, String key, Object val, Long time) {
        Set<Object> keys = redisUtils.smembers(String.format(KeyConstance.NOTICE_KEY_PREFIX, bizDeviceId, key));
        if (CollectionUtils.isEmpty(keys)) {
            return Response.success();
        }
        // 将keys中的当前已经失效的notice排除
        keys.forEach(i -> {
            if (!redisUtils.hasKey(KeyConstance.NOTICE_EXPIRE_KEY + i)) {
                redisUtils.setRemove(String.format(KeyConstance.NOTICE_KEY_PREFIX, bizDeviceId, key), i);
            }
        });
        Map<String, Object> noticeObj = new HashMap<>();
        noticeObj.put("time", time);
        noticeObj.put("biz_device_id", bizDeviceId);
        //  根据prodId和key判断类型，枚举转为对应desc。
        //查询所有的品类-产品
        Map<String, Object> param = new HashMap<>();
        param.put(key, val);
        noticeObj.put("parameter", param);
        Response<Map<String, List<ProductDeviceAttrMapResponse>>> response = productApi.getProductAttrsMapByProdId(Lists.newArrayList(bizProdId));
        if (response.isSuccess() && !MapUtil.isEmpty(response.getResult()) && response.getResult().containsKey(bizProdId)) {
            Map<String, ProductDeviceAttrMapResponse> descs = response.getResult().get(bizProdId).stream().collect(Collectors.toMap(
                    attr -> String.format("%s_%s", bizProdId, attr.getIdentifier()),
                    i -> i));
            ProductDeviceAttrMapResponse productDeviceAttrMapResponse = descs.get(String.format("%s_%s", bizProdId, key));
            if (null != productDeviceAttrMapResponse) {
                // 存在，判断枚举的单独处理
                if (ValueConstance.ENUMERATE.equals(productDeviceAttrMapResponse.getDataType()) || ValueConstance.BOOLEAN.equals(productDeviceAttrMapResponse.getDataType())) {
                    // 枚举值需要通过值把key找出来
                    Map<String, String> map = productDeviceAttrMapResponse.getValueDescription().stream().collect(Collectors.toMap(ValueDescriptionResponse::getKey, ValueDescriptionResponse::getValue, (v1, v2) -> v1));
                    // 转换value
                    if (ValueConstance.BOOLEAN.equals(productDeviceAttrMapResponse.getDataType())) {
                        if (String.valueOf(val).equals("1")) {
                            param.put(key, map.get(Boolean.TRUE.toString().toUpperCase()));
                        } else if (String.valueOf(val).equals("0")) {
                            param.put(key, map.get(Boolean.FALSE.toString().toUpperCase()));
                        } else {
                            param.put(key, map.get(String.valueOf(val).toUpperCase()));
                        }
                    } else {
                        if (map.containsKey(val)) {
                            param.put(key, map.get(val));
                        }
                    }
                }
            }
        }

        keys.forEach(i -> {
            mqttTemplate.publish("/notice/device_status/" + i, JSONUtil.toJsonStr(noticeObj), MqttQoS.AT_MOST_ONCE);
        });
        return Response.success();
    }

    @Override
    public Response<Boolean> changeAllStatusNotice(String bizDeviceId, String bizProdId, long time, Map<String, Object> valMap) {
        for (Map.Entry<String, Object> entry : valMap.entrySet()) {
            Set<Object> keys = redisUtils.smembers(String.format(KeyConstance.NOTICE_KEY_PREFIX, bizDeviceId, entry.getKey()));
            if (CollectionUtils.isEmpty(keys)) {
               continue;
            }
            // 将keys中的当前已经失效的notice排除
            keys.forEach(i -> {
                if (!redisUtils.hasKey(KeyConstance.NOTICE_EXPIRE_KEY + i)) {
                    redisUtils.setRemove(String.format(KeyConstance.NOTICE_KEY_PREFIX, bizDeviceId, entry.getKey()), i);
                }
            });
            Map<String, Object> noticeObj = new HashMap<>();
            noticeObj.put("time", time);
            noticeObj.put("biz_device_id", bizDeviceId);
            //  根据prodId和key判断类型，枚举转为对应desc。
            //查询所有的品类-产品
            Map<String, Object> param = new HashMap<>();
            param.put(entry.getKey(), entry.getValue());
            noticeObj.put("parameter", param);
            Response<Map<String, List<ProductDeviceAttrMapResponse>>> response = productApi.getProductAttrsMapByProdId(Lists.newArrayList(bizProdId));
            if (response.isSuccess() && !MapUtil.isEmpty(response.getResult()) && response.getResult().containsKey(bizProdId)) {
                Map<String, ProductDeviceAttrMapResponse> descs = response.getResult().get(bizProdId).stream().collect(Collectors.toMap(
                        attr -> String.format("%s_%s", bizProdId, attr.getIdentifier()),
                        i -> i));
                ProductDeviceAttrMapResponse productDeviceAttrMapResponse = descs.get(String.format("%s_%s", bizProdId, entry.getKey()));
                if (null != productDeviceAttrMapResponse) {
                    // 存在，判断枚举的单独处理
                    if (ValueConstance.ENUMERATE.equals(productDeviceAttrMapResponse.getDataType()) || ValueConstance.BOOLEAN.equals(productDeviceAttrMapResponse.getDataType())) {
                        // 枚举值需要通过值把key找出来
                        Map<String, String> map = productDeviceAttrMapResponse.getValueDescription().stream().collect(Collectors.toMap(ValueDescriptionResponse::getKey, ValueDescriptionResponse::getValue, (v1, v2) -> v1));
                        // 转换value
                        if (ValueConstance.BOOLEAN.equals(productDeviceAttrMapResponse.getDataType())) {
                            if (String.valueOf(entry.getValue()).equals("1")) {
                                param.put(entry.getKey(), map.get(Boolean.TRUE.toString().toUpperCase()));
                            } else if (String.valueOf(entry.getValue()).equals("0")) {
                                param.put(entry.getKey(), map.get(Boolean.FALSE.toString().toUpperCase()));
                            } else {
                                param.put(entry.getKey(), map.get(String.valueOf(entry.getValue()).toUpperCase()));
                            }
                        } else {
                            if (map.containsKey(entry.getValue())) {
                                param.put(entry.getKey(), map.get(entry.getValue()));
                            }
                        }
                    }
                }
            }

            keys.forEach(i -> {
                mqttTemplate.publish("/notice/device_status/" + i, JSONUtil.toJsonStr(noticeObj), MqttQoS.AT_MOST_ONCE);
            });
            continue;
        }
        return Response.success();
    }
}
