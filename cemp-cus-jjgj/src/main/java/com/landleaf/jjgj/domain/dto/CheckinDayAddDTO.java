package com.landleaf.jjgj.domain.dto;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.landleaf.comm.sta.util.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

import java.util.Date;

/**
 * CheckinDayEntity对象的新增时的参数封装
 *
 * @author hebin
 * @since 2023-09-21
 */
@Data
@Schema(name = "CheckinDayAddDTO", description = "CheckinDayEntity对象的新增时的参数封装")
public class CheckinDayAddDTO {

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
     * 日
     */
    @Schema(description = "日")
    private String day;

    /**
     * 时间
     */
    @Schema(description = "时间,yyyy-MM-dd")
    private String time;

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

    public void setTime(String time) {
        if (StringUtils.hasText(time)) {
            String[] str = time.split("-");
            year = str[0];
            month = str[1];
            if (2 > month.length()) {
                month = "0" + month;
            }
            day = str[2];
            if (2 > day.length()) {
                day = "0" + day;
            }
            staTime = DateUtil.parse(time, DateUtils.LC_DT_FMT_DAY).toTimestamp();
        }
    }

    public BigDecimal getCheckinRate() {
        if (checkinNum.compareTo(BigDecimal.valueOf(102)) > 0) {
            return new BigDecimal(100);
        } else if (checkinNum.compareTo(BigDecimal.ZERO) < 0) {
            return new BigDecimal(0);
        }
        return checkinNum.divide(new BigDecimal(1.02), 1, RoundingMode.UP);
    }

    public interface AddGroup {
    }

    public interface UpdateGroup {
    }
}
