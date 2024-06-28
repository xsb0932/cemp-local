package com.landleaf.jjgj.domain.dto;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 项目的查询时的参数封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "ProjectQueryDTO对象", description = "项目的查询时的参数封装")
public class ProjectQueryDTO extends PageParam {

    /**
     * 项目id
     */
    @Schema(name = "项目id")
    private Long id;

    /**
     * 项目业务id（全局唯一id）
     */
    @Schema(name = "项目业务id（全局唯一id）")
    private String bizProjectId;

    /**
     * 项目名称
     */
    @Schema(name = "项目名称")
    private String name;

    /**
     * 项目编码
     */
    @Schema(name = "项目编码")
    private String code;

    /**
     * 项目业态code（字典配置）
     */
    @Schema(name = "项目业态code（字典配置）")
    private String bizType;

    /**
     * 面积
     */
    @Schema(name = "面积")
    private BigDecimal area;

    /**
     * 能源类型（固定类型，枚举or字典配置，前端多选，后台`,`拼接存储，例：1,2,3对应水/电/气）
     */
    @Schema(name = "能源类型（固定类型，枚举or字典配置，前端多选，后台`,`拼接存储，例：1,2,3对应水/电/气）")
    private String energyType;

    /**
     * 项目状态（字典配置 0规划 1建设 2运维）
     */
    @Schema(name = "项目状态（字典配置 0规划 1建设 2运维）")
    private Integer status;

    /**
     * 负责人
     */
    @Schema(name = "负责人")
    private String director;

    /**
     * 负责人电话
     */
    @Schema(name = "负责人电话")
    private String mobile;

    /**
     * 项目地址
     */
    @Schema(name = "项目地址")
    private String address;

    /**
     * 项目归属管理节点业务id
     */
    @Schema(name = "项目归属管理节点业务id")
    private String parentBizNodeId;

    /**
     * 项目行政区域（tb_address）
     */
    @Schema(name = "项目行政区域（tb_address）")
    private String addressCode;

    /**
     * 高德-纬度
     */
    @Schema(name = "高德-纬度")
    private String gdLatitude;

    /**
     * 高德-经度
     */
    @Schema(name = "高德-经度")
    private String gdLongitude;

    /**
     * 租户id
     */
    @Schema(name = "租户id")
    private Long tenantId;

    /**
     * 权限路径path（冗余）
     */
    @Schema(name = "权限路径path（冗余）")
    private String path;

    /**
     * 项目对应的管理节点业务id
     */
    @Schema(name = "项目对应的管理节点业务id")
    private String bizNodeId;

    /**
     * 开始时间
     */
    @Schema(name = "开始时间,格式为yyyy-MM-dd")
    private String startTime;

    /**
     * 结束时间
     */
    @Schema(name = "结束时间,格式为yyyy-MM-dd")
    private String endTime;
}
