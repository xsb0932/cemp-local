package com.landleaf.script;

import lombok.Data;

import java.util.Map;

/**
 * 网关配置信息BO
 *
 * @author Yang
 */
@Data
public class GwConfigBO {
    /**
     * up k:topic v:description
     */
    private Map<String, String> upTopics;
    /**
     * down k:topic v:description
     */
    private Map<String, String> downTopics;
    /**
     * up js
     */
    private String upJs;
    /**
     * down js
     */
    private String downJs;
}
