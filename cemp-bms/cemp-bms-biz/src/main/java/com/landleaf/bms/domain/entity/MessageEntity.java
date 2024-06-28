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

import java.time.LocalDateTime;
import java.util.Date;
import java.sql.Timestamp;

import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * 消息信息表实体类
 *
 * @author hebin
 * @since 2023-11-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "MessageEntity", description = "消息信息表")
@TableName("tb_message")
public class MessageEntity extends BaseEntity{

/**
 * id
 */
        @Schema(description = "id")
            @TableId(type = IdType.AUTO)
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
 * 消息的内容
 */
        @Schema(description = "消息的内容")
        private String msgContent;

/**
 * 消息类型，数据字典（MSG_TYPE）
 */
        @Schema(description = "消息类型，数据字典（MSG_TYPE）")
        private String msgType;

/**
 * 消息状态：0：草稿，1：已发布
 */
        @Schema(description = "消息状态：0：草稿，1：已发布")
        private String msgStatus;

/**
 * 通知方式：第0位标识是否站内信，第1位标识是否邮件
 */
        @Schema(description = "通知方式：第0位标识是否站内信，第1位标识是否邮件")
        private Long noticeType;

/**
 * 已读人数，做个冗余
 */
        @Schema(description = "已读人数，做个冗余")
        private Long readCount;

/**
 * 发布者
 */
        @Schema(description = "发布者")
        private Long publisher;

/**
 * 发布时间
 */
        @Schema(description = "发布时间")
        private LocalDateTime publishTime;

/**
 * 租户id
 */
        @Schema(description = "租户id")
        private Long tenantId;
}