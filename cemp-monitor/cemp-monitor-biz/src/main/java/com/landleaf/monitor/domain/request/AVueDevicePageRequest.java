package com.landleaf.monitor.domain.request;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "avue选择设备分页查询对象")
public class AVueDevicePageRequest extends PageParam {
    @Schema(description = "AVue的viewId")
    @NotBlank
    private String viewId;

    @Schema(description = "品类名称")
    private String categoryName;

    @Schema(description = "设备名称")
    private String name;

    @Schema(description = "设备编码")
    private String code;
}
