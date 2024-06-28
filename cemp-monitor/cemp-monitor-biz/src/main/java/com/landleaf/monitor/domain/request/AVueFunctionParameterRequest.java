package com.landleaf.monitor.domain.request;

import lombok.Data;

@Data
public class AVueFunctionParameterRequest {
    private String identifier;
    private Object value;
}
