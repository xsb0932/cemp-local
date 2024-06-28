package com.landleaf.energy.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 区域看板分月数据
 *
 * @author xshibai
 * @since 2024/06/13
 **/
@Data
public class PlannedAreaMonthsDataResponse {

    private String year;
    private String month;
    private String consumption;

}
