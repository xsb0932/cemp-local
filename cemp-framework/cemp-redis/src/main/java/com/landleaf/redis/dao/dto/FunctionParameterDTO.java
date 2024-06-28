package com.landleaf.redis.dao.dto;

import lombok.Data;

import java.util.List;

/**
 * 事件、服务参数
 *
 * @author yue lin
 * @since 2023/6/25 13:09
 */
@Data
public class FunctionParameterDTO {

    /**
     * 字段标识符
     */
    private String identifier;

    /**
     * 名称
     */
    private String name;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 值类型
     */
    private List<ValueDescriptionDTO> valueDescription;

}
