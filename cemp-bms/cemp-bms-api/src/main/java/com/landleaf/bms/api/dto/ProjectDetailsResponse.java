package com.landleaf.bms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 项目详情返回参数
 *
 * @author 张力方
 * @since 2023/6/6
 **/
@Data
@Schema(name = "项目详情返回参数", description = "项目详情返回参数")
public class ProjectDetailsResponse {
    /**
     * 项目id
     */
    @Schema(description = "项目id", example = "1")
    private Long id;

    /**
     * 项目业务id（全局唯一id）
     */
    @Schema(description = "项目业务id（全局唯一id）", example = "1")
    private String bizProjectId;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称", example = "xxx")
    private String name;

    /**
     * 项目编码
     */
    @Schema(description = "项目编码", example = "xxx")
    private String code;

    /**
     * 项目业态code（字典配置）
     */
    @Schema(description = "项目业态code（字典配置）")
    private String bizType;

    /**
     * 项目业态名称
     */
    @Schema(description = "项目业态名称")
    private String bizTypeName;

    /**
     * 面积
     */
    @Schema(description = "面积", example = "1")
    private BigDecimal area;

    /**
     * 能源类型
     */
    @Schema(description = "能源类型")
    private List<String> energyType;

    /**
     * 能源类型名称
     */
    @Schema(description = "能源类型名称")
    private List<String> energyTypeName;

    /**
     * 项目状态
     */
    @Schema(description = "项目状态")
    private String status;

    /**
     * 项目状态名称
     */
    @Schema(description = "项目状态名称")
    private String statusName;

    /**
     * 能源子系统类型
     */
    @Schema(description = "能源子系统类型")
    private List<String> energySubSystem;

    /**
     * 能源子系统类型名称
     */
    @Schema(description = "能源子系统类型名称")
    private List<String> energySubSystemName;

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
     * 项目归属管理节点名称
     */
    @Schema(description = "项目归属管理节点名称")
    private String parentBizNodeName;

    /**
     * 项目行政区域
     */
    @Schema(description = "项目行政区域")
    private List<String> addressCode;

    /**
     * 项目气象区域
     */
    @Schema(description = "项目气象区域")
    private String weatherCode;

    /**
     * 项目行政区域名称
     */
    @Schema(description = "项目行政区域名称")
    private String addressName;

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
    @Schema(description = "权限路径path")
    private String path;

    /**
     * 项目对应的管理节点业务id
     */
    @Schema(description = "项目对应的管理节点业务id")
    private String bizNodeId;

    /**
     * 项目描述
     */
    @Schema(description = "项目描述", example = "xxxx")
    private String description;

    /**
     * 负责人用户id
     */
    @Schema(description = "负责人用户id")
    private Long directorUserId;
}
