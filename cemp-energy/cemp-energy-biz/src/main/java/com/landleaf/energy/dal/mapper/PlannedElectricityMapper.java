package com.landleaf.energy.dal.mapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.energy.domain.entity.PlannedElectricityEntity;
import com.landleaf.energy.response.PlannedAreaMonthsDataResponse;
import com.landleaf.pgsql.extension.ExtensionMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 计划用电持久层
 *
 * @author Tycoon
 * @since 2023/8/10 15:17
 **/
public interface PlannedElectricityMapper extends ExtensionMapper<PlannedElectricityEntity> {

    /**
     * 查询年份的计划用电
     *
     * @param projectBizId 项目业务ID
     * @param year 年份
     * @return 结果
     */
    default List<PlannedElectricityEntity> searchProjectYearPlans(@NotBlank String projectBizId, @NotNull String year) {
        return selectList(Wrappers.<PlannedElectricityEntity>lambdaQuery()
                .eq(PlannedElectricityEntity::getProjectBizId, projectBizId)
                .eq(PlannedElectricityEntity::getYear, year));
    }

    @Select("select t1.year,t1.month, sum(t1.plan_electricity_consumption) as consumption\n" +
            "from tb_project_planned_electricity t1 where t1.project_biz_id in (\n" +
            "select distinct biz_project_id  from tb_project  where parent_biz_node_id = #{nodeId}) and t1.year=#{year} group by t1.year,t1.month")
    List<PlannedAreaMonthsDataResponse> getAreaMonthsData(@Param("nodeId")String nodeId,
                                                          @Param("year")String year);
}
