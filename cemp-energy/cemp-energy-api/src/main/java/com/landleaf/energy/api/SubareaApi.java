package com.landleaf.energy.api;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.energy.enums.ApiConstants;
import com.landleaf.energy.enums.SubitemIndexEnum;
import com.landleaf.energy.request.*;
import com.landleaf.energy.response.SubitemYearRatioResoponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.Map;

/**
 * 分区Fegin接口
 *
 * @author xushibai
 * @since 2024/6/4 20:01
 */
@Tag(name = "Feign 服务 - 分项指标相关")
@FeignClient(name = ApiConstants.NAME)
public interface SubareaApi {




    /**
     * 查询分区月指标
     *
     * @param request 参数
     * @return 结果
     */
    @Operation(summary = "查询分区月指标")
    @PostMapping(ApiConstants.PREFIX + "/subarea/month")
    Response<Map<String,Map<YearMonth,BigDecimal>>> searchMonth(@Validated @RequestBody SubareaMonthRequest request);

}
