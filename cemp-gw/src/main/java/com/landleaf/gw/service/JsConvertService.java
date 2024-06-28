package com.landleaf.gw.service;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.landleaf.gw.context.GwConstant;
import com.landleaf.gw.context.GwContext;
import com.landleaf.kafka.sender.KafkaSender;
import com.landleaf.script.CempScriptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.script.ScriptException;

import static com.landleaf.kafka.conf.TopicDefineConst.DEVICE_STATUS_UPLOAD_TOPIC;

/**
 * @author Yang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JsConvertService {
    private final GwContext gwContext;
    private final CempScriptUtil cempScriptUtil;
    private final KafkaSender kafkaSender;

    public void upHandle(String topic, String msgStr) {
        log.info("up topic {} payload {}", topic, msgStr);
        try {
            Object obj = cempScriptUtil.handleUp(topic, msgStr);
            String json = JSONUtil.toJsonStr(obj);
            if (JSONUtil.isTypeJSONObject(json)) {
                JSONObject jsonObject = JSONUtil.parseObj(json);
                if (jsonObject.isEmpty()) {
                    return;
                }
                msgDeal(jsonObject);
                sendKafka(jsonObject.toString());
            }
            if (JSONUtil.isTypeJSONArray(json)) {
                JSONArray jsonArray = JSONUtil.parseArray(json);
                if (jsonArray.isEmpty()) {
                    return;
                }
                for (JSONObject jsonObject : jsonArray.jsonIter()) {
                    if (jsonObject.isEmpty()) {
                        return;
                    }
                    msgDeal(jsonObject);
                    sendKafka(jsonObject.toString());
                }
            }
        } catch (ScriptException | NoSuchMethodException e) {
            log.info("handle up js error", e);
        }
    }

    private void msgDeal(JSONObject jsonObject) {
        jsonObject.putOpt(GwConstant.GATE_ID, gwContext.getBizId());
        if (!jsonObject.containsKey(GwConstant.TIME)) {
            jsonObject.putOpt(GwConstant.TIME, System.currentTimeMillis());
        }
    }

    private void sendKafka(String msg) {
        log.info("kafka send {}", msg);
        kafkaSender.send(DEVICE_STATUS_UPLOAD_TOPIC + gwContext.getBizId(), msg);
    }
}
