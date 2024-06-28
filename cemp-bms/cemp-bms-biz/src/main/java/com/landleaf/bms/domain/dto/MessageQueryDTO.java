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
 * 消息信息表的查询时的参数封装
 *
 * @author hebin
 * @since 2023-11-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "MessageQueryDTO", description = "消息信息表的查询时的参数封装")
public class MessageQueryDTO extends PageParam {

    @Schema(description = "消息的Id")
    private Long id;

    /**
     * 消息的bizId
     */
    @Schema(description = "消息的bizId")
    private String bizMsgId;

    /**
     * 消息的标题
     */
    @Schema(description = "消息的标题")
    private String msgTitle;

    /**
     * 消息类型，数据字典（MSG_TYPE）
     */
    @Schema(description = "消息类型，数据字典（MSG_TYPE）")
    private String msgType;

    /**
     * 消息状态：0：草稿，1：已发布
     */
    @Schema(description = "消息状态：01：草稿，02：已发布")
    private String msgStatus;

    /**
     * 租户id
     */
    @Schema(description = "租户id")
    private Long tenantId;
}