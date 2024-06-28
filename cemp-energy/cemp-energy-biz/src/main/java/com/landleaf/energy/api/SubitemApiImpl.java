package com.landleaf.energy.api;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.energy.enums.SubitemIndexEnum;
import com.landleaf.energy.request.*;
import com.landleaf.energy.response.SubitemYearRatioResoponse;
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
 * @author yue lin
 * @since 2023/8/2 10:15
 */
@RestController
@RequiredArgsConstructor
public class SubitemApiImpl implements SubitemApi {

    private final StaSubitemService staSubitemService;

    @Override
    public Response<Map<LocalDate, Map<SubitemIndexEnum, BigDecimal>>> searchDataByDay(IndexDayRequest request) {
        return Response.success(staSubitemService.searchDataByDay(request.getProjectBizId(), request.getDays(), request.getIndices()));
    }

    @Override
    public Response<Map<String, BigDecimal>> searchDeviceDataByDay(DeviceDayRequest request) {
        return Response.success(staSubitemService.searchDeviceDataByDay(request.getDeviceBizId(), request.getDay()));
    }

    @Override
    public Response<Map<String, BigDecimal>> searchDeviceEpimport(DeviceDayRequest request) {
        return Response.success(staSubitemService.searchDeviceDataByDay(request.getDeviceBizId(), request.getDay()));
    }

    @Override
    public Response<Map<String, BigDecimal>> searchDeviceEpimportYear(DeviceDayRequest request) {
        return Response.success(staSubitemService.searchDeviceEpimportYear(request.getDeviceBizId(), String.valueOf(request.getDay().getYear())));
    }

    @Override
    public Response<Map<String, BigDecimal>> searchDeviceEpexportYear(DeviceDayRequest request) {
        return Response.success(staSubitemService.searchDeviceEpexportYear(request.getDeviceBizId(), String.valueOf(request.getDay().getYear())));
    }

    @Override
    public Response<Map<String, BigDecimal>> searchDeviceEpexport(DeviceDayRequest request) {
        return Response.success(staSubitemService.searchDeviceEpexport(request.getDeviceBizId(), request.getDay()));
    }

    @Override
    public Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> searchDataByMonth(IndexMonthRequest request) {
        return Response.success(staSubitemService.searchDataByMonth(request.getProjectBizId(), request.getMonths(), request.getIndices()));
    }

    @Override
    public Response<Map<String, Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>>> batchSearchDataOnlyByMonth(IndexMonthOnlyBatchRequest request) {
        request.validated();
        return Response.success(staSubitemService.batchSearchDataOnlyByMonth(request.getBizProjectIdList(), request.getMonths(), request.getIndices()));
    }

    @Override
    public Response<Map<Year, Map<SubitemIndexEnum, BigDecimal>>> searchDataByYear(IndexYearRequest request) {
        return Response.success(staSubitemService.searchDataByYear(request.getProjectBizId(), request.getYears(), request.getIndices()));
    }

    @Override
    public Response<Map<SubitemIndexEnum, BigDecimal>> searchDataByCumulative(IndexCumulativeRequest request) {
        return Response.success(staSubitemService.searchDataByCumulative(request.getProjectBizId(), request.getIndices()));
    }

    @Override
    public Response<Map<SubitemIndexEnum, BigDecimal>> searchDataByMonthCumulative(IndexCumulativeMonthRequest request) {
        return Response.success(staSubitemService.searchDataByMonthCumulative(request.getProjectBizId(), request.getMonth(), request.getIndices()));
    }

    @Override
    public Response<SubitemYearRatioResoponse> getSubitemYearRatio(SubitemRequest subitemRequest) {
        return Response.success(staSubitemService.getSubitemYearRatio(subitemRequest));
    }
}
