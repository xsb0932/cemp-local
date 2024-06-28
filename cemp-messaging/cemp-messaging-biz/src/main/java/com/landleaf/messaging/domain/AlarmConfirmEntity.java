package com.landleaf.messaging.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 告警确认表
 *
 * @author 张力方
 * @since 2023/8/14
 **/
@Data
@TableName(value = "tb_alarm_confirm")
public class AlarmConfirmEntity extends TenantBaseEntity {

    /**
     * id
     */
    @Schema(description = "id")
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 事件id
     */
    private String eventId;
    /**
     * 告警确认方式 数据字典（ALARM_CONFIRM_TYPE）
     */
    private String alarmConfirmType;
    /**
     * 确认状态，是否确认，true
     */
    private Boolean isConfirm;
    /**
     * 确认人 自动确认事件，sys，其他用户id
     */
    private Long confirmUser;
    /**
     * 确认时间
     */
    private LocalDateTime confirmTime;
    /**
     * 备注
     */
    private String remark;
}
