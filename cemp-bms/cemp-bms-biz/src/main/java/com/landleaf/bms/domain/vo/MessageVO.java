package com.landleaf.bms.domain.vo;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.landleaf.bms.api.dto.MsgNoticeUserDTO;
import com.landleaf.comm.util.date.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;
import java.util.List;

/**
 * 消息信息表的展示信息封装
 *
 * @author hebin
 * @since 2023-11-27
 */
@Data
@Schema(name = "MessageVO", description = "消息信息表的展示信息封装")
public class MessageVO {

    /**
     * id
     */
    @Schema(description = "id")
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

    @Schema(description = "是否站内信：0否；1是")
    private Integer mailFlag;

    @Schema(description = "是否邮件：0否；1是")
    private Integer emailFlag;

    public Integer getMailFlag() {
        return (int) (noticeType & 1);
    }

    public Integer getEmailFlag() {
        return (int) (noticeType & 2) >> 1;
    }

    /**
     * 已读人数，做个冗余
     */
    @Schema(description = "已读人数，做个冗余")
    private Long readCount;

    /**
     * 发布时间
     */
    @Schema(description = "发布时间")
    private Timestamp publishTime;

    /**
     * 发布时间
     */
    @Schema(description = "发布时间字符串")
    private String publishTimeStr;

    /**
     * 租户id
     */
    @Schema(description = "租户id")
    private Long tenantId;

    /**
     * 租户名
     */
    @Schema(description = "租户名")
    private String tenantName;


    @Schema(description = "用户编号")
    private Long userId;

    @Schema(description = "用户名")
    private String userName;

    /**
     * 通知的用户信息
     */
    @Schema(description = "通知的用户信息")
    List<MsgNoticeUserDTO> noticeUserInfo;

    public String getPublishTimeStr() {
        if ("02".equals(msgStatus)) {
            // 只有已发布，才有事件
            return DateUtil.format(publishTime, "yyyy-MM-dd HH:mm:ss");
        }
        return null;
    }
}