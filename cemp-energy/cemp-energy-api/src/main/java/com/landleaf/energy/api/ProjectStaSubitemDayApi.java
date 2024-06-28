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

@Tag(name = "Feign 服务 - 项目分项天统计查询")
@FeignClient(name = ApiConstants.NAME)
public interface ProjectStaSubitemDayApi {

    @Operation(summary = "获取时间范围内的项目用电合计")
    @GetMapping(ApiConstants.PREFIX + "/get-project-electricity-duration-total")
    Response<BigDecimal> getProjectElectricityDurationTotal(@RequestParam("bizProjectId") String bizProjectId,
                                                            @RequestParam("begin")
                                                            @JsonFormat(pattern = "yyyy-MM-dd")
                                                            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                            @RequestParam("end")
                                                            @JsonFormat(pattern = "yyyy-MM-dd")
                                                            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end);

    @Operation(summary = "获取时间范围内的项目用气合计")
    @GetMapping(ApiConstants.PREFIX + "/get-project-gas-duration-total")
    Response<BigDecimal> getProjectGasDurationTotal(@RequestParam("bizProjectId") String bizProjectId,
                                                    @RequestParam("begin")
                                                    @JsonFormat(pattern = "yyyy-MM-dd")
                                                    @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                    @RequestParam("end")
                                                    @JsonFormat(pattern = "yyyy-MM-dd")
                                                    @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end);

    @Operation(summary = "获取时间范围内的项目用水合计")
    @GetMapping(ApiConstants.PREFIX + "/get-project-water-duration-total")
    Response<BigDecimal> getProjectWaterDurationTotal(@RequestParam("bizProjectId") String bizProjectId,
                                                      @RequestParam("begin")
                                                      @JsonFormat(pattern = "yyyy-MM-dd")
                                                      @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                      @RequestParam("end")
                                                      @JsonFormat(pattern = "yyyy-MM-dd")
                                                      @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end);
}
