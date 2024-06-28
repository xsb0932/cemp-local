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
import com.landleaf.energy.dal.mapper.ProjectStaSubareaHourMapper;
import com.landleaf.energy.domain.dto.ProjectStaSubareaHourAddDTO;
import com.landleaf.energy.domain.dto.ProjectStaSubareaHourQueryDTO;
import com.landleaf.energy.domain.entity.ProjectStaSubareaHourEntity;
import com.landleaf.energy.service.ProjectStaSubareaHourService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ProjectStaSubareaHourEntity对象的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-06-24
 */
@Service
@AllArgsConstructor
@Slf4j
public class ProjectStaSubareaHourServiceImpl extends ServiceImpl<ProjectStaSubareaHourMapper, ProjectStaSubareaHourEntity> implements ProjectStaSubareaHourService {

	/**
	 * 数据库操作句柄
	 */
	@Resource
	ProjectStaSubareaHourMapper projectStaSubareaHourMapper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ProjectStaSubareaHourAddDTO save(ProjectStaSubareaHourAddDTO addInfo) {
		ProjectStaSubareaHourEntity entity = new ProjectStaSubareaHourEntity();
		BeanUtil.copyProperties(addInfo, entity);
		if (null == entity.getDeleted()) {
			entity.setDeleted(CommonConstant.DELETED_FLAG_NOT_DELETE);
		}
		if (null == entity.getCreateTime()) {
			entity.setCreateTime(LocalDateTime.now());
		}
		int effectNum = projectStaSubareaHourMapper.insert(entity);
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
	public void update(ProjectStaSubareaHourAddDTO updateInfo) {
		ProjectStaSubareaHourEntity entity = projectStaSubareaHourMapper.selectById(updateInfo.getId());
		if (null == entity) {
			// 修改失败
		throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
		}
		BeanUtil.copyProperties(updateInfo, entity, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

		projectStaSubareaHourMapper.updateById(entity);
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
		projectStaSubareaHourMapper.updateIsDeleted(idList, isDeleted);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaSubareaHourEntity selectById(Long id) {
		 ProjectStaSubareaHourEntity entity = projectStaSubareaHourMapper.selectById(id);
		if (null == entity) {
			return null;
		}
		return CommonConstant.DELETED_FLAG_NOT_DELETE == entity.getDeleted().intValue() ? entity : null;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaSubareaHourEntity> list(ProjectStaSubareaHourQueryDTO queryInfo) {
		return projectStaSubareaHourMapper.selectList(getCondition(queryInfo));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IPage<ProjectStaSubareaHourEntity> page(ProjectStaSubareaHourQueryDTO queryInfo) {
		IPage<ProjectStaSubareaHourEntity> page = new Page<>(queryInfo.getPageNo(), queryInfo.getPageSize());
		page = projectStaSubareaHourMapper.selectPage(page, getCondition(queryInfo));
		return page;
	}

	/**
	 * 封装查询的请求参数
	 *
	 * @param queryInfo
	 *            请求参数
	 * @return sql查询参数封装
	 */
	private LambdaQueryWrapper<ProjectStaSubareaHourEntity> getCondition(ProjectStaSubareaHourQueryDTO queryInfo) {
		LambdaQueryWrapper<ProjectStaSubareaHourEntity> wrapper = new QueryWrapper<ProjectStaSubareaHourEntity>().lambda().eq(ProjectStaSubareaHourEntity::getDeleted, CommonConstant.DELETED_FLAG_NOT_DELETE);

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
			wrapper.le(ProjectStaSubareaHourEntity::getCreateTime, new Timestamp(startTimeMillion));
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
				wrapper.ge(ProjectStaSubareaHourEntity::getCreateTime, new Timestamp(endTimeMillion));
			}
					// id
			if (null != queryInfo.getId()) {
				wrapper.eq(ProjectStaSubareaHourEntity::getId, queryInfo.getId());
			}
						// 指标CODE
			if (!StringUtils.hasText(queryInfo.getKpiCode())) {
				wrapper.like(ProjectStaSubareaHourEntity::getKpiCode, "%" + queryInfo.getKpiCode() + "%");
			}
						// 分区代码
			if (!StringUtils.hasText(queryInfo.getSubareaCode())) {
				wrapper.like(ProjectStaSubareaHourEntity::getSubareaCode, "%" + queryInfo.getSubareaCode() + "%");
			}
						// 分区名字
			if (!StringUtils.hasText(queryInfo.getSubareaName())) {
				wrapper.like(ProjectStaSubareaHourEntity::getSubareaName, "%" + queryInfo.getSubareaName() + "%");
			}
						// 项目ID
			if (!StringUtils.hasText(queryInfo.getBizProjectId())) {
				wrapper.like(ProjectStaSubareaHourEntity::getBizProjectId, "%" + queryInfo.getBizProjectId() + "%");
			}
						// 项目代码
			if (!StringUtils.hasText(queryInfo.getProjectCode())) {
				wrapper.like(ProjectStaSubareaHourEntity::getProjectCode, "%" + queryInfo.getProjectCode() + "%");
			}
						// 租户ID
			if (null != queryInfo.getTenantId()) {
				wrapper.eq(ProjectStaSubareaHourEntity::getTenantId, queryInfo.getTenantId());
			}
						// 租户代码
			if (!StringUtils.hasText(queryInfo.getTenantCode())) {
				wrapper.like(ProjectStaSubareaHourEntity::getTenantCode, "%" + queryInfo.getTenantCode() + "%");
			}
						// 项目名称
			if (!StringUtils.hasText(queryInfo.getProjectName())) {
				wrapper.like(ProjectStaSubareaHourEntity::getProjectName, "%" + queryInfo.getProjectName() + "%");
			}
						// 统计-年
			if (!StringUtils.hasText(queryInfo.getYear())) {
				wrapper.like(ProjectStaSubareaHourEntity::getYear, "%" + queryInfo.getYear() + "%");
			}
						// 统计-月
			if (!StringUtils.hasText(queryInfo.getMonth())) {
				wrapper.like(ProjectStaSubareaHourEntity::getMonth, "%" + queryInfo.getMonth() + "%");
			}
						// 统计-天
			if (!StringUtils.hasText(queryInfo.getDay())) {
				wrapper.like(ProjectStaSubareaHourEntity::getDay, "%" + queryInfo.getDay() + "%");
			}
						// 统计-小时
			if (!StringUtils.hasText(queryInfo.getHour())) {
				wrapper.like(ProjectStaSubareaHourEntity::getHour, "%" + queryInfo.getHour() + "%");
			}
						// 统计值
			if (null != queryInfo.getStaValue()) {
				wrapper.eq(ProjectStaSubareaHourEntity::getStaValue, queryInfo.getStaValue().doubleValue());
			}
						// 统计时间
			if (null != queryInfo.getStaTime()) {
				wrapper.eq(ProjectStaSubareaHourEntity::getStaTime, queryInfo.getStaTime());
			}
					wrapper.orderByDesc(ProjectStaSubareaHourEntity::getUpdateTime);
		return wrapper;
	}
}
