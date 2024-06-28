package com.landleaf.bms.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.LinkedHashMap;

/**
 * 产品上行报文
 *
 * @author Yang
 */
@Data
@Schema(description = "产品上行报文格式")
public class ProductDownPayloadBO {
    // 20231102修改，按产品要求展示json移除网关业务id
//    @Schema(description = "网关业务id")
//    private String gateId;
    @Schema(description = "")
    private String pkId;
    @Schema(description = "源设备id")
    private String sourceDevId;
    @Schema(description = "时间戳")
    private String time;
    @Schema(description = "设备事件下行")
    private LinkedHashMap<String, LinkedHashMap<String, Object>> events;
    @Schema(description = "设备服务下行")
    private LinkedHashMap<String, LinkedHashMap<String, Object>> services;
}
