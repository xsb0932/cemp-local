package com.landleaf.bms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MsgNoticeUserDTO {
    /**
     *
     */
    @Schema(description = "租户编号")
    private Long tenantId;

    @Schema(description = "租户名")
    private String tenantName;

    @Schema(description = "用户编号")
    private Long userId;

    @Schema(description = "用户名")
    private String userName;
}
