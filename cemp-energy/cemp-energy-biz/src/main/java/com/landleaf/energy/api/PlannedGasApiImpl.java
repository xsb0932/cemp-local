package com.landleaf.energy.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.date.DateUtils;
import com.landleaf.energy.dal.mapper.PlannedGasMapper;
import com.landleaf.energy.domain.entity.PlannedGasEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class PlannedGasApiImpl implements PlannedGasApi {
    private final PlannedGasMapper plannedGasMapper;

    @Override
    public Response<BigDecimal> getProjectDurationTotalPlan(String bizProjectId, LocalDate begin, LocalDate end) {
        TenantContext.setIgnore(true);
        try {
            // 目前只存在跨1一个月的情况 且只有锦江的一周统计 所以简单处理
            PlannedGasEntity plan = plannedGasMapper.selectOne(new LambdaQueryWrapper<PlannedGasEntity>()
                    .eq(PlannedGasEntity::getProjectBizId, bizProjectId)
                    .eq(PlannedGasEntity::getYear, String.valueOf(begin.getYear()))
                    .eq(PlannedGasEntity::getMonth, String.valueOf(begin.getMonthValue())));
            // 周用电计划 = 月计划的天平均值 * 周天数
            boolean sameMonth = DateUtils.isSameMonth(begin, end);
            if (sameMonth) {
                if (null != plan && null != plan.getPlanGasConsumption()) {
                    return Response.success(
                            plan.getPlanGasConsumption()
                                    .divide(BigDecimal.valueOf(begin.getMonth().maxLength()), 2, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(7))
                    );
                }
            } else {
                PlannedGasEntity plan2 = plannedGasMapper.selectOne(new LambdaQueryWrapper<PlannedGasEntity>()
                        .eq(PlannedGasEntity::getProjectBizId, bizProjectId)
                        .eq(PlannedGasEntity::getYear, String.valueOf(end.getYear()))
                        .eq(PlannedGasEntity::getMonth, String.valueOf(end.getMonthValue())));
                if (null != plan && null != plan.getPlanGasConsumption()
                        && null != plan2 && null != plan2.getPlanGasConsumption()) {
                    return Response.success(
                            plan.getPlanGasConsumption()
                                    .divide(BigDecimal.valueOf(begin.getMonth().maxLength()), 2, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(begin.getMonth().maxLength() + 1 - begin.getDayOfMonth()))
                                    .add(
                                            plan2.getPlanGasConsumption()
                                                    .divide(BigDecimal.valueOf(end.getMonth().maxLength()), 2, RoundingMode.HALF_UP)
                                                    .multiply(BigDecimal.valueOf(end.getDayOfMonth()))
                                    )
                    );
                }
            }
            return Response.success();
        } finally {
            TenantContext.setIgnore(false);
        }
    }
}
