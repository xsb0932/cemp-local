package com.landleaf.bms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.bms.api.json.ValueDescription;
import com.landleaf.bms.handler.ValueDescriptionListTypeHandler;
import com.landleaf.pgsql.base.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * 设备参数实体类
 *
 * @author yue lin
 * @since 2023/6/25 10:03
 */
@Data
@TableName(value = "tb_category_management_device_parameter", autoResultMap = true)
public class CategoryDeviceParameterEntity extends BaseEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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

    /**
     * 品类ID
     */
    @TableField(value = "category_id")
    private Long categoryId;

}
