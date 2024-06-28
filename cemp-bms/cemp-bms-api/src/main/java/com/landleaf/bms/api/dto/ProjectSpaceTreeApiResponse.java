package com.landleaf.bms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 项目-空间树状结构
 *
 * @author yue lin
 * @since 2023/7/12 14:40
 */
@Data
public class ProjectSpaceTreeApiResponse {

    /**
     * 区域ID
     */
    @Schema(description = "区域ID")
    private Long spaceId;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private Long projectId;

    /**
     * 业务ID
     */
    @Schema(description = "业务ID")
    private String bizId;

    /**
     * 区域名称
     */
    @Schema(description = "区域名称")
    private String spaceName;

    /**
     * 区域父级ID
     */
    @Schema(description = "区域父级ID")
    private Long parentId;

    /**
     * 区域父级名称
     */
    @Schema(description = "区域父级名称")
    private String parentName;

    /**
     * 区域类型
     */
    @Schema(description = "区域类型")
    private String spaceType;

    /**
     * 区域类型名称
     */
    @Schema(description = "区域类型名称")
    private String spaceTypeName;

    /**
     * 区域面积
     */
    @Schema(description = "区域面积")
    private BigDecimal spaceProportion;

    /**
     * 区域备注
     */
    @Schema(description = "区域备注")
    private String spaceDescription;

    /**
     * 子集
     */
    @Schema(description = "子集")
    private List<ProjectSpaceTreeApiResponse> children;

}
