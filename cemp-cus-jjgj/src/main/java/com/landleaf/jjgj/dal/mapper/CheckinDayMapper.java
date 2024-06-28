package com.landleaf.jjgj.dal.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.landleaf.jjgj.domain.entity.CheckinDayEntity;
import org.apache.ibatis.annotations.Select;

/**
 * CheckinDayEntity对象的数据库操作句柄
 *
 * @author hebin
 * @since 2023-09-21
 */
@Mapper
public interface CheckinDayMapper extends BaseMapper<CheckinDayEntity> {
	/**
	 * 根据id的列表，修改对应信息的is_deleted字段
	 *
	 * @param ids
	 *            id的列表
	 * @param isDeleted
	 *            修改后的值
	 */
	void updateIsDeleted(@Param("ids") List<Integer> ids, @Param("isDeleted") Integer isDeleted);

    List<String> selectExistsDate(@Param("bizProjectId") String bizProjectId, @Param("list") List<String> list);
}
