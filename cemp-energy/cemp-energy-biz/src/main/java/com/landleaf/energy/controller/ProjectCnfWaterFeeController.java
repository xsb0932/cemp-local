package com.landleaf.energy.controller;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.energy.domain.dto.ProjectCnfWaterFeeAddDTO;
import com.landleaf.energy.domain.dto.SubitemRelationDevicesDTO;
import com.landleaf.energy.domain.vo.ProjectCnfWaterFeeVO;
import com.landleaf.energy.service.ProjectCnfWaterFeeService;
import com.landleaf.web.util.ValidatorUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * 用水费用配置表的控制层接口定义
 *
 * @author hebin
 * @since 2023-07-04
 */
@RestController
@AllArgsConstructor
@RequestMapping("/project-cnf-water-fee")
@Tag(name = "用水费用配置表的控制层接口定义", description = "用水费用配置表的控制层接口定义")
public class ProjectCnfWaterFeeController {
    /**
     * 用水费用配置表的相关逻辑操作句柄
     */
    private final ProjectCnfWaterFeeService projectCnfWaterFeeServiceImpl;

    /**
     * 新增或修改用水费用配置表数据
     *
     * @param addInfo 新增或修改的对象实体封装
     * @return 成功后返回保存的实体信息
     */
    @PostMapping("/save")
    @Operation(summary = "新增", description = "传入ProjectCnfWaterFeeAddDTO")
    public Response<ProjectCnfWaterFeeAddDTO> save(@RequestBody @Valid ProjectCnfWaterFeeAddDTO addInfo) {
        if (null == addInfo.getId()) {
            addInfo = projectCnfWaterFeeServiceImpl.save(addInfo);
        } else {
            projectCnfWaterFeeServiceImpl.update(addInfo);
        }
        return Response.success(addInfo);
    }

//    /**
//     * 根据编号，删除用水费用配置表数据（逻辑删除）
//     *
//     * @param ids 要删除的ids的编号
//     * @return 成功返回true
//     */
//    @PostMapping("/remove")
//    @Operation(summary = "根据编号，删除用水费用配置表信息", description = "传入ids,多个以逗号分隔")
//    public Response<Boolean> update(@Parameter(description = "需要删除的id，多个以逗号分隔") @RequestParam("id") String ids) {
//        projectCnfWaterFeeServiceImpl.updateIsDeleted(ids, CommonConstant.DELETED_FLAG_DELETED);
//        return Response.success(true);
//    }

    /**
     * 根据编号，查询用水费用配置表详情数据
     *
     * @param bizProjectId 要查询的id编号
     * @return 成功返回true
     */
    @GetMapping("/detail")
    @Operation(summary = "根据bizProjectId查询用水费用配置表详情", description = "传入bizProjectId")
    public Response<ProjectCnfWaterFeeVO> get(@Parameter(description = "需要查询的id") @RequestParam("bizProjectId") String bizProjectId) {
        ProjectCnfWaterFeeVO entity = projectCnfWaterFeeServiceImpl.selectByBizProjectId(bizProjectId);
        return Response.success(entity);
    }

//    /**
//     * 查询用水费用配置表列表数据
//     *
//     * @param queryInfo 查询参数封装
//     * @return 返回数据的列表
//     */
//    @GetMapping("/list")
//    @Operation(summary = "查询用水费用配置表列表数据", description = "")
//    public Response<List<ProjectCnfWaterFeeVO>> list(ProjectCnfWaterFeeQueryDTO queryInfo) {
//        List<ProjectCnfWaterFeeEntity> cdList = projectCnfWaterFeeServiceImpl.list(queryInfo);
//        return Response.success(ProjectCnfWaterFeeWrapper.builder().listEntity2VO(cdList));
//    }
//
//    /**
//     * 分页查询用水费用配置表列表数据
//     *
//     * @param queryInfo 查询参数封装
//     * @return 返回数据的列表
//     */
//    @GetMapping("/page")
//    @Operation(summary = "分页查询用水费用配置表列表数据", description = "")
//    public Response<PageDTO<ProjectCnfWaterFeeVO>> page(ProjectCnfWaterFeeQueryDTO queryInfo) {
//        IPage<ProjectCnfWaterFeeEntity> page = projectCnfWaterFeeServiceImpl.page(queryInfo);
//        return Response.success(ProjectCnfWaterFeeWrapper.builder().pageEntity2VO(page));
//    }
}