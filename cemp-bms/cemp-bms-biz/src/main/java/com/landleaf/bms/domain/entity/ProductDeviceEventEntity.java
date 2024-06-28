package com.landleaf.bms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.bms.api.json.FunctionParameter;
import com.landleaf.bms.handler.FunctionParameterListTypeHandler;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.Data;

import java.util.List;

/**
 * 产品-设备事件
 *
 * @author 张力方
 * @since 2023/7/3
 **/
@Data
@TableName(value = "tb_product_device_event", autoResultMap = true)
public class ProductDeviceEventEntity extends TenantBaseEntity {

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
     * 功能类别-（数据字典 PRODUCT_FUNCTION_CATEGORY）-设备事件
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
     * 事件参数
     * 可由多个参数构成。每个参数，由字段标识符、名称、数据类型、单位、值描述组成、。参数字段的标识符只需要在参数内做标识符唯一校验。
     */
    @TableField(value = "event_parameter", typeHandler = FunctionParameterListTypeHandler.class)
    private List<FunctionParameter> eventParameter;

    /**
     * 响应参数
     */
    @TableField(value = "response_parameter", typeHandler = FunctionParameterListTypeHandler.class)
    private List<FunctionParameter> responseParameter;
}
