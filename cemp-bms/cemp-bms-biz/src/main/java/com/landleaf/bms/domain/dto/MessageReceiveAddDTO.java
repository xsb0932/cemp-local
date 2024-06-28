package com.landleaf.bms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;

/**
 * 消息读取信息表的新增时的参数封装
 *
 * @author hebin
 * @since 2023-11-27
 */
@Data
@Schema(name = "MessageReceiveAddDTO", description = "消息读取信息表的新增时的参数封装")
public class MessageReceiveAddDTO {

/**
 * id
 */
        @Schema(description = "id")
            @NotNull(groups = {UpdateGroup.class}, message = "id不能为空")
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

public interface AddGroup {
}

public interface UpdateGroup {
}
}