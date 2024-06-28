package com.landleaf.monitor.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "设备监控-表格表头宽度变更")
public class TableLabelWidthRequest {
    @NotBlank(message = "品类业务ID不能为空")
    @Schema(description = "品类业务编码")
    private String categoryBizId;

    @NotBlank(message = "prop不能为空")
    @Schema(description = "prop")
    private String prop;

    @NotNull(message = "width不能为空")
    @Schema(description = "width")
    private Integer width;
}
