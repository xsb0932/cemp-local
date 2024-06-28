package com.landleaf.bms.domain.request;

import com.landleaf.bms.domain.bo.Topic;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * GatewayAddRequest
 *
 * @author 张力方
 * @since 2023/8/15
 **/
@Data
public class GatewayAddRequest {
    /**
     * 协议类型 数据字典（GATEWAY_PROTOCOL_TYPE）
     */
    @NotBlank(message = "网关协议类型不能为空")
    @Schema(description = "协议类型 数据字典（GATEWAY_PROTOCOL_TYPE）", requiredMode = Schema.RequiredMode.REQUIRED, example = "01")
    private String protocolType;
    /**
     * 网关名称
     */
    @NotBlank(message = "网关名称不能为空")
    @Schema(description = "网关名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "123")
    private String name;
    /**
     * 项目业务id
     */
    @NotBlank(message = "项目业务id不能为空")
    @Schema(description = "项目业务id", requiredMode = Schema.RequiredMode.REQUIRED, example = "123")
    private String projectBizId;
    /**
     * 产品业务ids
     */
    @NotNull(message = "产品业务ids不能为空")
    @Schema(description = "产品业务ids", requiredMode = Schema.RequiredMode.REQUIRED, example = "[]")
    private List<String> productBizIds;
    /**
     * 网关描述
     */
    @Schema(description = "网关描述", example = "123")
    private String description;
    /**
     * client id
     */
    @Schema(description = "client id", example = "123")
    private String clientId;
    /**
     * 网关用户名
     */
    @NotBlank(message = "网关用户名不能为空")
    @Schema(description = "网关用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "123")
    private String username;
    /**
     * 网关密码
     */
    @NotBlank(message = "网关密码不能为空")
    @Schema(description = "网关密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123")
    private String password;
    /**
     * 上行主题
     */
    @Schema(description = "上行主题", example = "123")
    private List<Topic> upTopic;
    /**
     * 下行主题
     */
    @Schema(description = "下行主题", example = "123")
    private List<Topic> downTopic;

}
