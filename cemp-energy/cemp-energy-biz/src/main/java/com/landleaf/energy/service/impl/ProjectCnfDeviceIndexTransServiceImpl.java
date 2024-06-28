package com.landleaf.energy.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import com.landleaf.comm.constance.CommonConstant;
import com.landleaf.comm.constance.ErrorCodeEnumConst;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.landleaf.comm.exception.BusinessException;
import com.landleaf.energy.dal.mapper.ProjectCnfDeviceIndexTransMapper;
import com.landleaf.energy.domain.dto.ProjectCnfDeviceIndexTransAddDTO;
import com.landleaf.energy.domain.dto.ProjectCnfDeviceIndexTransQueryDTO;
import com.landleaf.energy.domain.entity.ProjectCnfDeviceIndexTransEntity;
import com.landleaf.energy.service.ProjectCnfDeviceIndexTransService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * 设备品类和指标维度转换配置表的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-06-24
 */
@Service
@AllArgsConstructor
@Slf4j
public class ProjectCnfDeviceIndexTransServiceImpl extends ServiceImpl<ProjectCnfDeviceIndexTransMapper, ProjectCnfDeviceIndexTransEntity> implements ProjectCnfDeviceIndexTransService {

	/**
	 * 数据库操作句柄
	 */
	@Resource
	public ProjectCnfDeviceIndexTransMapper projectCnfDeviceIndexTransMapper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ProjectCnfDeviceIndexTransAddDTO save(ProjectCnfDeviceIndexTransAddDTO addInfo) {
		ProjectCnfDeviceIndexTransEntity entity = new ProjectCnfDeviceIndexTransEntity();
		BeanUtil.copyProperties(addInfo, entity);
		if (null == entity.getDeleted()) {
			entity.setDeleted(CommonConstant.DELETED_FLAG_NOT_DELETE);
		}
		if (null == entity.getCreateTime()) {
			entity.setCreateTime(LocalDateTime.now());
		}
		int effectNum = projectCnfDeviceIndexTransMapper.insert(entity);
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
	public void update(ProjectCnfDeviceIndexTransAddDTO updateInfo) {
		ProjectCnfDeviceIndexTransEntity entity = projectCnfDeviceIndexTransMapper.selectById(updateInfo.getId());
		if (null == entity) {
			// 修改失败
		throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
		}
		BeanUtil.copyProperties(updateInfo, entity, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

		projectCnfDeviceIndexTransMapper.updateById(entity);
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
		projectCnfDeviceIndexTransMapper.updateIsDeleted(idList, isDeleted);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectCnfDeviceIndexTransEntity selectById(Long id) {
		 ProjectCnfDeviceIndexTransEntity entity = projectCnfDeviceIndexTransMapper.selectById(id);
		if (null == entity) {
			return null;
		}
		return CommonConstant.DELETED_FLAG_NOT_DELETE == entity.getDeleted().intValue() ? entity : null;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectCnfDeviceIndexTransEntity> list(ProjectCnfDeviceIndexTransQueryDTO queryInfo) {
		return projectCnfDeviceIndexTransMapper.selectList(getCondition(queryInfo));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IPage<ProjectCnfDeviceIndexTransEntity> page(ProjectCnfDeviceIndexTransQueryDTO queryInfo) {
		IPage<ProjectCnfDeviceIndexTransEntity> page = new Page<>(queryInfo.getPageNo(), queryInfo.getPageSize());
		page = projectCnfDeviceIndexTransMapper.selectPage(page, getCondition(queryInfo));
		return page;
	}

	/**
	 * 封装查询的请求参数
	 *
	 * @param queryInfo
	 *            请求参数
	 * @return sql查询参数封装
	 */
	private LambdaQueryWrapper<ProjectCnfDeviceIndexTransEntity> getCondition(ProjectCnfDeviceIndexTransQueryDTO queryInfo) {
		LambdaQueryWrapper<ProjectCnfDeviceIndexTransEntity> wrapper = new QueryWrapper<ProjectCnfDeviceIndexTransEntity>().lambda().eq(ProjectCnfDeviceIndexTransEntity::getDeleted, CommonConstant.DELETED_FLAG_NOT_DELETE);

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
			wrapper.le(ProjectCnfDeviceIndexTransEntity::getCreateTime, new Timestamp(startTimeMillion));
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
				wrapper.ge(ProjectCnfDeviceIndexTransEntity::getCreateTime, new Timestamp(endTimeMillion));
			}
					// id
			if (null != queryInfo.getId()) {
				wrapper.eq(ProjectCnfDeviceIndexTransEntity::getId, queryInfo.getId());
			}
						// 品类id
			if (!StringUtils.hasText(queryInfo.getBizCategoryId())) {
				wrapper.like(ProjectCnfDeviceIndexTransEntity::getBizCategoryId, "%" + queryInfo.getBizCategoryId() + "%");
			}
						// 品类代码
			if (!StringUtils.hasText(queryInfo.getBizCategoryCode())) {
				wrapper.like(ProjectCnfDeviceIndexTransEntity::getBizCategoryCode, "%" + queryInfo.getBizCategoryCode() + "%");
			}
						// 转换后的维度代码
			if (!StringUtils.hasText(queryInfo.getTransIndexCode())) {
				wrapper.like(ProjectCnfDeviceIndexTransEntity::getTransIndexCode, "%" + queryInfo.getTransIndexCode() + "%");
			}
						// 转换后的维度名称
			if (!StringUtils.hasText(queryInfo.getTransIndexName())) {
				wrapper.like(ProjectCnfDeviceIndexTransEntity::getTransIndexName, "%" + queryInfo.getTransIndexName() + "%");
			}
						// 项目ID
			if (!StringUtils.hasText(queryInfo.getBizProjectId())) {
				wrapper.like(ProjectCnfDeviceIndexTransEntity::getBizProjectId, "%" + queryInfo.getBizProjectId() + "%");
			}
						// 租户ID
			if (null != queryInfo.getTenantId()) {
				wrapper.eq(ProjectCnfDeviceIndexTransEntity::getTenantId, queryInfo.getTenantId());
			}
					wrapper.orderByDesc(ProjectCnfDeviceIndexTransEntity::getUpdateTime);
		return wrapper;
	}
}
