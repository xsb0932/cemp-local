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
import com.landleaf.energy.dal.mapper.ProjectStaSubitemMonthMapper;
import com.landleaf.energy.domain.dto.ProjectStaSubitemMonthAddDTO;
import com.landleaf.energy.domain.dto.ProjectStaSubitemMonthQueryDTO;
import com.landleaf.energy.domain.entity.ProjectStaSubitemMonthEntity;
import com.landleaf.energy.service.ProjectStaSubitemMonthService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ProjectStaSubitemMonthEntity对象的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-06-24
 */
@Service
@AllArgsConstructor
@Slf4j
public class ProjectStaSubitemMonthServiceImpl extends ServiceImpl<ProjectStaSubitemMonthMapper, ProjectStaSubitemMonthEntity> implements ProjectStaSubitemMonthService {

	/**
	 * 数据库操作句柄
	 */
	@Resource
	ProjectStaSubitemMonthMapper projectStaSubitemMonthMapper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ProjectStaSubitemMonthAddDTO save(ProjectStaSubitemMonthAddDTO addInfo) {
		ProjectStaSubitemMonthEntity entity = new ProjectStaSubitemMonthEntity();
		BeanUtil.copyProperties(addInfo, entity);
		if (null == entity.getDeleted()) {
			entity.setDeleted(CommonConstant.DELETED_FLAG_NOT_DELETE);
		}
		if (null == entity.getCreateTime()) {
			entity.setCreateTime(LocalDateTime.now());
		}
		int effectNum = projectStaSubitemMonthMapper.insert(entity);
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
	public void update(ProjectStaSubitemMonthAddDTO updateInfo) {
		ProjectStaSubitemMonthEntity entity = projectStaSubitemMonthMapper.selectById(updateInfo.getId());
		if (null == entity) {
			// 修改失败
		throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
		}
		BeanUtil.copyProperties(updateInfo, entity, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

		projectStaSubitemMonthMapper.updateById(entity);
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
		projectStaSubitemMonthMapper.updateIsDeleted(idList, isDeleted);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaSubitemMonthEntity selectById(Long id) {
		 ProjectStaSubitemMonthEntity entity = projectStaSubitemMonthMapper.selectById(id);
		if (null == entity) {
			return null;
		}
		return CommonConstant.DELETED_FLAG_NOT_DELETE == entity.getDeleted().intValue() ? entity : null;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaSubitemMonthEntity> list(ProjectStaSubitemMonthQueryDTO queryInfo) {
		return projectStaSubitemMonthMapper.selectList(getCondition(queryInfo));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IPage<ProjectStaSubitemMonthEntity> page(ProjectStaSubitemMonthQueryDTO queryInfo) {
		IPage<ProjectStaSubitemMonthEntity> page = new Page<>(queryInfo.getPageNo(), queryInfo.getPageSize());
		page = projectStaSubitemMonthMapper.selectPage(page, getCondition(queryInfo));
		return page;
	}

	/**
	 * 封装查询的请求参数
	 *
	 * @param queryInfo
	 *            请求参数
	 * @return sql查询参数封装
	 */
	private LambdaQueryWrapper<ProjectStaSubitemMonthEntity> getCondition(ProjectStaSubitemMonthQueryDTO queryInfo) {
		LambdaQueryWrapper<ProjectStaSubitemMonthEntity> wrapper = new QueryWrapper<ProjectStaSubitemMonthEntity>().lambda().eq(ProjectStaSubitemMonthEntity::getDeleted, CommonConstant.DELETED_FLAG_NOT_DELETE);

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
			wrapper.le(ProjectStaSubitemMonthEntity::getCreateTime, new Timestamp(startTimeMillion));
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
				wrapper.ge(ProjectStaSubitemMonthEntity::getCreateTime, new Timestamp(endTimeMillion));
			}
					// id
			if (null != queryInfo.getId()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getId, queryInfo.getId());
			}
						// 项目ID
			if (!StringUtils.hasText(queryInfo.getBizProjectId())) {
				wrapper.like(ProjectStaSubitemMonthEntity::getBizProjectId, "%" + queryInfo.getBizProjectId() + "%");
			}
						// 项目CODE
			if (!StringUtils.hasText(queryInfo.getProjectCode())) {
				wrapper.like(ProjectStaSubitemMonthEntity::getProjectCode, "%" + queryInfo.getProjectCode() + "%");
			}
						// 租户ID
			if (null != queryInfo.getTenantId()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getTenantId, queryInfo.getTenantId());
			}
						// 租户CODE
			if (!StringUtils.hasText(queryInfo.getTenantCode())) {
				wrapper.like(ProjectStaSubitemMonthEntity::getTenantCode, "%" + queryInfo.getTenantCode() + "%");
			}
						// 项目名称
			if (!StringUtils.hasText(queryInfo.getProjectName())) {
				wrapper.like(ProjectStaSubitemMonthEntity::getProjectName, "%" + queryInfo.getProjectName() + "%");
			}
						// 统计-年
			if (!StringUtils.hasText(queryInfo.getYear())) {
				wrapper.like(ProjectStaSubitemMonthEntity::getYear, "%" + queryInfo.getYear() + "%");
			}
						// 统计-月
			if (!StringUtils.hasText(queryInfo.getMonth())) {
				wrapper.like(ProjectStaSubitemMonthEntity::getMonth, "%" + queryInfo.getMonth() + "%");
			}
						// 总用气费
			if (null != queryInfo.getProjectGasFeeTotal()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectGasFeeTotal, queryInfo.getProjectGasFeeTotal().doubleValue());
			}
						// 总用气量
			if (null != queryInfo.getProjectGasUsageTotal()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectGasUsageTotal, queryInfo.getProjectGasUsageTotal().doubleValue());
			}
						// 总用水量
			if (null != queryInfo.getProjectWaterUsageTotal()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectWaterUsageTotal, queryInfo.getProjectWaterUsageTotal().doubleValue());
			}
						// 总计用水费
			if (null != queryInfo.getProjectWaterFeeWater()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectWaterFeeWater, queryInfo.getProjectWaterFeeWater().doubleValue());
			}
						// 总计污水处理费
			if (null != queryInfo.getProjectWaterFeeSewerage()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectWaterFeeSewerage, queryInfo.getProjectWaterFeeSewerage().doubleValue());
			}
						// 总计水费
			if (null != queryInfo.getProjectWaterFeeTotal()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectWaterFeeTotal, queryInfo.getProjectWaterFeeTotal().doubleValue());
			}
						// 全部负荷平用电量
			if (null != queryInfo.getProjectElectricityEnergyusageFlat()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectElectricityEnergyusageFlat, queryInfo.getProjectElectricityEnergyusageFlat().doubleValue());
			}
						// 全部负荷总用电量
			if (null != queryInfo.getProjectElectricityEnergyusageTotal()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectElectricityEnergyusageTotal, queryInfo.getProjectElectricityEnergyusageTotal().doubleValue());
			}
						// 全部负荷电度电费
			if (null != queryInfo.getProjectElectricityEnergyusagefeeTotal()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectElectricityEnergyusagefeeTotal, queryInfo.getProjectElectricityEnergyusagefeeTotal().doubleValue());
			}
						// 全部负荷尖用电量
			if (null != queryInfo.getProjectElectricityEnergyusageTip()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectElectricityEnergyusageTip, queryInfo.getProjectElectricityEnergyusageTip().doubleValue());
			}
						// 全部负荷谷用电量
			if (null != queryInfo.getProjectElectricityEnergyusageValley()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectElectricityEnergyusageValley, queryInfo.getProjectElectricityEnergyusageValley().doubleValue());
			}
						// 全部负荷峰用电量
			if (null != queryInfo.getProjectElectricityEnergyusagePeak()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectElectricityEnergyusagePeak, queryInfo.getProjectElectricityEnergyusagePeak().doubleValue());
			}
						// 电梯总用电量
			if (null != queryInfo.getProjectElectricitySubelevatorenergyTotal()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectElectricitySubelevatorenergyTotal, queryInfo.getProjectElectricitySubelevatorenergyTotal().doubleValue());
			}
						// 客房总用电量
			if (null != queryInfo.getProjectElectricitySubguestroomenergyTotal()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectElectricitySubguestroomenergyTotal, queryInfo.getProjectElectricitySubguestroomenergyTotal().doubleValue());
			}
						// 热水总用电量
			if (null != queryInfo.getProjectElectricitySubheatingwaterenergyTotal()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectElectricitySubheatingwaterenergyTotal, queryInfo.getProjectElectricitySubheatingwaterenergyTotal().doubleValue());
			}
						// 空调总用电量
			if (null != queryInfo.getProjectElectricitySubhavcenergyTotal()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectElectricitySubhavcenergyTotal, queryInfo.getProjectElectricitySubhavcenergyTotal().doubleValue());
			}
			if (null != queryInfo.getProjectElectricitySubpowersupplyenergyTotal()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectElectricitySubpowersupplyenergyTotal, queryInfo.getProjectElectricitySubpowersupplyenergyTotal().doubleValue());
			}
						// 其他总用电量
			if (null != queryInfo.getProjectElectricitySubothertypeTotal()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectElectricitySubothertypeTotal, queryInfo.getProjectElectricitySubothertypeTotal().doubleValue());
			}
						// 供水总用电量
			if (null != queryInfo.getProjectElectricitySubwatersupplyenergyTotal()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectElectricitySubwatersupplyenergyTotal, queryInfo.getProjectElectricitySubwatersupplyenergyTotal().doubleValue());
			}
						// 全部能耗标准煤当量
			if (null != queryInfo.getProjectCarbonTotalcoalTotal()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectCarbonTotalcoalTotal, queryInfo.getProjectCarbonTotalcoalTotal().doubleValue());
			}
						// 全部能耗粉尘当量
			if (null != queryInfo.getProjectCarbonTotaldustTotal()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectCarbonTotaldustTotal, queryInfo.getProjectCarbonTotaldustTotal().doubleValue());
			}
						// 全部能耗CO2当量
			if (null != queryInfo.getProjectCarbonTotalco2Total()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectCarbonTotalco2Total, queryInfo.getProjectCarbonTotalco2Total().doubleValue());
			}
						// 全部能耗SO2当量
			if (null != queryInfo.getProjectCarbonTotalso2Total()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectCarbonTotalso2Total, queryInfo.getProjectCarbonTotalso2Total().doubleValue());
			}
						// 气耗CO2当量
			if (null != queryInfo.getProjectCarbonGasusageco2Total()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectCarbonGasusageco2Total, queryInfo.getProjectCarbonGasusageco2Total().doubleValue());
			}
						// 气耗标准煤当量
			if (null != queryInfo.getProjectCarbonGasusagecoalTotal()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectCarbonGasusagecoalTotal, queryInfo.getProjectCarbonGasusagecoalTotal().doubleValue());
			}
						// 气耗粉尘当量
			if (null != queryInfo.getProjectCarbonGasusagedustTotal()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectCarbonGasusagedustTotal, queryInfo.getProjectCarbonGasusagedustTotal().doubleValue());
			}
						// 气耗SO2当量
			if (null != queryInfo.getProjectCarbonGasusageso2Total()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectCarbonGasusageso2Total, queryInfo.getProjectCarbonGasusageso2Total().doubleValue());
			}
						// 电耗SO2当量
			if (null != queryInfo.getProjectCarbonElectricityusageso2Total()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectCarbonElectricityusageso2Total, queryInfo.getProjectCarbonElectricityusageso2Total().doubleValue());
			}
						// 电耗粉尘当量
			if (null != queryInfo.getProjectCarbonElectricityusagedustTotal()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectCarbonElectricityusagedustTotal, queryInfo.getProjectCarbonElectricityusagedustTotal().doubleValue());
			}
						// 电耗CO2当量
			if (null != queryInfo.getProjectCarbonElectricityusageco2Total()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectCarbonElectricityusageco2Total, queryInfo.getProjectCarbonElectricityusageco2Total().doubleValue());
			}
						// 电耗标准煤当量
			if (null != queryInfo.getProjectCarbonElectricityusagecoalTotal()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectCarbonElectricityusagecoalTotal, queryInfo.getProjectCarbonElectricityusagecoalTotal().doubleValue());
			}
						// 水耗粉尘当量
			if (null != queryInfo.getProjectCarbonWaterusagedustTotal()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectCarbonWaterusagedustTotal, queryInfo.getProjectCarbonWaterusagedustTotal().doubleValue());
			}
						// 水耗标煤当量
			if (null != queryInfo.getProjectCarbonWaterusagecoalTotal()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectCarbonWaterusagecoalTotal, queryInfo.getProjectCarbonWaterusagecoalTotal().doubleValue());
			}
						// 水耗CO2当量
			if (null != queryInfo.getProjectCarbonWaterusageco2Total()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectCarbonWaterusageco2Total, queryInfo.getProjectCarbonWaterusageco2Total().doubleValue());
			}
						// 水耗SO2当量
			if (null != queryInfo.getProjectCarbonWaterusageso2Total()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getProjectCarbonWaterusageso2Total, queryInfo.getProjectCarbonWaterusageso2Total().doubleValue());
			}
						// 统计时间
			if (null != queryInfo.getStaTime()) {
				wrapper.eq(ProjectStaSubitemMonthEntity::getStaTime, queryInfo.getStaTime());
			}
					wrapper.orderByDesc(ProjectStaSubitemMonthEntity::getUpdateTime);
		return wrapper;
	}
}
