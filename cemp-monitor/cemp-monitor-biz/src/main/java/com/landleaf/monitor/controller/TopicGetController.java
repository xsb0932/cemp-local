package com.landleaf.monitor.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.stream.CollectorUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.landleaf.bms.api.DeviceIotApi;
import com.landleaf.bms.api.ProductApi;
import com.landleaf.bms.api.dto.ProductDeviceAttrMapResponse;
import com.landleaf.bms.api.dto.ValueDescriptionResponse;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.exception.enums.GlobalErrorCodeConstants;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.data.api.device.DeviceCurrentApi;
import com.landleaf.data.api.device.dto.DeviceCurrentDTO;
import com.landleaf.job.api.JobLogApi;
import com.landleaf.job.api.dto.JobLogSaveDTO;
import com.landleaf.job.api.dto.JobRpcRequest;
import com.landleaf.monitor.config.MqttApiConstants;
import com.landleaf.monitor.domain.dto.TopicGetDTO;
import com.landleaf.monitor.domain.dto.WeatherDTO;
import com.landleaf.monitor.domain.entity.DeviceMonitorEntity;
import com.landleaf.monitor.domain.enums.ValueConstance;
import com.landleaf.monitor.service.DeviceMonitorService;
import com.landleaf.oauth.api.TenantApi;
import com.landleaf.redis.RedisUtils;
import com.landleaf.redis.constance.KeyConstance;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.landleaf.comm.constance.CommonConstant.JOB_EXEC_ERROR;
import static com.landleaf.comm.constance.CommonConstant.JOB_EXEC_SUCCESS;

@RestController
@RequestMapping("/topic")
@Slf4j
public class TopicGetController {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private DeviceCurrentApi deviceCurrentApi;

    @Resource
    private DeviceMonitorService deviceMonitorServiceImpl;

    @Resource
    private ProductApi productApi;

    @Value("${mqtt.http-url:http://127.0.0.1:1883}")
    private String httpUrl;
    @Value("${mqtt.username:admin}")
    private String username;
    @Value("${mqtt.password:public}")
    private String password;

    @Resource
    private TenantApi tenantApi;
    @Resource
    private JobLogApi jobLogApi;

    /**
     * topic保存时间,min
     */
    private static final int TOPIC_PERSIST_TIME = 10;

    @PostMapping("/get")
    @Operation(summary = "获取订阅的topic")
    public Response<JSONObject> getTopic(@RequestBody List<TopicGetDTO> paramList) {
        TenantContext.setIgnore(true);
        // 给定一个topic
        String uid = UUID.randomUUID().toString().replaceAll("-", "");
        if (!CollectionUtils.isEmpty(paramList)) {
            // 将信息缓存
            redisUtils.sSetAndTime(KeyConstance.DYNAMICS_NOTICE_KEY, 0L, uid);
            redisUtils.set(KeyConstance.NOTICE_EXPIRE_KEY + uid, System.currentTimeMillis(), TOPIC_PERSIST_TIME * 60);
            // 反向，将对应的device的key缓存
            paramList.forEach(i -> {
                String key = String.format(KeyConstance.NOTICE_KEY_PREFIX, i.getBizId(), i.getCode());
                redisUtils.sSetAndTime(key, 0L, uid);
            });
        } else {
            return Response.success(new JSONObject());
        }
        String topic = "/notice/device_status/" + uid;

        List<String> bizDeviceIds = paramList.stream().map(TopicGetDTO::getBizId).collect(Collectors.toList());
        Map<String, DeviceCurrentDTO> currentMap = deviceCurrentApi.getDeviceCurrent(bizDeviceIds).getResult().stream()
                .collect(Collectors.toMap(DeviceCurrentDTO::getBizDeviceId, Function.identity()));
        List<DeviceMonitorEntity> deviceMonitorList = deviceMonitorServiceImpl.list(new QueryWrapper<DeviceMonitorEntity>().lambda().in(DeviceMonitorEntity::getBizDeviceId, bizDeviceIds));
        Map<String, String> deviceProdMap = deviceMonitorList.stream().collect(Collectors.toMap(DeviceMonitorEntity::getBizDeviceId, DeviceMonitorEntity::getBizProductId));
        Response<Map<String, List<ProductDeviceAttrMapResponse>>> response = productApi.getProductAttrsMapByProdId(Lists.newArrayList(deviceMonitorList.stream().map(DeviceMonitorEntity::getBizProductId).collect(Collectors.toList())));
        Map<String, Map<String, String>> descs = new HashMap<>();
        Map<String, String> boolKey = new HashMap<>();
        String tk;
        if (response.isSuccess() && !MapUtil.isEmpty(response.getResult())) {
            for (Map.Entry<String, List<ProductDeviceAttrMapResponse>> entry : response.getResult().entrySet()) {
                for (ProductDeviceAttrMapResponse temp : entry.getValue()) {
                    if (ValueConstance.ENUMERATE.equals(temp.getDataType()) || ValueConstance.BOOLEAN.equals(temp.getDataType())) {
                        tk = String.format("%s_%s", entry.getKey(), temp.getIdentifier());
                        descs.put(tk, temp.getValueDescription().stream().collect(Collectors.toMap(ValueDescriptionResponse::getKey, ValueDescriptionResponse::getValue, (v1, v2) -> v1)));
                        if (ValueConstance.BOOLEAN.equals(temp.getDataType())) {
                            // boolean单独处理下
                            boolKey.put(tk, tk);
                        }
                    }
                }
            }
        }
        JSONObject result = new JSONObject();
        result.set("topic", topic);
        Map<String, Map> displayMap = new HashMap<>();
        Map<String, List<TopicGetDTO>> map = paramList.stream().collect(Collectors.groupingBy(TopicGetDTO::getBizId));
        map.forEach((k, v) -> {
            Map<String, Object> current = new HashMap<>();
            v.forEach(i -> {
                // 空的不置空，置为""，防止json是把null给干了。
                Object currentVal = currentMap.containsKey(k) && currentMap.get(k).getCurrent().containsKey(i.getCode()) ? currentMap.get(k).getCurrent().get(i.getCode()) : StrUtil.EMPTY;
                // 判断有没有枚举需要转换
                if (deviceProdMap.containsKey(k)) {
                    String tempKey = String.format("%s_%s", deviceProdMap.get(k), i.getCode());
                    if (descs.containsKey(tempKey)) {
                        if (boolKey.containsKey(tempKey)) {
                            // 转换value
                            if (String.valueOf(currentVal).equals("1")) {
                                currentVal = Boolean.TRUE.toString().toUpperCase();
                            } else if (String.valueOf(currentVal).equals("0")) {
                                currentVal = Boolean.FALSE.toString().toUpperCase();
                            } else {
                                // 不处理，直接拿currentVal用就行了，需要toUpperCase
                                currentVal = null != currentVal ? String.valueOf(currentVal).toUpperCase() : currentVal;
                            }
                        }
                        currentVal = descs.get(tempKey).get(String.valueOf(currentVal));
                    }
                }
                current.put(i.getCode(), null != currentVal ? currentVal : StrUtil.EMPTY);
            });
            displayMap.put(k, current);
        });
        result.set("deviceStatus", displayMap);
        return Response.success(result);
    }

    /**
     * 清除不用的topic
     *
     * @return
     */
    @PostMapping("/clear")
    @Operation(summary = "清除不用的topic")
    public Response<Boolean> clearUnusedTopic(@RequestBody JobRpcRequest request) {
        JobLogSaveDTO jobLog = new JobLogSaveDTO();
        jobLog.setJobId(request.getJobId())
                .setExecTime(LocalDateTime.now())
                .setExecType(request.getExecType())
                .setExecUser(request.getExecUser());
        try {
            Long tenantId = tenantApi.getTenantAdmin().getCheckedData();
            jobLog.setTenantId(tenantId);
            // 访问获取所有当前订阅的topic
            HttpResponse response = HttpUtil.createRequest(Method.GET, httpUrl + MqttApiConstants.SUBSCRIPTIONS)
                    .basicAuth(username, password)
                    .execute();
            if (!response.isOk() || !StrUtil.equals("0", JSONUtil.parseObj(response.body()).getStr("code"))) {
                log.error("查询topic的subscription失败 {}", response.body());
                throw new ServiceException(GlobalErrorCodeConstants.MQTT_HTTP_FAILED);
            }
            JSONObject result = JSONUtil.parseObj(response.body());
            JSONArray data = result.getJSONArray("data");

            Set<Object> existsTopicList = redisUtils.smembers(KeyConstance.DYNAMICS_NOTICE_KEY);
            long currentTime = System.currentTimeMillis();
            if (CollectionUtils.isEmpty(data)) {
                // 空的，直接给他全部标记离线
                existsTopicList.forEach(i -> {
                    Object obj = redisUtils.get(KeyConstance.NOTICE_EXPIRE_KEY + i);
                    if (null == obj) {
                        redisUtils.set(KeyConstance.NOTICE_EXPIRE_KEY + i, currentTime, TOPIC_PERSIST_TIME * 60);
                    }
                    Long time = (long) obj;
                    if (currentTime - time > TOPIC_PERSIST_TIME * 60 * 1000) {
                        // 超时删出
                        redisUtils.del(KeyConstance.NOTICE_EXPIRE_KEY + i);
                        redisUtils.setRemove(KeyConstance.DYNAMICS_NOTICE_KEY, i);
                    }
                });
            } else {
                Map<String, String> keyMap = new HashMap<>();
                for (int i = 0; i < data.size(); i++) {
                    keyMap.put(data.getJSONObject(i).getStr("topic"), StrUtil.EMPTY);
                }
                // 开始处理
                existsTopicList.forEach(i -> {
                    if (keyMap.containsKey("/notice/device_status/" + String.valueOf(i)) || !redisUtils.hasKey(KeyConstance.NOTICE_EXPIRE_KEY + i)) {
                        // 查询到相应topic有对应的订阅信息，则重置该topic的过期时间，redis.hasKey只是为了非第一次连接时，不存在expire_Key这种情况，做个保护
                        redisUtils.set(KeyConstance.NOTICE_EXPIRE_KEY + i, currentTime, TOPIC_PERSIST_TIME * 600);
                    } else {
                        Object obj = redisUtils.get(KeyConstance.NOTICE_EXPIRE_KEY + i);
                        Long time = (long) obj;
                        if (currentTime - time > TOPIC_PERSIST_TIME * 60 * 1000) {
                            // 超时删出
                            redisUtils.del(KeyConstance.NOTICE_EXPIRE_KEY + i);
                            redisUtils.setRemove(KeyConstance.DYNAMICS_NOTICE_KEY, i);
                        }
                    }
                });
            }
            //
            jobLog.setStatus(JOB_EXEC_SUCCESS);
        } catch (Exception e) {
            log.error("查询topic的subscription失败", e);
            jobLog.setStatus(JOB_EXEC_ERROR);
        }
        jobLogApi.saveLog(jobLog);
        return Response.success(true);
    }
}
