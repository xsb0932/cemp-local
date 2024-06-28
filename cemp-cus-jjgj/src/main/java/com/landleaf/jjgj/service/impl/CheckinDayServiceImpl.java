package com.landleaf.jjgj.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.landleaf.comm.constance.CommonConstant;
import com.landleaf.comm.constance.ErrorCodeEnumConst;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.sta.util.DateUtils;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.jjgj.dal.mapper.CheckinDayMapper;
import com.landleaf.jjgj.dal.mapper.CheckinMonthMapper;
import com.landleaf.jjgj.dal.mapper.ProjectMapper;
import com.landleaf.jjgj.domain.dto.CheckinDayAddDTO;
import com.landleaf.jjgj.domain.dto.CheckinDayOfExcel;
import com.landleaf.jjgj.domain.dto.CheckinDayQueryDTO;
import com.landleaf.jjgj.domain.entity.CheckinDayEntity;
import com.landleaf.jjgj.domain.entity.CheckinMonthEntity;
import com.landleaf.jjgj.domain.entity.ProjectEntity;
import com.landleaf.jjgj.service.CheckinDayService;
import com.landleaf.job.api.dto.JobRpcRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JjgjCheckinDayEntity对象的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-10-16
 */
@Service
@AllArgsConstructor
@Slf4j
public class CheckinDayServiceImpl extends ServiceImpl<CheckinDayMapper, CheckinDayEntity> implements CheckinDayService {

    /**
     * 数据库操作句柄
     */
    private final CheckinDayMapper checkinDayMapper;
    private final CheckinMonthMapper checkinMonthMapper;

    private final ProjectMapper projectMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CheckinDayAddDTO save(CheckinDayAddDTO addInfo) {
        CheckinDayEntity entity = new CheckinDayEntity();
        BeanUtil.copyProperties(addInfo, entity);
        if (null == entity.getDeleted()) {
            entity.setDeleted(CommonConstant.DELETED_FLAG_NOT_DELETE);
        }
        if (null == entity.getCreateTime()) {
            entity.setCreateTime(LocalDateTime.now());
        }

        if (checkinDayMapper.exists(new LambdaQueryWrapper<CheckinDayEntity>()
                .eq(CheckinDayEntity::getYear, addInfo.getYear())
                .eq(CheckinDayEntity::getMonth, addInfo.getMonth())
                .eq(CheckinDayEntity::getDay, addInfo.getDay())
                .eq(CheckinDayEntity::getDeleted, "0")
        )) {
            throw new BusinessException(ErrorCodeEnumConst.DATA_INSERT_ERROR.getCode(), "当天已存在统计数据.");
        }

        int effectNum = checkinDayMapper.insert(entity);
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
    public void update(CheckinDayAddDTO updateInfo) {
        CheckinDayEntity entity = checkinDayMapper.selectById(updateInfo.getId());
        if (null == entity) {
            // 修改失败
            throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
        }
        BeanUtil.copyProperties(updateInfo, entity, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

        checkinDayMapper.updateById(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateIsDeleted(String ids, Integer isDeleted) {
        String[] idArray = ids.split(",");
        List<Integer> idList = new ArrayList<Integer>();
        for (String id : idArray) {
            idList.add(Integer.valueOf(id));
        }
        checkinDayMapper.updateIsDeleted(idList, isDeleted);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CheckinDayEntity selectById(Integer id) {
        CheckinDayEntity entity = checkinDayMapper.selectById(id);
        if (null == entity) {
            return null;
        }
        return CommonConstant.DELETED_FLAG_NOT_DELETE == entity.getDeleted().intValue() ? entity : null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CheckinDayEntity> list(CheckinDayQueryDTO queryInfo) {
        return checkinDayMapper.selectList(getCondition(queryInfo));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPage<CheckinDayEntity> page(CheckinDayQueryDTO queryInfo) {
        IPage<CheckinDayEntity> page = new Page<>(queryInfo.getPageNo(), queryInfo.getPageSize());
        page = checkinDayMapper.selectPage(page, getCondition(queryInfo));
        return page;
    }

    @Override
    public List<String> importFile(String bizProjectId, MultipartFile file) throws IOException {
        TenantContext.setIgnore(true);
        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());

        reader.addHeaderAlias("日期", "time");
        reader.addHeaderAlias("入住数", "checkinNum");

        List<CheckinDayOfExcel> list = reader.readAll(CheckinDayOfExcel.class);

        if (CollectionUtils.isEmpty(list)) {
            // 空数据。直接返回
            return Lists.newArrayList();
        }
        List<String> errMsgList = new ArrayList<>();
        // format the param time, trans 2 yyyy-MM-dd
        list.forEach(i -> {
            String[] str = null;
            if (i.getTime().contains("-")) {
                str = i.getTime().split("-");
            } else {
                str = i.getTime().split("/");
            }
            str[2] = str[2].replace("00:00:00", "").trim();
            String year = str[0];
            String month = str[1];
            if (2 > month.length()) {
                month = "0" + month;
            }
            String day = str[2];
            if (2 > day.length()) {
                day = "0" + day;
            }
            i.setTime(year + "-" + month + "-" + day);
        });
        List<String> existsDate = checkinDayMapper.selectExistsDate(bizProjectId, list.stream().map(CheckinDayOfExcel::getTime).collect(Collectors.toList()));
        if (!CollectionUtils.isEmpty(existsDate)) {
            // 不为空，则直接返回错误
            return existsDate.stream().map(i -> {
                return "日期：" + i + "已存在入住信息";
            }).collect(Collectors.toList());
        }
        // 通过bizProjectId获取对应的project信息
        LambdaQueryWrapper<ProjectEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ProjectEntity::getBizProjectId, bizProjectId);
        List<ProjectEntity> projects = projectMapper.selectList(lqw);
        if (CollectionUtils.isEmpty(projects)) {
            return Lists.newArrayList();
        }

        ProjectEntity projectInto = projects.get(0);
        Long tenantId = TenantContext.getTenantId();
        List<CheckinDayEntity> entityList = new ArrayList<>();
        list.forEach(i -> {
            CheckinDayEntity entity = new CheckinDayEntity();
            entity.setBizProjectId(bizProjectId);
            entity.setProjectName(projectInto.getName());
            entity.setCheckinNum(i.getCheckinNum());
            String[] str = null;
            str = i.getTime().split("-");
            entity.setYear(str[0]);
            entity.setMonth(str[1]);
            entity.setDay(str[2]);
            entity.setStaTime(DateUtil.parse(String.format("%s-%s-%s", str[0], str[1], str[2]), DateUtils.LC_DT_FMT_DAY).toTimestamp());
            BigDecimal checkinRate = null;
            if (i.getCheckinNum().compareTo(BigDecimal.valueOf(102)) > 0) {
                checkinRate = new BigDecimal(100);
            } else if (i.getCheckinNum().compareTo(BigDecimal.ZERO) < 0) {
                checkinRate = new BigDecimal(0);
            } else {
                checkinRate = i.getCheckinNum().divide(new BigDecimal(1.02), 1, RoundingMode.UP);
            }
            entity.setCheckinRate(checkinRate);
            entity.setTenantId(tenantId);
            entityList.add(entity);
        });
        saveBatch(entityList);
        return Lists.newArrayList();
    }

    @Override
    @Transactional
    public void staMonth(YearMonth month, JobRpcRequest request) {
        LambdaQueryWrapper<CheckinDayEntity> qwd = new LambdaQueryWrapper<CheckinDayEntity>()
                .eq(CheckinDayEntity::getYear, String.valueOf(month.getYear()))
                .eq(CheckinDayEntity::getMonth, String.valueOf(month.getMonthValue()));
        List<CheckinDayEntity> monthTotal = checkinDayMapper.selectList(qwd);
        if (monthTotal != null && monthTotal.size() > 0) {
            BigDecimal rateTotal = monthTotal.stream().map(CheckinDayEntity::getCheckinRate).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal checkInTotal = monthTotal.stream().map(CheckinDayEntity::getCheckinNum).reduce(BigDecimal.ZERO, BigDecimal::add);
            LocalDateTime dt = LocalDateTime.of(month.getYear(), month.getMonthValue(), 1, 0, 0, 0);
            CheckinMonthEntity monthEntity = new CheckinMonthEntity();
            monthEntity.setYear(String.valueOf(month.getYear()))
                    .setMonth(String.valueOf(month.getMonthValue()))
                    .setCheckinRate(rateTotal.divide(BigDecimal.valueOf(monthTotal.size()), 2, RoundingMode.HALF_UP))
                    .setProjectName("锦江体验中心酒店")
                    .setBizProjectId("PJ00000001")
                    .setCheckinNum(checkInTotal)
                    .setStaTime(Timestamp.valueOf(dt));
            // modify, 为预防重复数据，先删后加
            checkinMonthMapper.delete(new QueryWrapper<CheckinMonthEntity>().lambda().eq(CheckinMonthEntity::getBizProjectId, "PJ00000001")
                    .eq(CheckinMonthEntity::getYear, String.valueOf(month.getYear())).eq(CheckinMonthEntity::getMonth, String.valueOf(month.getMonthValue())));
            checkinMonthMapper.insert(monthEntity);
        }
    }

    /**
     * 封装查询的请求参数
     *
     * @param queryInfo 请求参数
     * @return sql查询参数封装
     */
    private LambdaQueryWrapper<CheckinDayEntity> getCondition(CheckinDayQueryDTO queryInfo) {
        LambdaQueryWrapper<CheckinDayEntity> wrapper = new QueryWrapper<CheckinDayEntity>().lambda().eq(CheckinDayEntity::getDeleted, CommonConstant.DELETED_FLAG_NOT_DELETE);

        // 开始时间
        String startYear;
        String startMonth;
        String startDay;
        if (StringUtils.hasText(queryInfo.getStartTime())) {
            String[] startStr = queryInfo.getStartTime().split("-");
            startYear = startStr[0];
            startMonth = startStr[1];
            startDay = startStr[2];
//            wrapper.and(i -> i.or(j -> j.gt(CheckinDayEntity::getYear, startYear)).
//                    or(j -> j.eq(CheckinDayEntity::getYear, startYear).gt(CheckinDayEntity::getMonth, startMonth))
//                    .or(j -> j.eq(CheckinDayEntity::getYear, startYear).eq(CheckinDayEntity::getMonth, startMonth).ge(CheckinDayEntity::getDay, startDay)));
            wrapper.ge(CheckinDayEntity::getStaTime, LocalDate.of(Integer.parseInt(startYear), Integer.parseInt(startMonth), Integer.parseInt(startDay)));
        }

        // 结束时间
        String endYear;
        String endMonth;
        String endDay;
        if (StringUtils.hasText(queryInfo.getEndTime())) {
            String[] endStr = queryInfo.getEndTime().split("-");
            endYear = endStr[0];
            endMonth = endStr[1];
            endDay = endStr[2];
//            wrapper.and(i -> i.or(j -> j.lt(CheckinDayEntity::getYear, endYear)).
//                    or(j -> j.eq(CheckinDayEntity::getYear, endYear).lt(CheckinDayEntity::getMonth, endMonth))
//                    .or(j -> j.eq(CheckinDayEntity::getYear, endYear).eq(CheckinDayEntity::getMonth, endMonth).le(CheckinDayEntity::getDay, endDay)));
            wrapper.le(CheckinDayEntity::getStaTime, LocalDate.of(Integer.parseInt(endYear), Integer.parseInt(endMonth), Integer.parseInt(endDay)));
        }
        // 项目id
        if (StringUtils.hasText(queryInfo.getBizProjectId())) {
            wrapper.eq(CheckinDayEntity::getBizProjectId, queryInfo.getBizProjectId());
        }
        wrapper.orderByDesc(CheckinDayEntity::getUpdateTime);
        return wrapper;
    }
}
