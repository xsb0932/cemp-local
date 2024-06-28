package com.landleaf.energy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.landleaf.energy.domain.dto.ProjectStaKpiDTO;
import com.landleaf.energy.domain.vo.*;
import com.landleaf.energy.domain.vo.rjd.*;
import com.landleaf.energy.domain.vo.station.StationCurrentStatusVO;
import com.landleaf.energy.domain.vo.station.StationRegionVO;

import java.util.List;
import java.util.Map;


public interface ProjectStaService {
    IPage<Map<String, Object>> getProjectStaData(ProjectStaKpiDTO qry);

    KanbanRJDEnergyVO getRJDEnegy(String bizProjectId);
    KanbanRJDEnergyVO getRJDEnegy2(String bizProjectId);

    KanbanRJDEnergyWeekVO getRJDEnegyWeek(String bizProjectId);

    KanbanRJDEnergyCostVO getRJDEnegyCost(String bizProjectId);

    KanbanRJDEnergyCarbonVO getRJDEnegyCarbon(String bizProjectId);

    List<KanbanRJDEnergyWeeksVO> getRJDEnegyWeek2(String bizProjectId);

    List<StationRegionVO> getRegions(String bizProjectId);

    StationCurrentStatusVO getDeviceCurrent(String bizProjectId, String regionName);

    List<CommonStaVO> getDeviceToday(String bizProjectId,String regionName);

    List<CommonStaVO> getDeviceMonth(String bizProjectId, String regionName);

    IPage<ProjectStaKpiDeviceVO> getDeviceData(ProjectStaKpiDTO qry);

    List<ProjectKpiSelectVO> getProjectKpi(String bizProjectId, String timePeriod);

    KanbanRJDYearEleVO getThisYearEle(String bizProjectId,String boardType);

    List<CommonProjectTreeVO> getProjectTree();

    ProjectStaSubitemMonthVO getMonthSta(String year,String month,List<String> kpiCodes,String bizProjectId);

    ProjectStaSubitemMonthVO getMonthSta(String year,String month,String staBeginDate ,String staEndDate ,List<String> kpiCodes,String bizProjectId);

    ProjectStaSubitemYearVO getYearSta(String year,List<String> kpiCodes,String bizProjectId);

    KanbanRJDYearEleVO getThisYearEle2(String bizProjectId, String boardType);

    KanbanRJDElectricDayVO getRJDElectricDay(String bizProjectId);
}
