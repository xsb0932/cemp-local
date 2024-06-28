package com.landleaf.energy.domain.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author Yang
 */
@Data
public class StaDeviceMonthTaskContext {
    private Long tenantId;
    private String tenantCode;
    private LocalDateTime staTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String year;
    private String month;
    private Timestamp timestamp;

    private LocalDateTime dayStartStaTime;
    private LocalDateTime dayEndStaTime;

    public StaDeviceMonthTaskContext(Long tenantId, String tenantCode, LocalDateTime staTime, LocalDateTime startTime, LocalDateTime endTime,
                                     LocalDateTime dayStartStaTime, LocalDateTime dayEndStaTime) {
        this.tenantId = tenantId;
        this.tenantCode = tenantCode;
        this.staTime = staTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.year = String.valueOf(startTime.getYear());
        this.month = String.valueOf(startTime.getMonthValue());
        this.timestamp = Timestamp.valueOf(
                LocalDateTime.of(startTime.getYear(), startTime.getMonthValue(), 1, 0, 0)
        );

        this.dayStartStaTime = dayStartStaTime;
        this.dayEndStaTime = dayEndStaTime;
    }
}
