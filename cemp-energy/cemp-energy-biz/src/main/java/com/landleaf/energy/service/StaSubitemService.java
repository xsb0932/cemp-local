package com.landleaf.energy.service;

import com.landleaf.energy.enums.SubitemIndexEnum;
import com.landleaf.energy.request.SubitemRequest;
import com.landleaf.energy.response.SubitemYearRatioResoponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 分项查询
 *
 * @author yue lin
 * @since 2023/7/28 9:39
 */
public interface StaSubitemService {

    /**
     * 查询某一天的指标
     *
     * @param projectBizId 项目ID
     * @param indices      指标
     * @param days         某天
     * @return 结果
     */
    Map<LocalDate, Map<SubitemIndexEnum, BigDecimal>> searchDataByDay(String projectBizId, LocalDate[] days, SubitemIndexEnum... indices);

    /**
     * 查询当天的指标
     *
     * @param projectBizId 项目ID
     * @param indices      指标
     * @return 结果
     */
    EnumMap<SubitemIndexEnum, BigDecimal> searchDataByDay(String projectBizId, SubitemIndexEnum... indices);

    /**
     * 查询某一月的指标
     *
     * @param projectBizId 项目ID
     * @param indices      指标
     * @param months       某月
     * @return 结果
     */
    Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> searchDataByMonth(String projectBizId, YearMonth[] months, SubitemIndexEnum... indices);

    /**
     * 查询当月的指标
     *
     * @param projectBizId 项目ID
     * @param indices      指标
     * @return 结果
     */
    EnumMap<SubitemIndexEnum, BigDecimal> searchDataByMonth(String projectBizId, SubitemIndexEnum... indices);

    /**
     * 查询某年的指标
     *
     * @param projectBizId 项目ID
     * @param indices      指标
     * @param years        某年
     * @return 结果
     */
    Map<Year, Map<SubitemIndexEnum, BigDecimal>> searchDataByYear(String projectBizId, Year[] years, SubitemIndexEnum... indices);

    /**
     * 查询当年的指标
     *
     * @param projectBizId 项目ID
     * @param indices      指标
     * @return 结果
     */
    EnumMap<SubitemIndexEnum, BigDecimal> searchDataByYear(String projectBizId, SubitemIndexEnum... indices);

    /**
     * 查询累计的指标
     *
     * @param projectBizId 项目ID
     * @param indices      指标
     * @return 结果
     */
    EnumMap<SubitemIndexEnum, BigDecimal> searchDataByCumulative(String projectBizId, SubitemIndexEnum... indices);

    /**
     * 查询截止某月(当年)为止累计的指标
     *
     * @param projectBizId 项目ID
     * @param month        截止月份（包含）
     * @param indices      指标
     * @return 结果
     */
    EnumMap<SubitemIndexEnum, BigDecimal> searchDataByMonthCumulative(String projectBizId, YearMonth month, SubitemIndexEnum... indices);

    /**
     * 查询充电桩的属性信息
     *
     * @param deviceBizIds 项目ID
     * @param day          某天
     * @return 结果
     */
    Map<String, BigDecimal> searchDeviceDataByDay(String[] deviceBizIds, LocalDate day);

    /**
     * 根据设备(电表)- 查询用电量/天
     *
     * @param deviceBizIds 项目ID
     * @param day          某天
     * @return 结果
     */
    Map<String, BigDecimal> searchDeviceEpimport(String[] deviceBizIds, LocalDate day);

    /**
     * 根据设备(电表)- 查询发电量/天
     *
     * @param deviceBizIds 项目ID
     * @param day          某天
     * @return 结果
     */
    Map<String, BigDecimal> searchDeviceEpexport(String[] deviceBizIds, LocalDate day);

    SubitemYearRatioResoponse getSubitemYearRatio(SubitemRequest subitemRequest);

    Map<String, BigDecimal> searchDeviceEpimportYear(String[] deviceBizId, String year);

    Map<String, BigDecimal> searchDeviceEpexportYear(String[] deviceBizId, String year);

    Map<String, Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> batchSearchDataOnlyByMonth(List<String> bizProjectIdList, List<YearMonth> months, List<SubitemIndexEnum> indices);
}
