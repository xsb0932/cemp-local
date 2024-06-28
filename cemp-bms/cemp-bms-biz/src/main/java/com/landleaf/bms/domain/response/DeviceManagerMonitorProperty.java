package com.landleaf.bms.domain.response;

import com.landleaf.comm.base.bo.ValueDescription;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "参数")
public class DeviceManagerMonitorProperty {
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

    @Schema(description = "功能类型-内容")
    private String functionTypeContent;

    @Schema(description = "数据类型（字典编码-PARAM_DATA_TYPE）")
    private String dataType;

    @Schema(description = "数据类型-内容")
    private String dataTypeContent;

    @Schema(description = "值描述")
    private List<ValueDescription> valueDescription;

    @Schema(description = "单位（字典编码-UNIT）")
    private String unit;

    @Schema(description = "是否可读写（字典编码-RW_TYPE）")
    private String rw;

    @Schema(description = "是否可读写中文描述")
    private String rwContent;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "参数值")
    private String value;
}
