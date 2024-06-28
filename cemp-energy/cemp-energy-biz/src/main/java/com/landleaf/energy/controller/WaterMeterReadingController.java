package com.landleaf.energy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.energy.domain.dto.MeterImportDTO;
import com.landleaf.energy.domain.dto.WaterMeterTreeDTO;
import com.landleaf.energy.domain.enums.MeterReadCycleEnum;
import com.landleaf.energy.domain.enums.MeterReadEnum;
import com.landleaf.energy.domain.request.WaterMeterDetailResponse;
import com.landleaf.energy.domain.request.WaterMeterDeviceResponse;
import com.landleaf.energy.domain.request.WaterMeterPageQueryRequest;
import com.landleaf.energy.domain.request.WaterMeterSaveRequest;
import com.landleaf.energy.domain.response.WaterMeterPageResponse;
import com.landleaf.energy.service.impl.WaterMeterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/water-meter-reading")
@Tag(name = "水表抄表接口", description = "水表抄表接口")
public class WaterMeterReadingController {
    private final WaterMeterService waterMeterService;

    @Operation(summary = "查询项目水表树")
    @GetMapping("/tree")
    public Response<List<WaterMeterTreeDTO>> tree(@RequestParam("meterRead") String meterRead, @RequestParam("meterReadCycle") String meterReadCycle) {
        MeterReadEnum meterReadEnum = MeterReadEnum.ofCode(meterRead);
        MeterReadCycleEnum meterReadCycleEnum = MeterReadCycleEnum.ofCode(meterReadCycle);
        List<WaterMeterTreeDTO> data = waterMeterService.tree(meterReadEnum, meterReadCycleEnum, LoginUserUtil.getLoginUserId());
        return Response.success(data);
    }

    @Operation(summary = "查询手动抄表水表")
    @GetMapping("/meter-devices")
    public Response<List<WaterMeterDeviceResponse>> meterDevices(@RequestParam("meterReadCycle") String meterReadCycle, @RequestParam("bizProjectId") String bizProjectId) {
        MeterReadCycleEnum meterReadCycleEnum = MeterReadCycleEnum.ofCode(meterReadCycle);
        List<WaterMeterDeviceResponse> data = waterMeterService.meterDevices(meterReadCycleEnum, bizProjectId, LoginUserUtil.getLoginUserId());
        return Response.success(data);
    }

    @Operation(summary = "分页查询设备报表")
    @PostMapping("/page")
    public Response<IPage<WaterMeterPageResponse>> page(@Validated @RequestBody WaterMeterPageQueryRequest request) {
        IPage<WaterMeterPageResponse> data = waterMeterService.page(request);
        return Response.success(data);
    }

    @Operation(summary = "获取记录详情")
    @GetMapping("/detail")
    public Response<WaterMeterDetailResponse> detail(@RequestParam("id") Long id, @RequestParam("meterReadCycle") String meterReadCycle) {
        MeterReadCycleEnum meterReadCycleEnum = MeterReadCycleEnum.ofCode(meterReadCycle);
        WaterMeterDetailResponse data = waterMeterService.detail(id, meterReadCycleEnum);
        return Response.success(data);
    }

    @Operation(summary = "获取设备期初值")
    @GetMapping("/get-water-start")
    public Response<BigDecimal> getWaterStart(@RequestParam("bizDeviceId") String bizDeviceId,
                                              @RequestParam("staTime") String staTime,
                                              @RequestParam("meterReadCycle") String meterReadCycle) {
        MeterReadCycleEnum meterReadCycleEnum = MeterReadCycleEnum.ofCode(meterReadCycle);
        BigDecimal data = waterMeterService.getWaterStart(bizDeviceId, staTime, meterReadCycleEnum);
        return Response.success(data);
    }

    @Operation(summary = "保存or更新水表抄表记录")
    @PutMapping("/save-or-update")
    public Response<Void> saveOrUpdate(@Validated @RequestBody WaterMeterSaveRequest request) {
        waterMeterService.saveOrUpdate(request);
        return Response.success();
    }

    @PostMapping(value = "/import")
    @Operation(summary = "批量导入手抄表数据", description = "批量导入手抄表数据")
    public Response<List<String>> excelImport(@RequestParam(value = "file") MultipartFile file) {
        MeterImportDTO dto = waterMeterService.excelImportCheck(file);
        if (!CollectionUtils.isEmpty(dto.getErrMsg())) {
            return Response.error("500", dto.formatErrMsg());
        }
        waterMeterService.excelImportSave(dto);
        return Response.success();
    }

}
