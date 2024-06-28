package com.landleaf.energy.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 计划用电初参数
 *
 * @author Tycoon
 * @since 2023/8/10 15:45
 **/
@Data
public class PlanElectricityRequest {

    private PlanElectricityRequest() {
    }

    @Data
    @Schema(description = "计划用电初始化请求参数")
    public static class Initialize {

        /**
         * 项目业务ID
         */
        @Schema(description = "项目业务ID")
        @NotBlank(message = "项目业务Id不能为空")
        private String projectBizId;

        /**
         * 年份
         */
        @Schema(description = "年份")
        @NotNull(message = "年份不能为空")
        private String year;

    }

    @Data
    @Schema(description = "变更计划用电")
    public static class Change {

        /**
         * 计划Id
         */
        @NotNull(message = "id不能为空")
        @Schema(description = "计划Id")
        private Long id;

        /**
         * 目标用电量
         */
        @NotNull(message = "目标用电量不能为空")
        @NegativeOrZero(message = "目标用电量不能为负数")
        @Schema(description = "目标用电量")
        private BigDecimal planElectricityConsumption;

    }

}
