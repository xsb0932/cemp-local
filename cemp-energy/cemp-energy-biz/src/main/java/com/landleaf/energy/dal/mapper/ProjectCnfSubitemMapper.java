package com.landleaf.energy.dal.mapper;

import java.util.List;

import com.landleaf.energy.domain.entity.DeviceMonitorEntity;
import com.landleaf.energy.domain.entity.ProjectKpiConfigEntity;
import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.landleaf.energy.domain.entity.ProjectCnfSubitemEntity;

/**
 * ProjectCnfSubitemEntity对象的数据库操作句柄
 *
 * @author hebin
 * @since 2023-06-24
 */
@Mapper
public interface ProjectCnfSubitemMapper extends BaseMapper<ProjectCnfSubitemEntity> {
    /**
     * 根据id的列表，修改对应信息的is_deleted字段
     *
     * @param ids       id的列表
     * @param isDeleted 修改后的值
     */
    void updateIsDeleted(@Param("ids") List<Long> ids, @Param("isDeleted") Integer isDeleted);

    @Select("SELECT * FROM tb_project_cnf_subitem t1 where t1.deleted = 0 and t1.kpi_subtype is not null and t1.project_id = #{bizProjectId}  order by path ")
    List<ProjectCnfSubitemEntity> getValidCnf(@Param("bizProjectId") String bizProjectId);

    @Select("select t2.* from tb_project_cnf_subitem t1 join tb_project_kpi_config t2 \n" +
            "on t1.kpi_subtype = t2.kpi_subtype and t2.deleted = 0\n" +
            "where t1.deleted = 0 and t1.parent_id = (select id||'' from tb_project_cnf_subitem where deleted = 0 and parent_id is null limit 1) and t1.project_id = #{bizProjectId}")
    List<ProjectKpiConfigEntity> getTopLevelCnfs(@Param("bizProjectId") String bizProjectId);

    Long queryIdByKpiCode(@Param("code") String kpiCode, @Param("bizProjectId") String bizProjectId, @Param("tenantId") Long tenantId);

    @Update("update tb_project_cnf_subitem set path=#{path} where deleted = 0 and id=#{id}")
    void updatePath(@Param("id") Long id, @Param("path") String path);

    @Delete("delete from tb_project_subitem_device where subitem_id =#{subitemId}")
    void unBind(@Param("subitemId") Long subitemId);

    @Delete("delete from tb_project_cnf_subitem where id =#{id}")
    void phDelete(@Param("id") Long id);
}
