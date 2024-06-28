package com.landleaf.monitor.controller;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.constance.ErrorCodeEnumConst;
import com.landleaf.comm.constance.PeriodTypeConst;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.data.api.device.dto.DeviceHistoryDTO;
import com.landleaf.monitor.domain.dto.HistoryQueryDTO;
import com.landleaf.monitor.domain.entity.DeviceMonitorEntity;
import com.landleaf.monitor.service.DeviceHistoryService;
import com.landleaf.monitor.service.DeviceMonitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/data/history")
@Tag(name = "设备-历史数据的控制层接口定义", description = "设备-历史数据的控制层接口定义")
@Slf4j
public class DeviceHistoryController {

    @Resource
    private DeviceHistoryService deviceHistoryServiceImpl;

    @Resource
    private DeviceMonitorService deviceMonitorServiceImpl;

    private static final String EXCEL_CONTENT_TYPE = "application/x-msdownload";
    private static final String EXCEL_HEAD_KEY_ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    private static final String EXCEL_HEAD_VALUE_CONTENT_DISPOSITION = "Content-Disposition";
    private static final String EXCEL_HEAD_KEY_CONTENT_DISPOSITION = "Content-disposition";

    private static final String EXCEL_POINT_TEMPLATE_FILE_NAME = "历史数据";

    /**
     * 根据参数查询历史数据
     *
     * @param queryDTO
     */
    @PostMapping("/list")
    @Operation(summary = "查询设备历史数据", description = "")
    public Response<List<DeviceHistoryDTO>> history(@RequestBody HistoryQueryDTO queryDTO) {
        // check param
        if (!StringUtils.hasText(queryDTO.getTimes()[0])) {
            // 失败，必须有开始时间
            throw new BusinessException(ErrorCodeEnumConst.CHECK_PARAM_ERROR);
        }
        if (!StringUtils.hasText(queryDTO.getBizDeviceIds())) {
            // 设备编号不能为控
            throw new BusinessException(ErrorCodeEnumConst.CHECK_PARAM_ERROR);
        }
        if (!StringUtils.hasText(queryDTO.getAttrCode())) {
            // 属性编号不能为控
            throw new BusinessException(ErrorCodeEnumConst.CHECK_PARAM_ERROR);
        }
        if (null == queryDTO.getPeriodType()) {
            queryDTO.setPeriodType(PeriodTypeConst.DEFAULT_PERIOD.getType());
        }

        List<DeviceHistoryDTO> result = deviceHistoryServiceImpl.queryHistory(queryDTO);
        return Response.success(result);
    }

    /**
     * 给锦江空调定制的根据参数查询历史数据
     *
     * @param queryDTO
     */
    @PostMapping("/kt/list")
    @Operation(summary = "查询空调电表功率历史数据", description = "")
    public Response<List<DeviceHistoryDTO>> ktElechistory(@RequestBody HistoryQueryDTO queryDTO) {
        // check param
        if (!StringUtils.hasText(queryDTO.getTimes()[0])) {
            // 失败，必须有开始时间
            throw new BusinessException(ErrorCodeEnumConst.CHECK_PARAM_ERROR);
        }
        if (!StringUtils.hasText(queryDTO.getBizDeviceIds())) {
            // 设备编号不能为控
            throw new BusinessException(ErrorCodeEnumConst.CHECK_PARAM_ERROR);
        }
        if (!StringUtils.hasText(queryDTO.getAttrCode())) {
            // 属性编号不能为控
            throw new BusinessException(ErrorCodeEnumConst.CHECK_PARAM_ERROR);
        }
        if (null == queryDTO.getPeriodType()) {
            queryDTO.setPeriodType(PeriodTypeConst.DEFAULT_PERIOD.getType());
        }

        // 将空调的id换为对应电表的id
        Map<String, String> relationMap = new HashMap<>();

        relationMap.put("D000000000048", "D000000000043");
        relationMap.put("D000000000049", "D000000000041");
        relationMap.put("D000000000050", "D000000000042");
        relationMap.put("D000000000051", "D000000000040");
        relationMap.put("D000000000052", "D000000000039");

        List<DeviceHistoryDTO> result = Lists.newArrayList();
        if (relationMap.containsKey(queryDTO.getBizDeviceIds())) {
            queryDTO.setBizDeviceIds(relationMap.get(queryDTO.getBizDeviceIds()));
            result = deviceHistoryServiceImpl.queryHistory(queryDTO);
        }
        return Response.success(result);
    }

    /**
     * 根据参数查询历史数据
     *
     * @param queryDTO
     */
    @PostMapping("/export")
    @Operation(summary = "导出设备历史数据", description = "")
    public void historyExport(HttpServletResponse resp, @RequestBody HistoryQueryDTO queryDTO) {
        // check param
        if (!StringUtils.hasText(queryDTO.getTimes()[0])) {
            // 失败，必须有开始时间
            throw new BusinessException(ErrorCodeEnumConst.CHECK_PARAM_ERROR);
        }
        if (!StringUtils.hasText(queryDTO.getBizDeviceIds())) {
            // 设备编号不能为控
            throw new BusinessException(ErrorCodeEnumConst.CHECK_PARAM_ERROR);
        }
        if (!StringUtils.hasText(queryDTO.getAttrCode())) {
            // 属性编号不能为控
            throw new BusinessException(ErrorCodeEnumConst.CHECK_PARAM_ERROR);
        }
        if (null == queryDTO.getPeriodType()) {
            queryDTO.setPeriodType(PeriodTypeConst.DEFAULT_PERIOD.getType());
        }

        List<DeviceHistoryDTO> result = deviceHistoryServiceImpl.queryHistory(queryDTO);

        // 将result格式化
        List<String> distinctTimes = new ArrayList<>();
        if (!CollectionUtils.isEmpty(result)) {
            for (DeviceHistoryDTO temp : result) {
                distinctTimes.addAll(temp.getTimes());
            }
            distinctTimes = distinctTimes.stream().distinct().collect(Collectors.toList());
            Collections.sort(distinctTimes, (o1, o2) -> o1.compareTo(o2));
        }

        String[] deviceIds = queryDTO.getBizDeviceIds().split(StrUtil.COMMA);
        String[] attrCodes = queryDTO.getAttrCode().split(StrUtil.COMMA);
        int columnLength = deviceIds.length * attrCodes.length + 1;
        try (ServletOutputStream os = resp.getOutputStream()) {
            //设置请求头数据
            String fileName = URLUtil.encode(EXCEL_POINT_TEMPLATE_FILE_NAME);
            resp.setCharacterEncoding(CharsetUtil.UTF_8);
            resp.setContentType(EXCEL_CONTENT_TYPE);
            resp.addHeader(EXCEL_HEAD_KEY_ACCESS_CONTROL_EXPOSE_HEADERS, EXCEL_HEAD_VALUE_CONTENT_DISPOSITION);
            resp.addHeader(EXCEL_HEAD_KEY_CONTENT_DISPOSITION, "attachment;filename=" + fileName + ".xlsx");
            //获取模板
            ExcelWriter writer = ExcelUtil.getWriter(true);

            // 查询设备信息
            List<DeviceMonitorEntity> devices = deviceMonitorServiceImpl.selectByBizDeviceIds(Arrays.asList(deviceIds));

            Map<String, String> deviceNameMap = devices.stream().collect(Collectors.toMap(DeviceMonitorEntity::getBizDeviceId, DeviceMonitorEntity::getName));

            //设置整体标题
            //标题占用表格长度
            writer.merge(columnLength - 1, "历史记录");

            //各个字段标题
            List<String> header = Lists.newArrayList();
            writer.addHeaderAlias("time", "时间");
            header.add("时间");
            writer.setColumnWidth(0, 60);
            int index = 1;
            for (String deviceId : deviceIds) {
                for (String attrCode : attrCodes) {
                    String key = deviceId + "_" + attrCode;
                    writer.addHeaderAlias(key, deviceNameMap.get(deviceId) + deviceHistoryServiceImpl.queryNameByCode(attrCode));
                    header.add(deviceNameMap.get(deviceId) + deviceHistoryServiceImpl.queryNameByCode(attrCode));
                    writer.setColumnWidth(index++, 30);
                }
            }
            writer.writeHeadRow(header);
            if (!CollectionUtils.isEmpty(distinctTimes)) {
                Map<String, Map<String, String>> valueMap = result.stream().collect(Collectors.toMap(k -> k.getBizDeviceId() + "_" + k.getAttrCode(), v -> v.getValueMap(), (v1, v2) -> v1));
                List<Map<String, Object>> rows = distinctTimes.stream().map(i -> {
                    Map<String, Object> maps = new HashMap<>();
                    maps.put("time", i);
                    for (String deviceId : deviceIds) {
                        for (String attrCode : attrCodes) {
                            String key = deviceId + "_" + attrCode;
                            maps.put(key, valueMap.containsKey(key) && valueMap.get(key).containsKey(i) ? valueMap.get(key).get(i) : "");
                        }
                    }
                    return maps;
                }).collect(Collectors.toList());
                writer.write(rows);
            }
            writer.flush(os);
        } catch (IOException e) {
            log.error("Excel下载异常", e);
        }
    }
}
