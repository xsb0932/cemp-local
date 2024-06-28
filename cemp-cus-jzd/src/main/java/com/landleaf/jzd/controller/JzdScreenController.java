package com.landleaf.jzd.controller;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.jzd.domain.enums.JzdConstants;
import com.landleaf.jzd.domain.response.JzdCurrentDatResponse;
import com.landleaf.jzd.domain.response.JzdOverviewResponse;
import com.landleaf.jzd.service.JzdScreenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 金智达定制大屏
 *
 * @author xushibai
 * @since 2023/01/22
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/screen")
@Tag(name = "金智达定制大屏相关接口", description = "金智达定制大屏相关接口")
public class JzdScreenController {

    private final JzdScreenService jzdScreenService;
    /**
     * 总览
     *
     */
    @GetMapping("/overview/page")
    @Operation(summary = "总览")
    public Response<JzdOverviewResponse> overview() {
        TenantContext.setTenantId(JzdConstants.JZD_TENANT_ID);
        String projectBizId = JzdConstants.PROJECT_BIZ_ID;
        JzdOverviewResponse overview = jzdScreenService.overview(projectBizId);
        return Response.success(overview);
    }

    /**
     * 实时数据
     *
     */
    @GetMapping("/current/page")
    @Operation(summary = "实时数据")
    public Response<JzdCurrentDatResponse> current() {
        TenantContext.setTenantId(JzdConstants.JZD_TENANT_ID);
        String projectBizId = JzdConstants.PROJECT_BIZ_ID;
        JzdCurrentDatResponse overview = jzdScreenService.current(projectBizId);
        return Response.success(overview);
    }


}
