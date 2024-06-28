package com.landleaf.bms.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;

/**
 * 消息读取信息表的展示信息封装
 *
 * @author hebin
 * @since 2023-11-27
 */
@Data
@Schema(name = "MessageReceiveVO", description = "消息读取信息表的展示信息封装")
public class MessageReceiveVO {

    /**
     * id
     */
    @Schema(description = "id")
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
     * 发送给对应的tenantId
     */
    @Schema(description = "发送给对应的tenant名称")
    private String targetTenantName;

    /**
     * 消发送给对应userId
     */
    @Schema(description = "消发送给对应userId")
    private Long targetUserId;
    /**
     * 消发送给对应userId
     */
    @Schema(description = "消发送给对应user名称")
    private String targetUserName;

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