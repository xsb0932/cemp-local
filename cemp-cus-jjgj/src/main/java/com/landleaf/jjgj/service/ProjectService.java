package com.landleaf.jjgj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.landleaf.jjgj.domain.entity.ProjectEntity;
import com.landleaf.jjgj.domain.dto.ProjectAddDTO;
import com.landleaf.jjgj.domain.dto.ProjectQueryDTO;
import com.landleaf.jjgj.domain.vo.rjd.KanbanRJDProjectVO;


import java.util.List;

/**
 * 项目的业务逻辑接口定义
 *
 * @author hebin
 * @since 2023-06-24
 */
public interface ProjectService extends IService<ProjectEntity> {

	/**
	 * 新增一个对象
	 *
	 * @param addInfo
	 *            新增对象的数据的封装
	 * @return 新增后的数据对象
	 */
	ProjectAddDTO save(ProjectAddDTO addInfo);

	/**
	 * 修改一个对象
	 *
	 * @param updateInfo
	 *            修改对象的数据的封装
	 */
	void update(ProjectAddDTO updateInfo);

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
	ProjectEntity selectById(Long id);

	/**
	 * 根据查询条件，查询实体的集合
	 *
	 * @param queryInfo
	 *            查询条件封装
	 * @return 实体的集合
	 */
	List<ProjectEntity> list(ProjectQueryDTO queryInfo);

	/**
	 * 根据查询条件，分页查询实体的集合
	 *
	 * @param queryInfo
	 *            查询条件封装
	 * @return 实体的集合的分页信息
	 */
	IPage<ProjectEntity> page(ProjectQueryDTO queryInfo);

	KanbanRJDProjectVO getByBizProjectId(String bizProjectId);
}
