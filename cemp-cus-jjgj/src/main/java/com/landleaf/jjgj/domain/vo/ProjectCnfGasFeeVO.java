package com.landleaf.jjgj.domain.vo;

import com.landleaf.jjgj.domain.dto.SubitemRelationDevicesDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 燃气费用配置表的展示信息封装
 *
 * @author hebin
 * @since 2023-07-04
 */
@Data
@Schema(name = "ProjectCnfGasFeeVO", description = "燃气费用配置表的展示信息封装")
public class ProjectCnfGasFeeVO {
    /**
     * 燃气配置id
     */
    @Schema(description = "燃气配置id")
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
}
