package com.landleaf.bms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.landleaf.comm.base.pojo.PageParam;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;

/**
 * 消息读取信息表的查询时的参数封装
 *
 * @author hebin
 * @since 2023-11-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "MessageReceiveQueryDTO", description = "消息读取信息表的查询时的参数封装")
public class MessageReceiveQueryDTO extends PageParam{

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

/**
 * 开始时间
 */
@Schema(name = "开始时间,格式为yyyy-MM-dd")
private String startTime;

/**
 * 结束时间
 */
@Schema(name = "结束时间,格式为yyyy-MM-dd")
private String endTime;
        }