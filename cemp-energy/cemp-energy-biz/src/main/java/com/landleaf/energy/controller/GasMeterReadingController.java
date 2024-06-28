package com.landleaf.energy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.energy.domain.dto.GasMeterTreeDTO;
import com.landleaf.energy.domain.dto.MeterImportDTO;
import com.landleaf.energy.domain.enums.MeterReadCycleEnum;
import com.landleaf.energy.domain.enums.MeterReadEnum;
import com.landleaf.energy.domain.request.GasMeterDetailResponse;
import com.landleaf.energy.domain.request.GasMeterDeviceResponse;
import com.landleaf.energy.domain.request.GasMeterPageQueryRequest;
import com.landleaf.energy.domain.request.GasMeterSaveRequest;
import com.landleaf.energy.domain.response.GasMeterPageResponse;
import com.landleaf.energy.service.impl.GasMeterService;
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
@RequestMapping("/gas-meter-reading")
@Tag(name = "燃气抄表接口", description = "燃气抄表接口")
public class GasMeterReadingController {
    private final GasMeterService gasMeterService;

    @Operation(summary = "查询项目燃气表树")
    @GetMapping("/tree")
    public Response<List<GasMeterTreeDTO>> tree(@RequestParam("meterRead") String meterRead, @RequestParam("meterReadCycle") String meterReadCycle) {
        MeterReadEnum meterReadEnum = MeterReadEnum.ofCode(meterRead);
        MeterReadCycleEnum meterReadCycleEnum = MeterReadCycleEnum.ofCode(meterReadCycle);
        List<GasMeterTreeDTO> data = gasMeterService.tree(meterReadEnum, meterReadCycleEnum, LoginUserUtil.getLoginUserId());
        return Response.success(data);
    }

    @Operation(summary = "查询手动抄表气表")
    @GetMapping("/meter-devices")
    public Response<List<GasMeterDeviceResponse>> meterDevices(@RequestParam("meterReadCycle") String meterReadCycle, @RequestParam("bizProjectId") String bizProjectId) {
        MeterReadCycleEnum meterReadCycleEnum = MeterReadCycleEnum.ofCode(meterReadCycle);
        List<GasMeterDeviceResponse> data = gasMeterService.meterDevices(meterReadCycleEnum, bizProjectId, LoginUserUtil.getLoginUserId());
        return Response.success(data);
    }

    @Operation(summary = "分页查询设备报表")
    @PostMapping("/page")
    public Response<IPage<GasMeterPageResponse>> page(@Validated @RequestBody GasMeterPageQueryRequest request) {
        IPage<GasMeterPageResponse> data = gasMeterService.page(request);
        return Response.success(data);
    }

    @Operation(summary = "获取记录详情")
    @GetMapping("/detail")
    public Response<GasMeterDetailResponse> detail(@RequestParam("id") Long id, @RequestParam("meterReadCycle") String meterReadCycle) {
        MeterReadCycleEnum meterReadCycleEnum = MeterReadCycleEnum.ofCode(meterReadCycle);
        GasMeterDetailResponse data = gasMeterService.detail(id, meterReadCycleEnum);
        return Response.success(data);
    }

    @Operation(summary = "获取设备期初值")
    @GetMapping("/get-gas-start")
    public Response<BigDecimal> getGasStart(@RequestParam("bizDeviceId") String bizDeviceId,
                                            @RequestParam("staTime") String staTime,
                                            @RequestParam("meterReadCycle") String meterReadCycle) {
        MeterReadCycleEnum meterReadCycleEnum = MeterReadCycleEnum.ofCode(meterReadCycle);
        BigDecimal data = gasMeterService.getGasStart(bizDeviceId, staTime, meterReadCycleEnum);
        return Response.success(data);
    }

    @Operation(summary = "保存or更新气表抄表记录")
    @PutMapping("/save-or-update")
    public Response<Void> saveOrUpdate(@Validated @RequestBody GasMeterSaveRequest request) {
        gasMeterService.saveOrUpdate(request);
        return Response.success();
    }

    @PostMapping(value = "/import")
    @Operation(summary = "批量导入手抄表数据", description = "批量导入手抄表数据")
    public Response<List<String>> excelImport(@RequestParam(value = "file") MultipartFile file) {
        MeterImportDTO dto = gasMeterService.excelImportCheck(file);
        if (!CollectionUtils.isEmpty(dto.getErrMsg())) {
            return Response.error("500", dto.formatErrMsg());
        }
        gasMeterService.excelImportSave(dto);
        return Response.success();
    }

}
