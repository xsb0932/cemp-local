package com.landleaf.jjgj.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.landleaf.jjgj.domain.entity.ProjectStaSubareaYearEntity;
import com.landleaf.jjgj.domain.vo.ProjectStaSubareaYearVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ProjectStaSubareaYearEntity对象的数据库操作句柄
 *
 * @author hebin
 * @since 2023-06-24
 */
@Mapper
public interface ProjectStaSubareaYearMapper extends BaseMapper<ProjectStaSubareaYearEntity> {
    /**
     * 根据id的列表，修改对应信息的is_deleted字段
     *
     * @param ids       id的列表
     * @param isDeleted 修改后的值
     */
    void updateIsDeleted(@Param("ids") List<Long> ids, @Param("isDeleted") Integer isDeleted);

    @Select("select t1.subarea_code, t1.subarea_name,sum(t1.sta_value) as sta_value from tb_project_sta_subarea_month t1 " +
            "where t1.deleted = 0 and t1.kpi_code = #{kpiCode} and t1.subarea_code = #{subareaCode} " +
            "and t1.year =  #{year} and t1.biz_project_id =  #{bizProjectId} " +
            "group by t1.subarea_code, t1.subarea_name;")
    ProjectStaSubareaYearVO getYearTotal(@Param("kpiCode") String kpiCode,
                                         @Param("subareaCode") String subareaCode,
                                         @Param("bizProjectId") String bizProjectId,
                                         @Param("year") String year);
}
