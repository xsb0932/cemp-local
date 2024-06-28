package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 字典数据排序请求
 *
 * @author 张力方
 * @since 2023/6/5
 **/
@Data
@Schema(name = "字典数据排序请求参数", description = "字典数据排序请求参数")
public class DictDataSortRequest {
    /**
     * 当前拖动字典数据id
     */
    @NotNull(message = "当前拖动字典数据id不能为空")
    @Schema(description = "当前拖动字典数据id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long dictDataId;
    /**
     * 当前字典数据下一个字典数据
     * <p>
     * 为空则表示拖到了最后
     */
    @Schema(description = "当前字典数据下一个字典数据", example = "2")
    private Long nextDictDataId;

}
