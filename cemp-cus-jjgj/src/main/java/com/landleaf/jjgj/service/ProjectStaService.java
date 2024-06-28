package com.landleaf.jjgj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.landleaf.comm.vo.CommonProjectTreeVO;
import com.landleaf.comm.vo.CommonStaVO;
import com.landleaf.jjgj.domain.dto.ProjectStaKpiDTO;
import com.landleaf.jjgj.domain.vo.ProjectKpiSelectVO;
import com.landleaf.jjgj.domain.vo.ProjectStaKpiDeviceVO;
import com.landleaf.jjgj.domain.vo.ProjectStaSubitemMonthVO;
import com.landleaf.jjgj.domain.vo.ProjectStaSubitemYearVO;
import com.landleaf.jjgj.domain.vo.rjd.*;
import com.landleaf.jjgj.domain.vo.station.StationCurrentStatusVO;
import com.landleaf.jjgj.domain.vo.station.StationRegionVO;


import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Map;


public interface ProjectStaService {
    IPage<Map<String, Object>> getProjectStaData(ProjectStaKpiDTO qry);

    KanbanRJDEnergyVO getRJDEnegy(String bizProjectId);
    KanbanRJDEnergyVO getRJDEnegy2(String bizProjectId);

    KanbanRJDEnergyWeekVO getRJDEnegyWeek(String bizProjectId);

    KanbanRJDEnergyCostVO getRJDEnegyCost(String bizProjectId);

    KanbanRJDEnergyCostVO getRJDEnegyCost(String bizProjectId,String year);

    KanbanRJDEnergyCarbonVO getRJDEnegyCarbon(String bizProjectId);

    List<KanbanRJDEnergyWeeksVO> getRJDEnegyWeek2(String bizProjectId);

    List<KanbanRJDEnergyWeeksVO> getRJDEnegyWeekNew(String bizProjectId,String year,String week) throws ParseException;

    List<StationRegionVO> getRegions(String bizProjectId);

    StationCurrentStatusVO getDeviceCurrent(String bizProjectId, String regionName);

    List<CommonStaVO> getDeviceToday(String bizProjectId, String regionName);

    List<CommonStaVO> getDeviceMonth(String bizProjectId, String regionName);

    IPage<ProjectStaKpiDeviceVO> getDeviceData(ProjectStaKpiDTO qry);

    List<ProjectKpiSelectVO> getProjectKpi(String bizProjectId, String timePeriod);

    KanbanRJDYearEleVO getThisYearEle(String bizProjectId,String boardType);

    List<CommonProjectTreeVO> getProjectTree();

    ProjectStaSubitemMonthVO getMonthSta(String year, String month, List<String> kpiCodes, String bizProjectId);

    ProjectStaSubitemMonthVO getMonthSta(String year,String month,String staBeginDate ,String staEndDate ,List<String> kpiCodes,String bizProjectId);

    ProjectStaSubitemYearVO getYearSta(String year, List<String> kpiCodes, String bizProjectId);

    KanbanRJDYearEleVO getThisYearEle2(String bizProjectId, String boardType,String year);

    KanbanRJDElectricDayVO getRJDElectricDay(String bizProjectId);

    List<CommonStaVO> getCheckinRate(String year);

    BigDecimal getCarbonDensity(String year);

    BigDecimal getElectricityDensity(String year);

    List<CommonStaVO> getCostMonths(String bizProjectIdJjgj, String year);

    List<CommonStaVO> getCostMonthsTrend(String bizProjectIdJjgj, String year);

    List<CommonStaVO> getScreenCostMonths(String projectId, String year);

    List<KanbanRJDElectricityPlanVO> getElectricityPlan(String bizProjectId);

    List<CommonStaVO> getElectricityTrend(String year, String month) throws ParseException;

    List<CommonStaVO> getEnergyAnalysis(String year, String code);

    KanbanRJDEnergyCarbonKeyKpiVO getCarbonKeyKpi(String year);

    KanbanRJDEnergyCarbonAlarmVO carbonAlarm(String year);

    List<CommonStaVO> carbonSubitemOrder(String year);

    KanbanRJDCarbonTrendVO getCarbonTrend(String year);

    List<CommonStaVO> carbonSubareaOrder(String year);

    List<KanbanRJDAlarmVO> getJJAlarm();

    ScreenProjectBasicVO getBasic(String bizProjectIdJjgj);

    ScreenProjectKeyKpiVO getProjectKeyKpi(String bizProjectIdJjgj);
}
