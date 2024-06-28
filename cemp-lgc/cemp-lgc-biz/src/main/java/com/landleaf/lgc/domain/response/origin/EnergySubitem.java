package com.landleaf.lgc.domain.response.origin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 年/月 度分项
 *
 * @author xushibai
 * @since 2023/09/05
 **/
@Data
public class EnergySubitem {

    /**
     * 年/月 度分项占比
     */
    @Schema(description = "年/月 度分项占比")
    private List<EnergyItem> result;


}
