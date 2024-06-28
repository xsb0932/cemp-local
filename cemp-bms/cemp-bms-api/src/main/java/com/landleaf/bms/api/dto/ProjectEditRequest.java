package com.landleaf.bms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 项目编辑
 *
 * @author 张力方
 * @since 2023/06/05
 */
@Data
@Schema(name = "项目-编辑请求参数", description = "项目-编辑请求参数")
public class ProjectEditRequest {

    /**
     * 项目id
     */
    @NotNull(message = "项目id不能为空")
    @Schema(description = "项目id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    /**
     * 项目名称
     * 租户内唯一
     */
    @NotBlank(message = "项目名称不能为空")
    @Size(min = 1, max = 100, message = "项目名称长度区间{min}-{max}")
    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "XXX")
    private String name;

    /**
     * 项目业态（字典编码-PROJECT_BIZ_TYPE）
     */
    @NotBlank(message = "项目业态不能为空")
    @Schema(description = "项目业态（字典编码-PROJECT_BIZ_TYPE）", requiredMode = Schema.RequiredMode.REQUIRED, example = "XXX")
    private String bizType;

    /**
     * 面积
     */
    @DecimalMax(value = "99999999999999.99", message = "面积最大值{value}")
    @Schema(description = "面积", example = "100")
    private BigDecimal area;

    /**
     * 能源类型（字典编码-ENERGY_TYPE）,多选
     */
    @NotNull(message = "能源类型不能为空")
    @Schema(description = "能源类型（字典编码-ENERGY_TYPE）", example = "xxx")
    private List<String> energyType;

    /**
     * 项目状态（字典编码-PROJECT_STATUS）
     */
    @NotNull(message = "项目状态不能为空")
    @Schema(description = "项目状态（字典编码-PROJECT_STATUS）", requiredMode = Schema.RequiredMode.REQUIRED, example = "XXX")
    private String status;

    @NotEmpty(message = "能源子系统类型不能为空")
    @Schema(description = "能源子系统类型（字典编码-ENERGY_SUB_SYSTEM）", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> energySubSystem;

    /**
     * 负责人
     */
    @Schema(description = "负责人")
    @Size(max = 10, message = "负责人最大长度{max}")
    private String director;

    /**
     * 负责人电话
     */
    @Schema(description = "负责人电话")
    private String mobile;

    /**
     * 项目地址
     */
    @Size(max = 50, message = "项目地址最大长度{max}")
    @Schema(description = "项目地址")
    private String address;

    /**
     * 项目行政区域（tb_address）
     */
    @Schema(description = "项目行政区域", example = "XXX")
    private List<String> addressCode;

    /**
     * 高德-纬度
     */
    @Size(max = 50, message = "高德-纬度最大长度{max}")
    @Schema(description = "高德-纬度", example = "120.123456")
    private String gdLatitude;

    /**
     * 高德-经度
     */
    @Size(max = 50, message = "高德-经度最大长度{max}")
    @Schema(description = "高德-经度", example = "80.123456")
    private String gdLongitude;

    /**
     * 项目描述
     */
    @Size(max = 255, message = "项目描述长度不能超过{max}")
    @Schema(description = "项目描述", example = "xxxx")
    private String description;

    /**
     * 项目归属的管理节点业务id
     */
    @NotNull(message = "项目归属的管理节点业务id不能为空")
    @Schema(description = "项目归属的管理节点业务id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String parentBizNodeId;

    /**
     * 负责人用户id
     */
    @Schema(description = "负责人用户id")
    private Long directorUserId;
}

