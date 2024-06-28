package com.landleaf.energy.request;

import cn.hutool.core.collection.CollUtil;
import com.landleaf.energy.enums.SubitemIndexEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.YearMonth;
import java.util.List;

@Data
@Schema(description = "分项指标-月查询")
public class IndexMonthOnlyBatchRequest {
    @Schema(description = "项目业务ID")
    private List<String> bizProjectIdList;

    @Schema(description = "月份")
    private List<YearMonth> months;

    @Schema(description = "指标")
    private List<SubitemIndexEnum> indices;

    public void validated() {
        if (CollUtil.isEmpty(bizProjectIdList)) {
            throw new IllegalArgumentException("项目业务id不能为空");
        }
        if (CollUtil.isEmpty(months)) {
            throw new IllegalArgumentException("月份不能为空");
        }
        if (CollUtil.isEmpty(indices)) {
            throw new IllegalArgumentException("指标不能为空");
        }
    }

}
