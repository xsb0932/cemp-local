package com.landleaf.energy.controller;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.landleaf.comm.sta.enums.KpiTypeCodeEnum;
import com.landleaf.energy.domain.entity.DeviceMonitorEntity;
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

import com.landleaf.energy.domain.dto.ProjectCnfSubareaAddDTO;
import com.landleaf.energy.domain.dto.ProjectCnfSubareaQueryDTO;
import com.landleaf.energy.domain.entity.ProjectCnfSubareaEntity;
import com.landleaf.energy.domain.vo.ProjectCnfSubareaVO;
import com.landleaf.energy.domain.wrapper.ProjectCnfSubareaWrapper;
import com.landleaf.energy.service.ProjectCnfSubareaService;

import lombok.AllArgsConstructor;


/**
 * 配置表-分区的控制层接口定义
 *
 * @author hebin
 * @since 2023-07-04
 */
@RestController
@AllArgsConstructor
@RequestMapping("/project-cnf-subarea")
@Tag(name = "配置表-分区的控制层接口定义", description = "配置表-分区的控制层接口定义")
public class ProjectCnfSubareaController {
    /**
     * 配置表-分区的相关逻辑操作句柄
     */
    private final ProjectCnfSubareaService projectCnfSubareaServiceImpl;


    /**
     * 新增或修改配置表-分区数据
     *
     * @param addInfo 新增或修改的对象实体封装
     * @return 成功后返回保存的实体信息
     */
    @PostMapping("/save")
    @Operation(summary = "新增", description = "传入ProjectCnfSubareaAddDTO")
    public Response add(@RequestBody @Valid ProjectCnfSubareaAddDTO addInfo) {
        projectCnfSubareaServiceImpl.add(addInfo);
        return Response.success();
    }

    /**
     * 新增配置表-分区数据
     *
     * @param addInfo 新增对象实体封装
     * @return 成功后返回保存的实体信息
     */
    @PostMapping("/edit")
    @Operation(summary = "编辑", description = "传入ProjectCnfSubareaAddDTO")
    public Response edit(@RequestBody @Valid ProjectCnfSubareaAddDTO addInfo) {
        projectCnfSubareaServiceImpl.edit(addInfo);
        return Response.success();
    }

    /**
     * 删除
     *
     * @return 成功返回true
     */
    @PostMapping("/delete")
    @Operation(summary = "删除分区", description = "删除分区")
    public Response<Boolean> delete(@Parameter(description = "分区id") @RequestParam("id") String id) {
        projectCnfSubareaServiceImpl.delete(id);
        return Response.success(true);
    }

    /**
     * 从空间分区导入
     *
     * @param
     * @return 成功返回true
     */
    @PostMapping("/space/import")
    @Operation(summary = "从空间分区导入", description = "从空间分区导入")
    public Response<Boolean> spaceImport(@Parameter(description = "项目id") @RequestParam("bizProjectId") String bizProjectId) {
        List<ProjectCnfSubareaEntity> result = projectCnfSubareaServiceImpl.list(new LambdaQueryWrapper<ProjectCnfSubareaEntity>().eq(ProjectCnfSubareaEntity::getProjectId, bizProjectId));
        if (result != null && result.size() > 0) {
            return Response.error("500", "项目已配置分区.");
        }
        //空间导入
        projectCnfSubareaServiceImpl.batchImport(bizProjectId);
        return Response.success(true);
    }

    /**
     * 分区详情
     *
     * @param id 要查询的id编号
     * @return 成功返回true
     */
    @GetMapping("/detail")
    @Operation(summary = "分区详情", description = "分区详情")
    public Response<ProjectCnfSubareaVO> get(@Parameter(description = "分区id") @RequestParam("id") Long id) {
        return Response.success(projectCnfSubareaServiceImpl.detail(id));
    }

    /**
     * 查询配置表-分区列表数据
     *
     * @return 返回数据的列表
     */
    @GetMapping("/listAll")
    @Operation(summary = "分区列表树", description = "分区列表树")
    public Response<List<ProjectCnfSubareaVO>> listAll(@Parameter(description = "项目ID") @RequestParam("bizProjectId") String bizProjectId, @Parameter(description = "指标大类代码：1电2水3气4碳") @RequestParam(name = "kpiTypeCode", required = false) String kpiTypeCode) {
        if (null == kpiTypeCode) {
            // 为空默认为电，为了保护之前的逻辑
            kpiTypeCode = KpiTypeCodeEnum.KPI_TYPE_ELECTRICITY.getCode();
        }
        List<ProjectCnfSubareaVO> result = projectCnfSubareaServiceImpl.listAll(bizProjectId, kpiTypeCode);
        return Response.success(result);
    }

    /**
     * 查询配置表-分区列表数据
     *
     * @return 返回数据的列表
     */
    @GetMapping("/devices/all")
    @Operation(summary = "查询所有设备", description = "查询所有设备")
    public Response<List<DeviceMonitorEntity>> allDevices(@Parameter(description = "项目ID") @RequestParam("bizProjectId") String bizProjectId) {
        List<DeviceMonitorEntity> result = projectCnfSubareaServiceImpl.allDevices(bizProjectId);
        return Response.success(result);
    }


}
