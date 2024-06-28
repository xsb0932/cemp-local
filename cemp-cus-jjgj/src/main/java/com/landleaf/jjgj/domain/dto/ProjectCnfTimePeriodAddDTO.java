package com.landleaf.jjgj.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * ProjectCnfTimePeriodEntity对象的新增时的参数封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@Schema(name = "ProjectCnfTimePeriodAddDTO对象", description = "ProjectCnfTimePeriodEntity对象的新增时的参数封装")
public class ProjectCnfTimePeriodAddDTO {
    @Schema(description = "原时间，yyyy年MM月.用于修改时，将原始的时间给过来方便删除操作")
    private String originalTime;

    @Schema(description = "时间：yyyy年MM月")
    private String time;

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

    @Schema(description = "尖时段时间段")
    private List<TimeDuringDTO> tipTimes;

    @Schema(description = "峰时段时间段")
    private List<TimeDuringDTO> peakTimes;

    @Schema(description = "谷时段时间段")
    private List<TimeDuringDTO> valleyTimes;

    @Schema(description = "平时段时间段")
    private List<TimeDuringDTO> flatTimes;
}
