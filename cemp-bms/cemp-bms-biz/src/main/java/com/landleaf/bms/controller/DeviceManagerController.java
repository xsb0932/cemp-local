package com.landleaf.bms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.entity.DeviceIotEntity;
import com.landleaf.bms.domain.request.DeviceManagerAttributeHistoryRequest;
import com.landleaf.bms.domain.request.DeviceManagerPageRequest;
import com.landleaf.bms.domain.request.DeviceManagerServiceControlRequest;
import com.landleaf.bms.domain.request.FunctionParameterRequest;
import com.landleaf.bms.domain.response.*;
import com.landleaf.bms.service.impl.DeviceManagerService;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.messaging.api.ServiceControlApi;
import com.landleaf.messaging.api.dto.FunctionParameter;
import com.landleaf.messaging.api.dto.SendServiceRequest;
import com.landleaf.monitor.api.dto.DeviceManagerEventHistoryDTO;
import com.landleaf.monitor.api.request.DeviceManagerEventExportRequest;
import com.landleaf.monitor.api.request.DeviceManagerEventPageRequest;
import com.landleaf.oauth.api.UserRpcApi;
import com.landleaf.oauth.api.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/device-manager")
@Tag(name = "设备管理-物联平台接口", description = "设备管理-物联平台接口")
public class DeviceManagerController {
    private final DeviceManagerService deviceManagerService;
    private final UserRpcApi userRpcApi;
    private final ServiceControlApi serviceControlApi;

    @GetMapping("/node-tree")
    @Operation(summary = "获取设备管理分组节点树", description = "获取设备管理分组节点树")
    public Response<DeviceManagerNodeTreeResponse> getDeviceManagerNodeTree() {
        DeviceManagerNodeTreeResponse result = deviceManagerService.getDeviceManagerNodeTree();
        return Response.success(result);
    }

    @GetMapping("/product-tree")
    @Operation(summary = "获取设备管理产品节点树", description = "获取设备管理产品节点树")
    public Response<DeviceManagerProductTreeResponse> getDeviceManagerProductTree() {
        DeviceManagerProductTreeResponse result = deviceManagerService.getDeviceManagerProductTree();
        return Response.success(result);
    }

    @PostMapping("/page")
    @Operation(summary = "分页列表查询", description = "分页列表查询")
    public Response<Page<DeviceManagerPageResponse>> page(@RequestBody DeviceManagerPageRequest request) {
        request.validateParam();
        return Response.success(deviceManagerService.page(request));
    }

    @GetMapping("/detail")
    @Operation(summary = "设备详情", description = "设备详情")
    public Response<DeviceManagerDetailResponse> detail(@RequestParam("bizDeviceId") String bizDeviceId) {
        DeviceManagerDetailResponse result = deviceManagerService.detail(bizDeviceId);
        return Response.success(result);
    }

    @GetMapping("/status-monitor")
    @Operation(summary = "运行监控", description = "运行监控")
    public Response<DeviceManagerMonitorResponse> statusMonitor(@RequestParam("bizDeviceId") String bizDeviceId) {
        DeviceManagerMonitorResponse result = deviceManagerService.statusMonitor(bizDeviceId);
        return Response.success(result);
    }

    @GetMapping("/device-attributes/list")
    @Operation(summary = "设备测点", description = "设备测点")
    public Response<List<DeviceManagerAttributesResponse>> deviceAttributes(@RequestParam("bizDeviceId") String bizDeviceId) {
        List<DeviceManagerAttributesResponse> result = deviceManagerService.deviceAttributes(bizDeviceId);
        return Response.success(result);
    }

    @PostMapping("/device-attributes/history")
    @Operation(summary = "历史数据", description = "历史数据")
    public Response<List<DeviceManagerAttributeHistoryResponse>> deviceAttributesHistory(@Validated @RequestBody DeviceManagerAttributeHistoryRequest request) {
        List<DeviceManagerAttributeHistoryResponse> result = deviceManagerService.deviceAttributesHistory(request);
        return Response.success(result);
    }

    @PostMapping("/device-attributes/history/export")
    @Operation(summary = "历史数据导出", description = "历史数据导出")
    public void deviceAttributesHistoryExport(@Validated @RequestBody DeviceManagerAttributeHistoryRequest request, HttpServletResponse response) {
        try (ServletOutputStream os = response.getOutputStream()) {
            // 设置请求头数据
            String fileName = URLUtil.encode("历史数据");
            setExportResponseHeader(response, fileName);
            // 获取模板
            ExcelWriter writer = ExcelUtil.getWriter(true);
            // 查询历史数据
            List<DeviceManagerAttributeHistoryResponse> historyList = deviceManagerService.deviceAttributesHistory(request);
            // 默认列宽
            writer.setColumnWidth(-1, 30);
            //各个字段标题
            List<String> header = Lists.newArrayList();
            List<Map<String, String>> rows = new ArrayList<>();
            for (DeviceManagerAttributeHistoryResponse history : historyList) {
                String headerAlia = StrUtil.isBlank(history.getUnit()) ? history.getName() : history.getName() + "(" + history.getUnit() + ")";
                writer.addHeaderAlias(history.getCode(), headerAlia);
                header.add(headerAlia);
                List<String> data = history.getData();
                if (CollUtil.isNotEmpty(data)) {
                    int size = data.size();
                    if (CollUtil.isEmpty(rows)) {
                        for (int i = 0; i < size; i++) {
                            rows.add(new HashMap<>(historyList.size()));
                        }
                    }
                    for (int i = 0; i < size; i++) {
                        rows.get(i).put(history.getCode(), data.get(i));
                    }
                }
            }
            writer.writeHeadRow(header);
            writer.write(rows);
            writer.flush(os);
        } catch (Exception e) {
            log.error("历史数据导出异常", e);
        }
    }

    @PostMapping("/device-events/history")
    @Operation(summary = "历史事件", description = "历史事件")
    public Response<Page<DeviceManagerEventHistoryDTO>> deviceEventsHistory(@Validated @RequestBody DeviceManagerEventPageRequest request) {
        Page<DeviceManagerEventHistoryDTO> result = deviceManagerService.deviceEventsHistory(request);
        return Response.success(result);
    }

    @PostMapping("/device-events/history/export")
    @Operation(summary = "历史事件导出", description = "历史事件导出")
    public void deviceEventsHistoryExport(@Validated @RequestBody DeviceManagerEventExportRequest request, HttpServletResponse response) {
        try (ServletOutputStream os = response.getOutputStream()) {
            // 设置请求头数据
            String fileName = URLUtil.encode("历史事件");
            setExportResponseHeader(response, fileName);
            // 获取模板
            ExcelWriter writer = ExcelUtil.getWriter(true);
            // 默认列宽
            writer.setColumnWidth(-1, 30);
            List<DeviceManagerEventHistoryDTO> dataList = deviceManagerService.deviceEventsHistoryExport(request);
            //各个字段标题
            List<String> header = CollUtil.newArrayList(
                    "时间", "事件ID", "事件类型", "事件码", "事件内容", "事件等级", "事件状态", "确认状态"
            );
            List<List<String>> rows = new ArrayList<>();
            for (DeviceManagerEventHistoryDTO data : dataList) {
                List<String> row = new ArrayList<>();
                row.add(LocalDateTimeUtil.format(data.getEventTime(), "yyyy-MM-dd HH:mm:ss"));
                row.add(data.getEventId());
                row.add(data.getAlarmTypeName());
                row.add(data.getAlarmCode());
                row.add(data.getAlarmDesc());
                row.add(data.getAlarmLevelName());
                row.add(data.getAlarmStatusName());
                row.add(null != data.getIsConfirm() && data.getIsConfirm() ? "已确认" : "未确认");
                rows.add(row);
            }
            writer.writeHeadRow(header);
            writer.write(rows);
            writer.flush(os);
        } catch (Exception e) {
            log.error("历史数据导出异常", e);
        }
    }

    private void setExportResponseHeader(HttpServletResponse response, String fileName) {
        response.setCharacterEncoding(CharsetUtil.UTF_8);
        response.setContentType("application/x-msdownload");
        response.addHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
    }

    @PostMapping("/service-control")
    @Operation(summary = "服务控制", description = "服务控制")
    public Response<Boolean> serviceControl(@Validated @RequestBody DeviceManagerServiceControlRequest request) {
        long now = System.currentTimeMillis();
        Long userId = LoginUserUtil.getLoginUserId();
        UserDTO user = userRpcApi.getUserInfo(userId).getCheckedData();
        if (null == user) {
            throw new BusinessException("当前登录用户不存在");
        }
        Boolean checkPassword = userRpcApi.checkUserPassword(userId, request.getPassword()).getCheckedData();
        if (!checkPassword) {
            throw new BusinessException("密码错误");
        }
        DeviceIotEntity device = deviceManagerService.getDeviceEntityByBizDeviceId(request.getBizDeviceId());
        if (null == device) {
            throw new BusinessException("设备不存在");
        }
        TenantContext.setIgnore(true);
        // 服务下发
        SendServiceRequest ssr = new SendServiceRequest();
        ArrayList<FunctionParameter> serviceParameter = new ArrayList<>();
        ssr.setBizDeviceId(device.getBizDeviceId())
                .setBizProjId(device.getBizProjectId())
                .setBizProdId(device.getBizProductId())
                .setSourceDeviceId(device.getSourceDeviceId())
                .setTime(now)
                .setIdentifier(request.getIdentifier())
                .setServiceParameter(serviceParameter)
                .setUserId(user.getId())
                .setUsername(user.getUsername())
                .setNickname(user.getNickname());
        if (null != request.getFunctionParameters()) {
            for (FunctionParameterRequest functionParameter : request.getFunctionParameters()) {
                FunctionParameter parameter = BeanUtil.copyProperties(functionParameter, FunctionParameter.class);
                serviceParameter.add(parameter);
            }
        }
        Boolean flag;
        try {
            Response<Boolean> response = serviceControlApi.sendService(ssr);
            if (!response.isSuccess()) {
                log.error("设备管理-服务控制失败:{}", response.getErrorMsg());
                flag = Boolean.FALSE;
                return Response.success(Boolean.FALSE);
            }
            flag = response.getResult();
//            deviceManagerService.addServiceEvent(
//                    now,
//                    TenantContext.getTenantId(),
//                    user.getNickname() + "(" + user.getUsername() + ")",
//                    device,
//                    request,
//                    flag
//            );
        } catch (Exception e) {
            log.error("设备管理-服务控制失败", e);
            flag = Boolean.FALSE;
            return Response.success(Boolean.FALSE);
        }
        return Response.success(flag);
    }
}
