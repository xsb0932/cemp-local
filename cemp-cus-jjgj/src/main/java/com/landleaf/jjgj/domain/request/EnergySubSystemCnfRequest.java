package com.landleaf.jjgj.domain.request;

import com.landleaf.jjgj.domain.entity.ProjectCnfChargeStationEntity;
import com.landleaf.jjgj.domain.entity.ProjectCnfPvEntity;
import com.landleaf.jjgj.domain.entity.ProjectCnfStorageEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 能源子系统配置-请求参数
 *
 * @author Tycoon
 * @since 2023/8/11 17:13
 **/
public class EnergySubSystemCnfRequest {

    private EnergySubSystemCnfRequest() {
    }

    /**
     * 光伏配置
     */
    @Data
    @Schema(description = "光伏配置")
    public static class Pv {

        /**
         * 项目业务ID
         */
        @NotBlank(message = "项目业务ID不能为空")
        @Schema(description = "项目业务ID")
        private String projectBizId;

        /**
         * 上网模式
         */
        @NotBlank(message = "上网模式不能为空")
        @Schema(description = "上网模式")
        private String onlineMode;

        /**
         * 价格
         */
        @NotNull(message = "价格不能为空")
        @NegativeOrZero(message = "价格不能为负数")
        @Schema(description = "价格")
        private BigDecimal price;

        /**
         * 装机容量
         */
        @NotNull(message = "装机容量不能为空")
        @NegativeOrZero(message = "装机容量不能为负数")
        @Schema(description = "装机容量")
        private BigDecimal capacity;

        public ProjectCnfPvEntity toEntity() {
            ProjectCnfPvEntity entity = new ProjectCnfPvEntity();
            entity.setProjectId(this.projectBizId);
            entity.setOnlineMode(this.onlineMode);
            entity.setPrice(this.price);
            entity.setTotalRp(this.capacity);
            return entity;
        }

    }

    /**
     * 储能配置
     */
    @Data
    @Schema(description = "储能配置")
    public static class Storage {

        /**
         * 项目业务ID
         */
        @NotBlank(message = "项目业务ID不能为空")
        @Schema(description = "项目业务ID")
        private String projectBizId;

        /**
         * 额定功率
         */
        @NotNull(message = "额定功率不能为空")
        @NegativeOrZero(message = "额定功率不能为负数")
        @Schema(description = "额定功率")
        private BigDecimal ratedPower;

        /**
         * 最大电量
         */
        @NotNull(message = "最大电量不能为空")
        @NegativeOrZero(message = "最大电量不能为负数")
        @Schema(description = "最大电量")
        private BigDecimal storageTotal;

        public ProjectCnfStorageEntity toEntity() {
            ProjectCnfStorageEntity entity = new ProjectCnfStorageEntity();
            entity.setProjectId(this.projectBizId);
            entity.setTotalRp(this.ratedPower);
            entity.setTotalStorage(this.storageTotal);
            return entity;
        }

    }

    /**
     * 充电桩配置
     */
    @Data
    @Schema(description = "充电桩配置")
    public static class ChargeStation {

        /**
         * 项目业务ID
         */
        @NotBlank(message = "项目业务ID不能为空")
        @Schema(description = "项目业务ID")
        private String projectBizId;

        /**
         * 计费模式
         */
        @NotBlank(message = "计费模式不能为空")
        @Schema(description = "计费模式")
        private String billingMode;

        /**
         * 服务费
         */
        @NotNull(message = "服务费不能为空")
        @NegativeOrZero(message = "服务费不能为负数")
        @Schema(description = "服务费")
        private BigDecimal price;

        /**
         * 额定功率
         */
        @NotNull(message = "额定功率不能为空")
        @NegativeOrZero(message = "额定功率不能为负数")
        @Schema(description = "额定功率")
        private BigDecimal ratedPower;

        /**
         * 交流桩数量
         */
        @NotNull(message = "交流桩数量不能为空")
        @NegativeOrZero(message = "交流桩数量不能为负数")
        @Schema(description = "交流桩数量")
        private Long acStationNum;

        /**
         * 直流桩数量
         */
        @NotNull(message = "直流桩数量不能为空")
        @NegativeOrZero(message = "直流桩数量不能为负数")
        @Schema(description = "直流桩数量")
        private Long dcStationNum;

        public ProjectCnfChargeStationEntity toEntity() {
            ProjectCnfChargeStationEntity entity = new ProjectCnfChargeStationEntity();
            entity.setProjectId(this.projectBizId);
            entity.setBillingMode(this.billingMode);
            entity.setRp(this.ratedPower);
            entity.setAcStationNum(this.acStationNum);
            entity.setDcStationNum(this.dcStationNum);
            entity.setPrice(this.price);
            return entity;
        }

    }

}
