package com.landleaf.lh.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "光字牌-回路项目VO")
public class LightBoardCircuitProjectResponse {
    @Schema(description = "项目ID")
    private String bizProjectId;
    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "非热水回路")
    private List<LightBoardCircuitNormalResponse> normalList;

    @Schema(description = "热水一次/二次回路")
    private List<LightBoardCircuitHotWaterSpecialResponse> hotWaterList1;

    @Schema(description = "热水回路")
    private List<LightBoardCircuitHotWaterResponse> hotWaterList2;
}
