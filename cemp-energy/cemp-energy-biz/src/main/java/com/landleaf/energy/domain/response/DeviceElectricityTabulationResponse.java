package com.landleaf.energy.domain.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 抄表列表
 *
 * @author Tycoon
 * @since 2023/8/17 10:17
 **/
@Data
@Schema(description = "抄表列表")
public class DeviceElectricityTabulationResponse {

    /**
     * id
     */
    @Schema(description = "id")
    private Long id;

    /**
     * 设备业务ID
     */
    @Schema(description = "设备业务ID")
    private String bizDeviceId;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 倍率
     */
    @Schema(description = "倍率")
    private String multiplyingFactor;

    /**
     * 期数
     */
    @Schema(description = "期数")
    private String time;

    /**
     * 期初表显
     */
    @Schema(description = "期初表显")
    private BigDecimal openDisplaysValue;

    /**
     * 期末表显
     */
    @Schema(description = "期末表显")
    private BigDecimal closeDisplaysValue;

    /**
     * 本期电量
     */
    @Schema(description = "本期电量")
    private BigDecimal activeTotal;

    /**
     * 记录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "记录时间")
    private Timestamp staTime;


    @Schema(description = "年")
    private String year;
    @Schema(description = "月")
    private String month;
    @Schema(description = "日")
    private String day;
    @Schema(description = "时")
    private String hour;

    /**
     * 抄表员工
     */
    @Schema(description = "抄表员工")
    private String username;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

}
