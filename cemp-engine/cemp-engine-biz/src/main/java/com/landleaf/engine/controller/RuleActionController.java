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

import com.landleaf.engine.domain.dto.RuleActionAddDTO;
import com.landleaf.engine.domain.dto.RuleActionQueryDTO;
import com.landleaf.engine.domain.entity.RuleActionEntity;
import com.landleaf.engine.domain.vo.RuleActionVO;
import com.landleaf.engine.domain.wrapper.RuleActionWrapper;
import com.landleaf.engine.service.RuleActionService;

import lombok.AllArgsConstructor;


/**
 * RuleActionEntity对象的控制层接口定义
 *
 * @author hebin
 * @since 2024-04-23
 */
@RestController
@AllArgsConstructor
@RequestMapping("/rule-action")
@Tag(name = "RuleActionEntity对象的控制层接口定义", description = "RuleActionEntity对象的控制层接口定义")
public class RuleActionController {
    /**
     * RuleActionEntity对象的相关逻辑操作句柄
     */
    private final RuleActionService ruleActionServiceImpl;

    /**
     * 新增或修改RuleActionEntity对象数据
     *
     * @param addInfo 新增或修改的对象实体封装
     * @return 成功后返回保存的实体信息
     */
    @PostMapping("/save")
    @Operation(summary = "新增", description = "传入RuleActionAddDTO")
    public Response<RuleActionAddDTO> save(@RequestBody @Valid RuleActionAddDTO addInfo) {
        if (null == addInfo.getId()) {
            ValidatorUtil.validate(addInfo, RuleActionAddDTO.AddGroup.class);
            addInfo = ruleActionServiceImpl.save(addInfo);
        } else {
            ValidatorUtil.validate(addInfo, RuleActionAddDTO.AddGroup.class);
            ruleActionServiceImpl.update(addInfo);
        }
        return Response.success(addInfo);
    }

    /**
     * 根据编号，删除RuleActionEntity对象数据（逻辑删除）
     *
     * @param ids 要删除的ids的编号
     * @return 成功返回true
     */
    @PostMapping("/remove")
    @Operation(summary = "根据编号，删除RuleActionEntity对象信息", description = "传入ids,多个以逗号分隔")
    public Response<Boolean> update(@Parameter(description = "需要删除的id，多个以逗号分隔") @RequestParam("id") String ids) {
        ruleActionServiceImpl.updateIsDeleted(ids, CommonConstant.DELETED_FLAG_DELETED);
        return Response.success(true);
    }

    /**
     * 根据编号，查询RuleActionEntity对象详情数据
     *
     * @param id 要查询的id编号
     * @return 成功返回true
     */
    @GetMapping("/detail")
    @Operation(summary = "根据id查询RuleActionEntity对象详情", description = "传入ids,多个以逗号分隔")
    public Response<RuleActionVO> get(@Parameter(description = "需要查询的id") @RequestParam("id") Long id) {
        RuleActionEntity entity = ruleActionServiceImpl.selectById(id);
        return Response.success(RuleActionWrapper.builder().entity2VO(entity));
    }

    /**
     * 查询RuleActionEntity对象列表数据
     *
     * @param queryInfo 查询参数封装
     * @return 返回数据的列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询RuleActionEntity对象列表数据", description = "")
    public Response<List<RuleActionVO>> list(RuleActionQueryDTO queryInfo) {
        List<RuleActionEntity> cdList = ruleActionServiceImpl.list(queryInfo);
        return Response.success(RuleActionWrapper.builder().listEntity2VO(cdList));
    }

    /**
     * 分页查询RuleActionEntity对象列表数据
     *
     * @param queryInfo 查询参数封装
     * @return 返回数据的列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询RuleActionEntity对象列表数据", description = "")
    public Response<PageDTO<RuleActionVO>> page(RuleActionQueryDTO queryInfo) {
        IPage<RuleActionEntity> page = ruleActionServiceImpl.page(queryInfo);
        return Response.success(RuleActionWrapper.builder().pageEntity2VO(page));
    }
}