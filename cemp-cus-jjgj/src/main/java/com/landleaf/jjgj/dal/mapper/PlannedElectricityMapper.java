package com.landleaf.jjgj.dal.mapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.jjgj.domain.entity.PlannedElectricityEntity;
import com.landleaf.pgsql.extension.ExtensionMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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

}
