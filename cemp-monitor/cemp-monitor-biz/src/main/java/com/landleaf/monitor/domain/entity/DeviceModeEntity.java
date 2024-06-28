package com.landleaf.monitor.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Value;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.BaseEntity;

import java.math.BigDecimal;

import java.util.Date;
import java.sql.Timestamp;

import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * 实体类
 *
 * @author hebin
 * @since 2023-09-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "DeviceModeEntity", description = "DeviceModeEntity对象")
@TableName("jjgj_device_mode")
public class DeviceModeEntity extends BaseEntity{

/**
 * id
 */
        @Schema(description = "id")
            @TableId(type = IdType.AUTO)
    private Long id;

/**
 * 设备业务id
 */
        @Schema(description = "设备业务id")
        private String bizDeviceId;

/**
 * 模式代码 1 手动模式 2 夏季模式 3冬季模式
 */
        @Schema(description = "模式代码 1 手动模式 2 夏季模式 3冬季模式")
        private String modeCode;

/**
 * 模式说明
 */
        @Schema(description = "模式说明")
        private String modeDesc;

/**
 * 租户id
 */
        @Schema(description = "租户id")
        private Integer tenantId;
}
