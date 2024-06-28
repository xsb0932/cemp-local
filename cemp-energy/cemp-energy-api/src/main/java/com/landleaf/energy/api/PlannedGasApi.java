package com.landleaf.energy.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.energy.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;

@Tag(name = "Feign 服务 - 计划用气查询")
@FeignClient(name = ApiConstants.NAME)
public interface PlannedGasApi {

    @Operation(summary = "获取时间范围内的项目计划用气合计")
    @GetMapping(ApiConstants.PREFIX + "/gas/get-project-duration-total-plan")
    Response<BigDecimal> getProjectDurationTotalPlan(@RequestParam("bizProjectId") String bizProjectId,
                                                     @RequestParam("begin")
                                                     @JsonFormat(pattern = "yyyy-MM-dd")
                                                     @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                     @RequestParam("end")
                                                     @JsonFormat(pattern = "yyyy-MM-dd")
                                                     @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end);
}
