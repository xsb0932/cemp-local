package com.landleaf.bms.domain.response;

import com.landleaf.bms.domain.bo.ProductDownPayloadBO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Yang
 */
@Data
@Schema(description = "产品下行参考报文响应对象")
public class ProductDownPayloadResponse {
    @Schema(description = "产品业务id")
    private String bizId;
    @Schema(description = "产品名称")
    private String name;
    @Schema(description = "报文")
    private ProductDownPayloadBO payload;
}
