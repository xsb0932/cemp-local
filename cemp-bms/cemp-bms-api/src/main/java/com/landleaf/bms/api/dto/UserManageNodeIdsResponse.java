package com.landleaf.bms.api.dto;

import lombok.Data;

import java.util.List;

/**
 * UserManageNodeIdsResponse
 *
 * @author 张力方
 * @since 2023/6/12
 **/
@Data
public class UserManageNodeIdsResponse {

    /**
     * 授权类型（1区域 2项目）
     */
    private Short type;

    /**
     * 节点ids
     */
    private List<Long> nodeIds;
}
