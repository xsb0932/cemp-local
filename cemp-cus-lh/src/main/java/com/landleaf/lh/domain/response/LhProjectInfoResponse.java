package com.landleaf.lh.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xusihbai
 * @since 2024/01/26
 **/
@Data
@Schema(description = "项目信息")
public class LhProjectInfoResponse {

    @Schema(description = "项目面积")
    private String area;
    @Schema(description = "项目城市")
    private String cityName;

}
