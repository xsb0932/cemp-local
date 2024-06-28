package com.landleaf.energy.request;

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
public class DeviceDayRequest {

    /**
     * 设备业务ID
     */
    @Schema(description = "设备业务ID")
    @NotEmpty(message = "设备不能为空")
    private String[] deviceBizId;
    /**
     * 日期
     */
    @Schema(description = "日期")
    @NotNull(message = "日期不能为空")
    private LocalDate day;

}
