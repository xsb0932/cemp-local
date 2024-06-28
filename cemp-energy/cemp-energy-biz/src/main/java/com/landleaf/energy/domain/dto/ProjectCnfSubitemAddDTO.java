package com.landleaf.energy.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.landleaf.energy.domain.vo.DeviceMonitorVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;
import java.util.List;

/**
 * 配置表-分项的新增时的参数封装
 *
 * @author hebin
 * @since 2023-07-06
 */
@Data
@Schema(name = "ProjectCnfSubitemAddDTO对象", description = "配置表-分项的新增时的参数封装")
public class ProjectCnfSubitemAddDTO {

    /**
     * 分项id
     */
    @Schema(description = "分项id")
    @NotNull(groups = {UpdateGroup.class}, message = "分项id不能为空")
    private Long id;

    /**
     * 分项名称
     */
    @Schema(description = "分项名称")
    private String name;

    /**
     * 父项ID
     */
    @Schema(description = "父项ID")
    private String parentId;

    /**
     * 路径
     */
    @Schema(description = "路径")
    private String path;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private String projectId;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private Long tenantId;

    /**
     * 分项指标代码
     */
    @Schema(description = "分项指标代码")
    private String kpiSubtype;

    /**
     * 指标大类
     */
    @Schema(description = "指标大类")
    private String kpiType;

    /**
     * 指标大类代码
     */
    @Schema(description = "指标大类代码")
    private String kpiTypeCode;

    @Schema(description = "分项绑定的设备")
    private List<DeviceMonitorVO> devices;


    public interface AddGroup {
    }

    public interface UpdateGroup {
    }
}
