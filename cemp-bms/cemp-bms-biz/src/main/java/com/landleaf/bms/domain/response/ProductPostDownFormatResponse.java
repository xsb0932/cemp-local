package com.landleaf.bms.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 产品下行报文格式
 *
 * @author 张力方
 * @since 2023/8/17
 **/
@Data
@Schema(description = "产品下行报文格式")
public class ProductPostDownFormatResponse {
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
    private PostDown productPostFormat;

    @Data
    public static class PostDown {
        private String gateId;
        private String pkId;
        private String sourceDevId;
        private Long time;
        private Map<String, Map<String, Object>> events = new LinkedHashMap<>();
        private Map<String, Map<String, Object>> services = new LinkedHashMap<>();
    }
}
