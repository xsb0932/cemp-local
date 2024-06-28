package com.landleaf.comm.base.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "事件、服务参数")
public class FunctionParameter {
    @Schema(description = "字段标识符")
    private String identifier;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "数据类型")
    private String dataType;
    @Schema(description = "值类型")
    private List<ValueDescription> valueDescription;
}
