package com.landleaf.energy.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Yang
 */
@Data
@NoArgsConstructor
public class ProjectReportPushDTO {
    private Integer pageNo;
    private Integer pageSize;
    private String bizProjectId;
    private String begin;
    private String end;
    private String staTimePeriod;
    private List<String> kpiCodes;

    public ProjectReportPushDTO(String bizProjectId, String begin, String end, List<String> kpiCodes) {
        this.bizProjectId = bizProjectId;
        this.begin = begin;
        this.end = end;
        this.kpiCodes = kpiCodes;
    }

    {
        pageNo = 1;
        pageSize = 10000;
        // 统计-天
        staTimePeriod = "2";
    }
}
