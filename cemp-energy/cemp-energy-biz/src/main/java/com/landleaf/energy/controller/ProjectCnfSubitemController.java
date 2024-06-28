package com.landleaf.energy.controller;

import java.util.List;

import com.landleaf.comm.sta.enums.KpiTypeCodeEnum;
import jakarta.validation.Valid;
import com.landleaf.web.util.ValidatorUtil;

import com.landleaf.comm.constance.CommonConstant;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.landleaf.comm.base.pojo.Response;

import com.landleaf.energy.domain.dto.ProjectCnfSubitemAddDTO;
import com.landleaf.energy.domain.dto.ProjectCnfSubitemQueryDTO;
import com.landleaf.energy.domain.entity.ProjectCnfSubitemEntity;
import com.landleaf.energy.domain.vo.ProjectCnfSubitemVO;
import com.landleaf.energy.domain.wrapper.ProjectCnfSubitemWrapper;
import com.landleaf.energy.service.ProjectCnfSubitemService;

import lombok.AllArgsConstructor;


/**
 * 配置表-分项的控制层接口定义
 *
 * @author hebin
 * @since 2023-07-06
 */
@RestController
@AllArgsConstructor
@RequestMapping("/project-cnf-subitem")
@Tag(name = "配置表-分项的控制层接口定义", description = "配置表-分项的控制层接口定义")
public class ProjectCnfSubitemController {
    /**
     * 配置表-分项的相关逻辑操作句柄
     */
    private final ProjectCnfSubitemService projectCnfSubitemServiceImpl;

    /**
     * 新增分项
     *
     * @param addInfo
     * @return
     */
    @PostMapping("/save")
    @Operation(summary = "新增分项", description = "新增分项")
    public Response save(@RequestBody @Valid ProjectCnfSubitemAddDTO addInfo) {
        projectCnfSubitemServiceImpl.add(addInfo);
        return Response.success();
    }

    /**
     * 修改分项
     *
     * @param addInfo
     * @return
     */
    @PostMapping("/edit")
    @Operation(summary = "修改分项", description = "修改分项")
    public Response edit(@RequestBody @Valid ProjectCnfSubitemAddDTO addInfo) {
        projectCnfSubitemServiceImpl.edit(addInfo);
        return Response.success();
    }

    /**
     * 删除分项
     *
     * @return
     */
    @PostMapping("/delete")
    @Operation(summary = "删除分项", description = "删除分项")
    public Response delete(@Parameter(description = "分项id") @RequestParam("id") String id) {
        projectCnfSubitemServiceImpl.delete(id);
        return Response.success();
    }


    /**
     * kpi描述
     *
     * @param code kpi代码
     * @return 成功返回true
     */
    @GetMapping("/kpi/desc")
    @Operation(summary = "kpi描述", description = "kpi描述")
    public Response<String> kpiDesc(@Parameter(description = "kpi代码") @RequestParam("code") String code) {
        return Response.success(projectCnfSubitemServiceImpl.kpiDesc(code));
    }

    /**
     * 分项明细
     *
     * @param id 要查询的id编号
     * @return 成功返回true
     */
    @GetMapping("/detail")
    @Operation(summary = "分项详情", description = "分项详情")
    public Response<ProjectCnfSubitemVO> get(@Parameter(description = "分项id") @RequestParam("id") Long id) {
        return Response.success(projectCnfSubitemServiceImpl.detail(id));
    }

    /**
     * 查询配置表-分项列表数据
     *
     * @return 返回数据的列表
     */
    @GetMapping("/listAll")
    @Operation(summary = "分项列表树", description = "分项列表树")
    public Response<List<ProjectCnfSubitemVO>> listAll(@Parameter(description = "项目ID") @RequestParam("bizProjectId") String bizProjectId, @Parameter(description = "指标大类代码：1电2水3气4碳") @RequestParam(name = "kpiTypeCode", required = false) String kpiTypeCode) {
        if (null == kpiTypeCode) {
            // 为空默认为电，为了保护之前的逻辑
            kpiTypeCode = KpiTypeCodeEnum.KPI_TYPE_ELECTRICITY.getCode();
        }
        List<ProjectCnfSubitemVO> cdList = projectCnfSubitemServiceImpl.listAll(bizProjectId, kpiTypeCode);
        return Response.success(cdList);
    }

}
