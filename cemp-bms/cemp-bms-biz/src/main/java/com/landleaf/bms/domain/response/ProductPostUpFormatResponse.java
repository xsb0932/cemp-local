package com.landleaf.bms.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 产品上行报文格式
 *
 * @author 张力方
 * @since 2023/8/17
 **/
@Data
@Schema(description = "产品上行报文格式")
public class ProductPostUpFormatResponse {
    /**
     * 产品业务id
     */
    private String productBizId;
    /**
     * 产品名称
     */
    private String productName;
    /**
     * 产品报文格式
     */
    private PostUp productPostFormat;

    @Data
    public static class PostUp {
        private String gateId;
        private String pkId = "";
        private String sourceDevId = "";
        private Long time;
        private Map<String, Object> propertys = new LinkedHashMap<>();
        private Map<String, Object> parameters = new LinkedHashMap<>();
        private Map<String, Map<String, Object>> events = new LinkedHashMap<>();
        private Map<String, Map<String, Object>> services = new LinkedHashMap<>();
    }

}
