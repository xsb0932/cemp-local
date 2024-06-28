package com.landleaf.bms.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 租户根节点创建请求
 *
 * @author 张力方
 * @since 2023/6/7
 **/
@Data
public class ManagementNodeRootCreateRequest {
    /**
     * 租户id
     */
    @NotNull(message = "租户id不能为空")
    public Long tenantId;
    /**
     * 租户管理员id
     */
    @NotNull(message = "租户管理员id不能为空")
    public Long tenantAdminId;
    /**
     * 租户名称
     * <p>
     * 租户名称作为管理节点根节点名称
     */
    @NotNull(message = "租户名称不能为空")
    public String tenantName;
    /**
     * 租户编码
     * <p>
     * 租户编码作为管理节点根节点编码
     */
    @NotNull(message = "租户编码不能为空")
    public String tenantCode;
}
