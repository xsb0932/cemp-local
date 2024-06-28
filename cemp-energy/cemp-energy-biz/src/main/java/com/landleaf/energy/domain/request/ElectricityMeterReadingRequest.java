package com.landleaf.energy.domain.request;

import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.energy.domain.entity.ProjectManualDeviceElectricityDayEntity;
import com.landleaf.energy.domain.entity.ProjectManualDeviceElectricityMonthEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * 抄表请求参数
 *
 * @author Tycoon
 * @since 2023/8/17 17:12
 **/
public class ElectricityMeterReadingRequest {

    private ElectricityMeterReadingRequest() {
    }

    @Data
    @Schema(description = "小时新增抄表")
    public static class HourCreate {

        /**
         * 设备业务ID
         */
        @NotBlank(message = "设备业ID不能为空")
        @Schema(description = "设备业务ID")
        private String bizDeviceId;

        /**
         * 倍率
         */
        @NotBlank(message = "倍率不能为空")
        @Schema(description = "倍率")
        private String multiplyingFactor;

        /**
         * 日期
         */
        @NotBlank(message = "日期不能为空")
        @Schema(description = "日期(yyyy-MM-dd HH)")
        private String time;

        /**
         * 期初表显
         */
        @NegativeOrZero(message = "期初表显不能为负数")
        @Schema(description = "期初表显")
        private BigDecimal openDisplaysValue;

        /**
         * 期末表显
         */
        @NegativeOrZero(message = "期末表显不能为负数")
        @NotNull(message = "期末表显不能为空")
        @Schema(description = "期末表显")
        private BigDecimal closeDisplaysValue;

        /**
         * 本期电量
         */
        @NegativeOrZero(message = "本期电量不能为负数")
        @NotNull(message = "本期电量不能为空")
        @Schema(description = "本期电量")
        private BigDecimal activeTotal;

        /**
         * 备注
         */
        @Size(max = 255, message = "备注长度不能超过{max}")
        @Schema(description = "备注")
        private String remark;

    }


    @Data
    @Schema(description = "日新增抄表")
    public static class DayCreate {

        /**
         * 设备业务ID
         */
        @NotBlank(message = "设备业ID不能为空")
        @Schema(description = "设备业务ID")
        private String bizDeviceId;

        /**
         * 倍率
         */
        @NotBlank(message = "倍率不能为空")
        @Schema(description = "倍率")
        private String multiplyingFactor;

        /**
         * 日期
         */
        @NotNull(message = "日期不能为空")
        @Schema(description = "日期")
        private LocalDate time;

        /**
         * 期初表显
         */
        @NegativeOrZero(message = "期初表显不能为负数")
        @NotNull(message = "期初表显不能为空")
        @Schema(description = "期初表显")
        private BigDecimal openDisplaysValue;

        /**
         * 期末表显
         */
        @NegativeOrZero(message = "期末表显不能为负数")
        @NotNull(message = "期末表显不能为空")
        @Schema(description = "期末表显")
        private BigDecimal closeDisplaysValue;

        /**
         * 本期电量
         */
        @NegativeOrZero(message = "本期电量不能为负数")
        @NotNull(message = "本期电量不能为空")
        @Schema(description = "本期电量")
        private BigDecimal activeTotal;

        /**
         * 备注
         */
        @Size(max = 255, message = "备注长度不能超过{max}")
        @Schema(description = "备注")
        private String remark;

        public ProjectManualDeviceElectricityDayEntity toEntity() {
            ProjectManualDeviceElectricityDayEntity entity = new ProjectManualDeviceElectricityDayEntity();
            entity.setBizDeviceId(this.bizDeviceId);
            entity.setYear(String.valueOf(time.getYear()));
            entity.setMonth(String.valueOf(time.getMonthValue()));
            entity.setDay(String.valueOf(time.getDayOfMonth()));
            entity.setEnergymeterEpimportTotal(this.activeTotal);
            entity.setStaTime(new Timestamp(System.currentTimeMillis()));
            entity.setOpenDisplaysValue(this.openDisplaysValue);
            entity.setCloseDisplaysValue(this.closeDisplaysValue);
            entity.setRemark(this.remark);
            entity.setTenantId(TenantContext.getTenantId());
            return entity;
        }

    }

    @Data
    @Schema(description = "月新增抄表")
    public static class MonthCreate {

        /**
         * 设备业务ID
         */
        @NotBlank(message = "设备业ID不能为空")
        @Schema(description = "设备业务ID")
        private String bizDeviceId;

        /**
         * 倍率
         */
        @NotBlank(message = "倍率不能为空")
        @Schema(description = "倍率")
        private String multiplyingFactor;

        /**
         * 日期
         */
        @NotNull(message = "日期不能为空")
        @Schema(description = "日期")
        private YearMonth time;

        /**
         * 期初表显
         */
        @NegativeOrZero(message = "期初表显不能为负数")
        @NotNull(message = "期初表显不能为空")
        @Schema(description = "期初表显")
        private BigDecimal openDisplaysValue;

        /**
         * 期末表显
         */
        @NegativeOrZero(message = "期末表显不能为负数")
        @NotNull(message = "期末表显不能为空")
        @Schema(description = "期末表显")
        private BigDecimal closeDisplaysValue;

        /**
         * 本期电量
         */
        @NegativeOrZero(message = "本期电量不能为负数")
        @NotNull(message = "本期电量不能为空")
        @Schema(description = "本期电量")
        private BigDecimal activeTotal;

        /**
         * 备注
         */
        @Size(max = 255, message = "备注长度不能超过{max}")
        @Schema(description = "备注")
        private String remark;

        public ProjectManualDeviceElectricityMonthEntity toEntity() {
            ProjectManualDeviceElectricityMonthEntity entity = new ProjectManualDeviceElectricityMonthEntity();
            entity.setBizDeviceId(this.bizDeviceId);
            entity.setYear(String.valueOf(time.getYear()));
            entity.setMonth(String.valueOf(time.getMonthValue()));
            entity.setEnergymeterEpimportTotal(this.activeTotal);
            entity.setStaTime(new Timestamp(System.currentTimeMillis()));
            entity.setOpenDisplaysValue(this.openDisplaysValue);
            entity.setCloseDisplaysValue(this.closeDisplaysValue);
            entity.setRemark(this.remark);
            entity.setTenantId(TenantContext.getTenantId());
            return entity;
        }
    }

    @Data
    @Schema(description = "更新抄表记录")
    public static class Update {


        @NotNull(message = "ID不能为空")
        @Schema(description = "ID")
        private Long id;

        @NotNull(message = "设备业务编号")
        @Schema(description = "bizDeviceId")
        private String bizDeviceId;

        @NotNull(message = "统计时间")
        @Schema(description = "time")
        private String time;

        /**
         * 期初表显
         */
        @NegativeOrZero(message = "期初表显不能为负数")
//        @NotNull(message = "期初表显不能为空")
        @Schema(description = "期初表显")
        private BigDecimal openDisplaysValue;

        /**
         * 期末表显
         */
        @NegativeOrZero(message = "期末表显不能为负数")
        @NotNull(message = "期末表显不能为空")
        @Schema(description = "期末表显")
        private BigDecimal closeDisplaysValue;

        /**
         * 本期电量
         */
        @NegativeOrZero(message = "本期电量不能为负数")
        @NotNull(message = "本期电量不能为空")
        @Schema(description = "本期电量")
        private BigDecimal activeTotal;

        /**
         * 备注
         */
        @Size(max = 255, message = "备注长度不能超过{max}")
        @Schema(description = "备注")
        private String remark;

        /**
         * 数据类型（1手抄日2手抄月3远程日4远程月5手抄时6远程时）
         */
        @Digits(fraction = 1, integer = 6, message = "数据类型[1,2,3,4,5,6]")
        @Schema(description = "数据类型（1手抄日2手抄月3远程日4远程月5手抄时6远程时）", minimum = "1", maximum = "6")
        private Long type;

    }

}
