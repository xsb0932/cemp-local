package com.landleaf.energy.controller;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.energy.domain.dto.MeterImportDTO;
import com.landleaf.energy.domain.request.ElectricityDayQueryRequest;
import com.landleaf.energy.domain.request.ElectricityHourQueryRequest;
import com.landleaf.energy.domain.request.ElectricityMeterReadingRequest;
import com.landleaf.energy.domain.request.ElectricityMonthQueryRequest;
import com.landleaf.energy.domain.response.DeviceElectricityTabulationResponse;
import com.landleaf.energy.domain.response.ElectricityMeterReadingTreeResponse;
import com.landleaf.energy.service.ElectricityMeterReadingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * 电表抄表接口
 *
 * @author Tycoon
 * @since 2023/8/17 10:10
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/electricity-meter-reading")
@Tag(name = "电表抄表接口", description = "电表抄表接口")
public class ElectricityMeterReadingController {

    private final ElectricityMeterReadingService electricityMeterReadingService;


    /**
     * 查询手动日抄表列表
     *
     * @param request 参数
     * @return 结果集
     */
    @Operation(summary = "查询手动日抄表列表")
    @GetMapping("/page/day/manual")
    public Response<Page<DeviceElectricityTabulationResponse>> searchManualElectricityTabulation(@ModelAttribute @Validated ElectricityDayQueryRequest request) {
        return Response.success(electricityMeterReadingService.searchManualElectricityTabulation(request));
    }

    /**
     * 查询手动月抄表列表
     *
     * @param request 参数
     * @return 结果集
     */
    @Operation(summary = "查询手动月抄表列表")
    @GetMapping("/page/month/manual")
    public Response<Page<DeviceElectricityTabulationResponse>> searchManualElectricityTabulation(@ModelAttribute @Validated ElectricityMonthQueryRequest request) {
        return Response.success(electricityMeterReadingService.searchManualElectricityTabulation(request));
    }

    @Operation(summary = "查询手动时抄表列表")
    @GetMapping("/page/hour/manual")
    public Response<Page<DeviceElectricityTabulationResponse>> searchManualElectricityTabulation(@ModelAttribute @Validated ElectricityHourQueryRequest request) {
        return Response.success(electricityMeterReadingService.searchManualElectricityTabulation(request));
    }

    /**
     * 查询自动动日抄表列表
     *
     * @param request 参数
     * @return 结果集
     */
    @Operation(summary = "查询自动动日抄表列表")
    @GetMapping("/page/day/sta")
    public Response<Page<DeviceElectricityTabulationResponse>> searchStaElectricityTabulation(@ModelAttribute @Validated ElectricityDayQueryRequest request) {
        return Response.success(electricityMeterReadingService.searchStaElectricityTabulation(request));
    }

    /**
     * 查询自动月抄表列表
     *
     * @param request 参数
     * @return 结果集
     */
    @Operation(summary = "查询自动月抄表列表")
    @GetMapping("/page/month/sta")
    public Response<Page<DeviceElectricityTabulationResponse>> searchStaElectricityTabulation(@ModelAttribute @Validated ElectricityMonthQueryRequest request) {
        return Response.success(electricityMeterReadingService.searchStaElectricityTabulation(request));
    }

    @Operation(summary = "查询自动时抄表列表")
    @GetMapping("/page/hour/sta")
    public Response<Page<DeviceElectricityTabulationResponse>> searchStaElectricityTabulation(@ModelAttribute @Validated ElectricityHourQueryRequest request) {
        return Response.success(electricityMeterReadingService.searchStaElectricityTabulation(request));
    }

    /**
     * 查询电表设备的倍率
     *
     * @param bizDeviceId 业务ID
     * @return 结果
     */
    @Operation(summary = "查询电表设备的倍率")
    @GetMapping("/multiplying-factor")
    public Response<String> searchMultiplyingFactor(@RequestParam("bizDeviceId") String bizDeviceId) {
        return Response.success(electricityMeterReadingService.searchMultiplyingFactor(bizDeviceId));
    }

    /**
     * 查询期初表显（时）
     *
     * @param bizDeviceId 业务ID
     * @param time        时间(yyyy-MM-dd HH)
     * @return 结果
     */
    @Parameter(name = "bizDeviceId", in = ParameterIn.QUERY, required = true, description = "业务ID")
    @Parameter(name = "time", in = ParameterIn.QUERY, required = true, description = "时间(yyyy-MM-dd HH)")
    @Operation(summary = "查询期初表显（小时）")
    @GetMapping("/hour-open-displays-value")
    public Response<BigDecimal> searchHourOpenDisplaysValue(@RequestParam("bizDeviceId") String bizDeviceId, @RequestParam("time") String time) {
        return Response.success(electricityMeterReadingService.searchHourOpenDisplaysValue(bizDeviceId, time));
    }

    /**
     * 查询期初表显（日）
     *
     * @param bizDeviceId 业务ID
     * @param time        时间(yyyy-MM-dd)
     * @return 结果
     */
    @Parameter(name = "bizDeviceId", in = ParameterIn.QUERY, required = true, description = "业务ID")
    @Parameter(name = "time", in = ParameterIn.QUERY, required = true, description = "时间(yyyy-MM-dd)")
    @Operation(summary = "查询期初表显（日）")
    @GetMapping("/open-displays-value")
    public Response<BigDecimal> searchOpenDisplaysValue(@RequestParam("bizDeviceId") String bizDeviceId, @RequestParam("time") LocalDate time) {
        return Response.success(electricityMeterReadingService.searchOpenDisplaysValue(bizDeviceId, time));
    }

    /**
     * 查询期初表显（月）
     *
     * @param bizDeviceId 业务ID
     * @param time        时间(yyyy-MM)
     * @return 结果
     */
    @Parameter(name = "bizDeviceId", in = ParameterIn.QUERY, required = true, description = "业务ID")
    @Parameter(name = "time", in = ParameterIn.QUERY, required = true, description = "时间(yyyy-MM)")
    @Operation(summary = "查询期初表显（月）")
    @GetMapping("/close-displays-value")
    public Response<BigDecimal> searchCloseDisplaysValue(@RequestParam("bizDeviceId") String bizDeviceId, @RequestParam("time") YearMonth time) {
        return Response.success(electricityMeterReadingService.searchCloseDisplaysValue(bizDeviceId, time));
    }

    @Operation(summary = "创建时手抄表")
    @PostMapping("/hour/manual")
    public Response<Void> addManualElectricityHour(@Validated @RequestBody ElectricityMeterReadingRequest.HourCreate request) {
        electricityMeterReadingService.addManualElectricityHour(request);
        return Response.success();
    }

    /**
     * 创建日手抄表
     *
     * @param request 参数
     * @return 结果
     */
    @Operation(summary = "创建日手抄表")
    @PostMapping("/day/manual")
    public Response<Void> addManualElectricityDay(@Validated @RequestBody ElectricityMeterReadingRequest.DayCreate request) {
        electricityMeterReadingService.addManualElectricityDay(request);
        return Response.success();
    }

    /**
     * 创建月手抄表
     *
     * @param request 参数
     * @return 结果
     */
    @Operation(summary = "创建月手抄表")
    @PostMapping("/month/manual")
    public Response<Void> addManualElectricityMonth(@Validated @RequestBody ElectricityMeterReadingRequest.MonthCreate request) {
        electricityMeterReadingService.addManualElectricityMonth(request);
        return Response.success();
    }

    /**
     * 删除日手抄表
     *
     * @param id 参数
     * @return 结果
     */
    @Parameter(name = "id", description = "id", in = ParameterIn.PATH, required = true)
    @Operation(summary = "删除日手抄表")
    @DeleteMapping("/day/manual/{id}")
    public Response<Void> deleteManualElectricityDay(@PathVariable("id") Long id) {
        electricityMeterReadingService.deleteManualElectricityDay(id);
        return Response.success();
    }

    /**
     * 删除月手抄表
     *
     * @param id 参数
     * @return 结果
     */
    @Parameter(name = "id", description = "id", in = ParameterIn.PATH, required = true)
    @Operation(summary = "删除月手抄表")
    @DeleteMapping("/month/manual/{id}")
    public Response<Void> deleteManualElectricityMonth(@PathVariable("id") Long id) {
        electricityMeterReadingService.deleteManualElectricityMonth(id);
        return Response.success();
    }

    /**
     * 变更抄表记录信息
     *
     * @param request 参数
     * @return 结果
     */
    @Operation(summary = "变更抄表记录信息")
    @PutMapping
    public Response<Void> updateElectricityMeterReading(@Validated @RequestBody ElectricityMeterReadingRequest.Update request) {
        electricityMeterReadingService.updateElectricityMeterReading(request);
        return Response.success();
    }

    /**
     * 查询电表菜单
     *
     * @param value 抄表方式(1手抄日2手抄月3远程日4远程月5手抄时6远程时)
     * @return 结果
     */
    @Parameter(name = "value", description = "抄表方式(1手抄日2手抄月3远程日4远程月5手抄时6远程时)")
    @Operation(summary = "查询电表菜单")
    @GetMapping("/tree/menu")
    public Response<List<ElectricityMeterReadingTreeResponse>> searchElectricityTree(@RequestParam("value") Integer value) {
        return Response.success(electricityMeterReadingService.searchElectricityTree(value));
    }


    @PostMapping(value = "/import")
    @Operation(summary = "批量导入手抄表数据", description = "批量导入手抄表数据")
    public Response<List<String>> excelImport(@RequestParam(value = "file") MultipartFile file) {
        MeterImportDTO dto = electricityMeterReadingService.excelImportCheck(file);
        if (!CollectionUtils.isEmpty(dto.getErrMsg())) {
            return Response.error("500", dto.formatErrMsg());
        }
        electricityMeterReadingService.excelImportSave(dto);
        return Response.success();
    }
}
