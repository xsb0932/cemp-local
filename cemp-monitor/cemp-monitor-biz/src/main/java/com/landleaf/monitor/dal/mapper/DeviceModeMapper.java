package com.landleaf.monitor.dal.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.landleaf.monitor.domain.entity.DeviceModeEntity;

/**
 * DeviceModeEntity对象的数据库操作句柄 
 *
 * @author hebin
 * @since 2023-09-14
 */
public interface DeviceModeMapper extends BaseMapper<DeviceModeEntity> {
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