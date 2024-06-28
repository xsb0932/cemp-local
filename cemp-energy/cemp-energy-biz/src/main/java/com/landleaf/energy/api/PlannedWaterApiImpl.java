package com.landleaf.energy.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Maps;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.date.DateUtils;
import com.landleaf.energy.dal.mapper.PlannedWaterMapper;
import com.landleaf.energy.domain.entity.PlannedWaterEntity;
import com.landleaf.energy.request.MonthPlanBatchRequest;
import com.landleaf.energy.response.PlannedAreaMonthsDataResponse;
import com.landleaf.energy.response.PlannedWaterResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class PlannedWaterApiImpl implements PlannedWaterApi {
    private final PlannedWaterMapper plannedWaterMapper;

    @Override
    public Response<BigDecimal> getProjectDurationTotalPlan(String bizProjectId, LocalDate begin, LocalDate end) {
        TenantContext.setIgnore(true);
        try {
            // 目前只存在跨1一个月的情况 且只有锦江的一周统计 所以简单处理
            PlannedWaterEntity plan = plannedWaterMapper.selectOne(new LambdaQueryWrapper<PlannedWaterEntity>()
                    .eq(PlannedWaterEntity::getProjectBizId, bizProjectId)
                    .eq(PlannedWaterEntity::getYear, String.valueOf(begin.getYear()))
                    .eq(PlannedWaterEntity::getMonth, String.valueOf(begin.getMonthValue())));
            // 周用电计划 = 月计划的天平均值 * 周天数
            boolean sameMonth = DateUtils.isSameMonth(begin, end);
            if (sameMonth) {
                if (null != plan && null != plan.getPlanWaterConsumption()) {
                    return Response.success(
                            plan.getPlanWaterConsumption()
                                    .divide(BigDecimal.valueOf(begin.getMonth().maxLength()), 2, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(7))
                    );
                }
            } else {
                PlannedWaterEntity plan2 = plannedWaterMapper.selectOne(new LambdaQueryWrapper<PlannedWaterEntity>()
                        .eq(PlannedWaterEntity::getProjectBizId, bizProjectId)
                        .eq(PlannedWaterEntity::getYear, String.valueOf(end.getYear()))
                        .eq(PlannedWaterEntity::getMonth, String.valueOf(end.getMonthValue())));
                if (null != plan && null != plan.getPlanWaterConsumption()
                        && null != plan2 && null != plan2.getPlanWaterConsumption()) {
                    return Response.success(
                            plan.getPlanWaterConsumption()
                                    .divide(BigDecimal.valueOf(begin.getMonth().maxLength()), 2, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(begin.getMonth().maxLength() + 1 - begin.getDayOfMonth()))
                                    .add(
                                            plan2.getPlanWaterConsumption()
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

    @Override
    public Response<List<PlannedWaterResponse>> getWaterPlanYear(String bizProjectId, String year, String month) {
        TenantContext.setIgnore(true);
//        BigDecimal waterPlan =  plannedWaterMapper.selectList(new LambdaQueryWrapper<PlannedWaterEntity>()
//                .eq(PlannedWaterEntity::getProjectBizId, bizProjectId)
//                .eq(PlannedWaterEntity::getYear, year)).stream().map(PlannedWaterEntity::getPlanWaterConsumption).reduce(BigDecimal.ZERO, BigDecimal::add);
        LambdaQueryWrapper<PlannedWaterEntity> lqw = new LambdaQueryWrapper<PlannedWaterEntity>()
                .eq(PlannedWaterEntity::getProjectBizId, bizProjectId);
        if (StringUtils.isNotBlank(year)) {
            lqw.eq(PlannedWaterEntity::getYear, year);
        }
        if (StringUtils.isNotBlank(month)) {
            lqw.eq(PlannedWaterEntity::getMonth, month);
        }
        lqw.orderByAsc(PlannedWaterEntity::getYear, PlannedWaterEntity::getMonth);
        return Response.success(
                plannedWaterMapper.selectList(lqw).stream()
                        .sorted((o1, o2) -> YearMonth.of(Integer.valueOf(o1.getYear()), Integer.valueOf(o1.getMonth())).compareTo(YearMonth.of(Integer.valueOf(o2.getYear()), Integer.valueOf(o2.getMonth()))))

                        .map(plannedWaterEntity -> {
                            PlannedWaterResponse response = new PlannedWaterResponse();
                            BeanUtils.copyProperties(plannedWaterEntity, response);
                            return response;
                        }).collect(Collectors.toList())
        );
    }

    @Override
    public Response<Map<String, Map<YearMonth, BigDecimal>>> batchMonthPlan(MonthPlanBatchRequest request) {
        request.validated();
        TenantContext.setIgnore(true);
        Map<String, Map<YearMonth, BigDecimal>> result = Maps.newHashMap();

        List<String> bizProjectIdList = request.getBizProjectIdList();
        List<YearMonth> months = request.getMonths();
        bizProjectIdList.forEach(id -> result.put(id, Maps.newHashMap()));

        for (YearMonth month : months) {
            Map<String, BigDecimal> projectPlanMap = plannedWaterMapper.selectList(
                            new LambdaQueryWrapper<PlannedWaterEntity>()
                                    .in(PlannedWaterEntity::getProjectBizId, bizProjectIdList)
                                    .eq(PlannedWaterEntity::getYear, String.valueOf(month.getYear()))
                                    .eq(PlannedWaterEntity::getMonth, String.valueOf(month.getMonthValue())))
                    .stream()
                    .collect(HashMap::new, (map, item) -> map.put(item.getProjectBizId(), item.getPlanWaterConsumption()), HashMap::putAll);
            for (String bizProjectId : bizProjectIdList) {
                Map<YearMonth, BigDecimal> monthMap = result.get(bizProjectId);
                BigDecimal plan = projectPlanMap.get(bizProjectId);
                monthMap.put(month, plan);
            }
        }
        return Response.success(result);
    }

    @Override
    public Response<List<PlannedAreaMonthsDataResponse>> getAreaMonthsData(String nodeId, String year) {
        List<PlannedAreaMonthsDataResponse> result = plannedWaterMapper.getAreaMonthsData(nodeId,year);
        return Response.success(result);
    }


}
