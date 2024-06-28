package com.landleaf.jjgj.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.landleaf.jjgj.domain.entity.ProjectCnfGasFeeEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 燃气费用配置表的数据库操作句柄
 *
 * @author hebin
 * @since 2023-07-04
 */
public interface ProjectCnfGasFeeMapper extends BaseMapper<ProjectCnfGasFeeEntity> {
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
