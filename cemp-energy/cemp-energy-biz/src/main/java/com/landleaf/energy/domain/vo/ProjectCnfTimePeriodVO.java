package com.landleaf.energy.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.landleaf.energy.domain.dto.TimeDuringDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

/**
 * ProjectCnfTimePeriodEntity对象的展示信息封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@Schema(name = "ProjectCnfTimePeriodVO对象", description = "ProjectCnfTimePeriodEntity对象的展示信息封装")
public class ProjectCnfTimePeriodVO {

    @Schema(description = "时间：yyyy年MM月")
    private String time;

    @JsonIgnore
    private YearMonth sortField;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private String projectId;

    /**
     * 尖时段价格
     */
    @Schema(description = "尖时段价格")
    private BigDecimal tipPrice;

    /**
     * 峰时段价格
     */
    @Schema(description = "峰时段价格")
    private BigDecimal peakPrice;

    /**
     * 谷时段价格
     */
    @Schema(description = "谷时段价格")
    private BigDecimal valleyPrice;

    /**
     * 平时段价格
     */
    @Schema(description = "平时段价格")
    private BigDecimal flatPrice;

    @Schema(description = "尖时段时间段：1:00~2:00、4:00~6:00")
    private String tipTimeDuring;

    @Schema(description = "峰时段时间段：1:00~2:00、4:00~6:00")
    private String peakTimeDuring;

    @Schema(description = "谷时段时间段：1:00~2:00、4:00~6:00")
    private String valleyTimeDuring;

    @Schema(description = "平时段时间段：1:00~2:00、4:00~6:00")
    private String flatTimeDuring;

    @Schema(description = "尖时段时间段")
    private List<TimeDuringDTO> tipTimes;

    @Schema(description = "峰时段时间段")
    private List<TimeDuringDTO> peakTimes;

    @Schema(description = "谷时段时间段")
    private List<TimeDuringDTO> valleyTimes;

    @Schema(description = "平时段时间段")
    private List<TimeDuringDTO> flatTimes;

    public String getTipTimeDuring() {
        if (null == tipTimeDuring && !CollectionUtils.isEmpty(tipTimes)) {
            StringBuilder sb = new StringBuilder();
            tipTimes.stream().forEach(i -> {
                sb.append("," + i.getTimeBegin() + "~" + i.getTimeEnd());
            });
            tipTimeDuring = sb.substring(1);
        }
        return tipTimeDuring;
    }

    public String getFlatTimeDuring() {
        if (null == flatTimeDuring && !CollectionUtils.isEmpty(flatTimes)) {
            StringBuilder sb = new StringBuilder();
            flatTimes.stream().forEach(i -> {
                sb.append("," + i.getTimeBegin() + "~" + i.getTimeEnd());
            });
            flatTimeDuring = sb.substring(1);
        }
        return flatTimeDuring;
    }

    public String getValleyTimeDuring() {
        if (null == valleyTimeDuring && !CollectionUtils.isEmpty(valleyTimes)) {
            StringBuilder sb = new StringBuilder();
            valleyTimes.stream().forEach(i -> {
                sb.append("," + i.getTimeBegin() + "~" + i.getTimeEnd());
            });
            valleyTimeDuring = sb.substring(1);
        }
        return valleyTimeDuring;
    }

    public String getPeakTimeDuring() {
        if (null == peakTimeDuring && !CollectionUtils.isEmpty(peakTimes)) {
            StringBuilder sb = new StringBuilder();
            peakTimes.stream().forEach(i -> {
                sb.append("," + i.getTimeBegin() + "~" + i.getTimeEnd());
            });
            peakTimeDuring = sb.substring(1);
        }
        return peakTimeDuring;
    }
}
