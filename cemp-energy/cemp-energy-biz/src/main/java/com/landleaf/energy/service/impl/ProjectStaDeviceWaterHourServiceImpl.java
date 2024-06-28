package com.landleaf.energy.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import com.landleaf.comm.constance.CommonConstant;
import com.landleaf.comm.constance.ErrorCodeEnumConst;
import javax.annotation.Resource;
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
import com.landleaf.energy.dal.mapper.ProjectStaDeviceWaterHourMapper;
import com.landleaf.energy.domain.dto.ProjectStaDeviceWaterHourAddDTO;
import com.landleaf.energy.domain.dto.ProjectStaDeviceWaterHourQueryDTO;
import com.landleaf.energy.domain.entity.ProjectStaDeviceWaterHourEntity;
import com.landleaf.energy.service.ProjectStaDeviceWaterHourService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 统计表-设备指标-水表-统计小时的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-06-24
 */
@Service
@AllArgsConstructor
@Slf4j
public class ProjectStaDeviceWaterHourServiceImpl extends ServiceImpl<ProjectStaDeviceWaterHourMapper, ProjectStaDeviceWaterHourEntity> implements ProjectStaDeviceWaterHourService {

	/**
	 * 数据库操作句柄
	 */
	@Resource
	ProjectStaDeviceWaterHourMapper projectStaDeviceWaterHourMapper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ProjectStaDeviceWaterHourAddDTO save(ProjectStaDeviceWaterHourAddDTO addInfo) {
		ProjectStaDeviceWaterHourEntity entity = new ProjectStaDeviceWaterHourEntity();
		BeanUtil.copyProperties(addInfo, entity);
		if (null == entity.getDeleted()) {
			entity.setDeleted(CommonConstant.DELETED_FLAG_NOT_DELETE);
		}
		if (null == entity.getCreateTime()) {
			entity.setCreateTime(LocalDateTime.now());
		}
		int effectNum = projectStaDeviceWaterHourMapper.insert(entity);
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
	public void update(ProjectStaDeviceWaterHourAddDTO updateInfo) {
		ProjectStaDeviceWaterHourEntity entity = projectStaDeviceWaterHourMapper.selectById(updateInfo.getId());
		if (null == entity) {
			// 修改失败
		throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
		}
		BeanUtil.copyProperties(updateInfo, entity, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

		projectStaDeviceWaterHourMapper.updateById(entity);
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
		projectStaDeviceWaterHourMapper.updateIsDeleted(idList, isDeleted);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaDeviceWaterHourEntity selectById(Long id) {
		 ProjectStaDeviceWaterHourEntity entity = projectStaDeviceWaterHourMapper.selectById(id);
		if (null == entity) {
			return null;
		}
		return CommonConstant.DELETED_FLAG_NOT_DELETE == entity.getDeleted().intValue() ? entity : null;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaDeviceWaterHourEntity> list(ProjectStaDeviceWaterHourQueryDTO queryInfo) {
		return projectStaDeviceWaterHourMapper.selectList(getCondition(queryInfo));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IPage<ProjectStaDeviceWaterHourEntity> page(ProjectStaDeviceWaterHourQueryDTO queryInfo) {
		IPage<ProjectStaDeviceWaterHourEntity> page = new Page<>(queryInfo.getPageNo(), queryInfo.getPageSize());
		page = projectStaDeviceWaterHourMapper.selectPage(page, getCondition(queryInfo));
		return page;
	}

	/**
	 * 封装查询的请求参数
	 *
	 * @param queryInfo
	 *            请求参数
	 * @return sql查询参数封装
	 */
	private LambdaQueryWrapper<ProjectStaDeviceWaterHourEntity> getCondition(ProjectStaDeviceWaterHourQueryDTO queryInfo) {
		LambdaQueryWrapper<ProjectStaDeviceWaterHourEntity> wrapper = new QueryWrapper<ProjectStaDeviceWaterHourEntity>().lambda().eq(ProjectStaDeviceWaterHourEntity::getDeleted, CommonConstant.DELETED_FLAG_NOT_DELETE);

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
			wrapper.le(ProjectStaDeviceWaterHourEntity::getCreateTime, new Timestamp(startTimeMillion));
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
				wrapper.ge(ProjectStaDeviceWaterHourEntity::getCreateTime, new Timestamp(endTimeMillion));
			}
					// id
			if (null != queryInfo.getId()) {
				wrapper.eq(ProjectStaDeviceWaterHourEntity::getId, queryInfo.getId());
			}
						// 设备ID
			if (!StringUtils.hasText(queryInfo.getBizDeviceId())) {
				wrapper.like(ProjectStaDeviceWaterHourEntity::getBizDeviceId, "%" + queryInfo.getBizDeviceId() + "%");
			}
						// 产品ID
			if (!StringUtils.hasText(queryInfo.getBizProductId())) {
				wrapper.like(ProjectStaDeviceWaterHourEntity::getBizProductId, "%" + queryInfo.getBizProductId() + "%");
			}
						// 品类ID
			if (!StringUtils.hasText(queryInfo.getBizCategoryId())) {
				wrapper.like(ProjectStaDeviceWaterHourEntity::getBizCategoryId, "%" + queryInfo.getBizCategoryId() + "%");
			}
						// 项目ID
			if (!StringUtils.hasText(queryInfo.getBizProjectId())) {
				wrapper.like(ProjectStaDeviceWaterHourEntity::getBizProjectId, "%" + queryInfo.getBizProjectId() + "%");
			}
						// 项目代码
			if (!StringUtils.hasText(queryInfo.getProjectCode())) {
				wrapper.like(ProjectStaDeviceWaterHourEntity::getProjectCode, "%" + queryInfo.getProjectCode() + "%");
			}
						// 租户ID
			if (null != queryInfo.getTenantId()) {
				wrapper.eq(ProjectStaDeviceWaterHourEntity::getTenantId, queryInfo.getTenantId());
			}
						// 租户代码
			if (!StringUtils.hasText(queryInfo.getTenantCode())) {
				wrapper.like(ProjectStaDeviceWaterHourEntity::getTenantCode, "%" + queryInfo.getTenantCode() + "%");
			}
						// 统计-年
			if (!StringUtils.hasText(queryInfo.getYear())) {
				wrapper.like(ProjectStaDeviceWaterHourEntity::getYear, "%" + queryInfo.getYear() + "%");
			}
						// 统计-月
			if (!StringUtils.hasText(queryInfo.getMonth())) {
				wrapper.like(ProjectStaDeviceWaterHourEntity::getMonth, "%" + queryInfo.getMonth() + "%");
			}
						// 统计-日
			if (!StringUtils.hasText(queryInfo.getDay())) {
				wrapper.like(ProjectStaDeviceWaterHourEntity::getDay, "%" + queryInfo.getDay() + "%");
			}
						// 统计-小时
			if (!StringUtils.hasText(queryInfo.getHour())) {
				wrapper.like(ProjectStaDeviceWaterHourEntity::getHour, "%" + queryInfo.getHour() + "%");
			}
						// 用水量
			if (null != queryInfo.getWatermeterUsageTotal()) {
				wrapper.eq(ProjectStaDeviceWaterHourEntity::getWatermeterUsageTotal, queryInfo.getWatermeterUsageTotal().doubleValue());
			}
						// 统计时间
			if (null != queryInfo.getStaTime()) {
				wrapper.eq(ProjectStaDeviceWaterHourEntity::getStaTime, queryInfo.getStaTime());
			}
					wrapper.orderByDesc(ProjectStaDeviceWaterHourEntity::getUpdateTime);
		return wrapper;
	}
}
