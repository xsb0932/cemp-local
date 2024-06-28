package com.landleaf.energy.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 项目的展示信息封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@Schema(name = "ProjectVO对象", description = "项目的展示信息封装")
public class ProjectVO {

    /**
     * 项目id
     */
    @Schema(description = "项目id")
    private Long id;

    /**
     * 项目业务id（全局唯一id）
     */
    @Schema(description = "项目业务id（全局唯一id）")
    private String bizProjectId;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称")
    private String name;

    /**
     * 项目编码
     */
    @Schema(description = "项目编码")
    private String code;

    /**
     * 项目业态code（字典配置）
     */
    @Schema(description = "项目业态code（字典配置）")
    private String bizType;

    /**
     * 面积
     */
    @Schema(description = "面积")
    private BigDecimal area;

    /**
     * 能源类型（固定类型，枚举or字典配置，前端多选，后台`,`拼接存储，例：1,2,3对应水/电/气）
     */
    @Schema(description = "能源类型（固定类型，枚举or字典配置，前端多选，后台`,`拼接存储，例：1,2,3对应水/电/气）")
    private String energyType;

    /**
     * 项目状态（字典配置 0规划 1建设 2运维）
     */
    @Schema(description = "项目状态（字典配置 0规划 1建设 2运维）")
    private Integer status;

    /**
     * 负责人
     */
    @Schema(description = "负责人")
    private String director;

    /**
     * 负责人电话
     */
    @Schema(description = "负责人电话")
    private String mobile;

    /**
     * 项目地址
     */
    @Schema(description = "项目地址")
    private String address;

    /**
     * 项目归属管理节点业务id
     */
    @Schema(description = "项目归属管理节点业务id")
    private String parentBizNodeId;

    /**
     * 项目行政区域（tb_address）
     */
    @Schema(description = "项目行政区域（tb_address）")
    private String addressCode;

    /**
     * 高德-纬度
     */
    @Schema(description = "高德-纬度")
    private String gdLatitude;

    /**
     * 高德-经度
     */
    @Schema(description = "高德-经度")
    private String gdLongitude;

    /**
     * 租户id
     */
    @Schema(description = "租户id")
    private Long tenantId;

    /**
     * 权限路径path（冗余）
     */
    @Schema(description = "权限路径path（冗余）")
    private String path;

    /**
     * 项目对应的管理节点业务id
     */
    @Schema(description = "项目对应的管理节点业务id")
    private String bizNodeId;
}
