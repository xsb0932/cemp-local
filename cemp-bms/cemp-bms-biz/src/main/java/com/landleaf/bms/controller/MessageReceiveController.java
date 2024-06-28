package com.landleaf.bms.controller;

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

import com.landleaf.bms.domain.dto.MessageReceiveAddDTO;
import com.landleaf.bms.domain.dto.MessageReceiveQueryDTO;
import com.landleaf.bms.domain.entity.MessageReceiveEntity;
import com.landleaf.bms.domain.vo.MessageReceiveVO;
import com.landleaf.bms.domain.wrapper.MessageReceiveWrapper;
import com.landleaf.bms.service.MessageReceiveService;

import lombok.AllArgsConstructor;


/**
 * 消息读取信息表的控制层接口定义
 *
 * @author hebin
 * @since 2023-11-27
 */
@RestController
@AllArgsConstructor
@RequestMapping("/message-receive")
@Tag(name = "消息读取信息表的控制层接口定义", description = "消息读取信息表的控制层接口定义")
public class MessageReceiveController {
    /**
     * 消息读取信息表的相关逻辑操作句柄
     */
    private final MessageReceiveService messageReceiveServiceImpl;

    /**
     * 新增或修改消息读取信息表数据
     *
     * @param addInfo 新增或修改的对象实体封装
     * @return 成功后返回保存的实体信息
     */
    @PostMapping("/save")
    @Operation(summary = "新增", description = "传入MessageReceiveAddDTO")
    public Response<MessageReceiveAddDTO> save(@RequestBody @Valid MessageReceiveAddDTO addInfo) {
        if (null == addInfo.getId()) {
            ValidatorUtil.validate(addInfo, MessageReceiveAddDTO.AddGroup.class);
            addInfo = messageReceiveServiceImpl.save(addInfo);
        } else {
            ValidatorUtil.validate(addInfo, MessageReceiveAddDTO.AddGroup.class);
            messageReceiveServiceImpl.update(addInfo);
        }
        return Response.success(addInfo);
    }

    /**
     * 根据编号，删除消息读取信息表数据（逻辑删除）
     *
     * @param ids 要删除的ids的编号
     * @return 成功返回true
     */
    @PostMapping("/remove")
    @Operation(summary = "根据编号，删除消息读取信息表信息", description = "传入ids,多个以逗号分隔")
    public Response<Boolean> update(@Parameter(description = "需要删除的id，多个以逗号分隔") @RequestParam("id") String ids) {
        messageReceiveServiceImpl.updateIsDeleted(ids, CommonConstant.DELETED_FLAG_DELETED);
        return Response.success(true);
    }

    /**
     * 根据编号，查询消息读取信息表详情数据
     *
     * @param id 要查询的id编号
     * @return 成功返回true
     */
    @GetMapping("/detail")
    @Operation(summary = "根据id查询消息读取信息表详情", description = "传入ids,多个以逗号分隔")
    public Response<MessageReceiveVO> get(@Parameter(description = "需要查询的id") @RequestParam("id") Long id) {
        MessageReceiveEntity entity = messageReceiveServiceImpl.selectById(id);
        return Response.success(MessageReceiveWrapper.builder().entity2VO(entity));
    }

    /**
     * 查询消息读取信息表列表数据
     *
     * @param queryInfo 查询参数封装
     * @return 返回数据的列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询消息读取信息表列表数据", description = "")
    public Response<List<MessageReceiveVO>> list(MessageReceiveQueryDTO queryInfo) {
        List<MessageReceiveEntity> cdList = messageReceiveServiceImpl.list(queryInfo);
        return Response.success(MessageReceiveWrapper.builder().listEntity2VO(cdList));
    }

    /**
     * 分页查询消息读取信息表列表数据
     *
     * @param queryInfo 查询参数封装
     * @return 返回数据的列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询消息读取信息表列表数据", description = "")
    public Response<PageDTO<MessageReceiveVO>> page(MessageReceiveQueryDTO queryInfo) {
        IPage<MessageReceiveEntity> page = messageReceiveServiceImpl.page(queryInfo);
        return Response.success(MessageReceiveWrapper.builder().pageEntity2VO(page));
    }
}