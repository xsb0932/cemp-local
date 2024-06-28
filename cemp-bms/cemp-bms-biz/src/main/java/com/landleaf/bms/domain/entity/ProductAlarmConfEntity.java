package com.landleaf.bms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.Data;

/**
 * 产品告警配置
 *
 * @author 张力方
 * @since 2023/8/10
 **/
@Data
@TableName(value = "tb_product_alarm_conf")
public class ProductAlarmConfEntity extends TenantBaseEntity {
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
     * 告警CODE
     */
    private String alarmCode;
    /**
     * 告警类型 数据字典（ALARM_TYPE）
     */
    private String alarmType;
    /**
     * 告警描述
     */
    private String alarmDesc;
    /**
     * 告警触发等级 数据字典（ALARM_LEVEL）
     */
    private String alarmTriggerLevel;
    /**
     * 告警复归等级 数据字典（ALARM_LEVEL）
     */
    private String alarmRelapseLevel;
    /**
     * 告警确认方式 数据字典（ALARM_CONFIRM_TYPE）
     */
    private String alarmConfirmType;
    /**
     * 是否是默认值，默认值不可修改
     */
    private Boolean isDefault;
}
