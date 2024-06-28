package com.landleaf.bms.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;
import java.util.List;

/**
 * 消息信息表的新增时的参数封装
 *
 * @author hebin
 * @since 2023-11-27
 */
@Data
@Schema(name = "MessageAddRequest", description = "消息信息表的新增时的参数封装")
public class MessageAddRequest {

    /**
     * id
     */
    @Schema(description = "id")
    @NotNull(groups = {UpdateGroup.class}, message = "id不能为空")
    private Long id;

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
     * 消息状态：01：草稿，02：已发布
     */
    @Schema(description = "消息状态：0：草稿，1：已发布")
    private String msgStatus;

    @Schema(description = "是否站内信：0否；1是")
    private Integer mailFlag;

    @Schema(description = "是否邮件：0否；1是")
    private Integer emailFlag;

    @Schema(description = "推送的信息")
    private List<MsgNoticeUserDTO> noticeUserInfo;

    @Schema(description = "当前租户编号，页面入参时，传空即可")
    private Long tenantId;

    public interface AddGroup {
    }

    public interface UpdateGroup {
    }
}