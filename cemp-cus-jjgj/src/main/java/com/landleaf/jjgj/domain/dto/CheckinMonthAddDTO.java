package com.landleaf.jjgj.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;

/**
 * CheckinMonthEntity对象的新增时的参数封装
 *
 * @author hebin
 * @since 2023-09-21
 */
@Data
@Schema(name = "CheckinMonthAddDTO", description = "CheckinMonthEntity对象的新增时的参数封装")
public class CheckinMonthAddDTO {

/**
 * id
 */
        @Schema(description = "id")
            @NotNull(groups = {UpdateGroup.class}, message = "id不能为空")
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

public interface AddGroup {
}

public interface UpdateGroup {
}
}
