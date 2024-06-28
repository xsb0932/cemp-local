package com.landleaf.jjgj.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "时间段")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimeDuringDTO {

    /**
     * 取值时间-开始
     */
    @Schema(description = "取值时间-开始")
    private Integer timeBegin;

    /**
     * 取值时间-结束
     */
    @Schema(description = "取值时间-结束")
    private Integer timeEnd;
}
