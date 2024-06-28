package com.landleaf.energy.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.landleaf.comm.base.pojo.Response;

import com.landleaf.energy.domain.dto.ProjectCnfElectricityPriceAddDTO;
import com.landleaf.energy.domain.vo.ProjectCnfElectricityPriceVO;
import com.landleaf.energy.service.ProjectCnfElectricityPriceService;

import lombok.AllArgsConstructor;


/**
 * 电费配置表的控制层接口定义
 *
 * @author hebin
 * @since 2024-03-20
 */
@RestController
@AllArgsConstructor
@RequestMapping("/project-cnf-electricity-price")
@Tag(name = "电费配置表的控制层接口定义", description = "电费配置表的控制层接口定义")
public class ProjectCnfElectricityPriceController {
    /**
     * 电费配置表的相关逻辑操作句柄
     */
    private final ProjectCnfElectricityPriceService projectCnfElectricityPriceServiceImpl;

    /**
     * 新增或修改电费配置表数据
     *
     * @param addInfo 新增或修改的对象实体封装
     * @return 成功后返回保存的实体信息
     */
    @PostMapping("/save")
    @Operation(summary = "新增", description = "传入ProjectCnfElectricityPriceAddDTO")
    public Response<ProjectCnfElectricityPriceAddDTO> save(@RequestBody @Valid ProjectCnfElectricityPriceAddDTO addInfo) {
        if (null == addInfo.getId()) {
            addInfo = projectCnfElectricityPriceServiceImpl.save(addInfo);
        } else {
            projectCnfElectricityPriceServiceImpl.update(addInfo);
        }
        return Response.success(addInfo);
    }

    /**
     * 根据编号，查询电费配置表详情数据
     *
     * @param bizProjectId 需要查询的bizProjectId
     * @return 成功返回true
     */
    @GetMapping("/detail")
    @Operation(summary = "根据id查询电费配置表详情", description = "需要查询的bizProjectId")
    public Response<ProjectCnfElectricityPriceVO> get(@Parameter(description = "需要查询的bizProjectId") @RequestParam("bizProjectId") String bizProjectId) {
        ProjectCnfElectricityPriceVO entity = projectCnfElectricityPriceServiceImpl.selectDetailById(bizProjectId);
        return Response.success(entity);
    }
}