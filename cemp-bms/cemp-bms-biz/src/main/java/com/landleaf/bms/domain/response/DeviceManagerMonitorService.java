package com.landleaf.bms.domain.response;

import com.landleaf.comm.base.bo.FunctionParameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "设备控制服务")
public class DeviceManagerMonitorService {
    @Schema(description = "id")
    private Long id;

    @Schema(description = "产品id")
    private Long productId;

    @Schema(description = "功能标识符")
    private String identifier;

    @Schema(description = "功能类别")
    private String functionCategory;

    @Schema(description = "功能名称")
    private String functionName;

    @Schema(description = "功能类型（字典编码-PRODUCT_FUNCTION_TYPE）")
    private String functionType;

    @Schema(description = "功能类型名称")
    private String functionTypeContent;

    @Schema(description = "服务参数")
    private List<FunctionParameter> functionParameter;

    @Schema(description = "响应参数")
    private List<FunctionParameter> responseParameter;

    @Schema(description = "控制参数")
    private List<FunctionParameter> controlPointDesc;
}
