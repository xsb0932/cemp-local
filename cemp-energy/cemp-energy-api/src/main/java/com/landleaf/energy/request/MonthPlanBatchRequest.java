package com.landleaf.energy.request;

import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.YearMonth;
import java.util.List;

@Data
public class MonthPlanBatchRequest {
    @Schema(description = "项目业务ID")
    private List<String> bizProjectIdList;
    @Schema(description = "月份")
    private List<YearMonth> months;

    public void validated() {
        if (CollUtil.isEmpty(bizProjectIdList)) {
            throw new IllegalArgumentException("项目业务id不能为空");
        }
        if (CollUtil.isEmpty(months)) {
            throw new IllegalArgumentException("月份不能为空");
        }
    }
}
