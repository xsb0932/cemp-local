package com.landleaf.energy.api;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.energy.dal.mapper.ProjectStaSubitemDayMapper;
import com.landleaf.energy.domain.entity.ProjectStaSubitemDayEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class ProjectStaSubitemDayApiImpl implements ProjectStaSubitemDayApi {
    private final ProjectStaSubitemDayMapper projectStaSubitemDayMapper;

    @Override
    public Response<BigDecimal> getProjectElectricityDurationTotal(String bizProjectId, LocalDate begin, LocalDate end) {
        TenantContext.setIgnore(true);
        try {
            BigDecimal total = BigDecimal.ZERO;
            while (begin.compareTo(end) <= 0) {
                ProjectStaSubitemDayEntity entity = projectStaSubitemDayMapper.selectOne(new LambdaQueryWrapper<ProjectStaSubitemDayEntity>()
                        .eq(ProjectStaSubitemDayEntity::getBizProjectId, bizProjectId)
                        .eq(ProjectStaSubitemDayEntity::getYear, String.valueOf(begin.getYear()))
                        .eq(ProjectStaSubitemDayEntity::getMonth, String.valueOf(begin.getMonthValue()))
                        .eq(ProjectStaSubitemDayEntity::getDay, String.valueOf(begin.getDayOfMonth())));
                if (null != entity && null != entity.getProjectElectricityEnergyusageTotal()) {
                    total = total.add(entity.getProjectElectricityEnergyusageTotal());
                }
                begin = begin.plusDays(1L);
            }
            return Response.success(total);
        } finally {
            TenantContext.setIgnore(false);
        }
    }

    @Override
    public Response<BigDecimal> getProjectGasDurationTotal(String bizProjectId, LocalDate begin, LocalDate end) {
        TenantContext.setIgnore(true);
        try {
            BigDecimal total = BigDecimal.ZERO;
            while (begin.compareTo(end) <= 0) {
                ProjectStaSubitemDayEntity entity = projectStaSubitemDayMapper.selectOne(new LambdaQueryWrapper<ProjectStaSubitemDayEntity>()
                        .eq(ProjectStaSubitemDayEntity::getBizProjectId, bizProjectId)
                        .eq(ProjectStaSubitemDayEntity::getYear, String.valueOf(begin.getYear()))
                        .eq(ProjectStaSubitemDayEntity::getMonth, String.valueOf(begin.getMonthValue()))
                        .eq(ProjectStaSubitemDayEntity::getDay, String.valueOf(begin.getDayOfMonth())));
                if (null != entity && null != entity.getProjectGasUsageTotal()) {
                    total = total.add(entity.getProjectGasUsageTotal());
                }
                begin = begin.plusDays(1L);
            }
            return Response.success(total);
        } finally {
            TenantContext.setIgnore(false);
        }
    }

    @Override
    public Response<BigDecimal> getProjectWaterDurationTotal(String bizProjectId, LocalDate begin, LocalDate end) {
        TenantContext.setIgnore(true);
        try {
            BigDecimal total = BigDecimal.ZERO;
            while (begin.compareTo(end) <= 0) {
                ProjectStaSubitemDayEntity entity = projectStaSubitemDayMapper.selectOne(new LambdaQueryWrapper<ProjectStaSubitemDayEntity>()
                        .eq(ProjectStaSubitemDayEntity::getBizProjectId, bizProjectId)
                        .eq(ProjectStaSubitemDayEntity::getYear, String.valueOf(begin.getYear()))
                        .eq(ProjectStaSubitemDayEntity::getMonth, String.valueOf(begin.getMonthValue()))
                        .eq(ProjectStaSubitemDayEntity::getDay, String.valueOf(begin.getDayOfMonth())));
                if (null != entity && null != entity.getProjectWaterUsageTotal()) {
                    total = total.add(entity.getProjectWaterUsageTotal());
                }
                begin = begin.plusDays(1L);
            }
            return Response.success(total);
        } finally {
            TenantContext.setIgnore(false);
        }
    }
}
