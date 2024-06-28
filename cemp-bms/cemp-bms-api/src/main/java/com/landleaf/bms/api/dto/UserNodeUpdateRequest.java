package com.landleaf.bms.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * UserNodeAddRequest
 *
 * @author 张力方
 * @since 2023/6/7
 **/
@Data
public class UserNodeUpdateRequest {
    /**
     * 用户id
     */
    @NotNull(message = "用户id不能为空")
    private Long userId;
    /**
     * 租户id
     */
    @NotNull(message = "租户id不能为空")
    private Long tenantId;
    /**
     * 管理节点类型
     */
    @NotNull(message = "管理节点类型不能为空")
    private Short type;
    /**
     * 节点ids
     * <p>
     * 为空则删除所有
     */
    List<Long> nodeIds;

}
