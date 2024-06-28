package com.landleaf.energy.response;


import lombok.Data;

import java.math.BigDecimal;

/**
 * 计划用水返回
 *
 * @author xshibai
 * @since 2023/01/29 15:1
 **/
@Data
public class PlannedWaterResponse  {

    private Long id;
    private String projectBizId;
    private String year;
    private String month;
    private BigDecimal planWaterConsumption;

}
