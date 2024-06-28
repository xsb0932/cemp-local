package com.landleaf.energy.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.energy.domain.dto.MeterImportDTO;
import com.landleaf.energy.domain.request.ElectricityDayQueryRequest;
import com.landleaf.energy.domain.request.ElectricityHourQueryRequest;
import com.landleaf.energy.domain.request.ElectricityMeterReadingRequest;
import com.landleaf.energy.domain.request.ElectricityMonthQueryRequest;
import com.landleaf.energy.domain.response.DeviceElectricityTabulationResponse;
import com.landleaf.energy.domain.response.ElectricityMeterReadingTreeResponse;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * 电表抄表业务
 *
 * @author Tycoon
 * @since 2023/8/17 10:12
 **/
public interface ElectricityMeterReadingService {

    /**
     * 查询手动日抄表列表
     *
     * @param request 参数
     * @return 结果集
     */
    Page<DeviceElectricityTabulationResponse> searchManualElectricityTabulation(ElectricityDayQueryRequest request);

    /**
     * 查询手动月抄表列表
     *
     * @param request 参数
     * @return 结果集
     */
    Page<DeviceElectricityTabulationResponse> searchManualElectricityTabulation(ElectricityMonthQueryRequest request);

    Page<DeviceElectricityTabulationResponse> searchManualElectricityTabulation(ElectricityHourQueryRequest request);

    /**
     * 查询自动动日抄表列表
     *
     * @param request 参数
     * @return 结果集
     */
    Page<DeviceElectricityTabulationResponse> searchStaElectricityTabulation(ElectricityDayQueryRequest request);

    /**
     * 查询自动月抄表列表
     *
     * @param request 参数
     * @return 结果集
     */
    Page<DeviceElectricityTabulationResponse> searchStaElectricityTabulation(ElectricityMonthQueryRequest request);

    Page<DeviceElectricityTabulationResponse> searchStaElectricityTabulation(ElectricityHourQueryRequest request);

    /**
     * 查询电表设备的倍率
     *
     * @param bizDeviceId 业务ID
     * @return 结果
     */
    String searchMultiplyingFactor(String bizDeviceId);

    BigDecimal searchHourOpenDisplaysValue(String bizDeviceId, String time);

    /**
     * 查询期初表显（日）
     *
     * @param bizDeviceId 业务ID
     * @param time        时间
     * @return 结果
     */
    BigDecimal searchOpenDisplaysValue(String bizDeviceId, LocalDate time);

    /**
     * 查询期初表显（月）
     *
     * @param bizDeviceId 业务ID
     * @param time        时间
     * @return 结果
     */
    BigDecimal searchCloseDisplaysValue(String bizDeviceId, YearMonth time);

    void addManualElectricityHour(ElectricityMeterReadingRequest.HourCreate request);

    /**
     * 创建日手抄表
     *
     * @param request 参数
     */
    void addManualElectricityDay(ElectricityMeterReadingRequest.DayCreate request);

    /**
     * 创建月手抄表
     *
     * @param request 参数
     */
    void addManualElectricityMonth(ElectricityMeterReadingRequest.MonthCreate request);

    /**
     * 删除日手抄表
     *
     * @param id 参数
     */
    void deleteManualElectricityDay(Long id);

    /**
     * 删除月手抄表
     *
     * @param id 参数
     */
    void deleteManualElectricityMonth(Long id);

    /**
     * 变更抄表记录信息
     *
     * @param request 参数
     */
    void updateElectricityMeterReading(ElectricityMeterReadingRequest.Update request);

    /**
     * 查询电表菜单
     *
     * @param value 抄表方式(1手抄日2手抄月3远程日4远程月5手抄时6远程时)
     * @return 结果
     */
    List<ElectricityMeterReadingTreeResponse> searchElectricityTree(Integer value);


    MeterImportDTO excelImportCheck(MultipartFile file);

    void excelImportSave(MeterImportDTO dto);
}
