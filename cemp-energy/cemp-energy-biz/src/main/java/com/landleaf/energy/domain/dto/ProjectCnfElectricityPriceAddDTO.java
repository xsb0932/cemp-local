package com.landleaf.energy.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;

/**
 * 电费配置表的新增时的参数封装
 *
 * @author hebin
 * @since 2024-03-20
 */
@Data
@Schema(name = "ProjectCnfElectricityPriceAddDTO", description = "电费配置表的新增时的参数封装")
public class ProjectCnfElectricityPriceAddDTO {

    /**
     * 分时配置id
     */
    @Schema(description = "分时配置id")
    @NotNull(groups = {ProjectCnfElectricityPriceAddDTO.UpdateGroup.class}, message = "分时配置id不能为空")
    private Long id;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private String projectId;

    /**
     * 电费类型，见字典electricity_price_type
     */
    @Schema(description = "电费类型，见字典electricity_price_type")
    private String type;

    /**
     * 电价
     */
    @Schema(description = "电价")
    private BigDecimal price;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private Long tenantId;

    public interface AddGroup {
    }

    public interface UpdateGroup {
    }
}