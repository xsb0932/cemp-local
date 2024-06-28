package com.landleaf.engine.controller;

import java.util.List;

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

import com.landleaf.engine.domain.dto.RuleTriggerAddDTO;
import com.landleaf.engine.domain.dto.RuleTriggerQueryDTO;
import com.landleaf.engine.domain.entity.RuleTriggerEntity;
import com.landleaf.engine.domain.vo.RuleTriggerVO;
import com.landleaf.engine.domain.wrapper.RuleTriggerWrapper;
import com.landleaf.engine.service.RuleTriggerService;

import lombok.AllArgsConstructor;


/**
 * RuleTriggerEntity对象的控制层接口定义
 *
 * @author hebin
 * @since 2024-04-23
 */
@RestController
@AllArgsConstructor
@RequestMapping("/rule-trigger")
@Tag(name = "RuleTriggerEntity对象的控制层接口定义", description = "RuleTriggerEntity对象的控制层接口定义")
public class RuleTriggerController {
/**
 * RuleTriggerEntity对象的相关逻辑操作句柄
 */
private final RuleTriggerService ruleTriggerServiceImpl;

/**
 * 新增或修改RuleTriggerEntity对象数据
 *
 * @param addInfo
 *            新增或修改的对象实体封装
 * @return 成功后返回保存的实体信息
 */
@PostMapping("/save")
@Operation(summary = "新增", description = "传入RuleTriggerAddDTO")
public Response<RuleTriggerAddDTO> save(@RequestBody @Valid RuleTriggerAddDTO addInfo){
        if(null==addInfo.getId()){
        ValidatorUtil.validate(addInfo, RuleTriggerAddDTO.AddGroup.class);
        addInfo= ruleTriggerServiceImpl.save(addInfo);
        }else{
        ValidatorUtil.validate(addInfo, RuleTriggerAddDTO.AddGroup.class);
    ruleTriggerServiceImpl.update(addInfo);
        }
        return Response.success(addInfo);
        }

/**
 * 根据编号，删除RuleTriggerEntity对象数据（逻辑删除）
 *
 * @param ids
 *            要删除的ids的编号
 * @return 成功返回true
 */
@PostMapping("/remove")
        @Operation(summary = "根据编号，删除RuleTriggerEntity对象信息", description = "传入ids,多个以逗号分隔")
public Response<Boolean> update(@Parameter(description = "需要删除的id，多个以逗号分隔") @RequestParam("id")  String ids){
    ruleTriggerServiceImpl.updateIsDeleted(ids,CommonConstant.DELETED_FLAG_DELETED);
        return Response.success(true);
        }

/**
 * 根据编号，查询RuleTriggerEntity对象详情数据
 *
 * @param id
 *            要查询的id编号
 * @return 成功返回true
 */
@GetMapping("/detail")
        @Operation(summary = "根据id查询RuleTriggerEntity对象详情", description = "传入ids,多个以逗号分隔")
public Response<RuleTriggerVO> get(@Parameter(description = "需要查询的id") @RequestParam("id")  Long id){
    RuleTriggerEntity entity= ruleTriggerServiceImpl.selectById(id);
        return Response.success(RuleTriggerWrapper.builder().entity2VO(entity));
        }

/**
 * 查询RuleTriggerEntity对象列表数据
 *
 * @param queryInfo
 *            查询参数封装
 * @return 返回数据的列表
 */
@GetMapping("/list")
        @Operation(summary ="查询RuleTriggerEntity对象列表数据", description = "")
public Response<List<RuleTriggerVO>>list(RuleTriggerQueryDTO queryInfo){
        List<RuleTriggerEntity> cdList= ruleTriggerServiceImpl.list(queryInfo);
        return Response.success(RuleTriggerWrapper.builder().listEntity2VO(cdList));
        }

/**
 * 分页查询RuleTriggerEntity对象列表数据
 *
 * @param queryInfo
 *            查询参数封装
 * @return 返回数据的列表
 */
@GetMapping("/page")
        @Operation(summary ="分页查询RuleTriggerEntity对象列表数据", description = "")
public Response<PageDTO<RuleTriggerVO>>page(RuleTriggerQueryDTO queryInfo){
        IPage<RuleTriggerEntity> page= ruleTriggerServiceImpl.page(queryInfo);
        return Response.success(RuleTriggerWrapper.builder().pageEntity2VO(page));
        }
        }