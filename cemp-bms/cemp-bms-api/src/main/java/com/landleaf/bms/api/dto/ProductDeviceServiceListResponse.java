package com.landleaf.bms.api.dto;

import com.landleaf.bms.api.json.FunctionParameter;
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
public class ProductDeviceServiceListResponse {

    /**
     * id
     */
    @Schema(description = "id")
    private Long id;

    /**
     * 产品id
     */
    @Schema(description = "产品id", example = "wxxxx001")
    private Long productId;

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

}
