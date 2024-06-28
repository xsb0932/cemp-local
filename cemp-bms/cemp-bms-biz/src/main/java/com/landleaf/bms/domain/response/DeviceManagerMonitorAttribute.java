package com.landleaf.bms.domain.response;

import com.landleaf.comm.base.bo.ValueDescription;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

import static com.landleaf.bms.constance.ValueConstance.*;

@Data
@Schema(description = "属性")
public class DeviceManagerMonitorAttribute {
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

    @Schema(description = "参数值")
    private String value;

    @Schema(description = "历史时间轴")
    private List<String> times;

    @Schema(description = "历史数据轴")
    private List<String> values;

    public int getFirstSort() {
        return switch (this.dataType) {
            case INTEGER, DOUBLE -> 0;
            case STRING, BOOLEAN, ENUMERATE -> 1;
            default -> 99;
        };
    }

    public Long getSecondSort() {
        return id;
    }
}
