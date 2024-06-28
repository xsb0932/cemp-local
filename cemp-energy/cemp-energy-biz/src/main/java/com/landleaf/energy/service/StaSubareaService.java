package com.landleaf.energy.service;


import com.landleaf.comm.base.pojo.Response;
import com.landleaf.energy.request.SubareaMonthRequest;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

/**
 * 分项查询
 *
 * @author xushibai
 * @since 2024/6/4 20:06
 */
public interface StaSubareaService {


    Response<Map<String, Map<YearMonth, BigDecimal>>> searchMonth(SubareaMonthRequest request);
}
