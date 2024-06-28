package com.landleaf.energy.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import com.landleaf.comm.constance.CommonConstant;
import com.landleaf.comm.constance.ErrorCodeEnumConst;
import com.landleaf.energy.domain.vo.ProjectCnfElectricityPriceVO;
import com.landleaf.energy.domain.vo.ProjectCnfTimePeriodVO;
import com.landleaf.energy.service.ProjectCnfTimePeriodService;
import com.landleaf.redis.constance.DictConstance;
import com.landleaf.redis.dict.DictUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.landleaf.comm.exception.BusinessException;
import com.landleaf.energy.dal.mapper.ProjectCnfElectricityPriceMapper;
import com.landleaf.energy.domain.dto.ProjectCnfElectricityPriceAddDTO;
import com.landleaf.energy.domain.dto.ProjectCnfElectricityPriceQueryDTO;
import com.landleaf.energy.domain.entity.ProjectCnfElectricityPriceEntity;
import com.landleaf.energy.service.ProjectCnfElectricityPriceService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 电费配置表的业务逻辑接口实现
 *
 * @author hebin
 * @since 2024-03-20
 */
@Service
@AllArgsConstructor
@Slf4j
public class ProjectCnfElectricityPriceServiceImpl extends ServiceImpl<ProjectCnfElectricityPriceMapper, ProjectCnfElectricityPriceEntity> implements ProjectCnfElectricityPriceService {

    /**
     * 数据库操作句柄
     */
    private final ProjectCnfElectricityPriceMapper projectCnfElectricityPriceMapper;

    private final ProjectCnfTimePeriodService projectCnfTimePeriodServiceImpl;

    private final DictUtils dictUtils;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectCnfElectricityPriceAddDTO save(ProjectCnfElectricityPriceAddDTO addInfo) {
        ProjectCnfElectricityPriceEntity entity = new ProjectCnfElectricityPriceEntity();
        BeanUtil.copyProperties(addInfo, entity);
        if (null == entity.getDeleted()) {
            entity.setDeleted(CommonConstant.DELETED_FLAG_NOT_DELETE);
        }
        if (null == entity.getCreateTime()) {
            entity.setCreateTime(LocalDateTime.now());
        }
        int effectNum = projectCnfElectricityPriceMapper.insert(entity);
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
    public void update(ProjectCnfElectricityPriceAddDTO updateInfo) {
        ProjectCnfElectricityPriceEntity entity = projectCnfElectricityPriceMapper.selectById(updateInfo.getId());
        if (null == entity) {
            // 修改失败
            throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
        }
        BeanUtil.copyProperties(updateInfo, entity, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

        projectCnfElectricityPriceMapper.updateById(entity);
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
        projectCnfElectricityPriceMapper.updateIsDeleted(idList, isDeleted);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectCnfElectricityPriceVO selectDetailById(String bizProjectId) {
        ProjectCnfElectricityPriceVO result = null;
        ProjectCnfElectricityPriceEntity entity = selectByBizProjId(bizProjectId);
        if (null == entity) {
            return null;
        }

        if (CommonConstant.DELETED_FLAG_NOT_DELETE == entity.getDeleted().intValue()) {
            result = BeanUtil.copyProperties(entity, ProjectCnfElectricityPriceVO.class);
            List<ProjectCnfTimePeriodVO> list = projectCnfTimePeriodServiceImpl.listByBizProjectId(bizProjectId);
            result.setProjectCnfTimePeriod(list);
            if (StringUtils.hasText(result.getType())) {
                result.setTypeDesc(dictUtils.selectDictLabel(DictConstance.ELECTRICITY_PRICE_TYPE, entity.getType()));
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public ProjectCnfElectricityPriceEntity selectByBizProjId(String bizProjectId) {
        return projectCnfElectricityPriceMapper.selectOne(new QueryWrapper<ProjectCnfElectricityPriceEntity>().lambda().eq(ProjectCnfElectricityPriceEntity::getProjectId, bizProjectId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProjectCnfElectricityPriceEntity> list(ProjectCnfElectricityPriceQueryDTO queryInfo) {
        return projectCnfElectricityPriceMapper.selectList(getCondition(queryInfo));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPage<ProjectCnfElectricityPriceEntity> page(ProjectCnfElectricityPriceQueryDTO queryInfo) {
        IPage<ProjectCnfElectricityPriceEntity> page = new Page<>(queryInfo.getPageNo(), queryInfo.getPageSize());
        page = projectCnfElectricityPriceMapper.selectPage(page, getCondition(queryInfo));
        return page;
    }

    /**
     * 封装查询的请求参数
     *
     * @param queryInfo 请求参数
     * @return sql查询参数封装
     */
    private LambdaQueryWrapper<ProjectCnfElectricityPriceEntity> getCondition(ProjectCnfElectricityPriceQueryDTO queryInfo) {
        LambdaQueryWrapper<ProjectCnfElectricityPriceEntity> wrapper = new QueryWrapper<ProjectCnfElectricityPriceEntity>().lambda().eq(ProjectCnfElectricityPriceEntity::getDeleted, CommonConstant.DELETED_FLAG_NOT_DELETE);

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
            wrapper.le(ProjectCnfElectricityPriceEntity::getCreateTime, new Timestamp(startTimeMillion));
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
            wrapper.ge(ProjectCnfElectricityPriceEntity::getCreateTime, new Timestamp(endTimeMillion));
        }
        // 分时配置id
        if (null != queryInfo.getId()) {
            wrapper.eq(ProjectCnfElectricityPriceEntity::getId, queryInfo.getId());
        }
        // 项目ID
        if (!StringUtils.hasText(queryInfo.getProjectId())) {
            wrapper.like(ProjectCnfElectricityPriceEntity::getProjectId, "%" + queryInfo.getProjectId() + "%");
        }
        // 电费类型，见字典electricity_price_type
        if (!StringUtils.hasText(queryInfo.getType())) {
            wrapper.like(ProjectCnfElectricityPriceEntity::getType, "%" + queryInfo.getType() + "%");
        }
        // 电价
        if (null != queryInfo.getPrice()) {
            wrapper.eq(ProjectCnfElectricityPriceEntity::getPrice, queryInfo.getPrice().doubleValue());
        }
        //  租户ID
        if (null != queryInfo.getTenantId()) {
            wrapper.eq(ProjectCnfElectricityPriceEntity::getTenantId, queryInfo.getTenantId());
        }
        wrapper.orderByDesc(ProjectCnfElectricityPriceEntity::getUpdateTime);
        return wrapper;
    }
}