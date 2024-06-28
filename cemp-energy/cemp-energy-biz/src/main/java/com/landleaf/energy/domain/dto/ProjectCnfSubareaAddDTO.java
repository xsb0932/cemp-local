package com.landleaf.energy.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.landleaf.energy.domain.entity.DeviceMonitorEntity;
import com.landleaf.energy.domain.entity.ProjectSubareaDeviceEntity;
import com.landleaf.energy.domain.vo.DeviceMonitorVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;
import java.util.List;

/**
 * ProjectCnfSubareaEntity对象的新增时的参数封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@Schema(name = "ProjectCnfSubareaAddDTO对象", description = "ProjectCnfSubareaEntity对象的新增时的参数封装")
public class ProjectCnfSubareaAddDTO {


    /**
     * 分区id
     */
    @Schema(description = "分区id")
    @NotNull(groups = {UpdateGroup.class}, message = "分区id不能为空")
    private Long id;

    /**
     * 分区名称
     */
    @Schema(description = "分区名称")
    private String name;

    /**
     * 分区类型
     */
    @Schema(description = "分区类型")
    private String type;

    /**
     * 父ID
     */
    @Schema(description = "父ID")
    private String parentId;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private String path;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private Long tenantId;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private String projectId;

    /**
     * 指标大类
     */
    @Schema(description = "指标大类")
    private String kpiType;

    /**
     * 分区指标代码
     */
    @Schema(description = "分区指标代码")
    private String kpiSubtype;

    /**
     * 指标大类代码
     */
    @Schema(description = "指标大类代码")
    private String kpiTypeCode;


    /**
     * 分区类型代码
     */
    @Schema(description = "分区类型代码")
    private String typeCode;

    @Schema(description = "分区绑定的设备")
    private List<DeviceMonitorVO> devices;

    public interface AddGroup {
    }

    public interface UpdateGroup {
    }
}
