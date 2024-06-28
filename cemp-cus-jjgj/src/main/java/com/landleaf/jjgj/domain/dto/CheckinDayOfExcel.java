package com.landleaf.jjgj.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

/**
 * CheckinDayEntity对象使用excel新增时的参数封装
 *
 * @author hebin
 * @since 2023-09-21
 */
@Data
@Schema(name = "CheckinDayOfExcel", description = "CheckinDayEntity对象使用excel新增时的参数封装")
public class CheckinDayOfExcel {
    /**
     * 时间
     */
    @Schema(description = "时间,yyyy-MM-dd")
    private String time;

    /**
     * 入住人数
     */
    @Schema(description = "入住人数")
    private BigDecimal checkinNum;
}
