package com.landleaf.bms.domain.request;

import com.landleaf.bms.domain.enums.DictStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 编辑字典数据
 *
 * @author 张力方
 * @since 2023/6/15
 **/
@Data
@Schema(name = "编辑字典数据请求参数", description = "编辑字典数据请求参数")
public class DictDataEditRequest {
    /**
     * 字典数据id（码值id）
     */
    @NotNull(message = "字典数据id不能为空")
    @Schema(description = "字典数据id（码值id）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    /**
     * 字典数据码值
     */
    @NotNull(message = "字典数据码值不能为空")
    @Size(min = 1, max = 50, message = "字典数据码值长度区间{min}-{max}")
    @Schema(description = "字典数据码值", requiredMode = Schema.RequiredMode.REQUIRED, example = "01")
    private String value;

    /**
     * 字典数据中文描述
     */
    @NotNull(message = "字典数据中文描述不能为空")
    @Size(min = 1, max = 50, message = "字典数据中文描述长度区间{min}-{max}")
    @Schema(description = "字典数据中文描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "项目")
    private String label;

    /**
     * 字典数据状态 （0 正常 1 失效）{@link DictStatusEnum}
     */
    @NotNull(message = "字典数据状态不能为空")
    @Schema(description = "字典数据状态 （0 正常 1 失效）", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer status;

}
