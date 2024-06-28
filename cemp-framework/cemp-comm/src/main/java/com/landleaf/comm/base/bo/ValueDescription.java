package com.landleaf.comm.base.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "值描述JSON数据格式")
public class ValueDescription {
    @Schema(description = "key值")
    private String key;
    @Schema(description = "value值")
    private String value;
}
