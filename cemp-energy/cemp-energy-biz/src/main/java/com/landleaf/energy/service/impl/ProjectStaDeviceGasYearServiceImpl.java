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
import com.landleaf.energy.dal.mapper.ProjectStaDeviceGasYearMapper;
import com.landleaf.energy.domain.dto.ProjectStaDeviceGasYearAddDTO;
import com.landleaf.energy.domain.dto.ProjectStaDeviceGasYearQueryDTO;
import com.landleaf.energy.domain.entity.ProjectStaDeviceGasYearEntity;
import com.landleaf.energy.service.ProjectStaDeviceGasYearService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 统计表-设备指标-气类-统计年的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-06-24
 */
@Service
@AllArgsConstructor
@Slf4j
public class ProjectStaDeviceGasYearServiceImpl extends ServiceImpl<ProjectStaDeviceGasYearMapper, ProjectStaDeviceGasYearEntity> implements ProjectStaDeviceGasYearService {

	/**
	 * 数据库操作句柄
	 */
	@Resource
	ProjectStaDeviceGasYearMapper projectStaDeviceGasYearMapper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ProjectStaDeviceGasYearAddDTO save(ProjectStaDeviceGasYearAddDTO addInfo) {
		ProjectStaDeviceGasYearEntity entity = new ProjectStaDeviceGasYearEntity();
		BeanUtil.copyProperties(addInfo, entity);
		if (null == entity.getDeleted()) {
			entity.setDeleted(CommonConstant.DELETED_FLAG_NOT_DELETE);
		}
		if (null == entity.getCreateTime()) {
			entity.setCreateTime(LocalDateTime.now());
		}
		int effectNum = projectStaDeviceGasYearMapper.insert(entity);
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
	public void update(ProjectStaDeviceGasYearAddDTO updateInfo) {
		ProjectStaDeviceGasYearEntity entity = projectStaDeviceGasYearMapper.selectById(updateInfo.getId());
		if (null == entity) {
			// 修改失败
		throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
		}
		BeanUtil.copyProperties(updateInfo, entity, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

		projectStaDeviceGasYearMapper.updateById(entity);
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
		projectStaDeviceGasYearMapper.updateIsDeleted(idList, isDeleted);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaDeviceGasYearEntity selectById(Long id) {
		 ProjectStaDeviceGasYearEntity entity = projectStaDeviceGasYearMapper.selectById(id);
		if (null == entity) {
			return null;
		}
		return CommonConstant.DELETED_FLAG_NOT_DELETE == entity.getDeleted().intValue() ? entity : null;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaDeviceGasYearEntity> list(ProjectStaDeviceGasYearQueryDTO queryInfo) {
		return projectStaDeviceGasYearMapper.selectList(getCondition(queryInfo));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IPage<ProjectStaDeviceGasYearEntity> page(ProjectStaDeviceGasYearQueryDTO queryInfo) {
		IPage<ProjectStaDeviceGasYearEntity> page = new Page<>(queryInfo.getPageNo(), queryInfo.getPageSize());
		page = projectStaDeviceGasYearMapper.selectPage(page, getCondition(queryInfo));
		return page;
	}

	/**
	 * 封装查询的请求参数
	 *
	 * @param queryInfo
	 *            请求参数
	 * @return sql查询参数封装
	 */
	private LambdaQueryWrapper<ProjectStaDeviceGasYearEntity> getCondition(ProjectStaDeviceGasYearQueryDTO queryInfo) {
		LambdaQueryWrapper<ProjectStaDeviceGasYearEntity> wrapper = new QueryWrapper<ProjectStaDeviceGasYearEntity>().lambda().eq(ProjectStaDeviceGasYearEntity::getDeleted, CommonConstant.DELETED_FLAG_NOT_DELETE);

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
			wrapper.le(ProjectStaDeviceGasYearEntity::getCreateTime, new Timestamp(startTimeMillion));
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
				wrapper.ge(ProjectStaDeviceGasYearEntity::getCreateTime, new Timestamp(endTimeMillion));
			}
					// id
			if (null != queryInfo.getId()) {
				wrapper.eq(ProjectStaDeviceGasYearEntity::getId, queryInfo.getId());
			}
						// 设备ID
			if (!StringUtils.hasText(queryInfo.getBizDeviceId())) {
				wrapper.like(ProjectStaDeviceGasYearEntity::getBizDeviceId, "%" + queryInfo.getBizDeviceId() + "%");
			}
						// 产品ID
			if (!StringUtils.hasText(queryInfo.getBizProductId())) {
				wrapper.like(ProjectStaDeviceGasYearEntity::getBizProductId, "%" + queryInfo.getBizProductId() + "%");
			}
						// 品类ID
			if (!StringUtils.hasText(queryInfo.getBizCategoryId())) {
				wrapper.like(ProjectStaDeviceGasYearEntity::getBizCategoryId, "%" + queryInfo.getBizCategoryId() + "%");
			}
						// 项目ID
			if (!StringUtils.hasText(queryInfo.getBizProjectId())) {
				wrapper.like(ProjectStaDeviceGasYearEntity::getBizProjectId, "%" + queryInfo.getBizProjectId() + "%");
			}
						// 项目代码
			if (!StringUtils.hasText(queryInfo.getProjectCode())) {
				wrapper.like(ProjectStaDeviceGasYearEntity::getProjectCode, "%" + queryInfo.getProjectCode() + "%");
			}
						// 租户ID
			if (null != queryInfo.getTenantId()) {
				wrapper.eq(ProjectStaDeviceGasYearEntity::getTenantId, queryInfo.getTenantId());
			}
						// 租户代码
			if (!StringUtils.hasText(queryInfo.getTenantCode())) {
				wrapper.like(ProjectStaDeviceGasYearEntity::getTenantCode, "%" + queryInfo.getTenantCode() + "%");
			}
						// 统计-年
			if (!StringUtils.hasText(queryInfo.getYear())) {
				wrapper.like(ProjectStaDeviceGasYearEntity::getYear, "%" + queryInfo.getYear() + "%");
			}
						// 用气量
			if (null != queryInfo.getGasmeterUsageTotal()) {
				wrapper.eq(ProjectStaDeviceGasYearEntity::getGasmeterUsageTotal, queryInfo.getGasmeterUsageTotal().doubleValue());
			}
						// 统计时间
			if (null != queryInfo.getStaTime()) {
				wrapper.eq(ProjectStaDeviceGasYearEntity::getStaTime, queryInfo.getStaTime());
			}
					wrapper.orderByDesc(ProjectStaDeviceGasYearEntity::getUpdateTime);
		return wrapper;
	}
}
