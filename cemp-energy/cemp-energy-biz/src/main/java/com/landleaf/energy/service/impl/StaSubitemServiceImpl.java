package com.landleaf.energy.service.impl;

import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Maps;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.vo.CommonStaVO;
import com.landleaf.energy.dal.mapper.*;
import com.landleaf.energy.domain.entity.*;
import com.landleaf.energy.domain.vo.ProjectStaSubitemMonthVO;
import com.landleaf.energy.domain.vo.ProjectStaSubitemYearVO;
import com.landleaf.energy.enums.SubitemIndexEnum;
import com.landleaf.energy.request.SubitemRequest;
import com.landleaf.energy.response.SubitemYearRatioResoponse;
import com.landleaf.energy.service.ProjectStaService;
import com.landleaf.energy.service.StaSubitemService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 分项指标查询实现
 *
 * @author yue lin
 * @since 2023/7/28 9:55
 */
@Service
@RequiredArgsConstructor
public class StaSubitemServiceImpl implements StaSubitemService {

    private final ProjectStaSubitemHourMapper projectStaSubitemHourMapper;
    private final ProjectStaSubitemDayMapper projectStaSubitemDayMapper;
    private final ProjectStaSubitemMonthMapper projectStaSubitemMonthMapper;
    private final ProjectStaSubitemYearMapper projectStaSubitemYearMapper;
    private final ProjectCnfPvMapper projectCnfPvMapper;
    private final ProjectCnfChargeStationMapper projectCnfChargeStationMapper;
    private final ProjectCnfTimePeriodMapper projectCnfTimePeriodMapper;
    private final ProjectStaDeviceElectricityHourMapper projectStaDeviceElectricityHourMapper;
    private final ProjectCnfSubitemMapper projectCnfSubitemMapper;
    private final ProjectStaService projectStaService;
    private final ProjectStaDeviceElectricityYearMapper projectStaDeviceElectricityYearMapper;

    @Override
    public Map<LocalDate, Map<SubitemIndexEnum, BigDecimal>> searchDataByDay(String projectBizId, LocalDate[] days, SubitemIndexEnum... indices) {
        TenantContext.setIgnore(true);
        LocalDate now = LocalDate.now();
        LinkedHashMap<LocalDate, Map<SubitemIndexEnum, BigDecimal>> linkedHashMap = Maps.newLinkedHashMap();
        for (LocalDate day : days) {
            EnumMap<SubitemIndexEnum, BigDecimal> decimalEnumMap = Maps.newEnumMap(SubitemIndexEnum.class);
            if (now.isEqual(day)) {
                linkedHashMap.put(day, searchDataByDay(projectBizId, indices));
            } else if (day.isAfter(now)) {
                for (SubitemIndexEnum subitemIndexEnum : indices) {
                    decimalEnumMap.put(subitemIndexEnum, BigDecimal.ZERO);
                }
                linkedHashMap.put(day, decimalEnumMap);
            } else {
                List<ProjectStaSubitemDayEntity> dayEntities = projectStaSubitemDayMapper.selectList(Wrappers.<ProjectStaSubitemDayEntity>lambdaQuery().eq(ProjectStaSubitemDayEntity::getBizProjectId, projectBizId).eq(ProjectStaSubitemDayEntity::getYear, String.valueOf(day.getYear())).eq(ProjectStaSubitemDayEntity::getMonth, String.valueOf(day.getMonthValue())).eq(ProjectStaSubitemDayEntity::getDay, String.valueOf(day.getDayOfMonth())));
                for (SubitemIndexEnum subitemIndexEnum : indices) {
                    BigDecimal reduce = dayEntities.stream().map(it -> ReflectUtil.getFieldValue(it, subitemIndexEnum.getField())).filter(Objects::nonNull).map(it -> (BigDecimal) it).reduce(BigDecimal.ZERO, BigDecimal::add);
                    decimalEnumMap.put(subitemIndexEnum, reduce);
                    linkedHashMap.put(day, decimalEnumMap);
                }
            }
        }
        return linkedHashMap;
    }

    @Override
    public EnumMap<SubitemIndexEnum, BigDecimal> searchDataByDay(String projectBizId, SubitemIndexEnum... indices) {
        TenantContext.setIgnore(true);
        LocalDate now = LocalDate.now();
        List<ProjectStaSubitemHourEntity> hourEntities = projectStaSubitemHourMapper.selectList(Wrappers.<ProjectStaSubitemHourEntity>lambdaQuery().eq(ProjectStaSubitemHourEntity::getBizProjectId, projectBizId).eq(ProjectStaSubitemHourEntity::getYear, String.valueOf(now.getYear())).eq(ProjectStaSubitemHourEntity::getMonth, String.valueOf(now.getMonthValue())).eq(ProjectStaSubitemHourEntity::getDay, String.valueOf(now.getDayOfMonth())));
        EnumMap<SubitemIndexEnum, BigDecimal> decimalEnumMap = Maps.newEnumMap(SubitemIndexEnum.class);
        for (SubitemIndexEnum subitemIndexEnum : indices) {
            decimalEnumMap.put(
                    subitemIndexEnum,
                    todayIndex(hourEntities, subitemIndexEnum, cnfPvPrice(projectBizId), chargeStationPrice(projectBizId), period(projectBizId, now))
            );
        }
        return decimalEnumMap;
    }

    @Override
    public Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> searchDataByMonth(String projectBizId, YearMonth[] months, SubitemIndexEnum... indices) {
        TenantContext.setIgnore(true);
        LinkedHashMap<YearMonth, Map<SubitemIndexEnum, BigDecimal>> linkedHashMap = Maps.newLinkedHashMap();
        YearMonth now = YearMonth.now();
        for (YearMonth month : months) {
            EnumMap<SubitemIndexEnum, BigDecimal> decimalEnumMap = Maps.newEnumMap(SubitemIndexEnum.class);
            if (now.compareTo(month) == 0) {
                linkedHashMap.put(month, searchDataByMonth(projectBizId, indices));
            } else if (month.isAfter(now)) {
                for (SubitemIndexEnum subitemIndexEnum : indices) {
                    decimalEnumMap.put(subitemIndexEnum, BigDecimal.ZERO);
                }
                linkedHashMap.put(month, decimalEnumMap);
            } else {
                List<ProjectStaSubitemMonthEntity> monthEntities = projectStaSubitemMonthMapper.selectList(Wrappers.<ProjectStaSubitemMonthEntity>lambdaQuery().eq(ProjectStaSubitemMonthEntity::getBizProjectId, projectBizId).eq(ProjectStaSubitemMonthEntity::getYear, String.valueOf(month.getYear())).eq(ProjectStaSubitemMonthEntity::getMonth, String.valueOf(month.getMonthValue())));
                for (SubitemIndexEnum subitemIndexEnum : indices) {
                    BigDecimal reduce = monthEntities.stream().map(it -> ReflectUtil.getFieldValue(it, subitemIndexEnum.getField())).filter(Objects::nonNull).map(it -> (BigDecimal) it).reduce(BigDecimal.ZERO, BigDecimal::add);
                    decimalEnumMap.put(subitemIndexEnum, reduce);
                    linkedHashMap.put(month, decimalEnumMap);
                }
            }
        }
        return linkedHashMap;
    }

    @Override
    public EnumMap<SubitemIndexEnum, BigDecimal> searchDataByMonth(String projectBizId, SubitemIndexEnum... indices) {
        TenantContext.setIgnore(true);
        LocalDate now = LocalDate.now();
        List<ProjectStaSubitemDayEntity> dayEntities = projectStaSubitemDayMapper.selectList(Wrappers.<ProjectStaSubitemDayEntity>lambdaQuery().eq(ProjectStaSubitemDayEntity::getBizProjectId, projectBizId).eq(ProjectStaSubitemDayEntity::getYear, String.valueOf(now.getYear())).eq(ProjectStaSubitemDayEntity::getMonth, String.valueOf(now.getMonthValue())));
        List<ProjectStaSubitemHourEntity> hourEntities = projectStaSubitemHourMapper.selectList(Wrappers.<ProjectStaSubitemHourEntity>lambdaQuery().eq(ProjectStaSubitemHourEntity::getBizProjectId, projectBizId).eq(ProjectStaSubitemHourEntity::getYear, String.valueOf(now.getYear())).eq(ProjectStaSubitemHourEntity::getMonth, String.valueOf(now.getMonthValue())).eq(ProjectStaSubitemHourEntity::getDay, String.valueOf(now.getDayOfMonth())));
        EnumMap<SubitemIndexEnum, BigDecimal> decimalEnumMap = Maps.newEnumMap(SubitemIndexEnum.class);
        for (SubitemIndexEnum subitemIndexEnum : indices) {
            BigDecimal reduce = dayEntities.stream().map(it -> ReflectUtil.getFieldValue(it, subitemIndexEnum.getField())).filter(Objects::nonNull).map(it -> (BigDecimal) it).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal reduce1 = todayIndex(hourEntities, subitemIndexEnum, cnfPvPrice(projectBizId), chargeStationPrice(projectBizId), period(projectBizId, now));
            decimalEnumMap.put(subitemIndexEnum, reduce.add(reduce1));
        }
        return decimalEnumMap;
    }

    @Override
    public Map<Year, Map<SubitemIndexEnum, BigDecimal>> searchDataByYear(String projectBizId, Year[] years, SubitemIndexEnum... indices) {
        TenantContext.setIgnore(true);
        LinkedHashMap<Year, Map<SubitemIndexEnum, BigDecimal>> linkedHashMap = Maps.newLinkedHashMap();
        Year now = Year.now();
        for (Year year : years) {
            EnumMap<SubitemIndexEnum, BigDecimal> decimalEnumMap = Maps.newEnumMap(SubitemIndexEnum.class);
            if (now.compareTo(year) == 0) {
                linkedHashMap.put(year, searchDataByYear(projectBizId, indices));
            } else if (year.isAfter(now)) {
                for (SubitemIndexEnum subitemIndexEnum : indices) {
                    decimalEnumMap.put(subitemIndexEnum, BigDecimal.ZERO);
                }
                linkedHashMap.put(year, decimalEnumMap);
            } else {
                List<ProjectStaSubitemYearEntity> yearEntities = projectStaSubitemYearMapper.selectList(Wrappers.<ProjectStaSubitemYearEntity>lambdaQuery().eq(ProjectStaSubitemYearEntity::getBizProjectId, projectBizId).eq(ProjectStaSubitemYearEntity::getYear, String.valueOf(year.getValue())));
                for (SubitemIndexEnum subitemIndexEnum : indices) {
                    BigDecimal reduce = yearEntities.stream().map(it -> ReflectUtil.getFieldValue(it, subitemIndexEnum.getField())).filter(Objects::nonNull).map(it -> (BigDecimal) it).reduce(BigDecimal.ZERO, BigDecimal::add);
                    decimalEnumMap.put(subitemIndexEnum, reduce);
                    linkedHashMap.put(year, decimalEnumMap);
                }
            }
        }
        return linkedHashMap;
    }

    @Override
    public EnumMap<SubitemIndexEnum, BigDecimal> searchDataByYear(String projectBizId, SubitemIndexEnum... indices) {
        TenantContext.setIgnore(true);
        LocalDate now = LocalDate.now();
        List<ProjectStaSubitemMonthEntity> monthEntities = projectStaSubitemMonthMapper.selectList(Wrappers.<ProjectStaSubitemMonthEntity>lambdaQuery().eq(ProjectStaSubitemMonthEntity::getBizProjectId, projectBizId).eq(ProjectStaSubitemMonthEntity::getYear, String.valueOf(now.getYear())));
        List<ProjectStaSubitemDayEntity> dayEntities = projectStaSubitemDayMapper.selectList(Wrappers.<ProjectStaSubitemDayEntity>lambdaQuery().eq(ProjectStaSubitemDayEntity::getBizProjectId, projectBizId).eq(ProjectStaSubitemDayEntity::getYear, String.valueOf(now.getYear())).eq(ProjectStaSubitemDayEntity::getMonth, String.valueOf(now.getMonthValue())));
        List<ProjectStaSubitemHourEntity> hourEntities = projectStaSubitemHourMapper.selectList(Wrappers.<ProjectStaSubitemHourEntity>lambdaQuery().eq(ProjectStaSubitemHourEntity::getBizProjectId, projectBizId).eq(ProjectStaSubitemHourEntity::getYear, String.valueOf(now.getYear())).eq(ProjectStaSubitemHourEntity::getMonth, String.valueOf(now.getMonthValue())).eq(ProjectStaSubitemHourEntity::getDay, String.valueOf(now.getDayOfMonth())));
        EnumMap<SubitemIndexEnum, BigDecimal> decimalEnumMap = Maps.newEnumMap(SubitemIndexEnum.class);
        for (SubitemIndexEnum subitemIndexEnum : indices) {
            BigDecimal reduce = monthEntities.stream().map(it -> ReflectUtil.getFieldValue(it, subitemIndexEnum.getField())).filter(Objects::nonNull).map(it -> (BigDecimal) it).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal reduce1 = dayEntities.stream().map(it -> ReflectUtil.getFieldValue(it, subitemIndexEnum.getField())).filter(Objects::nonNull).map(it -> (BigDecimal) it).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal reduce2 = todayIndex(hourEntities, subitemIndexEnum, cnfPvPrice(projectBizId), chargeStationPrice(projectBizId), period(projectBizId, now));
            decimalEnumMap.put(subitemIndexEnum, reduce.add(reduce1).add(reduce2));
        }
        return decimalEnumMap;
    }

    @Override
    public EnumMap<SubitemIndexEnum, BigDecimal> searchDataByCumulative(String projectBizId, SubitemIndexEnum... indices) {
        TenantContext.setIgnore(true);
        LocalDate now = LocalDate.now();
        List<ProjectStaSubitemYearEntity> yearEntities = projectStaSubitemYearMapper.selectList(Wrappers.<ProjectStaSubitemYearEntity>lambdaQuery().eq(ProjectStaSubitemYearEntity::getBizProjectId, projectBizId));
        List<ProjectStaSubitemMonthEntity> monthEntities = projectStaSubitemMonthMapper.selectList(Wrappers.<ProjectStaSubitemMonthEntity>lambdaQuery().eq(ProjectStaSubitemMonthEntity::getBizProjectId, projectBizId).eq(ProjectStaSubitemMonthEntity::getYear, String.valueOf(now.getYear())));
        List<ProjectStaSubitemDayEntity> dayEntities = projectStaSubitemDayMapper.selectList(Wrappers.<ProjectStaSubitemDayEntity>lambdaQuery().eq(ProjectStaSubitemDayEntity::getBizProjectId, projectBizId).eq(ProjectStaSubitemDayEntity::getYear, String.valueOf(now.getYear())).eq(ProjectStaSubitemDayEntity::getMonth, String.valueOf(now.getMonthValue())));
        List<ProjectStaSubitemHourEntity> hourEntities = projectStaSubitemHourMapper.selectList(Wrappers.<ProjectStaSubitemHourEntity>lambdaQuery().eq(ProjectStaSubitemHourEntity::getBizProjectId, projectBizId).eq(ProjectStaSubitemHourEntity::getYear, String.valueOf(now.getYear())).eq(ProjectStaSubitemHourEntity::getMonth, String.valueOf(now.getMonthValue())).eq(ProjectStaSubitemHourEntity::getDay, String.valueOf(now.getDayOfMonth())));
        EnumMap<SubitemIndexEnum, BigDecimal> decimalEnumMap = Maps.newEnumMap(SubitemIndexEnum.class);
        for (SubitemIndexEnum subitemIndexEnum : indices) {
            BigDecimal reduce = yearEntities.stream().map(it -> ReflectUtil.getFieldValue(it, subitemIndexEnum.getField())).filter(Objects::nonNull).map(it -> (BigDecimal) it).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal reduce1 = monthEntities.stream().map(it -> ReflectUtil.getFieldValue(it, subitemIndexEnum.getField())).filter(Objects::nonNull).map(it -> (BigDecimal) it).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal reduce2 = dayEntities.stream().map(it -> ReflectUtil.getFieldValue(it, subitemIndexEnum.getField())).filter(Objects::nonNull).map(it -> (BigDecimal) it).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal reduce3 = todayIndex(hourEntities, subitemIndexEnum, cnfPvPrice(projectBizId), chargeStationPrice(projectBizId), period(projectBizId, now));
            decimalEnumMap.put(subitemIndexEnum, reduce.add(reduce1).add(reduce2).add(reduce3));
        }
        return decimalEnumMap;
    }

    private BigDecimal getValue(SubitemIndexEnum key, Map<SubitemIndexEnum, BigDecimal> values) {
        if (values == null) {
            return null;
        }
        return values.get(key);
    }

    @Override
    public EnumMap<SubitemIndexEnum, BigDecimal> searchDataByMonthCumulative(String projectBizId, YearMonth month, SubitemIndexEnum... indices) {
        List<YearMonth> months = IntStream.range(1, month.getMonthValue() + 1).mapToObj(value -> YearMonth.of(month.getYear(), value)).collect(Collectors.toList());
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> result = this.searchDataByMonth(projectBizId, months.toArray(new YearMonth[]{}), indices);
        EnumMap<SubitemIndexEnum, BigDecimal> decimalEnumMap = Maps.newEnumMap(SubitemIndexEnum.class);
        for (SubitemIndexEnum subitemIndexEnum : indices) {
            BigDecimal total = months.stream().map(ym -> getValue(subitemIndexEnum, result.get(ym))).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
            decimalEnumMap.put(subitemIndexEnum, total);
        }
        return decimalEnumMap;
    }

    @Override
    public Map<String, BigDecimal> searchDeviceDataByDay(String[] deviceBizIds, LocalDate day) {
        TenantContext.setIgnore(true);
        return projectStaDeviceElectricityHourMapper.selectList(
                        Wrappers.<ProjectStaDeviceElectricityHourEntity>lambdaQuery()
                                .in(ProjectStaDeviceElectricityHourEntity::getBizDeviceId, deviceBizIds)
                                .eq(ProjectStaDeviceElectricityHourEntity::getYear, String.valueOf(day.getYear()))
                                .eq(ProjectStaDeviceElectricityHourEntity::getMonth, String.valueOf(day.getMonthValue()))
                                .eq(ProjectStaDeviceElectricityHourEntity::getDay, String.valueOf(day.getDayOfMonth()))
                ).stream()
                .filter(it -> Objects.nonNull(it.getEnergymeterEpimportStart()))
                .collect(Collectors.groupingBy(
                        ProjectStaDeviceElectricityHourEntity::getBizDeviceId,
                        Collectors.mapping(ProjectStaDeviceElectricityHourEntity::getEnergymeterEpimportTotal, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
    }

    @Override
    public Map<String, BigDecimal> searchDeviceEpimport(String[] deviceBizIds, LocalDate day) {
        TenantContext.setIgnore(true);
        return projectStaDeviceElectricityHourMapper.selectList(
                        Wrappers.<ProjectStaDeviceElectricityHourEntity>lambdaQuery()
                                .in(ProjectStaDeviceElectricityHourEntity::getBizDeviceId, deviceBizIds)
                                .eq(ProjectStaDeviceElectricityHourEntity::getYear, String.valueOf(day.getYear()))
                                .eq(ProjectStaDeviceElectricityHourEntity::getMonth, String.valueOf(day.getMonthValue()))
                                .eq(ProjectStaDeviceElectricityHourEntity::getDay, String.valueOf(day.getDayOfMonth()))
                ).stream()
                .filter(it -> Objects.nonNull(it.getEnergymeterEpimportStart()))
                .collect(Collectors.groupingBy(
                        ProjectStaDeviceElectricityHourEntity::getBizDeviceId,
                        Collectors.mapping(ProjectStaDeviceElectricityHourEntity::getEnergymeterEpimportTotal, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
    }

    @Override
    public Map<String, BigDecimal> searchDeviceEpexport(String[] deviceBizIds, LocalDate day) {
        TenantContext.setIgnore(true);
        return projectStaDeviceElectricityHourMapper.selectList(
                        Wrappers.<ProjectStaDeviceElectricityHourEntity>lambdaQuery()
                                .in(ProjectStaDeviceElectricityHourEntity::getBizDeviceId, deviceBizIds)
                                .eq(ProjectStaDeviceElectricityHourEntity::getYear, String.valueOf(day.getYear()))
                                .eq(ProjectStaDeviceElectricityHourEntity::getMonth, String.valueOf(day.getMonthValue()))
                                .eq(ProjectStaDeviceElectricityHourEntity::getDay, String.valueOf(day.getDayOfMonth()))
                ).stream()
                .filter(it -> Objects.nonNull(it.getEnergymeterEpimportStart()))
                .collect(Collectors.groupingBy(
                        ProjectStaDeviceElectricityHourEntity::getBizDeviceId,
                        Collectors.mapping(ProjectStaDeviceElectricityHourEntity::getEnergymeterEpexportTotal, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
    }

    private Object getValue(Object subitem, Class cla, String methodName) {
        try {
            Method method = cla.getDeclaredMethod(methodName);
            Object result = method.invoke(subitem);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    private String kpiCodeToProperty(String kpi, Class c) {
        String[] kpis = kpi.split("\\.");
        StringBuilder property = new StringBuilder("get");
        String kpiProperty = String.join("", kpis);
        //通过kpiProperty 匹配真实的字段
        List<Field> fields = Arrays.asList(c.getDeclaredFields());
        Map<String, String> fieldMap = fields.stream().collect(Collectors.toMap(f -> f.getName().toUpperCase(), f -> f.getName()));
        String realProperty = fieldMap.get(kpiProperty.toUpperCase());
        return new StringBuilder("get").append(realProperty.substring(0, 1).toUpperCase()).append(realProperty.substring(1)).toString();
    }

    @Override
    public SubitemYearRatioResoponse getSubitemYearRatio(SubitemRequest subitemRequest) {
        TenantContext.setTenantId(subitemRequest.getTenantId());
        SubitemYearRatioResoponse response = new SubitemYearRatioResoponse();
        String[] barChartXList = new String[]{"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
        List<com.landleaf.comm.vo.CommonStaVO> barChartData = new ArrayList<>();
        List<com.landleaf.comm.vo.CommonStaVO> pieChartData = new ArrayList<>();
        Integer year = LocalDateTime.now().getYear();
        LocalDateTime now = LocalDateTime.now();


//        //查询一级分项下所有kpi
//        TenantContext.setIgnore(true);
//        List<ProjectKpiConfigEntity> topCnfs = projectCnfSubitemMapper.getTopLevelCnfs(projectId);
//        TenantContext.setIgnore(false);
//        List<String> kpis = topCnfs.stream().map(ProjectKpiConfigEntity::getCode).collect(Collectors.toList());
//        List<ProjectStaSubitemMonthEntity> monthDatas = projectStaSubitemMonthMapper.getEleYearData(projectId, String.valueOf(year));
//        //查询年数据
//        ProjectStaSubitemYearVO yearDataVO = projectStaService.getYearSta(String.valueOf(now.getYear()), kpis, projectId);
//        ProjectStaSubitemYearEntity yearData = new ProjectStaSubitemYearEntity();
//        BeanUtils.copyProperties(yearDataVO, yearData);
//        Map<String, ProjectStaSubitemMonthEntity> yearMapData = monthDatas.stream().collect(
//                Collectors.toMap(ProjectStaSubitemMonthEntity::getMonth, t -> t));
//        List<String> kpiList = new ArrayList<>();
//        //饼图
//        List<String> pieChartXList = new ArrayList<>();
//        //饼图 - 分项说明
//        for (ProjectKpiConfigEntity cnf : topCnfs) {
//            pieChartXList.add(cnf.getName());
//        }
//
//        List<String> pieChartYList = new ArrayList<>();
//        List<BigDecimal> pieChartYValueList = new ArrayList<>();
//        BigDecimal total = new BigDecimal(0);
//        if (yearData != null) {
//            for (String kpi : kpiList) {
//                Object yValue = this.getValue(yearData, ProjectStaSubitemYearEntity.class, kpiCodeToProperty(kpi,ProjectStaSubitemMonthEntity.class));
//                BigDecimal byValue = yValue == null ? new BigDecimal(0) : (BigDecimal) yValue;
//                pieChartYValueList.add(byValue);
//                total = total.add(byValue);
//            }
//        }
//        pieChartData.add(new CommonStaVO("饼图数据", pieChartXList, pieChartYValueList.stream().map(bigDecimal -> bigDecimal == null ? null : bigDecimal.toPlainString()).collect(Collectors.toList())));

        //查询一级分项下所有kpi
        TenantContext.setIgnore(true);
        List<ProjectKpiConfigEntity> topCnfs = projectCnfSubitemMapper.getTopLevelCnfs(subitemRequest.getProjectId());
        TenantContext.setIgnore(false);
        List<String> kpis = topCnfs.stream().map(ProjectKpiConfigEntity::getCode).collect(Collectors.toList());

        //查询月数据
        List<ProjectStaSubitemMonthEntity> monthDatas = projectStaSubitemMonthMapper.getEleYearData(subitemRequest.getProjectId(), String.valueOf(year));
        ProjectStaSubitemMonthVO currentMonth = projectStaService.getMonthSta(String.valueOf(now.getYear()), String.format("%02d", now.getMonthValue()), kpis, subitemRequest.getProjectId());
        ProjectStaSubitemMonthEntity currentMonthEntity = new ProjectStaSubitemMonthEntity();
        BeanUtils.copyProperties(currentMonth, currentMonthEntity);
        monthDatas.add(currentMonthEntity);

        //查询年数据
        ProjectStaSubitemYearVO yearDataVO = projectStaService.getYearSta(String.valueOf(now.getYear()), kpis, subitemRequest.getProjectId());
        ProjectStaSubitemYearEntity yearData = new ProjectStaSubitemYearEntity();
        BeanUtils.copyProperties(yearDataVO, yearData);
        Map<String, ProjectStaSubitemMonthEntity> yearMapData = monthDatas.stream().collect(
                Collectors.toMap(ProjectStaSubitemMonthEntity::getMonth, t -> t));
        List<String> kpiList = new ArrayList<>();
        //饼图
        List<String> pieChartXList = new ArrayList<>();
        List<String> pieChartYList = new ArrayList<>();
        //配置柱状图
        for (ProjectKpiConfigEntity cnf : topCnfs) {
            String kpi = cnf.getCode();
            kpiList.add(kpi);
            pieChartXList.add(cnf.getName());
            List<String> barChartYList = new ArrayList<>();
            int index = 1;
            while (index <= 12) {
                if (yearMapData.get(String.valueOf(index)) != null) {
                    Object yValue = this.getValue(yearMapData.get(String.valueOf(index)), ProjectStaSubitemMonthEntity.class, kpiCodeToProperty(kpi, ProjectStaSubitemMonthEntity.class));
                    barChartYList.add((yValue == null ? "" : ((BigDecimal) yValue)).toString());
                } else {
                    barChartYList.add("");
                }

                index++;
            }
            CommonStaVO staVO = new CommonStaVO(cnf.getName(), Arrays.asList(barChartXList), barChartYList);
            barChartData.add(staVO);
        }
        //配置饼图
        List<BigDecimal> pieChartYValueList = new ArrayList<>();
        BigDecimal total = new BigDecimal(0);
        if (yearData != null) {
            for (String kpi : kpiList) {
                Object yValue = this.getValue(yearData, ProjectStaSubitemYearEntity.class, kpiCodeToProperty(kpi, ProjectStaSubitemMonthEntity.class));
                BigDecimal byValue = yValue == null ? new BigDecimal(0) : (BigDecimal) yValue;
                pieChartYValueList.add(byValue);
                total = total.add(byValue);
            }
        }
        pieChartData.add(new CommonStaVO("饼图数据", pieChartXList, pieChartYValueList.stream().map(bigDecimal -> bigDecimal == null ? null : bigDecimal.toPlainString()).collect(Collectors.toList())));


        response.setBarChartData(barChartData);
        response.setPieChartData(pieChartData);
        return response;
    }

    private BigDecimal todayIndex(List<ProjectStaSubitemHourEntity> hourEntities, SubitemIndexEnum subitemIndexEnum, BigDecimal cnfPvPrice, BigDecimal chargeStationPrice, List<ProjectCnfTimePeriodEntity> periods) {
        return switch (subitemIndexEnum) {
            case CARBON_DIOXIDE_EMISSIONS -> carbonDioxideEmissions(hourEntities);
            case CARBON_DIOXIDE_REDUCTION -> carbonDioxideReduction(hourEntities);
            case PHOTOVOLTAIC_REVENUE -> photovoltaicRevenue(hourEntities, cnfPvPrice, periods);
            case ENERGY_STORAGE_REVENUE -> energyStorageRevenue(hourEntities, periods);
            case INCOME_OF_CHARGING_STATION -> incomeOfChargingStation(hourEntities, chargeStationPrice);
            case DIRECT_USE -> directUse(hourEntities);
            case PV_DIRECT_USE -> pvDirectUse(hourEntities);
            default -> other(hourEntities, subitemIndexEnum);
        };
    }


    public BigDecimal carbonDioxideEmissions(List<ProjectStaSubitemHourEntity> hourEntities) {
        return hourEntities
                .stream()
                .map(it -> Objects.requireNonNullElse(it.getProjectElectricityPccEnergyUsageTotal(), BigDecimal.ZERO).multiply(new BigDecimal("0.00042"))
                        .add(Objects.requireNonNullElse(it.getProjectWaterUsageTotal(), BigDecimal.ZERO).multiply(new BigDecimal("0.00185")))
                        .add(Objects.requireNonNullElse(it.getProjectGasUsageTotal(), BigDecimal.ZERO).multiply(new BigDecimal("0.00218"))))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal carbonDioxideReduction(List<ProjectStaSubitemHourEntity> hourEntities) {
        return hourEntities.stream().map(it -> Objects.requireNonNullElse(it.getProjectElectricityPvEnergyProductionTotal(), BigDecimal.ZERO).multiply(new BigDecimal("0.00042"))).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal photovoltaicRevenue(List<ProjectStaSubitemHourEntity> hourEntities, BigDecimal cnfPvPrice, List<ProjectCnfTimePeriodEntity> periods) {
        return hourEntities
                .stream()
                .map(it -> Objects.requireNonNullElse(it.getProjectElectricityPccEnergyProductionTotal(), BigDecimal.ZERO).multiply(cnfPvPrice)
                        .add(Objects.requireNonNullElse(it.getProjectElectricityPvEnergyProductionTotal(), BigDecimal.ZERO)
                                .subtract(Objects.requireNonNullElse(it.getProjectElectricityPccEnergyProductionTotal(), BigDecimal.ZERO))
                                .multiply(electricityPrice(it, periods))))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal energyStorageRevenue(List<ProjectStaSubitemHourEntity> hourEntities, List<ProjectCnfTimePeriodEntity> periods) {
        BigDecimal decimal1 = hourEntities.stream().map(it -> it.getProjectElectricityStorageEnergyUsageTotal().multiply(electricityPrice(it, periods))).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal decimal2 = hourEntities.stream().map(it -> it.getProjectElectricityStorageEnergyProductionTotal().multiply(electricityPrice(it, periods))).reduce(BigDecimal.ZERO, BigDecimal::add);
        return decimal2.subtract(decimal1);
    }

    private BigDecimal incomeOfChargingStation(List<ProjectStaSubitemHourEntity> hourEntities, BigDecimal chargeStationPrice) {
        return hourEntities.stream().map(it -> it.getProjectElectricitySubChargeEnergyTotal().multiply(chargeStationPrice)).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal directUse(List<ProjectStaSubitemHourEntity> hourEntities) {
        BigDecimal reduce = hourEntities.stream().map(ProjectStaSubitemHourEntity::getProjectElectricityPvEnergyProductionTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal reduce1 = hourEntities.stream().map(ProjectStaSubitemHourEntity::getProjectElectricityPccEnergyProductionTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal reduce2 = hourEntities.stream().map(ProjectStaSubitemHourEntity::getProjectElectricityStorageEnergyUsagePvTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        return reduce.subtract(reduce1).subtract(reduce2);
    }

    private BigDecimal pvDirectUse(List<ProjectStaSubitemHourEntity> hourEntities) {
        BigDecimal reduce = hourEntities.stream().map(ProjectStaSubitemHourEntity::getProjectElectricityPvEnergyProductionTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal reduce1 = hourEntities.stream().map(ProjectStaSubitemHourEntity::getProjectElectricityPccEnergyProductionTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal reduce2 = hourEntities.stream().map(ProjectStaSubitemHourEntity::getProjectElectricityStorageEnergyUsagePvTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        return reduce.subtract(reduce1).subtract(reduce2);
    }

//    private BigDecimal energyStorageAndDischargeCapacityTip(List<ProjectStaSubitemHourEntity> hourEntities, Optional<ProjectCnfTimePeriodEntity> tip) {
//        return hourEntities.stream().filter(it -> condition(it, tip)).map(ProjectStaSubitemHourEntity::getProjectElectricityStorageEnergyProductionTotal).reduce(BigDecimal.ONE, BigDecimal::add);
//    }
//
//    private BigDecimal energyStorageAndDischargeCapacityPeak(List<ProjectStaSubitemHourEntity> hourEntities, Optional<ProjectCnfTimePeriodEntity> peak) {
//        return hourEntities.stream().filter(it -> condition(it, peak)).map(ProjectStaSubitemHourEntity::getProjectElectricityStorageEnergyProductionTotal).reduce(BigDecimal.ONE, BigDecimal::add);
//    }

    private BigDecimal other(List<ProjectStaSubitemHourEntity> hourEntities, SubitemIndexEnum subitemIndexEnum) {
        return hourEntities.stream().map(it -> ReflectUtil.getFieldValue(it, subitemIndexEnum.getHourFiled())).filter(Objects::nonNull).map(it -> (BigDecimal) it).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 获取该区间的电价
     *
     * @param hourEntity 时数据
     * @param periods    时条
     * @return 结果
     */
    private BigDecimal electricityPrice(ProjectStaSubitemHourEntity hourEntity, List<ProjectCnfTimePeriodEntity> periods) {
        return periods.stream().filter(it -> Integer.parseInt(hourEntity.getHour()) <= it.getTimeEnd() && Integer.parseInt(hourEntity.getHour()) >= it.getTimeBegin()).map(ProjectCnfTimePeriodEntity::getPrice).findFirst().orElse(BigDecimal.ZERO);
    }

    /**
     * 判断数据是否否和时间条件爱in
     *
     * @param hourEntity 时数据
     * @param period     时条
     * @return 结果
     */
    private boolean condition(ProjectStaSubitemHourEntity hourEntity, Optional<ProjectCnfTimePeriodEntity> period) {
        return period.map(it -> Objects.nonNull(hourEntity) && Integer.parseInt(hourEntity.getHour()) <= it.getTimeEnd() && Integer.parseInt(hourEntity.getHour()) >= it.getTimeBegin()).orElse(false);
    }

    @NotNull
    private BigDecimal chargeStationPrice(String projectBizId) {
        return Optional.ofNullable(projectCnfChargeStationMapper.selectOneByProject(projectBizId)).map(ProjectCnfChargeStationEntity::getPrice).orElse(BigDecimal.ZERO);
    }

    @NotNull
    private BigDecimal cnfPvPrice(String projectBizId) {
        return Optional.ofNullable(projectCnfPvMapper.selectOneByProject(projectBizId)).map(ProjectCnfPvEntity::getPrice).orElse(BigDecimal.ZERO);
    }

    @NotNull
    private List<ProjectCnfTimePeriodEntity> period(String projectBizId, LocalDate date) {
        return projectCnfTimePeriodMapper.searchByProjectYearMonth(projectBizId, date.getYear(), date.getMonthValue());
    }

    @Override
    public Map<String, BigDecimal> searchDeviceEpimportYear(String[] deviceBizId, String year) {
        TenantContext.setIgnore(true);
        if (LocalDate.now().getYear() == Integer.valueOf(year)) {
            return projectStaDeviceElectricityHourMapper.getEpimportYear(Arrays.asList(deviceBizId), year).stream().collect(Collectors.toMap(ProjectStaDeviceElectricityHourEntity::getBizDeviceId, ProjectStaDeviceElectricityHourEntity::getEnergymeterEpimportTotal));
        } else {
            return projectStaDeviceElectricityYearMapper.selectList(new LambdaQueryWrapper<ProjectStaDeviceElectricityYearEntity>()
                    .in(ProjectStaDeviceElectricityYearEntity::getBizDeviceId, deviceBizId)
            ).stream().collect(Collectors.toMap(ProjectStaDeviceElectricityYearEntity::getBizDeviceId, ProjectStaDeviceElectricityYearEntity::getEnergymeterEpimportTotal));
        }

    }

    @Override
    public Map<String, BigDecimal> searchDeviceEpexportYear(String[] deviceBizId, String year) {
        TenantContext.setIgnore(true);
        if (LocalDate.now().getYear() == Integer.valueOf(year)) {
            return projectStaDeviceElectricityHourMapper.getEpexportYear(Arrays.asList(deviceBizId), year).stream().collect(Collectors.toMap(ProjectStaDeviceElectricityHourEntity::getBizDeviceId, ProjectStaDeviceElectricityHourEntity::getEnergymeterEpexportTotal));
        } else {
            return projectStaDeviceElectricityYearMapper.selectList(new LambdaQueryWrapper<ProjectStaDeviceElectricityYearEntity>()
                    .in(ProjectStaDeviceElectricityYearEntity::getBizDeviceId, deviceBizId)
            ).stream().collect(Collectors.toMap(ProjectStaDeviceElectricityYearEntity::getBizDeviceId, ProjectStaDeviceElectricityYearEntity::getEnergymeterEpexportTotal));
        }

    }

    @Override
    public Map<String, Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> batchSearchDataOnlyByMonth(List<String> bizProjectIdList, List<YearMonth> months, List<SubitemIndexEnum> indices) {
        TenantContext.setIgnore(true);
        Map<String, Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> result = Maps.newHashMap();
        bizProjectIdList.forEach(id -> result.put(id, Maps.newLinkedHashMap()));
        YearMonth now = YearMonth.now();
        for (YearMonth month : months) {
            if (!month.isBefore(now)) {
                for (String bizProjectId : bizProjectIdList) {
                    Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> monthMap = result.get(bizProjectId);
                    EnumMap<SubitemIndexEnum, BigDecimal> indexMap = Maps.newEnumMap(SubitemIndexEnum.class);
                    for (SubitemIndexEnum index : indices) {
                        indexMap.put(index, null);
                    }
                    monthMap.put(month, indexMap);
                }
            } else {
                Map<String, ProjectStaSubitemMonthEntity> projectIndexMap = projectStaSubitemMonthMapper.selectList(
                                new LambdaQueryWrapper<ProjectStaSubitemMonthEntity>()
                                        .in(ProjectStaSubitemMonthEntity::getBizProjectId, bizProjectIdList)
                                        .eq(ProjectStaSubitemMonthEntity::getYear, String.valueOf(month.getYear()))
                                        .eq(ProjectStaSubitemMonthEntity::getMonth, String.valueOf(month.getMonthValue())))
                        .stream()
                        .collect(Collectors.toMap(ProjectStaSubitemMonthEntity::getBizProjectId, o -> o, (o1, o2) -> o1));
                for (String bizProjectId : bizProjectIdList) {
                    Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> monthMap = result.get(bizProjectId);
                    EnumMap<SubitemIndexEnum, BigDecimal> indexMap = Maps.newEnumMap(SubitemIndexEnum.class);
                    ProjectStaSubitemMonthEntity data = projectIndexMap.get(bizProjectId);
                    for (SubitemIndexEnum index : indices) {
                        if (null == data) {
                            indexMap.put(index, null);
                            continue;
                        }
                        Object value = ReflectUtil.getFieldValue(data, index.getField());
                        indexMap.put(index, null == value ? null : (BigDecimal) value);
                    }
                    monthMap.put(month, indexMap);
                }
            }
        }
        return result;
    }

}
