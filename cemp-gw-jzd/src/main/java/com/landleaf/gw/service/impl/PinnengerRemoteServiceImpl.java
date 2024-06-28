package com.landleaf.gw.service.impl;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.landleaf.gw.service.PinnengerRemoteService;
import com.landleaf.kafka.sender.KafkaSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.landleaf.kafka.conf.TopicDefineConst.DEVICE_STATUS_UPLOAD_TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class PinnengerRemoteServiceImpl implements PinnengerRemoteService {
    private static String PI_TOKEN = "";

    private final KafkaSender kafkaSender;

    private void sendKafka(String msg) {
        log.info("kafka send {}", msg);
        kafkaSender.send(DEVICE_STATUS_UPLOAD_TOPIC + "GW-JZD", msg);
    }

    @Override
    public String login() {
        String token = null;
        String url = "https://www.pinnenger.com/thirdData/login";
        HttpResponse response = HttpUtil.createPost(url)
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                .header(Header.ACCEPT_CHARSET, CharsetUtil.UTF_8)
                .body("{\"userName\":\"shjzd_bxzf\",\"password\":\"Aa!12345678\"}")
                .execute();
        String data = response.body();
        if (!response.isOk() || !JSONUtil.isTypeJSON(data)) {
            return null;
        }else{
            JSONObject jsonData = JSONObject.parseObject(data);
            if(jsonData.getInteger("failCode") == 0){
                token =  jsonData.getJSONObject("data").getString("tokenId");
            }
        }
        System.out.println(token);
        return token;
    }

    @Override
    public String getStationList() {
        String url = "https://www.pinnenger.com/thirdData/getStationList";
        HttpResponse response = HttpUtil.createPost(url)
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                .header("XSRF-TOKEN",PI_TOKEN)
                .header(Header.ACCEPT_CHARSET, CharsetUtil.UTF_8)
                .body("{}")
                .execute();
        String data = response.body();
        if (!response.isOk() || !JSONUtil.isTypeJSON(data)) {
            return null;
        }
        System.out.println(response.body());
        return "Success";
    }

    @Override
    public String getDevList() {
        String url = "https://www.pinnenger.com/thirdData/getDevList";
        HttpResponse response = HttpUtil.createPost(url)
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                .header("XSRF-TOKEN",PI_TOKEN)
                .header(Header.ACCEPT_CHARSET, CharsetUtil.UTF_8)
                .body("{\"stationCodes\":\"20559F1CB2D94671AC23FA43C4DFC5AD\"}")
                .execute();
        String data = response.body();
        if (!response.isOk() || !JSONUtil.isTypeJSON(data)) {
            return null;
        }
        System.out.println(response.body());
        return "Success";
    }

    @Override
    public String getDevRealKpi(boolean reSend) {
        String url = "https://www.pinnenger.com/thirdData/getDevRealKpi";
        HttpResponse response = HttpUtil.createPost(url)
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                .header("XSRF-TOKEN",PI_TOKEN)
                .header(Header.ACCEPT_CHARSET, CharsetUtil.UTF_8)
                .body("{\"devIds\" : \"1057743503624752\", \"devTypeId\":\"206\"}")
                .execute();
        String data = response.body();
        if (!response.isOk() || !JSONUtil.isTypeJSON(data)) {
            return "error";
        }else{
            JSONObject jsonData = JSONObject.parseObject(data);
            if(jsonData.getInteger("failCode") != 0){
                if(jsonData.getInteger("failCode") == 306){
                    if(reSend){return "error";}
                    //token 过期
                    PI_TOKEN = this.login();
                    getDevRealKpi(true);
                }else{
                    return "error";
                }
            }else{
                JSONObject dataItem = JSONObject.parseObject(data).getJSONArray("data").getJSONObject(0).getJSONObject("dataItemMap");

                JSONObject kafkaSend = new JSONObject();
                JSONObject property = new JSONObject();
                kafkaSend.put("pkId","PK00001052");
                kafkaSend.put("sourceDevId","1057743503624752");
                kafkaSend.put("gateId","GW-JZD");
                kafkaSend.put("time",System.currentTimeMillis());
                property.put("Eqexp",dataItem.getFloat("reverse_reactive_cap"));
                property.put("Epexp",dataItem.getFloat("reverse_active_cap"));
                property.put("Epimp",dataItem.getFloat("active_cap"));
                property.put("Eqimp",dataItem.getFloat("forward_reactive_cap"));
                kafkaSend.put("propertys",property);
                //String msg = "{\"pkId\":\"PK00001052\",\"sourceDevId\":\"1057743503624752\",\"propertys\":{\"CST\":1,\"Epexp\":1.9,\"Eqimp\":740.29,\"Epimp\":2386.8,\"Eqexp\":0.82},\"gateId\":\"GW-JZD\",\"time\":1710078964459}";
                System.out.println(JSON.toJSONString(kafkaSend));
                sendKafka(JSON.toJSONString(kafkaSend));
            }
        }
        return "Success";
    }

}
