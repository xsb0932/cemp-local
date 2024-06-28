package com.landleaf.energy.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * 设备参数明细表实体类
 *
 * @author hebin
 * @since 2023-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "DeviceParameterEntity对象", description = "设备参数明细表")
@TableName("tb_device_parameter")
public class DeviceParameterDetailEntity extends BaseEntity {

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private Long tenantId;

    /**
     * ID
     */
    @Schema(description = "ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 产品ID
     */
    @Schema(description = "产品ID")
    private Long productId;

    /**
     * 功能标识符
     */
    @Schema(description = "功能标识符")
    private String identifier;

    /**
     * 功能类型
     * 系统默认功能、系统可选功能、标准可选功能
     */
    @Schema(description = "功能类型* 系统默认功能、系统可选功能、标准可选功能")
    private String functionName;

    /**
     * 参数值
     */
    @Schema(description = "参数值")
    private String value;

    /**
     * 设备id
     */
    @Schema(description = "设备id")
    private String bizDeviceId;
}
