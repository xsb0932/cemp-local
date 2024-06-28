package com.landleaf.bms.domain.response;

import cn.hutool.extra.spring.SpringUtil;
import com.landleaf.bms.api.json.FunctionParameter;
import com.landleaf.bms.util.FunctionParameterUtil;
import com.landleaf.redis.constance.DictConstance;
import com.landleaf.redis.dict.DictUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 设备服务列表
 *
 * @author yue lin
 * @since 2023/6/27 16:13
 */
@Data
public class DeviceServiceTabulationResponse {

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
     * 功能类别-（数据字典 PRODUCT_FUNCTION_CATEGORY）-设备属性
     */
    @Schema(description = "功能类别", example = "设备属性")
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
     * 服务参数
     */
    @Schema(description = "服务参数")
    private List<FunctionParameter> functionParameter;

    /**
     * 服务参数-内容
     */
    @Schema(description = "服务参数-内容")
    private String functionParameterContent;

    /**
     * 响应参数
     */
    @Schema(description = "响应参数")
    private List<FunctionParameter> responseParameter;

    /**
     * 响应参数-内容
     */
    @Schema(description = "响应参数-内容")
    private String responseParameterContent;

    public DeviceServiceTabulationResponse fill() {
        DictUtils dictUtils = SpringUtil.getBean(DictUtils.class);
        this.functionTypeContent = dictUtils.selectDictLabel(DictConstance.PRODUCT_FUNCTION_TYPE, this.functionType);
        this.functionParameterContent = FunctionParameterUtil.convertToString(this.functionParameter);
        this.responseParameterContent = FunctionParameterUtil.convertToString(this.responseParameter);
        return this;
    }

}
