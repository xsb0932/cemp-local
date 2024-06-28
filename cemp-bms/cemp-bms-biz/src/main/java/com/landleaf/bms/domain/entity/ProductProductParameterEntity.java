package com.landleaf.bms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.bms.api.json.ValueDescription;
import com.landleaf.bms.handler.ValueDescriptionListTypeHandler;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.Data;

import java.util.List;

/**
 * 产品-产品参数
 *
 * @author 张力方
 * @since 2023/7/3
 **/
@Data
@TableName(value = "tb_product_product_parameter", autoResultMap = true)
public class ProductProductParameterEntity extends TenantBaseEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 功能标识符
     */
    private String identifier;

    /**
     * 功能类别-（数据字典 PRODUCT_FUNCTION_CATEGORY）-产品参数
     */
    private String functionCategory;

    /**
     * 功能名称
     */
    private String functionName;

    /**
     * 功能类型
     * 系统默认功能、系统可选功能、标准可选功能
     */
    private String functionType;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 值描述
     */
    @TableField(typeHandler = ValueDescriptionListTypeHandler.class)
    private List<ValueDescription> valueDescription;

    /**
     * 值
     */
    private String value;

}
