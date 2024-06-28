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
import com.landleaf.energy.dal.mapper.ProjectStaDeviceGasDayMapper;
import com.landleaf.energy.domain.dto.ProjectStaDeviceGasDayAddDTO;
import com.landleaf.energy.domain.dto.ProjectStaDeviceGasDayQueryDTO;
import com.landleaf.energy.domain.entity.ProjectStaDeviceGasDayEntity;
import com.landleaf.energy.service.ProjectStaDeviceGasDayService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 统计表-设备指标-气类-统计天的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-06-24
 */
@Service
@AllArgsConstructor
@Slf4j
public class ProjectStaDeviceGasDayServiceImpl extends ServiceImpl<ProjectStaDeviceGasDayMapper, ProjectStaDeviceGasDayEntity> implements ProjectStaDeviceGasDayService {

	/**
	 * 数据库操作句柄
	 */
	@Resource
	ProjectStaDeviceGasDayMapper projectStaDeviceGasDayMapper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ProjectStaDeviceGasDayAddDTO save(ProjectStaDeviceGasDayAddDTO addInfo) {
		ProjectStaDeviceGasDayEntity entity = new ProjectStaDeviceGasDayEntity();
		BeanUtil.copyProperties(addInfo, entity);
		if (null == entity.getDeleted()) {
			entity.setDeleted(CommonConstant.DELETED_FLAG_NOT_DELETE);
		}
		if (null == entity.getCreateTime()) {
			entity.setCreateTime(LocalDateTime.now());
		}
		int effectNum = projectStaDeviceGasDayMapper.insert(entity);
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
	public void update(ProjectStaDeviceGasDayAddDTO updateInfo) {
		ProjectStaDeviceGasDayEntity entity = projectStaDeviceGasDayMapper.selectById(updateInfo.getId());
		if (null == entity) {
			// 修改失败
		throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
		}
		BeanUtil.copyProperties(updateInfo, entity, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

		projectStaDeviceGasDayMapper.updateById(entity);
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
		projectStaDeviceGasDayMapper.updateIsDeleted(idList, isDeleted);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaDeviceGasDayEntity selectById(Long id) {
		 ProjectStaDeviceGasDayEntity entity = projectStaDeviceGasDayMapper.selectById(id);
		if (null == entity) {
			return null;
		}
		return CommonConstant.DELETED_FLAG_NOT_DELETE == entity.getDeleted().intValue() ? entity : null;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaDeviceGasDayEntity> list(ProjectStaDeviceGasDayQueryDTO queryInfo) {
		return projectStaDeviceGasDayMapper.selectList(getCondition(queryInfo));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IPage<ProjectStaDeviceGasDayEntity> page(ProjectStaDeviceGasDayQueryDTO queryInfo) {
		IPage<ProjectStaDeviceGasDayEntity> page = new Page<>(queryInfo.getPageNo(), queryInfo.getPageSize());
		page = projectStaDeviceGasDayMapper.selectPage(page, getCondition(queryInfo));
		return page;
	}

	/**
	 * 封装查询的请求参数
	 *
	 * @param queryInfo
	 *            请求参数
	 * @return sql查询参数封装
	 */
	private LambdaQueryWrapper<ProjectStaDeviceGasDayEntity> getCondition(ProjectStaDeviceGasDayQueryDTO queryInfo) {
		LambdaQueryWrapper<ProjectStaDeviceGasDayEntity> wrapper = new QueryWrapper<ProjectStaDeviceGasDayEntity>().lambda().eq(ProjectStaDeviceGasDayEntity::getDeleted, CommonConstant.DELETED_FLAG_NOT_DELETE);

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
			wrapper.le(ProjectStaDeviceGasDayEntity::getCreateTime, new Timestamp(startTimeMillion));
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
				wrapper.ge(ProjectStaDeviceGasDayEntity::getCreateTime, new Timestamp(endTimeMillion));
			}
					// id
			if (null != queryInfo.getId()) {
				wrapper.eq(ProjectStaDeviceGasDayEntity::getId, queryInfo.getId());
			}
						// 设备ID
			if (!StringUtils.hasText(queryInfo.getBizDeviceId())) {
				wrapper.like(ProjectStaDeviceGasDayEntity::getBizDeviceId, "%" + queryInfo.getBizDeviceId() + "%");
			}
						// 产品ID
			if (!StringUtils.hasText(queryInfo.getBizProductId())) {
				wrapper.like(ProjectStaDeviceGasDayEntity::getBizProductId, "%" + queryInfo.getBizProductId() + "%");
			}
						// 品类ID
			if (!StringUtils.hasText(queryInfo.getBizCategoryId())) {
				wrapper.like(ProjectStaDeviceGasDayEntity::getBizCategoryId, "%" + queryInfo.getBizCategoryId() + "%");
			}
						// 项目ID
			if (!StringUtils.hasText(queryInfo.getBizProjectId())) {
				wrapper.like(ProjectStaDeviceGasDayEntity::getBizProjectId, "%" + queryInfo.getBizProjectId() + "%");
			}
						// 项目代码
			if (!StringUtils.hasText(queryInfo.getProjectCode())) {
				wrapper.like(ProjectStaDeviceGasDayEntity::getProjectCode, "%" + queryInfo.getProjectCode() + "%");
			}
						// 租户ID
			if (null != queryInfo.getTenantId()) {
				wrapper.eq(ProjectStaDeviceGasDayEntity::getTenantId, queryInfo.getTenantId());
			}
						// 租户代码
			if (!StringUtils.hasText(queryInfo.getTenantCode())) {
				wrapper.like(ProjectStaDeviceGasDayEntity::getTenantCode, "%" + queryInfo.getTenantCode() + "%");
			}
						// 统计-年
			if (!StringUtils.hasText(queryInfo.getYear())) {
				wrapper.like(ProjectStaDeviceGasDayEntity::getYear, "%" + queryInfo.getYear() + "%");
			}
						// 统计-月
			if (!StringUtils.hasText(queryInfo.getMonth())) {
				wrapper.like(ProjectStaDeviceGasDayEntity::getMonth, "%" + queryInfo.getMonth() + "%");
			}
						// 统计-日
			if (!StringUtils.hasText(queryInfo.getDay())) {
				wrapper.like(ProjectStaDeviceGasDayEntity::getDay, "%" + queryInfo.getDay() + "%");
			}
						// 用气量
			if (null != queryInfo.getGasmeterUsageTotal()) {
				wrapper.eq(ProjectStaDeviceGasDayEntity::getGasmeterUsageTotal, queryInfo.getGasmeterUsageTotal().doubleValue());
			}
						// 统计时间
			if (null != queryInfo.getStaTime()) {
				wrapper.eq(ProjectStaDeviceGasDayEntity::getStaTime, queryInfo.getStaTime());
			}
					wrapper.orderByDesc(ProjectStaDeviceGasDayEntity::getUpdateTime);
		return wrapper;
	}
}
