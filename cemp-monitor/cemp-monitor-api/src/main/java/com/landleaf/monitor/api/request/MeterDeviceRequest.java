package com.landleaf.monitor.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeterDeviceRequest {
    private Collection<String> bizProjectIds;
    private String bizCategoryId;
    private String meterRead;
    private String meterReadCycle;
}
