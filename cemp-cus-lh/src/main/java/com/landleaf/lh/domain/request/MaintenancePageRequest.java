package com.landleaf.lh.domain.request;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "报修单分页列表查询参数封装")
public class MaintenancePageRequest extends PageParam {
    @Schema(description = "管理节点id")
    @NotBlank(message = "管理节点id不能为空")
    private String bizNodeId;
    @Schema(description = "报修单类别")
    private List<String> maintenanceType;
    @Schema(description = "起始月份（yyyy-MM）")
    @NotBlank(message = "起始月份不能为空")
    private String yearMonthStart;
    @Schema(description = "结束月份（yyyy-MM）")
    @NotBlank(message = "结束月份不能为空")
    private String yearMonthEnd;
}
