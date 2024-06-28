package com.landleaf.monitor.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 设备-监测-表格表头展示
 *
 * @author hebin
 * @since 2023-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_device_monitor_table_label")
public class DeviceMonitorTableLabelEntity extends TenantBaseEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 品类业务Id
     */
    @TableField("category_biz_id")
    private String categoryBizId;

    /**
     * 表头字段
     */
    @TableField("field_key")
    private String fieldKey;

    /**
     * 表头字段标签
     */
    @TableField("field_label")
    private String fieldLabel;

    /**
     * 表头字段是否展示
     */
    @TableField("field_show")
    private Boolean fieldShow;

    /**
     * 列显示顺序
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 宽度
     */
    @TableField("width")
    private Integer width;

}