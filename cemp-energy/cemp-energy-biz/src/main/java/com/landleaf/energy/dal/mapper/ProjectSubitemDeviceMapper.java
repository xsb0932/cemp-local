package com.landleaf.energy.dal.mapper;

import java.util.List;

import com.landleaf.energy.domain.vo.DeviceMonitorVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.landleaf.energy.domain.entity.ProjectSubitemDeviceEntity;
import org.apache.ibatis.annotations.Select;

/**
 * ProjectSubitemDeviceEntity对象的数据库操作句柄
 *
 * @author hebin
 * @since 2023-06-24
 */
@Mapper
public interface ProjectSubitemDeviceMapper extends BaseMapper<ProjectSubitemDeviceEntity> {
    /**
     * 根据id的列表，修改对应信息的is_deleted字段
     *
     * @param ids       id的列表
     * @param isDeleted 修改后的值
     */
    void updateIsDeleted(@Param("ids") List<Long> ids, @Param("isDeleted") Integer isDeleted);

    /**
     * 根据kpiCode和bizProjectId获取对应的设备列表
     *
     * @param bizProjectId
     * @param code
     */
    List<ProjectSubitemDeviceEntity> queryAllDeviceByKpiCode(@Param("bizProjectId") String bizProjectId, @Param("code") String code, @Param("tenantId") Long tenantId);

    /**
     * 根据kpiCode和bizProjectId删除对应的设备列表
     *
     * @param projectId
     * @param code
     * @param loginUserId
     */
    void rmAllDeviceByKpiCode(@Param("bizProjectId") String projectId, @Param("code") String code, @Param("loginUserId") Long loginUserId);

    @Select("select t2.*, t1.compute_tag from tb_project_subitem_device t1 join tb_device_monitor t2 on t1.device_id = t2.biz_device_id where t1.deleted = 0 and t1.subitem_id = #{subitemId} and t2.deleted = 0")
    List<DeviceMonitorVO> getSubs(@Param("subitemId") Long subitemId);

    List<ProjectSubitemDeviceEntity> listAllValid(@Param("subitemIds") List<Long> subitemIds);
}
