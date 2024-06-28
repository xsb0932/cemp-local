package com.landleaf.influx.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Measurement(name = "test")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestEntity extends TimeColumn {
    @Column(tag = true)
    public Long deviceId;

    @Column(tag = true)
    public String code;
    /**
     * 值
     */
    @Column
    public float value;

}