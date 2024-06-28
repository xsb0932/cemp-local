package com.landleaf.bms.api.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * ProductDeviceAttr
 *
 * @author 徐世白
 * @since 2023/10/9
 **/
@Data
public class ProductDeviceParameterResponse {


    /**
     * 产品id
     */
    private Long productId;

    /**
     * 功能标识符
     */
    @TableField(value = "identifier")
    private String identifier;

    /**
     * 功能类别-（数据字典 PRODUCT_FUNCTION_CATEGORY）-设备参数
     */
    @TableField(value = "function_category")
    private String functionCategory;

    /**
     * 功能名称
     */
    @TableField(value = "function_name")
    private String functionName;

    /**
     * 功能类型
     * 系统默认功能、系统可选功能、标准可选功能
     */
    @TableField(value = "function_type")
    private String functionType;

    /**
     * 数据类型
     */
    @TableField(value = "data_type")
    private String dataType;


}
