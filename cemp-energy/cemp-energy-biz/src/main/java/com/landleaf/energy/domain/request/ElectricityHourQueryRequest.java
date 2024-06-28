package com.landleaf.energy.domain.request;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 抄表日查询参数
 *
 * @author Tycoon
 * @since 2023/8/17 15:20
 **/
@Data
@Schema(description = "电表抄表时查询参数")
public class ElectricityHourQueryRequest extends PageParam {

    @NotBlank(message = "开始时间不能为空")
    @Schema(description = "开始时间")
    private String start;

    @NotBlank(message = "结束时间不能为空")
    @Schema(description = "结束时间")
    private String end;

    /**
     * 设备业务Ids
     */
    @NotEmpty(message = "设备不能为空")
    @Schema(description = "设备业务Ids")
    private List<String> bizDeviceIds;

}
