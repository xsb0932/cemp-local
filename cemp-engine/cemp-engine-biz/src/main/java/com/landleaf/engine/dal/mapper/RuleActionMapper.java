package com.landleaf.engine.dal.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.landleaf.engine.domain.entity.RuleActionEntity;

/**
 * RuleActionEntity对象的数据库操作句柄 
 *
 * @author hebin
 * @since 2024-04-23
 */
public interface RuleActionMapper extends BaseMapper<RuleActionEntity> {
	/**
	 * 根据id的列表，修改对应信息的is_deleted字段
	 * 
	 * @param ids
	 *            id的列表
	 * @param isDeleted
	 *            修改后的值
	 */
	void updateIsDeleted(@Param("ids") List<Long> ids, @Param("isDeleted") Integer isDeleted);
}