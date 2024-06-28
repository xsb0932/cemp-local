package com.landleaf.energy.domain.request;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "气表抄表分页查询对象")
public class GasMeterPageQueryRequest extends PageParam {
    @Schema(description = "抄表周期类型")
    @NotBlank(message = "抄表周期类型不能为空")
    private String meterReadCycle;

    @Schema(description = "设备业务id集合")
    private List<String> bizDeviceIds;

    @Schema(description = "开始时间")
    @NotBlank(message = "开始时间不能为空")
    private String start;

    @Schema(description = "结束时间")
    @NotBlank(message = "结束时间不能为空")
    private String end;
}
