package com.landleaf.bms.api.dto;

import lombok.Data;

/**
 * UserProjRelationResponse
 *
 * @author 张力方
 * @since 2023/6/7
 **/
@Data
public class UserProjRelationResponse {
    /**
     * 用户编号
     */
    private Long userId;

    /**
     * bizProjId
     */
    private String bizProjId;
}
