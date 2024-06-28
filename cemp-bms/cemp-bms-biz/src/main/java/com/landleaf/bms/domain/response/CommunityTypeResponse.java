package com.landleaf.bms.domain.response;

import lombok.Data;

/**
 * CommunityTypeResponse
 *
 * @author 张力方
 * @since 2023/7/12
 **/
@Data
public class CommunityTypeResponse {
    /**
     * 通讯方式类型
     */
    private Integer type;
    /**
     * 通讯方式名称
     */
    private String name;
}
