package com.landleaf.gw.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.landleaf.comm.exception.enums.GlobalErrorCodeConstants;
import com.landleaf.gw.conf.AirConditionPlatformConf;
import com.landleaf.gw.conf.DeviceIntelligenceControlType;
import com.landleaf.gw.conf.JjConstance;
import com.landleaf.gw.domain.dto.DeviceControlAckDTO;
import com.landleaf.gw.domain.dto.DeviceControlDTO;
import com.landleaf.gw.domain.dto.DeviceControlDetailDTO;
import com.landleaf.gw.domain.dto.DeviceIntelligenceControlDTO;
import com.landleaf.gw.service.DeviceRelationService;
import com.landleaf.gw.service.JjRemoteService;
import com.landleaf.gw.util.JjDateUtil;
import com.landleaf.influx.core.InfluxdbTemplate;
import com.landleaf.influx.util.MeasurementFindUtil;
import com.landleaf.kafka.conf.TopicDefineConst;
import com.landleaf.kafka.sender.KafkaSender;
import com.landleaf.redis.RedisUtils;
import com.landleaf.redis.constance.KeyConstance;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.core.ApplicationContext;
import org.influxdb.dto.Point;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties(AirConditionPlatformConf.class)
@Slf4j
public class JjRemoteServiceImpl implements JjRemoteService {

    @Resource
    public AirConditionPlatformConf airConditionPlatformConf;


    @Resource
    protected InfluxdbTemplate influxdbTemplate;

    @Resource
    protected RedisUtils redisUtils;

    @Resource
    private DeviceRelationService deviceRelationServiceImpl;

    /**
     * 请求成功的返回码
     */
    private static final int SUCCESS_CODE = 0;

    /**
     * 失效的token
     */
    private static final int INVALID_TOKEN_CODE = 40003;

    /**
     * 返回的状态码
     */
    private String retCodeKey = "code";

    /**
     *
     */
    private String tokenKey = "access_token";

    /**
     * 第三方token
     */
    private static String token;

    @Resource
    private KafkaSender kafkaSender;

    @Override
    public boolean freshToken() {
        if (!airConditionPlatformConf.isEnabled()) {
            return false;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("appKey", airConditionPlatformConf.getAppKey());
        param.put("appSecret", airConditionPlatformConf.getAppSecret());
        param.put("grant_type", airConditionPlatformConf.getGrantType());
        String result = HttpUtil.post(airConditionPlatformConf.getTokenUrl(), param, airConditionPlatformConf.getTimeout());
        if (!StringUtils.hasText(result)) {
            log.error("请求空调第三方获取token失败，参数为：{}", result);
            return false;
        }
        JSONObject obj = JSONUtil.parseObj(result);

        // 判断token是否成功
        if (obj.containsKey(retCodeKey) && SUCCESS_CODE == obj.getInt(retCodeKey)) {
            token = obj.getStr(tokenKey);
        } else {
            log.error("Get token error. result is :{}", result);
            return false;
        }
        return true;
    }

    @Override
    public boolean getAirConditionList(int times) {
        if (times > 3) {
            // 重试次数超限，直接返回失败
            return false;
        }
        times++;
        Map<String, String> header = new HashMap<>();
        header.put(tokenKey, token);
        header.put("Content-Type", "application/json; charset=UTF-8");

        Map<String, Object> param = new HashMap<>();
        param.put("pageSize", Integer.MAX_VALUE);
        param.put("page", 1);
        HttpResponse resp = HttpUtil.createGet(airConditionPlatformConf.getDeviceUrl()).addHeaders(header).form(param).timeout(airConditionPlatformConf.getTimeout()).execute();
        if (!resp.isOk()) {
            log.error("调用第三方获取空调信息失败,请求内容header为：{}, 内容为：{}", JSONUtil.toJsonStr(header), JSONUtil.toJsonStr(param));
            return false;
        }
        String result = resp.body();
        if (!StringUtils.hasText(result)) {
            log.error("调用第三方获取空调信息失败,请求内容header为：{}, 内容为：{}", JSONUtil.toJsonStr(header), JSONUtil.toJsonStr(param));
            return false;
        }
        JSONObject obj = JSONUtil.parseObj(result);
        // token失效，则直接获取token
        if (obj.containsKey(retCodeKey)) {
            if (INVALID_TOKEN_CODE == obj.getInt(retCodeKey)) {
                if (!freshToken()) {
                    log.error("调用第三方获取token失败");
                    return false;
                } else {
                    return getAirConditionList(times);
                }
            } else if (SUCCESS_CODE == obj.getInt(retCodeKey)) {
                // 正常返回，解析空调信息
                JSONObject data = obj.getJSONObject("data");

                log.info("调用第三方获取空调信息,内容为：{}", data);
                if (data.containsKey("items")) {
                    JSONArray items = data.getJSONArray("items");
                    if (CollectionUtils.isEmpty(items)) {
                        return true;
                    }
                    JSONObject temp;
                    List<Point> pointList = Lists.newArrayList();

                    for (int i = 0; i < items.size(); i++) {
                        temp = items.getJSONObject(i);
                        Long airID = temp.getLong("airID");

                        String bizDeviceId = deviceRelationServiceImpl.getBizDeviceIdBySupplierAndOuterId(JjConstance.AIR_CONDITION_SUPPLIER_ID, String.valueOf(airID));

                        Map<String, String> tags = Maps.newHashMap();
                        tags.put("biz_device_id", bizDeviceId);
                        tags.put("biz_tenant_id", JjConstance.BIZ_TENANT_ID);
                        tags.put("biz_project_id", JjConstance.BIZ_PROJECT_ID);
                        tags.put("biz_node_id", JjConstance.BIZ_NODE_ID);

                        Map<String, Object> valMap = Maps.newHashMap();
                        valMap.put("CST", temp.getBool("online") ? 1 : 0);
                        valMap.put("RST", temp.getBool("curIsRuning") ? 1 : 0);
                        valMap.put("Humidity", temp.getBigDecimal("curHumidity"));
                        valMap.put("Temperature", temp.getBigDecimal("curIndoorTemperature"));
                        // 采集时间，格式为yyyy-MM-dd HH:mm:ss
                        String time = temp.getStr("updateTime");
                        if (!StringUtils.hasText(time) || "null".equalsIgnoreCase(time)) {
                            // 没有采集时间，数据有问题，不处理
                            continue;
                        }
                        pointList.add(Point.measurement(MeasurementFindUtil.getDeviceStatusMeasurementByProdCode(JjConstance.AIR_CONDITION_PROD_ID)).tag(tags)
                                .fields(valMap).time(JjDateUtil.parseTimeStr2Long(time), TimeUnit.SECONDS)
                                .build());

                        redisUtils.hmset(KeyConstance.DEVICE_CURRENT_STATUS + bizDeviceId, valMap);
                    }

                    if (!CollectionUtils.isEmpty(pointList)) {
                        influxdbTemplate.insertBatchV1(pointList);
                    }
                }
                return true;
            } else {
                // 失败， 输出对应结果
                log.error("调用第三方获取空调信息失败. result is :{}", result);
                return false;
            }
        } else {
            log.error("调用第三方获取空调信息失败. result is :{}", result);
            return false;
        }
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public boolean writeCmd(DeviceControlDTO cmd, String topic) {
        DeviceControlAckDTO resultDTO = new DeviceControlAckDTO();
        resultDTO.setMsgId(cmd.getMsgId());
        if (!airConditionPlatformConf.isEnabled()) {
            resultDTO.setSuccess(false);
            resultDTO.setErrorMsg(GlobalErrorCodeConstants.NO_DEVICE_2_CONTROL.getMsg());
            resultDTO.setErrorCode(GlobalErrorCodeConstants.NO_DEVICE_2_CONTROL.getCode());
            kafkaSender.send(topic, JSONUtil.toJsonStr(resultDTO));
            return false;
        }
        // 获取token
        if (!StringUtils.hasText(getToken())) {
            if (!freshToken()) {
                log.error("调用第三方获取token失败");
                resultDTO.setSuccess(false);
                resultDTO.setErrorMsg(GlobalErrorCodeConstants.ACCESS_ERROR.getMsg());
                resultDTO.setErrorCode(GlobalErrorCodeConstants.ACCESS_ERROR.getCode());
                kafkaSender.send(topic, JSONUtil.toJsonStr(resultDTO));
                return false;
            }
        }
        String result = sendCmdAndGetResult(cmd);
        if (StrUtil.isEmpty(result)) {
            // 返回失败
            resultDTO.setSuccess(false);
            resultDTO.setErrorMsg(GlobalErrorCodeConstants.CONTROL_ERROR.getMsg());
            resultDTO.setErrorCode(GlobalErrorCodeConstants.CONTROL_ERROR.getCode());
            kafkaSender.send(topic, JSONUtil.toJsonStr(resultDTO));
            return false;
        }

        JSONObject obj = JSONUtil.parseObj(result);
        // token失效，则直接获取token
        if (obj.containsKey(retCodeKey)) {
            if (INVALID_TOKEN_CODE == obj.getInt(retCodeKey)) {
                if (!freshToken()) {
                    log.error("调用第三方获取token失败");
                    resultDTO.setSuccess(false);
                    resultDTO.setErrorMsg(GlobalErrorCodeConstants.ACCESS_ERROR.getMsg());
                    resultDTO.setErrorCode(GlobalErrorCodeConstants.ACCESS_ERROR.getCode());
                    kafkaSender.send(topic, JSONUtil.toJsonStr(resultDTO));
                    return false;
                } else {
                    result = sendCmdAndGetResult(cmd);
                }
            }
        }

        obj = JSONUtil.parseObj(result);
        if (SUCCESS_CODE == obj.getInt(retCodeKey)) {
            log.info("Send cmd to control air condition success. result is {}", obj);
            // 锦江的空调特殊处理，将写入的温度，写入设备的状态
            List<DeviceControlDetailDTO> detailList = cmd.getDetail();
            Optional<DeviceControlDetailDTO> detail = detailList.stream().filter(i -> i.getAttrCode().equals("setTemp")).findFirst();

            if (detail.isPresent()) {
                Map<String, String> tags = Maps.newHashMap();
                tags.put("biz_device_id", cmd.getBizDeviceId());
                tags.put("biz_tenant_id", JjConstance.BIZ_TENANT_ID);
                tags.put("biz_project_id", JjConstance.BIZ_PROJECT_ID);
                tags.put("biz_node_id", JjConstance.BIZ_NODE_ID);

                Map<String, Object> valMap = Maps.newHashMap();
                valMap.put("setTemp", detail.get().getValue());
                influxdbTemplate.insert(Point.measurement(MeasurementFindUtil.getDeviceStatusMeasurementByProdCode(JjConstance.AIR_CONDITION_PROD_ID)).tag(tags)
                        .fields(valMap).time(System.currentTimeMillis() / 1000, TimeUnit.SECONDS)
                        .build());
            }
            resultDTO.setSuccess(true);
            resultDTO.setErrorMsg(StrUtil.EMPTY);
            resultDTO.setErrorCode(StrUtil.EMPTY);
            kafkaSender.send(topic, JSONUtil.toJsonStr(resultDTO));
            return true;
        }
        // 调用失败，不管是啥愿意，即使是token失效，也返回失败
        log.error("Send cmd to control air condition error. result is {}", obj);
        resultDTO.setSuccess(false);
        resultDTO.setErrorMsg(GlobalErrorCodeConstants.CONTROL_ERROR.getMsg());
        resultDTO.setErrorCode(GlobalErrorCodeConstants.CONTROL_ERROR.getCode());
        kafkaSender.send(topic, JSONUtil.toJsonStr(resultDTO));
        return false;
    }

    @Override
    public boolean writeIntelligenceCmd(DeviceIntelligenceControlDTO cmd, int times) {
        times++;
        boolean flag = false;

        // 区分类型，搞不同的cmd
        DeviceControlDTO cmdDTO = new DeviceControlDTO();
        cmdDTO.setMsgId(cmd.getMsgId());
        cmdDTO.setBizDeviceId(cmd.getBizDeviceId());
        cmdDTO.setRunningStatus(cmd.getRunningStatus());
        String detail = null;
        if (DeviceIntelligenceControlType.GUEST_ROOM_NORMAL.getType() == cmd.getType()) {
            // 客房正常开机逻辑
            detail = DeviceIntelligenceControlType.GUEST_ROOM_NORMAL.getCmdDetail();
        } else if (DeviceIntelligenceControlType.GUEST_ROOM_LOW.getType() == cmd.getType()) {
            detail = DeviceIntelligenceControlType.GUEST_ROOM_LOW.getCmdDetail();
        } else if (DeviceIntelligenceControlType.LOBBY_MORNING.getType() == cmd.getType()) {
            detail = DeviceIntelligenceControlType.LOBBY_MORNING.getCmdDetail();
        } else if (DeviceIntelligenceControlType.LOBBY_NOON.getType() == cmd.getType()) {
            detail = DeviceIntelligenceControlType.LOBBY_NOON.getCmdDetail();
        } else if (DeviceIntelligenceControlType.LOBBY_AFTERNOON.getType() == cmd.getType()) {
            detail = DeviceIntelligenceControlType.LOBBY_AFTERNOON.getCmdDetail();
        } else if (DeviceIntelligenceControlType.LOBBY_OFF.getType() == cmd.getType()) {
            detail = DeviceIntelligenceControlType.LOBBY_OFF.getCmdDetail();
        } else if (DeviceIntelligenceControlType.AISLE_AFTERNOON.getType() == cmd.getType()) {
            detail = DeviceIntelligenceControlType.AISLE_AFTERNOON.getCmdDetail();
        } else if (DeviceIntelligenceControlType.AISLE_OFF.getType() == cmd.getType()) {
            detail = DeviceIntelligenceControlType.AISLE_OFF.getCmdDetail();
        }
        JSONObject cmdObj = JSONUtil.parseObj(detail);
        List<DeviceControlDetailDTO> list = new ArrayList<>();
        DeviceControlDetailDTO temp;

        for (String str : cmdObj.keySet()) {
            temp = new DeviceControlDetailDTO();
            temp.setAttrCode(str);
            temp.setValue(cmdObj.getStr(str));
            list.add(temp);
        }
        cmdDTO.setDetail(list);

        return writeCmd(cmdDTO, TopicDefineConst.JJ_DEVICE_INTELLIGENCE_WRITE_ACK_TOPIC);
    }

    private String sendCmdAndGetResult(DeviceControlDTO cmd) {
        Map<String, String> header = new HashMap<>();
        header.put(tokenKey, token);
        header.put("Content-Type", "application/json; charset=UTF-8");

        Map<String, Object> param = new HashMap<>();
        // 键名 keyName : 1: 电源 （开关机时）2 : 模式 （切换模式时，关机时用1）3 ：风速
        // 模式名modelName：1：自动，2：制冷，3：除湿，4：通风，5：制热，6：关机。
        // 风速 inputAirType 1：自动，2：静音，3：低风速，4：中风速，5：中高风，6:高风。
        // 开关机类型 switchType ：0：关机 1：开机

        param.put("airId", deviceRelationServiceImpl.getOuterIdByBizDeviceId(cmd.getBizDeviceId()));
        Map<String, String> map = cmd.getDetail().stream().collect(Collectors.toMap(DeviceControlDetailDTO::getAttrCode, DeviceControlDetailDTO::getValue, (v1, v2) -> v1));
        boolean resetMode = false;
        // 如果当前是开机，并且指令为开机，则取消开机操作，改为普通的模式下发
        if (map.containsKey("RST")) {
            if ("on".equals(map.get("RST")) && 1 == cmd.getRunningStatus()) {
                map.remove("RST");
            } else if ("off".equals(map.get("RST")) && 0 == cmd.getRunningStatus()) {
                // 要关机，但当前已经是关机状态了，直接返回
                log.info("Stop the device, but it was stopped.");
                return "{\"code\":0}";
            }
        }
        if (map.containsKey("RST")) {
            if ("on".equals(map.get("RST"))) {
                // 开机
                param.put("keyName", 1);
                param.put("switchType", 1);

                param.put("temp", map.get("setTemp"));
                String mode = map.get("ktMode");
                if ("auto".equals(mode)) {
                    param.put("modelName", 1);
                } else if ("cooling".equals(mode)) {
                    param.put("modelName", 2);
                } else if ("heating".equals(mode)) {
                    param.put("modelName", 5);
                } else if ("arefaction".equals(mode)) {
                    param.put("modelName", 3);
                } else if ("ventilation".equals(mode)) {
                    param.put("modelName", 4);
                }

                String windMode = map.get("windMode");
                if ("auto".equals(windMode)) {
                    param.put("inputAirType", 1);
                } else if ("silent".equals(windMode)) {
                    param.put("inputAirType", 2);
                } else if ("lowSpeed".equals(windMode)) {
                    param.put("inputAirType", 3);
                } else if ("midSpeed".equals(windMode)) {
                    param.put("inputAirType", 3);
                } else if ("midHighSpeed".equals(windMode)) {
                    param.put("inputAirType", 5);
                } else if ("highSpeed".equals(windMode)) {
                    param.put("inputAirType", 4);
                }
                resetMode = true;
            } else {
                // 关机
                param.put("keyName", 1);
                param.put("switchType", 0);
                param.put("modelName", 1);
                param.put("inputAirType", 1);
                param.put("temp", 25);
            }
        } else {
            // 换模式
            param.put("keyName", 2);
            param.put("switchType", 1);

            param.put("temp", map.get("setTemp"));
            String mode = map.get("ktMode");
            if ("auto".equals(mode)) {
                param.put("modelName", 1);
            } else if ("cooling".equals(mode)) {
                param.put("modelName", 2);
            } else if ("heating".equals(mode)) {
                param.put("modelName", 5);
            } else if ("arefaction".equals(mode)) {
                param.put("modelName", 3);
            } else if ("ventilation".equals(mode)) {
                param.put("modelName", 4);
            }

            String windMode = map.get("windMode");
            if ("auto".equals(windMode)) {
                param.put("inputAirType", 1);
            } else if ("silent".equals(windMode)) {
                param.put("inputAirType", 2);
            } else if ("lowSpeed".equals(windMode)) {
                param.put("inputAirType", 3);
            } else if ("midSpeed".equals(windMode)) {
                param.put("inputAirType", 3);
            } else if ("midHighSpeed".equals(windMode)) {
                param.put("inputAirType", 5);
            } else if ("highSpeed".equals(windMode)) {
                param.put("inputAirType", 4);
            }
        }

        log.info("Send control cmd 2 air condition. param is :{}", JSONUtil.toJsonStr(param));
        HttpResponse resp = null;
        if (1 == airConditionPlatformConf.getSendCmd()) {
            resp = HttpUtil.createPost(airConditionPlatformConf.getDeviceWriteUrl()).header(tokenKey, token)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(JSONUtil.toJsonStr(param)).timeout(airConditionPlatformConf.getTimeout()).execute();
            if (!resp.isOk()) {
                log.error("调用第三方控制空调失败,请求内容header为：{}, 内容为：{}", JSONUtil.toJsonStr(header), JSONUtil.toJsonStr(param));
                return null;
            }
        }
        if (resetMode) {
            cmd.setDetail(cmd.getDetail().stream().filter(i -> !i.getAttrCode().equals("RST")).collect(Collectors.toList()));
            return sendCmdAndGetResult(cmd);
        }
        if (1 == airConditionPlatformConf.getSendCmd()) {
            return resp.body();
        }
        return null;
    }
}
