package com.landleaf.energy.service.impl;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.energy.dal.mapper.*;
import com.landleaf.energy.domain.entity.ProjectCnfSubareaEntity;
import com.landleaf.energy.domain.entity.ProjectStaSubareaDayEntity;
import com.landleaf.energy.domain.entity.ProjectStaSubareaMonthEntity;
import com.landleaf.energy.request.SubareaMonthRequest;
import com.landleaf.energy.service.StaSubareaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaSubareaServiceImpl implements StaSubareaService {
    private final ProjectStaSubareaYearMapper projectStaSubareaYearMapper;
    private final ProjectStaSubareaMonthMapper projectStaSubareaMonthMapper;
    private final ProjectStaSubareaDayMapper projectStaSubareaDayMapper;
    private final ProjectStaSubareaHourMapper projectStaSubareaHourMapper;
    private final ProjectCnfSubareaMapper projectCnfSubareaMapper;

    @Override
    public Response<Map<String, Map<YearMonth, BigDecimal>>> searchMonth(SubareaMonthRequest request) {
        TenantContext.setTenantId(request.getTenantId());
        List<YearMonth> yms = request.getYms();
        YearMonth ymNow = YearMonth.now();
        Map<String, Map<YearMonth, BigDecimal>> response = new HashMap<>();
        response = getCnfs(request).stream().collect(Collectors.toMap(ProjectCnfSubareaEntity::getName, new Function<ProjectCnfSubareaEntity, Map<YearMonth, BigDecimal>>() {
            @Override
            public Map<YearMonth, BigDecimal> apply(ProjectCnfSubareaEntity projectCnfSubareaEntity) {
                return new HashMap<>();
            }
        }));
//        response = getCnfs(request).stream().collect(Collectors.toMap(new Function<ProjectCnfSubareaEntity, String>() {
//            @Override
//            public String apply(ProjectCnfSubareaEntity projectCnfSubareaEntity) {
//                return String.valueOf(projectCnfSubareaEntity.getProjectId());
//            }
//        }, new Function<ProjectCnfSubareaEntity, Map<YearMonth, BigDecimal>>() {
//            @Override
//            public Map<YearMonth, BigDecimal> apply(ProjectCnfSubareaEntity projectCnfSubareaEntity) {
//                return new HashMap<>();
//            }
//        }));
        Map<String, Map<YearMonth, BigDecimal>> finalResponse = response;
        yms.forEach(new Consumer<YearMonth>() {
            @Override
            public void accept(YearMonth ym) {
                Map<String,BigDecimal> staMonth = staMonth(request,ym);
                if(staMonth != null){
                    staMonth.keySet().forEach(new Consumer<String>() {
                        @Override
                        public void accept(String subareaCode) {
                            finalResponse.get(subareaCode).put(ym,staMonth.get(subareaCode));
                        }
                    });
                }

            }
        });
        return Response.success(finalResponse);
    }

    /**
     *
     * @param request
     * @param ym
     * @return key
     */
    private Map<String,BigDecimal> staMonth(SubareaMonthRequest request,YearMonth ym){
        YearMonth ymNow = YearMonth.now();
        if (ymNow.isAfter(ym)){
            //往月
            LambdaQueryWrapper<ProjectStaSubareaMonthEntity> lw = new LambdaQueryWrapper<>();
            lw.eq(ProjectStaSubareaMonthEntity::getYear,String.valueOf(ym.getYear()));
            lw.eq(ProjectStaSubareaMonthEntity::getMonth,String.valueOf(ym.getMonthValue()));
            lw.eq(ProjectStaSubareaMonthEntity::getBizProjectId, request.getProjectId());
            lw.in(ProjectStaSubareaMonthEntity::getSubareaName,request.getSubareaName());
            lw.eq(ProjectStaSubareaMonthEntity::getKpiCode,request.getKpi());
            return projectStaSubareaMonthMapper.selectList(lw).stream().collect(Collectors.toMap(ProjectStaSubareaMonthEntity::getSubareaName,ProjectStaSubareaMonthEntity::getStaValue));
        }else{
            //当月 = sum(天)
            LambdaQueryWrapper<ProjectStaSubareaDayEntity> lw = new LambdaQueryWrapper<>();
            lw.eq(ProjectStaSubareaDayEntity::getYear,String.valueOf(ym.getYear()));
            lw.eq(ProjectStaSubareaDayEntity::getMonth,String.valueOf(ym.getMonthValue()));
            lw.eq(ProjectStaSubareaDayEntity::getBizProjectId, request.getProjectId());
            lw.in(ProjectStaSubareaDayEntity::getSubareaName,request.getSubareaName());
            lw.eq(ProjectStaSubareaDayEntity::getKpiCode,request.getKpi());
            Map<String,List<ProjectStaSubareaDayEntity>> result = projectStaSubareaDayMapper.selectList(lw).stream().collect(Collectors.groupingBy(ProjectStaSubareaDayEntity::getSubareaName));
            Map<String,BigDecimal> response = new HashMap<>();
            result.entrySet().forEach(stringListEntry -> {
                String key  =stringListEntry.getKey();
                response.put(key,stringListEntry.getValue().stream().map(ProjectStaSubareaDayEntity::getStaValue).reduce(BigDecimal.ZERO,BigDecimal::add));
            });
            return response;

        }
    }

    private List<ProjectCnfSubareaEntity> getCnfs(SubareaMonthRequest request){
        LambdaQueryWrapper<ProjectCnfSubareaEntity> lw = new LambdaQueryWrapper<>();
        lw.eq(ProjectCnfSubareaEntity::getProjectId,request.getProjectId());
        lw.in(ProjectCnfSubareaEntity::getName,request.getSubareaName());
        lw.in(ProjectCnfSubareaEntity::getKpiType,request.getKpiType());
        List<ProjectCnfSubareaEntity> result = projectCnfSubareaMapper.selectList(lw);
        return result;
    }
}
