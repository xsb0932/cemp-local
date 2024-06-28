package com.landleaf.jjgj.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用水费用配置表的新增时的参数封装
 *
 * @author hebin
 * @since 2023-07-04
 */
@Data
@Schema(name = "ProjectCnfWaterFeeAddDTO", description = "用水费用配置表的新增时的参数封装")
public class ProjectCnfWaterFeeAddDTO {

    /**
     * 用水配置id
     */
    @Schema(description = "用水配置id")
    private Long id;

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

    /**
     * 分类对应的设备
     */
    @Schema(description = "分类对应的设备")
    private List<SubitemRelationDevicesDTO> deviceList;

    public interface AddGroup {
    }

    public interface UpdateGroup {
    }
}
