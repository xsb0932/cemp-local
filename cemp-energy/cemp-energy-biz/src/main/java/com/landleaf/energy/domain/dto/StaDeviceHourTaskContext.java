package com.landleaf.energy.domain.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author Yang
 */
@Data
public class StaDeviceHourTaskContext {
    private Long tenantId;
    private String tenantCode;
    private LocalDateTime staTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String year;
    private String month;
    private String day;
    private String hour;
    private Timestamp timestamp;

    public StaDeviceHourTaskContext(Long tenantId, String tenantCode, LocalDateTime staTime, LocalDateTime startTime, LocalDateTime endTime) {
        this.tenantId = tenantId;
        this.tenantCode = tenantCode;
        this.staTime = staTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.year = String.valueOf(startTime.getYear());
        this.month = String.valueOf(startTime.getMonthValue());
        this.day = String.valueOf(startTime.getDayOfMonth());
        this.hour = String.valueOf(startTime.getHour());
        this.timestamp = Timestamp.valueOf(
                LocalDateTime.of(startTime.getYear(), startTime.getMonthValue(), startTime.getDayOfMonth(), startTime.getHour(), 0)
        );
    }
}
