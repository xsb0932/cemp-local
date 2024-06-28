package com.landleaf.engine.service;

import java.util.List;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.landleaf.engine.domain.dto.RuleActionAddDTO;
import com.landleaf.engine.domain.dto.RuleActionQueryDTO;
import com.landleaf.engine.domain.entity.RuleActionEntity;
import com.landleaf.engine.domain.vo.RuleActionVO;

/**
 * RuleActionEntity对象的业务逻辑接口定义
 *
 * @author hebin
 * @since 2024-04-23
 */
public interface RuleActionService extends IService<RuleActionEntity> {

    /**
     * 新增一个对象
     *
     * @param addInfo 新增对象的数据的封装
     * @return 新增后的数据对象
     */
    RuleActionAddDTO save(RuleActionAddDTO addInfo);

    /**
     * 修改一个对象
     *
     * @param updateInfo 修改对象的数据的封装
     */
    void update(RuleActionAddDTO updateInfo);

    /**
     * 修改数据的is_delete标识
     *
     * @param ids       要修改的数据的编号
     * @param isDeleted 删除标记
     */
    void updateIsDeleted(String ids, Integer isDeleted);

    /**
     * 根据id，查询详情
     *
     * @param id 编号
     * @return 详情信息
     */
    RuleActionEntity selectById(Long id);

    /**
     * 根据查询条件，查询实体的集合
     *
     * @param queryInfo 查询条件封装
     * @return 实体的集合
     */
    List<RuleActionEntity> list(RuleActionQueryDTO queryInfo);

    /**
     * 根据查询条件，分页查询实体的集合
     *
     * @param queryInfo 查询条件封装
     * @return 实体的集合的分页信息
     */
    IPage<RuleActionEntity> page(RuleActionQueryDTO queryInfo);

    /**
     * 根据bizRuleId,删除执行动作
     *
     * @param bizRuleId
     */
    void deleteByBizRuleId(String bizRuleId);

    /**
     * 通过bizRuleId查询对应的动作信息
     * @param bizRuleId
     * @return
     */
    RuleActionVO selectByRuleId(String bizRuleId);
}