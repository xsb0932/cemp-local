package com.landleaf.energy.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.api.CategoryApi;
import com.landleaf.bms.api.UserProjectApi;
import com.landleaf.bms.api.dto.UserProjectDTO;
import com.landleaf.comm.constance.DeviceStaCategoryEnum;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.energy.dal.mapper.ProjectStaDeviceGasDayMapper;
import com.landleaf.energy.dal.mapper.ProjectStaDeviceGasMonthMapper;
import com.landleaf.energy.domain.dto.GasMeterTreeDTO;
import com.landleaf.energy.domain.dto.MeterImportDTO;
import com.landleaf.energy.domain.dto.MeterImportDataDTO;
import com.landleaf.energy.domain.entity.ProjectStaDeviceGasDayEntity;
import com.landleaf.energy.domain.entity.ProjectStaDeviceGasMonthEntity;
import com.landleaf.energy.domain.enums.MeterReadCycleEnum;
import com.landleaf.energy.domain.enums.MeterReadEnum;
import com.landleaf.energy.domain.request.GasMeterDetailResponse;
import com.landleaf.energy.domain.request.GasMeterDeviceResponse;
import com.landleaf.energy.domain.request.GasMeterPageQueryRequest;
import com.landleaf.energy.domain.request.GasMeterSaveRequest;
import com.landleaf.energy.domain.response.GasMeterPageResponse;
import com.landleaf.energy.util.MeterImportCheckUtil;
import com.landleaf.monitor.api.DeviceParameterApi;
import com.landleaf.monitor.api.dto.MeterDeviceDTO;
import com.landleaf.monitor.api.request.MeterDeviceRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GasMeterService {
    private final UserProjectApi userProjectApi;
    private final DeviceParameterApi deviceParameterApi;
    private final CategoryApi categoryApi;
    private final ProjectStaDeviceGasDayMapper projectStaDeviceGasDayMapper;
    private final ProjectStaDeviceGasMonthMapper projectStaDeviceGasMonthMapper;
    private final MeterImportCheckUtil meterImportCheckUtil;

    /**
     * 查询项目气表树
     *
     * @param meterReadEnum      抄表方式类型
     * @param meterReadCycleEnum 抄表周期类型
     * @param userId             用户id
     * @return List<WaterMeterTreeDTO>
     */
    public List<GasMeterTreeDTO> tree(MeterReadEnum meterReadEnum, MeterReadCycleEnum meterReadCycleEnum, Long userId) {
        Map<String, String> projectMap = userProjectApi.getUserProjectList(userId)
                .getCheckedData()
                .stream()
                .collect(Collectors.toMap(UserProjectDTO::getBizProjectId, UserProjectDTO::getName, (o1, o2) -> o1));
        if (MapUtil.isEmpty(projectMap)) {
            return Collections.emptyList();
        }
        TenantContext.setIgnore(true);
        try {
            String bizCategoryId = categoryApi.getBizCategoryId(DeviceStaCategoryEnum.RQB.getCode()).getCheckedData();
            List<MeterDeviceDTO> deviceList = deviceParameterApi.getDeviceByProjectCategoryParameter(
                    new MeterDeviceRequest(projectMap.keySet(), bizCategoryId, meterReadEnum.getCode(), meterReadCycleEnum.getCode())
            ).getCheckedData();
            if (CollectionUtil.isEmpty(deviceList)) {
                return Collections.emptyList();
            }

            List<GasMeterTreeDTO> result = new ArrayList<>();
            Map<String, List<MeterDeviceDTO>> projectDeviceMap = deviceList.stream().collect(Collectors.groupingBy(MeterDeviceDTO::getBizProjectId));
            for (Map.Entry<String, List<MeterDeviceDTO>> entry : projectDeviceMap.entrySet()) {
                String bizProjectId = entry.getKey();
                String projectName = projectMap.get(bizProjectId);
                GasMeterTreeDTO project = new GasMeterTreeDTO();
                project.setId(bizProjectId).setName(projectName).setType(0).setChildren(new ArrayList<>());
                result.add(project);

                for (MeterDeviceDTO meterDeviceDTO : entry.getValue()) {
                    GasMeterTreeDTO device = new GasMeterTreeDTO();
                    device.setId(meterDeviceDTO.getBizDeviceId()).setName(meterDeviceDTO.getName()).setType(1);
                    project.getChildren().add(device);
                }
            }
            return result;
        } finally {
            TenantContext.setIgnore(false);
        }
    }

    /**
     * 查询手动抄表气表
     *
     * @param meterReadCycleEnum 抄表周期类型
     * @param bizProjectId       项目业务id
     * @param userId             用户id
     * @return List<WaterMeterDeviceResponse>
     */
    public List<GasMeterDeviceResponse> meterDevices(MeterReadCycleEnum meterReadCycleEnum, String bizProjectId, Long userId) {
        Map<String, String> projectMap = userProjectApi.getUserProjectList(userId)
                .getCheckedData()
                .stream()
                .collect(Collectors.toMap(UserProjectDTO::getBizProjectId, UserProjectDTO::getName, (o1, o2) -> o1));
        if (MapUtil.isEmpty(projectMap) || !projectMap.containsKey(bizProjectId)) {
            return Collections.emptyList();
        }
        TenantContext.setIgnore(true);
        try {
            String bizCategoryId = categoryApi.getBizCategoryId(DeviceStaCategoryEnum.RQB.getCode()).getCheckedData();
            List<MeterDeviceDTO> deviceList = deviceParameterApi.getDeviceByProjectCategoryParameter(
                    new MeterDeviceRequest(CollectionUtil.newArrayList(bizProjectId), bizCategoryId, MeterReadEnum.manual.getCode(), meterReadCycleEnum.getCode())
            ).getCheckedData();
            if (CollectionUtil.isEmpty(deviceList)) {
                return Collections.emptyList();
            }

            List<GasMeterDeviceResponse> result = new ArrayList<>();
            for (MeterDeviceDTO meterDeviceDTO : deviceList) {
                GasMeterDeviceResponse device = new GasMeterDeviceResponse();
                device.setBizDeviceId(meterDeviceDTO.getBizDeviceId()).setName(meterDeviceDTO.getName());
                result.add(device);
            }
            return result;
        } finally {
            TenantContext.setIgnore(false);
        }
    }

    /**
     * 分页查询
     *
     * @param request 查询条件
     * @return Page<WaterMeterPageResponse>
     */
    public IPage<GasMeterPageResponse> page(GasMeterPageQueryRequest request) {
        TenantContext.setIgnore(true);
        try {
            if (CollectionUtil.isEmpty(request.getBizDeviceIds())) {
                return Page.of(request.getPageNo(), request.getPageSize());
            }
            MeterReadCycleEnum meterReadCycleEnum = MeterReadCycleEnum.ofCode(request.getMeterReadCycle());
            if (MeterReadCycleEnum.day.equals(meterReadCycleEnum)) {
                LocalDate start = LocalDate.parse(request.getStart());
                LocalDate end = LocalDate.parse(request.getEnd());
                return projectStaDeviceGasDayMapper.selectMeterPage(
                        Page.of(request.getPageNo(), request.getPageSize()),
                        request.getBizDeviceIds(),
                        Timestamp.valueOf(start.atStartOfDay()),
                        Timestamp.valueOf(end.atStartOfDay())
                );
            }

            if (MeterReadCycleEnum.month.equals(meterReadCycleEnum)) {
                YearMonth start = YearMonth.parse(request.getStart());
                YearMonth end = YearMonth.parse(request.getEnd());
                return projectStaDeviceGasMonthMapper.selectMeterPage(
                        Page.of(request.getPageNo(), request.getPageSize()),
                        request.getBizDeviceIds(),
                        Timestamp.valueOf(start.atDay(1).atStartOfDay()),
                        Timestamp.valueOf(end.atDay(1).atStartOfDay())
                );
            }
            return null;
        } finally {
            TenantContext.setIgnore(false);
        }
    }

    /**
     * 获取记录详情
     *
     * @param id                 主键id
     * @param meterReadCycleEnum 抄表周期类型
     * @return WaterMeterDetailResponse
     */
    public GasMeterDetailResponse detail(Long id, MeterReadCycleEnum meterReadCycleEnum) {
        TenantContext.setIgnore(true);
        try {
            if (MeterReadCycleEnum.day.equals(meterReadCycleEnum)) {
                return projectStaDeviceGasDayMapper.detail(id);
            }

            if (MeterReadCycleEnum.month.equals(meterReadCycleEnum)) {
                return projectStaDeviceGasMonthMapper.detail(id);
            }
            return null;
        } finally {
            TenantContext.setIgnore(false);
        }
    }

    /**
     * 获取设备期初值
     *
     * @param bizDeviceId        设备业务id
     * @param staTime            期数
     * @param meterReadCycleEnum 抄表周期类型
     * @return BigDecimal
     */
    public BigDecimal getGasStart(String bizDeviceId, String staTime, MeterReadCycleEnum meterReadCycleEnum) {
        TenantContext.setIgnore(true);
        try {
            if (MeterReadCycleEnum.day.equals(meterReadCycleEnum)) {
                LocalDate lastDay = LocalDate.parse(staTime).minusDays(1L);
                ProjectStaDeviceGasDayEntity record = projectStaDeviceGasDayMapper.selectOne(new LambdaQueryWrapper<ProjectStaDeviceGasDayEntity>()
                        .eq(ProjectStaDeviceGasDayEntity::getBizDeviceId, bizDeviceId)
                        .eq(ProjectStaDeviceGasDayEntity::getStaTime, Timestamp.valueOf(lastDay.atStartOfDay())));
                if (null != record) {
                    return record.getGasmeterUsageTotalEnd();
                }
            }

            if (MeterReadCycleEnum.month.equals(meterReadCycleEnum)) {
                YearMonth lastMonth = YearMonth.parse(staTime).minusMonths(1L);
                ProjectStaDeviceGasMonthEntity record = projectStaDeviceGasMonthMapper.selectOne(new LambdaQueryWrapper<ProjectStaDeviceGasMonthEntity>()
                        .eq(ProjectStaDeviceGasMonthEntity::getBizDeviceId, bizDeviceId)
                        .eq(ProjectStaDeviceGasMonthEntity::getStaTime, Timestamp.valueOf(lastMonth.atDay(1).atStartOfDay())));
                if (null != record) {
                    return record.getGasmeterUsageTotalEnd();
                }
            }
            return null;
        } finally {
            TenantContext.setIgnore(false);
        }
    }

    /**
     * 保存or更新气表抄表记录
     *
     * @param request 入参
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(GasMeterSaveRequest request) {
        Long tenantId = TenantContext.getTenantId();
        if (null == tenantId) {
            throw new BusinessException("获取当前租户信息异常");
        }
        TenantContext.setIgnore(true);
        try {
            MeterReadCycleEnum meterReadCycleEnum = MeterReadCycleEnum.ofCode(request.getMeterReadCycle());
            if (MeterReadCycleEnum.day.equals(meterReadCycleEnum)) {
                LocalDate staTime = LocalDate.parse(request.getTime());
                ProjectStaDeviceGasDayEntity entity = projectStaDeviceGasDayMapper.selectOne(new LambdaQueryWrapper<ProjectStaDeviceGasDayEntity>()
                        .eq(ProjectStaDeviceGasDayEntity::getBizDeviceId, request.getBizDeviceId())
                        .eq(ProjectStaDeviceGasDayEntity::getStaTime, Timestamp.valueOf(staTime.atStartOfDay())));
                if (null != entity) {
                    entity.setGasmeterUsageTotalStart(request.getGasStart())
                            .setGasmeterUsageTotal(request.getGasTotal())
                            .setGasmeterUsageTotalEnd(request.getGasEnd())
                            .setRemark(request.getRemark())
                            .setManualFlag(1);
                    projectStaDeviceGasDayMapper.updateById(entity);
                } else {
                    entity = projectStaDeviceGasDayMapper.getManualInsertData(request.getBizDeviceId());
                    if (null == entity) {
                        throw new BusinessException("获取设备信息异常，保存失败");
                    }
                    entity.setYear(String.valueOf(staTime.getYear()))
                            .setMonth(String.valueOf(staTime.getMonthValue()))
                            .setDay(String.valueOf(staTime.getDayOfMonth()))
                            .setGasmeterUsageTotal(request.getGasTotal())
                            .setGasmeterUsageTotalStart(request.getGasStart())
                            .setGasmeterUsageTotalEnd(request.getGasEnd())
                            .setStaTime(Timestamp.valueOf(staTime.atStartOfDay()))
                            .setRemark(request.getRemark())
                            .setManualFlag(1);
                    projectStaDeviceGasDayMapper.insert(entity);
                }
            }

            if (MeterReadCycleEnum.month.equals(meterReadCycleEnum)) {
                YearMonth staTime = YearMonth.parse(request.getTime());
                ProjectStaDeviceGasMonthEntity entity = projectStaDeviceGasMonthMapper.selectOne(new LambdaQueryWrapper<ProjectStaDeviceGasMonthEntity>()
                        .eq(ProjectStaDeviceGasMonthEntity::getBizDeviceId, request.getBizDeviceId())
                        .eq(ProjectStaDeviceGasMonthEntity::getStaTime, Timestamp.valueOf(staTime.atDay(1).atStartOfDay())));
                if (null != entity) {
                    entity.setGasmeterUsageTotalStart(request.getGasStart())
                            .setGasmeterUsageTotal(request.getGasTotal())
                            .setGasmeterUsageTotalEnd(request.getGasEnd())
                            .setRemark(request.getRemark())
                            .setManualFlag(1);
                    projectStaDeviceGasMonthMapper.updateById(entity);
                } else {
                    entity = projectStaDeviceGasMonthMapper.getManualInsertData(request.getBizDeviceId());
                    if (null == entity) {
                        throw new BusinessException("获取设备信息异常，保存失败");
                    }
                    entity.setYear(String.valueOf(staTime.getYear()))
                            .setMonth(String.valueOf(staTime.getMonthValue()))
                            .setGasmeterUsageTotal(request.getGasTotal())
                            .setGasmeterUsageTotalStart(request.getGasStart())
                            .setGasmeterUsageTotalEnd(request.getGasEnd())
                            .setStaTime(Timestamp.valueOf(staTime.atDay(1).atStartOfDay()))
                            .setRemark(request.getRemark())
                            .setManualFlag(1);
                    projectStaDeviceGasMonthMapper.insert(entity);
                }
            }
        } finally {
            TenantContext.setIgnore(false);
        }
    }

    public MeterImportDTO excelImportCheck(MultipartFile file) {
        MeterImportDTO dto = meterImportCheckUtil.getImportDTO(file, DeviceStaCategoryEnum.RQB.getCode());
        if (!dto.getErrMsg().isEmpty()) {
            return dto;
        }
        Map<String, BigDecimal> startMap = null;
        if (MeterReadCycleEnum.day.equals(dto.getCycle())) {
            LocalDate lastDay = dto.getDayTime().minusDays(1L);
            startMap = projectStaDeviceGasDayMapper.selectList(new LambdaQueryWrapper<ProjectStaDeviceGasDayEntity>()
                            .in(ProjectStaDeviceGasDayEntity::getBizDeviceId, dto.getBizDeviceIdList())
                            .eq(ProjectStaDeviceGasDayEntity::getStaTime, Timestamp.valueOf(lastDay.atStartOfDay())))
                    .stream()
                    .collect(HashMap::new, (k, v) -> k.put(v.getBizDeviceId(), v.getGasmeterUsageTotalEnd()), HashMap::putAll);
        }
        if (MeterReadCycleEnum.month.equals(dto.getCycle())) {
            YearMonth lastMonth = dto.getMonthTime().minusMonths(1L);
            startMap = projectStaDeviceGasMonthMapper.selectList(new LambdaQueryWrapper<ProjectStaDeviceGasMonthEntity>()
                            .in(ProjectStaDeviceGasMonthEntity::getBizDeviceId, dto.getBizDeviceIdList())
                            .eq(ProjectStaDeviceGasMonthEntity::getStaTime, Timestamp.valueOf(lastMonth.atDay(1).atStartOfDay())))
                    .stream()
                    .collect(HashMap::new, (k, v) -> k.put(v.getBizDeviceId(), v.getGasmeterUsageTotalEnd()), HashMap::putAll);
        }
        boolean flag = MapUtil.isNotEmpty(startMap);

        for (MeterImportDataDTO data : dto.getDataList()) {
            if (null == data.getStart() && flag) {
                data.setStart(startMap.get(data.getBizDeviceId()));
            }
            if (null == data.getTotal()) {
                if (null == data.getStart()) {
                    dto.getErrMsg().add("设备" + data.getDeviceName() + "缺少期初值计算本期用量");
                } else {
                    data.setTotal(data.getEnd().subtract(data.getStart()));
                }
            }
            if (null != data.getTotal() && data.getTotal().compareTo(BigDecimal.ZERO) < 0) {
                dto.getErrMsg().add("设备" + data.getDeviceName() + "本期用量结果不能为负");
            }
        }
        return dto;
    }

    @Transactional(rollbackFor = Exception.class)
    public void excelImportSave(MeterImportDTO dto) {
        if (MeterReadCycleEnum.day.equals(dto.getCycle())) {
            LocalDate dayTime = dto.getDayTime();
            Map<String, ProjectStaDeviceGasDayEntity> oldMap = projectStaDeviceGasDayMapper.selectList(
                    new LambdaQueryWrapper<ProjectStaDeviceGasDayEntity>()
                            .in(ProjectStaDeviceGasDayEntity::getBizDeviceId, dto.getBizDeviceIdList())
                            .eq(ProjectStaDeviceGasDayEntity::getStaTime, Timestamp.valueOf(dayTime.atStartOfDay()))
            ).stream().collect(Collectors.toMap(ProjectStaDeviceGasDayEntity::getBizDeviceId, o -> o, (o1, o2) -> o1));
            for (MeterImportDataDTO data : dto.getDataList()) {
                ProjectStaDeviceGasDayEntity entity = oldMap.get(data.getBizDeviceId());
                if (null != entity) {
                    entity.setGasmeterUsageTotalStart(data.getStart())
                            .setGasmeterUsageTotal(data.getTotal())
                            .setGasmeterUsageTotalEnd(data.getEnd())
                            .setManualFlag(1);
                    projectStaDeviceGasDayMapper.updateById(entity);
                } else {
                    TenantContext.setIgnore(true);
                    entity = projectStaDeviceGasDayMapper.getManualInsertData(data.getBizDeviceId());
                    if (null == entity) {
                        throw new BusinessException("获取设备信息异常，保存失败");
                    }
                    entity.setYear(String.valueOf(dto.getDayTime().getYear()))
                            .setMonth(String.valueOf(dayTime.getMonthValue()))
                            .setDay(String.valueOf(dayTime.getDayOfMonth()))
                            .setGasmeterUsageTotal(data.getTotal())
                            .setGasmeterUsageTotalStart(data.getStart())
                            .setGasmeterUsageTotalEnd(data.getEnd())
                            .setStaTime(Timestamp.valueOf(dayTime.atStartOfDay()))
                            .setManualFlag(1);
                    projectStaDeviceGasDayMapper.insert(entity);
                }
            }
        }

        if (MeterReadCycleEnum.month.equals(dto.getCycle())) {
            YearMonth monthTime = dto.getMonthTime();
            Map<String, ProjectStaDeviceGasMonthEntity> oldMap = projectStaDeviceGasMonthMapper.selectList(
                    new LambdaQueryWrapper<ProjectStaDeviceGasMonthEntity>()
                            .in(ProjectStaDeviceGasMonthEntity::getBizDeviceId, dto.getBizDeviceIdList())
                            .eq(ProjectStaDeviceGasMonthEntity::getStaTime, Timestamp.valueOf(monthTime.atDay(1).atStartOfDay()))
            ).stream().collect(Collectors.toMap(ProjectStaDeviceGasMonthEntity::getBizDeviceId, o -> o, (o1, o2) -> o1));
            for (MeterImportDataDTO data : dto.getDataList()) {
                ProjectStaDeviceGasMonthEntity entity = oldMap.get(data.getBizDeviceId());
                if (null != entity) {
                    entity.setGasmeterUsageTotalStart(data.getStart())
                            .setGasmeterUsageTotal(data.getTotal())
                            .setGasmeterUsageTotalEnd(data.getEnd())
                            .setManualFlag(1);
                    projectStaDeviceGasMonthMapper.updateById(entity);
                } else {
                    TenantContext.setIgnore(true);
                    entity = projectStaDeviceGasMonthMapper.getManualInsertData(data.getBizDeviceId());
                    if (null == entity) {
                        throw new BusinessException("获取设备信息异常，保存失败");
                    }
                    entity.setYear(String.valueOf(monthTime.getYear()))
                            .setMonth(String.valueOf(monthTime.getMonthValue()))
                            .setGasmeterUsageTotal(data.getTotal())
                            .setGasmeterUsageTotalStart(data.getStart())
                            .setGasmeterUsageTotalEnd(data.getEnd())
                            .setStaTime(Timestamp.valueOf(monthTime.atDay(1).atStartOfDay()))
                            .setManualFlag(1);
                    projectStaDeviceGasMonthMapper.insert(entity);
                }
            }
        }
    }
}
