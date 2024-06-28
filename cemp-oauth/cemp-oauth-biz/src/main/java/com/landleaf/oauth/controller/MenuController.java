package com.landleaf.oauth.controller;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.oauth.domain.request.MenuCreateRequest;
import com.landleaf.oauth.domain.request.MenuUpdateRequest;
import com.landleaf.oauth.domain.response.MenuTabulationResponse;
import com.landleaf.oauth.domain.response.ModuleMenuTabulationResponse;
import com.landleaf.oauth.domain.response.ModuleResponse;
import com.landleaf.oauth.service.MenuService;
import com.landleaf.operatelog.core.annotations.OperateLog;
import com.landleaf.operatelog.core.enums.ModuleTypeEnums;
import com.landleaf.operatelog.core.enums.OperateTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单接口
 *
 * @author yue lin
 * @since 2023/6/1 9:56
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/menu")
@Tag(name = "菜单接口")
public class MenuController {

    private final MenuService menuService;

    /**
     * 当前用户，所在租户的菜单查询列表
     *
     * @return 结果集
     */
    @Operation(summary = "当前租户菜单查询列表")
    @GetMapping("/menus/tenant")
    public Response<List<ModuleMenuTabulationResponse>> searchMenuTabulationByTenant() {
        return Response.success(menuService.searchMenuTabulationByTenant());
    }

    /**
     * 系统模块菜单查询列表
     *
     * @return 结果集
     */
    @Operation(summary = "系统模块菜单查询列表")
    @GetMapping("/system/module/menus")
    public Response<List<ModuleMenuTabulationResponse>> searchMenuTabulation() {
        return Response.success(menuService.searchMenuTabulation());
    }

    /**
     * 获取指定租户下的菜单查询列表
     *
     * @return 结果集
     */
    @Operation(summary = "当前租户菜单查询列表")
    @GetMapping("/menus/tenant/{tenantId}")
    public Response<List<ModuleMenuTabulationResponse>> searchMenuTabulations(@PathVariable("tenantId") String tenantId) {
        return Response.success(menuService.searchMenuTabulations(tenantId));
    }

    /**
     * 当前用户拥有的菜单查询列表
     *
     * @return 结果集
     */
    @Operation(summary = "当前用户拥有的菜单查询列表")
    @GetMapping("/menus/user")
    public Response<List<ModuleMenuTabulationResponse>> searchMenuTabulationByUser() {
        return Response.success(menuService.searchMenuTabulationByUser());
    }

    /**
     * 平台管理员新增菜单
     *
     * @param menuCreateRequest 参数
     * @return 结果集
     */
    @Operation(summary = "平台管理员新增菜单")
    @PostMapping("/creation")
    @OperateLog(module = ModuleTypeEnums.OAUTH, name = "新增菜单" , type = OperateTypeEnum.CREATE)
    public Response<Void> createMenu(@Validated @RequestBody MenuCreateRequest menuCreateRequest) {
        menuService.createMenu(menuCreateRequest);
        return Response.success();
    }

    /**
     * 更新菜单
     *
     * @param menuUpdateRequest 参数
     * @return 结果集
     */
    @Operation(summary = "更新菜单")
    @PutMapping("/change")
    @OperateLog(module = ModuleTypeEnums.OAUTH, name = "更新菜单" , type = OperateTypeEnum.UPDATE)
    public Response<Void> updateMenu(@Validated @RequestBody MenuUpdateRequest menuUpdateRequest) {
        menuService.updateMenu(menuUpdateRequest);
        return Response.success();
    }

    /**
     * 删除菜单
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Operation(summary = "删除菜单")
    @DeleteMapping("/{menuId}")
    @OperateLog(module = ModuleTypeEnums.OAUTH, name = "删除菜单" , type = OperateTypeEnum.DELETE)
    public Response<Void> updateMenu(@PathVariable("menuId") Long menuId) {
        menuService.deleteMenu(menuId);
        return Response.success();
    }

    /**
     * 查询模块列表（当前租户）
     * @return 结果
     */
    @GetMapping("/modules")
    @Operation(summary = "查询模块列表（当前租户）")
    public Response<List<ModuleResponse>> searchModules() {
        return Response.success(menuService.searchModules());
    }

    /**
     * 查询模块下菜单列表
     * @param moduleId 模块ID
     * @return  结果
     */
    @GetMapping("/module/menus")
    @Operation(summary = "查询模块下菜单列表")
    public Response<List<MenuTabulationResponse>> searchModuleMenus(@RequestParam Long moduleId) {
        return Response.success(menuService.searchModuleMenus(moduleId));
    }

}
