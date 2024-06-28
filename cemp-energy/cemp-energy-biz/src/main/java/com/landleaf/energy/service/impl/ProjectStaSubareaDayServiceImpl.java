package com.landleaf.energy.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import com.landleaf.comm.constance.CommonConstant;
import com.landleaf.comm.constance.ErrorCodeEnumConst;
import com.landleaf.energy.dal.mapper.ProjectStaSubareaDayMapper;
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
import com.landleaf.energy.domain.dto.ProjectStaSubareaDayAddDTO;
import com.landleaf.energy.domain.dto.ProjectStaSubareaDayQueryDTO;
import com.landleaf.energy.domain.entity.ProjectStaSubareaDayEntity;
import com.landleaf.energy.service.ProjectStaSubareaDayService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * ProjectStaSubareaDayEntity对象的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-06-25
 */
@Service
@AllArgsConstructor
@Slf4j
public class ProjectStaSubareaDayServiceImpl extends ServiceImpl<ProjectStaSubareaDayMapper, ProjectStaSubareaDayEntity> implements ProjectStaSubareaDayService {

	/**
	 * 数据库操作句柄
	 */
	@Resource
	ProjectStaSubareaDayMapper projectStaSubareaDayMapper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ProjectStaSubareaDayAddDTO save(ProjectStaSubareaDayAddDTO addInfo) {
		ProjectStaSubareaDayEntity entity = new ProjectStaSubareaDayEntity();
		BeanUtil.copyProperties(addInfo, entity);
		if (null == entity.getDeleted()) {
			entity.setDeleted(CommonConstant.DELETED_FLAG_NOT_DELETE);
		}
		if (null == entity.getCreateTime()) {
			entity.setCreateTime(LocalDateTime.now());
		}
		int effectNum = projectStaSubareaDayMapper.insert(entity);
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
	public void update(ProjectStaSubareaDayAddDTO updateInfo) {
		ProjectStaSubareaDayEntity entity = projectStaSubareaDayMapper.selectById(updateInfo.getId());
		if (null == entity) {
			// 修改失败
		throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
		}
		BeanUtil.copyProperties(updateInfo, entity, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

		projectStaSubareaDayMapper.updateById(entity);
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
		projectStaSubareaDayMapper.updateIsDeleted(idList, isDeleted);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaSubareaDayEntity selectById(Long id) {
		 ProjectStaSubareaDayEntity entity = projectStaSubareaDayMapper.selectById(id);
		if (null == entity) {
			return null;
		}
		return CommonConstant.DELETED_FLAG_NOT_DELETE == entity.getDeleted().intValue() ? entity : null;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaSubareaDayEntity> list(ProjectStaSubareaDayQueryDTO queryInfo) {
		return projectStaSubareaDayMapper.selectList(getCondition(queryInfo));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IPage<ProjectStaSubareaDayEntity> page(ProjectStaSubareaDayQueryDTO queryInfo) {
		IPage<ProjectStaSubareaDayEntity> page = new Page<>(queryInfo.getPageNo(), queryInfo.getPageSize());
		page = projectStaSubareaDayMapper.selectPage(page, getCondition(queryInfo));
		return page;
	}

	/**
	 * 封装查询的请求参数
	 *
	 * @param queryInfo
	 *            请求参数
	 * @return sql查询参数封装
	 */
	private LambdaQueryWrapper<ProjectStaSubareaDayEntity> getCondition(ProjectStaSubareaDayQueryDTO queryInfo) {
		LambdaQueryWrapper<ProjectStaSubareaDayEntity> wrapper = new QueryWrapper<ProjectStaSubareaDayEntity>().lambda().eq(ProjectStaSubareaDayEntity::getDeleted, CommonConstant.DELETED_FLAG_NOT_DELETE);

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
			wrapper.le(ProjectStaSubareaDayEntity::getCreateTime, new Timestamp(startTimeMillion));
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
				wrapper.ge(ProjectStaSubareaDayEntity::getCreateTime, new Timestamp(endTimeMillion));
			}
					// id
			if (null != queryInfo.getId()) {
				wrapper.eq(ProjectStaSubareaDayEntity::getId, queryInfo.getId());
			}
						// 指标CODE
			if (!StringUtils.hasText(queryInfo.getKpiCode())) {
				wrapper.like(ProjectStaSubareaDayEntity::getKpiCode, "%" + queryInfo.getKpiCode() + "%");
			}
						// 分区代码
			if (!StringUtils.hasText(queryInfo.getSubareaCode())) {
				wrapper.like(ProjectStaSubareaDayEntity::getSubareaCode, "%" + queryInfo.getSubareaCode() + "%");
			}
						// 分区名字
			if (!StringUtils.hasText(queryInfo.getSubareaName())) {
				wrapper.like(ProjectStaSubareaDayEntity::getSubareaName, "%" + queryInfo.getSubareaName() + "%");
			}
						// 项目ID
			if (!StringUtils.hasText(queryInfo.getBizProjectId())) {
				wrapper.like(ProjectStaSubareaDayEntity::getBizProjectId, "%" + queryInfo.getBizProjectId() + "%");
			}
						// 项目代码
			if (!StringUtils.hasText(queryInfo.getProjectCode())) {
				wrapper.like(ProjectStaSubareaDayEntity::getProjectCode, "%" + queryInfo.getProjectCode() + "%");
			}
						// 租户ID
			if (null != queryInfo.getTenantId()) {
				wrapper.eq(ProjectStaSubareaDayEntity::getTenantId, queryInfo.getTenantId());
			}
						// 租户代码
			if (!StringUtils.hasText(queryInfo.getTenantCode())) {
				wrapper.like(ProjectStaSubareaDayEntity::getTenantCode, "%" + queryInfo.getTenantCode() + "%");
			}
						// 项目名称
			if (!StringUtils.hasText(queryInfo.getProjectName())) {
				wrapper.like(ProjectStaSubareaDayEntity::getProjectName, "%" + queryInfo.getProjectName() + "%");
			}
						// 统计-年
			if (!StringUtils.hasText(queryInfo.getYear())) {
				wrapper.like(ProjectStaSubareaDayEntity::getYear, "%" + queryInfo.getYear() + "%");
			}
						// 统计-月
			if (!StringUtils.hasText(queryInfo.getMonth())) {
				wrapper.like(ProjectStaSubareaDayEntity::getMonth, "%" + queryInfo.getMonth() + "%");
			}
						// 统计-天
			if (!StringUtils.hasText(queryInfo.getDay())) {
				wrapper.like(ProjectStaSubareaDayEntity::getDay, "%" + queryInfo.getDay() + "%");
			}
						// 统计值
			if (null != queryInfo.getStaValue()) {
				wrapper.eq(ProjectStaSubareaDayEntity::getStaValue, queryInfo.getStaValue().doubleValue());
			}
						// 统计时间
			if (null != queryInfo.getStaTime()) {
				wrapper.eq(ProjectStaSubareaDayEntity::getStaTime, queryInfo.getStaTime());
			}
					wrapper.orderByDesc(ProjectStaSubareaDayEntity::getUpdateTime);
		return wrapper;
	}
}
