package com.landleaf.jjgj.controller;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.sta.enums.EnergyTypeEnum;
import com.landleaf.comm.sta.enums.ProjectConstant;
import com.landleaf.comm.sta.util.DateUtils;
import com.landleaf.comm.sta.util.KpiUtils;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.jjgj.domain.dto.ProjectStaKpiDTO;
import com.landleaf.jjgj.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import com.landleaf.jjgj.domain.vo.*;
import com.landleaf.jjgj.domain.vo.rjd.*;
import com.landleaf.comm.vo.*;
import com.landleaf.jjgj.domain.entity.*;

/**
 * 能源统计接口
 *
 * @author hebin
 * @since 2023-06-22
 */
@RestController
@AllArgsConstructor
@RequestMapping("/sta")
@Tag(name = "能源统计接口", description = "能源统计接口")
public class JJGJEnegyStaController {

    private final ProjectService projectServiceImpl;


    private static final String EXCEL_CONTENT_TYPE = "application/x-msdownload";
    private static final String EXCEL_HEAD_KEY_ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    private static final String EXCEL_HEAD_VALUE_CONTENT_DISPOSITION = "Content-Disposition";
    private static final String EXCEL_HEAD_KEY_CONTENT_DISPOSITION = "Content-disposition";
    private static final String EXCEL_PROJECT_TEMPLATE_FILE_NAME = "项目报表";
    private static final String EXCEL_DEVICE_TEMPLATE_FILE_NAME = "设备报表";

    @Resource
    public DeviceMonitorService deviceMonitorService;

    @Resource
    public ProjectStaService projectStaService;

    @Resource
    public ProjectService projectService;

    @Resource
    public ProjectKpiConfigService projectKpiConfigService;

    @Resource
    public DeviceCategoryKpiConfigService deviceCategoryKpiConfigService;


    /**
     * 锦江定制-入住率趋势查询
     *
     * @return
     */
    @GetMapping("/board/RJD/checkin/rate")
    @Operation(summary ="锦江定制-入住率趋势查询", description = "锦江定制-入住率趋势查询")
    public Response<List<CommonStaVO>> getCheckinRate(@RequestParam("year")String year){
        List<CommonStaVO> result = projectStaService.getCheckinRate(year);
        return Response.success(result);
    }

    /**
     * 锦江定制-碳排放强度
     *
     * @return 每平方总碳排放
     */
    @GetMapping("/board/RJD/carbon/density2")
    @Operation(summary ="锦江定制-酒店能耗强度", description = "锦江定制-酒店能耗强度")
    public Response<BigDecimal> carbonDensity(@RequestParam("year")String year){
        BigDecimal density = projectStaService.getCarbonDensity(year);
        return Response.success(density);
    }

    /**
     * 锦江定制-酒店能耗强度
     *
     * @return 每平方用电
     */
    @GetMapping("/board/RJD/carbon/density")
    @Operation(summary ="锦江定制-酒店能耗强度", description = "锦江定制-酒店能耗强度")
    public Response<BigDecimal> electricityDensity(@RequestParam("year")String year){
        BigDecimal density = projectStaService.getElectricityDensity(year);
        return Response.success(density);
    }


    /**
     * 锦江定制-项目树
     *
     * @return 项目统计-项目树
     */
    @GetMapping("/getProjectsTree")
    @Operation(summary ="锦江定制-项目树", description = "锦江定制-项目树")
    public Response<List<CommonProjectTreeVO>> getProjectsTree(){
        List<CommonProjectTreeVO> projectTreeVOS  =  projectStaService.getProjectTree();
        return Response.success(projectTreeVOS);
    }




    /**
     * 锦江定制-项目概况
     *
     * @return
     */
    @GetMapping("/board/RJD/projectInfo")
    @Operation(summary = "锦江定制-项目概况", description = "锦江定制-项目概况")
    public Response<KanbanRJDProjectVO> getRJDProjectInfo() {
        KanbanRJDProjectVO projectInfo = projectService.getByBizProjectId(ProjectConstant.BIZ_PROJECT_ID_JJGJ);
        return Response.success(projectInfo);
    }

    /**
     * 锦江定制-年用电耗分析
     *
     * @return 锦江看板-当年电耗分析
     */
    @GetMapping("/board/RJD/electricity/year")
    @Operation(summary ="锦江定制-年用电耗分析", description = "锦江定制-年用电耗分析")
    public Response<KanbanRJDYearEleVO> getThisYearEle(@Parameter(description = "能源类型 1:分类 2:分时 3:分区") @RequestParam(value = "boardType",required = true)String boardType,
                                                       @RequestParam(value = "year",required = true)String year){
        KanbanRJDYearEleVO yearEleData= projectStaService.getThisYearEle2(ProjectConstant.BIZ_PROJECT_ID_JJGJ,boardType,year);
        return Response.success(yearEleData);
    }

    /**
     * 锦江定制大屏-月能耗趋势
     * 迭代3.4
     *
     * @return 锦江定制大屏-月能耗趋势
     */
    @GetMapping("/screen/RJD/cost/subitem/months/trend")
    @Operation(summary ="锦江定制大屏-月能耗趋势", description = "锦江定制大屏-月能耗趋势")
    public Response<List<CommonStaVO>> getCostMonthsTrend(@RequestParam(value = "year",required = true)String year){
        List<CommonStaVO> cost= projectStaService.getCostMonthsTrend(ProjectConstant.BIZ_PROJECT_ID_JJGJ,year);
        return Response.success(cost);
    }

    /**
     * 锦江定制大屏-月费用趋势
     * 迭代3.4
     *
     * @return 锦江定制大屏-月费用趋势
     */
    @GetMapping("/screen/RJD/cost/subitem/months")
    @Operation(summary ="锦江定制大屏-月费用趋势", description = "锦江定制大屏-月费用趋势")
    public Response<List<CommonStaVO>> getScreenCostMonths(@RequestParam(value = "year",required = true)String year){
        List<CommonStaVO> cost= projectStaService.getScreenCostMonths(ProjectConstant.BIZ_PROJECT_ID_JJGJ,year);
        return Response.success(cost);
    }

    /**
     * 锦江定制大屏-项目基本信息
     * 迭代3.4
     *
     * @return 锦江定制大屏-项目基本信息
     */
    @GetMapping("/screen/project/basic")
    @Operation(summary ="锦江定制大屏-项目基本信息", description = "锦江定制大屏-项目基本信息")
    public Response<ScreenProjectBasicVO> getProjectBaisc(){
        ScreenProjectBasicVO ScreenProjectBasicVO = projectStaService.getBasic(ProjectConstant.BIZ_PROJECT_ID_JJGJ);
        return Response.success(ScreenProjectBasicVO);
    }

    /**
     * 锦江定制大屏-关键指标
     * 迭代3.4
     *
     * @return 锦江定制大屏-关键指标
     */
    @GetMapping("/screen/project/keyKpi")
    @Operation(summary ="锦江定制大屏-关键指标", description = "锦江定制大屏-关键指标")
    public Response<ScreenProjectKeyKpiVO> getProjectKeyKpi(){
        ScreenProjectKeyKpiVO vo = projectStaService.getProjectKeyKpi(ProjectConstant.BIZ_PROJECT_ID_JJGJ);
        return Response.success(vo);
    }

    /**
     * 锦江定制-分项能源费用按月趋势
     *
     * @return 锦江看板-分项能源费用按月趋势
     */
    @GetMapping("/board/RJD/cost/subitem/months")
    @Operation(summary ="锦江定制-分项能源费用按月趋势", description = "锦江定制-分项能源费用按月趋势")
    public Response<List<CommonStaVO>> getCostMonths(@RequestParam(value = "year",required = true)String year){
        List<CommonStaVO> cost= projectStaService.getCostMonths(ProjectConstant.BIZ_PROJECT_ID_JJGJ,year);
        return Response.success(cost);
    }

    /**
     * 锦江定制-月用电趋势
     *
     * @return 锦江看板-分项能源费用按月趋势
     */
    @GetMapping("/board/RJD/cost/months/trend")
    @Operation(summary ="锦江定制-月用电趋势", description = "锦江定制-月用电趋势")
    public Response<List<CommonStaVO>> getCostTrend(@RequestParam(value = "year",required = true)String year,
                                                    @RequestParam(value = "month",required = true)String month
    ) throws ParseException {
        List<CommonStaVO> cost= projectStaService.getElectricityTrend(year,month);
        return Response.success(cost);
    }

    /**
     * 锦江定制-能耗数据-关键指标
     * @return
     */
    @GetMapping("/board/RJD/enegy/keyKpi")
    @Operation(summary = "锦江定制-能耗数据-关键指标", description = "锦江定制-能耗数据-关键指标")
    public Response<KanbanRJDEnergyVO> getRJDEnegyInfo() {
        KanbanRJDEnergyVO enegy = projectStaService.getRJDEnegy2(ProjectConstant.BIZ_PROJECT_ID_JJGJ);
        return Response.success(enegy);
    }


    /**
     * 锦江定制-七日能耗-周能耗数据
     * @return
     */
    @GetMapping("/board/RJD/energy/week")
    @Operation(summary = "锦江定制-七日能耗-周能耗数据", description = "锦江定制-七日能耗-周能耗数据")
    public Response<List<KanbanRJDEnergyWeeksVO>> getRJDEnegyWeek(@RequestParam("year")String year,
                                                                  @RequestParam("week")String week) throws ParseException {
        List<KanbanRJDEnergyWeeksVO> enegy = projectStaService.getRJDEnegyWeekNew(ProjectConstant.BIZ_PROJECT_ID_JJGJ,year,week);
        return Response.success(enegy);
    }

    /**
     * 锦江定制-用电计划
     * @param
     * @return
     */
    @GetMapping("/board/RJD/electricity/plan")
    @Operation(summary = "锦江定制-用电计划", description = "锦江定制-用电计划")
    public Response<List<KanbanRJDElectricityPlanVO>> getElectricityPlan() {
        List<KanbanRJDElectricityPlanVO> planList = projectStaService.getElectricityPlan(ProjectConstant.BIZ_PROJECT_ID_JJGJ);
        return Response.success(planList);
    }



    /**
     * 锦江定制-年用能分析-水气
     * @param
     * @return
     */
    @GetMapping("/board/RJD/water/analysis/year")
    @Operation(summary = "锦江定制-年用能分析-水气", description = "锦江定制-年用能分析-水气")
    public Response<List<CommonStaVO>> getElectricityAnalysis(
            @Parameter(description = "年份") @RequestParam("year")String year,
            @Parameter(description = "能源类型 1:水 2:电 3:气") @RequestParam("type")String type) {

        List<CommonStaVO> analysis = projectStaService.getEnergyAnalysis(year, type);
        return Response.success(analysis);
    }

    /**
     * 锦江定制-最近预警
     * @param
     * @return
     */
    @GetMapping("/board/RJD/alarm")
    @Operation(summary = "锦江定制-最近预警", description = "锦江定制-最近预警")
    public Response<List<KanbanRJDAlarmVO>> getAlarms() {

        List<KanbanRJDAlarmVO> alarmVOS = new ArrayList<>();
        alarmVOS = projectStaService.getJJAlarm();
//        alarmVOS.add(new KanbanRJDAlarmVO("2023-09-26 19:05:21","电耗预警","电耗预警","已处理"));
//        alarmVOS.add(new KanbanRJDAlarmVO("2023-09-25 19:21:33","电耗预警","电耗预警","已处理"));
//        alarmVOS.add(new KanbanRJDAlarmVO("2023-09-24 19:30:54","电耗预警","电耗预警","已处理"));
//        alarmVOS.add(new KanbanRJDAlarmVO("2023-09-23 19:00:12","电耗预警","电耗预警","已处理"));
        return Response.success(alarmVOS);
    }

    /**
     * 锦江定制-水电气能源费用
     * @param year 统计年
     * @return
     */
    @GetMapping("/board/RJD/energy/cost")
    @Operation(summary = "锦江定制-水电气能源费用", description = "锦江定制-水电气能源费用")
    public Response<KanbanRJDEnergyCostVO> getRJDEnegyCost(@RequestParam("year")String year ) {
        KanbanRJDEnergyCostVO enegy = projectStaService.getRJDEnegyCost(ProjectConstant.BIZ_PROJECT_ID_JJGJ,year);
        return Response.success(enegy);
    }


    /**
     * 碳管理-关键指标
     * @return
     */
    @GetMapping("/board/carbon/keyKpi")
    @Operation(summary = "碳管理-关键指标", description = "碳管理-关键指标")
    public Response<KanbanRJDEnergyCarbonKeyKpiVO> getCarbonKeyKpi() {
        KanbanRJDEnergyCarbonKeyKpiVO kpi = projectStaService.getCarbonKeyKpi(String.valueOf(LocalDateTime.now().getYear()));
        return Response.success(kpi);
    }


    /**
     * 碳管理-年度排放超额预警
     * @param year
     * @return
     */
    @GetMapping("/board/carbon/alarm")
    @Operation(summary = "碳管理-年度排放超额预警", description = "碳管理-年度排放超额预警")
    public Response<KanbanRJDEnergyCarbonAlarmVO> carbonAlarm(@RequestParam("year")String year) {
        KanbanRJDEnergyCarbonAlarmVO alarm = projectStaService.carbonAlarm(year);
        return Response.success(alarm);
    }

    /**
     * 碳管理-碳排排名-分项
     * @param year
     * @return
     */
    @GetMapping("/board/carbon/order/subitem")
    @Operation(summary = "碳管理-碳排排名-分项", description = "碳管理-碳排排名-分项")
    public Response<List<CommonStaVO>> carbonSubitemOrder(@RequestParam("year")String year) {
        List<CommonStaVO>  orders = projectStaService.carbonSubitemOrder(year);
        return Response.success(orders);
    }

    /**
     * 碳管理-碳排排名-分区
     * @param year
     * @return
     */
    @GetMapping("/board/carbon/order/subarea")
    @Operation(summary = "碳管理-碳排排名-分区", description = "碳管理-碳排排名-分区")
    public Response<List<CommonStaVO>> carbonSubareaOrder(@RequestParam("year")String year) {
        List<CommonStaVO>  orders = projectStaService.carbonSubareaOrder(year);
        return Response.success(orders);
    }


    /**
     * 碳管理-碳排趋势
     *
     * @return 锦江看板-碳排趋势
     */
    @GetMapping("/board/carbon/trend")
    @Operation(summary ="碳管理-碳排趋势", description = "碳管理-碳排趋势")
    public Response<KanbanRJDCarbonTrendVO> getCarbonTrend(@RequestParam(value = "year",required = true)String year){
        KanbanRJDCarbonTrendVO trend= projectStaService.getCarbonTrend(year);
        return Response.success(trend);
    }




}
