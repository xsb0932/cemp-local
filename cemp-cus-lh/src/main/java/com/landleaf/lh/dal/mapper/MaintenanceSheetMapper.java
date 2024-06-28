package com.landleaf.lh.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.lh.domain.dto.MaintenanceExportDTO;
import com.landleaf.lh.domain.dto.MonthMaintenanceAverageDTO;
import com.landleaf.lh.domain.entity.MaintenanceSheetEntity;
import com.landleaf.lh.domain.response.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * MaintenanceSheetEntity对象的数据库操作句柄
 *
 * @author hebin
 * @since 2024-05-22
 */
public interface MaintenanceSheetMapper extends BaseMapper<MaintenanceSheetEntity> {

    Page<MaintenancePageResponse> maintenancePageQuery(@Param("page") Page<MaintenancePageResponse> page,
                                                       @Param("bizProjectIdList") List<String> bizProjectIdList,
                                                       @Param("maintenanceType") List<String> maintenanceType,
                                                       @Param("yearMonthStart") String yearMonthStart,
                                                       @Param("yearMonthEnd") String yearMonthEnd);

    MaintenanceInfoResponse info(@Param("id") Long id);

    List<MaintenanceExportDTO> selectExportList(@Param("bizProjectIdList") List<String> bizProjectIdList,
                                                @Param("maintenanceType") List<String> maintenanceType,
                                                @Param("yearMonthStart") String yearMonthStart,
                                                @Param("yearMonthEnd") String yearMonthEnd);

    List<MonthMaintenanceAverageDTO> selectMonthMaintenanceAverage(@Param("start") LocalDate start, @Param("end") LocalDate end);

    List<LhMaintenanceSortResponse> listMaintenanceSort(@Param("month") LocalDate month);

    @Select("select count(*) from lh_maintenance_sheet  t1 where biz_project_id in (select distinct biz_project_id  from tb_project  where parent_biz_node_id = #{nodeId}) and t1.maintenance_year = #{year} and t1.maintenance_month = #{month}")
    Integer getMNum(@Param("nodeId") String nodeId,
                    @Param("year") int year,
                    @Param("month") int month);

    @Select("select distinct maintenance_type from lh_maintenance_sheet  t1 where biz_project_id in (select distinct biz_project_id  from tb_project  where parent_biz_node_id = #{nodeId}) and t1.maintenance_year = #{year} and t1.maintenance_month = #{month}")
    List<String> getAllType(@Param("nodeId") String nodeId,
                            @Param("year") int year,
                            @Param("month") int mont);

    //    @Select("select maintenance_type as type,count(*) as num from lh_maintenance_sheet  t1 where biz_project_id in (select distinct biz_project_id  from tb_project  where parent_biz_node_id = #{nodeId}) and t1.maintenance_year = #{year} and t1.maintenance_month = #{month}  group by t1.maintenance_type")
    List<LhMaintenanceGropDataResponse> getAllTypeNum(@Param("bizProjectIdList") List<String> bizProjectIdList,
                                                      @Param("year") int year,
                                                      @Param("month") int mont);

    //    @Select("with list as (select biz_project_id,count(*) as maintenance_num from lh_maintenance_sheet  where biz_project_id in (select distinct biz_project_id  from tb_project  where parent_biz_node_id = #{nodeId}) and maintenance_year =  #{year} and maintenance_month = #{month} group by biz_project_id)\n" +
//            "select t2.name as project_name,t2.area , t1.maintenance_num  from list t1 join tb_project t2 on t1.biz_project_id = t2.biz_project_id ")
    List<LhAreaMaintenanceOrderResponse> getMaintenanceOrder(@Param("bizProjectIdList") List<String> bizProjectIdList,
                                                             @Param("year") int year,
                                                             @Param("month") int month);
}
