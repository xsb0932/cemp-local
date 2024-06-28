package com.landleaf.energy.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.constance.CommonConstant;
import com.landleaf.comm.constance.ErrorCodeEnumConst;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.energy.domain.dto.SubitemRelationDevicesDTO;
import com.landleaf.energy.domain.entity.ProjectCnfSubitemEntity;
import com.landleaf.energy.domain.entity.ProjectCnfWaterFeeEntity;
import com.landleaf.energy.domain.entity.ProjectSubitemDeviceEntity;
import com.landleaf.energy.domain.vo.ProjectCnfGasFeeVO;
import com.landleaf.energy.domain.vo.ProjectCnfWaterFeeVO;
import com.landleaf.energy.service.ProjectCnfSubitemService;
import com.landleaf.energy.service.ProjectSubitemDeviceService;
import com.landleaf.monitor.api.MonitorApi;
import com.landleaf.monitor.dto.DeviceMonitorVO;
import com.landleaf.redis.constance.DictConstance;
import com.landleaf.redis.dict.DictUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.landleaf.comm.exception.BusinessException;
import com.landleaf.energy.dal.mapper.ProjectCnfGasFeeMapper;
import com.landleaf.energy.domain.dto.ProjectCnfGasFeeAddDTO;
import com.landleaf.energy.domain.dto.ProjectCnfGasFeeQueryDTO;
import com.landleaf.energy.domain.entity.ProjectCnfGasFeeEntity;
import com.landleaf.energy.service.ProjectCnfGasFeeService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 燃气费用配置表的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-07-04
 */
@Service
@AllArgsConstructor
@Slf4j
public class ProjectCnfGasFeeServiceImpl extends ServiceImpl<ProjectCnfGasFeeMapper, ProjectCnfGasFeeEntity> implements ProjectCnfGasFeeService {

	/**
	 * 数据库操作句柄
	 */
	private final ProjectCnfGasFeeMapper projectCnfGasFeeMapper;


	/**
	 * 分项的设备配置
	 */
	private final ProjectSubitemDeviceService projectSubitemDeviceServiceImpl;

	private final ProjectCnfSubitemService projectCnfSubitemServiceImpl;

	/**
	 * 字典
	 */
	private final DictUtils dictUtils;

	/**
	 * 设备监听
	 */
	private final MonitorApi monitorApi;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ProjectCnfGasFeeAddDTO save(ProjectCnfGasFeeAddDTO addInfo) {
		ProjectCnfGasFeeEntity entity = new ProjectCnfGasFeeEntity();
		BeanUtil.copyProperties(addInfo, entity);
		if (null == entity.getDeleted()) {
			entity.setDeleted(CommonConstant.DELETED_FLAG_NOT_DELETE);
		}
		if (null == entity.getCreateTime()) {
			entity.setCreateTime(LocalDateTime.now());
		}
		int effectNum = projectCnfGasFeeMapper.insert(entity);
		if (0 == effectNum) {
			// 插入失败
		throw new BusinessException(ErrorCodeEnumConst.DATA_INSERT_ERROR.getCode(), ErrorCodeEnumConst.DATA_INSERT_ERROR.getMessage());
		}
		BeanUtil.copyProperties(entity, addInfo);

		// 根据code，删除设备
		TenantContext.setIgnore(true);
		projectSubitemDeviceServiceImpl.rmAllDeviceByKpiCode(addInfo.getProjectId(), "project.gas.usage.total");
		TenantContext.setIgnore(false);
		// 获取subitemId
		ProjectCnfSubitemEntity subitemEntity = new ProjectCnfSubitemEntity();
		subitemEntity.setProjectId(addInfo.getProjectId());
		subitemEntity.setKpiSubtype("gasAll");
		subitemEntity.setName("项目总用气");
		subitemEntity.setKpiType("气");
		subitemEntity.setKpiTypeCode("3");
		Long subitemId = projectCnfSubitemServiceImpl.queryIdByKpiCode("project.gas.usage.total", subitemEntity);
		// 根据code,插入设备配置
		if (!CollectionUtils.isEmpty(addInfo.getDeviceList())) {
			List<ProjectSubitemDeviceEntity> deviceList = new ArrayList<>();
			ProjectSubitemDeviceEntity deviceAddInfo;
			for (SubitemRelationDevicesDTO temp : addInfo.getDeviceList()) {
				deviceAddInfo = new ProjectSubitemDeviceEntity();
				deviceAddInfo.setDeviceId(temp.getBizDeviceId());
				deviceAddInfo.setDeviceName(temp.getDeviceName());
				deviceAddInfo.setComputeTag(temp.getComputerTag());
				deviceAddInfo.setSubitemId(subitemId);
				deviceList.add(deviceAddInfo);
			}
			projectSubitemDeviceServiceImpl.saveBatch(deviceList);
		}
		return addInfo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(ProjectCnfGasFeeAddDTO updateInfo) {
		ProjectCnfGasFeeEntity entity = projectCnfGasFeeMapper.selectById(updateInfo.getId());
		if (null == entity) {
			// 修改失败
		throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
		}
		BeanUtil.copyProperties(updateInfo, entity, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

		projectCnfGasFeeMapper.updateById(entity);

		// 根据code，删除设备
		TenantContext.setIgnore(true);
		projectSubitemDeviceServiceImpl.rmAllDeviceByKpiCode(updateInfo.getProjectId(), "project.gas.usage.total");
		TenantContext.setIgnore(false);
		// 获取subitemId
		ProjectCnfSubitemEntity subitemEntity = new ProjectCnfSubitemEntity();
		subitemEntity.setProjectId(updateInfo.getProjectId());
		subitemEntity.setKpiSubtype("gasAll");
		subitemEntity.setName("项目总用气");
		subitemEntity.setKpiType("气");
		subitemEntity.setKpiTypeCode("3");
		Long subitemId = projectCnfSubitemServiceImpl.queryIdByKpiCode("project.gas.usage.total", subitemEntity);
		// 根据code,插入设备配置
		if (!CollectionUtils.isEmpty(updateInfo.getDeviceList())) {
			List<ProjectSubitemDeviceEntity> deviceList = new ArrayList<>();
			ProjectSubitemDeviceEntity deviceAddInfo;
			for (SubitemRelationDevicesDTO temp : updateInfo.getDeviceList()) {
				deviceAddInfo = new ProjectSubitemDeviceEntity();
				deviceAddInfo.setDeviceId(temp.getBizDeviceId());
				deviceAddInfo.setDeviceName(temp.getDeviceName());
				deviceAddInfo.setComputeTag(temp.getComputerTag());
				deviceAddInfo.setSubitemId(subitemId);
				deviceList.add(deviceAddInfo);
			}
			projectSubitemDeviceServiceImpl.saveBatch(deviceList);
		}
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
		projectCnfGasFeeMapper.updateIsDeleted(idList, isDeleted);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectCnfGasFeeEntity selectById(Long id) {
		 ProjectCnfGasFeeEntity entity = projectCnfGasFeeMapper.selectById(id);
		if (null == entity) {
			return null;
		}
		return CommonConstant.DELETED_FLAG_NOT_DELETE == entity.getDeleted().intValue() ? entity : null;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectCnfGasFeeEntity> list(ProjectCnfGasFeeQueryDTO queryInfo) {
		return projectCnfGasFeeMapper.selectList(getCondition(queryInfo));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IPage<ProjectCnfGasFeeEntity> page(ProjectCnfGasFeeQueryDTO queryInfo) {
		IPage<ProjectCnfGasFeeEntity> page = new Page<>(queryInfo.getPageNo(), queryInfo.getPageSize());
		page = projectCnfGasFeeMapper.selectPage(page, getCondition(queryInfo));
		return page;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectCnfGasFeeVO selectByBizProjectId(String bizProjectId) {
		List<ProjectCnfGasFeeEntity> list = projectCnfGasFeeMapper.selectList(new QueryWrapper<ProjectCnfGasFeeEntity>().lambda().eq(ProjectCnfGasFeeEntity::getProjectId, bizProjectId));
		ProjectCnfGasFeeVO result = null;
		if (!CollectionUtils.isEmpty(list)) {
			result = BeanUtil.copyProperties(list.get(0), ProjectCnfGasFeeVO.class);
			String desc = dictUtils.selectDictLabel(DictConstance.CHARGING_MODE, String.valueOf(result.getChargingMode()));
			result.setChargingModeName(desc);

			// 获取设备的信息
			TenantContext.setIgnore(true);
			List<ProjectSubitemDeviceEntity> deviceList = projectSubitemDeviceServiceImpl.queryAllDeviceByKpiCode(bizProjectId, "project.gas.usage.total",TenantContext.getTenantId());
			TenantContext.setIgnore(false);
			if (!CollectionUtils.isEmpty(deviceList)) {
				List<String> deviceIds = deviceList.stream().map(ProjectSubitemDeviceEntity::getDeviceId).collect(Collectors.toList());
				Response<List<DeviceMonitorVO>> resp = monitorApi.getDeviceListByBizIds(deviceIds);
				if (resp.isSuccess()) {
					List<DeviceMonitorVO> deviceVOList = resp.getResult();
					Map<String, String> deviceNameMap = deviceVOList.stream().collect(Collectors.toMap(DeviceMonitorVO::getBizDeviceId, DeviceMonitorVO::getName));
					List<SubitemRelationDevicesDTO> deviceResltList = deviceList.stream().map(i -> {
						SubitemRelationDevicesDTO temp = new SubitemRelationDevicesDTO();
						temp.setComputerTag(i.getComputeTag());
						temp.setDeviceName(deviceNameMap.get(i.getDeviceId()));
						temp.setBizDeviceId(i.getDeviceId());
						temp.setComputerTagDesc(dictUtils.selectDictLabel(DictConstance.COMPUTER_TAG, i.getComputeTag()));
						return temp;
					}).collect(Collectors.toList());
					result.setDeviceList(deviceResltList);
				}
			}
		}
		return result;
	}

	/**
	 * 封装查询的请求参数
	 *
	 * @param queryInfo
	 *            请求参数
	 * @return sql查询参数封装
	 */
	private LambdaQueryWrapper<ProjectCnfGasFeeEntity> getCondition(ProjectCnfGasFeeQueryDTO queryInfo) {
		LambdaQueryWrapper<ProjectCnfGasFeeEntity> wrapper = new QueryWrapper<ProjectCnfGasFeeEntity>().lambda().eq(ProjectCnfGasFeeEntity::getDeleted, CommonConstant.DELETED_FLAG_NOT_DELETE);

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
			wrapper.le(ProjectCnfGasFeeEntity::getCreateTime, new Timestamp(startTimeMillion));
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
				wrapper.ge(ProjectCnfGasFeeEntity::getCreateTime, new Timestamp(endTimeMillion));
			}
					// 燃气配置id
			if (null != queryInfo.getId()) {
				wrapper.eq(ProjectCnfGasFeeEntity::getId, queryInfo.getId());
			}
						// 项目ID
			if (!StringUtils.hasText(queryInfo.getProjectId())) {
				wrapper.like(ProjectCnfGasFeeEntity::getProjectId, "%" + queryInfo.getProjectId() + "%");
			}
						// 收费模式，0=>单一价格
			if (null != queryInfo.getChargingMode()) {
				wrapper.eq(ProjectCnfGasFeeEntity::getChargingMode, queryInfo.getChargingMode());
			}
						// 燃气单价
			if (null != queryInfo.getPrice()) {
				wrapper.eq(ProjectCnfGasFeeEntity::getPrice, queryInfo.getPrice().doubleValue());
			}
						// 租户id
			if (null != queryInfo.getTenantId()) {
				wrapper.eq(ProjectCnfGasFeeEntity::getTenantId, queryInfo.getTenantId());
			}
					wrapper.orderByDesc(ProjectCnfGasFeeEntity::getUpdateTime);
		return wrapper;
	}
}
