package com.landleaf.sdl.controller;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.sdl.domain.enums.SDLConstants;
import com.landleaf.sdl.domain.response.SDLCurrentDataPage1Response;
import com.landleaf.sdl.domain.response.SDLCurrentDataPage2Response;
import com.landleaf.sdl.domain.response.SDLOverviewPage1Response;
import com.landleaf.sdl.domain.response.SDLOverviewPage2Response;
import com.landleaf.sdl.service.SDLScreenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 绥德路定制大屏
 *
 * @author xushibai
 * @since 2023/11/29
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/screen")
@Tag(name = "绥德路定制大屏相关接口", description = "绥德路定制大屏相关接口")
public class ScreenController {

    private final SDLScreenService screenService;

    /**
     * 总览- 大屏1
     *
     */
    @GetMapping("/overview/page1")
    @Operation(summary = "总览- 大屏1")
    public Response<SDLOverviewPage1Response> overview1() {
        TenantContext.setTenantId(SDLConstants.SDL_TENANT_ID);
        String projectBizId = SDLConstants.PROJECT_BIZ_ID;
        SDLOverviewPage1Response overview = screenService.overview1(projectBizId);
        return Response.success(overview);
    }

    /**
     * 总览- 大屏2
     *
     *
     */
    @GetMapping("/overview/page2")
    @Operation(summary = "总览- 大屏2")
    public Response<SDLOverviewPage2Response> overview2() {
        TenantContext.setTenantId(SDLConstants.SDL_TENANT_ID);
        String projectBizId = SDLConstants.PROJECT_BIZ_ID;
        SDLOverviewPage2Response overview = screenService.overview2(projectBizId);
        return Response.success(overview);
    }



    /**
     * 实时数据- 大屏1
     *
     */
    @GetMapping("/current/page1")
    @Operation(summary = "实时数据- 大屏1")
    public Response<SDLCurrentDataPage1Response> current1() {
        TenantContext.setTenantId(SDLConstants.SDL_TENANT_ID);
        String projectBizId = SDLConstants.PROJECT_BIZ_ID;
        SDLCurrentDataPage1Response current = screenService.current1(projectBizId);
        return Response.success(current);
    }

    /**
     * 实时数据- 大屏2
     *
     */
    @GetMapping("/current/page2")
    @Operation(summary = "实时数据- 大屏2")
    public Response<SDLCurrentDataPage2Response> current2() {
        TenantContext.setTenantId(SDLConstants.SDL_TENANT_ID);
        String projectBizId = SDLConstants.PROJECT_BIZ_ID;
        SDLCurrentDataPage2Response current = screenService.current2(projectBizId);
        return Response.success(current);
    }

}
