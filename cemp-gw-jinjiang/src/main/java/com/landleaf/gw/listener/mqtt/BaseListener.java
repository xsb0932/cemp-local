package com.landleaf.gw.listener.mqtt;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.landleaf.gw.service.DeviceRelationService;
import com.landleaf.influx.core.InfluxdbTemplate;
import com.landleaf.kafka.sender.KafkaSender;
import com.landleaf.redis.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public abstract class BaseListener {

    @Resource
    protected KafkaSender kafkaSender;

    @Resource
    protected DeviceRelationService deviceRelationServiceImpl;

    @Resource
    protected InfluxdbTemplate influxdbTemplate;

    @Resource
    protected RedisUtils redisUtils;

    /**
     * 从topic中获取产品code
     * @param topic
     * @return
     */
    public String getBizProdId(String topic) {
        String[] items = topic.split("/");
        if (items.length > 4) {
            return items[3];
        }
        return "";
    }
    /**
     * 从topic中获取产品code
     * @param topic
     * @return
     */
    public String getOuterDeviceId(String topic, String deviceId) {
        if (StringUtils.hasText(deviceId)) {
            return deviceId;
        }
        String[] items = topic.split("/");
        if (items.length > 4) {
            return items[4];
        }
        return "";
    }

    /**
     * 别用了， 性能不咋地
     * @param obj
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public Map<String, Object> trans2ValMap(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        Map<String, Object> valMap = Maps.newHashMap();
        if (null != fields && 0 < fields.length) {
            for (Field f : fields) {
                try {
                    Method m = obj.getClass().getMethod("get" + StringUtils.capitalize(f.getName()));
                    valMap.put(f.getName(), m.invoke(obj));
                } catch (Exception e) {
                    // 不处理，不管
                }
            }
        }
        return valMap;
    }
}
