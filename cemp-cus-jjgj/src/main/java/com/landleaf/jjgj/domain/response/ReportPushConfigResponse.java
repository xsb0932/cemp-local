package com.landleaf.jjgj.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author Yang
 */
@Data
@Schema(description = "报表推送配置对象")
public class ReportPushConfigResponse {
    @Schema(description = "项目业务id")
    private String bizProjectId;

    @Schema(description = "周报推送（0禁用 1启用）")
    private Integer weekStatus;

    @Schema(description = "周报推送时间（1日 2一 3二 4三 5四 6五 7六）")
    private Integer weekPush;

    @Schema(description = "周报项目指标code")
    private List<String> weekCodes;

    @Schema(description = "周报推送用户")
    private List<Long> weekUserIds;

    @Schema(description = "月报推送（0禁用 1启用）")
    private Integer monthStatus;

    @Schema(description = "月报推送时间（1~31号）")
    private Integer monthPush;

    @Schema(description = "月报项目指标code")
    private List<String> monthCodes;

    @Schema(description = "月报推送用户")
    private List<Long> monthUserIds;
}
