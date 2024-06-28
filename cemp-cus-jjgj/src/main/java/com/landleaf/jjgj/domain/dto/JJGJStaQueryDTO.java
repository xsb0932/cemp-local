package com.landleaf.jjgj.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 锦江查询DTO
 *
 * @author xushibai
 * @since 2023-09-21
 */
@Data
@Schema(name = "锦江查询DTO", description = "锦江查询DTO")
public class JJGJStaQueryDTO {

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
     * 日
     */
    @Schema(description = "日")
    private String day;
}
