package com.landleaf.bms.domain.response;

import com.landleaf.bms.domain.entity.DictDataEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字典数据-选择框返回
 *
 * @author yue lin
 * @since 2023/6/16 17:34
 */
@Data
@Schema(name = "字典数据-选择框返回参数", description = "字典数据-选择框返回参数")
public class DictDataSelectiveResponse {

    /**
     * id
     */
    @Schema(description = "id", example = "1")
    private Long id;

    /**
     * 编码
     */
    @Schema(description = "编码", example = "zzzxx1")
    private String value;

    /**
     * 描述
     */
    @Schema(description = "描述", example = "test")
    private String label;

    public static DictDataSelectiveResponse from(DictDataEntity entity) {
        DictDataSelectiveResponse response = new DictDataSelectiveResponse();
        response.setId(entity.getId());
        response.setValue(entity.getValue());
        response.setLabel(entity.getLabel());
        return response;
    }

}
