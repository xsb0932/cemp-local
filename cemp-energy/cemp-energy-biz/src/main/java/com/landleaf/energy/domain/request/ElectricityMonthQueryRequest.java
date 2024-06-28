package com.landleaf.energy.domain.request;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.YearMonth;
import java.util.List;

/**
 * 抄表日查询参数
 *
 * @author Tycoon
 * @since 2023/8/17 15:20
 **/
@Data
@Schema(description = "抄表日查询参数")
public class ElectricityMonthQueryRequest extends PageParam {

    /**
     * 开始月(yyyy-MM)
     */
    @NotNull(message = "开始月不能为空")
    @Schema(description = "开始月(yyyy-MM)", format = "yyyy-MM")
    private YearMonth startData;

    /**
     * 结束月(yyyy-MM)
     */
    @NotNull(message = "结束月不能为空")
    @Schema(description = "结束月(yyyy-MM)", format = "yyyy-MM")
    private YearMonth endData;

    /**
     * 设备业务Ids
     */
    @NotEmpty(message = "设备不能为空")
    @Schema(description = "设备业务Ids")
    private List<String> deviceBizIds;

}
