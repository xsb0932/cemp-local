package com.landleaf.jjgj.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.BaseEntity;
import com.landleaf.pgsql.handler.type.LongListTypeHandler;
import com.landleaf.pgsql.handler.type.StringListTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;


/**
 * 锦江报表推送配置实体类
 *
 * @author hebin
 * @since 2023-11-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "JjgjReportPushEntity", description = "锦江报表推送配置")
@TableName("jjgj_report_push")
public class JjgjReportPushEntity extends BaseEntity {

    /**
     * id
     */
    @Schema(description = "id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 项目id
     */
    @Schema(description = "项目id")
    private String bizProjectId;

    /**
     * 周报推送（0禁用 1启用）
     */
    @Schema(description = "周报推送（0禁用 1启用）")
    private Integer weekStatus;

    /**
     * 周报推送时间（1日 2一 3二 4三 5四 6五 7六）
     */
    @Schema(description = "周报推送时间（1日 2一 3二 4三 5四 6五 7六）")
    private Integer weekPush;

    /**
     * 周报项目指标code
     */
    @Schema(description = "周报项目指标code")
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> weekCodes;

    /**
     * 周报推送用户
     */
    @Schema(description = "周报推送用户")
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> weekUserIds;

    /**
     * 月报推送（0禁用 1启用）
     */
    @Schema(description = "月报推送（0禁用 1启用）")
    private Integer monthStatus;

    /**
     * 月报推送时间（1~31号）
     */
    @Schema(description = "月报推送时间（1~31号）")
    private Integer monthPush;

    /**
     * 月报项目指标code
     */
    @Schema(description = "月报项目指标code")
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> monthCodes;

    /**
     * 月报推送用户
     */
    @Schema(description = "月报推送用户")
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> monthUserIds;

    /**
     * 租户id
     */
    @Schema(description = "租户id")
    private Integer tenantId;
}