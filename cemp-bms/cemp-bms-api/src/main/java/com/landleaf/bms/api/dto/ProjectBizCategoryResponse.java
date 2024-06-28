package com.landleaf.bms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 返回项目下品类基础信息
 *
 * @author yue lin
 * @since 2023/7/20 9:25
 */
@Data
@Schema(name = "项目品类VO", description = "项目品类VO")
public class ProjectBizCategoryResponse {

    /**
     * 品类id
     */
    @Schema(description = "品类id")
    private String categoryId;

    /**
     * 品类名称
     */
    @Schema(description = "品类名称")
    private String categoryName;

    /**
     * 品类code
     */
    @Schema(description = "品类code")
    private String categoryCode;

}
