package com.landleaf.energy.dal.mapper;

import java.util.List;

import com.landleaf.energy.domain.entity.ProjectCnfSubareaEntity;
import com.landleaf.energy.domain.vo.DeviceMonitorVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.landleaf.energy.domain.entity.ProjectSubareaDeviceEntity;
import org.apache.ibatis.annotations.Select;

/**
 * ProjectSubareaDeviceEntity对象的数据库操作句柄
 *
 * @author hebin
 * @since 2023-06-24
 */
@Mapper
public interface ProjectSubareaDeviceMapper extends BaseMapper<ProjectSubareaDeviceEntity> {
    /**
     * 根据id的列表，修改对应信息的is_deleted字段
     *
     * @param ids       id的列表
     * @param isDeleted 修改后的值
     */
    void updateIsDeleted(@Param("ids") List<Long> ids, @Param("isDeleted") Integer isDeleted);

//	@Select("SELECT * FROM tb_project_subarea_device  where subaread_id =#{subareaId}")
//	List<ProjectSubareaDeviceEntity> getSubs(@Param("subareaId") Long subareaId);

    @Select("select t2.*, t1.compute_tag from tb_project_subarea_device t1 join tb_device_monitor t2 on t1.device_id = t2.biz_device_id where t1.deleted = 0 and t2.deleted = 0 and t1.subaread_id = #{subareaId}")
    List<DeviceMonitorVO> getSubs(@Param("subareaId") Long subareaId);

    @Select("select t1.* from tb_project_subarea_device t1 join tb_device_monitor t2 on t1.device_id = t2.biz_device_id  where t1.tenant_id = #{tenantId} and t1.deleted = 0 and t2.deleted = 0 order by t1.id")
    List<ProjectSubareaDeviceEntity> getAvaDevices(@Param("tenantId") Long tenantId);
}
