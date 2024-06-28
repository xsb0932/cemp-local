package com.landleaf.jjgj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.landleaf.jjgj.domain.entity.DeviceMonitorEntity;
import com.landleaf.comm.vo.SelectedVO;
import com.landleaf.jjgj.domain.dto.DeviceMonitorAddDTO;
import com.landleaf.jjgj.domain.dto.DeviceMonitorQueryDTO;


import java.util.List;

/**
 * DeviceMonitorEntity对象的业务逻辑接口定义
 *
 * @author hebin
 * @since 2023-06-22
 */
public interface DeviceMonitorService extends IService<DeviceMonitorEntity> {

	/**
	 * 新增一个对象
	 *
	 * @param addInfo
	 *            新增对象的数据的封装
	 * @return 新增后的数据对象
	 */
	DeviceMonitorAddDTO save(DeviceMonitorAddDTO addInfo);

	/**
	 * 修改一个对象
	 *
	 * @param updateInfo
	 *            修改对象的数据的封装
	 */
	void update(DeviceMonitorAddDTO updateInfo);

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
	DeviceMonitorEntity selectById(Long id);

	/**
	 * 根据查询条件，查询实体的集合
	 *
	 * @param queryInfo
	 *            查询条件封装
	 * @return 实体的集合
	 */
	List<DeviceMonitorEntity> list(DeviceMonitorQueryDTO queryInfo);

	/**
	 * 根据查询条件，分页查询实体的集合
	 *
	 * @param queryInfo
	 *            查询条件封装
	 * @return 实体的集合的分页信息
	 */
	IPage<DeviceMonitorEntity> page(DeviceMonitorQueryDTO queryInfo);

    List<SelectedVO> getDeviceByCategory(String bizCategoryId);


}
