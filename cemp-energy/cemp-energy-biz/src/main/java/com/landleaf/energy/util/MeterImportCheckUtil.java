package com.landleaf.energy.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.landleaf.bms.api.CategoryApi;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.energy.dal.mapper.DeviceMonitorMapper;
import com.landleaf.energy.dal.mapper.ProjectMapper;
import com.landleaf.energy.domain.dto.MeterImportDTO;
import com.landleaf.energy.domain.dto.MeterImportDataDTO;
import com.landleaf.energy.domain.entity.DeviceMonitorEntity;
import com.landleaf.energy.domain.entity.ProjectEntity;
import com.landleaf.energy.domain.enums.MeterReadCycleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeterImportCheckUtil {
    private final ProjectMapper projectMapper;
    private final DeviceMonitorMapper deviceMonitorMapper;
    private final CategoryApi categoryApi;

    private boolean isNumber(String str) {
        return NumberUtil.isLong(str) || NumberUtil.isDouble(str);
    }

    private void timeFormat(String timeStr, MeterImportDTO dto) {
        try {
            YearMonth time = YearMonth.parse(timeStr, DateTimeFormatter.ofPattern("yyyy-MM"));
            dto.setCycle(MeterReadCycleEnum.month);
            dto.setMonthTime(time);
        } catch (Exception e) {
            // do nothing
        }
        if (null != dto.getCycle()) {
            return;
        }
        try {
            LocalDate time = LocalDate.parse(timeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            dto.setCycle(MeterReadCycleEnum.day);
            dto.setDayTime(time);
        } catch (Exception e) {
            // do nothing
        }
        if (null != dto.getCycle()) {
            return;
        }
        dto.getErrMsg().add("计量周期格式异常");
    }

    private List<Map<String, Object>> readExcel(MultipartFile file) {
        try (InputStream ins = file.getInputStream()) {
            return ExcelUtil.getReader(ins).readAll();
        } catch (IOException e) {
            throw new BusinessException("读取excel异常", e);
        }
    }

    private MeterImportDTO baseCheck(List<Map<String, Object>> rowList) {
        MeterImportDTO dto = new MeterImportDTO();
        if (CollUtil.isEmpty(rowList)) {
            dto.getErrMsg().add("未读取到excel中待导入的数据");
            return dto;
        }
        HashSet<String> projectNameCol = new HashSet<>();
        HashSet<String> timeCol = new HashSet<>();
        for (int i = 0; i < rowList.size(); i++) {
            Map<String, Object> row = rowList.get(i);
            MeterImportDataDTO data = new MeterImportDataDTO();
            Object projectName = row.get("项目名称");
            if (null != projectName && StrUtil.isNotBlank(projectName.toString())) {
                projectNameCol.add(projectName.toString());
            } else {
                dto.getErrMsg().add("数据第" + (i + 1) + "行项目名称为空");
            }
            Object time = row.get("计量周期");
            if (null != time && StrUtil.isNotBlank(time.toString())) {
                timeCol.add(time.toString());
            } else {
                dto.getErrMsg().add("数据第" + (i + 1) + "行计量周期为空");
            }
            Object deviceName = row.get("设备名称");
            if (null == deviceName || StrUtil.isBlank(deviceName.toString())) {
                dto.getErrMsg().add("数据第" + (i + 1) + "行设备名称为空");
            } else {
                String deviceNameStr = deviceName.toString();
                if (dto.getDeviceNameList().contains(deviceNameStr)) {
                    dto.getErrMsg().add("设备" + deviceNameStr + "重复");
                } else {
                    data.setDeviceName(deviceNameStr);
                    dto.getDeviceNameList().add(deviceNameStr);
                }
            }
            String start = row.getOrDefault("期初表显值", StrUtil.EMPTY).toString();
            if (StrUtil.isNotBlank(start) && !StrUtil.equals("自动", start)) {
                if (isNumber(start)) {
                    data.setStart(new BigDecimal(start));
                } else {
                    dto.getErrMsg().add("数据第" + (i + 1) + "行期初表显值异常");
                }
            }
            String end = row.getOrDefault("期末表显值", StrUtil.EMPTY).toString();
            if (StrUtil.isBlank(end) || !isNumber(end)) {
                dto.getErrMsg().add("数据第" + (i + 1) + "行期末表显值异常");
            } else {
                data.setEnd(new BigDecimal(end));
            }
            String total = row.getOrDefault("本期用量", StrUtil.EMPTY).toString();
            if (StrUtil.isNotBlank(total) && !StrUtil.equals("自动", total)) {
                if (isNumber(total)) {
                    data.setTotal(new BigDecimal(total));
                } else {
                    dto.getErrMsg().add("数据第" + (i + 1) + "行本期用量异常");
                }
            }
            dto.getDataList().add(data);
        }
        switch (projectNameCol.size()) {
            case 0 -> dto.getErrMsg().add("项目名称为空");
            case 1 -> dto.setProjectName(projectNameCol.iterator().next());
            default -> dto.getErrMsg().add("一次只能导入一个项目");
        }
        switch (timeCol.size()) {
            case 0 -> dto.getErrMsg().add("计量周期为空的数据");
            case 1 -> timeFormat(timeCol.iterator().next(), dto);
            default -> dto.getErrMsg().add("一次只能导入一个周期的数据");
        }
        return dto;
    }

    private void dbCheck(MeterImportDTO dto) {
        ProjectEntity project = projectMapper.selectOne(new LambdaQueryWrapper<ProjectEntity>().eq(ProjectEntity::getName, dto.getProjectName()));
        if (null == project) {
            dto.getErrMsg().add("项目不存在");
            return;
        }
        dto.setBizProjectId(project.getBizProjectId());
        String categoryId = categoryApi.getBizCategoryId(dto.getCategoryCode()).getCheckedData();
        Map<String, String> deviceNameId = deviceMonitorMapper.selectList(new LambdaQueryWrapper<DeviceMonitorEntity>()
                        .eq(DeviceMonitorEntity::getBizCategoryId, categoryId)
                        .eq(DeviceMonitorEntity::getBizProjectId, project.getBizProjectId())
                        .in(DeviceMonitorEntity::getName, dto.getDeviceNameList()))
                .stream()
                .collect(Collectors.toMap(DeviceMonitorEntity::getName, DeviceMonitorEntity::getBizDeviceId, (o1, o2) -> o1));
        Collection<String> disjunction = CollUtil.disjunction(dto.getDeviceNameList(), deviceNameId.keySet());
        if (!disjunction.isEmpty()) {
            dto.getErrMsg().add("设备[" + StrUtil.join(",", disjunction) + "]不存在");
            return;
        }
        for (MeterImportDataDTO data : dto.getDataList()) {
            String bizDeviceId = deviceNameId.get(data.getDeviceName());
            data.setBizDeviceId(bizDeviceId);
            dto.getBizDeviceIdList().add(bizDeviceId);
        }
    }

    public MeterImportDTO getImportDTO(MultipartFile file, String categoryCode) {
        List<Map<String, Object>> rowList = readExcel(file);
        MeterImportDTO dto = baseCheck(rowList);
        if (!dto.getErrMsg().isEmpty()) {
            return dto;
        }
        dto.setCategoryCode(categoryCode);
        dbCheck(dto);
        return dto;
    }
}
