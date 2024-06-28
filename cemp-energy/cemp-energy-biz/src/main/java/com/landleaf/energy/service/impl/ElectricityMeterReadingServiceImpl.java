package com.landleaf.energy.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.api.UserProjectApi;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.constance.DeviceStaCategoryEnum;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.energy.dal.mapper.*;
import com.landleaf.energy.domain.dto.MeterImportDTO;
import com.landleaf.energy.domain.dto.MeterImportDataDTO;
import com.landleaf.energy.domain.entity.ProjectStaDeviceElectricityDayEntity;
import com.landleaf.energy.domain.entity.ProjectStaDeviceElectricityHourEntity;
import com.landleaf.energy.domain.entity.ProjectStaDeviceElectricityMonthEntity;
import com.landleaf.energy.domain.enums.MeterReadCycleEnum;
import com.landleaf.energy.domain.request.ElectricityDayQueryRequest;
import com.landleaf.energy.domain.request.ElectricityHourQueryRequest;
import com.landleaf.energy.domain.request.ElectricityMeterReadingRequest;
import com.landleaf.energy.domain.request.ElectricityMonthQueryRequest;
import com.landleaf.energy.domain.response.DeviceElectricityTabulationResponse;
import com.landleaf.energy.domain.response.ElectricityMeterReadingTreeResponse;
import com.landleaf.energy.service.ElectricityMeterReadingService;
import com.landleaf.energy.util.MeterImportCheckUtil;
import com.landleaf.monitor.api.DeviceParameterApi;
import com.landleaf.monitor.api.MonitorApi;
import com.landleaf.monitor.dto.DeviceMonitorVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.landleaf.energy.domain.enums.ErrorCodeConstants.PARAMETER_EXCEPTIONS;
import static com.landleaf.energy.domain.enums.ErrorCodeConstants.RECORD_NOT_EXIST;

/**
 * 电表抄表业务
 *
 * @author Tycoon
 * @since 2023/8/17 10:13
 **/
@Service
@RequiredArgsConstructor
public class ElectricityMeterReadingServiceImpl implements ElectricityMeterReadingService {

    private final ProjectManualDeviceElectricityDayMapper projectManualDeviceElectricityDayMapper;
    private final ProjectManualDeviceElectricityMonthMapper projectManualDeviceElectricityMonthMapper;
    private final ProjectStaDeviceElectricityDayMapper projectStaDeviceElectricityDayMapper;
    private final ProjectStaDeviceElectricityMonthMapper projectStaDeviceElectricityMonthMapper;
    private final ProjectStaDeviceElectricityHourMapper projectStaDeviceElectricityHourMapper;
    private final DeviceMonitorMapper deviceMonitorMapper;
    private final DeviceParameterApi deviceParameterApi;
    private final MonitorApi monitorApi;
    private final UserProjectApi userProjectApi;
    private final MeterImportCheckUtil meterImportCheckUtil;

    @Override
    public Page<DeviceElectricityTabulationResponse> searchManualElectricityTabulation(ElectricityDayQueryRequest request) {
        Assert.isTrue(CollUtil.isNotEmpty(request.getDeviceBizIds()), () -> new ServiceException(PARAMETER_EXCEPTIONS));
        //Page<DeviceElectricityTabulationResponse> response = projectManualDeviceElectricityDayMapper.searchPageData(Page.of(request.getPageNo(), request.getPageSize()), request);
        return projectStaDeviceElectricityDayMapper.searchPageData(Page.of(request.getPageNo(), request.getPageSize()), request);
//        //期初 和 期末值
//        List<String> ids = response.getRecords().stream().map(DeviceElectricityTabulationResponse::getBizDeviceId).collect(Collectors.toList());
//        if(ids != null && ids.size()>0){
//
//            Map<String,ProjectStaDeviceElectricityDayEntity> map = projectStaDeviceElectricityDayMapper.selectList(new LambdaQueryWrapper<ProjectStaDeviceElectricityDayEntity>()
//                    .in(ProjectStaDeviceElectricityDayEntity::getBizDeviceId,ids)
//                    .ge(ProjectStaDeviceElectricityDayEntity::getStaTime,request.getStartData())
//                    .le(ProjectStaDeviceElectricityDayEntity::getStaTime,request.getEndData())
//            ).stream().collect(Collectors.toMap(entity -> {
//                return entity.getBizDeviceId().concat("_").concat(DateUtils.fmt2Str(entity.getStaTime(),DateUtils.SD_DT_FMT_DAY));
//            }, t -> t));
//            response.setRecords(response.getRecords().stream().map(res -> {
//                String key = res.getBizDeviceId().concat("_").concat(String.format("%s-%s-%s",res.getYear(),String.format("%02d", Integer.valueOf(res.getMonth())),String.format("%02d", Integer.valueOf(res.getDay()))));
//                if(map.get(key) != null){
//                    res.setOpenDisplaysValue(map.get(key).getEnergymeterEpimportStart());
//                    res.setCloseDisplaysValue(map.get(key).getEnergymeterEpimportEnd());
//                    res.setActiveTotal(map.get(key).getEnergymeterEpimportTotal());
//                }
//                return res;
//            }).collect(Collectors.toList()));
//        }
//        return response;
    }

    @Override
    public Page<DeviceElectricityTabulationResponse> searchManualElectricityTabulation(ElectricityMonthQueryRequest request) {
        Assert.isTrue(CollUtil.isNotEmpty(request.getDeviceBizIds()), () -> new ServiceException(PARAMETER_EXCEPTIONS));
        //return projectManualDeviceElectricityMonthMapper.searchPageData(Page.of(request.getPageNo(), request.getPageSize()), request);
        return projectStaDeviceElectricityMonthMapper.searchPageData(Page.of(request.getPageNo(), request.getPageSize()), request);
    }

    @Override
    public Page<DeviceElectricityTabulationResponse> searchManualElectricityTabulation(ElectricityHourQueryRequest request) {
        Assert.isTrue(CollUtil.isNotEmpty(request.getBizDeviceIds()), () -> new ServiceException(PARAMETER_EXCEPTIONS));
        //return projectManualDeviceElectricityMonthMapper.searchPageData(Page.of(request.getPageNo(), request.getPageSize()), request);
        return projectStaDeviceElectricityHourMapper.searchPageData(Page.of(request.getPageNo(), request.getPageSize()), request);
    }

    @Override
    public Page<DeviceElectricityTabulationResponse> searchStaElectricityTabulation(ElectricityDayQueryRequest request) {
        Assert.isTrue(CollUtil.isNotEmpty(request.getDeviceBizIds()), () -> new ServiceException(PARAMETER_EXCEPTIONS));
        return projectStaDeviceElectricityDayMapper.searchPageData(Page.of(request.getPageNo(), request.getPageSize()), request);
    }

    @Override
    public Page<DeviceElectricityTabulationResponse> searchStaElectricityTabulation(ElectricityMonthQueryRequest request) {
        Assert.isTrue(CollUtil.isNotEmpty(request.getDeviceBizIds()), () -> new ServiceException(PARAMETER_EXCEPTIONS));
        return projectStaDeviceElectricityMonthMapper.searchPageData(Page.of(request.getPageNo(), request.getPageSize()), request);
    }

    @Override
    public Page<DeviceElectricityTabulationResponse> searchStaElectricityTabulation(ElectricityHourQueryRequest request) {
        Assert.isTrue(CollUtil.isNotEmpty(request.getBizDeviceIds()), () -> new ServiceException(PARAMETER_EXCEPTIONS));
        return projectStaDeviceElectricityHourMapper.searchPageData(Page.of(request.getPageNo(), request.getPageSize()), request);
    }

    @Override
    public String searchMultiplyingFactor(String bizDeviceId) {
        Response<String> response = deviceParameterApi.searchDeviceParameterValue(bizDeviceId, "multiplyingFactor");
        Assert.isTrue(response.isSuccess(), () -> new ServiceException(response.getErrorCode(), response.getMessage()));
        return response.getResult();
    }

    @Override
    public BigDecimal searchHourOpenDisplaysValue(String bizDeviceId, String time) {
        TenantContext.setIgnore(true);
        LocalDateTime lastHour = LocalDateTimeUtil.parse(time, "yyyy-MM-dd HH").minusHours(1L);
        ProjectStaDeviceElectricityHourEntity record = projectStaDeviceElectricityHourMapper.selectOne(new LambdaQueryWrapper<ProjectStaDeviceElectricityHourEntity>()
                .eq(ProjectStaDeviceElectricityHourEntity::getBizDeviceId, bizDeviceId)
                .eq(ProjectStaDeviceElectricityHourEntity::getStaTime, Timestamp.valueOf(lastHour)));
        if (null != record) {
            return record.getEnergymeterEpimportEnd();
        }
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal searchOpenDisplaysValue(String bizDeviceId, LocalDate time) {
        ProjectStaDeviceElectricityDayEntity deviceSta = projectStaDeviceElectricityDayMapper.selectOne(new LambdaQueryWrapper<ProjectStaDeviceElectricityDayEntity>()
                .eq(ProjectStaDeviceElectricityDayEntity::getBizDeviceId, bizDeviceId)
                .lt(ProjectStaDeviceElectricityDayEntity::getStaTime, time)
                .orderByDesc(ProjectStaDeviceElectricityDayEntity::getStaTime).last("limit 1")
        );
        return Optional.ofNullable(deviceSta).map(ProjectStaDeviceElectricityDayEntity::getEnergymeterEpimportEnd).orElse(null);
//        return Optional.ofNullable(projectManualDeviceElectricityDayMapper.searchProjectTime(bizDeviceId, time.minusDays(1)))
//                .map(ProjectManualDeviceElectricityDayEntity::getCloseDisplaysValue)
//                .orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal searchCloseDisplaysValue(String bizDeviceId, YearMonth time) {
        ProjectStaDeviceElectricityMonthEntity deviceSta = projectStaDeviceElectricityMonthMapper.selectOne(new LambdaQueryWrapper<ProjectStaDeviceElectricityMonthEntity>()
                .eq(ProjectStaDeviceElectricityMonthEntity::getBizDeviceId, bizDeviceId)
                .lt(ProjectStaDeviceElectricityMonthEntity::getStaTime, time.toString().concat("-01"))
                .orderByDesc(ProjectStaDeviceElectricityMonthEntity::getStaTime).last("limit 1")
        );
        return Optional.ofNullable(deviceSta).map(ProjectStaDeviceElectricityMonthEntity::getEnergymeterEpimportEnd).orElse(null);
//        return Optional.ofNullable(projectManualDeviceElectricityMonthMapper.searchProjectTime(bizDeviceId, time.minusMonths(1)))
//                .map(ProjectManualDeviceElectricityMonthEntity::getCloseDisplaysValue)
//                .orElse(BigDecimal.ZERO);
    }

    @Override
    public void addManualElectricityHour(ElectricityMeterReadingRequest.HourCreate request) {
        Long tenantId = TenantContext.getTenantId();
        if (null == tenantId) {
            throw new BusinessException("获取当前租户信息异常");
        }
        TenantContext.setIgnore(true);
        LocalDateTime staTime = LocalDateTimeUtil.parse(request.getTime(), "yyyy-MM-dd HH");
        ProjectStaDeviceElectricityHourEntity deviceSta = projectStaDeviceElectricityHourMapper.selectOne(
                new LambdaQueryWrapper<ProjectStaDeviceElectricityHourEntity>()
                        .eq(ProjectStaDeviceElectricityHourEntity::getBizDeviceId, request.getBizDeviceId())
                        .eq(ProjectStaDeviceElectricityHourEntity::getStaTime, Timestamp.valueOf(staTime))
        );
        if (deviceSta == null) {
            deviceSta = projectStaDeviceElectricityHourMapper.getManualInsertData(request.getBizDeviceId());
            deviceSta.setEnergymeterEpimportStart(request.getOpenDisplaysValue());
            deviceSta.setEnergymeterEpimportEnd(request.getCloseDisplaysValue());
            deviceSta.setEnergymeterEpimportTotal(request.getActiveTotal());
            deviceSta.setYear(String.valueOf(staTime.getYear()));
            deviceSta.setMonth(String.valueOf(staTime.getMonthValue()));
            deviceSta.setDay(String.valueOf(staTime.getDayOfMonth()));
            deviceSta.setHour(String.valueOf(staTime.getHour()));
            deviceSta.setStaTime(Timestamp.valueOf(staTime));
            deviceSta.setRemark(request.getRemark());
            deviceSta.setManualFlag(1);
            projectStaDeviceElectricityHourMapper.insert(deviceSta);
        } else {
            deviceSta.setEnergymeterEpimportStart(request.getOpenDisplaysValue());
            deviceSta.setEnergymeterEpimportEnd(request.getCloseDisplaysValue());
            deviceSta.setEnergymeterEpimportTotal(request.getActiveTotal());
            deviceSta.setRemark(request.getRemark());
            deviceSta.setManualFlag(1);
            projectStaDeviceElectricityHourMapper.updateById(deviceSta);
        }
    }

    @Override
    public void addManualElectricityDay(ElectricityMeterReadingRequest.DayCreate request) {
        ProjectStaDeviceElectricityDayEntity deviceSta = projectStaDeviceElectricityDayMapper.selectOne(
                new LambdaQueryWrapper<ProjectStaDeviceElectricityDayEntity>()
                        .eq(ProjectStaDeviceElectricityDayEntity::getBizDeviceId, request.getBizDeviceId())
                        .eq(ProjectStaDeviceElectricityDayEntity::getStaTime, request.getTime())
        );
        if (deviceSta == null) {
            TenantContext.setIgnore(true);
            ProjectStaDeviceElectricityDayEntity insert = projectStaDeviceElectricityDayMapper.getDeviceByBizid(request.getBizDeviceId(), TenantContext.getTenantId());
            TenantContext.setIgnore(false);
            insert.setEnergymeterEpimportStart(request.getOpenDisplaysValue());
            insert.setEnergymeterEpimportEnd(request.getCloseDisplaysValue());
            insert.setEnergymeterEpimportTotal(request.getActiveTotal());
            insert.setYear(String.valueOf(request.getTime().getYear()));
            insert.setMonth(String.valueOf(request.getTime().getMonthValue()));
            insert.setDay(String.valueOf(request.getTime().getDayOfMonth()));
            insert.setStaTime(Timestamp.valueOf(LocalDateTime.of(request.getTime().getYear(), request.getTime().getMonthValue(), request.getTime().getDayOfMonth(), 0, 0, 0)));
            insert.setRemark(request.getRemark());
            insert.setManualFlag(1);
            projectStaDeviceElectricityDayMapper.insert(insert);
        } else {
            deviceSta.setEnergymeterEpimportStart(request.getOpenDisplaysValue());
            deviceSta.setEnergymeterEpimportEnd(request.getCloseDisplaysValue());
            deviceSta.setEnergymeterEpimportTotal(request.getActiveTotal());
            deviceSta.setRemark(request.getRemark());
            deviceSta.setManualFlag(1);
            projectStaDeviceElectricityDayMapper.updateById(deviceSta);
        }
    }

    @Override
    public void addManualElectricityMonth(ElectricityMeterReadingRequest.MonthCreate request) {
        ProjectStaDeviceElectricityMonthEntity deviceSta = projectStaDeviceElectricityMonthMapper.selectOne(
                new LambdaQueryWrapper<ProjectStaDeviceElectricityMonthEntity>()
                        .eq(ProjectStaDeviceElectricityMonthEntity::getBizDeviceId, request.getBizDeviceId())
                        .eq(ProjectStaDeviceElectricityMonthEntity::getStaTime, request.getTime().toString().concat("-01 00:00:00"))
        );
        if (deviceSta == null) {
            TenantContext.setIgnore(true);
            ProjectStaDeviceElectricityMonthEntity insert = projectStaDeviceElectricityMonthMapper.getDeviceByBizid(request.getBizDeviceId(), TenantContext.getTenantId());
            TenantContext.setIgnore(false);
            insert.setEnergymeterEpimportStart(request.getOpenDisplaysValue());
            insert.setEnergymeterEpimportEnd(request.getCloseDisplaysValue());
            //insert.setEnergymeterEpimportTotal(NumberUtil.sub(insert.getEnergymeterEpimportEnd(),insert.getEnergymeterEpimportStart()));
            insert.setEnergymeterEpimportTotal(request.getActiveTotal());
            insert.setYear(String.valueOf(request.getTime().getYear()));
            insert.setMonth(String.valueOf(request.getTime().getMonthValue()));
            insert.setStaTime(Timestamp.valueOf(LocalDateTime.of(request.getTime().getYear(), request.getTime().getMonthValue(), 1, 0, 0, 0)));
            insert.setRemark(request.getRemark());
            insert.setManualFlag(1);
            projectStaDeviceElectricityMonthMapper.insert(insert);
        } else {
            deviceSta.setEnergymeterEpimportStart(request.getOpenDisplaysValue());
            deviceSta.setEnergymeterEpimportEnd(request.getCloseDisplaysValue());
            deviceSta.setEnergymeterEpimportTotal(request.getActiveTotal());
            deviceSta.setRemark(request.getRemark());
            deviceSta.setManualFlag(1);
            projectStaDeviceElectricityMonthMapper.updateById(deviceSta);
        }


    }

    @Override
    public void deleteManualElectricityDay(Long id) {
        Assert.notNull(projectManualDeviceElectricityDayMapper.selectById(id), () -> new ServiceException(RECORD_NOT_EXIST));
        projectManualDeviceElectricityDayMapper.deleteById(id);
    }

    @Override
    public void deleteManualElectricityMonth(Long id) {
        Assert.notNull(projectManualDeviceElectricityMonthMapper.selectById(id), () -> new ServiceException(RECORD_NOT_EXIST));
        projectManualDeviceElectricityMonthMapper.deleteById(id);
    }

    @Override
    public void updateElectricityMeterReading(ElectricityMeterReadingRequest.Update request) {
        // 更新到统计表
        if (request.getType() == 1 || request.getType() == 3) {
            var entity = projectStaDeviceElectricityDayMapper.selectOne(
                    new LambdaQueryWrapper<ProjectStaDeviceElectricityDayEntity>()
                            .eq(ProjectStaDeviceElectricityDayEntity::getBizDeviceId, request.getBizDeviceId())
                            .eq(ProjectStaDeviceElectricityDayEntity::getStaTime, request.getTime())
            );
            //var entity = projectStaDeviceElectricityDayMapper.selectById(request.getId());
            Assert.notNull(entity, () -> new ServiceException(RECORD_NOT_EXIST));
            entity.setEnergymeterEpimportStart(request.getOpenDisplaysValue());
            entity.setEnergymeterEpimportEnd(request.getCloseDisplaysValue());
            entity.setEnergymeterEpimportTotal(request.getActiveTotal());
            entity.setRemark(request.getRemark());
            // 维护电表手工记录标识
            entity.setManualFlag(1);
            projectStaDeviceElectricityDayMapper.updateById(entity);
        } else if (request.getType() == 2 || request.getType() == 4) {
            var entity = projectStaDeviceElectricityMonthMapper.selectOne(
                    new LambdaQueryWrapper<ProjectStaDeviceElectricityMonthEntity>()
                            .eq(ProjectStaDeviceElectricityMonthEntity::getBizDeviceId, request.getBizDeviceId())
                            .eq(ProjectStaDeviceElectricityMonthEntity::getStaTime, request.getTime().toString().concat("-01 00:00:00"))
            );
            //var entity = projectStaDeviceElectricityMonthMapper.selectById(request.getId());
            Assert.notNull(entity, () -> new ServiceException(RECORD_NOT_EXIST));
            entity.setEnergymeterEpimportStart(request.getOpenDisplaysValue());
            entity.setEnergymeterEpimportEnd(request.getCloseDisplaysValue());
            entity.setEnergymeterEpimportTotal(request.getActiveTotal());
            entity.setRemark(request.getRemark());
            // 维护电表手工记录标识
            entity.setManualFlag(1);
            projectStaDeviceElectricityMonthMapper.updateById(entity);
        } else {
            var entity = projectStaDeviceElectricityHourMapper.selectOne(
                    new LambdaQueryWrapper<ProjectStaDeviceElectricityHourEntity>()
                            .eq(ProjectStaDeviceElectricityHourEntity::getBizDeviceId, request.getBizDeviceId())
                            .eq(ProjectStaDeviceElectricityHourEntity::getStaTime, request.getTime().concat(":00:00"))
            );
            Assert.notNull(entity, () -> new ServiceException(RECORD_NOT_EXIST));
            entity.setEnergymeterEpimportStart(request.getOpenDisplaysValue());
            entity.setEnergymeterEpimportEnd(request.getCloseDisplaysValue());
            entity.setEnergymeterEpimportTotal(request.getActiveTotal());
            entity.setRemark(request.getRemark());
            // 维护电表手工记录标识
            entity.setManualFlag(1);
            projectStaDeviceElectricityHourMapper.updateById(entity);
        }

        //
    }

    @Override
    public List<ElectricityMeterReadingTreeResponse> searchElectricityTree(Integer value) {
        Response<List<String>> response;
        Long userId = LoginUserUtil.getLoginUserId();
        if (value == 1) {
            response = deviceParameterApi.searchDeviceBizIds(TenantContext.getTenantId(), "02", "1", userId);
        } else if (value == 2) {
            response = deviceParameterApi.searchDeviceBizIds(TenantContext.getTenantId(), "02", "2", userId);
        } else if (value == 3) {
            response = deviceParameterApi.searchDeviceBizIds(TenantContext.getTenantId(), "01", "1", userId);
        } else if (value == 4) {
            response = deviceParameterApi.searchDeviceBizIds(TenantContext.getTenantId(), "01", "2", userId);
        } else if (value == 5) {
            response = deviceParameterApi.searchDeviceBizIds(TenantContext.getTenantId(), "02", "3", userId);
        } else if (value == 6) {
            response = deviceParameterApi.searchDeviceBizIds(TenantContext.getTenantId(), "01", "3", userId);
        } else {
            throw new ServiceException("1", "该操作不存在");
        }
        Assert.isTrue(response.isSuccess(), "获取电表失败");
        List<String> result = response.getResult();
        if (result.isEmpty()) {
            return List.of();
        }
        // iot的设备会少于monitor的设备 应该是从monitor查会靠谱点。。。也是小改
        List<DeviceMonitorVO> deviceResult = monitorApi.getDeviceListByBizIds(result).getCheckedData();

        Map<String, List<DeviceMonitorVO>> collect = deviceResult.stream()
                .collect(Collectors.groupingBy(DeviceMonitorVO::getBizProjectId));
        Map<String, String> projectNameMap = deviceResult.stream()
                .collect(Collectors.toMap(DeviceMonitorVO::getBizProjectId, DeviceMonitorVO::getProjectName, (v1, v2) -> v1));

        return projectNameMap
                .entrySet()
                .stream()
                .map(it -> {
                    ElectricityMeterReadingTreeResponse treeNode = new ElectricityMeterReadingTreeResponse();
                    treeNode.setId(it.getKey());
                    treeNode.setName(it.getValue());
                    return treeNode;
                })
                .distinct()
                .map(it -> {
                    List<ElectricityMeterReadingTreeResponse.Electricity> electrifies = collect.getOrDefault(it.getId(), List.of())
                            .stream()
                            .map(i -> {
                                ElectricityMeterReadingTreeResponse.Electricity electricity = new ElectricityMeterReadingTreeResponse.Electricity();
                                electricity.setId(i.getBizDeviceId());
                                electricity.setName(i.getName());
                                return electricity;
                            }).toList();
                    it.setChildren(electrifies);
                    return it;
                }).toList();
    }

    @Override
    public MeterImportDTO excelImportCheck(MultipartFile file) {
        MeterImportDTO dto = meterImportCheckUtil.getImportDTO(file, DeviceStaCategoryEnum.DB3PH.getCode());
        if (!dto.getErrMsg().isEmpty()) {
            return dto;
        }
        Map<String, BigDecimal> startMap = null;
        if (MeterReadCycleEnum.day.equals(dto.getCycle())) {
            LocalDate lastDay = dto.getDayTime().minusDays(1L);
            startMap = projectStaDeviceElectricityDayMapper.selectList(new LambdaQueryWrapper<ProjectStaDeviceElectricityDayEntity>()
                            .in(ProjectStaDeviceElectricityDayEntity::getBizDeviceId, dto.getBizDeviceIdList())
                            .eq(ProjectStaDeviceElectricityDayEntity::getStaTime, Timestamp.valueOf(lastDay.atStartOfDay())))
                    .stream()
                    .collect(HashMap::new, (k, v) -> k.put(v.getBizDeviceId(), v.getEnergymeterEpimportEnd()), HashMap::putAll);

        }
        if (MeterReadCycleEnum.month.equals(dto.getCycle())) {
            YearMonth lastMonth = dto.getMonthTime().minusMonths(1L);
            startMap = projectStaDeviceElectricityMonthMapper.selectList(new LambdaQueryWrapper<ProjectStaDeviceElectricityMonthEntity>()
                            .in(ProjectStaDeviceElectricityMonthEntity::getBizDeviceId, dto.getBizDeviceIdList())
                            .eq(ProjectStaDeviceElectricityMonthEntity::getStaTime, Timestamp.valueOf(lastMonth.atDay(1).atStartOfDay())))
                    .stream()
                    .collect(HashMap::new, (k, v) -> k.put(v.getBizDeviceId(), v.getEnergymeterEpimportEnd()), HashMap::putAll);
        }
        boolean flag = MapUtil.isNotEmpty(startMap);

        Map<String, BigDecimal> deviceMultiply = deviceParameterApi.getDevicesMultiplyingFactor(dto.getBizDeviceIdList()).getCheckedData();
        for (MeterImportDataDTO data : dto.getDataList()) {
            if (null == data.getStart() && flag) {
                data.setStart(startMap.get(data.getBizDeviceId()));
            }
            if (null == data.getTotal()) {
                if (null == data.getStart()) {
                    dto.getErrMsg().add("设备" + data.getDeviceName() + "缺少期初值计算本期用量");
                } else {
                    data.setTotal(data.getEnd().subtract(data.getStart()).multiply(deviceMultiply.getOrDefault(data.getBizDeviceId(), BigDecimal.ONE)));
                }
            }
            if (null != data.getTotal() && data.getTotal().compareTo(BigDecimal.ZERO) < 0) {
                dto.getErrMsg().add("设备" + data.getDeviceName() + "本期用量结果不能为负");
            }
        }
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void excelImportSave(MeterImportDTO dto) {
        if (MeterReadCycleEnum.day.equals(dto.getCycle())) {
            LocalDate dayTime = dto.getDayTime();
            Map<String, ProjectStaDeviceElectricityDayEntity> oldMap = projectStaDeviceElectricityDayMapper.selectList(
                    new LambdaQueryWrapper<ProjectStaDeviceElectricityDayEntity>()
                            .in(ProjectStaDeviceElectricityDayEntity::getBizDeviceId, dto.getBizDeviceIdList())
                            .eq(ProjectStaDeviceElectricityDayEntity::getStaTime, Timestamp.valueOf(dayTime.atStartOfDay()))
            ).stream().collect(Collectors.toMap(ProjectStaDeviceElectricityDayEntity::getBizDeviceId, o -> o, (o1, o2) -> o1));
            for (MeterImportDataDTO data : dto.getDataList()) {
                ProjectStaDeviceElectricityDayEntity entity = oldMap.get(data.getBizDeviceId());
                if (null != entity) {
                    entity.setEnergymeterEpimportTotal(data.getTotal())
                            .setEnergymeterEpimportStart(data.getStart())
                            .setEnergymeterEpimportEnd(data.getEnd())
                            .setManualFlag(1);
                    projectStaDeviceElectricityDayMapper.updateById(entity);
                } else {
                    TenantContext.setIgnore(true);
                    entity = projectStaDeviceElectricityDayMapper.getManualInsertData(data.getBizDeviceId());
                    if (null == entity) {
                        throw new BusinessException("获取设备信息异常，保存失败");
                    }
                    entity.setYear(String.valueOf(dto.getDayTime().getYear()))
                            .setMonth(String.valueOf(dayTime.getMonthValue()))
                            .setDay(String.valueOf(dayTime.getDayOfMonth()))
                            .setEnergymeterEpimportTotal(data.getTotal())
                            .setEnergymeterEpimportStart(data.getStart())
                            .setEnergymeterEpimportEnd(data.getEnd())
                            .setStaTime(Timestamp.valueOf(dayTime.atStartOfDay()))
                            .setManualFlag(1);
                    projectStaDeviceElectricityDayMapper.insert(entity);
                }
            }
        }

        if (MeterReadCycleEnum.month.equals(dto.getCycle())) {
            YearMonth monthTime = dto.getMonthTime();
            Map<String, ProjectStaDeviceElectricityMonthEntity> oldMap = projectStaDeviceElectricityMonthMapper.selectList(
                    new LambdaQueryWrapper<ProjectStaDeviceElectricityMonthEntity>()
                            .in(ProjectStaDeviceElectricityMonthEntity::getBizDeviceId, dto.getBizDeviceIdList())
                            .eq(ProjectStaDeviceElectricityMonthEntity::getStaTime, Timestamp.valueOf(monthTime.atDay(1).atStartOfDay()))
            ).stream().collect(Collectors.toMap(ProjectStaDeviceElectricityMonthEntity::getBizDeviceId, o -> o, (o1, o2) -> o1));
            for (MeterImportDataDTO data : dto.getDataList()) {
                ProjectStaDeviceElectricityMonthEntity entity = oldMap.get(data.getBizDeviceId());
                if (null != entity) {
                    entity.setEnergymeterEpimportTotal(data.getTotal())
                            .setEnergymeterEpimportStart(data.getStart())
                            .setEnergymeterEpimportEnd(data.getEnd())
                            .setManualFlag(1);
                    projectStaDeviceElectricityMonthMapper.updateById(entity);
                } else {
                    TenantContext.setIgnore(true);
                    entity = projectStaDeviceElectricityMonthMapper.getManualInsertData(data.getBizDeviceId());
                    if (null == entity) {
                        throw new BusinessException("获取设备信息异常，保存失败");
                    }
                    entity.setYear(String.valueOf(monthTime.getYear()))
                            .setMonth(String.valueOf(monthTime.getMonthValue()))
                            .setEnergymeterEpimportTotal(data.getTotal())
                            .setEnergymeterEpimportStart(data.getStart())
                            .setEnergymeterEpimportEnd(data.getEnd())
                            .setStaTime(Timestamp.valueOf(monthTime.atDay(1).atStartOfDay()))
                            .setManualFlag(1);
                    projectStaDeviceElectricityMonthMapper.insert(entity);
                }
            }
        }
    }
}
