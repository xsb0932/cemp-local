package com.landleaf.bms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 网关js处理脚本
 *
 * @author 张力方
 * @since 2023/8/17
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "tb_gateway_js")
public class GatewayJsEntity extends TenantBaseEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 网关业务id
     */
    private String gatewayBizId;
    /**
     * 上行数据处理脚本
     */
    private String upJs;
    /**
     * 下行数据处理脚本
     */
    private String downJs;
    /**
     * 上次上行模拟运行输入
     */
    private String lastUpPayload;
    /**
     * 上次上行模拟运行状态（系统字典 01：成功 02：失败）
     */
    private String lastUpStatus;
    /**
     * 上次上行模拟运行结果
     */
    private String lastUpResult;
    /**
     * 上次上行模拟运行时间
     */
    private LocalDateTime lastUpRuntime;
    /**
     * 上次下行模拟运行输入
     */
    private String lastDownPayload;
    /**
     * 上次下行模拟运行状态（系统字典 01：成功 02：失败）
     */
    private String lastDownStatus;
    /**
     * 上次下行模拟运行结果
     */
    private String lastDownResult;
    /**
     * 上次下行模拟运行时间
     */
    private LocalDateTime lastDownRuntime;
}
