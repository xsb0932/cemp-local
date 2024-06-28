package com.landleaf.energy.request;

import com.landleaf.energy.enums.SubitemIndexEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 分项指标-日查询
 *
 * @author yue lin
 * @since 2023/7/28 9:34
 */
@Data
@Schema(description = "分项指标-日查询")
public class IndexDayRequest {

    /**
     * 项目业务ID
     */
    @Schema(description = "项目业务ID")
    @NotNull(message = "项目不能为空")
    private String projectBizId;
    /**
     * 日期
     */
    @Schema(description = "日期")
    @NotEmpty(message = "日期不能为空")
    private LocalDate[] days;
    /**
     * 指标
     */
    @Schema(description = "指标")
    @NotEmpty(message = "指标不能为空")
    private SubitemIndexEnum[] indices;

}
