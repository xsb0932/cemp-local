package com.landleaf.energy.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.landleaf.energy.domain.dto.SubitemRelationDevicesDTO;
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

import com.landleaf.energy.domain.dto.ProjectCnfGasFeeAddDTO;
import com.landleaf.energy.domain.dto.ProjectCnfGasFeeQueryDTO;
import com.landleaf.energy.domain.entity.ProjectCnfGasFeeEntity;
import com.landleaf.energy.domain.vo.ProjectCnfGasFeeVO;
import com.landleaf.energy.domain.wrapper.ProjectCnfGasFeeWrapper;
import com.landleaf.energy.service.ProjectCnfGasFeeService;

import lombok.AllArgsConstructor;


/**
 * 燃气费用配置表的控制层接口定义
 *
 * @author hebin
 * @since 2023-07-04
 */
@RestController
@AllArgsConstructor
@RequestMapping("/project-cnf-gas-fee")
@Tag(name = "燃气费用配置表的控制层接口定义", description = "燃气费用配置表的控制层接口定义")
public class ProjectCnfGasFeeController {
    /**
     * 燃气费用配置表的相关逻辑操作句柄
     */
    private final ProjectCnfGasFeeService projectCnfGasFeeServiceImpl;

    /**
     * 新增或修改燃气费用配置表数据
     *
     * @param addInfo 新增或修改的对象实体封装
     * @return 成功后返回保存的实体信息
     */
    @PostMapping("/save")
    @Operation(summary = "新增", description = "传入ProjectCnfGasFeeAddDTO")
    public Response<ProjectCnfGasFeeAddDTO> save(@RequestBody @Valid ProjectCnfGasFeeAddDTO addInfo) {
        if (null == addInfo.getId()) {
            addInfo = projectCnfGasFeeServiceImpl.save(addInfo);
        } else {
            projectCnfGasFeeServiceImpl.update(addInfo);
        }
        return Response.success(addInfo);
    }

//    /**
//     * 根据编号，删除燃气费用配置表数据（逻辑删除）
//     *
//     * @param ids 要删除的ids的编号
//     * @return 成功返回true
//     */
//    @PostMapping("/remove")
//    @Operation(summary = "根据编号，删除燃气费用配置表信息", description = "传入ids,多个以逗号分隔")
//    public Response<Boolean> update(@Parameter(description = "需要删除的id，多个以逗号分隔") @RequestParam("id") String ids) {
//        projectCnfGasFeeServiceImpl.updateIsDeleted(ids, CommonConstant.DELETED_FLAG_DELETED);
//        return Response.success(true);
//    }

    /**
     * 根据bizProjectId，查询燃气费用配置表详情数据
     *
     * @param bizProjectId 要查询的bizProjectId编号
     * @return 成功返回true
     */
    @GetMapping("/detail")
    @Operation(summary = "根据bizProjectId查询燃气费用配置表详情", description = "传入bizProjectId")
    public Response<ProjectCnfGasFeeVO> get(@Parameter(description = "需要查询的id") @RequestParam("bizProjectId") String bizProjectId) {
        ProjectCnfGasFeeVO entity = projectCnfGasFeeServiceImpl.selectByBizProjectId(bizProjectId);
        return Response.success(entity);
    }
}