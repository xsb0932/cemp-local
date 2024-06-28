package com.landleaf.jjgj.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.jjgj.domain.entity.ProjectStaSubitemMonthEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ProjectStaSubitemMonthEntity对象的数据库操作句柄
 *
 * @author hebin
 * @since 2023-06-24
 */
@Mapper
public interface ProjectStaSubitemMonthMapper extends BaseMapper<ProjectStaSubitemMonthEntity> {
	/**
	 * 根据id的列表，修改对应信息的is_deleted字段
	 *
	 * @param ids
	 *            id的列表
	 * @param isDeleted
	 *            修改后的值
	 */
	void updateIsDeleted(@Param("ids") List<Long> ids, @Param("isDeleted") Integer isDeleted);


	default ProjectStaSubitemMonthEntity getCurrentMonth(String bizProjectId, String year,String month) {
		return selectOne(Wrappers.<ProjectStaSubitemMonthEntity>lambdaQuery()
				.eq(ProjectStaSubitemMonthEntity::getBizProjectId, bizProjectId)
				.eq(ProjectStaSubitemMonthEntity::getYear, year)
				.eq(ProjectStaSubitemMonthEntity::getMonth, month));
	}

	default List<ProjectStaSubitemMonthEntity> getEleYearData(String bizProjectId, String year) {
		return selectList(Wrappers.<ProjectStaSubitemMonthEntity>lambdaQuery()
				.eq(ProjectStaSubitemMonthEntity::getBizProjectId, bizProjectId)
				.eq(ProjectStaSubitemMonthEntity::getYear, year));
	}
}
