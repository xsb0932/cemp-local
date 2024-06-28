package com.landleaf.jjgj.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.landleaf.jjgj.domain.entity.ProjectStaSubareaMonthEntity;
import com.landleaf.jjgj.domain.vo.ProjectStaSubareaMonthVO;
import com.landleaf.jjgj.domain.vo.rjd.KanbanRJDEleYearRO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ProjectStaSubareaMonthEntity对象的数据库操作句柄
 *
 * @author hebin
 * @since 2023-06-24
 */
@Mapper
public interface ProjectStaSubareaMonthMapper extends BaseMapper<ProjectStaSubareaMonthEntity> {
    /**
     * 根据id的列表，修改对应信息的is_deleted字段
     *
     * @param ids       id的列表
     * @param isDeleted 修改后的值
     */
    void updateIsDeleted(@Param("ids") List<Long> ids, @Param("isDeleted") Integer isDeleted);

    @Select("SELECT\n" +
            "\t* \n" +
            "FROM\n" +
            "\t( SELECT t1.NAME FROM tb_project_cnf_subarea t1 WHERE t1.deleted = 0 and TYPE IN ( '楼层', '其他' ) ) AS tmp1\n" +
            "\tLEFT JOIN ( SELECT subarea_code, SUM ( sta_value ) AS svalue FROM tb_project_sta_subarea_year WHERE deleted = 0 and biz_project_id = 'PJ00000001' AND YEAR = '2023' GROUP BY subarea_code ) tmp2 ON tmp1.NAME = tmp2.subarea_code")
    List<KanbanRJDEleYearRO> getEleYearData();

    @Select("SELECT id as subarea_code,name  from tb_project_cnf_subarea t1 WHERE t1.deleted = 0 and TYPE IN ( '楼层', '其他' ) and t1.project_id = 'PJ00000001'")
    List<KanbanRJDEleYearRO> getEleSubareaCodes();

    @Select("SELECT id as subarea_code,name  from tb_project_cnf_subarea where deleted = 0 and parent_id is null and project_id = 'PJ00000001' limit 1")
    KanbanRJDEleYearRO getEleRootSubareaCodes();


    @Select("select t1.subarea_code, t1.subarea_name ,sum(t1.sta_value) as sta_value from tb_project_sta_subarea_day t1 " +
            "where t1.deleted = 0 and t1.kpi_code = #{kpiCode} and t1.subarea_code = #{subareaCode} " +
            "and t1.sta_time > #{begin} and t1.sta_time < #{end} and t1.biz_project_id = #{bizProjectId} " +
            "group by t1.subarea_code, t1.subarea_name")
    ProjectStaSubareaMonthVO getMonthTotal(@Param("kpiCode") String kpiCode,
                                           @Param("subareaCode") String subareaCode,
                                           @Param("bizProjectId") String bizProjectId,
                                           @Param("begin") String begin,
                                           @Param("end") String end);

    @Select("select * from tb_project_sta_subarea_month t1 where t1.deleted = 0 and year = '2023' and t1.subarea_code = '锦江体验中心酒店'")
    List<ProjectStaSubareaMonthEntity> getEleYearBarData(@Param("year") String year);

    @Select("select * from tb_project_sta_subarea_month t1 where t1.deleted = 0 and year = #{year} and t1.subarea_code = #{subareaCode} and t1.kpi_code = #{kpiCode} and t1.biz_project_id = #{bizProjectId} and t1.deleted = 0")
    List<ProjectStaSubareaMonthEntity> getEleYearBarData(@Param("kpiCode") String kpiCode,
                                                         @Param("subareaCode") String subareaCode,
                                                         @Param("bizProjectId") String bizProjectId,
                                                         @Param("year") String year);
}
