package com.landleaf.energy.dal.mapper;

import java.util.List;

import com.landleaf.energy.domain.entity.DeviceMonitorEntity;
import com.landleaf.energy.domain.entity.ProjectCnfSubitemEntity;
import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.landleaf.energy.domain.entity.ProjectCnfSubareaEntity;

/**
 * ProjectCnfSubareaEntity对象的数据库操作句柄
 *
 * @author hebin
 * @since 2023-06-24
 */
@Mapper
public interface ProjectCnfSubareaMapper extends BaseMapper<ProjectCnfSubareaEntity> {
    /**
     * 根据id的列表，修改对应信息的is_deleted字段
     *
     * @param ids       id的列表
     * @param isDeleted 修改后的值
     */
    void updateIsDeleted(@Param("ids") List<Long> ids, @Param("isDeleted") Integer isDeleted);

    @Select("SELECT * FROM tb_project_cnf_subarea t1 where t1.deleted = 0 and t1.kpi_subtype is not null and t1.project_id = #{bizProjectId} order by kpi_type,path")
    List<ProjectCnfSubareaEntity> getValidCnf(@Param("bizProjectId") String bizProjectId);

    @Delete("delete from tb_project_subarea_device where subaread_id =#{subareaId}")
    void unBind(@Param("subareaId") Long subareaId);

    @Delete("delete from tb_project_cnf_subarea where id =#{id}")
    void phDelete(@Param("id") Long id);

    @Update("update tb_project_cnf_subarea set path=#{path} where deleted = 0 and id=#{id}")
    void updatePath(@Param("id") Long id, @Param("path") String path);
}
