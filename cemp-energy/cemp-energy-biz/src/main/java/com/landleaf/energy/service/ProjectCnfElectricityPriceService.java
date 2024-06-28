package com.landleaf.energy.service;

import java.util.List;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.landleaf.energy.domain.dto.ProjectCnfElectricityPriceAddDTO;
import com.landleaf.energy.domain.dto.ProjectCnfElectricityPriceQueryDTO;
import com.landleaf.energy.domain.entity.ProjectCnfElectricityPriceEntity;
import com.landleaf.energy.domain.vo.ProjectCnfElectricityPriceVO;

/**
 * 电费配置表的业务逻辑接口定义 
 *
 * @author hebin
 * @since 2024-03-20
 */
public interface ProjectCnfElectricityPriceService extends IService<ProjectCnfElectricityPriceEntity> {

	/**
	 * 新增一个对象
	 * 
	 * @param addInfo
	 *            新增对象的数据的封装
	 * @return 新增后的数据对象
	 */
	ProjectCnfElectricityPriceAddDTO save(ProjectCnfElectricityPriceAddDTO addInfo);

	/**
	 * 修改一个对象
	 * 
	 * @param updateInfo
	 *            修改对象的数据的封装
	 */
	void update(ProjectCnfElectricityPriceAddDTO updateInfo);

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
	 * 根据id，查询详情,
	 * 
	 * @param bizProjectId
	 *            项目编号
	 * @return 详情信息
	 */
	ProjectCnfElectricityPriceVO selectDetailById(String bizProjectId);

	/**
	 * 根据bizProjectId，查询内容
	 *
	 * @param bizProjectId
	 * @return
	 */
	ProjectCnfElectricityPriceEntity selectByBizProjId(String bizProjectId);

	/**
	 * 根据查询条件，查询实体的集合
	 * 
	 * @param queryInfo
	 *            查询条件封装
	 * @return 实体的集合
	 */
	List<ProjectCnfElectricityPriceEntity> list(ProjectCnfElectricityPriceQueryDTO queryInfo);

	/**
	 * 根据查询条件，分页查询实体的集合
	 * 
	 * @param queryInfo
	 *            查询条件封装
	 * @return 实体的集合的分页信息
	 */
	IPage<ProjectCnfElectricityPriceEntity> page(ProjectCnfElectricityPriceQueryDTO queryInfo);
}