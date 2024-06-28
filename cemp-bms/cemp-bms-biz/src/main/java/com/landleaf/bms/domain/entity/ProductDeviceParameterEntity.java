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
 * 产品-设备参数
 *
 * @author 张力方
 * @since 2023/7/3
 **/
@Data
@TableName(value = "tb_product_device_parameter", autoResultMap = true)
public class ProductDeviceParameterEntity extends TenantBaseEntity {

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

    /**
     * 值描述
     */
    @TableField(value = "value_description", typeHandler = ValueDescriptionListTypeHandler.class)
    private List<ValueDescription> valueDescription;

    /**
     * 是否可读写（字典编码-RW_TYPE）
     */
    @TableField(value = "rw")
    private String rw;
}
