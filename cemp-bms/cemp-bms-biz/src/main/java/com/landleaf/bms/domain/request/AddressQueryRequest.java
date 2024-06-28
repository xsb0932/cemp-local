package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AddressQueryRequest {
    @Schema(description = "编码")
    private String addressCode;
}
