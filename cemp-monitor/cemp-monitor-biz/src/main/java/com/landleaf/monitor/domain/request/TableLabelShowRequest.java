package com.landleaf.monitor.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 设备监控-表格表头显示变更
 *
 * @author yue lin
 * @since 2023/7/20 15:43
 */
@Data
@Schema(description = "设备监控-表格表头显示变更")
public class TableLabelShowRequest {

    /**
     * 品类业务ID
     */
    @NotBlank(message = "品类业务ID不能为空")
    @Schema(description = "品类业务编码")
    private String categoryBizId;

    /**
     * prop
     */
    @NotBlank(message = "prop不能为空")
    @Schema(description = "prop")
    private String prop;

    /**
     * 是否展示
     */
    @NotNull(message = "展示不能为空")
    @Schema(description = "show")
    private Boolean show;

}
