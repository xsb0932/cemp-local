package com.landleaf.energy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.landleaf.comm.constance.CommonConstant;
import com.landleaf.comm.constance.ElectricityBillType;
import com.landleaf.comm.constance.ErrorCodeEnumConst;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.exception.enums.GlobalErrorCodeConstants;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.energy.dal.mapper.ProjectCnfTimePeriodMapper;
import com.landleaf.energy.domain.dto.ProjectCnfTimePeriodAddDTO;
import com.landleaf.energy.domain.dto.ProjectCnfTimePeriodQueryDTO;
import com.landleaf.energy.domain.dto.ProjectCnfTimePeriodRmDTO;
import com.landleaf.energy.domain.dto.TimeDuringDTO;
import com.landleaf.energy.domain.entity.ProjectCnfTimePeriodEntity;
import com.landleaf.energy.domain.vo.ProjectCnfTimePeriodVO;
import com.landleaf.energy.service.ProjectCnfTimePeriodService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * ProjectCnfTimePeriodEntity对象的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-06-24
 */
@Service
@AllArgsConstructor
@Slf4j
public class ProjectCnfTimePeriodServiceImpl extends ServiceImpl<ProjectCnfTimePeriodMapper, ProjectCnfTimePeriodEntity> implements ProjectCnfTimePeriodService {

    /**
     * 数据库操作句柄
     */
    @Resource
    ProjectCnfTimePeriodMapper projectCnfTimePeriodMapper;

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日");

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectCnfTimePeriodAddDTO save(ProjectCnfTimePeriodAddDTO addInfo) {
        ProjectCnfTimePeriodEntity entity = new ProjectCnfTimePeriodEntity();
        BeanUtil.copyProperties(addInfo, entity);
        if (null == entity.getDeleted()) {
            entity.setDeleted(CommonConstant.DELETED_FLAG_NOT_DELETE);
        }
        if (null == entity.getCreateTime()) {
            entity.setCreateTime(LocalDateTime.now());
        }
        int effectNum = projectCnfTimePeriodMapper.insert(entity);
        if (0 == effectNum) {
            // 插入失败
            throw new BusinessException(ErrorCodeEnumConst.DATA_INSERT_ERROR.getCode(), ErrorCodeEnumConst.DATA_INSERT_ERROR.getMessage());
        }
        BeanUtil.copyProperties(entity, addInfo);
        return addInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ProjectCnfTimePeriodAddDTO updateInfo) {
//        ProjectCnfTimePeriodEntity entity = projectCnfTimePeriodMapper.selectById(updateInfo.getId());
//        if (null == entity) {
//            // 修改失败
//            throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
//        }
//        BeanUtil.copyProperties(updateInfo, entity, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
//
//        projectCnfTimePeriodMapper.updateById(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateIsDeleted(String ids, Integer isDeleted) {
        String[] idArray = ids.split(",");
        List<Long> idList = new ArrayList<Long>();
        for (String id : idArray) {
            idList.add(Long.valueOf(id));
        }
        projectCnfTimePeriodMapper.updateIsDeleted(idList, isDeleted);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectCnfTimePeriodEntity selectById(Long id) {
        ProjectCnfTimePeriodEntity entity = projectCnfTimePeriodMapper.selectById(id);
        if (null == entity) {
            return null;
        }
        return CommonConstant.DELETED_FLAG_NOT_DELETE == entity.getDeleted().intValue() ? entity : null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProjectCnfTimePeriodEntity> list(ProjectCnfTimePeriodQueryDTO queryInfo) {
        return projectCnfTimePeriodMapper.selectList(getCondition(queryInfo));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPage<ProjectCnfTimePeriodEntity> page(ProjectCnfTimePeriodQueryDTO queryInfo) {
        IPage<ProjectCnfTimePeriodEntity> page = new Page<>(queryInfo.getPageNo(), queryInfo.getPageSize());
        page = projectCnfTimePeriodMapper.selectPage(page, getCondition(queryInfo));
        return page;
    }

    @Override
    public List<ProjectCnfTimePeriodVO> listByBizProjectId(String bizProjectId) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        // pd要求，展示去年1月份以后的所有配置，所以，取年份>=year-1即可
        List<ProjectCnfTimePeriodEntity> list = projectCnfTimePeriodMapper.selectList(new QueryWrapper<ProjectCnfTimePeriodEntity>().lambda()
                .eq(ProjectCnfTimePeriodEntity::getProjectId, bizProjectId)
                .eq(ProjectCnfTimePeriodEntity::getTenantId, TenantContext.getTenantId())
                .ge(ProjectCnfTimePeriodEntity::getPeriodYear, String.valueOf(year - 1)).orderByAsc(ProjectCnfTimePeriodEntity::getTimeBegin));
        List<ProjectCnfTimePeriodVO> result = new ArrayList<>();

        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        Map<String, ProjectCnfTimePeriodVO> resultMap = new HashMap<>();

        String time = null;
        for (ProjectCnfTimePeriodEntity temp : list) {
            time = temp.getPeriodYear() + "年" + temp.getPeriodMonth() + "月";
            if (!resultMap.containsKey(time)) {
                resultMap.put(time, new ProjectCnfTimePeriodVO().setSortField(YearMonth.of(Integer.parseInt(temp.getPeriodYear()), Integer.parseInt(temp.getPeriodMonth()))));
                resultMap.get(time).setTime(time);
                resultMap.get(time).setProjectId(bizProjectId);
            }
            if (ElectricityBillType.VALLEY.getType().equals(temp.getCode())) {
                // 谷时段
                resultMap.get(time).setValleyPrice(temp.getPrice());
                if (null == resultMap.get(time).getValleyTimes()) {
                    resultMap.get(time).setValleyTimes(new ArrayList<>());
                }
                resultMap.get(time).getValleyTimes().add(TimeDuringDTO.builder().timeBegin(temp.getTimeBegin()).timeEnd(temp.getTimeEnd()).build());
            } else if (ElectricityBillType.FLAT.getType().equals(temp.getCode())) {
                // 平时段
                resultMap.get(time).setFlatPrice(temp.getPrice());
                if (null == resultMap.get(time).getFlatTimes()) {
                    resultMap.get(time).setFlatTimes(new ArrayList<>());
                }
                resultMap.get(time).getFlatTimes().add(TimeDuringDTO.builder().timeBegin(temp.getTimeBegin()).timeEnd(temp.getTimeEnd()).build());
            } else if (ElectricityBillType.PEAK.getType().equals(temp.getCode())) {
                // 峰时段
                resultMap.get(time).setPeakPrice(temp.getPrice());
                if (null == resultMap.get(time).getPeakTimes()) {
                    resultMap.get(time).setPeakTimes(new ArrayList<>());
                }
                resultMap.get(time).getPeakTimes().add(TimeDuringDTO.builder().timeBegin(temp.getTimeBegin()).timeEnd(temp.getTimeEnd()).build());
            } else if (ElectricityBillType.TIP.getType().equals(temp.getCode())) {
                // 尖时段
                resultMap.get(time).setTipPrice(temp.getPrice());
                if (null == resultMap.get(time).getTipTimes()) {
                    resultMap.get(time).setTipTimes(new ArrayList<>());
                }
                resultMap.get(time).getTipTimes().add(TimeDuringDTO.builder().timeBegin(temp.getTimeBegin()).timeEnd(temp.getTimeEnd()).build());
            }
        }
        result = resultMap.values().stream().sorted(Comparator.comparing(ProjectCnfTimePeriodVO::getSortField).reversed()).toList();
        return result;
    }

    private String formatInt2HHmm(Integer hour) {
        if (null == hour) {
            return hour + ":00";
        }
        if (hour < 10) {
            return "0" + hour + ":00";
        } else {
            return hour + ":00";
        }
    }

    @Override
    public boolean intelligentInsert(ProjectCnfTimePeriodAddDTO addInfo) {
        // 校验数据，是时间段不重复且全量覆盖1天的所有时间段
        List<TimeDuringDTO> tipTimes = addInfo.getTipTimes();
        List<TimeDuringDTO> peakTimes = addInfo.getPeakTimes();
        List<TimeDuringDTO> valleyTimes = addInfo.getValleyTimes();
        List<TimeDuringDTO> flatTimes = addInfo.getFlatTimes();
        List<Integer> times = new ArrayList<>();
        if (!CollectionUtil.isEmpty(tipTimes)) {
            for (TimeDuringDTO dto : tipTimes) {
                if (null == dto || null == dto.getTimeBegin()) {
                    continue;
                }
                for (int i = dto.getTimeBegin(); i <= dto.getTimeEnd(); i++) {
                    if (times.contains(i)) {
                        // 重复，返回false
                        throw new BusinessException(GlobalErrorCodeConstants.ELECTRICITY_PRICE_TIME_EXISTS.getCode(), GlobalErrorCodeConstants.ELECTRICITY_PRICE_TIME_EXISTS.getMsg() + ",重复的时间为" + i + "点");
                    }
                    times.add(i);
                }
            }
        }
        if (!CollectionUtil.isEmpty(peakTimes)) {
            for (TimeDuringDTO dto : peakTimes) {
                for (int i = dto.getTimeBegin(); i <= dto.getTimeEnd(); i++) {
                    if (times.contains(i)) {
                        // 重复，返回false
                        throw new BusinessException(GlobalErrorCodeConstants.ELECTRICITY_PRICE_TIME_EXISTS.getCode(), GlobalErrorCodeConstants.ELECTRICITY_PRICE_TIME_EXISTS.getMsg() + ",重复的时间为" + i + "点");
                    }
                    times.add(i);
                }
            }
        }
        if (!CollectionUtil.isEmpty(valleyTimes)) {
            for (TimeDuringDTO dto : valleyTimes) {
                for (int i = dto.getTimeBegin(); i <= dto.getTimeEnd(); i++) {
                    if (times.contains(i)) {
                        // 重复，返回false

                        throw new BusinessException(GlobalErrorCodeConstants.ELECTRICITY_PRICE_TIME_EXISTS.getCode(), GlobalErrorCodeConstants.ELECTRICITY_PRICE_TIME_EXISTS.getMsg() + ",重复的时间为" + i + "点");
                    }
                    times.add(i);
                }
            }
        }
        if (!CollectionUtil.isEmpty(flatTimes)) {
            for (TimeDuringDTO dto : flatTimes) {
                for (int i = dto.getTimeBegin(); i <= dto.getTimeEnd(); i++) {
                    if (times.contains(i)) {
                        // 重复，返回false

                        throw new BusinessException(GlobalErrorCodeConstants.ELECTRICITY_PRICE_TIME_EXISTS.getCode(), GlobalErrorCodeConstants.ELECTRICITY_PRICE_TIME_EXISTS.getMsg() + ",重复的时间为" + i + "点");
                    }
                    times.add(i);
                }
            }
        }
        for (int i = 0; i < 24; i++) {
            if (!times.contains(i)) {
                // 返回失败，未全量覆盖
                throw new BusinessException(GlobalErrorCodeConstants.ELECTRICITY_PRICE_TIME_EXISTS.getCode(), GlobalErrorCodeConstants.ELECTRICITY_PRICE_TIME_NOT_FULL_COVER.getMsg() + ",缺失的时间为" + i + "点");
            }
        }

        // 新增
        LocalDate ld = null;
        try {
            ld = LocalDate.parse(addInfo.getTime() + "1日", dateTimeFormatter);
        } catch (DateTimeParseException e) {
            // 日期处理不对， 返回异常
            throw new BusinessException(ErrorCodeEnumConst.DATE_FORMAT_ERROR);
        }
        LocalDate now = LocalDate.now();
        // 如果时间<=当前月，则返回错误
        if (ld.getYear() < now.getYear()) {
            // 错误
            throw new BusinessException(GlobalErrorCodeConstants.ERROR_UPDATE_TIME_ILLEGAL.getCode(), GlobalErrorCodeConstants.ERROR_UPDATE_TIME_ILLEGAL.getMsg());
        } else if (ld.getYear() == now.getYear() && ld.getMonthValue() <= now.getMonthValue()) {
            // 错误
            throw new BusinessException(GlobalErrorCodeConstants.ERROR_UPDATE_TIME_ILLEGAL.getCode(), GlobalErrorCodeConstants.ERROR_UPDATE_TIME_ILLEGAL.getMsg());
        }

        if (StringUtils.hasText(addInfo.getOriginalTime())) {
            LocalDate od = null;
            try {
                od = LocalDate.parse(addInfo.getOriginalTime() + "1日", dateTimeFormatter);
            } catch (DateTimeParseException e) {
                // 日期处理不对， 返回异常
                throw new BusinessException(ErrorCodeEnumConst.DATE_FORMAT_ERROR);
            }
            // 如果时间<=当前月，则返回错误
            if (od.getYear() < now.getYear()) {
                // 错误
                throw new BusinessException(GlobalErrorCodeConstants.ERROR_UPDATE_EXISTS_CONF.getCode(), GlobalErrorCodeConstants.ERROR_UPDATE_EXISTS_CONF.getMsg());
            } else if (od.getYear() == now.getYear() && od.getMonthValue() <= now.getMonthValue()) {
                // 错误
                throw new BusinessException(GlobalErrorCodeConstants.ERROR_UPDATE_EXISTS_CONF.getCode(), GlobalErrorCodeConstants.ERROR_UPDATE_EXISTS_CONF.getMsg());
            }

            // 将原来的删了
            ProjectCnfTimePeriodRmDTO rmDTO = new ProjectCnfTimePeriodRmDTO();
            rmDTO.setProjectId(addInfo.getProjectId());
            rmDTO.setTime(addInfo.getOriginalTime());
            removeByProjIdAndTime(rmDTO);

            // 判断新的时间，是否重复
            List<ProjectCnfTimePeriodEntity> list = projectCnfTimePeriodMapper.selectList(new QueryWrapper<ProjectCnfTimePeriodEntity>().lambda()
                    .eq(ProjectCnfTimePeriodEntity::getProjectId, addInfo.getProjectId())
                    .eq(ProjectCnfTimePeriodEntity::getTenantId, TenantContext.getTenantId())
                    .eq(ProjectCnfTimePeriodEntity::getPeriodYear, String.valueOf(ld.getYear()))
                    .eq(ProjectCnfTimePeriodEntity::getPeriodMonth, String.valueOf(ld.getMonthValue())));
            if (!CollectionUtils.isEmpty(list)) {
                throw new BusinessException(GlobalErrorCodeConstants.ERROR_UPDATE_TIME_EXISTS.getCode(), GlobalErrorCodeConstants.ERROR_UPDATE_TIME_EXISTS.getMsg());
            }

        }

        List<ProjectCnfTimePeriodEntity> list = new ArrayList<>();
        LocalDate finalLd = ld;
        if (!CollectionUtils.isEmpty(addInfo.getPeakTimes())) {
            addInfo.getPeakTimes().stream().forEach(i -> {
//                if (!StringUtils.hasText(i.getTimeBegin())) {
//                    throw new BusinessException(GlobalErrorCodeConstants.ERROR_UPDATE_TIME_NULL.getCode(), GlobalErrorCodeConstants.ERROR_UPDATE_TIME_NULL.getMsg());
//                }
//                if (!StringUtils.hasText(i.getTimeEnd())) {
//                    throw new BusinessException(GlobalErrorCodeConstants.ERROR_UPDATE_TIME_NULL.getCode(), GlobalErrorCodeConstants.ERROR_UPDATE_TIME_NULL.getMsg());
//                }
                ProjectCnfTimePeriodEntity temp = new ProjectCnfTimePeriodEntity();
                temp.setProjectId(addInfo.getProjectId());
                temp.setCode(ElectricityBillType.PEAK.getType());
                temp.setPeriodYear(String.valueOf(finalLd.getYear()));
                temp.setPeriodMonth(String.valueOf(finalLd.getMonthValue()));
                temp.setName(ElectricityBillType.PEAK.getDesc());
                temp.setPrice(addInfo.getPeakPrice());
                temp.setTimeBegin(i.getTimeBegin());
                temp.setTimeEnd(i.getTimeEnd());
                temp.setTenantId(TenantContext.getTenantId());
                temp.setCreator(LoginUserUtil.getLoginUserId());
                temp.setUpdater(LoginUserUtil.getLoginUserId());
                temp.setCreateTime(LocalDateTime.now());
                temp.setUpdateTime(LocalDateTime.now());
                list.add(temp);
            });
        }
        if (!CollectionUtils.isEmpty(addInfo.getTipTimes())) {
            addInfo.getTipTimes().stream().forEach(i -> {
                if (null != i && null != i.getTimeBegin()) {
                    ProjectCnfTimePeriodEntity temp = new ProjectCnfTimePeriodEntity();
                    temp.setProjectId(addInfo.getProjectId());
                    temp.setCode(ElectricityBillType.TIP.getType());
                    temp.setPeriodYear(String.valueOf(finalLd.getYear()));
                    temp.setPeriodMonth(String.valueOf(finalLd.getMonthValue()));
                    temp.setName(ElectricityBillType.TIP.getDesc());
                    temp.setPrice(addInfo.getTipPrice());
                    temp.setTimeBegin(i.getTimeBegin());
                    temp.setTimeEnd(i.getTimeEnd());
                    temp.setTenantId(TenantContext.getTenantId());
                    temp.setCreator(LoginUserUtil.getLoginUserId());
                    temp.setUpdater(LoginUserUtil.getLoginUserId());
                    temp.setCreateTime(LocalDateTime.now());
                    temp.setUpdateTime(LocalDateTime.now());
                    list.add(temp);
                }
//                if (!StringUtils.hasText(i.getTimeEnd())) {
//                    throw new BusinessException(GlobalErrorCodeConstants.ERROR_UPDATE_TIME_NULL.getCode(), GlobalErrorCodeConstants.ERROR_UPDATE_TIME_NULL.getMsg());
//                }
            });
        }
        if (!CollectionUtils.isEmpty(addInfo.getFlatTimes())) {
            addInfo.getFlatTimes().stream().forEach(i -> {
//                if (!StringUtils.hasText(i.getTimeBegin())) {
//                    throw new BusinessException(GlobalErrorCodeConstants.ERROR_UPDATE_TIME_NULL.getCode(), GlobalErrorCodeConstants.ERROR_UPDATE_TIME_NULL.getMsg());
//                }
//                if (!StringUtils.hasText(i.getTimeEnd())) {
//                    throw new BusinessException(GlobalErrorCodeConstants.ERROR_UPDATE_TIME_NULL.getCode(), GlobalErrorCodeConstants.ERROR_UPDATE_TIME_NULL.getMsg());
//                }
                ProjectCnfTimePeriodEntity temp = new ProjectCnfTimePeriodEntity();
                temp.setProjectId(addInfo.getProjectId());
                temp.setCode(ElectricityBillType.FLAT.getType());
                temp.setPeriodYear(String.valueOf(finalLd.getYear()));
                temp.setPeriodMonth(String.valueOf(finalLd.getMonthValue()));
                temp.setName(ElectricityBillType.FLAT.getDesc());
                temp.setPrice(addInfo.getFlatPrice());
                temp.setTimeBegin(i.getTimeBegin());
                temp.setTimeEnd(i.getTimeEnd());
                temp.setTenantId(TenantContext.getTenantId());
                temp.setCreator(LoginUserUtil.getLoginUserId());
                temp.setUpdater(LoginUserUtil.getLoginUserId());
                temp.setCreateTime(LocalDateTime.now());
                temp.setUpdateTime(LocalDateTime.now());
                list.add(temp);
            });
        }
        if (!CollectionUtils.isEmpty(addInfo.getValleyTimes())) {
            addInfo.getValleyTimes().stream().forEach(i -> {
//                if (!StringUtils.hasText(i.getTimeBegin())) {
//                    throw new BusinessException(GlobalErrorCodeConstants.ERROR_UPDATE_TIME_NULL.getCode(), GlobalErrorCodeConstants.ERROR_UPDATE_TIME_NULL.getMsg());
//                }
//                if (!StringUtils.hasText(i.getTimeEnd())) {
//                    throw new BusinessException(GlobalErrorCodeConstants.ERROR_UPDATE_TIME_NULL.getCode(), GlobalErrorCodeConstants.ERROR_UPDATE_TIME_NULL.getMsg());
//                }
                ProjectCnfTimePeriodEntity temp = new ProjectCnfTimePeriodEntity();
                temp.setProjectId(addInfo.getProjectId());
                temp.setCode(ElectricityBillType.VALLEY.getType());
                temp.setPeriodYear(String.valueOf(finalLd.getYear()));
                temp.setPeriodMonth(String.valueOf(finalLd.getMonthValue()));
                temp.setName(ElectricityBillType.VALLEY.getDesc());
                temp.setPrice(addInfo.getValleyPrice());
                temp.setTimeBegin(i.getTimeBegin());
                temp.setTimeEnd(i.getTimeEnd());
                temp.setTenantId(TenantContext.getTenantId());
                temp.setCreator(LoginUserUtil.getLoginUserId());
                temp.setUpdater(LoginUserUtil.getLoginUserId());
                temp.setCreateTime(LocalDateTime.now());
                temp.setUpdateTime(LocalDateTime.now());
                list.add(temp);
            });
        }
        if (!CollectionUtils.isEmpty(list)) {
            saveBatch(list);
        }
        return true;
    }

    @Override
    public void removeByProjIdAndTime(ProjectCnfTimePeriodRmDTO rmDTO) {
        LocalDate ld = null;
        try {
            ld = LocalDate.parse(rmDTO.getTime() + "1日", dateTimeFormatter);
        } catch (DateTimeParseException e) {
            // 日期处理不对， 返回异常
            throw new BusinessException(ErrorCodeEnumConst.DATE_FORMAT_ERROR);
        }
        LocalDate now = LocalDate.now();
        // 如果时间<=当前月，则返回错误
        if (ld.getYear() < now.getYear()) {
            // 错误
        } else if (ld.getYear() == now.getYear() && ld.getMonthValue() <= now.getMonthValue()) {
            // 错误
        }
        projectCnfTimePeriodMapper.delete(new QueryWrapper<ProjectCnfTimePeriodEntity>().lambda().eq(
                        ProjectCnfTimePeriodEntity::getPeriodMonth, String.valueOf(ld.getMonthValue()))
                .eq(ProjectCnfTimePeriodEntity::getPeriodYear, String.valueOf(ld.getYear()))
                .eq(ProjectCnfTimePeriodEntity::getProjectId, rmDTO.getProjectId())
                .eq(ProjectCnfTimePeriodEntity::getTenantId, TenantContext.getTenantId())
        );
    }

    /**
     * 封装查询的请求参数
     *
     * @param queryInfo 请求参数
     * @return sql查询参数封装
     */
    private LambdaQueryWrapper<ProjectCnfTimePeriodEntity> getCondition(ProjectCnfTimePeriodQueryDTO queryInfo) {
        LambdaQueryWrapper<ProjectCnfTimePeriodEntity> wrapper = new QueryWrapper<ProjectCnfTimePeriodEntity>().lambda().eq(ProjectCnfTimePeriodEntity::getDeleted, CommonConstant.DELETED_FLAG_NOT_DELETE);

        // 开始时间
        if (!StringUtils.isEmpty(queryInfo.getStartTime())) {
            long startTimeMillion = 0L;
            try {
                startTimeMillion = DateUtils
                        .parseDate(queryInfo.getStartTime() + " 00:00:00")
                        .getTime();
            } catch (Exception e) {
                log.error("查询参数错误，startTime不符合格式{}", queryInfo.getStartTime());
                throw new BusinessException(ErrorCodeEnumConst.DATE_FORMAT_ERROR.getCode(), ErrorCodeEnumConst.DATE_FORMAT_ERROR.getMessage());
            }
            wrapper.le(ProjectCnfTimePeriodEntity::getCreateTime, new Timestamp(startTimeMillion));
        }

        // 结束时间
        if (!StringUtils.isEmpty(queryInfo.getEndTime())) {
            long endTimeMillion = 0L;
            try {
                endTimeMillion = DateUtils
                        .parseDate(queryInfo.getEndTime() + " 23:59:59")
                        .getTime();
            } catch (Exception e) {
                log.error("查询参数错误，endTime不符合格式{}", queryInfo.getEndTime());
                throw new BusinessException(ErrorCodeEnumConst.DATE_FORMAT_ERROR.getCode(), ErrorCodeEnumConst.DATE_FORMAT_ERROR.getMessage());
            }
            wrapper.ge(ProjectCnfTimePeriodEntity::getCreateTime, new Timestamp(endTimeMillion));
        }
        // 分时配置id
        if (null != queryInfo.getId()) {
            wrapper.eq(ProjectCnfTimePeriodEntity::getId, queryInfo.getId());
        }
        // 项目ID
        if (!StringUtils.hasText(queryInfo.getProjectId())) {
            wrapper.like(ProjectCnfTimePeriodEntity::getProjectId, "%" + queryInfo.getProjectId() + "%");
        }
        // 时间段-年
        if (!StringUtils.hasText(queryInfo.getPeriodYear())) {
            wrapper.like(ProjectCnfTimePeriodEntity::getPeriodYear, "%" + queryInfo.getPeriodYear() + "%");
        }
        // 时间段-月
        if (!StringUtils.hasText(queryInfo.getPeriodMonth())) {
            wrapper.like(ProjectCnfTimePeriodEntity::getPeriodMonth, "%" + queryInfo.getPeriodMonth() + "%");
        }
        // 分时code
        if (!StringUtils.hasText(queryInfo.getCode())) {
            wrapper.like(ProjectCnfTimePeriodEntity::getCode, "%" + queryInfo.getCode() + "%");
        }
        // 分时name
        if (!StringUtils.hasText(queryInfo.getName())) {
            wrapper.like(ProjectCnfTimePeriodEntity::getName, "%" + queryInfo.getName() + "%");
        }
        // 取值时间-开始
        if (null != queryInfo.getTimeBegin()) {
            wrapper.eq(ProjectCnfTimePeriodEntity::getTimeBegin, queryInfo.getTimeBegin());
        }
        // 取值时间-结束
        if (null != queryInfo.getTimeEnd()) {
            wrapper.eq(ProjectCnfTimePeriodEntity::getTimeEnd, queryInfo.getTimeEnd());
        }
        // 电价
        if (null != queryInfo.getPrice()) {
            wrapper.eq(ProjectCnfTimePeriodEntity::getPrice, queryInfo.getPrice().doubleValue());
        }
        wrapper.orderByDesc(ProjectCnfTimePeriodEntity::getUpdateTime);
        return wrapper;
    }
}
