package com.landleaf.energy.domain.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author Yang
 */
@Data
public class StaDeviceYearTaskContext {
    private Long tenantId;
    private String tenantCode;
    private LocalDateTime staTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String year;
    private Timestamp timestamp;

    public StaDeviceYearTaskContext(Long tenantId, String tenantCode, LocalDateTime staTime, LocalDateTime startTime, LocalDateTime endTime) {
        this.tenantId = tenantId;
        this.tenantCode = tenantCode;
        this.staTime = staTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.year = String.valueOf(startTime.getYear());
        this.timestamp = Timestamp.valueOf(
                LocalDateTime.of(startTime.getYear(), 1, 1, 0, 0)
        );
    }
}
