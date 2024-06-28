package com.landleaf.bms.domain.response;

import com.landleaf.bms.domain.bo.Topic;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * GatewayAddRequest
 *
 * @author 张力方
 * @since 2023/8/15
 **/
@Data
public class GatewayListResponse {
    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private Long id;
    /**
     * 网关业务id
     */
    @Schema(description = "网关业务id")
    private String bizId;
    /**
     * 网关名称
     */
    @Schema(description = "网关名称")
    private String name;
    /**
     * 项目业务id
     */
    @Schema(description = "项目业务id")
    private String projectBizId;
    /**
     * 项目名称
     */
    @Schema(description = "项目名称")
    private String projectBizName;
    /**
     * 产品业务ids
     */
    @Schema(description = "产品业务ids")
    private List<String> productBizIds;
    /**
     * 产品名称
     */
    @Schema(description = "产品名称")
    private List<String> productNames;
    /**
     * 网关描述
     */
    @Schema(description = "网关描述")
    private String description;
    /**
     * client id
     */
    @Schema(description = "client id")
    private String clientId;
    /**
     * 网关用户名
     */
    @Schema(description = "网关用户名")
    private String username;
    /**
     * 网关密码
     */
    @Schema(description = "网关密码")
    private String password;
    /**
     * 网关状态 数据字典（GATEWAY_STATUS）
     */
    @Schema(description = "网关状态 数据字典（GATEWAY_STATUS）")
    private String status;
    /**
     * 网关状态 名称
     */
    @Schema(description = "网关状态 名称")
    private String statusName;
    /**
     * 协议类型 数据字典（GATEWAY_PROTOCOL_TYPE）
     */
    @Schema(description = "协议类型 数据字典（GATEWAY_PROTOCOL_TYPE）")
    private String protocolType;
    /**
     * 网关状态 名称
     */
    @Schema(description = "协议类型 名称")
    private String protocolTypeName;
    /**
     * 上行主题
     */
    @Schema(description = "上行主题")
    private List<Topic> upTopic;
    /**
     * 下行主题
     */
    @Schema(description = "下行主题")
    private List<Topic> downTopic;

}
