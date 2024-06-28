package com.landleaf.bms.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "mqtt主题")
public class Topic {
    /**
     * 主题名称
     */
    @Schema(description = "主题名称", example = "电表上行")
    private String name;
    /**
     * 主题
     */
    @Schema(description = "主题", example = "/aaa/bbb/001")
    private String topic;
}
