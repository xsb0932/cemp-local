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
import com.landleaf.energy.dal.mapper.ProjectSubareaDeviceMapper;
import com.landleaf.energy.domain.dto.ProjectSubareaDeviceAddDTO;
import com.landleaf.energy.domain.dto.ProjectSubareaDeviceQueryDTO;
import com.landleaf.energy.domain.entity.ProjectSubareaDeviceEntity;
import com.landleaf.energy.service.ProjectSubareaDeviceService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ProjectSubareaDeviceEntity对象的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-06-24
 */
@Service
@AllArgsConstructor
@Slf4j
public class ProjectSubareaDeviceServiceImpl extends ServiceImpl<ProjectSubareaDeviceMapper, ProjectSubareaDeviceEntity> implements ProjectSubareaDeviceService {

	/**
	 * 数据库操作句柄
	 */
	@Resource
	ProjectSubareaDeviceMapper projectSubareaDeviceMapper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ProjectSubareaDeviceAddDTO save(ProjectSubareaDeviceAddDTO addInfo) {
		ProjectSubareaDeviceEntity entity = new ProjectSubareaDeviceEntity();
		BeanUtil.copyProperties(addInfo, entity);
		if (null == entity.getDeleted()) {
			entity.setDeleted(CommonConstant.DELETED_FLAG_NOT_DELETE);
		}
		if (null == entity.getCreateTime()) {
			entity.setCreateTime(LocalDateTime.now());
		}
		int effectNum = projectSubareaDeviceMapper.insert(entity);
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
	public void update(ProjectSubareaDeviceAddDTO updateInfo) {
		ProjectSubareaDeviceEntity entity = projectSubareaDeviceMapper.selectById(updateInfo.getId());
		if (null == entity) {
			// 修改失败
		throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
		}
		BeanUtil.copyProperties(updateInfo, entity, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

		projectSubareaDeviceMapper.updateById(entity);
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
		projectSubareaDeviceMapper.updateIsDeleted(idList, isDeleted);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectSubareaDeviceEntity selectById(Long id) {
		 ProjectSubareaDeviceEntity entity = projectSubareaDeviceMapper.selectById(id);
		if (null == entity) {
			return null;
		}
		return CommonConstant.DELETED_FLAG_NOT_DELETE == entity.getDeleted().intValue() ? entity : null;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectSubareaDeviceEntity> list(ProjectSubareaDeviceQueryDTO queryInfo) {
		return projectSubareaDeviceMapper.selectList(getCondition(queryInfo));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IPage<ProjectSubareaDeviceEntity> page(ProjectSubareaDeviceQueryDTO queryInfo) {
		IPage<ProjectSubareaDeviceEntity> page = new Page<>(queryInfo.getPageNo(), queryInfo.getPageSize());
		page = projectSubareaDeviceMapper.selectPage(page, getCondition(queryInfo));
		return page;
	}

	/**
	 * 封装查询的请求参数
	 *
	 * @param queryInfo
	 *            请求参数
	 * @return sql查询参数封装
	 */
	private LambdaQueryWrapper<ProjectSubareaDeviceEntity> getCondition(ProjectSubareaDeviceQueryDTO queryInfo) {
		LambdaQueryWrapper<ProjectSubareaDeviceEntity> wrapper = new QueryWrapper<ProjectSubareaDeviceEntity>().lambda().eq(ProjectSubareaDeviceEntity::getDeleted, CommonConstant.DELETED_FLAG_NOT_DELETE);

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
			wrapper.le(ProjectSubareaDeviceEntity::getCreateTime, new Timestamp(startTimeMillion));
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
				wrapper.ge(ProjectSubareaDeviceEntity::getCreateTime, new Timestamp(endTimeMillion));
			}
					// id
			if (null != queryInfo.getId()) {
				wrapper.eq(ProjectSubareaDeviceEntity::getId, queryInfo.getId());
			}
						// 分区ID
			if (null != queryInfo.getSubareadId()) {
				wrapper.eq(ProjectSubareaDeviceEntity::getSubareadId, queryInfo.getSubareadId());
			}
						// 设备ID
			if (!StringUtils.hasText(queryInfo.getDeviceId())) {
				wrapper.like(ProjectSubareaDeviceEntity::getDeviceId, "%" + queryInfo.getDeviceId() + "%");
			}
						// 计算标志位1,-1
			if (!StringUtils.hasText(queryInfo.getComputeTag())) {
				wrapper.like(ProjectSubareaDeviceEntity::getComputeTag, "%" + queryInfo.getComputeTag() + "%");
			}
						// 租户ID
			if (null != queryInfo.getTenantId()) {
				wrapper.eq(ProjectSubareaDeviceEntity::getTenantId, queryInfo.getTenantId());
			}
					wrapper.orderByDesc(ProjectSubareaDeviceEntity::getUpdateTime);
		return wrapper;
	}
}
