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
 * 实体类
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "ProjectSubareaDeviceEntity对象", description = "ProjectSubareaDeviceEntity对象")
@TableName("tb_project_subarea_device")
public class ProjectSubareaDeviceEntity extends BaseEntity {

    /**
     * id
     */
    @Schema(description = "id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分区ID
     */
    @Schema(description = "分区ID")
    private Long subareadId;

    /**
     * 设备ID
     */
    @Schema(description = "设备ID")
    private String deviceId;

    /**
     * 计算标志位1,-1
     */
    @Schema(description = "计算标志位1,-1")
    private String computeTag;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private Long tenantId;
}
