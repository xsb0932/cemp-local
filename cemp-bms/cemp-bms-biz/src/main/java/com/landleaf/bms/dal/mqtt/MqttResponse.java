package com.landleaf.bms.dal.mqtt;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

/**
 * MqttResponse
 *
 * @author 张力方
 * @since 2023/8/16
 **/
@Data
public class MqttResponse {
    private String code;
    private JSONObject data;
}
