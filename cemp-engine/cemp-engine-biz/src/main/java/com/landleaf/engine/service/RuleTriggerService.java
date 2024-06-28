package com.landleaf.engine.service;

import java.util.List;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.landleaf.engine.domain.dto.RuleTriggerAddDTO;
import com.landleaf.engine.domain.dto.RuleTriggerQueryDTO;
import com.landleaf.engine.domain.entity.RuleTriggerEntity;
import com.landleaf.engine.domain.vo.RuleTriggerVO;

/**
 * RuleTriggerEntity对象的业务逻辑接口定义 
 *
 * @author hebin
 * @since 2024-04-23
 */
public interface RuleTriggerService extends IService<RuleTriggerEntity> {

	/**
	 * 新增一个对象
	 * 
	 * @param addInfo
	 *            新增对象的数据的封装
	 * @return 新增后的数据对象
	 */
	RuleTriggerAddDTO save(RuleTriggerAddDTO addInfo);

	/**
	 * 修改一个对象
	 * 
	 * @param updateInfo
	 *            修改对象的数据的封装
	 */
	void update(RuleTriggerAddDTO updateInfo);

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
	RuleTriggerEntity selectById(Long id);

	/**
	 * 根据查询条件，查询实体的集合
	 * 
	 * @param queryInfo
	 *            查询条件封装
	 * @return 实体的集合
	 */
	List<RuleTriggerEntity> list(RuleTriggerQueryDTO queryInfo);

	/**
	 * 根据查询条件，分页查询实体的集合
	 * 
	 * @param queryInfo
	 *            查询条件封装
	 * @return 实体的集合的分页信息
	 */
	IPage<RuleTriggerEntity> page(RuleTriggerQueryDTO queryInfo);

	/**
	 * 根据bizRuleId删除数据
	 * @param bizRuleId
	 */
    void deleteByBizRuleId(String bizRuleId);

	/**
	 * 通过bizRuleId查询对应的触发信息
	 * @param bizRuleId
	 * @return
	 */
    RuleTriggerVO selectByRuleId(String bizRuleId);
}