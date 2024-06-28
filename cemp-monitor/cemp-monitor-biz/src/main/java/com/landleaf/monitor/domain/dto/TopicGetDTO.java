package com.landleaf.monitor.domain.dto;

import lombok.Data;

@Data
public class TopicGetDTO {
    /**
     * bizId
     */
    private String bizId;

    /**
     * 对应的属性的code
     */
    private String code;
}
