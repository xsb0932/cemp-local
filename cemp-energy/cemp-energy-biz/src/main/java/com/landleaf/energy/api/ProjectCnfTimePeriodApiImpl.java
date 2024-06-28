package com.landleaf.energy.api;

import cn.hutool.core.bean.BeanUtil;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.energy.dal.mapper.ProjectCnfTimePeriodMapper;
import com.landleaf.energy.domain.entity.ProjectCnfTimePeriodEntity;
import com.landleaf.energy.request.ProjectCnfTimePeriodResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * ProjectCnfTimePeriodApiImpl
 *
 * @author 张力方
 * @since 2023/8/2
 **/
@RestController
@RequiredArgsConstructor
public class ProjectCnfTimePeriodApiImpl implements ProjectCnfTimePeriodApi {
    private final ProjectCnfTimePeriodMapper projectCnfTimePeriodMapper;

    @Override
    public Response<List<ProjectCnfTimePeriodResponse>> getElectricityPrice(String projectId) {
        TenantContext.setIgnore(true);
        LocalDate now = LocalDate.now();
        List<ProjectCnfTimePeriodEntity> projectCnfTimePeriodEntities = projectCnfTimePeriodMapper.searchByProjectYearMonth(projectId, now.getYear(), now.getMonthValue());
        List<ProjectCnfTimePeriodResponse> projectCnfTimePeriodResponses = BeanUtil.copyToList(projectCnfTimePeriodEntities, ProjectCnfTimePeriodResponse.class);
        return Response.success(projectCnfTimePeriodResponses);
    }
}
