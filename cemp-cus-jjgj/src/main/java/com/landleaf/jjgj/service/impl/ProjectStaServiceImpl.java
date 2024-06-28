package com.landleaf.jjgj.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.sta.enums.EnergyTypeEnum;
import com.landleaf.comm.sta.enums.ProjectConstant;
import com.landleaf.comm.sta.enums.StaTimePeriodEnum;
import com.landleaf.comm.sta.util.DateUtils;
import com.landleaf.comm.sta.util.KpiUtils;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.vo.CommonProjectTreeVO;
import com.landleaf.comm.vo.CommonStaVO;
import com.landleaf.comm.vo.CommonStaVODetail;
import com.landleaf.energy.api.SubitemApi;
import com.landleaf.energy.enums.SubitemIndexEnum;
import com.landleaf.energy.request.IndexCumulativeMonthRequest;
import com.landleaf.energy.request.IndexDayRequest;
import com.landleaf.energy.request.IndexMonthRequest;
import com.landleaf.energy.request.IndexYearRequest;
import com.landleaf.influx.condition.WhereCondition;
import com.landleaf.influx.core.InfluxdbTemplate;
import com.landleaf.influx.enums.SqlKeyword;
import com.landleaf.jjgj.dal.mapper.*;
import com.landleaf.jjgj.domain.dto.ProjectStaKpiDTO;
import com.landleaf.jjgj.domain.entity.*;
import com.landleaf.jjgj.domain.vo.*;
import com.landleaf.jjgj.domain.vo.rjd.*;
import com.landleaf.jjgj.domain.vo.station.StationCurrentStatusVO;
import com.landleaf.jjgj.domain.vo.station.StationRegionVO;
import com.landleaf.jjgj.service.ProjectStaService;
import com.landleaf.monitor.api.MonitorApi;
import com.landleaf.monitor.dto.AlarmResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectStaServiceImpl implements ProjectStaService {

    private final SubitemApi subitemApi;
    @Autowired
    MonitorApi monitorApi;
    @Autowired
    ProjectMapper projectMapper;
    @Autowired
    ProjectCnfSubitemMapper projectCnfSubitemMapper;
    @Autowired
    ProjectCnfSubareaMapper projectCnfSubareaMapper;
    @Autowired
    ProjectKpiConfigMapper projectKpiConfigMapper;
    @Autowired
    private InfluxdbTemplate influxdbTemplate;
    @Autowired
    ProjectStaDeviceAirHourMapper projectStaDeviceAirHourMapper;
    @Autowired
    ProjectStaDeviceAirDayMapper projectStaDeviceAirDayMapper;
    @Autowired
    ProjectStaDeviceAirMonthMapper projectStaDeviceAirMonthMapper;
    @Autowired
    ProjectStaDeviceAirYearMapper projectStaDeviceAirYearMapper;

    @Autowired
    ProjectStaDeviceGasHourMapper projectStaDeviceGasHourMapper;
    @Autowired
    ProjectStaDeviceGasDayMapper projectStaDeviceGasDayMapper;
    @Autowired
    ProjectStaDeviceGasMonthMapper projectStaDeviceGasMonthMapper;
    @Autowired
    ProjectStaDeviceGasYearMapper projectStaDeviceGasYearMapper;

    @Autowired
    ProjectStaDeviceElectricityHourMapper projectStaDeviceElectricityHourMapper;
    @Autowired
    ProjectStaDeviceElectricityDayMapper projectStaDeviceElectricityDayMapper;
    @Autowired
    ProjectStaDeviceElectricityMonthMapper projectStaDeviceElectricityMonthMapper;
    @Autowired
    ProjectStaDeviceElectricityYearMapper projectStaDeviceElectricityYearMapper;

    @Autowired
    ProjectStaDeviceZnbHourMapper projectStaDeviceZnbHourMapper;
    @Autowired
    ProjectStaDeviceZnbDayMapper projectStaDeviceZnbDayMapper;
    @Autowired
    ProjectStaDeviceZnbMonthMapper projectStaDeviceZnbMonthMapper;
    @Autowired
    ProjectStaDeviceZnbYearMapper projectStaDeviceZnbYearMapper;
    @Autowired
    ProjectStaDeviceGscnHourMapper projectStaDeviceGscnHourMapper;
    @Autowired
    ProjectStaDeviceGscnDayMapper projectStaDeviceGscnDayMapper;
    @Autowired
    ProjectStaDeviceGscnMonthMapper projectStaDeviceGscnMonthMapper;
    @Autowired
    ProjectStaDeviceGscnYearMapper projectStaDeviceGscnYearMapper;
    @Autowired
    ProjectStaDeviceWaterHourMapper projectStaDeviceWaterHourMapper;
    @Autowired
    ProjectStaDeviceWaterDayMapper projectStaDeviceWaterDayMapper;
    @Autowired
    ProjectStaDeviceWaterMonthMapper projectStaDeviceWaterMonthMapper;
    @Autowired
    ProjectStaDeviceWaterYearMapper projectStaDeviceWaterYearMapper;

    @Autowired
    ProjectStaSubitemHourMapper projectStaSubitemHourMapper;
    @Autowired
    ProjectStaSubitemDayMapper projectStaSubitemDayMapper;
    @Autowired
    ProjectStaSubitemMonthMapper projectStaSubitemMonthMapper;
    @Autowired
    ProjectStaSubitemYearMapper projectStaSubitemYearMapper;

    @Autowired
    ProjectStaSubareaHourMapper projectStaSubareaHourMapper;
    @Autowired
    ProjectStaSubareaDayMapper projectStaSubareaDayMapper;
    @Autowired
    ProjectStaSubareaMonthMapper projectStaSubareaMonthMapper;
    @Autowired
    ProjectStaSubareaYearMapper projectStaSubareaYearMapper;

    @Autowired
    ProjectCnfTimePeriodMapper projectCnfTimePeriodMapper;


    @Autowired
    DeviceMonitorMapper deviceMonitorMapper;

    @Autowired
    CheckinDayMapper checkinDayMapper;
    @Autowired
    CheckinMonthMapper checkinMonthMapper;
    @Autowired
    ProjectPlannedElectricityMapper projectPlannedElectricityMapper;
    @Autowired
    ProjectPlannedWaterMapper projectPlannedWaterMapper;
    @Autowired
    ProjectPlannedGasMapper projectPlannedGasMapper;

    private final String[] X_LIST_YEAR_DESC = new String[]{"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
    private final String[] X_LIST_YEAR = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    private final String[] X_LIST_HALF_YEAR_ONE = new String[]{"01", "02", "03", "04", "05", "06"};
    private final String[] X_LIST_HALF_YEAR_TWO = new String[]{"07", "08", "09", "10", "11", "12"};
    private final String[] X_LIST_QUARTER_ONE = new String[]{"01", "02", "03"};
    private final String[] X_LIST_QUARTER_TWO = new String[]{"04", "05", "06"};
    private final String[] X_LIST_QUARTER_THREE = new String[]{"07", "08", "09"};
    private final String[] X_LIST_QUARTER_FOUR = new String[]{"10", "11", "12"};

    private String transStaTime(String time, String staTimePeriod) {
        if (staTimePeriod.equals(StaTimePeriodEnum.HOUR.getType())) {
            return time;
        } else if (staTimePeriod.equals(StaTimePeriodEnum.DAY.getType())) {
            return time.concat(" 00:00:00");
        } else if (staTimePeriod.equals(StaTimePeriodEnum.MONTH.getType())) {
            return time.concat("-01 00:00:00");
        } else if (staTimePeriod.equals(StaTimePeriodEnum.YEAR.getType())) {
            return time.concat("-01-01 00:00:00");
        }
        return time;
    }

    public static String transStaTime2(String time, String staTimePeriod) {
        if (staTimePeriod.equals(StaTimePeriodEnum.HOUR.getType())) {
            return time;
        } else if (staTimePeriod.equals(StaTimePeriodEnum.DAY.getType())) {
            return time.concat(" 00:00:00");
        } else if (staTimePeriod.equals(StaTimePeriodEnum.MONTH.getType())) {
            return time.concat("-01 00:00:00");
        } else if (staTimePeriod.equals(StaTimePeriodEnum.YEAR.getType())) {
            return time.concat("-01-01 00:00:00");
        }
        return time;
    }

    private void mergePage(IPage origin, IPage target) {
        target.setPages(origin.getPages());
        target.setTotal(origin.getTotal());
        target.setSize(origin.getSize());
        target.setCurrent(origin.getCurrent());
    }

    private String VO2TimeFmt(String year, String month, String day, String hour, String staTimePeriod) {
        if (staTimePeriod.equals(StaTimePeriodEnum.HOUR.getType())) {
            return String.format("%s-%s-%s %s:00:00", year, month, day, hour);
        } else if (staTimePeriod.equals(StaTimePeriodEnum.DAY.getType())) {
            return String.format("%s-%s-%s", year, month, day);
        } else if (staTimePeriod.equals(StaTimePeriodEnum.MONTH.getType())) {
            return String.format("%s-%s", year, month);
        } else if (staTimePeriod.equals(StaTimePeriodEnum.YEAR.getType())) {
            return year;
        }
        return null;
    }

    public IPage<ProjectStaKpiDeviceVO> getElectricityData(ProjectStaKpiDTO qry, Map<String, DeviceMonitorEntity> deviceMap) {
        String timeBegin = "";
        String timeEnd = "";
        IPage<ProjectStaKpiDeviceVO> kpiPage = new Page<>(qry.getPageNo(), qry.getPageSize());
        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.HOUR.getType())) {
            IPage<ProjectStaDeviceElectricityHourEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaDeviceElectricityHourEntity> lqw = new LambdaQueryWrapper<>();
            lqw.ge(ProjectStaDeviceElectricityHourEntity::getStaTime, qry.getBegin());
            lqw.le(ProjectStaDeviceElectricityHourEntity::getStaTime, qry.getEnd());
            if (StringUtils.isNotBlank(qry.getBizProjectId())) {
                lqw.eq(ProjectStaDeviceElectricityHourEntity::getBizProjectId, qry.getBizProjectId());
            }
            if (StringUtils.isNotBlank(qry.getBizDeviceId())) {
                lqw.eq(ProjectStaDeviceElectricityHourEntity::getBizDeviceId, qry.getBizDeviceId());
            }
            if (qry.getBizDeviceIds() != null && qry.getBizDeviceIds().size() > 0) {
                lqw.in(ProjectStaDeviceElectricityHourEntity::getBizDeviceId, qry.getBizDeviceIds());
            }
            lqw.orderByDesc(ProjectStaDeviceElectricityHourEntity::getStaTime);

            page = projectStaDeviceElectricityHourMapper.selectPage(page, lqw);
            List<ProjectStaDeviceElectricityHourEntity> records = page.getRecords();
            List<ProjectStaKpiDeviceVO> kpiRecords = records.stream().map(device -> {
                ProjectStaKpiDeviceVO kpi = new ProjectStaKpiDeviceVO();
                BeanUtils.copyProperties(device, kpi);
                String bizProjectId = device.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.setProjectName(projectEntity.getName());
                kpi.setDeviceName(deviceMap.get(device.getBizDeviceId()).getName());
                kpi.setDeviceCode(deviceMap.get(device.getBizDeviceId()).getCode());
                kpi.setStaTime(VO2TimeFmt(device.getYear(), device.getMonth(), device.getDay(), device.getHour(), StaTimePeriodEnum.HOUR.getType()));
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;

        }
        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.DAY.getType())) {
            IPage<ProjectStaDeviceElectricityDayEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaDeviceElectricityDayEntity> lqw = new LambdaQueryWrapper<>();
            timeBegin = this.transStaTime(qry.getBegin(), StaTimePeriodEnum.DAY.getType());
            timeEnd = DateUtils.get(this.transStaTime(qry.getEnd(), StaTimePeriodEnum.DAY.getType()), StaTimePeriodEnum.DAY.getType());
            lqw.ge(ProjectStaDeviceElectricityDayEntity::getStaTime, timeBegin);
            lqw.lt(ProjectStaDeviceElectricityDayEntity::getStaTime, timeEnd);
            if (StringUtils.isNotBlank(qry.getBizProjectId())) {
                lqw.eq(ProjectStaDeviceElectricityDayEntity::getBizProjectId, qry.getBizProjectId());
            }
            if (StringUtils.isNotBlank(qry.getBizDeviceId())) {
                lqw.eq(ProjectStaDeviceElectricityDayEntity::getBizDeviceId, qry.getBizDeviceId());
            }
            if (qry.getBizDeviceIds() != null && qry.getBizDeviceIds().size() > 0) {
                lqw.in(ProjectStaDeviceElectricityDayEntity::getBizDeviceId, qry.getBizDeviceIds());
            }
            lqw.orderByDesc(ProjectStaDeviceElectricityDayEntity::getStaTime);

            page = projectStaDeviceElectricityDayMapper.selectPage(page, lqw);
            List<ProjectStaDeviceElectricityDayEntity> records = page.getRecords();
            List<ProjectStaKpiDeviceVO> kpiRecords = records.stream().map(device -> {
                ProjectStaKpiDeviceVO kpi = new ProjectStaKpiDeviceVO();
                BeanUtils.copyProperties(device, kpi);
                String bizProjectId = device.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.setProjectName(projectEntity.getName());
                kpi.setDeviceName(deviceMap.get(device.getBizDeviceId()).getName());
                kpi.setDeviceCode(deviceMap.get(device.getBizDeviceId()).getCode());
                kpi.setStaTime(VO2TimeFmt(device.getYear(), device.getMonth(), device.getDay(), null, StaTimePeriodEnum.DAY.getType()));
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;

        }
        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.MONTH.getType())) {
            IPage<ProjectStaDeviceElectricityMonthEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaDeviceElectricityMonthEntity> lqw = new LambdaQueryWrapper<>();
            timeBegin = this.transStaTime(qry.getBegin(), StaTimePeriodEnum.MONTH.getType());
            timeEnd = DateUtils.get(this.transStaTime(qry.getEnd(), StaTimePeriodEnum.MONTH.getType()), StaTimePeriodEnum.MONTH.getType());
            lqw.ge(ProjectStaDeviceElectricityMonthEntity::getStaTime, timeBegin);
            lqw.lt(ProjectStaDeviceElectricityMonthEntity::getStaTime, timeEnd);
            if (StringUtils.isNotBlank(qry.getBizProjectId())) {
                lqw.eq(ProjectStaDeviceElectricityMonthEntity::getBizProjectId, qry.getBizProjectId());
            }
            if (StringUtils.isNotBlank(qry.getBizDeviceId())) {
                lqw.eq(ProjectStaDeviceElectricityMonthEntity::getBizDeviceId, qry.getBizDeviceId());
            }
            if (qry.getBizDeviceIds() != null && qry.getBizDeviceIds().size() > 0) {
                lqw.in(ProjectStaDeviceElectricityMonthEntity::getBizDeviceId, qry.getBizDeviceIds());
            }
            lqw.orderByDesc(ProjectStaDeviceElectricityMonthEntity::getStaTime);
            page = projectStaDeviceElectricityMonthMapper.selectPage(page, lqw);
            List<ProjectStaDeviceElectricityMonthEntity> records = page.getRecords();
            List<ProjectStaKpiDeviceVO> kpiRecords = records.stream().map(device -> {
                ProjectStaKpiDeviceVO kpi = new ProjectStaKpiDeviceVO();
                BeanUtils.copyProperties(device, kpi);
                String bizProjectId = device.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.setProjectName(projectEntity.getName());
                kpi.setDeviceName(deviceMap.get(device.getBizDeviceId()).getName());
                kpi.setDeviceCode(deviceMap.get(device.getBizDeviceId()).getCode());
                kpi.setStaTime(VO2TimeFmt(device.getYear(), device.getMonth(), null, null, StaTimePeriodEnum.MONTH.getType()));
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;
        }

        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.YEAR.getType())) {
            IPage<ProjectStaDeviceElectricityYearEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaDeviceElectricityYearEntity> lqw = new LambdaQueryWrapper<>();
            timeBegin = this.transStaTime(qry.getBegin(), StaTimePeriodEnum.YEAR.getType());
            timeEnd = DateUtils.get(this.transStaTime(qry.getEnd(), StaTimePeriodEnum.YEAR.getType()), StaTimePeriodEnum.YEAR.getType());
            lqw.ge(ProjectStaDeviceElectricityYearEntity::getStaTime, timeBegin);
            lqw.lt(ProjectStaDeviceElectricityYearEntity::getStaTime, timeEnd);
            if (StringUtils.isNotBlank(qry.getBizProjectId())) {
                lqw.eq(ProjectStaDeviceElectricityYearEntity::getBizProjectId, qry.getBizProjectId());
            }
            if (StringUtils.isNotBlank(qry.getBizDeviceId())) {
                lqw.eq(ProjectStaDeviceElectricityYearEntity::getBizDeviceId, qry.getBizDeviceId());
            }
            if (qry.getBizDeviceIds() != null && qry.getBizDeviceIds().size() > 0) {
                lqw.in(ProjectStaDeviceElectricityYearEntity::getBizDeviceId, qry.getBizDeviceIds());
            }
            lqw.orderByDesc(ProjectStaDeviceElectricityYearEntity::getStaTime);
            page = projectStaDeviceElectricityYearMapper.selectPage(page, lqw);
            List<ProjectStaDeviceElectricityYearEntity> records = page.getRecords();
            List<ProjectStaKpiDeviceVO> kpiRecords = records.stream().map(device -> {
                ProjectStaKpiDeviceVO kpi = new ProjectStaKpiDeviceVO();
                BeanUtils.copyProperties(device, kpi);
                String bizProjectId = device.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.setProjectName(projectEntity.getName());
                kpi.setDeviceName(deviceMap.get(device.getBizDeviceId()).getName());
                kpi.setDeviceCode(deviceMap.get(device.getBizDeviceId()).getCode());
                kpi.setStaTime(VO2TimeFmt(device.getYear(), null, null, null, StaTimePeriodEnum.YEAR.getType()));
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;
        }
        return null;
    }


    public static void main(String[] args) {
//        ProjectStaServiceImpl p = new ProjectStaServiceImpl();
        String month = "7月";
        System.out.println(month.substring(0, month.indexOf("月")));

        Class c = ProjectStaSubitemDayEntity.class;
        List<Field> fields = Arrays.asList(c.getDeclaredFields());
        fields.forEach(new Consumer<Field>() {
            @Override
            public void accept(Field field) {
                System.out.println(field.getName());
            }
        });


//		DeviceMonitorServiceImpl de = new DeviceMonitorServiceImpl();
//		String year = "2023";
//		String month = "6";
//		String day = "1";
//		String hour = "0";
//		String test1= String.format("%0" + 2 + "d",Integer.valueOf(day));
//        String year = "2023";
//        String month = "2023-06";
//        String date = "2023-06-23";
//        System.out.println(DateUtils.get(date,StaTimePeriodEnum.DAY.getType()));
//        System.out.println(DateUtils.get(ProjectStaServiceImpl.transStaTime2(month,StaTimePeriodEnum.MONTH.getType()),StaTimePeriodEnum.MONTH.getType()));
//        System.out.println(DateUtils.get(ProjectStaServiceImpl.transStaTime2(year,StaTimePeriodEnum.YEAR.getType()),StaTimePeriodEnum.YEAR.getType()));


//        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
//        DateTimeFormatter fmt2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime l1 = LocalDateTime.parse("2023-06-25 00:00:00", fmt2);
//        System.out.println(l1.getDayOfWeek().name());
//        LocalDateTime time = LocalDateTime.now();
//
//        LocalDateTime end = time.plus(1, ChronoUnit.DAYS);
//        LocalDateTime begin = time.minus(14L, ChronoUnit.DAYS);
//
//        String dateEnd = end.format(fmt);
//        String dateBegin = begin.format(fmt);
//
//        DayOfWeek week = time.getDayOfWeek();
//
//        System.out.println(week.getValue());
//        System.out.println(week.name());
//
//        DayOfWeek MonWeek = week.minus(6L);
//        System.out.println(MonWeek.getValue());
//        System.out.println(MonWeek.name());

    }

    public IPage<ProjectStaKpiDeviceVO> getAirData(ProjectStaKpiDTO qry, Map<String, DeviceMonitorEntity> deviceMap) {
        String timeBegin = "";
        String timeEnd = "";
        IPage<ProjectStaKpiDeviceVO> kpiPage = new Page<>(qry.getPageNo(), qry.getPageSize());
        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.HOUR.getType())) {
            IPage<ProjectStaDeviceAirHourEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaDeviceAirHourEntity> lqw = new LambdaQueryWrapper<>();
            lqw.ge(ProjectStaDeviceAirHourEntity::getStaTime, qry.getBegin());
            lqw.le(ProjectStaDeviceAirHourEntity::getStaTime, qry.getEnd());
            if (StringUtils.isNotBlank(qry.getBizProjectId())) {
                lqw.eq(ProjectStaDeviceAirHourEntity::getBizProjectId, qry.getBizProjectId());
            }
            if (StringUtils.isNotBlank(qry.getBizDeviceId())) {
                lqw.eq(ProjectStaDeviceAirHourEntity::getBizDeviceId, qry.getBizDeviceId());
            }
            if (qry.getBizDeviceIds() != null && qry.getBizDeviceIds().size() > 0) {
                lqw.in(ProjectStaDeviceAirHourEntity::getBizDeviceId, qry.getBizDeviceIds());
            }
            lqw.orderByDesc(ProjectStaDeviceAirHourEntity::getStaTime);

            page = projectStaDeviceAirHourMapper.selectPage(page, lqw);
            List<ProjectStaDeviceAirHourEntity> records = page.getRecords();
            List<ProjectStaKpiDeviceVO> kpiRecords = records.stream().map(device -> {
                ProjectStaKpiDeviceVO kpi = new ProjectStaKpiDeviceVO();
                BeanUtils.copyProperties(device, kpi);
                String bizProjectId = device.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.setProjectName(projectEntity.getName());
                kpi.setDeviceName(deviceMap.get(device.getBizDeviceId()).getName());
                kpi.setDeviceCode(deviceMap.get(device.getBizDeviceId()).getCode());
                kpi.setStaTime(VO2TimeFmt(device.getYear(), device.getMonth(), device.getDay(), device.getHour(), StaTimePeriodEnum.HOUR.getType()));
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;

        }
        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.DAY.getType())) {
            IPage<ProjectStaDeviceAirDayEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaDeviceAirDayEntity> lqw = new LambdaQueryWrapper<>();
            timeBegin = this.transStaTime(qry.getBegin(), StaTimePeriodEnum.DAY.getType());
            timeEnd = DateUtils.get(this.transStaTime(qry.getEnd(), StaTimePeriodEnum.DAY.getType()), StaTimePeriodEnum.DAY.getType());
            lqw.ge(ProjectStaDeviceAirDayEntity::getStaTime, timeBegin);
            lqw.lt(ProjectStaDeviceAirDayEntity::getStaTime, timeEnd);
            if (StringUtils.isNotBlank(qry.getBizProjectId())) {
                lqw.eq(ProjectStaDeviceAirDayEntity::getBizProjectId, qry.getBizProjectId());
            }
            if (StringUtils.isNotBlank(qry.getBizDeviceId())) {
                lqw.eq(ProjectStaDeviceAirDayEntity::getBizDeviceId, qry.getBizDeviceId());
            }
            if (qry.getBizDeviceIds() != null && qry.getBizDeviceIds().size() > 0) {
                lqw.in(ProjectStaDeviceAirDayEntity::getBizDeviceId, qry.getBizDeviceIds());
            }
            lqw.orderByDesc(ProjectStaDeviceAirDayEntity::getStaTime);

            page = projectStaDeviceAirDayMapper.selectPage(page, lqw);
            List<ProjectStaDeviceAirDayEntity> records = page.getRecords();
            List<ProjectStaKpiDeviceVO> kpiRecords = records.stream().map(device -> {
                ProjectStaKpiDeviceVO kpi = new ProjectStaKpiDeviceVO();
                BeanUtils.copyProperties(device, kpi);
                String bizProjectId = device.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.setProjectName(projectEntity.getName());
                kpi.setDeviceName(deviceMap.get(device.getBizDeviceId()).getName());
                kpi.setDeviceCode(deviceMap.get(device.getBizDeviceId()).getCode());
                kpi.setStaTime(VO2TimeFmt(device.getYear(), device.getMonth(), device.getDay(), null, StaTimePeriodEnum.DAY.getType()));
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;

        }
        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.MONTH.getType())) {
            IPage<ProjectStaDeviceAirMonthEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaDeviceAirMonthEntity> lqw = new LambdaQueryWrapper<>();
            timeBegin = this.transStaTime(qry.getBegin(), StaTimePeriodEnum.MONTH.getType());
            timeEnd = DateUtils.get(this.transStaTime(qry.getEnd(), StaTimePeriodEnum.MONTH.getType()), StaTimePeriodEnum.MONTH.getType());
            lqw.ge(ProjectStaDeviceAirMonthEntity::getStaTime, timeBegin);
            lqw.lt(ProjectStaDeviceAirMonthEntity::getStaTime, timeEnd);
            if (StringUtils.isNotBlank(qry.getBizProjectId())) {
                lqw.eq(ProjectStaDeviceAirMonthEntity::getBizProjectId, qry.getBizProjectId());
            }
            if (StringUtils.isNotBlank(qry.getBizDeviceId())) {
                lqw.eq(ProjectStaDeviceAirMonthEntity::getBizDeviceId, qry.getBizDeviceId());
            }
            if (qry.getBizDeviceIds() != null && qry.getBizDeviceIds().size() > 0) {
                lqw.in(ProjectStaDeviceAirMonthEntity::getBizDeviceId, qry.getBizDeviceIds());
            }
            lqw.orderByDesc(ProjectStaDeviceAirMonthEntity::getStaTime);
            page = projectStaDeviceAirMonthMapper.selectPage(page, lqw);
            List<ProjectStaDeviceAirMonthEntity> records = page.getRecords();
            List<ProjectStaKpiDeviceVO> kpiRecords = records.stream().map(device -> {
                ProjectStaKpiDeviceVO kpi = new ProjectStaKpiDeviceVO();
                BeanUtils.copyProperties(device, kpi);
                String bizProjectId = device.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.setProjectName(projectEntity.getName());
                kpi.setDeviceName(deviceMap.get(device.getBizDeviceId()).getName());
                kpi.setDeviceCode(deviceMap.get(device.getBizDeviceId()).getCode());
                kpi.setStaTime(VO2TimeFmt(device.getYear(), device.getMonth(), null, null, StaTimePeriodEnum.MONTH.getType()));
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;
        }

        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.YEAR.getType())) {
            IPage<ProjectStaDeviceAirYearEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaDeviceAirYearEntity> lqw = new LambdaQueryWrapper<>();
            timeBegin = this.transStaTime(qry.getBegin(), StaTimePeriodEnum.YEAR.getType());
            timeEnd = DateUtils.get(this.transStaTime(qry.getEnd(), StaTimePeriodEnum.YEAR.getType()), StaTimePeriodEnum.YEAR.getType());
            lqw.ge(ProjectStaDeviceAirYearEntity::getStaTime, timeBegin);
            lqw.lt(ProjectStaDeviceAirYearEntity::getStaTime, timeEnd);
            if (StringUtils.isNotBlank(qry.getBizProjectId())) {
                lqw.eq(ProjectStaDeviceAirYearEntity::getBizProjectId, qry.getBizProjectId());
            }
            if (StringUtils.isNotBlank(qry.getBizDeviceId())) {
                lqw.eq(ProjectStaDeviceAirYearEntity::getBizDeviceId, qry.getBizDeviceId());
            }
            if (qry.getBizDeviceIds() != null && qry.getBizDeviceIds().size() > 0) {
                lqw.in(ProjectStaDeviceAirYearEntity::getBizDeviceId, qry.getBizDeviceIds());
            }
            lqw.orderByDesc(ProjectStaDeviceAirYearEntity::getStaTime);
            page = projectStaDeviceAirYearMapper.selectPage(page, lqw);
            List<ProjectStaDeviceAirYearEntity> records = page.getRecords();
            List<ProjectStaKpiDeviceVO> kpiRecords = records.stream().map(device -> {
                ProjectStaKpiDeviceVO kpi = new ProjectStaKpiDeviceVO();
                BeanUtils.copyProperties(device, kpi);
                String bizProjectId = device.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.setProjectName(projectEntity.getName());
                kpi.setDeviceName(deviceMap.get(device.getBizDeviceId()).getName());
                kpi.setDeviceCode(deviceMap.get(device.getBizDeviceId()).getCode());
                kpi.setStaTime(VO2TimeFmt(device.getYear(), null, null, null, StaTimePeriodEnum.YEAR.getType()));
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;
        }
        return null;
    }

    public IPage<ProjectStaKpiDeviceVO> getGasData(ProjectStaKpiDTO qry, Map<String, DeviceMonitorEntity> deviceMap) {
        String timeBegin = "";
        String timeEnd = "";
        IPage<ProjectStaKpiDeviceVO> kpiPage = new Page<>(qry.getPageNo(), qry.getPageSize());
        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.HOUR.getType())) {
            IPage<ProjectStaDeviceGasHourEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaDeviceGasHourEntity> lqw = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(qry.getBegin())) {
                lqw.ge(ProjectStaDeviceGasHourEntity::getStaTime, qry.getBegin());
            }
            if (StringUtils.isNotBlank(qry.getEnd())) {
                lqw.le(ProjectStaDeviceGasHourEntity::getStaTime, qry.getEnd());
            }
            if (StringUtils.isNotBlank(qry.getBizProjectId())) {
                lqw.eq(ProjectStaDeviceGasHourEntity::getBizProjectId, qry.getBizProjectId());
            }
            if (StringUtils.isNotBlank(qry.getBizDeviceId())) {
                lqw.eq(ProjectStaDeviceGasHourEntity::getBizDeviceId, qry.getBizDeviceId());
            }
            if (qry.getBizDeviceIds() != null && qry.getBizDeviceIds().size() > 0) {
                lqw.in(ProjectStaDeviceGasHourEntity::getBizDeviceId, qry.getBizDeviceIds());
            }
            lqw.orderByDesc(ProjectStaDeviceGasHourEntity::getStaTime);

            page = projectStaDeviceGasHourMapper.selectPage(page, lqw);
            List<ProjectStaDeviceGasHourEntity> records = page.getRecords();
            List<ProjectStaKpiDeviceVO> kpiRecords = records.stream().map(device -> {
                ProjectStaKpiDeviceVO kpi = new ProjectStaKpiDeviceVO();
                BeanUtils.copyProperties(device, kpi);
                String bizProjectId = device.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.setProjectName(projectEntity.getName());
                kpi.setDeviceName(deviceMap.get(device.getBizDeviceId()).getName());
                kpi.setDeviceCode(deviceMap.get(device.getBizDeviceId()).getCode());
                kpi.setStaTime(VO2TimeFmt(device.getYear(), device.getMonth(), device.getDay(), device.getHour(), StaTimePeriodEnum.HOUR.getType()));
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;

        }
        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.DAY.getType())) {
            IPage<ProjectStaDeviceGasDayEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaDeviceGasDayEntity> lqw = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(qry.getBegin())) {
                timeBegin = this.transStaTime(qry.getBegin(), StaTimePeriodEnum.DAY.getType());
                lqw.ge(ProjectStaDeviceGasDayEntity::getStaTime, timeBegin);
            }
            if (StringUtils.isNotBlank(qry.getEnd())) {
                timeEnd = DateUtils.get(this.transStaTime(qry.getEnd(), StaTimePeriodEnum.DAY.getType()), StaTimePeriodEnum.DAY.getType());
                lqw.lt(ProjectStaDeviceGasDayEntity::getStaTime, timeEnd);
            }
            if (StringUtils.isNotBlank(qry.getBizProjectId())) {
                lqw.eq(ProjectStaDeviceGasDayEntity::getBizProjectId, qry.getBizProjectId());
            }
            if (StringUtils.isNotBlank(qry.getBizDeviceId())) {
                lqw.eq(ProjectStaDeviceGasDayEntity::getBizDeviceId, qry.getBizDeviceId());
            }
            if (qry.getBizDeviceIds() != null && qry.getBizDeviceIds().size() > 0) {
                lqw.in(ProjectStaDeviceGasDayEntity::getBizDeviceId, qry.getBizDeviceIds());
            }
            lqw.orderByDesc(ProjectStaDeviceGasDayEntity::getStaTime);
            page = projectStaDeviceGasDayMapper.selectPage(page, lqw);
            List<ProjectStaDeviceGasDayEntity> records = page.getRecords();
            List<ProjectStaKpiDeviceVO> kpiRecords = records.stream().map(device -> {
                ProjectStaKpiDeviceVO kpi = new ProjectStaKpiDeviceVO();
                BeanUtils.copyProperties(device, kpi);
                String bizProjectId = device.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.setProjectName(projectEntity.getName());
                kpi.setDeviceName(deviceMap.get(device.getBizDeviceId()).getName());
                kpi.setDeviceCode(deviceMap.get(device.getBizDeviceId()).getCode());
                kpi.setStaTime(VO2TimeFmt(device.getYear(), device.getMonth(), device.getDay(), null, StaTimePeriodEnum.DAY.getType()));
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;

        }
        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.MONTH.getType())) {
            IPage<ProjectStaDeviceGasMonthEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaDeviceGasMonthEntity> lqw = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(qry.getBegin())) {
                timeBegin = this.transStaTime(qry.getBegin(), StaTimePeriodEnum.MONTH.getType());
                lqw.ge(ProjectStaDeviceGasMonthEntity::getStaTime, timeBegin);
            }
            if (StringUtils.isNotBlank(qry.getEnd())) {
                timeEnd = DateUtils.get(this.transStaTime(qry.getEnd(), StaTimePeriodEnum.MONTH.getType()), StaTimePeriodEnum.MONTH.getType());
                lqw.lt(ProjectStaDeviceGasMonthEntity::getStaTime, timeEnd);
            }
            if (StringUtils.isNotBlank(qry.getBizProjectId())) {
                lqw.eq(ProjectStaDeviceGasMonthEntity::getBizProjectId, qry.getBizProjectId());
            }
            if (StringUtils.isNotBlank(qry.getBizDeviceId())) {
                lqw.eq(ProjectStaDeviceGasMonthEntity::getBizDeviceId, qry.getBizDeviceId());
            }
            if (qry.getBizDeviceIds() != null && qry.getBizDeviceIds().size() > 0) {
                lqw.in(ProjectStaDeviceGasMonthEntity::getBizDeviceId, qry.getBizDeviceIds());
            }
            lqw.orderByDesc(ProjectStaDeviceGasMonthEntity::getStaTime);
            page = projectStaDeviceGasMonthMapper.selectPage(page, lqw);
            List<ProjectStaDeviceGasMonthEntity> records = page.getRecords();
            List<ProjectStaKpiDeviceVO> kpiRecords = records.stream().map(device -> {
                ProjectStaKpiDeviceVO kpi = new ProjectStaKpiDeviceVO();
                BeanUtils.copyProperties(device, kpi);
                String bizProjectId = device.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.setProjectName(projectEntity.getName());
                kpi.setDeviceName(deviceMap.get(device.getBizDeviceId()).getName());
                kpi.setDeviceCode(deviceMap.get(device.getBizDeviceId()).getCode());
                kpi.setStaTime(VO2TimeFmt(device.getYear(), device.getMonth(), null, null, StaTimePeriodEnum.MONTH.getType()));
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;
        }

        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.YEAR.getType())) {
            IPage<ProjectStaDeviceGasYearEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaDeviceGasYearEntity> lqw = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(qry.getBegin())) {
                timeBegin = this.transStaTime(qry.getBegin(), StaTimePeriodEnum.YEAR.getType());
                lqw.ge(ProjectStaDeviceGasYearEntity::getStaTime, timeBegin);
            }
            if (StringUtils.isNotBlank(qry.getEnd())) {
                timeEnd = DateUtils.get(this.transStaTime(qry.getEnd(), StaTimePeriodEnum.YEAR.getType()), StaTimePeriodEnum.YEAR.getType());
                lqw.lt(ProjectStaDeviceGasYearEntity::getStaTime, timeEnd);
            }
            if (StringUtils.isNotBlank(qry.getBizProjectId())) {
                lqw.eq(ProjectStaDeviceGasYearEntity::getBizProjectId, qry.getBizProjectId());
            }
            if (StringUtils.isNotBlank(qry.getBizDeviceId())) {
                lqw.eq(ProjectStaDeviceGasYearEntity::getBizDeviceId, qry.getBizDeviceId());
            }
            if (qry.getBizDeviceIds() != null && qry.getBizDeviceIds().size() > 0) {
                lqw.in(ProjectStaDeviceGasYearEntity::getBizDeviceId, qry.getBizDeviceIds());
            }
            lqw.orderByDesc(ProjectStaDeviceGasYearEntity::getStaTime);
            page = projectStaDeviceGasYearMapper.selectPage(page, lqw);
            List<ProjectStaDeviceGasYearEntity> records = page.getRecords();
            List<ProjectStaKpiDeviceVO> kpiRecords = records.stream().map(device -> {
                ProjectStaKpiDeviceVO kpi = new ProjectStaKpiDeviceVO();
                BeanUtils.copyProperties(device, kpi);
                String bizProjectId = device.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.setProjectName(projectEntity.getName());
                kpi.setDeviceName(deviceMap.get(device.getBizDeviceId()).getName());
                kpi.setDeviceCode(deviceMap.get(device.getBizDeviceId()).getCode());
                kpi.setStaTime(VO2TimeFmt(device.getYear(), null, null, null, StaTimePeriodEnum.YEAR.getType()));
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;
        }
        return null;

    }


    /**
     * 设备-水
     *
     * @param qry
     * @return
     */
    public IPage<ProjectStaKpiDeviceVO> getWaterData(ProjectStaKpiDTO qry, Map<String, DeviceMonitorEntity> deviceMap) {
        String timeBegin = "";
        String timeEnd = "";
        IPage<ProjectStaKpiDeviceVO> kpiPage = new Page<>(qry.getPageNo(), qry.getPageSize());
        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.HOUR.getType())) {
            IPage<ProjectStaDeviceWaterHourEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaDeviceWaterHourEntity> lqw = new LambdaQueryWrapper<>();
            lqw.ge(ProjectStaDeviceWaterHourEntity::getStaTime, qry.getBegin());
            lqw.le(ProjectStaDeviceWaterHourEntity::getStaTime, qry.getEnd());
            if (StringUtils.isNotBlank(qry.getBizProjectId())) {
                lqw.eq(ProjectStaDeviceWaterHourEntity::getBizProjectId, qry.getBizProjectId());
            }
            if (StringUtils.isNotBlank(qry.getBizDeviceId())) {
                lqw.eq(ProjectStaDeviceWaterHourEntity::getBizDeviceId, qry.getBizDeviceId());
            }
            if (qry.getBizDeviceIds() != null && qry.getBizDeviceIds().size() > 0) {
                lqw.in(ProjectStaDeviceWaterHourEntity::getBizDeviceId, qry.getBizDeviceIds());
            }
            lqw.orderByDesc(ProjectStaDeviceWaterHourEntity::getStaTime);

            page = projectStaDeviceWaterHourMapper.selectPage(page, lqw);
            List<ProjectStaDeviceWaterHourEntity> records = page.getRecords();
            List<ProjectStaKpiDeviceVO> kpiRecords = records.stream().map(device -> {
                ProjectStaKpiDeviceVO kpi = new ProjectStaKpiDeviceVO();
                BeanUtils.copyProperties(device, kpi);
                String bizProjectId = device.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.setProjectName(projectEntity.getName());
                kpi.setDeviceName(deviceMap.get(device.getBizDeviceId()).getName());
                kpi.setDeviceCode(deviceMap.get(device.getBizDeviceId()).getCode());
                kpi.setStaTime(VO2TimeFmt(device.getYear(), device.getMonth(), device.getDay(), device.getHour(), StaTimePeriodEnum.HOUR.getType()));
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;

        }
        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.DAY.getType())) {
            IPage<ProjectStaDeviceWaterDayEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaDeviceWaterDayEntity> lqw = new LambdaQueryWrapper<>();
            timeBegin = this.transStaTime(qry.getBegin(), StaTimePeriodEnum.DAY.getType());
            timeEnd = DateUtils.get(this.transStaTime(qry.getEnd(), StaTimePeriodEnum.DAY.getType()), StaTimePeriodEnum.DAY.getType());
            lqw.ge(ProjectStaDeviceWaterDayEntity::getStaTime, timeBegin);
            lqw.lt(ProjectStaDeviceWaterDayEntity::getStaTime, timeEnd);
            if (StringUtils.isNotBlank(qry.getBizProjectId())) {
                lqw.eq(ProjectStaDeviceWaterDayEntity::getBizProjectId, qry.getBizProjectId());
            }
            if (StringUtils.isNotBlank(qry.getBizDeviceId())) {
                lqw.eq(ProjectStaDeviceWaterDayEntity::getBizDeviceId, qry.getBizDeviceId());
            }
            if (qry.getBizDeviceIds() != null && qry.getBizDeviceIds().size() > 0) {
                lqw.in(ProjectStaDeviceWaterDayEntity::getBizDeviceId, qry.getBizDeviceIds());
            }
            lqw.orderByDesc(ProjectStaDeviceWaterDayEntity::getStaTime);

            page = projectStaDeviceWaterDayMapper.selectPage(page, lqw);
            List<ProjectStaDeviceWaterDayEntity> records = page.getRecords();
            List<ProjectStaKpiDeviceVO> kpiRecords = records.stream().map(device -> {
                ProjectStaKpiDeviceVO kpi = new ProjectStaKpiDeviceVO();
                BeanUtils.copyProperties(device, kpi);
                String bizProjectId = device.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.setProjectName(projectEntity.getName());
                kpi.setDeviceName(deviceMap.get(device.getBizDeviceId()).getName());
                kpi.setDeviceCode(deviceMap.get(device.getBizDeviceId()).getCode());
                kpi.setStaTime(VO2TimeFmt(device.getYear(), device.getMonth(), device.getDay(), null, StaTimePeriodEnum.DAY.getType()));
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;

        }
        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.MONTH.getType())) {
            IPage<ProjectStaDeviceWaterMonthEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaDeviceWaterMonthEntity> lqw = new LambdaQueryWrapper<>();
            timeBegin = this.transStaTime(qry.getBegin(), StaTimePeriodEnum.MONTH.getType());
            timeEnd = DateUtils.get(this.transStaTime(qry.getEnd(), StaTimePeriodEnum.MONTH.getType()), StaTimePeriodEnum.MONTH.getType());
            lqw.ge(ProjectStaDeviceWaterMonthEntity::getStaTime, timeBegin);
            lqw.lt(ProjectStaDeviceWaterMonthEntity::getStaTime, timeEnd);
            if (StringUtils.isNotBlank(qry.getBizProjectId())) {
                lqw.eq(ProjectStaDeviceWaterMonthEntity::getBizProjectId, qry.getBizProjectId());
            }
            if (StringUtils.isNotBlank(qry.getBizDeviceId())) {
                lqw.eq(ProjectStaDeviceWaterMonthEntity::getBizDeviceId, qry.getBizDeviceId());
            }
            if (qry.getBizDeviceIds() != null && qry.getBizDeviceIds().size() > 0) {
                lqw.in(ProjectStaDeviceWaterMonthEntity::getBizDeviceId, qry.getBizDeviceIds());
            }
            lqw.orderByDesc(ProjectStaDeviceWaterMonthEntity::getStaTime);
            page = projectStaDeviceWaterMonthMapper.selectPage(page, lqw);
            List<ProjectStaDeviceWaterMonthEntity> records = page.getRecords();
            List<ProjectStaKpiDeviceVO> kpiRecords = records.stream().map(device -> {
                ProjectStaKpiDeviceVO kpi = new ProjectStaKpiDeviceVO();
                BeanUtils.copyProperties(device, kpi);
                String bizProjectId = device.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.setProjectName(projectEntity.getName());
                kpi.setDeviceName(deviceMap.get(device.getBizDeviceId()).getName());
                kpi.setDeviceCode(deviceMap.get(device.getBizDeviceId()).getCode());
                kpi.setStaTime(VO2TimeFmt(device.getYear(), device.getMonth(), null, null, StaTimePeriodEnum.MONTH.getType()));
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;
        }

        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.YEAR.getType())) {
            IPage<ProjectStaDeviceWaterYearEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaDeviceWaterYearEntity> lqw = new LambdaQueryWrapper<>();
            timeBegin = this.transStaTime(qry.getBegin(), StaTimePeriodEnum.YEAR.getType());
            timeEnd = DateUtils.get(this.transStaTime(qry.getEnd(), StaTimePeriodEnum.YEAR.getType()), StaTimePeriodEnum.YEAR.getType());
            lqw.ge(ProjectStaDeviceWaterYearEntity::getStaTime, timeBegin);
            lqw.lt(ProjectStaDeviceWaterYearEntity::getStaTime, timeEnd);
            if (StringUtils.isNotBlank(qry.getBizProjectId())) {
                lqw.eq(ProjectStaDeviceWaterYearEntity::getBizProjectId, qry.getBizProjectId());
            }
            if (StringUtils.isNotBlank(qry.getBizDeviceId())) {
                lqw.eq(ProjectStaDeviceWaterYearEntity::getBizDeviceId, qry.getBizDeviceId());
            }
            if (qry.getBizDeviceIds() != null && qry.getBizDeviceIds().size() > 0) {
                lqw.in(ProjectStaDeviceWaterYearEntity::getBizDeviceId, qry.getBizDeviceIds());
            }
            lqw.orderByDesc(ProjectStaDeviceWaterYearEntity::getStaTime);
            page = projectStaDeviceWaterYearMapper.selectPage(page, lqw);
            List<ProjectStaDeviceWaterYearEntity> records = page.getRecords();
            List<ProjectStaKpiDeviceVO> kpiRecords = records.stream().map(device -> {
                ProjectStaKpiDeviceVO kpi = new ProjectStaKpiDeviceVO();
                BeanUtils.copyProperties(device, kpi);
                String bizProjectId = device.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.setProjectName(projectEntity.getName());
                kpi.setDeviceName(deviceMap.get(device.getBizDeviceId()).getName());
                kpi.setDeviceCode(deviceMap.get(device.getBizDeviceId()).getCode());
                kpi.setStaTime(VO2TimeFmt(device.getYear(), null, null, null, StaTimePeriodEnum.YEAR.getType()));
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;
        }
        return null;
    }

    private String getDate(Timestamp date) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return fmt.format(date);
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

    /**
     * @param subitem
     * @param cla
     * @param kpis
     * @param staTimePeriod
     * @return
     */
    private Map<String, Object> getStaMap2(Object subitem, Class cla, List<String> kpis, String staTimePeriod) {
        Map<String, Object> map = new HashMap<>();
        //获取统计时间
        Timestamp staTime = null;
        Object result = this.getValue(subitem, cla, "getStaTime");
        if (result != null)
            staTime = (Timestamp) result;

        for (String kpi : kpis) {
            if (kpi.startsWith(KpiUtils.KPI_PREFFIX_SUBITEM)) {
                //通过property 反射获得属性
                String methodName = kpiCodeToProperty(kpi, cla);
                Object objValue = this.getValue(subitem, cla, methodName);
                BigDecimal value = null;
                if (objValue != null)
                    value = (BigDecimal) objValue;

                map.put(kpi, value);

            } else if (kpi.startsWith(KpiUtils.KPI_PREFFIX_SUBAREA)) {
                //通过kpiCode , staTime , 时间维度(hour,month,year,day) 查询对应的 分区数据
                String realKpi = kpi.split("_")[0];
                String subareaCode = kpi.split("_")[1];
                map.put(kpi, this.getSubareaData(realKpi, subareaCode, staTime, staTimePeriod));
            }

        }

        return map;
    }

    private BigDecimal getSubareaData(String kpi, String subareaCode, Timestamp staTime, String staTimePeriod) {
        BigDecimal staValue = null;
        if (staTimePeriod.equals(StaTimePeriodEnum.HOUR.getType())) {
            LambdaQueryWrapper<ProjectStaSubareaHourEntity> lqw = new LambdaQueryWrapper<>();
            lqw.eq(ProjectStaSubareaHourEntity::getKpiCode, kpi);
            lqw.eq(ProjectStaSubareaHourEntity::getStaTime, staTime);
            lqw.eq(ProjectStaSubareaHourEntity::getSubareaName, subareaCode);
            List<ProjectStaSubareaHourEntity> result = projectStaSubareaHourMapper.selectList(lqw);
            if (result != null && result.size() > 0) {
                staValue = result.get(0).getStaValue();
            }
        } else if (staTimePeriod.equals(StaTimePeriodEnum.DAY.getType())) {
            LambdaQueryWrapper<ProjectStaSubareaDayEntity> lqw = new LambdaQueryWrapper<>();
            lqw.eq(ProjectStaSubareaDayEntity::getKpiCode, kpi);
            lqw.eq(ProjectStaSubareaDayEntity::getStaTime, staTime);
            lqw.eq(ProjectStaSubareaDayEntity::getSubareaName, subareaCode);
            List<ProjectStaSubareaDayEntity> result = projectStaSubareaDayMapper.selectList(lqw);
            if (result != null && result.size() > 0) {
                staValue = result.get(0).getStaValue();
            }
        } else if (staTimePeriod.equals(StaTimePeriodEnum.MONTH.getType())) {
            LambdaQueryWrapper<ProjectStaSubareaMonthEntity> lqw = new LambdaQueryWrapper<>();
            lqw.eq(ProjectStaSubareaMonthEntity::getKpiCode, kpi);
            lqw.eq(ProjectStaSubareaMonthEntity::getStaTime, staTime);
            lqw.eq(ProjectStaSubareaMonthEntity::getSubareaName, subareaCode);
            List<ProjectStaSubareaMonthEntity> result = projectStaSubareaMonthMapper.selectList(lqw);
            if (result != null && result.size() > 0) {
                staValue = result.get(0).getStaValue();
            }
        } else if (staTimePeriod.equals(StaTimePeriodEnum.YEAR.getType())) {
            LambdaQueryWrapper<ProjectStaSubareaYearEntity> lqw = new LambdaQueryWrapper<>();
            lqw.eq(ProjectStaSubareaYearEntity::getKpiCode, kpi);
            lqw.eq(ProjectStaSubareaYearEntity::getStaTime, staTime);
            lqw.eq(ProjectStaSubareaYearEntity::getSubareaName, subareaCode);
            List<ProjectStaSubareaYearEntity> result = projectStaSubareaYearMapper.selectList(lqw);
            if (result != null && result.size() > 0) {
                staValue = result.get(0).getStaValue();
            }
        }
        return staValue;
    }

    private String getKpi(String connectedAPI) {
        return connectedAPI.substring(connectedAPI.lastIndexOf("_") + 1, connectedAPI.length());
    }

    private String getSubareaCode(String connectedAPI) {
        return connectedAPI.substring(connectedAPI.indexOf("_") + 1, connectedAPI.lastIndexOf("_"));
    }

    private String getSubareaCode2(String connectedAPI) {
        return connectedAPI.substring(connectedAPI.indexOf("_") + 1, connectedAPI.lastIndexOf("_"));
    }


//    private String kpiCodeToProperty(String kpi) {
//        String[] kpis = kpi.split("\\.");
//        StringBuilder property = new StringBuilder("get");
//        for (int i = 0; i < kpis.length; i++) {
//            String prop = kpis[i];
//            property.append(prop.substring(0, 1).toUpperCase());
//            property.append(prop.substring(1).toLowerCase());
//        }
//        return property.toString();
//    }

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


    private String kpiCodeToProperty2(String kpi) {
        String[] kpis = kpi.split("\\.");
        StringBuilder property = new StringBuilder("get");
        for (int i = 0; i < kpis.length; i++) {
            String prop = kpis[i];
            property.append(prop.substring(0, 1).toUpperCase());
            property.append(prop.substring(1).toLowerCase());
        }
        return property.toString();
    }


    @Override
    public IPage<Map<String, Object>> getProjectStaData(ProjectStaKpiDTO qry) {
        IPage<Map<String, Object>> kpiPage = new Page<>(qry.getPageNo(), qry.getPageSize());
        String timeBegin = "";
        String timeEnd = "";
        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.DAY.getType())) {

            IPage<ProjectStaSubitemDayEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaSubitemDayEntity> lqw = new LambdaQueryWrapper<>();
            timeBegin = this.transStaTime(qry.getBegin(), StaTimePeriodEnum.DAY.getType());
            timeEnd = DateUtils.get(this.transStaTime(qry.getEnd(), StaTimePeriodEnum.DAY.getType()), StaTimePeriodEnum.DAY.getType());
            lqw.ge(ProjectStaSubitemDayEntity::getStaTime, timeBegin);
            lqw.lt(ProjectStaSubitemDayEntity::getStaTime, timeEnd);
            lqw.orderByDesc(ProjectStaSubitemDayEntity::getStaTime);
            lqw.eq(ProjectStaSubitemDayEntity::getBizProjectId, qry.getBizProjectId());
            page = projectStaSubitemDayMapper.selectPage(page, lqw);
            List<ProjectStaSubitemDayEntity> records = page.getRecords();
            List<Map<String, Object>> kpiRecords = records.stream().map(subitem -> {
                Map<String, Object> kpi = new HashMap<>();
                //kpi = getStaMap(subitem,ProjectStaSubitemDayEntity.class,qry.getKpiCodes(),qry.getStaTimePeriod());
                kpi = getStaMap2(subitem, ProjectStaSubitemDayEntity.class, qry.getKpiCodes(), qry.getStaTimePeriod());
                kpi.put("bizProjectId", subitem.getBizProjectId());
                kpi.put("staTime", DateUtils.fmt2Str(subitem.getStaTime(), DateUtils.SD_DT_FMT_DAY));
                kpi.put("projectCode", subitem.getProjectCode());
                String bizProjectId = subitem.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.put("projectName", projectEntity.getName());
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;
        }
        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.HOUR.getType())) {

            IPage<ProjectStaSubitemHourEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaSubitemHourEntity> lqw = new LambdaQueryWrapper<>();
            timeBegin = this.transStaTime(qry.getBegin(), StaTimePeriodEnum.HOUR.getType());
            timeEnd = DateUtils.get(this.transStaTime(qry.getEnd(), StaTimePeriodEnum.DAY.getType()), StaTimePeriodEnum.DAY.getType());
            lqw.ge(ProjectStaSubitemHourEntity::getStaTime, timeBegin);
            lqw.lt(ProjectStaSubitemHourEntity::getStaTime, timeEnd);
            lqw.eq(ProjectStaSubitemHourEntity::getBizProjectId, qry.getBizProjectId());
            lqw.orderByDesc(ProjectStaSubitemHourEntity::getStaTime);
            page = projectStaSubitemHourMapper.selectPage(page, lqw);
            List<ProjectStaSubitemHourEntity> records = page.getRecords();
            List<Map<String, Object>> kpiRecords = records.stream().map(subitem -> {
                Map<String, Object> kpi = new HashMap<>();
                //kpi = getStaMap(subitem,ProjectStaSubitemDayEntity.class,qry.getKpiCodes(),qry.getStaTimePeriod());
                kpi = getStaMap2(subitem, ProjectStaSubitemHourEntity.class, qry.getKpiCodes(), qry.getStaTimePeriod());
                kpi.put("bizProjectId", subitem.getBizProjectId());
                kpi.put("staTime", DateUtils.fmt2Str(subitem.getStaTime(), DateUtils.SD_DT_FMT_HOUR));
                kpi.put("projectCode", subitem.getProjectCode());
                String bizProjectId = subitem.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.put("projectName", projectEntity.getName());
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;
        }
        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.MONTH.getType())) {

            IPage<ProjectStaSubitemMonthEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaSubitemMonthEntity> lqw = new LambdaQueryWrapper<>();
            timeBegin = this.transStaTime(qry.getBegin(), StaTimePeriodEnum.MONTH.getType());
            timeEnd = DateUtils.get(this.transStaTime(qry.getEnd(), StaTimePeriodEnum.MONTH.getType()), StaTimePeriodEnum.MONTH.getType());
            lqw.ge(ProjectStaSubitemMonthEntity::getStaTime, timeBegin);
            lqw.lt(ProjectStaSubitemMonthEntity::getStaTime, timeEnd);
            lqw.eq(ProjectStaSubitemMonthEntity::getBizProjectId, qry.getBizProjectId());
            lqw.orderByDesc(ProjectStaSubitemMonthEntity::getStaTime);
            page = projectStaSubitemMonthMapper.selectPage(page, lqw);
            List<ProjectStaSubitemMonthEntity> records = page.getRecords();
            List<Map<String, Object>> kpiRecords = records.stream().map(subitem -> {
                Map<String, Object> kpi = new HashMap<>();
                //kpi = getStaMap(subitem,ProjectStaSubitemDayEntity.class,qry.getKpiCodes(),qry.getStaTimePeriod());
                kpi = getStaMap2(subitem, ProjectStaSubitemMonthEntity.class, qry.getKpiCodes(), qry.getStaTimePeriod());
                kpi.put("bizProjectId", subitem.getBizProjectId());
                kpi.put("staTime", DateUtils.fmt2Str(subitem.getStaTime(), DateUtils.SD_DT_FMT_MONTH));
                kpi.put("projectCode", subitem.getProjectCode());
                String bizProjectId = subitem.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.put("projectName", projectEntity.getName());
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;
        }
        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.YEAR.getType())) {

            IPage<ProjectStaSubitemYearEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaSubitemYearEntity> lqw = new LambdaQueryWrapper<>();
            timeBegin = this.transStaTime(qry.getBegin(), StaTimePeriodEnum.YEAR.getType());
            timeEnd = DateUtils.get(this.transStaTime(qry.getEnd(), StaTimePeriodEnum.YEAR.getType()), StaTimePeriodEnum.YEAR.getType());
            lqw.ge(ProjectStaSubitemYearEntity::getStaTime, timeBegin);
            lqw.lt(ProjectStaSubitemYearEntity::getStaTime, timeEnd);
            lqw.eq(ProjectStaSubitemYearEntity::getBizProjectId, qry.getBizProjectId());
            lqw.orderByDesc(ProjectStaSubitemYearEntity::getStaTime);
            page = projectStaSubitemYearMapper.selectPage(page, lqw);
            List<ProjectStaSubitemYearEntity> records = page.getRecords();
            List<Map<String, Object>> kpiRecords = records.stream().map(subitem -> {
                Map<String, Object> kpi = new HashMap<>();
                //kpi = getStaMap(subitem,ProjectStaSubitemDayEntity.class,qry.getKpiCodes(),qry.getStaTimePeriod());
                kpi = getStaMap2(subitem, ProjectStaSubitemYearEntity.class, qry.getKpiCodes(), qry.getStaTimePeriod());
                kpi.put("bizProjectId", subitem.getBizProjectId());
                kpi.put("staTime", DateUtils.fmt2Str(subitem.getStaTime(), DateUtils.SD_DT_FMT_YEAR));
                kpi.put("projectCode", subitem.getProjectCode());
                String bizProjectId = subitem.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.put("projectName", projectEntity.getName());
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;
        }
        return null;
    }

    /**
     * 计算同比环比
     *
     * @param v1
     * @param v2
     * @return
     */
    private BigDecimal compare(BigDecimal v1, BigDecimal v2) {
        BigDecimal zeroValue = new BigDecimal(0);
        if (v1 == null || v2 == null) {
            return zeroValue;
        }
        if (v1.compareTo(zeroValue) == 0 || v2.compareTo(zeroValue) == 0) {
            return zeroValue;
        }
        return (v1.subtract(v2)).divide(v2, 2, RoundingMode.HALF_EVEN);
    }

    @Override
    public KanbanRJDEnergyVO getRJDEnegy(String bizProjectId) {
        KanbanRJDEnergyVO energy = new KanbanRJDEnergyVO();
        LocalDateTime time = LocalDateTime.now();
        ProjectStaSubitemMonthEntity staMonth = projectStaSubitemMonthMapper.getCurrentMonth(bizProjectId, String.valueOf(time.getYear()), String.valueOf(time.getMonthValue()));
        energy.setElectricityMonth(staMonth == null ? new BigDecimal(0) : staMonth.getProjectElectricityEnergyusageTotal());
        energy.setGasMonth(staMonth == null ? new BigDecimal(0) : staMonth.getProjectGasUsageTotal());
        energy.setWaterMonth(staMonth == null ? new BigDecimal(0) : staMonth.getProjectWaterUsageTotal());
        energy.setCarbonMonth(staMonth == null ? new BigDecimal(0) : staMonth.getProjectCarbonGasusageco2Total());

        ProjectStaSubitemYearEntity staYear = projectStaSubitemYearMapper.getCurrentYear(bizProjectId, String.valueOf(time.getYear()));
        energy.setElectricityYear(staYear == null ? new BigDecimal(0) : staYear.getProjectElectricityEnergyusageTotal());
        energy.setGasYear(staYear == null ? new BigDecimal(0) : staYear.getProjectGasUsageTotal());
        energy.setWaterYear(staYear == null ? new BigDecimal(0) : staYear.getProjectWaterUsageTotal());
        energy.setCarbonYear(staYear == null ? new BigDecimal(0) : staYear.getProjectCarbonGasusageco2Total());

        //LocalDateTime lastMonth = time.minus(1L,ChronoUnit.MONTHS);
        //环比
        energy.setCarbonQOQ(new BigDecimal(0));
        energy.setElectricityQOQ(new BigDecimal(0));
        energy.setGasQOQ(new BigDecimal(0));
        energy.setWaterQOQ(new BigDecimal(0));
        //同比
        energy.setCarbonYOY(new BigDecimal(0));
        energy.setElectricityYOY(new BigDecimal(0));
        energy.setGasYOY(new BigDecimal(0));
        energy.setWaterYOY(new BigDecimal(0));
        return energy;
    }

    /**
     * 计算 同环比
     *
     * @param v1
     * @param v2
     * @return
     */
    private BigDecimal getRatio(BigDecimal v1, BigDecimal v2) {
        if (v1 == null || v2 == null) {
            return null;
        } else if (v1.compareTo(BigDecimal.ZERO) == 0 || v2.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        } else {
            return v1.subtract(v2).multiply(BigDecimal.valueOf(100)).divide(v2, 2, RoundingMode.HALF_EVEN);
        }

    }

    @Override
    public KanbanRJDEnergyVO getRJDEnegy2(String bizProjectId) {
        LocalDateTime now = LocalDateTime.now();
        KanbanRJDEnergyVO energy = new KanbanRJDEnergyVO();
        KanbanRJDEnergyVO energyLastCycle = new KanbanRJDEnergyVO();
        LocalDateTime time = LocalDateTime.now();
        String[] kpis = new String[]{"project.electricity.energyUsage.total",
                "project.gas.usage.total",
                "project.water.usage.total",
                "project.carbon.totalCO2.total",
                "project.electricity.energyUsageFee.total",
                "project.gas.fee.total",
                "project.water.fee.total"};

        String currentDay = null;
        if (1 == now.getDayOfMonth()) {
            currentDay = "01";
        } else {
            currentDay = String.format("%02d", now.getDayOfMonth() - 1);
        }
        String dayBegin = String.format("%s-%s-01", String.valueOf(now.getYear()), String.format("%02d", now.getMonthValue()));
        String dayEnd = String.format("%s-%s-%s", String.valueOf(now.getYear()), String.format("%02d", now.getMonthValue()), currentDay);
        String datBeginLastMonth = null;
        String datEndLastMonth = null;
        // 查询上个月的日期的最小值
        int lastDay = now.minusMonths(1).getDayOfMonth();
        String lastCurrentDay = null;
        if (1 == lastDay) {
            lastCurrentDay = "01";
        } else {
            lastCurrentDay = String.format("%02d", lastDay - 1);
        }
        if (1 == now.getDayOfMonth()) {
            currentDay = "01";
        } else {
            currentDay = String.format("%02d", now.getDayOfMonth() - 1);
        }
        if (1 == now.getMonthValue()) {
            datBeginLastMonth = String.format("%s-%s-01", String.valueOf(now.getYear() - 1), String.format("%02d", 12));
            datEndLastMonth = String.format("%s-%s-%s", String.valueOf(now.getYear() - 1), String.format("%02d", 12), lastCurrentDay);
        } else {
            datBeginLastMonth = String.format("%s-%s-01", String.valueOf(now.getYear()), String.format("%02d", (now.minusMonths(1L)).getMonthValue()));
            datEndLastMonth = String.format("%s-%s-%s", String.valueOf(now.getYear()), String.format("%02d", (now.minusMonths(1L)).getMonthValue()), lastCurrentDay);
        }

        //当月数据
        ProjectStaSubitemMonthVO currentMonthSta = this.getMonthSta(String.valueOf(now.getYear()), String.format("%02d", now.getMonthValue()), dayBegin, dayEnd, Arrays.asList(kpis), bizProjectId);
        //上月数据
        YearMonth currentYM = YearMonth.of(now.getYear(), now.getMonthValue());
        YearMonth lastYM = currentYM.minusMonths(1L);
        ProjectStaSubitemMonthVO lastMonthSta = this.getMonthSta(String.valueOf(lastYM.getYear()), String.format("%02d", lastYM.getMonthValue()), datBeginLastMonth, datEndLastMonth, Arrays.asList(kpis), bizProjectId);
        //当年数据
        //ProjectStaSubitemYearVO currentYearSta = this.getYearSta(String.valueOf(now.getYear()), Arrays.asList(kpis), bizProjectId);
        ProjectStaSubitemYearVO currentYearSta = this.getCumulative(currentYM, bizProjectId);

        //上年数据
        //ProjectStaSubitemYearVO lastYearSta = this.getYearSta(String.valueOf((now.minusYears(1L)).getYear()), Arrays.asList(kpis), bizProjectId);
        ProjectStaSubitemYearVO lastYearSta = this.getCumulative(currentYM.minusYears(1L), bizProjectId);

        energy.setElectricityMonth(currentMonthSta == null ? new BigDecimal(0) : currentMonthSta.getProjectElectricityEnergyusageTotal());
        energy.setGasMonth(currentMonthSta == null ? new BigDecimal(0) : currentMonthSta.getProjectGasUsageTotal());
        energy.setWaterMonth(currentMonthSta == null ? new BigDecimal(0) : currentMonthSta.getProjectWaterUsageTotal());
        energy.setCarbonMonth(currentMonthSta == null ? new BigDecimal(0) : currentMonthSta.getProjectCarbonTotalco2Total());
        energy.setCostMonth(currentMonthSta == null ? BigDecimal.ZERO : NumberUtil.add(currentMonthSta.getProjectElectricityEnergyusagefeeTotal(), currentMonthSta.getProjectWaterFeeTotal(), currentMonthSta.getProjectGasFeeTotal()));

        energy.setElectricityYear(currentYearSta == null ? new BigDecimal(0) : currentYearSta.getProjectElectricityEnergyusageTotal());
        energy.setGasYear(currentYearSta == null ? new BigDecimal(0) : currentYearSta.getProjectGasUsageTotal());
        energy.setWaterYear(currentYearSta == null ? new BigDecimal(0) : currentYearSta.getProjectWaterUsageTotal());
        energy.setCarbonYear(currentYearSta == null ? new BigDecimal(0) : currentYearSta.getProjectCarbonTotalco2Total());
        energy.setCostYear(currentYearSta == null ? BigDecimal.ZERO : NumberUtil.add(currentYearSta.getProjectElectricityEnergyusagefeeTotal(), currentYearSta.getProjectWaterFeeTotal(), currentYearSta.getProjectGasFeeTotal()));

        energyLastCycle.setElectricityMonth(lastMonthSta == null ? new BigDecimal(0) : lastMonthSta.getProjectElectricityEnergyusageTotal());
        energyLastCycle.setGasMonth(lastMonthSta == null ? new BigDecimal(0) : lastMonthSta.getProjectGasUsageTotal());
        energyLastCycle.setWaterMonth(lastMonthSta == null ? new BigDecimal(0) : lastMonthSta.getProjectWaterUsageTotal());
        energyLastCycle.setCarbonMonth(lastMonthSta == null ? new BigDecimal(0) : lastMonthSta.getProjectCarbonTotalco2Total());
        energyLastCycle.setCostMonth(lastMonthSta == null ? BigDecimal.ZERO : NumberUtil.add(lastMonthSta.getProjectElectricityEnergyusagefeeTotal(), lastMonthSta.getProjectWaterFeeTotal(), lastMonthSta.getProjectGasFeeTotal()));

        energyLastCycle.setElectricityYear(lastYearSta == null ? new BigDecimal(0) : lastYearSta.getProjectElectricityEnergyusageTotal());
        energyLastCycle.setGasYear(lastYearSta == null ? new BigDecimal(0) : lastYearSta.getProjectGasUsageTotal());
        energyLastCycle.setWaterYear(lastYearSta == null ? new BigDecimal(0) : lastYearSta.getProjectWaterUsageTotal());
        energyLastCycle.setCarbonYear(lastYearSta == null ? new BigDecimal(0) : lastYearSta.getProjectCarbonTotalco2Total());
        energyLastCycle.setCostYear(lastYearSta == null ? BigDecimal.ZERO : NumberUtil.add(lastYearSta.getProjectElectricityEnergyusagefeeTotal(), lastYearSta.getProjectWaterFeeTotal(), lastYearSta.getProjectGasFeeTotal()));
        //LocalDateTime lastMonth = time.minus(1L,ChronoUnit.MONTHS);
        //环比
        energy.setCarbonQOQ(getRatio(energy.getCarbonMonth(), energyLastCycle.getCarbonMonth()));
        energy.setElectricityQOQ(getRatio(energy.getElectricityMonth(), energyLastCycle.getElectricityMonth()));
        energy.setGasQOQ(getRatio(energy.getGasMonth(), energyLastCycle.getGasMonth()));
        energy.setWaterQOQ(getRatio(energy.getWaterMonth(), energyLastCycle.getWaterMonth()));
        energy.setCostQOQ(getRatio(energy.getCostMonth(), energyLastCycle.getCostMonth()));
        //同比
        energy.setCarbonYOY(getRatio(energy.getCarbonYear(), energyLastCycle.getCarbonYear()));
        energy.setElectricityYOY(getRatio(energy.getElectricityYear(), energyLastCycle.getElectricityYear()));
        energy.setGasYOY(getRatio(energy.getGasYear(), energyLastCycle.getGasYear()));
        energy.setWaterYOY(getRatio(energy.getWaterYear(), energyLastCycle.getWaterYear()));
        energy.setCostYOY(getRatio(energy.getCostYear(), energyLastCycle.getCostYear()));
        return energy;
    }


    @Override
    public KanbanRJDEnergyWeekVO getRJDEnegyWeek(String bizProjectId) {
        //todo
        KanbanRJDEnergyWeekVO energyWeek = new KanbanRJDEnergyWeekVO();
        LocalDateTime timeNow = LocalDateTime.now();
        //timeNow.getDayOfWeek().get


        return energyWeek;
    }

    @Override
    public KanbanRJDEnergyCostVO getRJDEnegyCost(String bizProjectId) {
        String[] kpis = new String[]{"project.electricity.energyUsageFee.total",
                "project.electricity.energyUsage.total",
                "project.gas.fee.total",
                "project.water.fee.total",
                "project.water.fee.water",
                "project.water.fee.sewerage",
                "project.electricity.energyUsage.peak",
                "project.electricity.energyUsage.valley",
                "project.electricity.energyUsage.flat",
                "project.electricity.energyUsage.tip",
                "project.electricity.energyUsageFee.peak",
                "project.electricity.energyUsageFee.valley",
                "project.electricity.energyUsageFee.flat",
                "project.electricity.energyUsageFee.tip",};

        LocalDateTime timeNow = LocalDateTime.now();
        ProjectStaSubitemYearVO yearSta = this.getYearSta(String.valueOf(timeNow.getYear()), Arrays.asList(kpis), bizProjectId);
        KanbanRJDEnergyCostVO cost = new KanbanRJDEnergyCostVO();
        //cost.setEnergyUsageFee(yearSta.getProjectElectricityEnergyusagefeeTotal());
        //cost.setEnergyUsageFee(yearSta.getProjectElectricityEnergyusageTotal());
        cost.setGasFee(yearSta.getProjectGasFeeTotal());
        cost.setWaterFee(yearSta.getProjectWaterFeeTotal());
        cost.setWaterFeeWater(yearSta.getProjectWaterFeeWater());
        cost.setWaterFeeSewerage(yearSta.getProjectWaterFeeSewerage());
        cost.setEnergyUsageFeePeek(yearSta.getProjectElectricityEnergyusagefeePeak());
        cost.setEnergyUsageFeeValley(yearSta.getProjectElectricityEnergyusagefeeValley());
        cost.setEnergyUsageFeeFlat(yearSta.getProjectElectricityEnergyusagefeeFlat());
        cost.setEnergyUsageFeeTip(yearSta.getProjectElectricityEnergyusagefeeTip());
        cost.setEnergyUsageFee(NumberUtil.add(cost.getEnergyUsageFeeTip(), cost.getEnergyUsageFeePeek(), cost.getEnergyUsageFeeFlat(), cost.getEnergyUsageFeeValley()));
        cost.setTotalFee(cost.getEnergyUsageFee().add(cost.getGasFee()).add(cost.getWaterFee()));
        cost.setYoy(new BigDecimal(0));
        return cost;
    }

    private KanbanRJDEnergyCostVO getCostByYear(String bizProjectId, YearMonth yearMonth) {
        IndexCumulativeMonthRequest indexCumulativeMonthRequest = new IndexCumulativeMonthRequest();
        indexCumulativeMonthRequest.setMonth(yearMonth);
        indexCumulativeMonthRequest.setProjectBizId(bizProjectId);
        SubitemIndexEnum[] monthCumulativeIndices = new SubitemIndexEnum[]{
                // 尖
                SubitemIndexEnum.ELECTRICITY_ENERGY_USAGE_FEE_TIP,
                // 峰
                SubitemIndexEnum.ELECTRICITY_ENERGY_USAGE_FEE_PEAK,
                // 谷
                SubitemIndexEnum.ELECTRICITY_ENERGY_USAGE_FEE_FLAT,
                // 平
                SubitemIndexEnum.ELECTRICITY_ENERGY_USAGE_FEE_VALLEY,
                // 总电费
                SubitemIndexEnum.ELECTRICITY_ENERGY_USAGE_FEE_TOTAL,
                // 水费
                SubitemIndexEnum.WATER_FEE_TOTAL,
                // 气费
                SubitemIndexEnum.GAS_FEE_TOTAL,
        };
        indexCumulativeMonthRequest.setIndices(monthCumulativeIndices);
        Response<Map<SubitemIndexEnum, BigDecimal>> searchDataByMonthCumulative = subitemApi.searchDataByMonthCumulative(indexCumulativeMonthRequest);
        Map<SubitemIndexEnum, BigDecimal> cumulativeMonth = searchDataByMonthCumulative.getResult();

        KanbanRJDEnergyCostVO cost = new KanbanRJDEnergyCostVO();
        cost.setGasFee(getValue(SubitemIndexEnum.GAS_FEE_TOTAL, cumulativeMonth));
        cost.setWaterFee(getValue(SubitemIndexEnum.WATER_FEE_TOTAL, cumulativeMonth));
        cost.setEnergyUsageFeePeek(getValue(SubitemIndexEnum.ELECTRICITY_ENERGY_USAGE_FEE_PEAK, cumulativeMonth));
        cost.setEnergyUsageFeeValley(getValue(SubitemIndexEnum.ELECTRICITY_ENERGY_USAGE_FEE_VALLEY, cumulativeMonth));
        cost.setEnergyUsageFeeFlat(getValue(SubitemIndexEnum.ELECTRICITY_ENERGY_USAGE_FEE_FLAT, cumulativeMonth));
        cost.setEnergyUsageFeeTip(getValue(SubitemIndexEnum.ELECTRICITY_ENERGY_USAGE_FEE_TIP, cumulativeMonth));
        cost.setEnergyUsageFee(getValue(SubitemIndexEnum.ELECTRICITY_ENERGY_USAGE_FEE_TOTAL, cumulativeMonth));
        cost.setTotalFee(NumberUtil.add(cost.getEnergyUsageFee(), cost.getGasFee(), cost.getWaterFee()));
        return cost;
    }

    @Override
    public KanbanRJDEnergyCostVO getRJDEnegyCost(String bizProjectId, String year) {
        //判断是否统计当年
        LocalDateTime timeNow = LocalDateTime.now();
        YearMonth yearMonthCurrent = null;
        if (StringUtils.equals(year, String.valueOf(timeNow.getYear()))) {
            yearMonthCurrent = YearMonth.now();
        } else {
            yearMonthCurrent = YearMonth.of(Integer.valueOf(year), 12);
        }
        YearMonth yearMonthLast = yearMonthCurrent.minusYears(1);
        KanbanRJDEnergyCostVO costCurrent = getCostByYear(bizProjectId, yearMonthCurrent);
        KanbanRJDEnergyCostVO costLast = getCostByYear(bizProjectId, yearMonthLast);
        costCurrent.setYoy(getRatio(costCurrent.getTotalFee(), costLast.getTotalFee()));
        //costCurrent.setYoy(costLast.getTotalFee() == null || costLast.getTotalFee().compareTo(BigDecimal.ZERO) == 0 ? null : NumberUtil.div(costCurrent.getTotalFee().min(costLast.getTotalFee()), costLast.getTotalFee()));
        return costCurrent;
    }

    @Override
    public KanbanRJDEnergyCarbonVO getRJDEnegyCarbon(String bizProjectId) {
        String[] kpis = new String[]{"project.electricity.energyUsageFee.total",
                "project.carbon.totalCoal.total",
                "project.carbon.totalCO2.total",
                "project.carbon.totalSO2.total",
                "project.carbon.totalDust.total",
                "project.carbon.waterUsageCO2.total",
                "project.carbon.gasUsageCO2.total",
                "project.carbon.electricityUsageCO2.total"};
        LocalDateTime timeNow = LocalDateTime.now();
        ProjectStaSubitemYearVO yearSta = this.getYearSta(String.valueOf(timeNow.getYear()), Arrays.asList(kpis), bizProjectId);


        KanbanRJDEnergyCarbonVO carbon = new KanbanRJDEnergyCarbonVO();
        //查询当年分项指标
        carbon.setTotalCoal(yearSta.getProjectCarbonTotalcoalTotal());
        carbon.setTotalCO2(yearSta.getProjectCarbonTotalco2Total());
        carbon.setTotalSO2(yearSta.getProjectCarbonTotalso2Total());
        carbon.setTotalDust(yearSta.getProjectCarbonTotaldustTotal());

        BigDecimal waterCO2 = yearSta.getProjectCarbonWaterusageco2Total() == null ? new BigDecimal(0) : yearSta.getProjectCarbonWaterusageco2Total();
        BigDecimal gasCO2 = yearSta.getProjectCarbonGasusageco2Total() == null ? new BigDecimal(0) : yearSta.getProjectCarbonGasusageco2Total();
        BigDecimal eleCO2 = yearSta.getProjectCarbonElectricityusageco2Total() == null ? new BigDecimal(0) : yearSta.getProjectCarbonElectricityusageco2Total();
        BigDecimal total = new BigDecimal(0).add(waterCO2).add(gasCO2).add(eleCO2);
//        carbon.setWaterCO2Ratio(waterCO2.divide(total, 2, RoundingMode.HALF_EVEN));
//        carbon.setGasCO2Ratio(gasCO2.divide(total, 2, RoundingMode.HALF_EVEN));
//        carbon.setEleCO2Ratio(eleCO2.divide(total, 2, RoundingMode.HALF_EVEN));
        carbon.setWaterCO2Ratio(waterCO2);
        carbon.setGasCO2Ratio(gasCO2);
        carbon.setEleCO2Ratio(eleCO2);

        return carbon;
    }

    private List<String> getThisWeekXList(LocalDateTime time) {
        List<String> thisWeekXList = new ArrayList<>();
        Date dateNow = DateUtil.parse(DateUtil.format(time, DateUtils.STR_DT_FMT_DAY), DateUtils.STR_DT_FMT_DAY);
        int dayOfWeek = DateUtil.dayOfWeek(dateNow) - 1;
        if (dayOfWeek == 0)
            dayOfWeek = 7;

        for (int i = 1 - dayOfWeek; i <= 7 - dayOfWeek; i++) {
            if (i < 0) {
                thisWeekXList.add(DateUtil.format(time.minusDays(i * -1), DateUtils.STR_DT_FMT_DAY));
            } else if (i > 0) {
                thisWeekXList.add(DateUtil.format(time.plusDays(i), DateUtils.STR_DT_FMT_DAY));
            } else {
                thisWeekXList.add(DateUtil.format(time, DateUtils.STR_DT_FMT_DAY));
            }
        }
        return thisWeekXList;
    }

    @Override
    public List<KanbanRJDEnergyWeeksVO> getRJDEnegyWeek2(String bizProjectId) {
        LocalDateTime now = LocalDateTime.now();
        String[] yList1 = new String[]{"0.00", "0.00", "0.00", "0.00", "0.00", "0.00", "0.00"};
        String[] yList2 = new String[]{"0.00", "0.00", "0.00", "0.00", "0.00", "792.00", "0.00"};
        String[] xList = new String[]{"1", "2", "3", "4", "5", "6", "7"};
        List<String> xListDaysThisWeek = this.getThisWeekXList(now);
        List<String> xListDaysLastWeek = this.getThisWeekXList(now.minusWeeks(1L));

        List<ProjectStaSubitemDayEntity> dayStasThisWeek = projectStaSubitemDayMapper.selectList(new LambdaQueryWrapper<ProjectStaSubitemDayEntity>()
                .ge(ProjectStaSubitemDayEntity::getStaTime, xListDaysThisWeek.get(0))
                .le(ProjectStaSubitemDayEntity::getStaTime, xListDaysThisWeek.get(6)));
        Map<String, ProjectStaSubitemDayEntity> dayStasMapThisWeek = dayStasThisWeek.stream().collect(
                Collectors.toMap(projectStaSubitemDayEntity -> DateUtils.parseDate(projectStaSubitemDayEntity.getStaTime()), t -> t, (v1, v2) -> v2));

        List<ProjectStaSubitemDayEntity> dayStasLastWeek = projectStaSubitemDayMapper.selectList(new LambdaQueryWrapper<ProjectStaSubitemDayEntity>()
                .ge(ProjectStaSubitemDayEntity::getStaTime, xListDaysLastWeek.get(0))
                .le(ProjectStaSubitemDayEntity::getStaTime, xListDaysLastWeek.get(6)));
        Map<String, ProjectStaSubitemDayEntity> dayStasMapLastWeek = dayStasLastWeek.stream().collect(
                Collectors.toMap(projectStaSubitemDayEntity -> DateUtils.parseDate(projectStaSubitemDayEntity.getStaTime()), t -> t, (v1, v2) -> v2));

        List<KanbanRJDEnergyWeeksVO> weeks = new ArrayList<>();
        //电
        KanbanRJDEnergyWeeksVO eleWeek = new KanbanRJDEnergyWeeksVO();
        eleWeek.setType("电");
        KanbanRJDEnergyWeeksDetailVO eleThisWeekDetail = new KanbanRJDEnergyWeeksDetailVO();
        KanbanRJDEnergyWeeksDetailVO eleLastWeekDetail = new KanbanRJDEnergyWeeksDetailVO();


        eleThisWeekDetail.setAttrs(xListDaysThisWeek.stream().map(s -> dayStasMapThisWeek.get(s) == null ? new BigDecimal(0) : dayStasMapThisWeek.get(s).getProjectElectricityEnergyusageTotal())
                .map(bigDecimal -> bigDecimal == null ? null : bigDecimal.toPlainString())
                .collect(Collectors.toList()));
        eleThisWeekDetail.setXlist(Arrays.asList(xList));
        eleLastWeekDetail.setAttrs(xListDaysLastWeek.stream().map(s -> dayStasMapLastWeek.get(s) == null ? new BigDecimal(0) : dayStasMapLastWeek.get(s).getProjectElectricityEnergyusageTotal())
                .map(bigDecimal -> bigDecimal == null ? null : bigDecimal.toPlainString())
                .collect(Collectors.toList()));
        eleLastWeekDetail.setXlist(Arrays.asList(xList));
        eleWeek.setThisWeek(eleThisWeekDetail);
        eleWeek.setLastWeek(eleLastWeekDetail);
        weeks.add(eleWeek);
        //水
        KanbanRJDEnergyWeeksVO waterWeek = new KanbanRJDEnergyWeeksVO();
        waterWeek.setType("水");
        KanbanRJDEnergyWeeksDetailVO waterThisWeekDetail = new KanbanRJDEnergyWeeksDetailVO();
        KanbanRJDEnergyWeeksDetailVO waterLastWeekDetail = new KanbanRJDEnergyWeeksDetailVO();
        waterThisWeekDetail.setAttrs(xListDaysThisWeek.stream().map(s -> dayStasMapThisWeek.get(s) == null ? new BigDecimal(0) : dayStasMapThisWeek.get(s).getProjectWaterUsageTotal())
                .map(bigDecimal -> bigDecimal == null ? null : bigDecimal.toPlainString())
                .collect(Collectors.toList()));
        waterThisWeekDetail.setXlist(Arrays.asList(xList));
        waterLastWeekDetail.setAttrs(xListDaysLastWeek.stream().map(s -> dayStasMapLastWeek.get(s) == null ? new BigDecimal(0) : dayStasMapLastWeek.get(s).getProjectWaterUsageTotal())
                .map(bigDecimal -> bigDecimal == null ? null : bigDecimal.toPlainString())
                .collect(Collectors.toList()));
        waterLastWeekDetail.setXlist(Arrays.asList(xList));
        waterWeek.setThisWeek(waterThisWeekDetail);
        waterWeek.setLastWeek(waterLastWeekDetail);
        weeks.add(waterWeek);
        //气
        KanbanRJDEnergyWeeksVO gasWeek = new KanbanRJDEnergyWeeksVO();
        gasWeek.setType("气");
        KanbanRJDEnergyWeeksDetailVO gasThisWeekDetail = new KanbanRJDEnergyWeeksDetailVO();
        KanbanRJDEnergyWeeksDetailVO gasLastWeekDetail = new KanbanRJDEnergyWeeksDetailVO();
        gasThisWeekDetail.setAttrs(xListDaysThisWeek.stream().map(s -> dayStasMapThisWeek.get(s) == null ? new BigDecimal(0) : dayStasMapThisWeek.get(s).getProjectGasUsageTotal())
                .map(bigDecimal -> bigDecimal == null ? null : bigDecimal.toPlainString())
                .collect(Collectors.toList()));
        gasThisWeekDetail.setXlist(Arrays.asList(xList));
        gasLastWeekDetail.setAttrs(xListDaysLastWeek.stream().map(s -> dayStasMapLastWeek.get(s) == null ? new BigDecimal(0) : dayStasMapLastWeek.get(s).getProjectGasUsageTotal())
                .map(bigDecimal -> bigDecimal == null ? null : bigDecimal.toPlainString())
                .collect(Collectors.toList()));
        gasLastWeekDetail.setXlist(Arrays.asList(xList));
        gasWeek.setThisWeek(gasThisWeekDetail);
        gasWeek.setLastWeek(gasLastWeekDetail);
        weeks.add(gasWeek);
        return weeks;
    }

    private LocalDateTime getDateOfWeek(String year, String week) throws ParseException {
        LocalDateTime dateYearBegin = DateUtil.beginOfYear(DateUtils.SD_DT_FMT_YEAR.parse(year)).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        if (DayOfWeek.THURSDAY.compareTo(LocalDate.of(Integer.valueOf(year), 1, 1).getDayOfWeek()) >= 0) {
            return dateYearBegin.plusDays(7 * (Integer.valueOf(week) - 1));
        } else {
            return dateYearBegin.plusDays(7 * (Integer.valueOf(week)));
        }
    }


    @Override
    public List<KanbanRJDEnergyWeeksVO> getRJDEnegyWeekNew(String bizProjectId, String year, String week) throws ParseException {
        LocalDateTime dayOfWeek = getDateOfWeek(year, week);
        String[] xList = new String[]{"1", "2", "3", "4", "5", "6", "7"};
        String[] xListDesc = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        List<String> xListDaysThisWeek = this.getThisWeekXList(dayOfWeek);
        List<String> xListDaysLastWeek = this.getThisWeekXList(dayOfWeek.minusWeeks(1L));

        List<ProjectStaSubitemDayEntity> dayStasThisWeek = projectStaSubitemDayMapper.selectList(new LambdaQueryWrapper<ProjectStaSubitemDayEntity>()
                .ge(ProjectStaSubitemDayEntity::getStaTime, xListDaysThisWeek.get(0))
                .le(ProjectStaSubitemDayEntity::getStaTime, xListDaysThisWeek.get(6)));
        Map<String, ProjectStaSubitemDayEntity> dayStasMapThisWeek = dayStasThisWeek.stream().collect(
                Collectors.toMap(projectStaSubitemDayEntity -> DateUtils.parseDate(projectStaSubitemDayEntity.getStaTime()), t -> t, (v1, v2) -> v2));

        List<ProjectStaSubitemDayEntity> dayStasLastWeek = projectStaSubitemDayMapper.selectList(new LambdaQueryWrapper<ProjectStaSubitemDayEntity>()
                .ge(ProjectStaSubitemDayEntity::getStaTime, xListDaysLastWeek.get(0))
                .le(ProjectStaSubitemDayEntity::getStaTime, xListDaysLastWeek.get(6)));
        Map<String, ProjectStaSubitemDayEntity> dayStasMapLastWeek = dayStasLastWeek.stream().collect(
                Collectors.toMap(projectStaSubitemDayEntity -> DateUtils.parseDate(projectStaSubitemDayEntity.getStaTime()), t -> t, (v1, v2) -> v2));

        List<KanbanRJDEnergyWeeksVO> weeks = new ArrayList<>();
        //电
        KanbanRJDEnergyWeeksVO eleWeek = new KanbanRJDEnergyWeeksVO();
        eleWeek.setType("电");
        KanbanRJDEnergyWeeksDetailVO eleThisWeekDetail = new KanbanRJDEnergyWeeksDetailVO();
        KanbanRJDEnergyWeeksDetailVO eleLastWeekDetail = new KanbanRJDEnergyWeeksDetailVO();


        eleThisWeekDetail.setAttrs(xListDaysThisWeek.stream().map(s -> dayStasMapThisWeek.get(s) == null ? new BigDecimal(0) : dayStasMapThisWeek.get(s).getProjectElectricityEnergyusageTotal())
                .map(bigDecimal -> bigDecimal == null ? null : bigDecimal.toPlainString())
                .collect(Collectors.toList()));
        eleThisWeekDetail.setXlist(Arrays.asList(xListDesc));
        eleLastWeekDetail.setAttrs(xListDaysLastWeek.stream().map(s -> dayStasMapLastWeek.get(s) == null ? new BigDecimal(0) : dayStasMapLastWeek.get(s).getProjectElectricityEnergyusageTotal())
                .map(bigDecimal -> bigDecimal == null ? null : bigDecimal.toPlainString())
                .collect(Collectors.toList()));
        eleLastWeekDetail.setXlist(Arrays.asList(xListDesc));
        eleWeek.setThisWeek(eleThisWeekDetail);
        eleWeek.setLastWeek(eleLastWeekDetail);
        weeks.add(eleWeek);
        //水
        KanbanRJDEnergyWeeksVO waterWeek = new KanbanRJDEnergyWeeksVO();
        waterWeek.setType("水");
        KanbanRJDEnergyWeeksDetailVO waterThisWeekDetail = new KanbanRJDEnergyWeeksDetailVO();
        KanbanRJDEnergyWeeksDetailVO waterLastWeekDetail = new KanbanRJDEnergyWeeksDetailVO();
        waterThisWeekDetail.setAttrs(xListDaysThisWeek.stream().map(s -> dayStasMapThisWeek.get(s) == null ? new BigDecimal(0) : dayStasMapThisWeek.get(s).getProjectWaterUsageTotal())
                .map(bigDecimal -> bigDecimal == null ? null : bigDecimal.toPlainString())
                .collect(Collectors.toList()));
        waterThisWeekDetail.setXlist(Arrays.asList(xListDesc));
        waterLastWeekDetail.setAttrs(xListDaysLastWeek.stream().map(s -> dayStasMapLastWeek.get(s) == null ? new BigDecimal(0) : dayStasMapLastWeek.get(s).getProjectWaterUsageTotal())
                .map(bigDecimal -> bigDecimal == null ? null : bigDecimal.toPlainString())
                .collect(Collectors.toList()));
        waterLastWeekDetail.setXlist(Arrays.asList(xListDesc));
        waterWeek.setThisWeek(waterThisWeekDetail);
        waterWeek.setLastWeek(waterLastWeekDetail);
        weeks.add(waterWeek);
        //气
        KanbanRJDEnergyWeeksVO gasWeek = new KanbanRJDEnergyWeeksVO();
        gasWeek.setType("气");
        KanbanRJDEnergyWeeksDetailVO gasThisWeekDetail = new KanbanRJDEnergyWeeksDetailVO();
        KanbanRJDEnergyWeeksDetailVO gasLastWeekDetail = new KanbanRJDEnergyWeeksDetailVO();
        gasThisWeekDetail.setAttrs(xListDaysThisWeek.stream().map(s -> dayStasMapThisWeek.get(s) == null ? new BigDecimal(0) : dayStasMapThisWeek.get(s).getProjectGasUsageTotal())
                .map(bigDecimal -> bigDecimal == null ? null : bigDecimal.toPlainString())
                .collect(Collectors.toList()));
        gasThisWeekDetail.setXlist(Arrays.asList(xListDesc));
        gasLastWeekDetail.setAttrs(xListDaysLastWeek.stream().map(s -> dayStasMapLastWeek.get(s) == null ? new BigDecimal(0) : dayStasMapLastWeek.get(s).getProjectGasUsageTotal())
                .map(bigDecimal -> bigDecimal == null ? null : bigDecimal.toPlainString())
                .collect(Collectors.toList()));
        gasLastWeekDetail.setXlist(Arrays.asList(xListDesc));
        gasWeek.setThisWeek(gasThisWeekDetail);
        gasWeek.setLastWeek(gasLastWeekDetail);
        weeks.add(gasWeek);
        return weeks;
    }

    /**
     * 测试数据
     *
     * @return
     */
    private List<StationRegionVO> getRegionList(String bizProjectId) {
        StationRegionVO regionVO1 = new StationRegionVO(bizProjectId, "锦江体验中心酒店", "1", "8203", null,
                Arrays.asList(new String[]{"D000000000048", "D000000000043"}));
        StationRegionVO regionVO2 = new StationRegionVO(bizProjectId, "锦江体验中心酒店", "1", "8303", null,
                Arrays.asList(new String[]{"D000000000049", "D000000000041"}));
        StationRegionVO regionVO3 = new StationRegionVO(bizProjectId, "锦江体验中心酒店", "1", "8312", null,
                Arrays.asList(new String[]{"D000000000050", "D000000000042"}));
        StationRegionVO regionVO4 = new StationRegionVO(bizProjectId, "锦江体验中心酒店", "1", "8503", null,
                Arrays.asList(new String[]{"D000000000051", "D000000000040"}));
        StationRegionVO regionVO5 = new StationRegionVO(bizProjectId, "锦江体验中心酒店", "1", "8505", null,
                Arrays.asList(new String[]{"D000000000052", "D000000000039"}));

        StationRegionVO regionVO6 = new StationRegionVO(bizProjectId, "锦江体验中心酒店", "2", "大堂", null, Arrays.asList(new String[]{"D000000000059", "D000000000030", "D000000000031", "D000000000032", "D000000000033", "D000000000034", "D000000000035"}));
        StationRegionVO regionVO7 = new StationRegionVO(bizProjectId, "锦江体验中心酒店", "2", "二层东", null, Arrays.asList(new String[]{"D000000000055", "D000000000025"}));
        StationRegionVO regionVO8 = new StationRegionVO(bizProjectId, "锦江体验中心酒店", "2", "二层西", null, Arrays.asList(new String[]{"D000000000056", "D000000000025"}));
        StationRegionVO regionVO9 = new StationRegionVO(bizProjectId, "锦江体验中心酒店", "2", "三层东", null, Arrays.asList(new String[]{"D000000000053", "D000000000027"}));
        StationRegionVO regionVO10 = new StationRegionVO(bizProjectId, "锦江体验中心酒店", "2", "三层西", null, Arrays.asList(new String[]{"D000000000054", "D000000000027"}));
        StationRegionVO regionVO11 = new StationRegionVO(bizProjectId, "锦江体验中心酒店", "2", "五层东", null, Arrays.asList(new String[]{"D000000000057", "D000000000029"}));
        StationRegionVO regionVO12 = new StationRegionVO(bizProjectId, "锦江体验中心酒店", "2", "五层西", null, Arrays.asList(new String[]{"D000000000058", "D000000000029"}));
        return Arrays.asList(new StationRegionVO[]{regionVO1, regionVO2, regionVO3, regionVO4, regionVO5, regionVO6, regionVO7, regionVO8,
                regionVO9, regionVO10, regionVO11, regionVO12});
    }

    private StationRegionVO getRegion(String bizProjectId, String regionName) {
        List<StationRegionVO> regions = getRegionList(bizProjectId);
        Map<String, StationRegionVO> regionMap = regions.stream().collect(
                Collectors.toMap(StationRegionVO::getRegionName, t -> t));
        return regionMap.get(regionName);
    }

    @Override
    public List<StationRegionVO> getRegions(String bizProjectId) {
        //todo
        return this.getRegionList(bizProjectId);
    }

    @Override
    public StationCurrentStatusVO getDeviceCurrent(String bizProjectId, String regionName) {
        StationCurrentStatusVO status = new StationCurrentStatusVO();
        StationRegionVO region = this.getRegion(bizProjectId, regionName);
        List<String> deviceIds = region.getDevices();
        //查询所有设备
        LambdaQueryWrapper<DeviceMonitorEntity> lqwDevice = new LambdaQueryWrapper<>();
        lqwDevice.eq(DeviceMonitorEntity::getBizProjectId, bizProjectId);
        if (deviceIds == null || deviceIds.size() == 0) {
            return status;
        } else {
            lqwDevice.in(DeviceMonitorEntity::getBizDeviceId, deviceIds);
        }
        List<DeviceMonitorEntity> devices = deviceMonitorMapper.selectList(lqwDevice);
        //调用influx 服务查询当前状态值
        BigDecimal pTotal = BigDecimal.ZERO;
        for (DeviceMonitorEntity device : devices) {
            String measurement = "device_status_".concat(device.getBizProductId());
            WhereCondition whereCondition = new WhereCondition("biz_device_id", SqlKeyword.EQ, device.getBizDeviceId());
            List<WhereCondition> conditions = new ArrayList<>();
            conditions.add(whereCondition);
            List<JSONObject> jsons = influxdbTemplate.getLast(null, measurement, conditions, new Date());

            if (jsons != null && jsons.size() > 0) {
                JSONObject jobj = jsons.get(0);
                if ("PK00000001".equals(device.getBizProductId())) {
                    //status.setPower(jobj.getStr("P"));
                    pTotal = NumberUtil.add(pTotal.toPlainString(), jobj.getStr("P") == null ? "0" : jobj.getStr("P"));
                } else if ("PK00000004".equals(device.getBizProductId())) {
                    status.setMod(monitorApi.getMode(deviceIds.get(0)).getResult());
                    status.setComStatus("0");
                    if (jobj.get("CST") != null && jobj.getDouble("CST") > 0) {
                        status.setComStatus("1");
                        status.setElectricityQuantity(null);
                        status.setTmpSet(null);
                        //status.setRunStatus(jobj.get("RST")!=null && jobj.getDouble("RST") > 0 ? "1" : "0");
                        //运行状态参考最新的逻辑
                        status.setRunStatus(monitorApi.getRunningStatus(device.getBizDeviceId()).getResult());
                        status.setTempNow(jobj.getStr("Temperature"));
                    }
                }
            }
        }
        status.setPower(pTotal.toString());
        return status;
    }

    @Override
    public List<CommonStaVO> getDeviceToday(String bizProjectId, String regionName) {
        List<CommonStaVO> todayDevicesData = new ArrayList<>();
        StationCurrentStatusVO status = new StationCurrentStatusVO();
        StationRegionVO region = this.getRegion(bizProjectId, regionName);
        List<String> deviceIds = region.getDevices();
        LocalDateTime timeNow = LocalDateTime.now();
        String begin = DateUtils.getToday(timeNow);
        String end = DateUtils.getTomorrow(timeNow);
        List<String> xlist = Arrays.asList(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"});

        //查询所有设备
        for (String deviceId : deviceIds) {
            DeviceMonitorEntity device = deviceMonitorMapper.selectOne(bizProjectId, deviceId);
            if ("PK00000001".equals(device.getBizProductId())) {
                //查询电表设备
                List<ProjectStaDeviceElectricityHourEntity> datas = projectStaDeviceElectricityHourMapper.list(deviceId, bizProjectId, begin, end);
                Map<String, ProjectStaDeviceElectricityHourEntity> dataMap =
                        datas.stream().collect(Collectors.toMap(ProjectStaDeviceElectricityHourEntity::getHour, t -> t, (v1, v2) -> v2));

                List<String> ylist = new ArrayList<>();
                for (String index : xlist) {
                    ProjectStaDeviceElectricityHourEntity data = dataMap.get(index);
                    if (dataMap != null) {
                        ylist.add(dataMap.get(index) == null ? null : (dataMap.get(index).getEnergymeterEpimportTotal() == null ? null : dataMap.get(index).getEnergymeterEpimportTotal().toPlainString()));
                    } else {
                        ylist.add(null);
                    }
                }
                CommonStaVO staVO = new CommonStaVO(device.getName().concat("功率"), xlist, ylist);
                todayDevicesData.add(staVO);
            } else if ("PK00000004".equals(device.getBizProductId())) {
                //遥控
                List<ProjectStaDeviceAirHourEntity> datas = projectStaDeviceAirHourMapper.list(deviceId, bizProjectId, begin, end);
                Map<String, ProjectStaDeviceAirHourEntity> dataMap =
                        datas.stream().collect(Collectors.toMap(ProjectStaDeviceAirHourEntity::getHour, t -> t, (v1, v2) -> v2));

                List<String> ylist1 = new ArrayList<>();
                List<String> ylist2 = new ArrayList<>();
                List<String> ylist3 = new ArrayList<>();
                for (String index : xlist) {
                    ProjectStaDeviceAirHourEntity data = dataMap.get(index);
                    if (dataMap != null) {
                        ylist1.add(dataMap.get(index) == null ? null : (dataMap.get(index).getAirconditionercontrollerActualtempAvg() == null ? null : dataMap.get(index).getAirconditionercontrollerActualtempAvg().toPlainString()));
                        ylist2.add(null);
                        ylist3.add(null);
                    } else {
                        ylist1.add(null);
                        ylist2.add(null);
                        ylist3.add(null);
                    }
                }
                CommonStaVO staVOTemp1 = new CommonStaVO("房间实际温度", xlist, ylist1);      //房间实际温度
                CommonStaVO staVOTemp2 = new CommonStaVO("室外温度", xlist, ylist2);      //室外温度
                CommonStaVO staVOTemp3 = new CommonStaVO("设定温度", xlist, ylist3);       //设定温度
                todayDevicesData.add(staVOTemp1);
                todayDevicesData.add(staVOTemp2);
                todayDevicesData.add(staVOTemp3);
            }
        }
        return todayDevicesData;
    }

    private List<String> getOneMonthDays() {
        List<String> monthDays = new ArrayList<>();
        LocalDateTime timeNow = LocalDateTime.now();
        int i = 0;
        while (i < 30) {
            monthDays.add(0, (timeNow.plus(i * -1, ChronoUnit.DAYS)).format(DateUtils.LC_DT_FMT_DAY));
            i++;
        }
        return monthDays;
    }

    @Override
    public List<CommonStaVO> getDeviceMonth(String bizProjectId, String regionName) {
        List<CommonStaVO> monthDevicesData = new ArrayList<>();
        StationCurrentStatusVO status = new StationCurrentStatusVO();
        StationRegionVO region = this.getRegion(bizProjectId, regionName);
        List<String> deviceIds = region.getDevices();
        List<String> xlist = getOneMonthDays();
        String begin = xlist.get(0);
        String end = xlist.get(xlist.size() - 1);
        //查询所有设备
        for (String deviceId : deviceIds) {
            DeviceMonitorEntity device = deviceMonitorMapper.selectOne(bizProjectId, deviceId);
            if ("PK00000001".equals(device.getBizProductId())) {
                //查询电表设备
                List<ProjectStaDeviceElectricityDayEntity> datas = projectStaDeviceElectricityDayMapper.list(deviceId, bizProjectId, begin, end);
                Map<String, ProjectStaDeviceElectricityDayEntity> dataMap =
                        datas.stream().collect(Collectors.toMap(new Function<ProjectStaDeviceElectricityDayEntity, String>() {
                            @Override
                            public String apply(ProjectStaDeviceElectricityDayEntity projectStaDeviceElectricityDayEntity) {
                                return DateUtils.SD_DT_FMT_DAY.format(projectStaDeviceElectricityDayEntity.getStaTime());
                            }
                        }, t -> t, (v1, v2) -> v2));

                List<String> ylist = new ArrayList<>();
                for (String dayStr : xlist) {
                    ProjectStaDeviceElectricityDayEntity data = dataMap.get(dayStr);
                    if (dataMap != null) {
                        ylist.add(dataMap.get(dayStr) == null ? null : (dataMap.get(dayStr).getEnergymeterEpimportTotal() == null ? null : dataMap.get(dayStr).getEnergymeterEpimportTotal().toPlainString()));
                    } else {
                        ylist.add(null);
                    }
                }
                CommonStaVO staVO = new CommonStaVO("电量", xlist, ylist);
                monthDevicesData.add(staVO);
            } else if ("PK00000004".equals(device.getBizProductId())) {
                //遥控
                List<ProjectStaDeviceAirDayEntity> datas = projectStaDeviceAirDayMapper.list(deviceId, bizProjectId, begin, end);
                Map<String, ProjectStaDeviceAirDayEntity> dataMap =
                        datas.stream().collect(Collectors.toMap(new Function<ProjectStaDeviceAirDayEntity, String>() {
                            @Override
                            public String apply(ProjectStaDeviceAirDayEntity entity) {
                                return DateUtils.SD_DT_FMT_DAY.format(entity.getStaTime());
                            }
                        }, t -> t, (v1, v2) -> v2));

                List<String> ylist = new ArrayList<>();
                for (String dayStr : xlist) {
                    ProjectStaDeviceAirDayEntity data = dataMap.get(dayStr);
                    if (data != null) {
                        ylist.add(data.getAirconditionercontrollerOnlinetimeTotal() == null ? null : data.getAirconditionercontrollerOnlinetimeTotal().divide(new BigDecimal(3600), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP).toPlainString());
                    } else {
                        ylist.add(null);
                    }
                }
                CommonStaVO staVO = new CommonStaVO("入住时长", xlist, ylist);
                monthDevicesData.add(staVO);
            }
        }
        return monthDevicesData;
    }

    private Map<String, DeviceMonitorEntity> getDeviceMap(String projectId, String catgoryId) {
        LambdaQueryWrapper<DeviceMonitorEntity> lqw = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(projectId))
            lqw.eq(DeviceMonitorEntity::getBizProjectId, projectId);

        if (StringUtils.isNotBlank(catgoryId))
            lqw.eq(DeviceMonitorEntity::getBizCategoryId, catgoryId);

        List<DeviceMonitorEntity> devices = deviceMonitorMapper.selectList(lqw);
        return devices.stream().collect(Collectors.toMap(DeviceMonitorEntity::getBizDeviceId, t -> t));
    }

    @Override
    public IPage<ProjectStaKpiDeviceVO> getDeviceData(ProjectStaKpiDTO qry) {
        Map<String, DeviceMonitorEntity> deviceMap = this.getDeviceMap(qry.getBizProjectId(), qry.getBizCategoryId());
        if ("PC0001".equals(qry.getBizCategoryId())) {
            return this.getElectricityData(qry, deviceMap);
        } else if ("PC0002".equals(qry.getBizCategoryId())) {
            return this.getGasData(qry, deviceMap);
        } else if ("PC0003".equals(qry.getBizCategoryId())) {
            return this.getWaterData(qry, deviceMap);
        } else if ("PC0004".equals(qry.getBizCategoryId())) {
            return this.getAirData(qry, deviceMap);
        } else if ("PC0027".equals(qry.getBizCategoryId())) {
            return this.getZnbData(qry, deviceMap);
        } else if ("PC0028".equals(qry.getBizCategoryId())) {
            return this.getGscnData(qry, deviceMap);
        }
        return null;
    }

    public IPage<ProjectStaKpiDeviceVO> getGscnData(ProjectStaKpiDTO qry, Map<String, DeviceMonitorEntity> deviceMap) {
        String timeBegin = "";
        String timeEnd = "";
        IPage<ProjectStaKpiDeviceVO> kpiPage = new Page<>(qry.getPageNo(), qry.getPageSize());
        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.HOUR.getType())) {
            IPage<ProjectStaDeviceGscnHourEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaDeviceGscnHourEntity> lqw = new LambdaQueryWrapper<>();
            lqw.ge(ProjectStaDeviceGscnHourEntity::getStaTime, qry.getBegin());
            lqw.le(ProjectStaDeviceGscnHourEntity::getStaTime, qry.getEnd());
            if (StringUtils.isNotBlank(qry.getBizProjectId())) {
                lqw.eq(ProjectStaDeviceGscnHourEntity::getBizProjectId, qry.getBizProjectId());
            }
            if (StringUtils.isNotBlank(qry.getBizDeviceId())) {
                lqw.eq(ProjectStaDeviceGscnHourEntity::getBizDeviceId, qry.getBizDeviceId());
            }
            if (qry.getBizDeviceIds() != null && qry.getBizDeviceIds().size() > 0) {
                lqw.in(ProjectStaDeviceGscnHourEntity::getBizDeviceId, qry.getBizDeviceIds());
            }
            lqw.orderByDesc(ProjectStaDeviceGscnHourEntity::getStaTime);

            page = projectStaDeviceGscnHourMapper.selectPage(page, lqw);
            List<ProjectStaDeviceGscnHourEntity> records = page.getRecords();
            List<ProjectStaKpiDeviceVO> kpiRecords = records.stream().map(device -> {
                ProjectStaKpiDeviceVO kpi = new ProjectStaKpiDeviceVO();
                BeanUtils.copyProperties(device, kpi);
                String bizProjectId = device.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.setProjectName(projectEntity.getName());
                kpi.setDeviceName(deviceMap.get(device.getBizDeviceId()).getName());
                kpi.setDeviceCode(deviceMap.get(device.getBizDeviceId()).getCode());
                kpi.setStaTime(VO2TimeFmt(device.getYear(), device.getMonth(), device.getDay(), device.getHour(), StaTimePeriodEnum.HOUR.getType()));
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;

        }
        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.DAY.getType())) {
            IPage<ProjectStaDeviceGscnDayEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaDeviceGscnDayEntity> lqw = new LambdaQueryWrapper<>();
            timeBegin = this.transStaTime(qry.getBegin(), StaTimePeriodEnum.DAY.getType());
            timeEnd = DateUtils.get(this.transStaTime(qry.getEnd(), StaTimePeriodEnum.DAY.getType()), StaTimePeriodEnum.DAY.getType());
            lqw.ge(ProjectStaDeviceGscnDayEntity::getStaTime, timeBegin);
            lqw.lt(ProjectStaDeviceGscnDayEntity::getStaTime, timeEnd);
            if (StringUtils.isNotBlank(qry.getBizProjectId())) {
                lqw.eq(ProjectStaDeviceGscnDayEntity::getBizProjectId, qry.getBizProjectId());
            }
            if (StringUtils.isNotBlank(qry.getBizDeviceId())) {
                lqw.eq(ProjectStaDeviceGscnDayEntity::getBizDeviceId, qry.getBizDeviceId());
            }
            if (qry.getBizDeviceIds() != null && qry.getBizDeviceIds().size() > 0) {
                lqw.in(ProjectStaDeviceGscnDayEntity::getBizDeviceId, qry.getBizDeviceIds());
            }
            lqw.orderByDesc(ProjectStaDeviceGscnDayEntity::getStaTime);

            page = projectStaDeviceGscnDayMapper.selectPage(page, lqw);
            List<ProjectStaDeviceGscnDayEntity> records = page.getRecords();
            List<ProjectStaKpiDeviceVO> kpiRecords = records.stream().map(device -> {
                ProjectStaKpiDeviceVO kpi = new ProjectStaKpiDeviceVO();
                BeanUtils.copyProperties(device, kpi);
                String bizProjectId = device.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.setProjectName(projectEntity.getName());
                kpi.setDeviceName(deviceMap.get(device.getBizDeviceId()).getName());
                kpi.setDeviceCode(deviceMap.get(device.getBizDeviceId()).getCode());
                kpi.setStaTime(VO2TimeFmt(device.getYear(), device.getMonth(), device.getDay(), null, StaTimePeriodEnum.DAY.getType()));
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;

        }
        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.MONTH.getType())) {
            IPage<ProjectStaDeviceGscnMonthEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaDeviceGscnMonthEntity> lqw = new LambdaQueryWrapper<>();
            timeBegin = this.transStaTime(qry.getBegin(), StaTimePeriodEnum.MONTH.getType());
            timeEnd = DateUtils.get(this.transStaTime(qry.getEnd(), StaTimePeriodEnum.MONTH.getType()), StaTimePeriodEnum.MONTH.getType());
            lqw.ge(ProjectStaDeviceGscnMonthEntity::getStaTime, timeBegin);
            lqw.lt(ProjectStaDeviceGscnMonthEntity::getStaTime, timeEnd);
            if (StringUtils.isNotBlank(qry.getBizProjectId())) {
                lqw.eq(ProjectStaDeviceGscnMonthEntity::getBizProjectId, qry.getBizProjectId());
            }
            if (StringUtils.isNotBlank(qry.getBizDeviceId())) {
                lqw.eq(ProjectStaDeviceGscnMonthEntity::getBizDeviceId, qry.getBizDeviceId());
            }
            if (qry.getBizDeviceIds() != null && qry.getBizDeviceIds().size() > 0) {
                lqw.in(ProjectStaDeviceGscnMonthEntity::getBizDeviceId, qry.getBizDeviceIds());
            }
            lqw.orderByDesc(ProjectStaDeviceGscnMonthEntity::getStaTime);
            page = projectStaDeviceGscnMonthMapper.selectPage(page, lqw);
            List<ProjectStaDeviceGscnMonthEntity> records = page.getRecords();
            List<ProjectStaKpiDeviceVO> kpiRecords = records.stream().map(device -> {
                ProjectStaKpiDeviceVO kpi = new ProjectStaKpiDeviceVO();
                BeanUtils.copyProperties(device, kpi);
                String bizProjectId = device.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.setProjectName(projectEntity.getName());
                kpi.setDeviceName(deviceMap.get(device.getBizDeviceId()).getName());
                kpi.setDeviceCode(deviceMap.get(device.getBizDeviceId()).getCode());
                kpi.setStaTime(VO2TimeFmt(device.getYear(), device.getMonth(), null, null, StaTimePeriodEnum.MONTH.getType()));
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;
        }

        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.YEAR.getType())) {
            IPage<ProjectStaDeviceGscnYearEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaDeviceGscnYearEntity> lqw = new LambdaQueryWrapper<>();
            timeBegin = this.transStaTime(qry.getBegin(), StaTimePeriodEnum.YEAR.getType());
            timeEnd = DateUtils.get(this.transStaTime(qry.getEnd(), StaTimePeriodEnum.YEAR.getType()), StaTimePeriodEnum.YEAR.getType());
            lqw.ge(ProjectStaDeviceGscnYearEntity::getStaTime, timeBegin);
            lqw.lt(ProjectStaDeviceGscnYearEntity::getStaTime, timeEnd);
            if (StringUtils.isNotBlank(qry.getBizProjectId())) {
                lqw.eq(ProjectStaDeviceGscnYearEntity::getBizProjectId, qry.getBizProjectId());
            }
            if (StringUtils.isNotBlank(qry.getBizDeviceId())) {
                lqw.eq(ProjectStaDeviceGscnYearEntity::getBizDeviceId, qry.getBizDeviceId());
            }
            if (qry.getBizDeviceIds() != null && qry.getBizDeviceIds().size() > 0) {
                lqw.in(ProjectStaDeviceGscnYearEntity::getBizDeviceId, qry.getBizDeviceIds());
            }
            lqw.orderByDesc(ProjectStaDeviceGscnYearEntity::getStaTime);
            page = projectStaDeviceGscnYearMapper.selectPage(page, lqw);
            List<ProjectStaDeviceGscnYearEntity> records = page.getRecords();
            List<ProjectStaKpiDeviceVO> kpiRecords = records.stream().map(device -> {
                ProjectStaKpiDeviceVO kpi = new ProjectStaKpiDeviceVO();
                BeanUtils.copyProperties(device, kpi);
                String bizProjectId = device.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.setProjectName(projectEntity.getName());
                kpi.setDeviceName(deviceMap.get(device.getBizDeviceId()).getName());
                kpi.setDeviceCode(deviceMap.get(device.getBizDeviceId()).getCode());
                kpi.setStaTime(VO2TimeFmt(device.getYear(), null, null, null, StaTimePeriodEnum.YEAR.getType()));
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;
        }
        return null;
    }

    public IPage<ProjectStaKpiDeviceVO> getZnbData(ProjectStaKpiDTO qry, Map<String, DeviceMonitorEntity> deviceMap) {
        String timeBegin = "";
        String timeEnd = "";
        IPage<ProjectStaKpiDeviceVO> kpiPage = new Page<>(qry.getPageNo(), qry.getPageSize());
        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.HOUR.getType())) {
            IPage<ProjectStaDeviceZnbHourEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaDeviceZnbHourEntity> lqw = new LambdaQueryWrapper<>();
            lqw.ge(ProjectStaDeviceZnbHourEntity::getStaTime, qry.getBegin());
            lqw.le(ProjectStaDeviceZnbHourEntity::getStaTime, qry.getEnd());
            if (StringUtils.isNotBlank(qry.getBizProjectId())) {
                lqw.eq(ProjectStaDeviceZnbHourEntity::getBizProjectId, qry.getBizProjectId());
            }
            if (StringUtils.isNotBlank(qry.getBizDeviceId())) {
                lqw.eq(ProjectStaDeviceZnbHourEntity::getBizDeviceId, qry.getBizDeviceId());
            }
            if (qry.getBizDeviceIds() != null && qry.getBizDeviceIds().size() > 0) {
                lqw.in(ProjectStaDeviceZnbHourEntity::getBizDeviceId, qry.getBizDeviceIds());
            }
            lqw.orderByDesc(ProjectStaDeviceZnbHourEntity::getStaTime);

            page = projectStaDeviceZnbHourMapper.selectPage(page, lqw);
            List<ProjectStaDeviceZnbHourEntity> records = page.getRecords();
            List<ProjectStaKpiDeviceVO> kpiRecords = records.stream().map(device -> {
                ProjectStaKpiDeviceVO kpi = new ProjectStaKpiDeviceVO();
                BeanUtils.copyProperties(device, kpi);
                String bizProjectId = device.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.setProjectName(projectEntity.getName());
                kpi.setDeviceName(deviceMap.get(device.getBizDeviceId()).getName());
                kpi.setDeviceCode(deviceMap.get(device.getBizDeviceId()).getCode());
                kpi.setStaTime(VO2TimeFmt(device.getYear(), device.getMonth(), device.getDay(), device.getHour(), StaTimePeriodEnum.HOUR.getType()));
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;

        }
        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.DAY.getType())) {
            IPage<ProjectStaDeviceZnbDayEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaDeviceZnbDayEntity> lqw = new LambdaQueryWrapper<>();
            timeBegin = this.transStaTime(qry.getBegin(), StaTimePeriodEnum.DAY.getType());
            timeEnd = DateUtils.get(this.transStaTime(qry.getEnd(), StaTimePeriodEnum.DAY.getType()), StaTimePeriodEnum.DAY.getType());
            lqw.ge(ProjectStaDeviceZnbDayEntity::getStaTime, timeBegin);
            lqw.lt(ProjectStaDeviceZnbDayEntity::getStaTime, timeEnd);
            if (StringUtils.isNotBlank(qry.getBizProjectId())) {
                lqw.eq(ProjectStaDeviceZnbDayEntity::getBizProjectId, qry.getBizProjectId());
            }
            if (StringUtils.isNotBlank(qry.getBizDeviceId())) {
                lqw.eq(ProjectStaDeviceZnbDayEntity::getBizDeviceId, qry.getBizDeviceId());
            }
            if (qry.getBizDeviceIds() != null && qry.getBizDeviceIds().size() > 0) {
                lqw.in(ProjectStaDeviceZnbDayEntity::getBizDeviceId, qry.getBizDeviceIds());
            }
            lqw.orderByDesc(ProjectStaDeviceZnbDayEntity::getStaTime);

            page = projectStaDeviceZnbDayMapper.selectPage(page, lqw);
            List<ProjectStaDeviceZnbDayEntity> records = page.getRecords();
            List<ProjectStaKpiDeviceVO> kpiRecords = records.stream().map(device -> {
                ProjectStaKpiDeviceVO kpi = new ProjectStaKpiDeviceVO();
                BeanUtils.copyProperties(device, kpi);
                String bizProjectId = device.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.setProjectName(projectEntity.getName());
                kpi.setDeviceName(deviceMap.get(device.getBizDeviceId()).getName());
                kpi.setDeviceCode(deviceMap.get(device.getBizDeviceId()).getCode());
                kpi.setStaTime(VO2TimeFmt(device.getYear(), device.getMonth(), device.getDay(), null, StaTimePeriodEnum.DAY.getType()));
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;

        }
        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.MONTH.getType())) {
            IPage<ProjectStaDeviceZnbMonthEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaDeviceZnbMonthEntity> lqw = new LambdaQueryWrapper<>();
            timeBegin = this.transStaTime(qry.getBegin(), StaTimePeriodEnum.MONTH.getType());
            timeEnd = DateUtils.get(this.transStaTime(qry.getEnd(), StaTimePeriodEnum.MONTH.getType()), StaTimePeriodEnum.MONTH.getType());
            lqw.ge(ProjectStaDeviceZnbMonthEntity::getStaTime, timeBegin);
            lqw.lt(ProjectStaDeviceZnbMonthEntity::getStaTime, timeEnd);
            if (StringUtils.isNotBlank(qry.getBizProjectId())) {
                lqw.eq(ProjectStaDeviceZnbMonthEntity::getBizProjectId, qry.getBizProjectId());
            }
            if (StringUtils.isNotBlank(qry.getBizDeviceId())) {
                lqw.eq(ProjectStaDeviceZnbMonthEntity::getBizDeviceId, qry.getBizDeviceId());
            }
            if (qry.getBizDeviceIds() != null && qry.getBizDeviceIds().size() > 0) {
                lqw.in(ProjectStaDeviceZnbMonthEntity::getBizDeviceId, qry.getBizDeviceIds());
            }
            lqw.orderByDesc(ProjectStaDeviceZnbMonthEntity::getStaTime);
            page = projectStaDeviceZnbMonthMapper.selectPage(page, lqw);
            List<ProjectStaDeviceZnbMonthEntity> records = page.getRecords();
            List<ProjectStaKpiDeviceVO> kpiRecords = records.stream().map(device -> {
                ProjectStaKpiDeviceVO kpi = new ProjectStaKpiDeviceVO();
                BeanUtils.copyProperties(device, kpi);
                String bizProjectId = device.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.setProjectName(projectEntity.getName());
                kpi.setDeviceName(deviceMap.get(device.getBizDeviceId()).getName());
                kpi.setDeviceCode(deviceMap.get(device.getBizDeviceId()).getCode());
                kpi.setStaTime(VO2TimeFmt(device.getYear(), device.getMonth(), null, null, StaTimePeriodEnum.MONTH.getType()));
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;
        }

        if (qry.getStaTimePeriod().equals(StaTimePeriodEnum.YEAR.getType())) {
            IPage<ProjectStaDeviceZnbYearEntity> page = new Page<>(qry.getPageNo(), qry.getPageSize());
            LambdaQueryWrapper<ProjectStaDeviceZnbYearEntity> lqw = new LambdaQueryWrapper<>();
            timeBegin = this.transStaTime(qry.getBegin(), StaTimePeriodEnum.YEAR.getType());
            timeEnd = DateUtils.get(this.transStaTime(qry.getEnd(), StaTimePeriodEnum.YEAR.getType()), StaTimePeriodEnum.YEAR.getType());
            lqw.ge(ProjectStaDeviceZnbYearEntity::getStaTime, timeBegin);
            lqw.lt(ProjectStaDeviceZnbYearEntity::getStaTime, timeEnd);
            if (StringUtils.isNotBlank(qry.getBizProjectId())) {
                lqw.eq(ProjectStaDeviceZnbYearEntity::getBizProjectId, qry.getBizProjectId());
            }
            if (StringUtils.isNotBlank(qry.getBizDeviceId())) {
                lqw.eq(ProjectStaDeviceZnbYearEntity::getBizDeviceId, qry.getBizDeviceId());
            }
            if (qry.getBizDeviceIds() != null && qry.getBizDeviceIds().size() > 0) {
                lqw.in(ProjectStaDeviceZnbYearEntity::getBizDeviceId, qry.getBizDeviceIds());
            }
            lqw.orderByDesc(ProjectStaDeviceZnbYearEntity::getStaTime);
            page = projectStaDeviceZnbYearMapper.selectPage(page, lqw);
            List<ProjectStaDeviceZnbYearEntity> records = page.getRecords();
            List<ProjectStaKpiDeviceVO> kpiRecords = records.stream().map(device -> {
                ProjectStaKpiDeviceVO kpi = new ProjectStaKpiDeviceVO();
                BeanUtils.copyProperties(device, kpi);
                String bizProjectId = device.getBizProjectId();
                ProjectEntity projectEntity = projectMapper.selectOne(Wrappers.<ProjectEntity>lambdaQuery().eq(ProjectEntity::getBizProjectId, bizProjectId));
                kpi.setProjectName(projectEntity.getName());
                kpi.setDeviceName(deviceMap.get(device.getBizDeviceId()).getName());
                kpi.setDeviceCode(deviceMap.get(device.getBizDeviceId()).getCode());
                kpi.setStaTime(VO2TimeFmt(device.getYear(), null, null, null, StaTimePeriodEnum.YEAR.getType()));
                return kpi;
            }).collect(Collectors.toList());
            this.mergePage(page, kpiPage);
            kpiPage.setRecords(kpiRecords);
            return kpiPage;
        }
        return null;
    }

    @Override
    public List<ProjectKpiSelectVO> getProjectKpi(String bizProjectId, String timePeriod) {
        TenantContext.setIgnore(true);
        List<ProjectKpiSelectVO> kpis = new ArrayList<>();
        List<ProjectKpiSelectVO> kpisTmp = new ArrayList<>();
        Integer hour = StringUtils.equals("1", timePeriod) ? 1 : null;
        Integer ymd = StringUtils.equals("1", timePeriod) ? null : 1;
        List<ProjectCnfSubitemEntity> subitemCnfs = projectCnfSubitemMapper.getValidCnf(bizProjectId);
        List<ProjectCnfSubareaEntity> subareaCnfs = projectCnfSubareaMapper.getValidCnf(bizProjectId);
        Map<String, ProjectCnfSubitemEntity> subitemCnfsMap = subitemCnfs.stream().collect(Collectors.toMap(ProjectCnfSubitemEntity::getKpiSubtype, t -> t, (v1, v2) -> v2));
        ///Map<String,ProjectCnfSubareaEntity> subareaCnfsMap =  subareaCnfs.stream().collect(Collectors.toMap(ProjectCnfSubareaEntity::getKpiSubtype, t->t,(v1, v2) -> v2));

        String isHour = null;
        String isYMD = null;
        if (StringUtils.equals("1", timePeriod))
            isHour = "1";

        if (!StringUtils.equals("1", timePeriod))
            isYMD = "1";

        List<ProjectKpiConfigEntity> allKpis = projectKpiConfigMapper.getAllKpisByProject(bizProjectId, isHour, isYMD);
        Map<String, String> allKpiUnitMap = allKpis.stream().collect(Collectors.toMap(ProjectKpiConfigEntity::getCode, projectKpiConfigEntity -> projectKpiConfigEntity.getUnit() == null ? "" : projectKpiConfigEntity.getUnit()));

        //分项 分区 区别处理
        //分项kpi
        List<ProjectKpiConfigEntity> subitemKpis = allKpis.stream().filter(projectKpiConfigEntity -> KpiUtils.getKpiTag(projectKpiConfigEntity.getCode()) == 1).collect(Collectors.toList());
        //分区kpi
        List<ProjectKpiConfigEntity> subareaKpis = allKpis.stream().filter(projectKpiConfigEntity -> KpiUtils.getKpiTag(projectKpiConfigEntity.getCode()) == 2).collect(Collectors.toList());


        //处理分项
        Map<String, List<ProjectKpiConfigEntity>> groupSubitemKpiMap = subitemKpis.stream().collect(Collectors.groupingBy(ProjectKpiConfigEntity::getKpiSubtype));
        for (Map.Entry<String, List<ProjectKpiConfigEntity>> entry : groupSubitemKpiMap.entrySet()) {
            String subKpiType = entry.getKey();
            if (subitemCnfsMap.containsKey(subKpiType)) {
                String name = subitemCnfsMap.containsKey(subKpiType) ? subitemCnfsMap.get(subKpiType).getName() : null;
                String kpiType = subitemCnfsMap.containsKey(subKpiType) ? subitemCnfsMap.get(subKpiType).getKpiType() : null;
                List<ProjectKpiConfigEntity> subKpis = entry.getValue();
                ProjectKpiSelectVO kpiSelectVO = new ProjectKpiSelectVO();
                kpiSelectVO.setKpiSubtype(subKpiType);
                kpiSelectVO.setName(name);
                kpiSelectVO.setTag("1");    //分项
                kpiSelectVO.setKpiType(kpiType);
                kpiSelectVO.setKpis(subKpis.stream().map(cnf -> new EnergySelectedVO(cnf.getName(), cnf.getCode(), KpiUtils.kpiToProperty(cnf.getCode()), cnf.getUnit())).collect(Collectors.toList()));
                kpisTmp.add(kpiSelectVO);
            }
        }
        Map<String, ProjectKpiSelectVO> kpiTmpMap = kpisTmp.stream().collect(
                Collectors.toMap(ProjectKpiSelectVO::getKpiSubtype, t -> t));
        //分项排序
        kpis = subitemCnfs.stream().map(projectCnfSubitemEntity -> kpiTmpMap.get(projectCnfSubitemEntity.getKpiSubtype())).filter(projectKpiSelectVO -> projectKpiSelectVO != null).collect(Collectors.toList());
        //处理分区
//        Map<String,List<ProjectKpiConfigEntity>> groupSubareaKpiMap =  subareaKpis.stream().collect(Collectors.groupingBy(ProjectKpiConfigEntity::getKpiSubtype));
//        for(Map.Entry<String,List<ProjectKpiConfigEntity>> entry : groupSubareaKpiMap.entrySet() ){
//            String subKpiType = entry.getKey();
//
//        }
        for (ProjectKpiConfigEntity area : subareaKpis) {
            ProjectKpiSelectVO kpiSelectVO = new ProjectKpiSelectVO();
            kpiSelectVO.setKpiSubtype(area.getKpiSubtype());
            kpiSelectVO.setName(area.getName());
            kpiSelectVO.setTag("2");    //分项
            kpiSelectVO.setKpiType(area.getKpiType());
            //根据kpisubtype查询所有分区
            kpiSelectVO.setKpis(subareaCnfs.stream().map(cnf -> new EnergySelectedVO(area.getName().concat("_").concat(cnf.getName()), area.getCode().concat("_").concat(cnf.getName()), null, allKpiUnitMap.get(area.getCode()))).collect(Collectors.toList()));
            kpis.add(kpiSelectVO);
        }

        return kpis;
    }


    @Override
    public KanbanRJDYearEleVO getThisYearEle(String bizProjectId, String boardType) {

        List<CommonStaVO> barChartData = new ArrayList<>();
        List<CommonStaVO> pieChartData = new ArrayList<>();
        String[] barChartXList = new String[]{"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
        Integer year = LocalDateTime.now().getYear();


        //分项
        if ("1".equals(boardType)) {
            //查询一级分项下所有kpi
            List<ProjectKpiConfigEntity> topCnfs = projectCnfSubitemMapper.getTopLevelCnfs(bizProjectId);

            //查询月数据
            List<ProjectStaSubitemMonthEntity> monthDatas = projectStaSubitemMonthMapper.getEleYearData(bizProjectId, String.valueOf(year));
            //查询年数据
            ProjectStaSubitemYearEntity yearData = projectStaSubitemYearMapper.getCurrentYear(bizProjectId, String.valueOf(year));
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
            BigDecimal finalTotal = total;
            pieChartYList = pieChartYValueList.stream().map(new Function<BigDecimal, String>() {
                @Override
                public String apply(BigDecimal bValue) {
                    return (bValue.divide(finalTotal, 2, RoundingMode.HALF_EVEN)).toString();

                }
            }).collect(Collectors.toList());
            pieChartData.add(new CommonStaVO("饼图数据", pieChartXList, pieChartYList));

        } else if ("2".equals(boardType)) {
            //分时
            //查询尖峰谷平 指标数据
            List<String> kpis = Arrays.asList(new String[]{"project.electricity.energyUsage.peak", "project.electricity.energyUsage.flat", "project.electricity.energyUsage.valley", "project.electricity.energyUsage.tip"});
            List<String> kpiNames = Arrays.asList(new String[]{"全部负荷峰用电量", "全部负荷平用电量", "全部负荷谷用电量", "全部负荷尖用电量"});
            //查询月数据
            List<ProjectStaSubitemMonthEntity> monthDatas = projectStaSubitemMonthMapper.getEleYearData(bizProjectId, String.valueOf(year));
            //查询年数据
            ProjectStaSubitemYearEntity yearData = projectStaSubitemYearMapper.getCurrentYear(bizProjectId, String.valueOf(year));
            Map<String, ProjectStaSubitemMonthEntity> yearMapData = monthDatas.stream().collect(
                    Collectors.toMap(ProjectStaSubitemMonthEntity::getMonth, t -> t));
            //饼图
            List<String> pieChartXList = new ArrayList<>();
            List<String> pieChartYList = new ArrayList<>();

            for (int i = 0; i < kpis.size(); i++) {
                String kpi = kpis.get(i);
                String kpiName = kpiNames.get(i);
                pieChartXList.add(kpiName);
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
                CommonStaVO staVO = new CommonStaVO(kpiName, Arrays.asList(barChartXList), barChartYList);
                barChartData.add(staVO);
            }

            //配置饼图
            List<BigDecimal> pieChartYValueList = new ArrayList<>();
            BigDecimal total = new BigDecimal(0);
            if (yearData != null) {
                for (String kpi : kpis) {
                    Object yValue = this.getValue(yearData, ProjectStaSubitemYearEntity.class, kpiCodeToProperty(kpi, ProjectStaSubitemMonthEntity.class));
                    BigDecimal byValue = yValue == null ? new BigDecimal(0) : (BigDecimal) yValue;
                    pieChartYValueList.add(byValue);
                    total = total.add(byValue);
                }
            }
            BigDecimal finalTotal = total;
            pieChartYList = pieChartYValueList.stream().map(new Function<BigDecimal, String>() {
                @Override
                public String apply(BigDecimal bValue) {
                    return (bValue.divide(finalTotal, 2, RoundingMode.HALF_EVEN)).toString();

                }
            }).collect(Collectors.toList());
            pieChartData.add(new CommonStaVO("饼图数据", pieChartXList, pieChartYList));

        } else if ("3".equals(boardType)) {
            //查询所有分区
            List<KanbanRJDEleYearRO> result = projectStaSubareaMonthMapper.getEleYearData();
            List<String> peChartDataXList = result.stream().map(kanbanRJDEleYearRO -> kanbanRJDEleYearRO.getName()).collect(Collectors.toList());
            BigDecimal total = result.stream().map(kanbanRJDEleYearRO -> kanbanRJDEleYearRO.getSvalue() == null ? new BigDecimal(0) : kanbanRJDEleYearRO.getSvalue()).reduce(BigDecimal.ZERO, BigDecimal::add);
            List<String> peChartDataYList = result.stream().map(data -> {
                BigDecimal yValue = data.getSvalue() == null ? new BigDecimal(0) : (BigDecimal) data.getSvalue();
                return (yValue.divide(total, 2, RoundingMode.HALF_EVEN)).toString();
            }).collect(Collectors.toList());
            CommonStaVO pieVo = new CommonStaVO("分区饼图", peChartDataXList, peChartDataYList);
            pieChartData.add(pieVo);
            //柱状图
            List<ProjectStaSubareaMonthEntity> barDatas = projectStaSubareaMonthMapper.getEleYearBarData(String.valueOf(year));
            Map<String, ProjectStaSubareaMonthEntity> barMapData = barDatas.stream().collect(Collectors.toMap(ProjectStaSubareaMonthEntity::getMonth, t -> t));
            List<String> barChartDataXList = Arrays.asList(barChartXList);
            List<String> barChartDataYList = barChartDataXList.stream().map(s -> barMapData.get(s) == null ? "" : barMapData.get(s).getStaValue().toString()).collect(Collectors.toList());
            CommonStaVO barVo = new CommonStaVO("锦江体验中心酒店", barChartDataXList, barChartDataYList);
            barChartData.add(barVo);
        }

        return new KanbanRJDYearEleVO(pieChartData, barChartData);
    }

    @Override
    public KanbanRJDYearEleVO getThisYearEle2(String bizProjectId, String boardType, String pyear) {

        List<CommonStaVO> barChartData = new ArrayList<>();
        List<CommonStaVO> pieChartData = new ArrayList<>();
        String[] barChartXList = new String[]{"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
        Integer year = Integer.valueOf(pyear);
        LocalDateTime now = LocalDateTime.now();

        //分项
        if ("1".equals(boardType)) {
            //查询一级分项下所有kpi
            TenantContext.setIgnore(true);
            List<ProjectKpiConfigEntity> topCnfs = projectCnfSubitemMapper.getTopLevelCnfs(bizProjectId);
            TenantContext.setIgnore(false);
            List<String> kpis = topCnfs.stream().map(ProjectKpiConfigEntity::getCode).collect(Collectors.toList());

            //查询月数据
            List<ProjectStaSubitemMonthEntity> monthDatas = projectStaSubitemMonthMapper.getEleYearData(bizProjectId, String.valueOf(year));
            if (year == now.getYear()) {//补上当月数据
                ProjectStaSubitemMonthVO currentMonth = this.getMonthSta(String.valueOf(now.getYear()), String.format("%02d", now.getMonthValue()), kpis, bizProjectId);
                ProjectStaSubitemMonthEntity currentMonthEntity = new ProjectStaSubitemMonthEntity();
                BeanUtils.copyProperties(currentMonth, currentMonthEntity);
                monthDatas.add(currentMonthEntity);
            }
            //查询年数据
            ProjectStaSubitemYearVO yearDataVO = this.getYearSta(String.valueOf(now.getYear()), kpis, bizProjectId);
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

        } else if ("2".equals(boardType)) {
            //分时
            //查询尖峰谷平 指标数据
            List<String> kpis = Arrays.asList(new String[]{"project.electricity.energyUsage.peak", "project.electricity.energyUsage.flat", "project.electricity.energyUsage.valley", "project.electricity.energyUsage.tip"});
            List<String> kpiNames = Arrays.asList(new String[]{"全部负荷峰用电量", "全部负荷平用电量", "全部负荷谷用电量", "全部负荷尖用电量"});
            //查询月数据
            List<ProjectStaSubitemMonthEntity> monthDatas = projectStaSubitemMonthMapper.getEleYearData(bizProjectId, String.valueOf(year));
            if (year == now.getYear()) {   //补上当月数据
                ProjectStaSubitemMonthVO currentMonth = this.getMonthSta(String.valueOf(now.getYear()), String.format("%02d", now.getMonthValue()), kpis, bizProjectId);
                ProjectStaSubitemMonthEntity currentMonthEntity = new ProjectStaSubitemMonthEntity();
                BeanUtils.copyProperties(currentMonth, currentMonthEntity);
                monthDatas.add(currentMonthEntity);
            }
            //查询年数据
            ProjectStaSubitemYearVO yearDataVO = this.getYearSta(String.valueOf(now.getYear()), kpis, bizProjectId);
            ProjectStaSubitemYearEntity yearData = new ProjectStaSubitemYearEntity();
            BeanUtils.copyProperties(yearDataVO, yearData);
            Map<String, ProjectStaSubitemMonthEntity> yearMapData = monthDatas.stream().collect(
                    Collectors.toMap(ProjectStaSubitemMonthEntity::getMonth, t -> t));


            //饼图
            List<String> pieChartXList = new ArrayList<>();
            List<String> pieChartYList = new ArrayList<>();

            for (int i = 0; i < kpis.size(); i++) {
                String kpi = kpis.get(i);
                String kpiName = kpiNames.get(i);
                pieChartXList.add(kpiName);
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
                CommonStaVO staVO = new CommonStaVO(kpiName, Arrays.asList(barChartXList), barChartYList);
                barChartData.add(staVO);
            }

            //配置饼图
            List<BigDecimal> pieChartYValueList = new ArrayList<>();
            BigDecimal total = new BigDecimal(0);
            if (yearData != null) {
                for (String kpi : kpis) {
                    Object yValue = this.getValue(yearData, ProjectStaSubitemYearEntity.class, kpiCodeToProperty(kpi, ProjectStaSubitemMonthEntity.class));
                    BigDecimal byValue = yValue == null ? new BigDecimal(0) : (BigDecimal) yValue;
                    pieChartYValueList.add(byValue);
                    total = total.add(byValue);
                }
            }

            pieChartData.add(new CommonStaVO("饼图数据", pieChartXList, pieChartYValueList.stream().map(bigDecimal -> bigDecimal == null ? null : bigDecimal.toPlainString()).collect(Collectors.toList())));

        } else if ("3".equals(boardType)) {
            //查询所有分区
            List<KanbanRJDEleYearRO> areaCodes = projectStaSubareaMonthMapper.getEleSubareaCodes();
            //年统计
            String kpi = "area.electricity.energyUsage.total";
            List<ProjectStaSubareaYearVO> yearStas = areaCodes.stream().map(new Function<KanbanRJDEleYearRO, ProjectStaSubareaYearVO>() {
                @Override
                public ProjectStaSubareaYearVO apply(KanbanRJDEleYearRO s) {
                    return getYearStaSubarea(String.valueOf(year), kpi, s.getSubareaCode(), s.getName(), bizProjectId);
                }
            }).collect(Collectors.toList());

            List<KanbanRJDEleYearRO> result = yearStas.stream().map(new Function<ProjectStaSubareaYearVO, KanbanRJDEleYearRO>() {
                @Override
                public KanbanRJDEleYearRO apply(ProjectStaSubareaYearVO yearVO) {
                    return new KanbanRJDEleYearRO(yearVO.getSubareaName(), yearVO.getSubareaCode(), yearVO.getStaValue());
                }
            }).collect(Collectors.toList());


            //List<KanbanRJDEleYearRO> result = projectStaSubareaMonthMapper.getEleSubareaCodes();
            List<String> peChartDataXList = result.stream().map(kanbanRJDEleYearRO -> kanbanRJDEleYearRO.getName()).collect(Collectors.toList());
            BigDecimal total = result.stream().map(kanbanRJDEleYearRO -> kanbanRJDEleYearRO.getSvalue() == null ? new BigDecimal(0) : kanbanRJDEleYearRO.getSvalue()).reduce(BigDecimal.ZERO, BigDecimal::add);
            List<String> peChartDataYList = result.stream().map(data -> {
                BigDecimal yValue = data.getSvalue() == null ? new BigDecimal(0) : (BigDecimal) data.getSvalue();
                if (yValue.compareTo(BigDecimal.ZERO) == 0 || total.compareTo(BigDecimal.ZERO) == 0) {
                    return "0";
                } else {
                    return (yValue.divide(total, 2, RoundingMode.HALF_EVEN)).toString();
                }
            }).collect(Collectors.toList());
            CommonStaVO pieVo = new CommonStaVO("分区饼图", peChartDataXList, result.stream().map(kanbanRJDEleYearRO -> kanbanRJDEleYearRO.getSvalue().toPlainString()).collect(Collectors.toList()));
            pieChartData.add(pieVo);
            //柱状图
            for (KanbanRJDEleYearRO area : areaCodes) {
                List<ProjectStaSubareaMonthEntity> barDatas = projectStaSubareaMonthMapper.getEleYearBarData(kpi, area.getSubareaCode(), bizProjectId, String.valueOf(year));
                ProjectStaSubareaMonthVO currentMonthVO = this.getMonthStaSubarea(String.valueOf(year), String.format("%02d", now.getMonthValue()), kpi, area.getSubareaCode(), bizProjectId);
                if (currentMonthVO == null) {
                    currentMonthVO = new ProjectStaSubareaMonthVO();
                    currentMonthVO.setYear(String.valueOf(year));
                    currentMonthVO.setMonth(String.valueOf(now.getMonthValue()));
                    currentMonthVO.setKpiCode(kpi);
                    currentMonthVO.setSubareaCode(area.getSubareaCode());
                    currentMonthVO.setSubareaName(area.getName());
                    currentMonthVO.setStaValue(BigDecimal.ZERO);
                }
                ProjectStaSubareaMonthEntity currentMonthEntity = new ProjectStaSubareaMonthEntity();
                BeanUtils.copyProperties(currentMonthVO, currentMonthEntity);
                barDatas.add(currentMonthEntity);

                Map<String, ProjectStaSubareaMonthEntity> barMapData = barDatas.stream().collect(Collectors.toMap(ProjectStaSubareaMonthEntity::getMonth, t -> t));
                List<String> barChartDataXList = Arrays.asList(barChartXList);
                List<String> barChartDataYList = barChartDataXList.stream().map(s -> barMapData.get(s.substring(0, s.indexOf("月"))) == null ? "" : barMapData.get(s.substring(0, s.indexOf("月"))).getStaValue().toString()).collect(Collectors.toList());
                CommonStaVO barVo = new CommonStaVO(area.getName(), barChartDataXList, barChartDataYList);
                barChartData.add(barVo);
            }

//            KanbanRJDEleYearRO root = projectStaSubareaMonthMapper.getEleRootSubareaCodes();
//            List<ProjectStaSubareaMonthEntity> barDatas = projectStaSubareaMonthMapper.getEleYearBarData();
//            ProjectStaSubareaMonthVO currentMonthVO = this.getMonthStaSubarea(String.valueOf(year), String.format("%02d", now.getMonthValue()), kpi, root.getSubareaCode(), bizProjectId);
//            if(currentMonthVO == null){
//                currentMonthVO = new ProjectStaSubareaMonthVO();
//                currentMonthVO.setYear(String.valueOf(year));
//                currentMonthVO.setMonth(String.valueOf(now.getMonthValue()));
//                currentMonthVO.setKpiCode(kpi);
//                currentMonthVO.setSubareaCode(root.getSubareaCode());
//                currentMonthVO.setSubareaName(root.getName());
//                currentMonthVO.setStaValue(BigDecimal.ZERO);
//            }
//
//            ProjectStaSubareaMonthEntity currentMonthEntity = new ProjectStaSubareaMonthEntity();
//            BeanUtils.copyProperties(currentMonthVO, currentMonthEntity);
//            barDatas.add(currentMonthEntity);
//
//            Map<String, ProjectStaSubareaMonthEntity> barMapData = barDatas.stream().collect(Collectors.toMap(ProjectStaSubareaMonthEntity::getMonth, t -> t));
//            List<String> barChartDataXList = Arrays.asList(barChartXList);
//            List<String> barChartDataYList = barChartDataXList.stream().map(s -> barMapData.get(s.substring(0, s.indexOf("月"))) == null ? "" : barMapData.get(s.substring(0, s.indexOf("月"))).getStaValue().toString()).collect(Collectors.toList());
//            CommonStaVO barVo = new CommonStaVO("锦江体验中心酒店", barChartDataXList, barChartDataYList);
//            barChartData.add(barVo);
        }

        return new KanbanRJDYearEleVO(pieChartData, barChartData);
    }

    @Override
    public KanbanRJDElectricDayVO getRJDElectricDay(String bizProjectId) {
        KanbanRJDElectricDayVO response = new KanbanRJDElectricDayVO();
        //统计四个设备
        String[] devices = new String[]{"D000000000045", "D000000000046", "D000000000047", "D000000000012"};
        LocalDateTime now = LocalDateTime.now();
        String today = now.format(DateUtils.LC_DT_FMT_DAY);
        String yestoday = now.minusDays(1L).format(DateUtils.LC_DT_FMT_DAY);
        String tomorrow = now.plusDays(1L).format(DateUtils.LC_DT_FMT_DAY);
        List<ProjectStaDeviceElectricityHourEntity> yestodaySta = projectStaDeviceElectricityHourMapper.getDailyEle(Arrays.asList(devices), yestoday, today);
        Map<String, BigDecimal> yestodayStaMap = yestodaySta.stream().collect(Collectors.toMap(ProjectStaDeviceElectricityHourEntity::getHour, entity -> entity.getEnergymeterEpimportTotal() == null ? new BigDecimal(0) : entity.getEnergymeterEpimportTotal()));
        List<ProjectStaDeviceElectricityHourEntity> todaySta = projectStaDeviceElectricityHourMapper.getDailyEle(Arrays.asList(devices), today, tomorrow);
        Map<String, BigDecimal> todayStaMap = todaySta.stream().collect(Collectors.toMap(ProjectStaDeviceElectricityHourEntity::getHour, entity -> entity.getEnergymeterEpimportTotal() == null ? new BigDecimal(0) : entity.getEnergymeterEpimportTotal()));

        KanbanRJDElectricDayDetailVO todayVo = new KanbanRJDElectricDayDetailVO();
        KanbanRJDElectricDayDetailVO yestodayVo = new KanbanRJDElectricDayDetailVO();
        List<String> xlist = Arrays.asList(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"});
        List<String> ylistYestoday = xlist.stream().map(s -> yestodayStaMap.get(s) == null ? "" : yestodayStaMap.get(s).toPlainString()).collect(Collectors.toList());
        List<String> ylistToday = xlist.stream().map(s -> yestodayStaMap.get(s) == null ? "" : yestodayStaMap.get(s).toPlainString()).collect(Collectors.toList());
        todayVo.setXlist(xlist);
        todayVo.setYlist(ylistToday);
        yestodayVo.setXlist(xlist);
        yestodayVo.setYlist(ylistYestoday);
        response.setToday(todayVo);
        response.setYestoday(yestodayVo);
        return response;
    }

    @Override
    public List<CommonStaVO> getCheckinRate(String year) {
        List<CommonStaVO> result = new ArrayList<>();
        Map<String, BigDecimal> checkinMapCurrentYear = checkinMonthMapper.selectList(new LambdaQueryWrapper<CheckinMonthEntity>().eq(CheckinMonthEntity::getYear, year).eq(CheckinMonthEntity::getBizProjectId, ProjectConstant.BIZ_PROJECT_ID_JJGJ))
                .stream().collect(Collectors.toMap(CheckinMonthEntity::getMonth, CheckinMonthEntity::getCheckinRate));
        Map<String, BigDecimal> checkinMapLastYear = checkinMonthMapper.selectList(new LambdaQueryWrapper<CheckinMonthEntity>().eq(CheckinMonthEntity::getYear, String.valueOf(Integer.valueOf(year) - 1)).eq(CheckinMonthEntity::getBizProjectId, ProjectConstant.BIZ_PROJECT_ID_JJGJ))
                .stream().collect(Collectors.toMap(CheckinMonthEntity::getMonth, CheckinMonthEntity::getCheckinRate));
        final Map<String, BigDecimal> finalCheckinMapCurrentYear = checkinMapCurrentYear == null ? new HashMap<String, BigDecimal>() : checkinMapCurrentYear;
        final Map<String, BigDecimal> finalCheckinMapLastYear = checkinMapLastYear == null ? new HashMap<String, BigDecimal>() : checkinMapLastYear;

        CommonStaVO currentYear = new CommonStaVO("今年",
                Arrays.asList(X_LIST_YEAR_DESC),
                Arrays.stream(X_LIST_YEAR).map(month -> KpiUtils.show(finalCheckinMapCurrentYear.getOrDefault(Integer.valueOf(month).toString(), BigDecimal.ZERO))).collect(Collectors.toList()));
        CommonStaVO lastYear = new CommonStaVO("去年",
                Arrays.asList(X_LIST_YEAR_DESC),
                Arrays.stream(X_LIST_YEAR).map(month -> KpiUtils.show(finalCheckinMapLastYear.getOrDefault(Integer.valueOf(month).toString(), BigDecimal.ZERO))).collect(Collectors.toList()));
        result.add(lastYear);
        result.add(currentYear);
        return result;
    }


    private YearMonth[] getCurrentYearMonth(YearMonth now) {
        List<YearMonth> yearMonths = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            yearMonths.add(YearMonth.of(now.getYear(), i));
        }
        return yearMonths.toArray(new YearMonth[0]);
    }

    private YearMonth[] getLastYearMonth(YearMonth now) {
        List<YearMonth> yearMonths = new ArrayList<>();
        int lastYear = now.minusYears(1).getYear();
        for (int i = 1; i <= 12; i++) {
            yearMonths.add(YearMonth.of(lastYear, i));
        }
        return yearMonths.toArray(new YearMonth[0]);
    }

    private LocalDate[] getCurrentMonthDay(LocalDate now) {
        List<LocalDate> monthDays = new ArrayList<>();
        int days = now.lengthOfMonth();
        for (int i = 1; i <= days; i++) {
            monthDays.add(LocalDate.of(now.getYear(), now.getMonth(), i));
        }
        return monthDays.toArray(new LocalDate[0]);
    }

    private BigDecimal getValue(SubitemIndexEnum key, Map<SubitemIndexEnum, BigDecimal> values) {
        if (values == null) {
            return null;
        }
        return values.get(key);
    }

    private BigDecimal getMonthsSum(SubitemIndexEnum key, Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> dataMap) {
        return dataMap.keySet().stream().map(yearMonth -> dataMap.get(yearMonth).get(key)).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getCarbonDensity(String year) {
        SubitemIndexEnum[] kpis = new SubitemIndexEnum[]{
                // 碳排
                SubitemIndexEnum.CARBON_DIOXIDE_EMISSIONS,
                // 电碳排
                SubitemIndexEnum.CARBON_ELECTRICITY_USAGE_CO2_TOTAL,
                // 气碳排
                SubitemIndexEnum.CARBON_GAS_USAGE_CO2_TOTAL,
        };
        //当年累计
        LocalDateTime now = LocalDateTime.now();
        YearMonth yearMonth = YearMonth.of(Integer.valueOf(year), now.getMonthValue());
        IndexCumulativeMonthRequest indexCumulativeMonthRequest = new IndexCumulativeMonthRequest();
        indexCumulativeMonthRequest.setMonth(yearMonth);
        indexCumulativeMonthRequest.setProjectBizId(ProjectConstant.BIZ_PROJECT_ID_JJGJ);
        indexCumulativeMonthRequest.setIndices(kpis);
        Response<Map<SubitemIndexEnum, BigDecimal>> searchDataByMonthCumulative = subitemApi.searchDataByMonthCumulative(indexCumulativeMonthRequest);
        Map<SubitemIndexEnum, BigDecimal> cumulativeMonth = searchDataByMonthCumulative.getResult();
        // 电 + 气
        BigDecimal currentYearTotal = NumberUtil.add(
                getValue(SubitemIndexEnum.CARBON_ELECTRICITY_USAGE_CO2_TOTAL, cumulativeMonth),
                getValue(SubitemIndexEnum.CARBON_GAS_USAGE_CO2_TOTAL, cumulativeMonth)
        );
        return currentYearTotal.multiply(BigDecimal.valueOf(1000)).divide(getArea(ProjectConstant.BIZ_PROJECT_ID_JJGJ), 4, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getElectricityDensity(String year) {
        SubitemIndexEnum[] kpis = new SubitemIndexEnum[]{
                // 全部用电量
                SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,
        };
        //当年累计
        LocalDateTime now = LocalDateTime.now();
        YearMonth yearMonth = YearMonth.of(Integer.valueOf(year), now.getMonthValue());
        IndexCumulativeMonthRequest indexCumulativeMonthRequest = new IndexCumulativeMonthRequest();
        indexCumulativeMonthRequest.setMonth(yearMonth);
        indexCumulativeMonthRequest.setProjectBizId(ProjectConstant.BIZ_PROJECT_ID_JJGJ);
        indexCumulativeMonthRequest.setIndices(kpis);
        Response<Map<SubitemIndexEnum, BigDecimal>> searchDataByMonthCumulative = subitemApi.searchDataByMonthCumulative(indexCumulativeMonthRequest);
        Map<SubitemIndexEnum, BigDecimal> cumulativeMonth = searchDataByMonthCumulative.getResult();
        BigDecimal currentYearTotal = getValue(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS, cumulativeMonth);
        return currentYearTotal.divide(getArea(ProjectConstant.BIZ_PROJECT_ID_JJGJ), 4, RoundingMode.HALF_UP);
    }

    @Override
    public List<CommonProjectTreeVO> getProjectTree() {
        List<ProjectEntity> list = projectMapper.selectList(new LambdaQueryWrapper<ProjectEntity>());
        return list.stream().map(project -> new CommonProjectTreeVO(project.getBizProjectId(), project.getName(), true, null)).collect(Collectors.toList());
    }

    private boolean isThisYear(String year) {
        LocalDateTime now = LocalDateTime.now();
        if (Integer.valueOf(year) == now.getYear()) {
            return true;
        } else {
            return false;
        }
    }

    public ProjectStaSubareaMonthVO getMonthStaSubarea(String year, String month, String kpi, String subareaCode, String bizProjectId) {
        String begin = String.format("%s-%s-01", year, month);
        DateTime date = DateUtil.parse(begin, DateUtils.LC_DT_FMT_DAY);
        Date dateEnd = DateUtil.endOfMonth(date);
        String end = DateUtils.parseDate(dateEnd);
        ProjectStaSubareaMonthVO vo = projectStaSubareaMonthMapper.getMonthTotal(kpi, subareaCode, bizProjectId, begin, end);
        if (vo == null) {
            return null;
        } else {
            vo.setYear(year);
            vo.setMonth(month.replaceFirst("^0*", ""));
        }
        return vo;
    }

    /**
     * 统计分区年数据
     *
     * @param year
     * @param kpi
     * @param subareaCode
     * @param subareaName
     * @param bizProjectId
     * @return
     */
    public ProjectStaSubareaYearVO getYearStaSubarea(String year, String kpi, String subareaCode, String subareaName, String bizProjectId) {
        ProjectStaSubareaYearVO staYear = new ProjectStaSubareaYearVO(kpi, subareaCode, subareaName, bizProjectId);
        if (isThisYear(year)) {
            LocalDateTime now = LocalDateTime.now();
            BigDecimal total = BigDecimal.ZERO;
            //当年累计 1-当前月
            for (int month = 1; month <= now.getMonthValue(); month++) {
                ProjectStaSubareaMonthVO staMonth = this.getMonthStaSubarea(year, String.format("%02d", month), kpi, subareaCode, bizProjectId);
                if (staMonth != null) {
                    total = NumberUtil.add(total, staMonth.getStaValue());
                }
            }
            //ProjectStaSubareaMonthVO staCurrentMonth = this.getMonthStaSubarea(year, String.format("%02d", now.getMonthValue()), kpi, subareaCode, bizProjectId);
            staYear.setStaValue(total);
        } else {
            ProjectStaSubareaYearVO staData = projectStaSubareaYearMapper.getYearTotal(kpi, subareaCode, bizProjectId, year);
            staYear.setStaValue(staData == null ? BigDecimal.ZERO : staData.getStaValue());
        }
        staYear.setYear(year);
        return staYear;
    }

    @Override
    public ProjectStaSubitemMonthVO getMonthSta(String year, String month, List<String> kpiCode, String bizProjectId) {
        return this.getMonthSta(year, month, null, null, kpiCode, bizProjectId);
    }

    @Override
    public ProjectStaSubitemMonthVO getMonthSta(String year, String month, String staBeginDate, String staEndDate, List<String> kpiCode, String bizProjectId) {
        ProjectStaSubitemMonthVO staMonth = new ProjectStaSubitemMonthVO();
        String dateBegin = StringUtils.isBlank(staBeginDate) ? String.format("%s-%s-01", year, month) : staBeginDate;
        DateTime date = DateUtil.parse(dateBegin, DateUtils.LC_DT_FMT_DAY);
        //Date dateEnd = StringUtils.isBlank(staEndDate) ? DateUtil.endOfMonth(date) : staEndDate;
        String begin = dateBegin;
        String end = StringUtils.isBlank(staEndDate) ? DateUtils.parseDate(DateUtil.endOfMonth(date)) : staEndDate;
        LambdaQueryWrapper<ProjectStaSubitemDayEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ProjectStaSubitemDayEntity::getBizProjectId, bizProjectId);
        lqw.ge(ProjectStaSubitemDayEntity::getStaTime, begin);
        lqw.le(ProjectStaSubitemDayEntity::getStaTime, end);
        List<ProjectStaSubitemDayEntity> result = projectStaSubitemDayMapper.selectList(lqw);
        if (result != null && result.size() > 0) {
            List<String> getMethods = kpiCode.stream().map(kpi -> KpiUtils.getMethod(KpiUtils.kpiToProperty(kpi), "get")).collect(Collectors.toList());
            List<String> setMethods = kpiCode.stream().map(kpi -> KpiUtils.getMethod(KpiUtils.kpiToProperty(kpi), "set")).collect(Collectors.toList());
            for (int i = 0; i < getMethods.size(); i++) {
                String getMethod = getMethods.get(i);
                String setMethod = setMethods.get(i);
                BigDecimal total = new BigDecimal(0);
                for (ProjectStaSubitemDayEntity subitemDay : result) {
                    BigDecimal value = KpiUtils.getValue(subitemDay, ProjectStaSubitemDayEntity.class, getMethod) == null ? new BigDecimal(0) : (BigDecimal) KpiUtils.getValue(subitemDay, ProjectStaSubitemDayEntity.class, getMethod);
                    total = total.add(value);
                }
                KpiUtils.setValue(staMonth, ProjectStaSubitemMonthVO.class, setMethod, total);
            }
        }
        //补全字段
        staMonth.setYear(year);
        staMonth.setMonth(new BigDecimal(month).toPlainString());
        //todo 尖峰谷平电费暂时手动计算
        List<ProjectCnfTimePeriodEntity> periodCnfs = projectCnfTimePeriodMapper.selectList(new LambdaQueryWrapper<ProjectCnfTimePeriodEntity>().eq(ProjectCnfTimePeriodEntity::getProjectId, bizProjectId)
                .eq(ProjectCnfTimePeriodEntity::getPeriodYear, staMonth.getYear()).eq(ProjectCnfTimePeriodEntity::getPeriodMonth, staMonth.getMonth()));
        Map<String, BigDecimal> periodCnfsMap = new HashMap<>();
        periodCnfsMap = periodCnfs.stream().collect(HashMap::new, (map, item) -> map.put(item.getCode(), item.getPrice()), HashMap::putAll);
        staMonth.setProjectElectricityEnergyusagefeeTip(staMonth.getProjectElectricityEnergyusageTip() == null ? BigDecimal.ZERO : staMonth.getProjectElectricityEnergyusageTip().multiply(periodCnfsMap.get("tip") == null ? BigDecimal.ZERO : periodCnfsMap.get("tip")).setScale(2, RoundingMode.HALF_UP));
        staMonth.setProjectElectricityEnergyusagefeePeak(staMonth.getProjectElectricityEnergyusagePeak() == null ? BigDecimal.ZERO : staMonth.getProjectElectricityEnergyusagePeak().multiply(periodCnfsMap.get("peak") == null ? BigDecimal.ZERO : periodCnfsMap.get("peak")).setScale(2, RoundingMode.HALF_UP));
        staMonth.setProjectElectricityEnergyusagefeeValley(staMonth.getProjectElectricityEnergyusageValley() == null ? BigDecimal.ZERO : staMonth.getProjectElectricityEnergyusageValley().multiply(periodCnfsMap.get("valley") == null ? BigDecimal.ZERO : periodCnfsMap.get("valley")).setScale(2, RoundingMode.HALF_UP));
        staMonth.setProjectElectricityEnergyusagefeeFlat(staMonth.getProjectElectricityEnergyusageFlat() == null ? BigDecimal.ZERO : staMonth.getProjectElectricityEnergyusageFlat().multiply(periodCnfsMap.get("flat") == null ? BigDecimal.ZERO : periodCnfsMap.get("flat")).setScale(2, RoundingMode.HALF_UP));

        return staMonth;
    }

    @Override
    public ProjectStaSubitemYearVO getYearSta(String year, List<String> kpiCodes, String bizProjectId) {
        //从Month统计表中统计
        ProjectStaSubitemYearVO staYear = new ProjectStaSubitemYearVO();
        List<ProjectStaSubitemMonthEntity> result = projectStaSubitemMonthMapper.selectList(new LambdaQueryWrapper<ProjectStaSubitemMonthEntity>().eq(ProjectStaSubitemMonthEntity::getYear, year).eq(ProjectStaSubitemMonthEntity::getBizProjectId, bizProjectId));
        List<String> getMethods = kpiCodes.stream().map(kpi -> KpiUtils.getMethod(KpiUtils.kpiToProperty(kpi), "get")).collect(Collectors.toList());
        List<String> setMethods = kpiCodes.stream().map(kpi -> KpiUtils.getMethod(KpiUtils.kpiToProperty(kpi), "set")).collect(Collectors.toList());
        //todo 尖峰谷平电费暂时手动计算
        //查询 尖峰谷平电费
        List<ProjectCnfTimePeriodEntity> periodCnfs = projectCnfTimePeriodMapper.selectList(new LambdaQueryWrapper<ProjectCnfTimePeriodEntity>().eq(ProjectCnfTimePeriodEntity::getProjectId, bizProjectId).eq(ProjectCnfTimePeriodEntity::getPeriodYear, year));
        Map<String, List<ProjectCnfTimePeriodEntity>> periodCnfsGMap = periodCnfs.stream().collect(Collectors.groupingBy(ProjectCnfTimePeriodEntity::getPeriodMonth));
        Map<String, Map<String, BigDecimal>> periodCnfsMap = new HashMap<>();
        periodCnfsGMap.forEach((month, cnfs) -> periodCnfsMap.put(month, cnfs.stream().collect(HashMap::new, (k, v) -> k.put(v.getCode(), v.getPrice() == null ? BigDecimal.ZERO : v.getPrice()), HashMap::putAll)));

        if (result != null && result.size() > 0) {
            //todo 尖峰谷平电费暂时手动计算
            result = result.stream().map(subitem -> {
                subitem.setProjectElectricityEnergyusagefeeTip(subitem.getProjectElectricityEnergyusageTip() == null ? BigDecimal.ZERO : subitem.getProjectElectricityEnergyusageTip().multiply(periodCnfsMap.getOrDefault(subitem.getMonth(), new HashMap<>()).getOrDefault("tip", BigDecimal.ZERO)).setScale(2, RoundingMode.HALF_UP));
                subitem.setProjectElectricityEnergyusagefeeValley(subitem.getProjectElectricityEnergyusageValley() == null ? BigDecimal.ZERO : subitem.getProjectElectricityEnergyusageValley().multiply(periodCnfsMap.getOrDefault(subitem.getMonth(), new HashMap<>()).getOrDefault("valley", BigDecimal.ZERO)).setScale(2, RoundingMode.HALF_UP));
                subitem.setProjectElectricityEnergyusagefeePeak(subitem.getProjectElectricityEnergyusagePeak() == null ? BigDecimal.ZERO : subitem.getProjectElectricityEnergyusagePeak().multiply(periodCnfsMap.getOrDefault(subitem.getMonth(), new HashMap<>()).getOrDefault("peak", BigDecimal.ZERO)).setScale(2, RoundingMode.HALF_UP));
                subitem.setProjectElectricityEnergyusagefeeFlat(subitem.getProjectElectricityEnergyusageFlat() == null ? BigDecimal.ZERO : subitem.getProjectElectricityEnergyusageFlat().multiply(periodCnfsMap.getOrDefault(subitem.getMonth(), new HashMap<>()).getOrDefault("flat", BigDecimal.ZERO)).setScale(2, RoundingMode.HALF_UP));
                return subitem;
            }).collect(Collectors.toList());

            for (int i = 0; i < getMethods.size(); i++) {
                String getMethod = getMethods.get(i);
                String setMethod = setMethods.get(i);
                BigDecimal total = new BigDecimal(0);
                for (ProjectStaSubitemMonthEntity subitemMonth : result) {
                    BigDecimal value = (BigDecimal) KpiUtils.getValue(subitemMonth, ProjectStaSubitemMonthEntity.class, getMethod);
                    total = total.add(value == null ? new BigDecimal(0) : value);
                }
                KpiUtils.setValue(staYear, ProjectStaSubitemYearVO.class, setMethod, total);
            }
        }
        //累计当月统计
        LocalDateTime now = LocalDateTime.now();
        ProjectStaSubitemMonthVO currentMonth = this.getMonthSta(year, String.format("%02d", now.getMonthValue()), kpiCodes, bizProjectId);
        for (int i = 0; i < getMethods.size(); i++) {
            String getMethod = getMethods.get(i);
            String setMethod = setMethods.get(i);
            BigDecimal valueCurrentMonth = KpiUtils.getValue(currentMonth, ProjectStaSubitemMonthVO.class, getMethod) == null ? new BigDecimal(0) : (BigDecimal) KpiUtils.getValue(currentMonth, ProjectStaSubitemMonthVO.class, getMethod);
            BigDecimal valueYearTotal = KpiUtils.getValue(staYear, ProjectStaSubitemYearVO.class, getMethod) == null ? new BigDecimal(0) : (BigDecimal) KpiUtils.getValue(staYear, ProjectStaSubitemYearVO.class, getMethod);
            KpiUtils.setValue(staYear, ProjectStaSubitemYearVO.class, setMethod, valueCurrentMonth.add(valueYearTotal));
        }
        //补全字段
        staYear.setYear(year);
        return staYear;
    }

    private Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> getStaByMonths(List<YearMonth> months, String bizProjectId, SubitemIndexEnum[] kpis) {
        IndexMonthRequest indexMonthRequest = new IndexMonthRequest();
        indexMonthRequest.setMonths(months.toArray(new YearMonth[]{}));
        indexMonthRequest.setProjectBizId(bizProjectId);
        indexMonthRequest.setIndices(kpis);
        Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> response = subitemApi.searchDataByMonth(indexMonthRequest);
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> monthsResult = response.getResult();
        return monthsResult;
    }

    @Override
    public List<CommonStaVO> getCostMonths(String bizProjectId, String year) {
        List<CommonStaVO> costs = new ArrayList<>();
        SubitemIndexEnum[] kpis = new SubitemIndexEnum[]{
                // 电费
                SubitemIndexEnum.ELECTRICITY_ENERGY_USAGE_FEE_TOTAL,
                // 水费
                SubitemIndexEnum.WATER_FEE_TOTAL,
                // 气费
                SubitemIndexEnum.GAS_FEE_TOTAL,
        };

        //统计年
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> thisYearCost = getStaByMonths(
                Arrays.stream(X_LIST_YEAR).map(month -> YearMonth.of(Integer.valueOf(year), Integer.valueOf(month))).collect(Collectors.toList()),
                bizProjectId,
                kpis
        );
        //统计年上年
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> lastYearCost = getStaByMonths(
                Arrays.stream(X_LIST_YEAR).map(month -> YearMonth.of(Integer.valueOf(year) - 1, Integer.valueOf(month))).collect(Collectors.toList()),
                bizProjectId,
                kpis
        );

        CommonStaVO costElectricity = new CommonStaVO(
                "电度电费",
                Arrays.asList(X_LIST_YEAR_DESC),
                Arrays.stream(X_LIST_YEAR).map(s -> thisYearCost.get(YearMonth.of(Integer.valueOf(year), Integer.valueOf(s))).get(SubitemIndexEnum.ELECTRICITY_ENERGY_USAGE_FEE_TOTAL).toPlainString()).collect(Collectors.toList())
        );

        CommonStaVO costWater = new CommonStaVO(
                "水费",
                Arrays.asList(X_LIST_YEAR_DESC),
                Arrays.stream(X_LIST_YEAR).map(s -> thisYearCost.get(YearMonth.of(Integer.valueOf(year), Integer.valueOf(s))).get(SubitemIndexEnum.WATER_FEE_TOTAL).toPlainString()).collect(Collectors.toList())
        );

        CommonStaVO costGas = new CommonStaVO(
                "气费",
                Arrays.asList(X_LIST_YEAR_DESC),
                Arrays.stream(X_LIST_YEAR).map(s -> thisYearCost.get(YearMonth.of(Integer.valueOf(year), Integer.valueOf(s))).get(SubitemIndexEnum.GAS_FEE_TOTAL).toPlainString()).collect(Collectors.toList())
        );
        BigDecimal electicityFeeTotal = thisYearCost.keySet().stream().map(key -> thisYearCost.get(key).get(SubitemIndexEnum.ELECTRICITY_ENERGY_USAGE_FEE_TOTAL)).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal waterFeeTotal = thisYearCost.keySet().stream().map(key -> thisYearCost.get(key).get(SubitemIndexEnum.WATER_FEE_TOTAL)).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal gasFeeTotal = thisYearCost.keySet().stream().map(key -> thisYearCost.get(key).get(SubitemIndexEnum.GAS_FEE_TOTAL)).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalFeeThisyear = NumberUtil.add(
                thisYearCost.keySet().stream().map(key -> thisYearCost.get(key).get(SubitemIndexEnum.ELECTRICITY_ENERGY_USAGE_FEE_TOTAL)).reduce(BigDecimal.ZERO, BigDecimal::add),
                thisYearCost.keySet().stream().map(key -> thisYearCost.get(key).get(SubitemIndexEnum.WATER_FEE_TOTAL)).reduce(BigDecimal.ZERO, BigDecimal::add),
                thisYearCost.keySet().stream().map(key -> thisYearCost.get(key).get(SubitemIndexEnum.GAS_FEE_TOTAL)).reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        BigDecimal totalFeeLastyear = NumberUtil.add(
                lastYearCost.keySet().stream().map(key -> lastYearCost.get(key).get(SubitemIndexEnum.ELECTRICITY_ENERGY_USAGE_FEE_TOTAL)).reduce(BigDecimal.ZERO, BigDecimal::add),
                lastYearCost.keySet().stream().map(key -> lastYearCost.get(key).get(SubitemIndexEnum.WATER_FEE_TOTAL)).reduce(BigDecimal.ZERO, BigDecimal::add),
                lastYearCost.keySet().stream().map(key -> lastYearCost.get(key).get(SubitemIndexEnum.GAS_FEE_TOTAL)).reduce(BigDecimal.ZERO, BigDecimal::add)
        );
        costs.add(costElectricity);
        costs.add(costWater);
        costs.add(costGas);


        //计算同比
        List<String> ylist = Arrays.stream(X_LIST_YEAR).map(month -> {
            if (Integer.valueOf(year) == LocalDateTime.now().getYear() && Integer.valueOf(month) < LocalDateTime.now().getMonthValue()) {
                BigDecimal staCurrent = staSum(thisYearCost, kpis, YearMonth.of(Integer.valueOf(year), Integer.valueOf(month)));
                BigDecimal staLast = staSum(lastYearCost, kpis, YearMonth.of(Integer.valueOf(year) - 1, Integer.valueOf(month)));
                return KpiUtils.getYoy(staCurrent, staLast);
            }
            return null;
        }).collect(Collectors.toList());
        CommonStaVO yoy = new CommonStaVO(
                "同比",
                Arrays.asList(X_LIST_YEAR_DESC),
                ylist
        );
        costs.add(yoy);

        return costs;
    }

    @Override
    public List<CommonStaVO> getCostMonthsTrend(String bizProjectId, String year) {
        List<CommonStaVO> costs = new ArrayList<>();
        SubitemIndexEnum[] kpis = new SubitemIndexEnum[]{
                // 能耗 -电
                SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS
        };

        //统计年
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> thisYearCost = getStaByMonths(
                Arrays.stream(X_LIST_YEAR).map(month -> YearMonth.of(Integer.valueOf(year), Integer.valueOf(month))).collect(Collectors.toList()),
                bizProjectId,
                kpis
        );
        //统计年上年
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> lastYearCost = getStaByMonths(
                Arrays.stream(X_LIST_YEAR).map(month -> YearMonth.of(Integer.valueOf(year) - 1, Integer.valueOf(month))).collect(Collectors.toList()),
                bizProjectId,
                kpis
        );


        CommonStaVO thisYear = new CommonStaVO(
                "当年",
                Arrays.asList(X_LIST_YEAR_DESC),
                Arrays.stream(X_LIST_YEAR).map(s ->
                        thisYearCost.get(YearMonth.of(Integer.valueOf(year), Integer.valueOf(s))).getOrDefault(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS, BigDecimal.ZERO)
                ).map(val -> val.compareTo(BigDecimal.ZERO) == 0 ? null : val.toPlainString()).collect(Collectors.toList())
        );

        CommonStaVO lastYear = new CommonStaVO(
                "上年",
                Arrays.asList(X_LIST_YEAR_DESC),
                Arrays.stream(X_LIST_YEAR).map(s ->
                        lastYearCost.get(YearMonth.of(Integer.valueOf(year) - 1, Integer.valueOf(s))).getOrDefault(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS, BigDecimal.ZERO)
                ).map(val -> val.compareTo(BigDecimal.ZERO) == 0 ? null : val.toPlainString()).collect(Collectors.toList())
        );

        //入住率
        List<CommonStaVO> result = new ArrayList<>();
        Map<String, BigDecimal> checkinMapCurrentYear = checkinMonthMapper.selectList(new LambdaQueryWrapper<CheckinMonthEntity>().eq(CheckinMonthEntity::getYear, year).eq(CheckinMonthEntity::getBizProjectId, ProjectConstant.BIZ_PROJECT_ID_JJGJ))
                .stream().collect(Collectors.toMap(CheckinMonthEntity::getMonth, CheckinMonthEntity::getCheckinRate));
        final Map<String, BigDecimal> finalCheckinMapCurrentYear = checkinMapCurrentYear == null ? new HashMap<String, BigDecimal>() : checkinMapCurrentYear;
        CommonStaVO currentYear = new CommonStaVO("入住率",
                Arrays.asList(X_LIST_YEAR_DESC),
                Arrays.stream(X_LIST_YEAR).map(month -> finalCheckinMapCurrentYear.getOrDefault(Integer.valueOf(month).toString(), BigDecimal.ZERO)).map(val -> val.compareTo(BigDecimal.ZERO) == 0 ? null : val.toPlainString()).collect(Collectors.toList()));
        costs.add(thisYear);
        costs.add(lastYear);
        costs.add(currentYear);
        return costs;
    }


    @Override
    public List<CommonStaVO> getScreenCostMonths(String bizProjectId, String year) {
        List<CommonStaVO> costs = new ArrayList<>();
        SubitemIndexEnum[] kpis = new SubitemIndexEnum[]{
                // 电费
                SubitemIndexEnum.ELECTRICITY_ENERGY_USAGE_FEE_TOTAL,
                // 水费
                SubitemIndexEnum.WATER_FEE_TOTAL,
                // 气费
                SubitemIndexEnum.GAS_FEE_TOTAL,
        };

        //统计年
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> thisYearCost = getStaByMonths(
                Arrays.stream(X_LIST_YEAR).map(month -> YearMonth.of(Integer.valueOf(year), Integer.valueOf(month))).collect(Collectors.toList()),
                bizProjectId,
                kpis
        );
        //统计年上年
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> lastYearCost = getStaByMonths(
                Arrays.stream(X_LIST_YEAR).map(month -> YearMonth.of(Integer.valueOf(year) - 1, Integer.valueOf(month))).collect(Collectors.toList()),
                bizProjectId,
                kpis
        );
        //当年入住率
        Map<String, CheckinMonthEntity> checkinMap = checkinMonthMapper.selectList(
                new LambdaQueryWrapper<CheckinMonthEntity>()
                        .eq(CheckinMonthEntity::getYear, year)
                        .eq(CheckinMonthEntity::getBizProjectId, bizProjectId)
        ).stream().collect(Collectors.toMap(CheckinMonthEntity::getMonth, v -> v, (v1, v2) -> v2));
        costs.add(new CommonStaVO(
                "入住率",
                Arrays.asList(X_LIST_YEAR_DESC),
                Arrays.stream(X_LIST_YEAR_DESC).map((Function<String, String>) s -> s.replaceAll("月", "")).map(s -> {
                    return checkinMap.get(s) == null ? "" : checkinMap.get(s).getCheckinRate().toPlainString();
                }).collect(Collectors.toList())
        ));

        CommonStaVO costTotalThisYear = new CommonStaVO(
                "当年",
                Arrays.asList(X_LIST_YEAR_DESC),
                Arrays.stream(X_LIST_YEAR).map(s -> {
                    Map<SubitemIndexEnum, BigDecimal> staMonth = thisYearCost.get(YearMonth.of(Integer.valueOf(year), Integer.valueOf(s)));
                    return NumberUtil.add(staMonth.get(SubitemIndexEnum.ELECTRICITY_ENERGY_USAGE_FEE_TOTAL), staMonth.get(SubitemIndexEnum.WATER_FEE_TOTAL), staMonth.get(SubitemIndexEnum.GAS_FEE_TOTAL)).toPlainString();
                }).collect(Collectors.toList())
        );

        CommonStaVO costTotalLastYear = new CommonStaVO(
                "上年",
                Arrays.asList(X_LIST_YEAR_DESC),
                Arrays.stream(X_LIST_YEAR).map(s -> {
                    Map<SubitemIndexEnum, BigDecimal> staMonth = lastYearCost.get(YearMonth.of(Integer.valueOf(year) - 1, Integer.valueOf(s)));
                    return NumberUtil.add(staMonth.get(SubitemIndexEnum.ELECTRICITY_ENERGY_USAGE_FEE_TOTAL), staMonth.get(SubitemIndexEnum.WATER_FEE_TOTAL), staMonth.get(SubitemIndexEnum.GAS_FEE_TOTAL)).toPlainString();
                }).collect(Collectors.toList())
        );

        costs.add(costTotalThisYear);
        costs.add(costTotalLastYear);

        return costs;
    }


    private BigDecimal staSum(Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> dataMap, SubitemIndexEnum[] SubitemIndexEnums, YearMonth ym) {
        return Arrays.stream(SubitemIndexEnums).map(kpi -> dataMap.get(ym).get(kpi)).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private KanbanRJDElectricityPlanVO getPlan(LocalDateTime dt, List<YearMonth> months, IndexMonthRequest indexMonthRequest, String bizProjectId, String desc, KanbanRJDElectricityPlanVO currentPlan) {
        KanbanRJDElectricityPlanVO plan = new KanbanRJDElectricityPlanVO();
        plan.setEnergyPlan(
                projectPlannedElectricityMapper.selectList(
                        new LambdaQueryWrapper<ProjectPlannedElectricityEntity>()
                                .eq(ProjectPlannedElectricityEntity::getYear, String.valueOf(dt.getYear()))
                                .eq(ProjectPlannedElectricityEntity::getProjectBizId, bizProjectId)
                                .in(ProjectPlannedElectricityEntity::getMonth, months.stream().map(yearMonth -> String.valueOf(yearMonth.getMonthValue())).collect(Collectors.toList()))
                ).stream().map(ProjectPlannedElectricityEntity::getPlanElectricityConsumption).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add)
        );
        indexMonthRequest.setMonths(months.toArray(new YearMonth[]{}));
        indexMonthRequest.setProjectBizId(bizProjectId);
        Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> response = subitemApi.searchDataByMonth(indexMonthRequest);
        //plan.setEnergyUseage(NumberUtil.add(getMonthsSum(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS, response.getResult()), currentPlan.getEnergyUseage()));
        plan.setEnergyUseage(getMonthsSum(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS, response.getResult()));
        plan.setEnergyLeft(NumberUtil.sub(plan.getEnergyPlan(), plan.getEnergyUseage()));
        plan.setEnergyRate(plan.getEnergyPlan().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : NumberUtil.div(plan.getEnergyUseage().multiply(BigDecimal.valueOf(100)), plan.getEnergyPlan(), 2, RoundingMode.HALF_UP));
        plan.setName(desc);
        return plan;
    }

    private KanbanRJDElectricityPlanVO getPlanCurrentMonth() {
        KanbanRJDElectricityPlanVO plan = new KanbanRJDElectricityPlanVO();
        LocalDateTime now = LocalDateTime.now();
        plan.setEnergyPlan(
                projectPlannedElectricityMapper.selectList(
                        new LambdaQueryWrapper<ProjectPlannedElectricityEntity>()
                                .eq(ProjectPlannedElectricityEntity::getYear, String.valueOf(now.getYear()))
                                .eq(ProjectPlannedElectricityEntity::getProjectBizId, ProjectConstant.BIZ_PROJECT_ID_JJGJ)
                                .eq(ProjectPlannedElectricityEntity::getMonth, String.valueOf(now.getMonthValue()))
                ).stream().map(ProjectPlannedElectricityEntity::getPlanElectricityConsumption).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add)
        );
        IndexDayRequest indexDayRequest = new IndexDayRequest();
        indexDayRequest.setDays(DateUtils.getTotalDaysByMonth(String.valueOf(now.getYear()), String.valueOf(now.getMonthValue())).toArray(new LocalDate[]{}));
        indexDayRequest.setProjectBizId(ProjectConstant.BIZ_PROJECT_ID_JJGJ);
        SubitemIndexEnum[] currentDayIndices = new SubitemIndexEnum[]{
                // 总用电量
                SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,
        };
        indexDayRequest.setIndices(currentDayIndices);
        Response<Map<LocalDate, Map<SubitemIndexEnum, BigDecimal>>> mapDayResponse = subitemApi.searchDataByDay(indexDayRequest);
        BigDecimal energyTotal = mapDayResponse.getResult().keySet().stream()
                .map(dt -> getValue(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS, mapDayResponse.getResult().get(dt))).reduce(BigDecimal.ZERO, BigDecimal::add);


        plan.setEnergyUseage(energyTotal);
        plan.setEnergyLeft(NumberUtil.sub(plan.getEnergyPlan(), plan.getEnergyUseage()));
        plan.setEnergyRate(plan.getEnergyPlan() == null || plan.getEnergyPlan().compareTo(BigDecimal.ZERO) == 0 ? null :
                NumberUtil.div(plan.getEnergyUseage().multiply(BigDecimal.valueOf(100)), plan.getEnergyPlan(), 2, RoundingMode.HALF_UP));
        plan.setName("当月");
        return plan;
    }

    @Override
    public List<KanbanRJDElectricityPlanVO> getElectricityPlan(String bizProjectId) {
        List<KanbanRJDElectricityPlanVO> plans = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        List<YearMonth> currentMonths = Arrays.asList(new YearMonth[]{YearMonth.of(now.getYear(), now.getMonthValue())});
        List<YearMonth> quartzMonths = new ArrayList<>();
        List<YearMonth> halfYearMonths = new ArrayList<>();
        List<YearMonth> totalYearMonths = new ArrayList<>();
        if (now.getMonthValue() <= 6) {
            if (now.getMonthValue() <= 3) {
                quartzMonths = Arrays.stream(X_LIST_QUARTER_ONE).map(month -> YearMonth.of(now.getYear(), Integer.valueOf(month))).collect(Collectors.toList());
            } else {
                quartzMonths = Arrays.stream(X_LIST_QUARTER_TWO).map(month -> YearMonth.of(now.getYear(), Integer.valueOf(month))).collect(Collectors.toList());
            }
            halfYearMonths = Arrays.stream(X_LIST_HALF_YEAR_ONE).map(month -> YearMonth.of(now.getYear(), Integer.valueOf(month))).collect(Collectors.toList());
        } else {
            if (now.getMonthValue() <= 9) {
                quartzMonths = Arrays.stream(X_LIST_QUARTER_THREE).map(month -> YearMonth.of(now.getYear(), Integer.valueOf(month))).collect(Collectors.toList());
            } else {
                quartzMonths = Arrays.stream(X_LIST_QUARTER_FOUR).map(month -> YearMonth.of(now.getYear(), Integer.valueOf(month))).collect(Collectors.toList());
            }
            halfYearMonths = Arrays.stream(X_LIST_HALF_YEAR_TWO).map(month -> YearMonth.of(now.getYear(), Integer.valueOf(month))).collect(Collectors.toList());
        }
        totalYearMonths = Arrays.stream(X_LIST_YEAR).map(month -> YearMonth.of(now.getYear(), Integer.valueOf(month))).collect(Collectors.toList());
        SubitemIndexEnum[] kpis = new SubitemIndexEnum[]{
                // 电量
                SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,
        };

        IndexMonthRequest indexMonthRequest = new IndexMonthRequest();
        indexMonthRequest.setMonths(currentMonths.toArray(new YearMonth[]{}));
        indexMonthRequest.setIndices(kpis);

        //plans.add(this.getPlan(now,currentMonths,indexMonthRequest,bizProjectId,"当月"));
        KanbanRJDElectricityPlanVO currentPlan = this.getPlanCurrentMonth();
        plans.add(currentPlan);
        plans.add(this.getPlan(now, quartzMonths, indexMonthRequest, bizProjectId, "当季", currentPlan));
        plans.add(this.getPlan(now, halfYearMonths, indexMonthRequest, bizProjectId, "半年", currentPlan));
        plans.add(this.getPlan(now, totalYearMonths, indexMonthRequest, bizProjectId, "全年", currentPlan));
        return plans;
    }

    @Override
    public List<CommonStaVO> getElectricityTrend(String year, String month) throws ParseException {
        List<CommonStaVO> trendVOS = new ArrayList<>();
        KanbanRJDElectricityTrendVO trendVO = new KanbanRJDElectricityTrendVO();
        List<LocalDate> days = DateUtils.getTotalDaysByMonth(year, month);
        List<String> xlist = days.stream().map(date -> date.format(DateUtils.LC_DT_FMT_DAY)).collect(Collectors.toList());
        List<String> xlistShow = days.stream().map(date -> String.valueOf(date.getDayOfMonth())).collect(Collectors.toList());
        //用电量
        IndexDayRequest indexDayRequest = new IndexDayRequest();
        indexDayRequest.setDays(days.toArray(new LocalDate[]{}));
        indexDayRequest.setProjectBizId(ProjectConstant.BIZ_PROJECT_ID_JJGJ);
        SubitemIndexEnum[] currentDayIndices = new SubitemIndexEnum[]{
                // 用电量
                SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,
        };
        indexDayRequest.setIndices(currentDayIndices);
        Response<Map<LocalDate, Map<SubitemIndexEnum, BigDecimal>>> mapDayResponse = subitemApi.searchDataByDay(indexDayRequest);
        Map<LocalDate, Map<SubitemIndexEnum, BigDecimal>> energyDays = mapDayResponse.getResult();

        Map<String, BigDecimal> energyMapData = energyDays.keySet().stream().collect(Collectors.toMap(
                date -> date.format(DateUtils.LC_DT_FMT_DAY),
                date -> getValue(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS, energyDays.get(date))));
        CommonStaVO energyVO = new CommonStaVO("用电量",
                xlistShow,
                xlist.stream().map(month1 -> energyMapData.getOrDefault(month1, BigDecimal.ZERO).toPlainString()).collect(Collectors.toList())
        );

        //计划值 = 月计划/天数
        BigDecimal monthPlanData = projectPlannedElectricityMapper.selectList(new LambdaQueryWrapper<ProjectPlannedElectricityEntity>()
                .eq(ProjectPlannedElectricityEntity::getYear, year)
                .eq(ProjectPlannedElectricityEntity::getMonth, month)
                .eq(ProjectPlannedElectricityEntity::getProjectBizId, ProjectConstant.BIZ_PROJECT_ID_JJGJ)
        ).stream().map(ProjectPlannedElectricityEntity::getPlanElectricityConsumption).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        CommonStaVO planVO = new CommonStaVO("计划值",
                xlistShow,
                xlist.stream().map(month1 -> NumberUtil.div(monthPlanData, BigDecimal.valueOf(xlist.size()), 1, RoundingMode.HALF_UP).toPlainString()).collect(Collectors.toList())
        );
        //入住率
        Map<String, BigDecimal> checkInMapData = checkinDayMapper.selectList(new LambdaQueryWrapper<CheckinDayEntity>()
                .eq(CheckinDayEntity::getYear, year)
                .eq(CheckinDayEntity::getMonth, String.format("%02d", Integer.valueOf(month)))
        ).stream().collect(Collectors.toMap(checkinDayEntity -> DateUtils.SD_DT_FMT_DAY.format(checkinDayEntity.getStaTime()), CheckinDayEntity::getCheckinRate, (v1, v2) -> v2));
        ;
        CommonStaVO checkinVO = new CommonStaVO("入住率",
                xlistShow,
                xlist.stream().map(month1 -> KpiUtils.show(checkInMapData.getOrDefault(month1, BigDecimal.ZERO))).collect(Collectors.toList())
        );
        trendVOS.add(energyVO);
        trendVOS.add(planVO);
        trendVOS.add(checkinVO);

        return trendVOS;
    }

    @Override
    public List<CommonStaVO> getEnergyAnalysis(String year, String type) {
        List<CommonStaVO> analysisVos = new ArrayList<>();
        SubitemIndexEnum staEnum = null;
        if (EnergyTypeEnum.ENERGY_TYPE_WATER.getCode().equals(type)) {
            staEnum = SubitemIndexEnum.WATER_USAGE_TOTAL;
        } else if (EnergyTypeEnum.ENERGY_TYPE_GAS.getCode().equals(type)) {
            staEnum = SubitemIndexEnum.GAS_USAGE_TOTAL;
        }
        //当年-统计实际值
        IndexMonthRequest indexMonthRequest = new IndexMonthRequest();
        YearMonth[] months = Arrays.stream(X_LIST_YEAR).map((Function<String, Object>) month -> YearMonth.of(Integer.valueOf(year), Integer.valueOf(month))).collect(Collectors.toList()).toArray(new YearMonth[]{});
        indexMonthRequest.setMonths(months);
        indexMonthRequest.setProjectBizId(ProjectConstant.BIZ_PROJECT_ID_JJGJ);
        SubitemIndexEnum[] currentMonthIndices = new SubitemIndexEnum[]{
                staEnum,
        };
        indexMonthRequest.setIndices(currentMonthIndices);
        Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> staResponse = subitemApi.searchDataByMonth(indexMonthRequest);
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> staMapData = staResponse.getResult();
        //上一年-统计实际值
        YearMonth[] monthsLastYear = Arrays.stream(X_LIST_YEAR).map((Function<String, Object>) month -> YearMonth.of(Integer.valueOf(year) - 1, Integer.valueOf(month))).collect(Collectors.toList()).toArray(new YearMonth[]{});
        indexMonthRequest.setMonths(monthsLastYear);
        Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> staResponseLastYear = subitemApi.searchDataByMonth(indexMonthRequest);
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> waterMapDataLastYear = staResponseLastYear.getResult();
        SubitemIndexEnum finalStaEnum = staEnum;
        CommonStaVO planVO = null;
        if (EnergyTypeEnum.ENERGY_TYPE_WATER.getCode().equals(type)) {
            //当年-用水量计划值
            Map<YearMonth, BigDecimal> waterDataThisYear = projectPlannedWaterMapper.selectList(new LambdaQueryWrapper<ProjectPlannedWaterEntity>()
                    .eq(ProjectPlannedWaterEntity::getProjectBizId, ProjectConstant.BIZ_PROJECT_ID_JJGJ)
                    .eq(ProjectPlannedWaterEntity::getYear, year)
            ).stream().collect(Collectors.toMap(entity -> YearMonth.of(Integer.valueOf(entity.getYear()), Integer.valueOf(entity.getMonth())), entity -> Optional.ofNullable(entity.getPlanWaterConsumption()).orElse(BigDecimal.ZERO)));
            planVO = new CommonStaVO("计划",
                    Arrays.asList(X_LIST_YEAR_DESC),
                    Arrays.stream(X_LIST_YEAR).map(new Function<String, String>() {
                        @Override
                        public String apply(String month) {
                            YearMonth ym = YearMonth.of(Integer.valueOf(year), Integer.valueOf(month));
                            BigDecimal val = waterDataThisYear.get(ym);
                            return val == null ? "0" : val.toPlainString();
                        }
                    }).collect(Collectors.toList())

            );
        } else if (EnergyTypeEnum.ENERGY_TYPE_GAS.getCode().equals(type)) {
            //当年-用气量计划值
            Map<YearMonth, BigDecimal> gasDataThisYear = projectPlannedGasMapper.selectList(new LambdaQueryWrapper<ProjectPlannedGasEntity>()
                    .eq(ProjectPlannedGasEntity::getYear, year)
            ).stream().collect(Collectors.toMap(entity -> YearMonth.of(Integer.valueOf(entity.getYear()), Integer.valueOf(entity.getMonth())), entity -> Optional.ofNullable(entity.getPlanGasConsumption()).orElse(BigDecimal.ZERO)));
            planVO = new CommonStaVO("计划",
                    Arrays.asList(X_LIST_YEAR_DESC),
                    Arrays.stream(X_LIST_YEAR).map(new Function<String, String>() {
                        @Override
                        public String apply(String month) {
                            YearMonth ym = YearMonth.of(Integer.valueOf(year), Integer.valueOf(month));
                            BigDecimal val = gasDataThisYear.get(ym);
                            return val == null ? "0" : val.toPlainString();
                        }
                    }).collect(Collectors.toList())
            );
        }

        CommonStaVO actVO = new CommonStaVO("实际",
                Arrays.asList(X_LIST_YEAR_DESC),
                Arrays.stream(X_LIST_YEAR).map(new Function<String, String>() {
                    @Override
                    public String apply(String month) {
                        YearMonth ym = YearMonth.of(Integer.valueOf(year), Integer.valueOf(month));
                        BigDecimal val = getValue(finalStaEnum, staMapData.get(ym));
                        return val == null ? "0" : val.toPlainString();
                    }
                }).collect(Collectors.toList())
        );

        CommonStaVO yoyVO = new CommonStaVO("去年同比",
                Arrays.asList(X_LIST_YEAR_DESC),
                Arrays.stream(X_LIST_YEAR).map(new Function<String, String>() {
                    @Override
                    public String apply(String month) {
                        if (Integer.valueOf(year) == LocalDateTime.now().getYear() && Integer.valueOf(month) < LocalDateTime.now().getMonthValue()) {
                            BigDecimal current = getValue(finalStaEnum, staMapData.get(YearMonth.of(Integer.valueOf(year), Integer.valueOf(month))));
                            BigDecimal last = getValue(finalStaEnum, waterMapDataLastYear.get(YearMonth.of(Integer.valueOf(year) - 1, Integer.valueOf(month))));
                            return KpiUtils.getYoy(current, last);
                        }
                        return null;
                    }
                }).collect(Collectors.toList())
        );
        analysisVos.add(planVO);
        analysisVos.add(actVO);
        analysisVos.add(yoyVO);
        return analysisVos;
    }

    @Override
    public KanbanRJDEnergyCarbonKeyKpiVO getCarbonKeyKpi(String year) {
        KanbanRJDEnergyCarbonKeyKpiVO kpi = new KanbanRJDEnergyCarbonKeyKpiVO();
        SubitemIndexEnum[] indicates = new SubitemIndexEnum[]{
                // 标准煤
                SubitemIndexEnum.CARBON_USAGE_COAL_TOTAL,
                //碳排总量
                SubitemIndexEnum.CARBON_DIOXIDE_EMISSIONS,
                // 电碳排
                SubitemIndexEnum.CARBON_ELECTRICITY_USAGE_CO2_TOTAL,
                // 气碳排
                SubitemIndexEnum.CARBON_GAS_USAGE_CO2_TOTAL,
        };
        //当年累计
        LocalDateTime now = LocalDateTime.now();
        YearMonth yearMonth = YearMonth.of(Integer.valueOf(year), now.getMonthValue());
        IndexCumulativeMonthRequest indexCumulativeMonthRequest = new IndexCumulativeMonthRequest();
        indexCumulativeMonthRequest.setMonth(yearMonth);
        indexCumulativeMonthRequest.setProjectBizId(ProjectConstant.BIZ_PROJECT_ID_JJGJ);
        indexCumulativeMonthRequest.setIndices(indicates);
        Response<Map<SubitemIndexEnum, BigDecimal>> searchDataByMonthCumulative = subitemApi.searchDataByMonthCumulative(indexCumulativeMonthRequest);
        Map<SubitemIndexEnum, BigDecimal> cumulativeMonth = searchDataByMonthCumulative.getResult();

        kpi.setTotalCoal(getValue(SubitemIndexEnum.CARBON_USAGE_COAL_TOTAL, cumulativeMonth));

        kpi.setTotalCO2(NumberUtil.add(getValue(SubitemIndexEnum.CARBON_ELECTRICITY_USAGE_CO2_TOTAL, cumulativeMonth), getValue(SubitemIndexEnum.CARBON_GAS_USAGE_CO2_TOTAL, cumulativeMonth)));
        //累计碳排 往年 + 当年累计
        List<ProjectStaSubitemYearEntity> historyData = projectStaSubitemYearMapper.selectList(
                new LambdaQueryWrapper<ProjectStaSubitemYearEntity>()
                        .eq(ProjectStaSubitemYearEntity::getBizProjectId, ProjectConstant.BIZ_PROJECT_ID_JJGJ)
                        .ne(ProjectStaSubitemYearEntity::getYear, year)
        );
        BigDecimal historyValue = historyData.stream().map(entity -> NumberUtil.null2Zero(entity.getProjectCarbonElectricityusageco2Total()).add(NumberUtil.null2Zero(entity.getProjectCarbonGasusageco2Total()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        kpi.setTotalCulCO2(NumberUtil.add(historyValue, kpi.getTotalCO2()));
        //强度
        kpi.setCoalDensity(this.getCarbonDensity(year));
        return kpi;
    }

    /**
     * 获取当年碳排量
     *
     * @param year
     * @return
     */
    private BigDecimal getCo2Consumption(String year) {

        SubitemIndexEnum[] kpis = new SubitemIndexEnum[]{
                // 电耗CO2当量
                SubitemIndexEnum.CARBON_ELECTRICITY_USAGE_CO2_TOTAL,
                // 气耗CO2当量
                SubitemIndexEnum.CARBON_GAS_USAGE_CO2_TOTAL,
        };
        if (isThisYear(year)) {
            LocalDateTime now = LocalDateTime.now();
            YearMonth yearMonth = YearMonth.of(Integer.valueOf(year), now.getMonthValue());
            IndexCumulativeMonthRequest indexCumulativeMonthRequest = new IndexCumulativeMonthRequest();
            indexCumulativeMonthRequest.setMonth(yearMonth);
            indexCumulativeMonthRequest.setProjectBizId(ProjectConstant.BIZ_PROJECT_ID_JJGJ);
            indexCumulativeMonthRequest.setIndices(kpis);
            Response<Map<SubitemIndexEnum, BigDecimal>> searchDataByMonthCumulative = subitemApi.searchDataByMonthCumulative(indexCumulativeMonthRequest);
            Map<SubitemIndexEnum, BigDecimal> cumulativeMonth = searchDataByMonthCumulative.getResult();
            return NumberUtil.add(
                    getValue(SubitemIndexEnum.CARBON_ELECTRICITY_USAGE_CO2_TOTAL, cumulativeMonth),
                    getValue(SubitemIndexEnum.CARBON_GAS_USAGE_CO2_TOTAL, cumulativeMonth)
            );
        } else {
            Year staYear = Year.of(Integer.valueOf(year));
            IndexYearRequest indexYearRequest = new IndexYearRequest();
            indexYearRequest.setYears(new Year[]{staYear});
            indexYearRequest.setProjectBizId(ProjectConstant.BIZ_PROJECT_ID_JJGJ);
            indexYearRequest.setIndices(kpis);
            Response<Map<Year, Map<SubitemIndexEnum, BigDecimal>>> mapYearResponse = subitemApi.searchDataByYear(indexYearRequest);
            Map<SubitemIndexEnum, BigDecimal> yearDataMap = mapYearResponse.getResult().get(staYear);
            return NumberUtil.add(
                    getValue(SubitemIndexEnum.CARBON_ELECTRICITY_USAGE_CO2_TOTAL, yearDataMap),
                    getValue(SubitemIndexEnum.CARBON_GAS_USAGE_CO2_TOTAL, yearDataMap)
            );
        }
    }

    private BigDecimal getArea(String bizProjectId) {
        return projectMapper.selectOne(new LambdaQueryWrapper<ProjectEntity>().eq(ProjectEntity::getBizProjectId, bizProjectId)).getArea();
    }

    private Integer getAlarmStatus(KanbanRJDEnergyCarbonAlarmVO alarmVO, String year) {
        LocalDateTime now = LocalDateTime.now();
        if (alarmVO.getPlanCO2().compareTo(alarmVO.getTotalCO2()) <= 0)
            return 2;

        if (Integer.valueOf(year) == now.getYear()) {
            //当年
            BigDecimal valve = NumberUtil.div(alarmVO.getPlanCO2().multiply(BigDecimal.valueOf(now.getDayOfYear())).multiply(BigDecimal.valueOf(0.95)), BigDecimal.valueOf(365), 4, RoundingMode.HALF_UP);
            if (alarmVO.getTotalCO2().compareTo(valve) > 0)
                return 1;
        }
        return 0;
    }

    @Override
    public KanbanRJDEnergyCarbonAlarmVO carbonAlarm(String year) {
        KanbanRJDEnergyCarbonAlarmVO alarmVO = new KanbanRJDEnergyCarbonAlarmVO();
        Year staYear = Year.of(Integer.valueOf(year));
        //计划排放 = 计划用电 * 碳排因子 + 计划用气 * 因子
        //电碳排因子 0.42 t/kwh , 2.18 t/m3
        BigDecimal rateEle = BigDecimal.valueOf(0.42);
        BigDecimal rateGas = BigDecimal.valueOf(0.42);
        //计划用电
        BigDecimal elePlan = projectPlannedElectricityMapper.selectList(new LambdaQueryWrapper<ProjectPlannedElectricityEntity>()
                .eq(ProjectPlannedElectricityEntity::getYear, year)
                .eq(ProjectPlannedElectricityEntity::getProjectBizId, ProjectConstant.BIZ_PROJECT_ID_JJGJ)
        ).stream().map(ProjectPlannedElectricityEntity::getPlanElectricityConsumption).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        //计划用气
        BigDecimal gasPlan = projectPlannedGasMapper.selectList(new LambdaQueryWrapper<ProjectPlannedGasEntity>()
                .eq(ProjectPlannedGasEntity::getYear, year)
                .eq(ProjectPlannedGasEntity::getProjectBizId, ProjectConstant.BIZ_PROJECT_ID_JJGJ)
        ).stream().map(ProjectPlannedGasEntity::getPlanGasConsumption).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        alarmVO.setPlanCO2(NumberUtil.add(elePlan.multiply(rateEle), gasPlan.multiply(rateGas)).divide(getArea(ProjectConstant.BIZ_PROJECT_ID_JJGJ), 4, RoundingMode.HALF_UP));

        //实际碳排
        alarmVO.setTotalCO2(this.getCarbonDensity(year));
        //预警
        alarmVO.setAlarmStatus(getAlarmStatus(alarmVO, year));
        return alarmVO;
    }

    @Override
    public List<CommonStaVO> carbonSubitemOrder(String year) {
        LocalDateTime now = LocalDateTime.now();
        List<CommonStaVO> orders = new ArrayList<>();
        Map<SubitemIndexEnum, String> xMap = new HashMap<>();
        //查询分项列表
        TenantContext.setIgnore(true);
        List<ProjectKpiConfigEntity> configs = projectKpiConfigMapper.getJJGJSubitemKpis(ProjectConstant.BIZ_PROJECT_ID_JJGJ);
        TenantContext.setIgnore(false);
        Map<String, SubitemIndexEnum> enumMap = Arrays.stream(SubitemIndexEnum.values()).collect(
                Collectors.toMap(subitemIndexEnum -> subitemIndexEnum.getField().toUpperCase(), t -> t, (v1, v2) -> v2));
        xMap = configs.stream().collect(Collectors.toMap(
                config -> enumMap.get(KpiUtils.kpiToProperty(config.getCode()).toUpperCase()), ProjectKpiConfigEntity::getName));

        SubitemIndexEnum[] currentYearIndices = xMap.keySet().toArray(new SubitemIndexEnum[0]);
        Map<SubitemIndexEnum, String> finalXMap = xMap;
        List<String> xlist = Arrays.stream(currentYearIndices).map(subitemIndexEnum -> finalXMap.get(subitemIndexEnum)).collect(Collectors.toList());
        //分项用电
        if (isThisYear(year)) {
            YearMonth yearMonth = YearMonth.of(Integer.valueOf(year), now.getMonthValue());
            IndexCumulativeMonthRequest indexCumulativeMonthRequest = new IndexCumulativeMonthRequest();
            indexCumulativeMonthRequest.setMonth(yearMonth);
            indexCumulativeMonthRequest.setProjectBizId(ProjectConstant.BIZ_PROJECT_ID_JJGJ);
            indexCumulativeMonthRequest.setIndices(currentYearIndices);
            Response<Map<SubitemIndexEnum, BigDecimal>> searchDataByMonthCumulative = subitemApi.searchDataByMonthCumulative(indexCumulativeMonthRequest);
            Map<SubitemIndexEnum, BigDecimal> cumulativeMonth = searchDataByMonthCumulative.getResult();

            //上年
            Year staLastYear = Year.of(Integer.valueOf(year) - 1);
            IndexYearRequest indexYearRequest = new IndexYearRequest();
            indexYearRequest.setProjectBizId(ProjectConstant.BIZ_PROJECT_ID_JJGJ);
            indexYearRequest.setIndices(currentYearIndices);
            indexYearRequest.setYears(new Year[]{staLastYear});
            Response<Map<Year, Map<SubitemIndexEnum, BigDecimal>>> mapLastYearResponse = subitemApi.searchDataByYear(indexYearRequest);
            Map<SubitemIndexEnum, BigDecimal> lastYearDataMapTemp = new HashMap<>();
            if (mapLastYearResponse.isSuccess() && null != mapLastYearResponse.getResult()) {
                lastYearDataMapTemp = mapLastYearResponse.getResult().get(staLastYear);
            }
            Map<SubitemIndexEnum, BigDecimal> lastYearDataMap = lastYearDataMapTemp;

            CommonStaVO co2Sta = new CommonStaVO();
            co2Sta.setXlist(xlist);
            co2Sta.setYlist(
                    Arrays.stream(currentYearIndices).map(subitemIndexEnum -> NumberUtil.mul(new BigDecimal(0.00042), getValue(subitemIndexEnum, cumulativeMonth)).setScale(2, RoundingMode.HALF_UP).toPlainString()).collect(Collectors.toList())
            );
            co2Sta.setDesc("当年碳排量");
            CommonStaVO yoySta = new CommonStaVO();
            yoySta.setXlist(xlist);

            yoySta.setYlist(
                    Arrays.stream(currentYearIndices).map(new Function<SubitemIndexEnum, String>() {
                        @Override
                        public String apply(SubitemIndexEnum subitemIndexEnum) {
                            BigDecimal current = NumberUtil.mul(new BigDecimal(0.00042), getValue(subitemIndexEnum, cumulativeMonth)).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal last = NumberUtil.mul(new BigDecimal(0.00042), getValue(subitemIndexEnum, lastYearDataMap)).setScale(2, RoundingMode.HALF_UP);
                            return KpiUtils.getYoy(current, last);
                        }
                    }).collect(Collectors.toList())
            );
//            yoySta.setYlist(
//                    Arrays.stream(currentYearIndices).map(subitemIndexEnum -> NumberUtil.mul(new BigDecimal(0.00042),getValue(subitemIndexEnum, lastYearDataMap)).setScale(2,RoundingMode.HALF_UP).toPlainString()).collect(Collectors.toList())
//            );
            yoySta.setDesc("上年同比");
            orders.add(co2Sta);
            orders.add(yoySta);

        } else {

            Year staYear = Year.of(Integer.valueOf(year));
            Year staLastYear = Year.of(Integer.valueOf(year) - 1);
            IndexYearRequest indexYearRequest = new IndexYearRequest();
            indexYearRequest.setProjectBizId(ProjectConstant.BIZ_PROJECT_ID_JJGJ);
            indexYearRequest.setIndices(currentYearIndices);
            indexYearRequest.setYears(new Year[]{staYear});
            Response<Map<Year, Map<SubitemIndexEnum, BigDecimal>>> mapYearResponse = subitemApi.searchDataByYear(indexYearRequest);
            Map<SubitemIndexEnum, BigDecimal> yearDataMap = mapYearResponse.getResult().get(staYear);

            indexYearRequest.setYears(new Year[]{staLastYear});
            Response<Map<Year, Map<SubitemIndexEnum, BigDecimal>>> mapLastYearResponse = subitemApi.searchDataByYear(indexYearRequest);
            Map<SubitemIndexEnum, BigDecimal> lastYearDataMap = mapLastYearResponse.getResult().get(staLastYear);

            CommonStaVO co2Sta = new CommonStaVO();
            co2Sta.setXlist(xlist);
            co2Sta.setYlist(
                    Arrays.stream(currentYearIndices).map(subitemIndexEnum -> NumberUtil.mul(new BigDecimal(0.00042), getValue(subitemIndexEnum, yearDataMap)).setScale(2, RoundingMode.HALF_UP).toPlainString()).collect(Collectors.toList())
            );
            co2Sta.setDesc("当年碳排量");
            CommonStaVO yoySta = new CommonStaVO();
            yoySta.setXlist(xlist);
            yoySta.setYlist(
                    Arrays.stream(currentYearIndices).map(subitemIndexEnum -> {
                        BigDecimal current = getValue(subitemIndexEnum, yearDataMap);
                        BigDecimal last = getValue(subitemIndexEnum, lastYearDataMap);
                        return KpiUtils.getYoy(current, last);
                    }).collect(Collectors.toList())
            );
            yoySta.setDesc("上年同比");
            orders.add(co2Sta);
            orders.add(yoySta);
        }

        //添加后缀
        orders.get(0).setXlist(orders.get(0).getXlist().stream().map(s -> s.equals("空调总用电量") ? "公区".concat(s) : s).map(s -> s.concat("碳排")).collect(Collectors.toList()));
        orders.get(1).setXlist(orders.get(1).getXlist().stream().map(s -> s.equals("空调总用电量") ? "公区".concat(s) : s).map(s -> s.concat("碳排")).collect(Collectors.toList()));

        final int[] index = {0};
        List<Integer> orderList = orders.get(0).getYlist().stream().map(s -> {
            CommonStaVODetail detail = new CommonStaVODetail(null, s, index[0]);
            index[0]++;
            return detail;
        }).sorted((o1, o2) -> new BigDecimal(o1.getYValue()).compareTo(new BigDecimal(o2.getYValue()))).map(CommonStaVODetail::getOrder).collect(Collectors.toList());
        orders.get(0).setYlist(orderList.stream().map(i -> orders.get(0).getYlist().get(i)).collect(Collectors.toList()));
        orders.get(0).setXlist(orderList.stream().map(i -> orders.get(0).getXlist().get(i)).collect(Collectors.toList()));
        orders.get(1).setYlist(orderList.stream().map(i -> orders.get(1).getYlist().get(i)).collect(Collectors.toList()));
        orders.get(1).setXlist(orderList.stream().map(i -> orders.get(1).getXlist().get(i)).collect(Collectors.toList()));
        return orders;
    }

    @Override
    public KanbanRJDCarbonTrendVO getCarbonTrend(String year) {
        Integer iYear = Integer.valueOf(year);
        //柱状图
        //外购电-电碳排 天然气-气碳排 同比(电+气)
        //当年
        IndexMonthRequest indexMonthRequest = new IndexMonthRequest();
        YearMonth[] currentYearMonth = Arrays.stream(X_LIST_YEAR).map(month -> YearMonth.of(iYear, Integer.valueOf(month))).collect(Collectors.toList()).toArray(new YearMonth[]{});
        indexMonthRequest.setMonths(currentYearMonth);
        indexMonthRequest.setProjectBizId(ProjectConstant.BIZ_PROJECT_ID_JJGJ);
        SubitemIndexEnum[] currentMonthIndices = new SubitemIndexEnum[]{
                // 电碳排
                SubitemIndexEnum.CARBON_ELECTRICITY_USAGE_CO2_TOTAL,
                // 气碳排
                SubitemIndexEnum.CARBON_GAS_USAGE_CO2_TOTAL,
                // 总碳排
                SubitemIndexEnum.CARBON_DIOXIDE_EMISSIONS
        };
        indexMonthRequest.setIndices(currentMonthIndices);
        Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> currentYearResponse = subitemApi.searchDataByMonth(indexMonthRequest);
        //外购电-电碳排

        CommonStaVO staElectricity = new CommonStaVO("外购电",
                Arrays.asList(X_LIST_YEAR),
                Arrays.stream(X_LIST_YEAR).map(new Function<String, String>() {
                    @Override
                    public String apply(String month) {
                        return getValue(SubitemIndexEnum.CARBON_ELECTRICITY_USAGE_CO2_TOTAL, currentYearResponse.getResult().get(YearMonth.of(iYear, Integer.valueOf(month)))).toPlainString();
                    }
                }).collect(Collectors.toList()));
        //天然气-气碳排
        CommonStaVO staGas = new CommonStaVO("天然气",
                Arrays.asList(X_LIST_YEAR),
                Arrays.stream(X_LIST_YEAR).map(new Function<String, String>() {
                    @Override
                    public String apply(String month) {
                        return getValue(SubitemIndexEnum.CARBON_GAS_USAGE_CO2_TOTAL, currentYearResponse.getResult().get(YearMonth.of(iYear, Integer.valueOf(month)))).toPlainString();
                    }
                }).collect(Collectors.toList()));
        //当年总碳排
        BigDecimal carbonTotal = Arrays.stream(X_LIST_YEAR).map(month -> getValue(SubitemIndexEnum.CARBON_DIOXIDE_EMISSIONS, currentYearResponse.getResult().get(YearMonth.of(iYear, Integer.valueOf(month))))).reduce(BigDecimal.ZERO, BigDecimal::add);

        //上年
        YearMonth[] lastYearMonth = Arrays.stream(X_LIST_YEAR).map(month -> YearMonth.of(iYear - 1, Integer.valueOf(month))).collect(Collectors.toList()).toArray(new YearMonth[]{});
        indexMonthRequest.setMonths(lastYearMonth);
        Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> lastYearResponse = subitemApi.searchDataByMonth(indexMonthRequest);
        //外购电-电碳排-上年

        CommonStaVO staLastElectricity = new CommonStaVO("外购电",
                Arrays.asList(X_LIST_YEAR),
                Arrays.stream(X_LIST_YEAR).map(new Function<String, String>() {
                    @Override
                    public String apply(String month) {
                        return getValue(SubitemIndexEnum.CARBON_ELECTRICITY_USAGE_CO2_TOTAL, lastYearResponse.getResult().get(YearMonth.of(iYear - 1, Integer.valueOf(month)))).toPlainString();
                    }
                }).collect(Collectors.toList()));
        //天然气-气碳排-上年
        CommonStaVO staLastGas = new CommonStaVO("天然气",
                Arrays.asList(X_LIST_YEAR),
                Arrays.stream(X_LIST_YEAR).map(new Function<String, String>() {
                    @Override
                    public String apply(String month) {
                        return getValue(SubitemIndexEnum.CARBON_GAS_USAGE_CO2_TOTAL, lastYearResponse.getResult().get(YearMonth.of(iYear - 1, Integer.valueOf(month)))).toPlainString();
                    }
                }).collect(Collectors.toList()));

        CommonStaVO yoy = new CommonStaVO("上年同比",
                Arrays.asList(X_LIST_YEAR),
                Arrays.stream(X_LIST_YEAR).map(month -> {
                    BigDecimal currentYearTotal = NumberUtil.add(
                            getValue(SubitemIndexEnum.CARBON_ELECTRICITY_USAGE_CO2_TOTAL, currentYearResponse.getResult().get(YearMonth.of(iYear, Integer.valueOf(month)))),
                            getValue(SubitemIndexEnum.CARBON_GAS_USAGE_CO2_TOTAL, currentYearResponse.getResult().get(YearMonth.of(iYear, Integer.valueOf(month))))
                    );
                    BigDecimal lastYearTotal = NumberUtil.add(
                            getValue(SubitemIndexEnum.CARBON_ELECTRICITY_USAGE_CO2_TOTAL, lastYearResponse.getResult().get(YearMonth.of(iYear - 1, Integer.valueOf(month)))),
                            getValue(SubitemIndexEnum.CARBON_GAS_USAGE_CO2_TOTAL, lastYearResponse.getResult().get(YearMonth.of(iYear - 1, Integer.valueOf(month))))
                    );
                    return KpiUtils.getYoy(currentYearTotal, lastYearTotal);
                }).collect(Collectors.toList()));

        List<CommonStaVO> barChartData = new ArrayList<>();
        barChartData.add(staElectricity);
        barChartData.add(staGas);
        barChartData.add(yoy);

        // 饼图
        List<CommonStaVO> pieChartData = new ArrayList<>();
        CommonStaVO pieDataDetail = new CommonStaVO();
        pieDataDetail.setDesc("饼图数据");
        pieDataDetail.setXlist(Arrays.asList(new String[]{"外购电", "天然气"}));
        pieDataDetail.setYlist(Arrays.asList(new String[]{
                staElectricity.getYlist().stream().map(NumberUtil::toBigDecimal).reduce(BigDecimal.ZERO, BigDecimal::add).toPlainString(),
                staGas.getYlist().stream().map(NumberUtil::toBigDecimal).reduce(BigDecimal.ZERO, BigDecimal::add).toPlainString()
        }));
        pieChartData.add(pieDataDetail);


        return new KanbanRJDCarbonTrendVO(carbonTotal.toPlainString(), pieChartData, barChartData);
    }

    @Override
    public List<CommonStaVO> carbonSubareaOrder(String year) {
        List<CommonStaVO> orders = new ArrayList<>();
        Integer iYear = Integer.valueOf(year);
        //查询所有分区
        List<KanbanRJDEleYearRO> areaCodes = projectStaSubareaMonthMapper.getEleSubareaCodes();
        String kpi = "area.electricity.energyUsage.total";

        Map<String, ProjectStaSubareaYearVO> yearStaMap = areaCodes.stream().map(s -> getYearStaSubarea(year, kpi, s.getSubareaCode(), s.getName(), ProjectConstant.BIZ_PROJECT_ID_JJGJ)).collect(Collectors.toList()).stream().collect(Collectors.toMap(ProjectStaSubareaYearVO::getSubareaCode, t -> t));
        Map<String, ProjectStaSubareaYearVO> lastYearStaMap = areaCodes.stream().map(s -> getYearStaSubarea(String.valueOf(iYear - 1), kpi, s.getSubareaCode(), s.getName(), ProjectConstant.BIZ_PROJECT_ID_JJGJ)).collect(Collectors.toList()).stream().collect(Collectors.toMap(ProjectStaSubareaYearVO::getSubareaCode, t -> t));

        //当年碳排
        CommonStaVO currentYearVo = new CommonStaVO("当年碳排",
                areaCodes.stream().map(KanbanRJDEleYearRO::getName).collect(Collectors.toList()),
                areaCodes.stream().map(new Function<KanbanRJDEleYearRO, String>() {
                    @Override
                    public String apply(KanbanRJDEleYearRO yearRo) {
                        return NumberUtil.mul(yearStaMap.get(yearRo.getSubareaCode()).getStaValue(), BigDecimal.valueOf(0.00042)).toPlainString();
                    }
                }).collect(Collectors.toList())

        );
        //上年同比
        CommonStaVO lastYearVo = new CommonStaVO("当年碳排",
                areaCodes.stream().map(KanbanRJDEleYearRO::getName).collect(Collectors.toList()),
                areaCodes.stream().map(new Function<KanbanRJDEleYearRO, String>() {
                    @Override
                    public String apply(KanbanRJDEleYearRO yearRo) {
                        BigDecimal current = NumberUtil.mul(yearStaMap.get(yearRo.getSubareaCode()).getStaValue(), BigDecimal.valueOf(0.00042));
                        BigDecimal last = NumberUtil.mul(lastYearStaMap.get(yearRo.getSubareaCode()).getStaValue(), BigDecimal.valueOf(0.00042));
                        return KpiUtils.getYoy(current, last);
//                        return NumberUtil.mul(lastYearStaMap.get(yearRo.getSubareaCode()).getStaValue(), BigDecimal.valueOf(0.00042)).toPlainString();
                    }
                }).collect(Collectors.toList())

        );

        orders.add(currentYearVo);
        orders.add(lastYearVo);


        final int[] index = {0};
        List<Integer> orderList = orders.get(0).getYlist().stream().map(s -> {
            CommonStaVODetail detail = new CommonStaVODetail(null, s, index[0]);
            index[0]++;
            return detail;
        }).sorted((o1, o2) -> new BigDecimal(o1.getYValue()).compareTo(new BigDecimal(o2.getYValue()))).map(CommonStaVODetail::getOrder).collect(Collectors.toList());
        orders.get(0).setYlist(orderList.stream().map(i -> orders.get(0).getYlist().get(i)).collect(Collectors.toList()));
        orders.get(0).setXlist(orderList.stream().map(i -> orders.get(0).getXlist().get(i)).collect(Collectors.toList()));
        orders.get(1).setYlist(orderList.stream().map(i -> orders.get(1).getYlist().get(i)).collect(Collectors.toList()));
        orders.get(1).setXlist(orderList.stream().map(i -> orders.get(1).getXlist().get(i)).collect(Collectors.toList()));

        return orders;
    }

    @Override
    public List<KanbanRJDAlarmVO> getJJAlarm() {
        Response<List<AlarmResponse>> response = monitorApi.getAlarms();
        return response.getResult().stream().map(alarmResponse -> new KanbanRJDAlarmVO(DateUtils.fmt2Str(alarmResponse.getTime(), DateUtils.SD_DT_FMT_HOUR), alarmResponse.getAlarmType(), alarmResponse.getAlarmDesc(), alarmResponse.getAlarmStatus())).collect(Collectors.toList());
    }

    @Override
    public ScreenProjectBasicVO getBasic(String bizProjectId) {
        TenantContext.setIgnore(true);
        return new ScreenProjectBasicVO(
                deviceMonitorMapper.getJJGJBasic(bizProjectId, "空调"),
                deviceMonitorMapper.getJJGJBasic(bizProjectId, "电表"),
                deviceMonitorMapper.getJJGJBasic(bizProjectId, "水表"),
                deviceMonitorMapper.getJJGJBasic(bizProjectId, "气表")
        );
    }

    @Override
    public ScreenProjectKeyKpiVO getProjectKeyKpi(String bizProjectIdJjgj) {
        ScreenProjectKeyKpiVO kpiVO = new ScreenProjectKeyKpiVO();
        //

        SubitemIndexEnum[] kpis = new SubitemIndexEnum[]{
                // 总用电量
                SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,
                // 用水量
                SubitemIndexEnum.WATER_USAGE_TOTAL,
                // 总碳排
                SubitemIndexEnum.CARBON_DIOXIDE_EMISSIONS,
                // 总用气
                SubitemIndexEnum.GAS_USAGE_TOTAL,
                // 电碳排
                SubitemIndexEnum.CARBON_ELECTRICITY_USAGE_CO2_TOTAL,
                // 气碳排
                SubitemIndexEnum.CARBON_GAS_USAGE_CO2_TOTAL,

        };
        LocalDateTime now = LocalDateTime.now();
        YearMonth yearMonth = YearMonth.of(Integer.valueOf(now.getYear()), now.getMonthValue());
        IndexCumulativeMonthRequest indexCumulativeMonthRequest = new IndexCumulativeMonthRequest();
        indexCumulativeMonthRequest.setMonth(yearMonth);
        indexCumulativeMonthRequest.setProjectBizId(ProjectConstant.BIZ_PROJECT_ID_JJGJ);
        indexCumulativeMonthRequest.setIndices(kpis);
        Response<Map<SubitemIndexEnum, BigDecimal>> searchDataByMonthCumulative = subitemApi.searchDataByMonthCumulative(indexCumulativeMonthRequest);
        Map<SubitemIndexEnum, BigDecimal> cumulativeMonth = searchDataByMonthCumulative.getResult();
        kpiVO.setEleUsage(getValue(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS, cumulativeMonth));
        kpiVO.setCarbonUsage(NumberUtil.add(getValue(SubitemIndexEnum.CARBON_ELECTRICITY_USAGE_CO2_TOTAL, cumulativeMonth), getValue(SubitemIndexEnum.CARBON_GAS_USAGE_CO2_TOTAL, cumulativeMonth)));
        kpiVO.setGasUsage(getValue(SubitemIndexEnum.GAS_USAGE_TOTAL, cumulativeMonth));
        kpiVO.setWaterUsage(getValue(SubitemIndexEnum.WATER_USAGE_TOTAL, cumulativeMonth));

        //计划值
        String staYear = String.valueOf(LocalDateTime.now().getYear());
        //计划用电
        BigDecimal elePlan = projectPlannedElectricityMapper.selectList(new LambdaQueryWrapper<ProjectPlannedElectricityEntity>()
                .eq(ProjectPlannedElectricityEntity::getYear, staYear)
                .eq(ProjectPlannedElectricityEntity::getProjectBizId, ProjectConstant.BIZ_PROJECT_ID_JJGJ)
        ).stream().map(ProjectPlannedElectricityEntity::getPlanElectricityConsumption).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

        //计划用气
        BigDecimal gasPlan = projectPlannedGasMapper.selectList(new LambdaQueryWrapper<ProjectPlannedGasEntity>()
                .eq(ProjectPlannedGasEntity::getYear, staYear)
                .eq(ProjectPlannedGasEntity::getProjectBizId, ProjectConstant.BIZ_PROJECT_ID_JJGJ)
        ).stream().map(ProjectPlannedGasEntity::getPlanGasConsumption).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

        //计划用水
        BigDecimal waterPlan = projectPlannedWaterMapper.selectList(new LambdaQueryWrapper<ProjectPlannedWaterEntity>()
                .eq(ProjectPlannedWaterEntity::getYear, staYear)
                .eq(ProjectPlannedWaterEntity::getProjectBizId, ProjectConstant.BIZ_PROJECT_ID_JJGJ)
        ).stream().map(ProjectPlannedWaterEntity::getPlanWaterConsumption).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal carbonPlan = NumberUtil.add(elePlan.multiply(new BigDecimal("0.00042")), waterPlan.multiply(new BigDecimal("0.00185")));

        kpiVO.setEleUsageRatio(elePlan.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : NumberUtil.div(kpiVO.getEleUsage(), elePlan, 2, RoundingMode.HALF_UP));
        kpiVO.setWaterUsageRatio(waterPlan.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : NumberUtil.div(kpiVO.getWaterUsage(), waterPlan, 2, RoundingMode.HALF_UP));
        kpiVO.setGasUsageRatio(gasPlan.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : NumberUtil.div(kpiVO.getGasUsage(), gasPlan, 2, RoundingMode.HALF_UP));
        kpiVO.setCarbonUsageRatio(carbonPlan.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : NumberUtil.div(kpiVO.getCarbonUsage(), carbonPlan, 2, RoundingMode.HALF_UP));
        return kpiVO;
    }

    public ProjectStaSubitemYearVO getCumulative(YearMonth ym, String bizProjectId) {
        SubitemIndexEnum[] kpis = new SubitemIndexEnum[]{
                SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,
                SubitemIndexEnum.WATER_USAGE_TOTAL,
                SubitemIndexEnum.GAS_USAGE_TOTAL,
                SubitemIndexEnum.CARBON_DIOXIDE_EMISSIONS,
                SubitemIndexEnum.ELECTRICITY_ENERGY_USAGE_FEE_TOTAL,
                SubitemIndexEnum.WATER_FEE_TOTAL,
                SubitemIndexEnum.GAS_FEE_TOTAL,
        };
        //当年累计
        IndexCumulativeMonthRequest indexCumulativeMonthRequest = new IndexCumulativeMonthRequest();
        indexCumulativeMonthRequest.setMonth(ym);
        indexCumulativeMonthRequest.setProjectBizId(ProjectConstant.BIZ_PROJECT_ID_JJGJ);
        indexCumulativeMonthRequest.setIndices(kpis);
        Response<Map<SubitemIndexEnum, BigDecimal>> searchDataByMonthCumulative = subitemApi.searchDataByMonthCumulative(indexCumulativeMonthRequest);
        Map<SubitemIndexEnum, BigDecimal> cumulativeMonth = searchDataByMonthCumulative.getResult();

        ProjectStaSubitemYearVO cumulData = new ProjectStaSubitemYearVO();
        cumulData.setProjectElectricityEnergyusageTotal(getValue(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS, cumulativeMonth));
        cumulData.setProjectGasUsageTotal(getValue(SubitemIndexEnum.GAS_USAGE_TOTAL, cumulativeMonth));
        cumulData.setProjectWaterUsageTotal(getValue(SubitemIndexEnum.WATER_USAGE_TOTAL, cumulativeMonth));
        cumulData.setProjectCarbonTotalco2Total(getValue(SubitemIndexEnum.CARBON_DIOXIDE_EMISSIONS, cumulativeMonth));
        cumulData.setProjectElectricityEnergyusagefeeTotal(getValue(SubitemIndexEnum.ELECTRICITY_ENERGY_USAGE_FEE_TOTAL, cumulativeMonth));
        cumulData.setProjectWaterFeeTotal(getValue(SubitemIndexEnum.WATER_FEE_TOTAL, cumulativeMonth));
        cumulData.setProjectGasFeeTotal(getValue(SubitemIndexEnum.GAS_FEE_TOTAL, cumulativeMonth));
        return cumulData;
    }
}
