package com.landleaf.bms.api.dto;

import lombok.Data;

/**
 * @author Yang
 */
@Data
public class GatewayProjectResponse {
    private Long tenantId;
    private String bizTenantId;
    private String gatewayBizId;
    private String projectBizId;
    private String nodeBizId;
    private String parentNodeBizId;
    private String projectName;
}
