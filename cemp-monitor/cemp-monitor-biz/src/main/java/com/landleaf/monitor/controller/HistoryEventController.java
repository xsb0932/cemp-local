package com.landleaf.monitor.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.monitor.domain.request.HistoryEventListRequest;
import com.landleaf.monitor.domain.response.AlarmListResponse;
import com.landleaf.monitor.domain.response.AlarmTypeNumResponse;
import com.landleaf.monitor.service.HistoryEventService;
import com.landleaf.monitor.service.UnconfirmedEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 历史事件相关接口
 *
 * @author 张力方
 * @since 2023/8/14
 **/
@RestController
@AllArgsConstructor
@RequestMapping("/event/history")
@Tag(name = "历史事件相关接口", description = "历史事件相关接口")
@Slf4j
public class HistoryEventController {
    private static final String EXCEL_CONTENT_TYPE = "application/x-msdownload";
    private static final String EXCEL_HEAD_KEY_ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    private static final String EXCEL_HEAD_VALUE_CONTENT_DISPOSITION = "Content-Disposition";
    private static final String EXCEL_HEAD_KEY_CONTENT_DISPOSITION = "Content-disposition";
    private static final String EXCEL_POINT_TEMPLATE_FILE_NAME = "历史事件数据";
    private final HistoryEventService historyEventService;
    private final UnconfirmedEventService unconfirmedEventService;

    /**
     * 获取告警类型数量
     *
     * @param request 请求条件
     * @return 告警类型&数量
     */
    @GetMapping("/type/num")
    @Operation(summary = "获取告警类型数量", description = "获取告警类型数量")
    public Response<List<AlarmTypeNumResponse>> getAlarmTypeNum(HistoryEventListRequest request) {
        List<AlarmTypeNumResponse> alarmTypeNum = historyEventService.getAlarmTypeNum(request);
        return Response.success(alarmTypeNum);
    }

    /**
     * 获取历史事件列表
     *
     * @param request 请求条件
     * @return 历史事件列表
     */
    @GetMapping("/page-query")
    @Operation(summary = "获取历史事件列表", description = "获取历史事件列表")
    public Response<Page<AlarmListResponse>> getAlarmResponse(HistoryEventListRequest request) {
        Page<AlarmListResponse> alarmResponse = historyEventService.getAlarmResponse(request);
        return Response.success(alarmResponse);
    }

    /**
     * 导出历史事件数据
     *
     * @param request 请求条件
     */
    @GetMapping("/export")
    @Operation(summary = "导出历史事件数据", description = "导出历史事件数据")
    public void export(HistoryEventListRequest request, HttpServletResponse response) {
        List<AlarmListResponse> alarmExcelResponse = historyEventService.getAlarmExcelResponse(request);
        try (ServletOutputStream os = response.getOutputStream()) {
            //设置请求头数据
            String fileName = URLUtil.encode(EXCEL_POINT_TEMPLATE_FILE_NAME);
            response.setCharacterEncoding(CharsetUtil.UTF_8);
            response.setContentType(EXCEL_CONTENT_TYPE);
            response.addHeader(EXCEL_HEAD_KEY_ACCESS_CONTROL_EXPOSE_HEADERS, EXCEL_HEAD_VALUE_CONTENT_DISPOSITION);
            response.addHeader(EXCEL_HEAD_KEY_CONTENT_DISPOSITION, "attachment;filename=" + fileName + ".xlsx");
            // 获取模板
            ExcelWriter writer = ExcelUtil.getWriter(true);
            // 自定义标题别名
            writer.addHeaderAlias("eventId", "事件id");
            writer.addHeaderAlias("eventTime", "事件发生时间");
            writer.addHeaderAlias("eventTypeName", "事件类型");
            writer.addHeaderAlias("alarmObjTypeName", "告警对象类型");
            writer.addHeaderAlias("objName", "告警对象名称");
            writer.addHeaderAlias("projectName", "所属项目名称");
            writer.addHeaderAlias("alarmBizId", "告警业务id");
            writer.addHeaderAlias("alarmCode", "告警码");
            writer.addHeaderAlias("alarmTypeName", "告警类型");
            writer.addHeaderAlias("alarmLevelName", "告警等级");
            writer.addHeaderAlias("alarmDesc", "告警描述");
            writer.addHeaderAlias("alarmStatusName", "告警状态");
            writer.addHeaderAlias("isConfirm", "确认状态");
            writer.addHeaderAlias("confirmUser", "确认人");
            writer.addHeaderAlias("confirmTime", "确认时间");
            writer.addHeaderAlias("confirmRemark", "确认备注");

            // 默认的，未添加alias的属性也会写出
            writer.setOnlyAlias(true);
            // 合并单元格后的标题行，使用默认标题样式
            writer.merge(15, "历史事件数据");
            // 一次性写出内容，使用默认样式，强制输出标题
            writer.write(alarmExcelResponse, true);
            // 关闭writer，释放内存
            writer.flush(os, true);
            // 关闭writer，释放内存
            writer.close();
        } catch (IOException e) {
            log.error("Excel下载异常", e);
        }
    }

    /**
     * 获取未确认事件数量
     *
     * @return 未确认事件数量
     */
    @GetMapping("/unconfirmed/count")
    @Operation(summary = "获取未确认事件数量", description = "获取未确认事件数量")
    public Response<Integer> getUnconfirmedCount() {
        Long userId = LoginUserUtil.getLoginUserId();
        Map<String, Integer> countMap = unconfirmedEventService.getUnconfirmedCount(userId);
        if (MapUtil.isEmpty(countMap)) {
            return Response.success(0);
        }
        return Response.success(countMap.values().stream().mapToInt(Integer::intValue).sum());
    }

    /**
     * 获取未确认事件
     *
     * @return 未确认事件
     */
    @GetMapping("/unconfirmed/info")
    @Operation(summary = "获取未确认事件", description = "获取未确认事件")
    public Response<AlarmListResponse> getUnconfirmedAlarm(@RequestParam @Nullable @Schema(name = "currentId", description = "当前信息的id，没有传null") Long currentId) {
        AlarmListResponse info = unconfirmedEventService.getUnconfirmedInfo(currentId);
        return Response.success(info);
    }
}
