package com.landleaf.energy.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import com.landleaf.comm.constance.CommonConstant;
import com.landleaf.comm.constance.ErrorCodeEnumConst;
import javax.annotation.Resource;

import com.landleaf.comm.util.servlet.LoginUserUtil;
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
import com.landleaf.energy.dal.mapper.ProjectSubitemDeviceMapper;
import com.landleaf.energy.domain.dto.ProjectSubitemDeviceAddDTO;
import com.landleaf.energy.domain.dto.ProjectSubitemDeviceQueryDTO;
import com.landleaf.energy.domain.entity.ProjectSubitemDeviceEntity;
import com.landleaf.energy.service.ProjectSubitemDeviceService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ProjectSubitemDeviceEntity对象的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-06-24
 */
@Service
@AllArgsConstructor
@Slf4j
public class ProjectSubitemDeviceServiceImpl extends ServiceImpl<ProjectSubitemDeviceMapper, ProjectSubitemDeviceEntity> implements ProjectSubitemDeviceService {

	/**
	 * 数据库操作句柄
	 */
	@Resource
	ProjectSubitemDeviceMapper projectSubitemDeviceMapper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ProjectSubitemDeviceAddDTO save(ProjectSubitemDeviceAddDTO addInfo) {
		ProjectSubitemDeviceEntity entity = new ProjectSubitemDeviceEntity();
		BeanUtil.copyProperties(addInfo, entity);
		if (null == entity.getDeleted()) {
			entity.setDeleted(CommonConstant.DELETED_FLAG_NOT_DELETE);
		}
		if (null == entity.getCreateTime()) {
			entity.setCreateTime(LocalDateTime.now());
		}
		int effectNum = projectSubitemDeviceMapper.insert(entity);
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
	public void update(ProjectSubitemDeviceAddDTO updateInfo) {
		ProjectSubitemDeviceEntity entity = projectSubitemDeviceMapper.selectById(updateInfo.getId());
		if (null == entity) {
			// 修改失败
		throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
		}
		BeanUtil.copyProperties(updateInfo, entity, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

		projectSubitemDeviceMapper.updateById(entity);
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
		projectSubitemDeviceMapper.updateIsDeleted(idList, isDeleted);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectSubitemDeviceEntity selectById(Long id) {
		 ProjectSubitemDeviceEntity entity = projectSubitemDeviceMapper.selectById(id);
		if (null == entity) {
			return null;
		}
		return CommonConstant.DELETED_FLAG_NOT_DELETE == entity.getDeleted().intValue() ? entity : null;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectSubitemDeviceEntity> list(ProjectSubitemDeviceQueryDTO queryInfo) {
		return projectSubitemDeviceMapper.selectList(getCondition(queryInfo));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IPage<ProjectSubitemDeviceEntity> page(ProjectSubitemDeviceQueryDTO queryInfo) {
		IPage<ProjectSubitemDeviceEntity> page = new Page<>(queryInfo.getPageNo(), queryInfo.getPageSize());
		page = projectSubitemDeviceMapper.selectPage(page, getCondition(queryInfo));
		return page;
	}

	@Override
	public List<ProjectSubitemDeviceEntity> queryAllDeviceByKpiCode(String bizProjectId, String code,Long tenantId) {
		return projectSubitemDeviceMapper.queryAllDeviceByKpiCode(bizProjectId, code,tenantId);
	}

	@Override
	public void rmAllDeviceByKpiCode(String projectId, String code) {
		projectSubitemDeviceMapper.rmAllDeviceByKpiCode(projectId, code, LoginUserUtil.getLoginUserId());
	}

	/**
	 * 封装查询的请求参数
	 *
	 * @param queryInfo
	 *            请求参数
	 * @return sql查询参数封装
	 */
	private LambdaQueryWrapper<ProjectSubitemDeviceEntity> getCondition(ProjectSubitemDeviceQueryDTO queryInfo) {
		LambdaQueryWrapper<ProjectSubitemDeviceEntity> wrapper = new QueryWrapper<ProjectSubitemDeviceEntity>().lambda().eq(ProjectSubitemDeviceEntity::getDeleted, CommonConstant.DELETED_FLAG_NOT_DELETE);

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
			wrapper.le(ProjectSubitemDeviceEntity::getCreateTime, new Timestamp(startTimeMillion));
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
				wrapper.ge(ProjectSubitemDeviceEntity::getCreateTime, new Timestamp(endTimeMillion));
			}
					// id
			if (null != queryInfo.getId()) {
				wrapper.eq(ProjectSubitemDeviceEntity::getId, queryInfo.getId());
			}
						// 分项ID
			if (null != queryInfo.getSubitemId()) {
				wrapper.eq(ProjectSubitemDeviceEntity::getSubitemId, queryInfo.getSubitemId());
			}
						// 设备ID
			if (!StringUtils.hasText(queryInfo.getDeviceId())) {
				wrapper.like(ProjectSubitemDeviceEntity::getDeviceId, "%" + queryInfo.getDeviceId() + "%");
			}
						// 设备名称
			if (!StringUtils.hasText(queryInfo.getDeviceName())) {
				wrapper.like(ProjectSubitemDeviceEntity::getDeviceName, "%" + queryInfo.getDeviceName() + "%");
			}
						// 计算标志位1,-1
			if (!StringUtils.hasText(queryInfo.getComputeTag())) {
				wrapper.like(ProjectSubitemDeviceEntity::getComputeTag, "%" + queryInfo.getComputeTag() + "%");
			}
						// 租户id
			if (null != queryInfo.getTenantId()) {
				wrapper.eq(ProjectSubitemDeviceEntity::getTenantId, queryInfo.getTenantId());
			}
					wrapper.orderByDesc(ProjectSubitemDeviceEntity::getUpdateTime);
		return wrapper;
	}
}
