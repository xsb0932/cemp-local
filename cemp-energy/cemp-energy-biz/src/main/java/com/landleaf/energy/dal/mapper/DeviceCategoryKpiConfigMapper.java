package com.landleaf.energy.dal.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.landleaf.energy.domain.entity.DeviceCategoryKpiConfigEntity;
import org.apache.ibatis.annotations.Select;

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

    @Select("SELECT distinct t1.category_name,t1.biz_category_id FROM tb_device_category_kpi_config  t1 where t1.deleted = 0 and EXISTS (select 1 from tb_product t2 join tb_product_ref t3 on t2.id = t3.product_id and t3.tenant_id = #{tenantId} and t2.category_id = t1.biz_category_id and t2.deleted = 0 and t3.deleted = 0 )  ")
    List<DeviceCategoryKpiConfigEntity> listCategory(@Param("tenantId") Long tenantId);

    @Select("SELECT * from  tb_device_category_kpi_config where deleted = 0 order by biz_category_id")
    List<DeviceCategoryKpiConfigEntity> listAll();
}
