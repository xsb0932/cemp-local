package com.landleaf.bms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.landleaf.bms.domain.bo.Topic;
import com.landleaf.bms.handler.TopicListTypeHandler;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 网关表
 *
 * @author 张力方
 * @since 2023/8/15
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "tb_gateway")
public class GatewayEntity extends TenantBaseEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 网关业务id
     */
    private String bizId;
    /**
     * 网关名称
     */
    private String name;
    /**
     * 项目业务id
     */
    private String projectBizId;
    /**
     * 产品业务ids
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> productBizIds;
    /**
     * 网关描述
     */
    private String description;
    /**
     * 网关状态 数据字典（GATEWAY_STATUS）
     */
    private String status;
    /**
     * client id
     */
    private String clientId;
    /**
     * 网关用户名
     */
    private String username;
    /**
     * 网关密码
     */
    private String password;
    /**
     * 协议类型 数据字典（GATEWAY_PROTOCOL_TYPE）
     */
    private String protocolType;
    /**
     * 上行主题
     */
    @TableField(typeHandler = TopicListTypeHandler.class)
    private List<Topic> upTopic;
    /**
     * 下行主题
     */
    @TableField(typeHandler = TopicListTypeHandler.class)
    private List<Topic> downTopic;

}

