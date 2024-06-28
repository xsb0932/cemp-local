package com.landleaf.monitor.domain.vo;

import com.landleaf.bms.api.dto.ProjectBizCategoryResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 项目-品类
 *
 * @author xshibai
 */
@Data
@Schema(name = "项目品类VO", description = "项目品类VO")
public class ProjectBizCategoryVO {
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

    public static ProjectBizCategoryVO from(ProjectBizCategoryResponse response) {
        ProjectBizCategoryVO vo = new ProjectBizCategoryVO();
        vo.setCategoryId(response.getCategoryId());
        vo.setCategoryName(response.getCategoryName());
        vo.setCategoryCode(response.getCategoryCode());
        return vo;
    }

}
