package com.landleaf.engine.service;

import java.util.List;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.landleaf.engine.domain.dto.RuleConditionAddDTO;
import com.landleaf.engine.domain.dto.RuleConditionQueryDTO;
import com.landleaf.engine.domain.entity.RuleConditionEntity;
import com.landleaf.engine.domain.vo.RuleConditionVO;

/**
 * RuleConditionEntity对象的业务逻辑接口定义 
 *
 * @author hebin
 * @since 2024-04-23
 */
public interface RuleConditionService extends IService<RuleConditionEntity> {

	/**
	 * 新增一个对象
	 * 
	 * @param addInfo
	 *            新增对象的数据的封装
	 * @return 新增后的数据对象
	 */
	RuleConditionAddDTO save(RuleConditionAddDTO addInfo);

	/**
	 * 修改一个对象
	 * 
	 * @param updateInfo
	 *            修改对象的数据的封装
	 */
	void update(RuleConditionAddDTO updateInfo);

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
	RuleConditionEntity selectById(Long id);

	/**
	 * 根据查询条件，查询实体的集合
	 * 
	 * @param queryInfo
	 *            查询条件封装
	 * @return 实体的集合
	 */
	List<RuleConditionEntity> list(RuleConditionQueryDTO queryInfo);

	/**
	 * 根据查询条件，分页查询实体的集合
	 * 
	 * @param queryInfo
	 *            查询条件封装
	 * @return 实体的集合的分页信息
	 */
	IPage<RuleConditionEntity> page(RuleConditionQueryDTO queryInfo);

	/**
	 * 根据bizRuleId删除数据
	 * @param bizRuleId
	 */
    void deleteByBizRuleId(String bizRuleId);

	/**
	 * 通过bizRuleId查询对应的条件信息
	 * @param bizRuleId
	 * @return
	 */
    List<RuleConditionVO> selectListByRuleId(String bizRuleId);
}