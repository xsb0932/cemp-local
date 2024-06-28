package com.landleaf.lh.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "光字牌-新风项目VO")
public class LightBoardFreshAirProjectResponse {
    @Schema(description = "项目ID")
    private String bizProjectId;
    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "新风机列表")
    private List<LightBoardFreshAirResponse> freshAirList;
}
