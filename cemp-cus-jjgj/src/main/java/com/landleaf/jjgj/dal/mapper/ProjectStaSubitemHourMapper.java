package com.landleaf.jjgj.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.landleaf.jjgj.domain.entity.ProjectStaSubitemHourEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ProjectStaSubitemHourEntity对象的数据库操作句柄
 *
 * @author hebin
 * @since 2023-06-24
 */
@Mapper
public interface ProjectStaSubitemHourMapper extends BaseMapper<ProjectStaSubitemHourEntity> {
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
