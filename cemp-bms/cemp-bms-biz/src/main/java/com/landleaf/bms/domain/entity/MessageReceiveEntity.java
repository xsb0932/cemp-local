package com.landleaf.bms.domain.entity;

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
 * 消息读取信息表实体类
 *
 * @author hebin
 * @since 2023-11-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "MessageReceiveEntity", description = "消息读取信息表")
@TableName("tb_message_receive")
public class MessageReceiveEntity extends BaseEntity {

    /**
     * id
     */
    @Schema(description = "id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 消息的Id
     */
    @Schema(description = "消息的Id")
    private Long msgId;

    /**
     * 消息的bizId
     */
    @Schema(description = "消息的bizId")
    private String bizMsgId;

    /**
     * 发送给对应的tenantId
     */
    @Schema(description = "发送给对应的tenantId")
    private Long targetTenantId;

    /**
     * 消发送给对应userId
     */
    @Schema(description = "消发送给对应userId")
    private Long targetUserId;

    /**
     * 是否已读：0未读；1已读
     */
    @Schema(description = "是否已读：0未读；1已读")
    private Long readFlag;

    /**
     * 接收方式：未接收为0，否则为对应的接收方式。
     */
    @Schema(description = "接收方式：未接收为0，否则为对应的接收方式。")
    private Long receiveType;

    /**
     * 租户id
     */
    @Schema(description = "租户id")
    private Long tenantId;
}