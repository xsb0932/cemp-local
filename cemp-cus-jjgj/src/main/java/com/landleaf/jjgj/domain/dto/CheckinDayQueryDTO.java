package com.landleaf.jjgj.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.landleaf.comm.base.pojo.PageParam;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;

/**
 * CheckinDayEntity对象的查询时的参数封装
 *
 * @author hebin
 * @since 2023-09-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "CheckinDayQueryDTO", description = "CheckinDayEntity对象的查询时的参数封装")
public class CheckinDayQueryDTO extends PageParam {

    /**
     * 项目id
     */
    @Schema(description = "项目id")
    private String bizProjectId;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间,格式为yyyy-MM-dd")
    private String startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间,格式为yyyy-MM-dd")
    private String endTime;
}