package com.landleaf.energy.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import com.landleaf.comm.constance.CommonConstant;
import com.landleaf.comm.constance.ErrorCodeEnumConst;
import com.landleaf.energy.dal.mapper.DeviceMonitorMapper;
import com.landleaf.energy.domain.entity.*;
import com.landleaf.energy.domain.vo.SelectedVO;
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
import com.landleaf.energy.domain.dto.DeviceMonitorAddDTO;
import com.landleaf.energy.domain.dto.DeviceMonitorQueryDTO;
import com.landleaf.energy.service.DeviceMonitorService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * DeviceMonitorEntity对象的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-06-22
 */
@Service
@AllArgsConstructor
@Slf4j
public class DeviceMonitorServiceImpl extends ServiceImpl<DeviceMonitorMapper, DeviceMonitorEntity> implements DeviceMonitorService {

	/**
	 * 数据库操作句柄
	 */
	@Resource
	DeviceMonitorMapper deviceMonitorMapper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public DeviceMonitorAddDTO save(DeviceMonitorAddDTO addInfo) {
		DeviceMonitorEntity entity = new DeviceMonitorEntity();
		BeanUtil.copyProperties(addInfo, entity);
		if (null == entity.getDeleted()) {
			entity.setDeleted(CommonConstant.DELETED_FLAG_NOT_DELETE);
		}
		if (null == entity.getCreateTime()) {
			entity.setCreateTime(LocalDateTime.now());
		}
		int effectNum = deviceMonitorMapper.insert(entity);
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
	public void update(DeviceMonitorAddDTO updateInfo) {
		DeviceMonitorEntity entity = deviceMonitorMapper.selectById(updateInfo.getId());
		if (null == entity) {
			// 修改失败
		throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
		}
		BeanUtil.copyProperties(updateInfo, entity, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

		deviceMonitorMapper.updateById(entity);
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
		deviceMonitorMapper.updateIsDeleted(idList, isDeleted);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DeviceMonitorEntity selectById(Long id) {
		 DeviceMonitorEntity entity = deviceMonitorMapper.selectById(id);
		if (null == entity) {
			return null;
		}
		return CommonConstant.DELETED_FLAG_NOT_DELETE == entity.getDeleted().intValue() ? entity : null;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<DeviceMonitorEntity> list(DeviceMonitorQueryDTO queryInfo) {
		return deviceMonitorMapper.selectList(getCondition(queryInfo));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IPage<DeviceMonitorEntity> page(DeviceMonitorQueryDTO queryInfo) {
		IPage<DeviceMonitorEntity> page = new Page<>(queryInfo.getPageNo(), queryInfo.getPageSize());
		page = deviceMonitorMapper.selectPage(page, getCondition(queryInfo));
		return page;
	}

	@Override
	public List<SelectedVO> getDeviceByCategory(String bizCategoryId) {
		LambdaQueryWrapper<DeviceMonitorEntity> lqw = new LambdaQueryWrapper<>();
		lqw.eq(DeviceMonitorEntity::getBizCategoryId, bizCategoryId);
		List<DeviceMonitorEntity> devices =  this.baseMapper.selectList(lqw);
		return devices.stream().map(deviceMonitorEntity -> new SelectedVO(deviceMonitorEntity.getName(),deviceMonitorEntity.getBizDeviceId())).collect(Collectors.toList());
	}


	/**
	 * 封装查询的请求参数
	 *
	 * @param queryInfo
	 *            请求参数
	 * @return sql查询参数封装
	 */
	private LambdaQueryWrapper<DeviceMonitorEntity> getCondition(DeviceMonitorQueryDTO queryInfo) {
		LambdaQueryWrapper<DeviceMonitorEntity> wrapper = new QueryWrapper<DeviceMonitorEntity>().lambda().eq(DeviceMonitorEntity::getDeleted, CommonConstant.DELETED_FLAG_NOT_DELETE);

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
			wrapper.le(DeviceMonitorEntity::getCreateTime, new Timestamp(startTimeMillion));
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
				wrapper.ge(DeviceMonitorEntity::getCreateTime, new Timestamp(endTimeMillion));
			}
					// 设备id
			if (null != queryInfo.getId()) {
				wrapper.eq(DeviceMonitorEntity::getId, queryInfo.getId());
			}
						// 项目id（全局唯一id）
			if (!StringUtils.hasText(queryInfo.getBizProjectId())) {
				wrapper.like(DeviceMonitorEntity::getBizProjectId, "%" + queryInfo.getBizProjectId() + "%");
			}
						// 分区id（全局唯一id）
			if (!StringUtils.hasText(queryInfo.getBizAreaId())) {
				wrapper.like(DeviceMonitorEntity::getBizAreaId, "%" + queryInfo.getBizAreaId() + "%");
			}
						// 分区路径path
			if (!StringUtils.hasText(queryInfo.getAreaPath())) {
				wrapper.like(DeviceMonitorEntity::getAreaPath, "%" + queryInfo.getAreaPath() + "%");
			}
						// 设备id（全局唯一id）
			if (!StringUtils.hasText(queryInfo.getBizDeviceId())) {
				wrapper.like(DeviceMonitorEntity::getBizDeviceId, "%" + queryInfo.getBizDeviceId() + "%");
			}
						// 设备名称
			if (!StringUtils.hasText(queryInfo.getName())) {
				wrapper.like(DeviceMonitorEntity::getName, "%" + queryInfo.getName() + "%");
			}
						// 产品id（全局唯一id）
			if (!StringUtils.hasText(queryInfo.getBizProductId())) {
				wrapper.like(DeviceMonitorEntity::getBizProductId, "%" + queryInfo.getBizProductId() + "%");
			}
						// 品类id（全局唯一id）
			if (!StringUtils.hasText(queryInfo.getBizCategoryId())) {
				wrapper.like(DeviceMonitorEntity::getBizCategoryId, "%" + queryInfo.getBizCategoryId() + "%");
			}
						// 设备编码（校验唯一）
			if (!StringUtils.hasText(queryInfo.getCode())) {
				wrapper.like(DeviceMonitorEntity::getCode, "%" + queryInfo.getCode() + "%");
			}
						// 租户id
			if (null != queryInfo.getTenantId()) {
				wrapper.eq(DeviceMonitorEntity::getTenantId, queryInfo.getTenantId());
			}
					wrapper.orderByDesc(DeviceMonitorEntity::getUpdateTime);
		return wrapper;
	}
}
