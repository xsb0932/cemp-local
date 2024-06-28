package com.landleaf.energy.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.landleaf.energy.domain.dto.SubitemRelationDevicesDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;
import java.util.List;

/**
 * 用水费用配置表的展示信息封装
 *
 * @author hebin
 * @since 2023-07-04
 */
@Data
@Schema(name = "ProjectCnfWaterFeeVO", description = "用水费用配置表的展示信息封装")
public class ProjectCnfWaterFeeVO {

    /**
     * 用水配置id
     */
    @Schema(description = "用水配置id")
    private Long id;

    /**
     * 分类对应的设备
     */
    @Schema(description = "分类对应的设备")
    private List<SubitemRelationDevicesDTO> deviceList;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private String projectId;

    /**
     * 收费模式，0=>单一价格
     */
    @Schema(description = "收费模式，0=>单一价格")
    private Integer chargingMode;

    @Schema(description = "收费模式对应的中文名")
    private String chargingModeName;

    /**
     * 燃气单价
     */
    @Schema(description = "燃气单价")
    private BigDecimal price;

    /**
     * 污水比例
     */
    @Schema(description = "污水比例")
    private BigDecimal sewageRatio;

    /**
     * 污水处理价格
     */
    @Schema(description = "污水处理价格")
    private BigDecimal sewagePrice;
}