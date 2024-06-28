package com.landleaf.bms.controller;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.landleaf.bms.domain.entity.DeviceIotEntity;
import com.landleaf.bms.domain.entity.ProductDeviceParameterEntity;
import com.landleaf.bms.domain.request.DeviceIotRequest;
import com.landleaf.bms.domain.response.DeviceIotInfoResponse;
import com.landleaf.bms.domain.response.DeviceIotResponse;
import com.landleaf.bms.service.DeviceIotService;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.monitor.api.MonitorApi;
import com.landleaf.monitor.dto.DeviceMonitorVO;
import com.landleaf.operatelog.core.annotations.OperateLog;
import com.landleaf.operatelog.core.enums.ModuleTypeEnums;
import com.landleaf.operatelog.core.enums.OperateTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 设备-监测平台的控制层接口定义
 *
 * @author hebin
 * @since 2023-07-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("/device-monitor")
@Tag(name = "设备-物联平台接口", description = "设备-物联平台接口")
public class DeviceIotController {

    private static final String EXCEL_CONTENT_TYPE = "application/x-msdownload";
    private static final String EXCEL_HEAD_KEY_ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    private static final String EXCEL_HEAD_VALUE_CONTENT_DISPOSITION = "Content-Disposition";
    private static final String EXCEL_HEAD_KEY_CONTENT_DISPOSITION = "Content-disposition";
    private static final String EXCEL_DEVICE_TEMPLATE_FILE_NAME = "批量导入模板";


    @Resource
    DeviceIotService deviceIotService;

    private final MonitorApi monitorApi;

    /**
     * 新增设备
     *
     * @param addInfo
     * @return
     */
    @PostMapping("/save")
    @Operation(summary = "新增设备", description = "新增设备")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "添加设备", type = OperateTypeEnum.CREATE)
    public Response save(@RequestBody @Valid DeviceIotRequest addInfo) {

        DeviceIotEntity entity = deviceIotService.add(addInfo);
        DeviceMonitorVO vo = new DeviceMonitorVO();
        BeanUtils.copyProperties(entity, vo);
        Response response = monitorApi.add(vo);
        return Response.success();
    }

    @GetMapping("/info")
    @Operation(description = "编辑设备时获取设备详情")
    public Response<DeviceIotInfoResponse> info(@RequestParam("id") Long id) {
        DeviceIotInfoResponse result = deviceIotService.info(id);
        return Response.success(result);
    }

    /**
     * 删除设备
     *
     * @return
     */
    @PostMapping("/delete")
    @Operation(summary = "删除设备", description = "删除设备")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "删除设备", type = OperateTypeEnum.DELETE)
    public Response delete(@Parameter(description = "设备id") @RequestParam("id") Long id) {
        TenantContext.setIgnore(true);
        DeviceIotEntity entity = deviceIotService.getById(id);
        monitorApi.delete(entity.getBizDeviceId());          //同步监控平台
        deviceIotService.deleteParameters(id);
        deviceIotService.removeById(id);
        return Response.success();
    }

    /**
     * 更新设备
     *
     * @return
     */
    @PostMapping("/edit")
    @Operation(summary = "更新设备", description = "更新设备")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "修改设备", type = OperateTypeEnum.UPDATE)
    public Response edit(@RequestBody @Valid DeviceIotRequest editInfo) {
        DeviceMonitorVO updateVO = new DeviceMonitorVO();
        BeanUtils.copyProperties(editInfo, updateVO);
        monitorApi.edit(updateVO);
        deviceIotService.edit(editInfo);
        return Response.success();
    }

    /**
     * 分页查询
     *
     * @return
     */
    @PostMapping("/list")
    @Operation(summary = "分页查询", description = "分页查询")
    public Response<IPage<DeviceIotResponse>> getProjectStaData(@RequestBody DeviceIotRequest qry) {
        IPage<DeviceIotResponse> page = deviceIotService.getProjectStaData(qry);
        return Response.success(page);
    }

    /**
     * 导出设备
     *
     * @return
     */
    @GetMapping("/export/{productId}")
    @Operation(summary = "下载模板", description = "下载模板")
    public void export(@PathVariable("productId") Long productId, HttpServletResponse resp) throws IOException {
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //所有kpi名称
        TenantContext.setIgnore(true);
        try (ServletOutputStream os = resp.getOutputStream()) {
            String fileName = URLUtil.encode(EXCEL_DEVICE_TEMPLATE_FILE_NAME);
            resp.setCharacterEncoding(CharsetUtil.UTF_8);
            resp.setContentType(EXCEL_CONTENT_TYPE);
            resp.addHeader(EXCEL_HEAD_KEY_ACCESS_CONTROL_EXPOSE_HEADERS, EXCEL_HEAD_VALUE_CONTENT_DISPOSITION);
            resp.addHeader(EXCEL_HEAD_KEY_CONTENT_DISPOSITION, "attachment;filename=" + fileName + ".xlsx");

            List<String> rowDatas = new ArrayList<>();
            rowDatas.add("所属项目");
            rowDatas.add("所属产品");
            rowDatas.add("设备名称");
            rowDatas.add("设备编码");
            rowDatas.add("外部ID");
            rowDatas.add("所属空间");
            rowDatas.add("设备位置");
            rowDatas.add("设备描述");
            //其他参数
            List<ProductDeviceParameterEntity> parameters = deviceIotService.getParameters(productId);
            parameters.forEach(parameter -> rowDatas.add(parameter.getFunctionName()));

            writer.writeHeadRow(rowDatas);
            writer.flush(os);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @PostMapping(value = "/import/{productId}")
    @Operation(summary = "批量导入", description = "批量导入")
    public Response<List<String>> importFamilyInfo(@PathVariable("productId") Long productId, @RequestParam(value = "file", required = true) MultipartFile file, HttpServletResponse response) throws IOException {
        List<String> errMsg = deviceIotService.importFile(file, productId);
        if (errMsg != null && errMsg.size() > 0) {
            return Response.error("500", String.join(";", errMsg));
        } else {
            return Response.success();
        }
    }

}
