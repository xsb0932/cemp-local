package com.landleaf.energy.service;

import java.util.List;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.landleaf.energy.domain.dto.ProjectStaDeviceElectricityDayAddDTO;
import com.landleaf.energy.domain.dto.ProjectStaDeviceElectricityDayQueryDTO;
import com.landleaf.energy.domain.entity.ProjectStaDeviceElectricityDayEntity;

/**
 * 统计表-设备指标-电表-统计天的业务逻辑接口定义 
 *
 * @author hebin
 * @since 2023-06-24
 */
public interface ProjectStaDeviceElectricityDayService extends IService<ProjectStaDeviceElectricityDayEntity> {

	/**
	 * 新增一个对象
	 * 
	 * @param addInfo
	 *            新增对象的数据的封装
	 * @return 新增后的数据对象
	 */
	ProjectStaDeviceElectricityDayAddDTO save(ProjectStaDeviceElectricityDayAddDTO addInfo);

	/**
	 * 修改一个对象
	 * 
	 * @param updateInfo
	 *            修改对象的数据的封装
	 */
	void update(ProjectStaDeviceElectricityDayAddDTO updateInfo);

	/**
	 * 修改数据的is_delete标识
	 * 
	 * @param ids
	 *            要修改的数据的编号
	 * @param isDeleted
	 *            删除标记
	 */
	void updateIsDeleted(String ids, Integer isDeleted);

	/**
	 * 根据id，查询详情
	 * 
	 * @param id
	 *            编号
	 * @return 详情信息
	 */
	ProjectStaDeviceElectricityDayEntity selectById(Long id);

	/**
	 * 根据查询条件，查询实体的集合
	 * 
	 * @param queryInfo
	 *            查询条件封装
	 * @return 实体的集合
	 */
	List<ProjectStaDeviceElectricityDayEntity> list(ProjectStaDeviceElectricityDayQueryDTO queryInfo);

	/**
	 * 根据查询条件，分页查询实体的集合
	 * 
	 * @param queryInfo
	 *            查询条件封装
	 * @return 实体的集合的分页信息
	 */
	IPage<ProjectStaDeviceElectricityDayEntity> page(ProjectStaDeviceElectricityDayQueryDTO queryInfo);
}