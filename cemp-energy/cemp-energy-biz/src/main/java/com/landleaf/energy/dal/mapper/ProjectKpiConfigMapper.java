package com.landleaf.energy.dal.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.landleaf.energy.domain.entity.ProjectKpiConfigEntity;
import org.apache.ibatis.annotations.Select;

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
}
