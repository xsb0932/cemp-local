package com.landleaf.jjgj.domain.entity;

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
@Schema(name = "ProjectSubitemDeviceEntity对象", description = "ProjectSubitemDeviceEntity对象")
@TableName("tb_project_subitem_device")
public class ProjectSubitemDeviceEntity extends BaseEntity {

    /**
     * id
     */
    @Schema(description = "id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分项ID
     */
    @Schema(description = "分项ID")
    private Long subitemId;

    /**
     * 设备ID
     */
    @Schema(description = "设备ID")
    private String deviceId;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 计算标志位1,-1
     */
    @Schema(description = "计算标志位1,-1")
    private String computeTag;

    /**
     * 租户id
     */
    @Schema(description = "租户id")
    private Long tenantId;
}
