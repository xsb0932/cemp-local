package com.landleaf.jjgj.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.landleaf.jjgj.domain.entity.DeviceCategoryKpiConfigEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * DeviceCategoryKpiConfigEntity对象的数据库操作句柄
 *
 * @author hebin
 * @since 2023-08-01
 */
public interface DeviceCategoryKpiConfigMapper extends BaseMapper<DeviceCategoryKpiConfigEntity> {
    /**
     * 根据id的列表，修改对应信息的is_deleted字段
     *
     * @param ids       id的列表
     * @param isDeleted 修改后的值
     */
    void updateIsDeleted(@Param("ids") List<Long> ids, @Param("isDeleted") Integer isDeleted);

    @Select("SELECT distinct category_name,biz_category_id FROM tb_device_category_kpi_config where deleted = 0")
    List<DeviceCategoryKpiConfigEntity> listCategory();

    @Select("SELECT * from  tb_device_category_kpi_config where deleted = 0 order by biz_category_id")
    List<DeviceCategoryKpiConfigEntity> listAll();
}
