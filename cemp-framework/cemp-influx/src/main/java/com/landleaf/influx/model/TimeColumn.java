package com.landleaf.influx.model;

import com.influxdb.annotations.Column;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * influx base时间字段
 *
 * @author eason
 */
@Data
public class TimeColumn {
    @Column(timestamp = true)
    public Instant time;

    public LocalDateTime getLocalTime() {
        return LocalDateTime.ofInstant(getTime(), ZoneId.systemDefault());
    }
}
