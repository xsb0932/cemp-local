package com.landleaf.energy.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;
import java.util.List;

/**
 * 燃气费用配置表的新增时的参数封装
 *
 * @author hebin
 * @since 2023-07-04
 */
@Data
@Schema(name = "ProjectCnfGasFeeAddDTO", description = "燃气费用配置表的新增时的参数封装")
public class ProjectCnfGasFeeAddDTO {

    /**
     * 燃气配置id
     */
    @Schema(description = "燃气配置id")
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
     * 分类对应的设备
     */
    @Schema(description = "分类对应的设备")
    private List<SubitemRelationDevicesDTO> deviceList;

    public interface AddGroup {
    }

    public interface UpdateGroup {
    }
}