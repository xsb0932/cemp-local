package com.landleaf.engine.controller;

import java.util.List;

import com.landleaf.engine.domain.dto.RuleDetailAddDTO;
import com.landleaf.engine.domain.vo.RuleDetailVO;
import jakarta.validation.Valid;
import com.landleaf.web.util.ValidatorUtil;

import com.landleaf.comm.constance.CommonConstant;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import org.bouncycastle.cert.ocsp.Req;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.landleaf.comm.base.pojo.Response;

import com.landleaf.engine.domain.dto.RuleAddDTO;
import com.landleaf.engine.domain.dto.RuleQueryDTO;
import com.landleaf.engine.domain.entity.RuleEntity;
import com.landleaf.engine.domain.vo.RuleVO;
import com.landleaf.engine.domain.wrapper.RuleWrapper;
import com.landleaf.engine.service.RuleService;

import lombok.AllArgsConstructor;


/**
 * RuleEntity对象的控制层接口定义
 *
 * @author hebin
 * @since 2024-04-23
 */
@RestController
@AllArgsConstructor
@RequestMapping("/rule")
@Tag(name = "RuleEntity对象的控制层接口定义", description = "RuleEntity对象的控制层接口定义")
public class RuleController {
    /**
     * RuleEntity对象的相关逻辑操作句柄
     */
    private final RuleService ruleServiceImpl;

    /**
     * 新增或修改RuleEntity对象数据
     *
     * @param addInfo 新增或修改的对象实体封装
     * @return 成功后返回保存的实体信息
     */
    @PostMapping("/save")
    @Operation(summary = "新增", description = "传入RuleAddDTO")
    public Response<RuleAddDTO> save(@RequestBody @Valid RuleAddDTO addInfo) {
        if (null == addInfo.getId()) {
            ValidatorUtil.validate(addInfo, RuleAddDTO.AddGroup.class);
            addInfo = ruleServiceImpl.save(addInfo);
        } else {
            ValidatorUtil.validate(addInfo, RuleAddDTO.AddGroup.class);
            ruleServiceImpl.update(addInfo);
        }
        return Response.success(addInfo);
    }

    /**
     * 根据编号，删除RuleEntity对象数据（逻辑删除）
     *
     * @param id 要删除的id的编号
     * @return 成功返回true
     */
    @PostMapping("/remove")
    @Operation(summary = "根据编号，删除RuleEntity对象信息", description = "传入id")
    public Response<Boolean> update(@Parameter(description = "需要删除的id") @RequestParam("id") Long id) {
        ruleServiceImpl.updateIsDeleted(id, CommonConstant.DELETED_FLAG_DELETED);
        return Response.success(true);
    }

    /**
     * 修改启用/停用状态
     *
     * @param id 要删除的ids的编号
     * @return 成功返回true
     */
    @PostMapping("/status/change")
    @Operation(summary = "根据编号，切换RuleEntity对象可用状态", description = "传入id")
    public Response<Boolean> changeStatus(@Parameter(description = "需要修改状态的id，多个以逗号分隔") @RequestParam("id") Long id, @RequestParam("status") String status) {
        ruleServiceImpl.changeStatus(id, status);
        return Response.success(true);
    }

    /**
     * 根据编号，查询RuleEntity对象详情数据
     *
     * @param id 要查询的id编号
     * @return 成功返回true
     */
    @GetMapping("/detail")
    @Operation(summary = "根据id查询RuleEntity对象详情", description = "传入ids,多个以逗号分隔")
    public Response<RuleDetailVO> get(@Parameter(description = "需要查询的id") @RequestParam("id") Long id) {
        RuleDetailVO result = ruleServiceImpl.getDetail(id);
        return Response.success(result);
    }

    /**
     * 根据编号，查询RuleEntity对象详情数据
     *
     * @param addInfo 新增的数据封装
     * @return 成功返回true
     */
    @PostMapping("/detail/save")
    @Operation(summary = "根据id查询RuleEntity对象详情", description = "传入ids,多个以逗号分隔")
    public Response<RuleDetailVO> saveDetail(@RequestBody @Valid RuleDetailAddDTO addInfo) {
        ruleServiceImpl.saveDetail(addInfo);
        return Response.success(ruleServiceImpl.getDetail(addInfo.getId()));
    }

//    /**
//     * 查询RuleEntity对象列表数据
//     *
//     * @param queryInfo 查询参数封装
//     * @return 返回数据的列表
//     */
//    @GetMapping("/list")
//    @Operation(summary = "查询RuleEntity对象列表数据", description = "")
//    public Response<List<RuleVO>> list(RuleQueryDTO queryInfo) {
//        List<RuleEntity> cdList = ruleServiceImpl.list(queryInfo);
//        return Response.success(RuleWrapper.builder().listEntity2VO(cdList));
//    }

    /**
     * 分页查询RuleEntity对象列表数据
     *
     * @param queryInfo 查询参数封装
     * @return 返回数据的列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询RuleEntity对象列表数据", description = "")
    public Response<PageDTO<RuleVO>> page(RuleQueryDTO queryInfo) {
        PageDTO<RuleVO> page = ruleServiceImpl.page(queryInfo);
        return Response.success(page);
    }
}