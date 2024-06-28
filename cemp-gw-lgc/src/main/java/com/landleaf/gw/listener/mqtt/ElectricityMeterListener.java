package com.landleaf.gw.listener.mqtt;


import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.gw.conf.LgcConstance;
import com.landleaf.gw.domain.ElectricityMeter;
import com.landleaf.gw.domain.screen.response.ChargeResponse;
import com.landleaf.gw.domain.screen.response.OverviewResponse;
import com.landleaf.influx.util.MeasurementFindUtil;
import com.landleaf.monitor.api.MonitorApi;
import com.landleaf.mqtt.annotation.MqttMessageListener;
import com.landleaf.mqtt.core.MqttListener;
import com.landleaf.mqtt.core.MqttTemplate;
import com.landleaf.mqtt.enums.MqttQoS;
import com.landleaf.redis.constance.KeyConstance;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.Point;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static com.landleaf.gw.domain.screen.enums.LgcConstants.*;
import static com.landleaf.redis.constance.KeyConstance.DEVICE_CURRENT_STATUS;


/**
 * 气表的监听
 */
@Component
@AllArgsConstructor
@Slf4j
@MqttMessageListener(topic = "/lgc/PC0001/#")
public class ElectricityMeterListener extends BaseListener implements MqttListener<String> {

    private final MonitorApi monitorApi;

    private final MqttTemplate mqttTemplate;

    @Override
    public void onMessage(String topic, String msgStr) {
        String bizProdId = getBizProdId(topic);
        log.info("receive msg from mqtt, topic is :{}, content is : {}", topic, msgStr);
        ElectricityMeter msg = JSONUtil.toBean(msgStr, ElectricityMeter.class);
        String outerDeviceId = getOuterDeviceId(topic, msg.getDeviceId());
        Response<String> response = monitorApi.getdevice(outerDeviceId);
        String bizDeviceId =response.isSuccess() ? response.getResult() :"";
        Map<String, Object> valMap = msg.toMap();
        // 将信息入库
        if (MapUtil.isEmpty(valMap)) {
            // TODO 记录在线
            return;
        }
        // 依次判断值是否相等，不相等则推送kafka，入influx
        Map<String, String> tags = Maps.newHashMap();
        tags.put("biz_device_id", bizDeviceId);
        tags.put("biz_tenant_id", LgcConstance.BIZ_TENANT_ID);
        tags.put("biz_project_id", LgcConstance.BIZ_PROJECT_ID);
        tags.put("biz_node_id", LgcConstance.BIZ_NODE_ID);

        // 补上设备在线
        valMap.put("CST", LgcConstance.CST_ONLINE);
        Map<String, Object> currentMapNew = valMap.entrySet().stream().collect(Collectors.toMap(k -> k.getKey(), v -> String.valueOf(v.getValue())));

        if (!MapUtil.isEmpty(currentMapNew)) {
            if(AL_STATION1.equals(bizDeviceId)||AL_STATION2.equals(bizDeviceId)||AL_STATION3.equals(bizDeviceId)||AL_STATION4.equals(bizDeviceId)||AL_STATION5.equals(bizDeviceId)||DIR_STATION1.equals(bizDeviceId)){
                mqttTemplate.publish("/screen/lgc/device", buildFCharge(currentMapNew,bizDeviceId), MqttQoS.AT_LEAST_ONCE);
            }
            if(PCC1.equals(bizDeviceId)||PCC2.equals(bizDeviceId)){
                mqttTemplate.publish("/screen/lgc/device", buildFOverview(currentMapNew,bizDeviceId), MqttQoS.AT_LEAST_ONCE);
            }

//            redisUtils.hmset(KeyConstance.DEVICE_CURRENT_STATUS + bizDeviceId, currentMapNew);
//            // 插入influx
//            Point point = Point.measurement(MeasurementFindUtil.getDeviceStatusMeasurementByProdCode(bizProdId)).tag(tags)
//                    .fields(valMap).time(System.currentTimeMillis() / 1000, TimeUnit.SECONDS)
//                    .build();
//            influxdbTemplate.insert(point);
        }
    }

    void delProp(ChargeResponse.Middle middle , String bizDeviceId){
        if(AL_STATION1.equals(bizDeviceId)){
            middle.setAlStation02(null);middle.setAlStation03(null);middle.setAlStation04(null);middle.setAlStation05(null);middle.setDirStation01(null);
        }else if(AL_STATION2.equals(bizDeviceId)){
            middle.setAlStation02(null);middle.setAlStation03(null);middle.setAlStation04(null);middle.setAlStation05(null);middle.setDirStation01(null);
        }else if(AL_STATION3.equals(bizDeviceId)){
            middle.setAlStation01(null);middle.setAlStation02(null);middle.setAlStation04(null);middle.setAlStation05(null);middle.setDirStation01(null);
        }else if(AL_STATION4.equals(bizDeviceId)){
            middle.setAlStation01(null);middle.setAlStation02(null);middle.setAlStation03(null);middle.setAlStation05(null);middle.setDirStation01(null);
        }else if(AL_STATION5.equals(bizDeviceId)){
            middle.setAlStation01(null);middle.setAlStation02(null);middle.setAlStation03(null);middle.setAlStation04(null);middle.setDirStation01(null);
        }else if(DIR_STATION1.equals(bizDeviceId)){
            middle.setAlStation01(null);middle.setAlStation02(null);middle.setAlStation03(null);middle.setAlStation04(null);middle.setAlStation05(null);
        }
    }

    //D000000001238,D000000001239,D000000001240,D000000001241,D000000001242, D000000001237
    private String buildFCharge(Map<String, Object> currentMapNew , String bizDeviceId){
        ChargeResponse response = new ChargeResponse();
        ChargeResponse.Middle middle = new ChargeResponse.Middle();
        ChargeResponse.Station station = new ChargeResponse.Station();
        BigDecimal alp = new BigDecimal(String.valueOf(currentMapNew.get("P")));
        station.setInChargeP(alp);
        station.setStatus(alp.compareTo(BigDecimal.valueOf(0.5)) > 0 ? "充电中" : "充电停止");
        //station.setInChargeCurrentDay(new BigDecimal(String.valueOf(currentMapNew.get("dayChargeE"))));
        if(AL_STATION1.equals(bizDeviceId)){
            middle.setAlStation01(station);
        }else if(AL_STATION2.equals(bizDeviceId)){
            middle.setAlStation02(station);
        }else if(AL_STATION3.equals(bizDeviceId)){
            middle.setAlStation03(station);
        }else if(AL_STATION4.equals(bizDeviceId)){
            middle.setAlStation04(station);
        }else if(AL_STATION5.equals(bizDeviceId)){
            middle.setAlStation05(station);
        }else if(DIR_STATION1.equals(bizDeviceId)){
            middle.setDirStation01(station);
        }
        this.delProp(middle,bizDeviceId);
        response.setMiddle(middle);
        JSONObject jo =  JSONObject.from(response);
        jo.put("pageCode","/screen/lgc/charge");
        jo.put("bizDeviceId",bizDeviceId);
        return jo.toJSONString();
    }

    //D000000001235、D000000001236
    private String buildFOverview(Map<String, Object> currentMapNew, String bizDeviceId){
        Map<String, Object> otherPcc = null;
        if (bizDeviceId.equals(PCC1)) {
            otherPcc = deviceCurrentApi.getDeviceCurrentById(PCC2).getResult();
        } else  {
            otherPcc = deviceCurrentApi.getDeviceCurrentById(PCC1).getResult();
        }
        OverviewResponse response = new OverviewResponse();
        OverviewResponse.Middle middle = new OverviewResponse.Middle();
        //运行状态
        middle.setPccCurrentActiveP(NumberUtil.add(new BigDecimal(String.valueOf(currentMapNew.getOrDefault("P","0"))),new BigDecimal(String.valueOf(otherPcc.getOrDefault("P","0")))));
        middle.setPccCurrentReactiveP(NumberUtil.add(new BigDecimal(String.valueOf(currentMapNew.getOrDefault("Q","0"))),new BigDecimal(String.valueOf(otherPcc.getOrDefault("Q","0")))));
        response.setMiddle(middle);
        JSONObject jo =  JSONObject.from(response);
        jo.put("pageCode","/screen/lgc/overview");
        jo.put("bizDeviceId",bizDeviceId);
        return jo.toJSONString();
    }
}
