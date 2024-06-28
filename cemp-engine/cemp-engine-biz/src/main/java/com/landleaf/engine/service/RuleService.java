package com.landleaf.engine.service;

import java.util.List;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import com.landleaf.engine.domain.dto.RuleAddDTO;
import com.landleaf.engine.domain.dto.RuleDetailAddDTO;
import com.landleaf.engine.domain.dto.RuleQueryDTO;
import com.landleaf.engine.domain.entity.RuleEntity;
import com.landleaf.engine.domain.vo.RuleDetailVO;
import com.landleaf.engine.domain.vo.RuleVO;

/**
 * RuleEntity对象的业务逻辑接口定义
 *
 * @author hebin
 * @since 2024-04-23
 */
public interface RuleService extends IService<RuleEntity> {

    /**
     * 新增一个对象
     *
     * @param addInfo 新增对象的数据的封装
     * @return 新增后的数据对象
     */
    RuleAddDTO save(RuleAddDTO addInfo);

    /**
     * 修改一个对象
     *
     * @param updateInfo 修改对象的数据的封装
     */
    void update(RuleAddDTO updateInfo);

    /**
     * 修改数据的is_delete标识
     *
     * @param id        要修改的数据的编号
     * @param isDeleted 删除标记
     */
    void updateIsDeleted(Long id, Integer isDeleted);

    /**
     * 根据id，查询详情
     *
     * @param id 编号
     * @return 详情信息
     */
    RuleEntity selectById(Long id);

    /**
     * 根据查询条件，查询实体的集合
     *
     * @param queryInfo 查询条件封装
     * @return 实体的集合
     */
    List<RuleEntity> list(RuleQueryDTO queryInfo);

    /**
     * 根据查询条件，分页查询实体的集合
     *
     * @param queryInfo 查询条件封装
     * @return 实体的集合的分页信息
     */
    PageDTO<RuleVO> page(RuleQueryDTO queryInfo);

    /**
     * 修改可用状态
     *
     * @param id
     * @param status
     */
    void changeStatus(Long id, String status);

    /**
     * 保存规则相请
     *
     * @param addInfo
     * @return
     */
    boolean saveDetail(RuleDetailAddDTO addInfo);

    /**
     * 根据ruleId，查询rule的相请
     *
     * @param id
     * @return
     */
    RuleDetailVO getDetail(Long id);
}