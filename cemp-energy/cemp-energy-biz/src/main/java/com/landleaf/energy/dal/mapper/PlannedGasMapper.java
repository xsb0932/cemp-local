package com.landleaf.energy.dal.mapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.energy.domain.entity.PlannedGasEntity;
import com.landleaf.energy.domain.entity.PlannedWaterEntity;
import com.landleaf.pgsql.extension.ExtensionMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 计划用气持久层
 *
 * @author Tycoon
 * @since 2023/8/10 15:17
 **/
public interface PlannedGasMapper extends ExtensionMapper<PlannedGasEntity> {

    /**
     * 查询年份的计划用气
     *
     * @param projectBizId 项目业务ID
     * @param year 年份
     * @return 结果
     */
    default List<PlannedGasEntity> searchProjectYearPlans(@NotBlank String projectBizId, @NotNull String year) {
        return selectList(Wrappers.<PlannedGasEntity>lambdaQuery()
                .eq(PlannedGasEntity::getProjectBizId, projectBizId)
                .eq(PlannedGasEntity::getYear, year));
    }

}
