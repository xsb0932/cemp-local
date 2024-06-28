package com.landleaf.jjgj.domain.entity;

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

import java.util.Date;
import java.sql.Timestamp;

import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * 实体类
 *
 * @author hebin
 * @since 2023-09-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "CheckinMonthEntity", description = "CheckinMonthEntity对象")
@TableName("jjgj_checkin_month")
public class CheckinMonthEntity extends BaseEntity{

/**
 * id
 */
        @Schema(description = "id")
            @TableId(type = IdType.AUTO)
    private Integer id;

/**
 * 项目id
 */
        @Schema(description = "项目id")
        private String bizProjectId;

/**
 * 项目名称
 */
        @Schema(description = "项目名称")
        private String projectName;

/**
 * 年
 */
        @Schema(description = "年")
        private String year;

/**
 * 月
 */
        @Schema(description = "月")
        private String month;

/**
 * 统计时间
 */
        @Schema(description = "统计时间")
        private Timestamp staTime;

/**
 * 入住人数
 */
        @Schema(description = "入住人数")
        private BigDecimal checkinNum;

/**
 * 入住率
 */
        @Schema(description = "入住率")
        private BigDecimal checkinRate;

/**
 * 租户id
 */
        @Schema(description = "租户id")
        private Long tenantId;
}
