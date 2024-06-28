package com.landleaf.energy.domain.dto;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@Schema(description = "ProjectStaKpiDTO对象", name = "设备统计查询对象")
public class ProjectStaKpiDTO extends PageParam {

    /**
     * 产品ID
     */
    @Schema(description = "设备ID")
    private String bizDeviceId;

    /**
     * 产品ID
     */
    @Schema(description = "设备ID集合")
    private List<String> bizDeviceIds;


    /**
     * 产品ID
     */
    @Schema(description = "产品ID")
    private String bizProductId;

    /**
     * 品类ID
     */
    @Schema(description = "品类ID")
    private String bizCategoryId;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private String bizProjectId;

    /**
     * 项目代码
     */
    @Schema(description = "项目代码")
    private String projectCode;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private String begin;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private String end;

    /**
     * 是否查询小时
     */
    @Schema(description = "统计周期")
    private String staTimePeriod;

    @Schema(description = "指标代码集")
    private List<String> kpiCodes;

}
