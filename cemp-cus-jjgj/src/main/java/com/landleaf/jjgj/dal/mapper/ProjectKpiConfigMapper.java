package com.landleaf.jjgj.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.landleaf.jjgj.domain.entity.ProjectKpiConfigEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ProjectKpiConfigEntity对象的数据库操作句柄
 *
 * @author hebin
 * @since 2023-06-24
 */
@Mapper
public interface ProjectKpiConfigMapper extends BaseMapper<ProjectKpiConfigEntity> {
    /**
     * 根据id的列表，修改对应信息的is_deleted字段
     *
     * @param ids       id的列表
     * @param isDeleted 修改后的值
     */
    void updateIsDeleted(@Param("ids") List<Long> ids, @Param("isDeleted") Integer isDeleted);

    @Select("SELECT * FROM tb_project_kpi_config t1 where t1.deleted = 0 and t1.kpi_type = #{kpitype}")
    List<ProjectKpiConfigEntity> getByKpiType(@Param("kpitype") String kpitype);

    @Select("SELECT * FROM tb_project_kpi_config where deleted = 0")
    List<ProjectKpiConfigEntity> getAll();

    List<ProjectKpiConfigEntity> getAllKpisByProject(@Param("bizProjectId") String bizProjectId, @Param("isHour") String isHour, @Param("isYMD") String isYMD);

    @Select("select * from tb_project_kpi_config t1 where t1.deleted = 0 and t1.kpi_subtype in (select kpi_subtype from tb_project_cnf_subitem  " +
            "where deleted = 0 and project_id = #{bizProjectId} and parent_id = (select id||'' from tb_project_cnf_subitem where deleted = 0 and project_id = #{bizProjectId} and  name = '项目总负荷')) and code in ('project.electricity.subElevatorEnergy.total','project.electricity.subHeatingWaterEnergy.total','project.electricity.subHAVCEnergy.total','project.electricity.subWaterSupplyEnergy.total')")
    List<ProjectKpiConfigEntity> getJJGJSubitemKpis(@Param("bizProjectId") String bizProjectId);
}
