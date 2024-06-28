package com.landleaf.bms.domain.response;

import com.landleaf.bms.domain.bo.ProductUpPayloadBO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Yang
 */
@Data
@Schema(description = "产品上行参考报文响应对象")
public class ProductUpPayloadResponse {
    @Schema(description = "产品业务id")
    private String bizId;
    @Schema(description = "产品名称")
    private String name;
    @Schema(description = "报文")
    private ProductUpPayloadBO payload;
}
