package com.landleaf.energy.service;

import java.util.List;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.landleaf.energy.domain.dto.ProjectStaDeviceGasHourAddDTO;
import com.landleaf.energy.domain.dto.ProjectStaDeviceGasHourQueryDTO;
import com.landleaf.energy.domain.entity.ProjectStaDeviceGasHourEntity;

/**
 * 统计表-设备指标-气类-统计小时的业务逻辑接口定义 
 *
 * @author hebin
 * @since 2023-06-24
 */
public interface ProjectStaDeviceGasHourService extends IService<ProjectStaDeviceGasHourEntity> {

	/**
	 * 新增一个对象
	 * 
	 * @param addInfo
	 *            新增对象的数据的封装
	 * @return 新增后的数据对象
	 */
	ProjectStaDeviceGasHourAddDTO save(ProjectStaDeviceGasHourAddDTO addInfo);

	/**
	 * 修改一个对象
	 * 
	 * @param updateInfo
	 *            修改对象的数据的封装
	 */
	void update(ProjectStaDeviceGasHourAddDTO updateInfo);

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
	ProjectStaDeviceGasHourEntity selectById(Long id);

	/**
	 * 根据查询条件，查询实体的集合
	 * 
	 * @param queryInfo
	 *            查询条件封装
	 * @return 实体的集合
	 */
	List<ProjectStaDeviceGasHourEntity> list(ProjectStaDeviceGasHourQueryDTO queryInfo);

	/**
	 * 根据查询条件，分页查询实体的集合
	 * 
	 * @param queryInfo
	 *            查询条件封装
	 * @return 实体的集合的分页信息
	 */
	IPage<ProjectStaDeviceGasHourEntity> page(ProjectStaDeviceGasHourQueryDTO queryInfo);
}