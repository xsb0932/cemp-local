package com.landleaf.bms.domain.response;

import cn.hutool.extra.spring.SpringUtil;
import com.landleaf.bms.api.json.ValueDescription;
import com.landleaf.bms.util.ValueDescriptionUtil;
import com.landleaf.redis.constance.DictConstance;
import com.landleaf.redis.dict.DictUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 产品参数列表
 *
 * @author yue lin
 * @since 2023/6/25 15:15
 */
@Data
@Schema(description = "产品参数列表")
public class ProductParameterTabulationResponse {

    /**
     * id
     */
    @Schema(description = "id")
    private Long id;

    /**
     * 功能标识符
     */
    @Schema(description = "功能标识符")
    private String identifier;

    /**
     * 功能类别-（数据字典 PRODUCT_FUNCTION_CATEGORY）-产品参数列表
     */
    @Schema(description = "功能类别", example = "产品参数列表")
    private String functionCategory;

    /**
     * 功能名称
     */
    @Schema(description = "功能名称")
    private String functionName;

    /**
     * 功能类型（字典编码-PRODUCT_FUNCTION_TYPE）
     * <p>
     * 系统默认功能、系统可选功能、标准可选功能
     */
    @Schema(description = "功能类型（字典编码-PRODUCT_FUNCTION_TYPE）")
    private String functionType;

    /**
     * 功能类型-内容
     */
    @Schema(description = "功能类型-内容")
    private String functionTypeContent;

    /**
     * 数据类型（字典编码-PARAM_DATA_TYPE）
     */
    @Schema(description = "数据类型（字典编码-PARAM_DATA_TYPE）")
    private String dataType;

    /**
     * 数据类型-内容
     */
    @Schema(description = "数据类型-内容")
    private String dataTypeContent;

    /**
     * 值描述
     */
    @Schema(description = "值描述")
    private List<ValueDescription> valueDescription;

    /**
     * 值描述-内容
     */
    @Schema(description = "值描述-内容")
    private String valueDescriptionContent;

    /**
     * 单位（只有数据类型整形和浮点才有数据）
     */
    @Schema(description = "单位（只有数据类型整形和浮点才有数据）")
    private String unit;

    public ProductParameterTabulationResponse fill() {
        DictUtils dictUtils = SpringUtil.getBean(DictUtils.class);
        this.functionTypeContent = dictUtils.selectDictLabel(DictConstance.PRODUCT_FUNCTION_TYPE, this.functionType);
        this.dataTypeContent = dictUtils.selectDictLabel(DictConstance.PARAM_DATA_TYPE,this.dataType);
        this.functionTypeContent =dictUtils.selectDictLabel(DictConstance.PRODUCT_FUNCTION_TYPE, this.functionType);
        this.valueDescriptionContent = ValueDescriptionUtil.convertToString(this.dataType, this.valueDescription);
        this.unit = ValueDescriptionUtil.unitToString(this.valueDescription);
        return this;
    }

}
