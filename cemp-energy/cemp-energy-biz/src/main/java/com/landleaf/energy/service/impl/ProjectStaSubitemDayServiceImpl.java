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
import com.landleaf.energy.dal.mapper.ProjectStaSubitemDayMapper;
import com.landleaf.energy.domain.dto.ProjectStaSubitemDayAddDTO;
import com.landleaf.energy.domain.dto.ProjectStaSubitemDayQueryDTO;
import com.landleaf.energy.domain.entity.ProjectStaSubitemDayEntity;
import com.landleaf.energy.service.ProjectStaSubitemDayService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ProjectStaSubitemDayEntity对象的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-06-24
 */
@Service
@AllArgsConstructor
@Slf4j
public class ProjectStaSubitemDayServiceImpl extends ServiceImpl<ProjectStaSubitemDayMapper, ProjectStaSubitemDayEntity> implements ProjectStaSubitemDayService {

	/**
	 * 数据库操作句柄
	 */
	@Resource
	ProjectStaSubitemDayMapper projectStaSubitemDayMapper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ProjectStaSubitemDayAddDTO save(ProjectStaSubitemDayAddDTO addInfo) {
		ProjectStaSubitemDayEntity entity = new ProjectStaSubitemDayEntity();
		BeanUtil.copyProperties(addInfo, entity);
		if (null == entity.getDeleted()) {
			entity.setDeleted(CommonConstant.DELETED_FLAG_NOT_DELETE);
		}
		if (null == entity.getCreateTime()) {
			entity.setCreateTime(LocalDateTime.now());
		}
		int effectNum = projectStaSubitemDayMapper.insert(entity);
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
	public void update(ProjectStaSubitemDayAddDTO updateInfo) {
		ProjectStaSubitemDayEntity entity = projectStaSubitemDayMapper.selectById(updateInfo.getId());
		if (null == entity) {
			// 修改失败
		throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
		}
		BeanUtil.copyProperties(updateInfo, entity, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

		projectStaSubitemDayMapper.updateById(entity);
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
		projectStaSubitemDayMapper.updateIsDeleted(idList, isDeleted);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaSubitemDayEntity selectById(Long id) {
		 ProjectStaSubitemDayEntity entity = projectStaSubitemDayMapper.selectById(id);
		if (null == entity) {
			return null;
		}
		return CommonConstant.DELETED_FLAG_NOT_DELETE == entity.getDeleted().intValue() ? entity : null;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaSubitemDayEntity> list(ProjectStaSubitemDayQueryDTO queryInfo) {
		return projectStaSubitemDayMapper.selectList(getCondition(queryInfo));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IPage<ProjectStaSubitemDayEntity> page(ProjectStaSubitemDayQueryDTO queryInfo) {
		IPage<ProjectStaSubitemDayEntity> page = new Page<>(queryInfo.getPageNo(), queryInfo.getPageSize());
		page = projectStaSubitemDayMapper.selectPage(page, getCondition(queryInfo));
		return page;
	}

	/**
	 * 封装查询的请求参数
	 *
	 * @param queryInfo
	 *            请求参数
	 * @return sql查询参数封装
	 */
	private LambdaQueryWrapper<ProjectStaSubitemDayEntity> getCondition(ProjectStaSubitemDayQueryDTO queryInfo) {
		LambdaQueryWrapper<ProjectStaSubitemDayEntity> wrapper = new QueryWrapper<ProjectStaSubitemDayEntity>().lambda().eq(ProjectStaSubitemDayEntity::getDeleted, CommonConstant.DELETED_FLAG_NOT_DELETE);

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
			wrapper.le(ProjectStaSubitemDayEntity::getCreateTime, new Timestamp(startTimeMillion));
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
				wrapper.ge(ProjectStaSubitemDayEntity::getCreateTime, new Timestamp(endTimeMillion));
			}
					// id
			if (null != queryInfo.getId()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getId, queryInfo.getId());
			}
						// 项目ID
			if (!StringUtils.hasText(queryInfo.getBizProjectId())) {
				wrapper.like(ProjectStaSubitemDayEntity::getBizProjectId, "%" + queryInfo.getBizProjectId() + "%");
			}
						// 项目CODE
			if (!StringUtils.hasText(queryInfo.getProjectCode())) {
				wrapper.like(ProjectStaSubitemDayEntity::getProjectCode, "%" + queryInfo.getProjectCode() + "%");
			}
						// 租户ID
			if (null != queryInfo.getTenantId()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getTenantId, queryInfo.getTenantId());
			}
						// 租户CODE
			if (!StringUtils.hasText(queryInfo.getTenantCode())) {
				wrapper.like(ProjectStaSubitemDayEntity::getTenantCode, "%" + queryInfo.getTenantCode() + "%");
			}
						// 项目名称
			if (!StringUtils.hasText(queryInfo.getProjectName())) {
				wrapper.like(ProjectStaSubitemDayEntity::getProjectName, "%" + queryInfo.getProjectName() + "%");
			}
						// 统计-年
			if (!StringUtils.hasText(queryInfo.getYear())) {
				wrapper.like(ProjectStaSubitemDayEntity::getYear, "%" + queryInfo.getYear() + "%");
			}
						// 统计-月
			if (!StringUtils.hasText(queryInfo.getMonth())) {
				wrapper.like(ProjectStaSubitemDayEntity::getMonth, "%" + queryInfo.getMonth() + "%");
			}
						// 统计-天
			if (!StringUtils.hasText(queryInfo.getDay())) {
				wrapper.like(ProjectStaSubitemDayEntity::getDay, "%" + queryInfo.getDay() + "%");
			}
						// 总用气费
			if (null != queryInfo.getProjectGasFeeTotal()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectGasFeeTotal, queryInfo.getProjectGasFeeTotal().doubleValue());
			}
						// 总用气量
			if (null != queryInfo.getProjectGasUsageTotal()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectGasUsageTotal, queryInfo.getProjectGasUsageTotal().doubleValue());
			}
						// 总用水量
			if (null != queryInfo.getProjectWaterUsageTotal()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectWaterUsageTotal, queryInfo.getProjectWaterUsageTotal().doubleValue());
			}
						// 总计用水费
			if (null != queryInfo.getProjectWaterFeeWater()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectWaterFeeWater, queryInfo.getProjectWaterFeeWater().doubleValue());
			}
						// 总计污水处理费
			if (null != queryInfo.getProjectWaterFeeSewerage()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectWaterFeeSewerage, queryInfo.getProjectWaterFeeSewerage().doubleValue());
			}
						// 总计水费
			if (null != queryInfo.getProjectWaterFeeTotal()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectWaterFeeTotal, queryInfo.getProjectWaterFeeTotal().doubleValue());
			}
						// 全部负荷平用电量
			if (null != queryInfo.getProjectElectricityEnergyusageFlat()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectElectricityEnergyusageFlat, queryInfo.getProjectElectricityEnergyusageFlat().doubleValue());
			}
						// 全部负荷总用电量
			if (null != queryInfo.getProjectElectricityEnergyusageTotal()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectElectricityEnergyusageTotal, queryInfo.getProjectElectricityEnergyusageTotal().doubleValue());
			}
						// 全部负荷电度电费
			if (null != queryInfo.getProjectElectricityEnergyusagefeeTotal()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectElectricityEnergyusagefeeTotal, queryInfo.getProjectElectricityEnergyusagefeeTotal().doubleValue());
			}
						// 全部负荷尖用电量
			if (null != queryInfo.getProjectElectricityEnergyusageTip()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectElectricityEnergyusageTip, queryInfo.getProjectElectricityEnergyusageTip().doubleValue());
			}
						// 全部负荷谷用电量
			if (null != queryInfo.getProjectElectricityEnergyusageValley()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectElectricityEnergyusageValley, queryInfo.getProjectElectricityEnergyusageValley().doubleValue());
			}
						// 全部负荷峰用电量
			if (null != queryInfo.getProjectElectricityEnergyusagePeak()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectElectricityEnergyusagePeak, queryInfo.getProjectElectricityEnergyusagePeak().doubleValue());
			}
						// 电梯总用电量
			if (null != queryInfo.getProjectElectricitySubelevatorenergyTotal()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectElectricitySubelevatorenergyTotal, queryInfo.getProjectElectricitySubelevatorenergyTotal().doubleValue());
			}
						// 客房总用电量
			if (null != queryInfo.getProjectElectricitySubguestroomenergyTotal()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectElectricitySubguestroomenergyTotal, queryInfo.getProjectElectricitySubguestroomenergyTotal().doubleValue());
			}
						// 热水总用电量
			if (null != queryInfo.getProjectElectricitySubheatingwaterenergyTotal()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectElectricitySubheatingwaterenergyTotal, queryInfo.getProjectElectricitySubheatingwaterenergyTotal().doubleValue());
			}
						// 空调总用电量
			if (null != queryInfo.getProjectElectricitySubhavcenergyTotal()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectElectricitySubhavcenergyTotal, queryInfo.getProjectElectricitySubhavcenergyTotal().doubleValue());
			}
			if (null != queryInfo.getProjectElectricitySubpowersupplyenergyTotal()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectElectricitySubpowersupplyenergyTotal, queryInfo.getProjectElectricitySubpowersupplyenergyTotal().doubleValue());
			}

						// 其他总用电量
			if (null != queryInfo.getProjectElectricitySubothertypeTotal()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectElectricitySubothertypeTotal, queryInfo.getProjectElectricitySubothertypeTotal().doubleValue());
			}
						// 供水总用电量
			if (null != queryInfo.getProjectElectricitySubwatersupplyenergyTotal()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectElectricitySubwatersupplyenergyTotal, queryInfo.getProjectElectricitySubwatersupplyenergyTotal().doubleValue());
			}
						// 全部能耗标准煤当量
			if (null != queryInfo.getProjectCarbonTotalcoalTotal()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectCarbonTotalcoalTotal, queryInfo.getProjectCarbonTotalcoalTotal().doubleValue());
			}
						// 全部能耗粉尘当量
			if (null != queryInfo.getProjectCarbonTotaldustTotal()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectCarbonTotaldustTotal, queryInfo.getProjectCarbonTotaldustTotal().doubleValue());
			}
						// 全部能耗CO2当量
			if (null != queryInfo.getProjectCarbonTotalco2Total()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectCarbonTotalco2Total, queryInfo.getProjectCarbonTotalco2Total().doubleValue());
			}
						// 全部能耗SO2当量
			if (null != queryInfo.getProjectCarbonTotalso2Total()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectCarbonTotalso2Total, queryInfo.getProjectCarbonTotalso2Total().doubleValue());
			}
						// 气耗CO2当量
			if (null != queryInfo.getProjectCarbonGasusageco2Total()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectCarbonGasusageco2Total, queryInfo.getProjectCarbonGasusageco2Total().doubleValue());
			}
						// 气耗标准煤当量
			if (null != queryInfo.getProjectCarbonGasusagecoalTotal()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectCarbonGasusagecoalTotal, queryInfo.getProjectCarbonGasusagecoalTotal().doubleValue());
			}
						// 气耗粉尘当量
			if (null != queryInfo.getProjectCarbonGasusagedustTotal()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectCarbonGasusagedustTotal, queryInfo.getProjectCarbonGasusagedustTotal().doubleValue());
			}
						// 气耗SO2当量
			if (null != queryInfo.getProjectCarbonGasusageso2Total()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectCarbonGasusageso2Total, queryInfo.getProjectCarbonGasusageso2Total().doubleValue());
			}
						// 电耗SO2当量
			if (null != queryInfo.getProjectCarbonElectricityusageso2Total()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectCarbonElectricityusageso2Total, queryInfo.getProjectCarbonElectricityusageso2Total().doubleValue());
			}
						// 电耗粉尘当量
			if (null != queryInfo.getProjectCarbonElectricityusagedustTotal()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectCarbonElectricityusagedustTotal, queryInfo.getProjectCarbonElectricityusagedustTotal().doubleValue());
			}
						// 电耗CO2当量
			if (null != queryInfo.getProjectCarbonElectricityusageco2Total()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectCarbonElectricityusageco2Total, queryInfo.getProjectCarbonElectricityusageco2Total().doubleValue());
			}
						// 电耗标准煤当量
			if (null != queryInfo.getProjectCarbonElectricityusagecoalTotal()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectCarbonElectricityusagecoalTotal, queryInfo.getProjectCarbonElectricityusagecoalTotal().doubleValue());
			}
						// 水耗粉尘当量
			if (null != queryInfo.getProjectCarbonWaterusagedustTotal()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectCarbonWaterusagedustTotal, queryInfo.getProjectCarbonWaterusagedustTotal().doubleValue());
			}
						// 水耗标煤当量
			if (null != queryInfo.getProjectCarbonWaterusagecoalTotal()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectCarbonWaterusagecoalTotal, queryInfo.getProjectCarbonWaterusagecoalTotal().doubleValue());
			}
						// 水耗CO2当量
			if (null != queryInfo.getProjectCarbonWaterusageco2Total()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectCarbonWaterusageco2Total, queryInfo.getProjectCarbonWaterusageco2Total().doubleValue());
			}
						// 水耗SO2当量
			if (null != queryInfo.getProjectCarbonWaterusageso2Total()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getProjectCarbonWaterusageso2Total, queryInfo.getProjectCarbonWaterusageso2Total().doubleValue());
			}
						// 统计时间
			if (null != queryInfo.getStaTime()) {
				wrapper.eq(ProjectStaSubitemDayEntity::getStaTime, queryInfo.getStaTime());
			}
					wrapper.orderByDesc(ProjectStaSubitemDayEntity::getUpdateTime);
		return wrapper;
	}
}
