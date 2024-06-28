package com.landleaf.data.api.device.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BigDecimalAttrValue(LocalDateTime time, BigDecimal value) {
}
