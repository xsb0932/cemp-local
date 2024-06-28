package com.landleaf.energy.controller;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.collection.CollectionUtil;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.exception.enums.GlobalErrorCodeConstants;
import com.landleaf.energy.domain.dto.ProjectCnfTimePeriodRmDTO;
import com.landleaf.energy.domain.dto.TimeDuringDTO;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.landleaf.comm.base.pojo.Response;

import com.landleaf.energy.domain.dto.ProjectCnfTimePeriodAddDTO;
import com.landleaf.energy.domain.vo.ProjectCnfTimePeriodVO;
import com.landleaf.energy.service.ProjectCnfTimePeriodService;

import lombok.AllArgsConstructor;


/**
 * 配置表-分时的控制层接口定义
 *
 * @author hebin
 * @since 2023-07-04
 */
@RestController
@AllArgsConstructor
@RequestMapping("/project-cnf-time-period")
@Tag(name = "配置表-分时的控制层接口定义", description = "配置表-分时的控制层接口定义")
public class ProjectCnfTimePeriodController {
    /**
     * 配置表-分时的相关逻辑操作句柄
     */
    private final ProjectCnfTimePeriodService projectCnfTimePeriodServiceImpl;

    /**
     * 新增或修改配置表-分时数据
     *
     * @param addInfo 新增或修改的对象实体封装
     * @return 成功后返回保存的实体信息
     */
    @PostMapping("/save")
    @Operation(summary = "新增", description = "传入ProjectCnfTimePeriodAddDTO")
    public Response<Boolean> save(@RequestBody @Valid ProjectCnfTimePeriodAddDTO addInfo) {
        boolean result = projectCnfTimePeriodServiceImpl.intelligentInsert(addInfo);
        return Response.success(result);
    }

    /**
     * 根据编号，删除配置表-分时数据（逻辑删除）
     *
     * @param rmDTO 要删除的ids的编号
     * @return 成功返回true
     */
    @PostMapping("/remove")
    @Operation(summary = "根据bizProject和年月，删除对应的电费配置", description = "根据bizProject和年月，删除对应的电费配置")
    public Response<Boolean> update(@RequestBody ProjectCnfTimePeriodRmDTO rmDTO) {
        projectCnfTimePeriodServiceImpl.removeByProjIdAndTime(rmDTO);
        return Response.success(true);
    }

//    /**
//     * 根据编号，查询配置表-分时详情数据
//     *
//     * @param id 要查询的id编号
//     * @return 成功返回true
//     */
//    @GetMapping("/detail")
//    @Operation(summary = "根据id查询配置表-分时详情", description = "传入ids,多个以逗号分隔")
//    public Response<ProjectCnfTimePeriodVO> get(@Parameter(description = "需要查询的id") @RequestParam("id") Long id) {
//        ProjectCnfTimePeriodEntity entity = projectCnfTimePeriodServiceImpl.selectById(id);
//        return Response.success(ProjectCnfTimePeriodWrapper.builder().entity2VO(entity));
//    }

    /**
     * 查询配置表-分时列表数据
     *
     * @param bizProjectId 查询参数封装
     * @return 返回数据的列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询配置表-分时列表数据", description = "")
    public Response<List<ProjectCnfTimePeriodVO>> list(@Parameter(description = "需要查询的id") @RequestParam("bizProjectId") String bizProjectId) {
        List<ProjectCnfTimePeriodVO> cdList = projectCnfTimePeriodServiceImpl.listByBizProjectId(bizProjectId);
        return Response.success(cdList);
    }
}