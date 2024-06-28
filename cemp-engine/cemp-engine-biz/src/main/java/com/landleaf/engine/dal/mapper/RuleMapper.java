package com.landleaf.engine.dal.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.landleaf.engine.domain.dto.RuleQueryDTO;
import com.landleaf.engine.domain.vo.RuleVO;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.landleaf.engine.domain.entity.RuleEntity;

/**
 * RuleEntity对象的数据库操作句柄
 *
 * @author hebin
 * @since 2024-04-23
 */
public interface RuleMapper extends BaseMapper<RuleEntity> {
    /**
     * 根据id的列表，修改对应信息的is_deleted字段
     *
     * @param ids       id的列表
     * @param isDeleted 修改后的值
     */
    void updateIsDeleted(@Param("ids") List<Long> ids, @Param("isDeleted") Integer isDeleted);

    /**
     * 根据条件，查询对应的信息
     *
     * @param page
     * @param queryInfo
     * @return
     */
    Page<RuleVO> selectPageVO(@Param("page") Page<Object> page, @Param("queryInfo") RuleQueryDTO queryInfo, @Param("userId") Long userId);
}