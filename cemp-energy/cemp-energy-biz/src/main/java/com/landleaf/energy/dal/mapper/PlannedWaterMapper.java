package com.landleaf.energy.dal.mapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.energy.domain.entity.PlannedElectricityEntity;
import com.landleaf.energy.domain.entity.PlannedWaterEntity;
import com.landleaf.energy.response.PlannedAreaMonthsDataResponse;
import com.landleaf.pgsql.extension.ExtensionMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 计划用水持久层
 *
 * @author Tycoon
 * @since 2023/8/10 15:17
 **/
public interface PlannedWaterMapper extends ExtensionMapper<PlannedWaterEntity> {

    /**
     * 查询年份的计划用水
     *
     * @param projectBizId 项目业务ID
     * @param year 年份
     * @return 结果
     */
    default List<PlannedWaterEntity> searchProjectYearPlans(@NotBlank String projectBizId, @NotNull String year) {
        return selectList(Wrappers.<PlannedWaterEntity>lambdaQuery()
                .eq(PlannedWaterEntity::getProjectBizId, projectBizId)
                .eq(PlannedWaterEntity::getYear, year));
    }


    @Select("select t1.year,t1.month, sum(t1.plan_water_consumption) as consumption\n" +
            "from tb_project_planned_water t1 where t1.project_biz_id in (\n" +
            "select distinct biz_project_id  from tb_project  where parent_biz_node_id = #{nodeId}) and t1.year=#{year} group by t1.year,t1.month")
    List<PlannedAreaMonthsDataResponse> getAreaMonthsData(@Param("nodeId")String nodeId,
                                                          @Param("year")String year);}
