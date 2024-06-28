package com.landleaf.energy.api;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.energy.enums.SubitemIndexEnum;
import com.landleaf.energy.request.*;
import com.landleaf.energy.response.SubitemYearRatioResoponse;
import com.landleaf.energy.service.StaSubareaService;
import com.landleaf.energy.service.StaSubitemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.Map;

/**
 * 分项指标查询
 *
 * @author xushibai
 * @since 2024/6/4 20:06
 */
@RestController
@RequiredArgsConstructor
public class SubareaApiImpl implements SubareaApi {

    private final StaSubareaService staSubareaService;


    @Override
    public Response<Map<String, Map<YearMonth, BigDecimal>>> searchMonth(SubareaMonthRequest request) {
        return staSubareaService.searchMonth(request);
    }
}
