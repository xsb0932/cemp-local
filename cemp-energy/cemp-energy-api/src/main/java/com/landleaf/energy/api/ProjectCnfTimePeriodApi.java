package com.landleaf.energy.api;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.energy.enums.ApiConstants;
import com.landleaf.energy.request.ProjectCnfTimePeriodResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign 服务 - 电耗分时api
 *
 * @author 张力方
 * @since 2023/8/2
 **/
@Tag(name = "Feign 服务 - 电耗分时api")
@FeignClient(name = ApiConstants.NAME)
public interface ProjectCnfTimePeriodApi {
    /**
     * 获取当前项目当月电价
     *
     * @param projectId 项目id
     * @return 结果
     */
    @GetMapping(ApiConstants.PREFIX + "/electricity/price")
    Response<List<ProjectCnfTimePeriodResponse>> getElectricityPrice(@RequestParam("projectId") String projectId);

}
