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
 * 分项Fegin接口
 *
 * @author yue lin
 * @since 2023/7/28 9:26
 */
@Tag(name = "Feign 服务 - 分项指标相关")
@FeignClient(name = ApiConstants.NAME)
public interface SubitemApi {


    /**
     * 查询某一天的指标
     *
     * @param request 参数
     * @return 结果
     */
    @Operation(summary = "查询某一天的指标")
    @PostMapping(ApiConstants.PREFIX + "/subitems/index/day")
    Response<Map<LocalDate, Map<SubitemIndexEnum, BigDecimal>>> searchDataByDay(@Validated @RequestBody IndexDayRequest request);

    /**
     * 查询某一天的充电桩数据信息
     *
     * @param request 参数
     * @return 结果
     */
    @Operation(summary = "查询某一天的充电桩数据信息")
    @PostMapping(ApiConstants.PREFIX + "/subitems/device/day")
    Response<Map<String, BigDecimal>> searchDeviceDataByDay(@Validated @RequestBody DeviceDayRequest request);


    /**
     * 根据设备(电表)- 查询用电量/天
     *
     * @param request 参数
     * @return 结果
     */
    @Operation(summary = "根据设备(电表)- 查询用电量/天")
    @PostMapping(ApiConstants.PREFIX + "/subitems/device/epimport")
    Response<Map<String, BigDecimal>> searchDeviceEpimport(@Validated @RequestBody DeviceDayRequest request);

    /**
     * 根据设备(电表)- 查询用电量/年
     *
     * @param request 参数
     * @return 结果
     */
    @Operation(summary = "根据设备(电表)- 查询用电量/年")
    @PostMapping(ApiConstants.PREFIX + "/subitems/device/epimport/year")
    Response<Map<String, BigDecimal>> searchDeviceEpimportYear(@Validated @RequestBody DeviceDayRequest request);

    /**
     * 根据设备(电表)- 查询发电量/年
     *
     * @param request 参数
     * @return 结果
     */
    @Operation(summary = "根据设备(电表)- 查询发电量/年")
    @PostMapping(ApiConstants.PREFIX + "/subitems/device/epexport/year")
    Response<Map<String, BigDecimal>> searchDeviceEpexportYear(@Validated @RequestBody DeviceDayRequest request);

    /**
     * 根据设备(电表)- 查询发电量/天
     *
     * @param request 参数
     * @return 结果
     */
    @Operation(summary = "根据设备(电表)- 查询发电量/天")
    @PostMapping(ApiConstants.PREFIX + "/subitems/device/epexport")
    Response<Map<String, BigDecimal>> searchDeviceEpexport(@Validated @RequestBody DeviceDayRequest request);


    /**
     * 查询某一月的指标
     *
     * @param request 参数
     * @return 结果
     */
    @Operation(summary = "查询某一月的指标")
    @PostMapping(ApiConstants.PREFIX + "/subitems/index/month")
    Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> searchDataByMonth(@Validated @RequestBody IndexMonthRequest request);

    @Operation(summary = "查询多个项目某一月的指标（数据源仅月报表）")
    @PostMapping(ApiConstants.PREFIX + "/subitems/index/month-only/batch")
    Response<Map<String, Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>>> batchSearchDataOnlyByMonth(@RequestBody IndexMonthOnlyBatchRequest request);

    /**
     * 查询某年的指标
     *
     * @param request 参数
     * @return 结果
     */
    @Operation(summary = "查询某年的指标")
    @PostMapping(ApiConstants.PREFIX + "/subitems/index/year")
    Response<Map<Year, Map<SubitemIndexEnum, BigDecimal>>> searchDataByYear(@Validated @RequestBody IndexYearRequest request);

    /**
     * 查询累计的指标
     *
     * @param request 参数
     * @return 结果
     */
    @Operation(summary = "查询累计的指标")
    @PostMapping(ApiConstants.PREFIX + "/subitems/index/cumulative")
    Response<Map<SubitemIndexEnum, BigDecimal>> searchDataByCumulative(@Validated @RequestBody IndexCumulativeRequest request);


    /**
     * 查询截止某月为止累计的指标（包含该月份）
     *
     * @param request 参数
     * @return 结果
     */
    @Operation(summary = "查询截止某月为止累计的指标（包含该月份）")
    @PostMapping(ApiConstants.PREFIX + "/subitems/index/cumulative/month")
    Response<Map<SubitemIndexEnum, BigDecimal>> searchDataByMonthCumulative(@Validated @RequestBody IndexCumulativeMonthRequest request);

    /**
     * 查询分项年数据结构
     *
     * @return 结果
     */
    @Operation(summary = "查询分项年数据结构")
    @PostMapping(ApiConstants.PREFIX + "/subitems/year/ratio")
    Response<SubitemYearRatioResoponse> getSubitemYearRatio(@Validated @RequestBody SubitemRequest subitemRequest);

}
