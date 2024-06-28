package com.landleaf.lh.controller;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.file.api.FileApi;
import com.landleaf.lh.domain.dto.MaintenanceImportDTO;
import com.landleaf.lh.domain.enums.MaintenanceTypeEnum;
import com.landleaf.lh.domain.request.MaintenanceAddRequest;
import com.landleaf.lh.domain.request.MaintenanceEditRequest;
import com.landleaf.lh.domain.request.MaintenanceExportRequest;
import com.landleaf.lh.domain.request.MaintenancePageRequest;
import com.landleaf.lh.domain.response.MaintenanceInfoResponse;
import com.landleaf.lh.domain.response.MaintenancePageResponse;
import com.landleaf.lh.domain.response.MaintenanceTypeListResponse;
import com.landleaf.lh.service.MaintenanceSheetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;


/**
 * MaintenanceSheetEntity对象的控制层接口定义
 *
 * @author hebin
 * @since 2024-05-22
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/maintenance-sheet")
@Tag(name = "MaintenanceSheetEntity对象的控制层接口定义", description = "MaintenanceSheetEntity对象的控制层接口定义")
public class MaintenanceSheetController {
    private final MaintenanceSheetService maintenanceSheetService;
    private final FileApi fileApi;
    @Value("${maintenance.bucketName}")
    private String bucketName;
    @Value("${maintenance.objectPath}")
    private String objectPath;

    @PostMapping("/page")
    @Operation(summary = "分页列表", description = "分页列表")
    public Response<Page<MaintenancePageResponse>> page(@RequestBody @Validated MaintenancePageRequest request) {
        return Response.success(maintenanceSheetService.selectPage(request));
    }

    @GetMapping("/list-maintenance-type")
    @Operation(summary = "报修类型", description = "报修类型")
    public Response<List<MaintenanceTypeListResponse>> listMaintenanceType() {
        return Response.success(MaintenanceTypeEnum.toTypeList());
    }

    @PostMapping("/add")
    @Operation(summary = "添加", description = "添加")
    public Response<Void> add(@RequestBody @Validated MaintenanceAddRequest request) {
        maintenanceSheetService.add(request);
        return Response.success();
    }

    @PutMapping("/edit")
    @Operation(summary = "修改", description = "修改")
    public Response<Void> edit(@RequestBody @Validated MaintenanceEditRequest request) {
        maintenanceSheetService.edit(request);
        return Response.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除", description = "删除")
    public Response<Void> delete(@PathVariable("id") Long id) {
        maintenanceSheetService.delete(id);
        return Response.success();
    }

    @GetMapping("/info/{id}")
    @Operation(summary = "详情", description = "详情")
    public Response<MaintenanceInfoResponse> info(@PathVariable("id") Long id) {
        return Response.success(maintenanceSheetService.info(id));
    }

    @PostMapping("/export")
    @Operation(summary = "导出", description = "导出")
    public void export(@RequestBody @Validated MaintenanceExportRequest request, HttpServletResponse response) {
        maintenanceSheetService.export(request, response);
    }

    @PostMapping(value = "/import")
    @Operation(summary = "导入报修单数据", description = "导入报修单数据")
    public Response<List<String>> excelImport(@RequestParam(value = "file") MultipartFile file) {
        MaintenanceImportDTO dto = maintenanceSheetService.excelImportCheck(file);
        if (!CollectionUtils.isEmpty(dto.getErrMsg())) {
            return Response.error("500", dto.formatErrMsg());
        }
        maintenanceSheetService.excelImportSave(dto);
        return Response.success();
    }

    @GetMapping("/excel-url")
    @Operation(summary = "导入模板地址", description = "导入模板地址")
    public void excelUrl(HttpServletResponse response) {
        try (ServletOutputStream out = response.getOutputStream()) {
            ResponseEntity<byte[]> responseEntity = fileApi.downloadFile(bucketName, objectPath);
            if (responseEntity.getStatusCode().value() != HttpStatus.OK.value()) {
                log.error("报修单导入模板下载异常 {}", responseEntity.getBody());
                throw new BusinessException("报修单导入模板下载异常");
            }
            response.setCharacterEncoding(CharsetUtil.UTF_8);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.addHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("报修单导入模板", UTF_8) + ".xlsx");

            ExcelWriter writer = ExcelUtil.getReader(new ByteArrayInputStream(Objects.requireNonNull(responseEntity.getBody())))
                    .getWriter();
            writer.flush(out, true);
            writer.close();
        } catch (IOException e) {
            log.error("报修单导入模板下载异常", e);
        }
    }
}