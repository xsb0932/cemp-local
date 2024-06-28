package com.landleaf.energy.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "项目-水表树")
public class WaterMeterTreeDTO {
    @Schema(description = "业务id")
    private String id;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "类型：0项目 1水表")
    private Integer type;
    @Schema(description = "子节点")
    private List<WaterMeterTreeDTO> children;
}
