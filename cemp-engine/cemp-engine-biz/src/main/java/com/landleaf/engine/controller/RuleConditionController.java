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

import com.landleaf.engine.domain.dto.RuleConditionAddDTO;
import com.landleaf.engine.domain.dto.RuleConditionQueryDTO;
import com.landleaf.engine.domain.entity.RuleConditionEntity;
import com.landleaf.engine.domain.vo.RuleConditionVO;
import com.landleaf.engine.domain.wrapper.RuleConditionWrapper;
import com.landleaf.engine.service.RuleConditionService;

import lombok.AllArgsConstructor;


/**
 * RuleConditionEntity对象的控制层接口定义
 *
 * @author hebin
 * @since 2024-04-23
 */
@RestController
@AllArgsConstructor
@RequestMapping("/rule-condition")
@Tag(name = "RuleConditionEntity对象的控制层接口定义", description = "RuleConditionEntity对象的控制层接口定义")
public class RuleConditionController {
/**
 * RuleConditionEntity对象的相关逻辑操作句柄
 */
private final RuleConditionService ruleConditionServiceImpl;

/**
 * 新增或修改RuleConditionEntity对象数据
 *
 * @param addInfo
 *            新增或修改的对象实体封装
 * @return 成功后返回保存的实体信息
 */
@PostMapping("/save")
@Operation(summary = "新增", description = "传入RuleConditionAddDTO")
public Response<RuleConditionAddDTO> save(@RequestBody @Valid RuleConditionAddDTO addInfo){
        if(null==addInfo.getId()){
        ValidatorUtil.validate(addInfo, RuleConditionAddDTO.AddGroup.class);
        addInfo= ruleConditionServiceImpl.save(addInfo);
        }else{
        ValidatorUtil.validate(addInfo, RuleConditionAddDTO.AddGroup.class);
    ruleConditionServiceImpl.update(addInfo);
        }
        return Response.success(addInfo);
        }

/**
 * 根据编号，删除RuleConditionEntity对象数据（逻辑删除）
 *
 * @param ids
 *            要删除的ids的编号
 * @return 成功返回true
 */
@PostMapping("/remove")
        @Operation(summary = "根据编号，删除RuleConditionEntity对象信息", description = "传入ids,多个以逗号分隔")
public Response<Boolean> update(@Parameter(description = "需要删除的id，多个以逗号分隔") @RequestParam("id")  String ids){
    ruleConditionServiceImpl.updateIsDeleted(ids,CommonConstant.DELETED_FLAG_DELETED);
        return Response.success(true);
        }

/**
 * 根据编号，查询RuleConditionEntity对象详情数据
 *
 * @param id
 *            要查询的id编号
 * @return 成功返回true
 */
@GetMapping("/detail")
        @Operation(summary = "根据id查询RuleConditionEntity对象详情", description = "传入ids,多个以逗号分隔")
public Response<RuleConditionVO> get(@Parameter(description = "需要查询的id") @RequestParam("id")  Long id){
    RuleConditionEntity entity= ruleConditionServiceImpl.selectById(id);
        return Response.success(RuleConditionWrapper.builder().entity2VO(entity));
        }

/**
 * 查询RuleConditionEntity对象列表数据
 *
 * @param queryInfo
 *            查询参数封装
 * @return 返回数据的列表
 */
@GetMapping("/list")
        @Operation(summary ="查询RuleConditionEntity对象列表数据", description = "")
public Response<List<RuleConditionVO>>list(RuleConditionQueryDTO queryInfo){
        List<RuleConditionEntity> cdList= ruleConditionServiceImpl.list(queryInfo);
        return Response.success(RuleConditionWrapper.builder().listEntity2VO(cdList));
        }

/**
 * 分页查询RuleConditionEntity对象列表数据
 *
 * @param queryInfo
 *            查询参数封装
 * @return 返回数据的列表
 */
@GetMapping("/page")
        @Operation(summary ="分页查询RuleConditionEntity对象列表数据", description = "")
public Response<PageDTO<RuleConditionVO>>page(RuleConditionQueryDTO queryInfo){
        IPage<RuleConditionEntity> page= ruleConditionServiceImpl.page(queryInfo);
        return Response.success(RuleConditionWrapper.builder().pageEntity2VO(page));
        }
        }