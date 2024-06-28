package com.landleaf.bms.controller;

import java.util.List;

import jakarta.validation.Valid;
import com.landleaf.web.util.ValidatorUtil;

import com.landleaf.comm.constance.CommonConstant;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.landleaf.comm.base.pojo.Response;

import com.landleaf.bms.api.dto.MessageAddRequest;
import com.landleaf.bms.domain.dto.MessageQueryDTO;
import com.landleaf.bms.domain.vo.MessageVO;
import com.landleaf.bms.service.MessageService;

import lombok.AllArgsConstructor;


/**
 * 消息信息表的控制层接口定义
 *
 * @author hebin
 * @since 2023-11-27
 */
@RestController
@AllArgsConstructor
@RequestMapping("/message")
@Tag(name = "消息信息表的控制层接口定义", description = "消息信息表的控制层接口定义")
public class MessageController {
    /**
     * 消息信息表的相关逻辑操作句柄
     */
    private final MessageService messageServiceImpl;

    /**
     * 新增或修改消息信息表数据
     *
     * @param addInfo 新增或修改的对象实体封装
     * @return 成功后返回保存的实体信息
     */
    @PostMapping("/save")
    @Operation(summary = "新增", description = "传入MessageAddDTO")
    public Response<MessageAddRequest> save(@RequestBody @Valid MessageAddRequest addInfo) {
        if (null == addInfo.getId()) {
            ValidatorUtil.validate(addInfo, MessageAddRequest.AddGroup.class);
            addInfo = messageServiceImpl.save(addInfo);
        } else {
            ValidatorUtil.validate(addInfo, MessageAddRequest.AddGroup.class);
            messageServiceImpl.update(addInfo);
        }
        return Response.success(addInfo);
    }

    /**
     * 根据编号，删除消息信息表数据（逻辑删除）
     *
     * @param id 要删除的ids的编号
     * @return 成功返回true
     */
    @PostMapping("/remove")
    @Operation(summary = "根据编号，删除消息信息表信息", description = "传入ids,多个以逗号分隔")
    public Response<Boolean> update(@Parameter(description = "需要删除的id，多个以逗号分隔") @RequestParam("id") String id) {
        messageServiceImpl.updateIsDeleted(id, CommonConstant.DELETED_FLAG_DELETED);
        return Response.success(true);
    }

    /**
     * 根据编号，查询消息信息表详情数据
     *
     * @param id 要查询的id编号
     * @return 成功返回true
     */
    @GetMapping("/detail")
    @Operation(summary = "根据id查询消息信息表详情", description = "传入id")
    public Response<MessageVO> get(@Parameter(description = "需要查询的id") @RequestParam("id") Long id) {
        MessageVO entity = messageServiceImpl.selectDetailById(id);
        return Response.success(entity);
    }

    /**
     * 根据编号，发布对应的消息
     *
     * @param id 要查询的id编号
     * @return 成功返回true
     */
    @GetMapping("/publish")
    @Operation(summary = "据编号，发布对应的消息", description = "传入id")
    public Response<MessageVO> publish(@Parameter(description = "需要查询的id") @RequestParam("id") Long id) {
        MessageVO entity = messageServiceImpl.publishById(id);
        return Response.success(entity);
    }

    /**
     * 根据编号，读取消息信息
     *
     * @param id 要查询的id编号
     * @return 成功返回true
     */
    @GetMapping("/read")
    @Operation(summary = "根据id查询消息信息表详情", description = "传入id")
    public Response<MessageVO> read(@Parameter(description = "需要查询的id") @RequestParam("id") Long id) {
        MessageVO entity = messageServiceImpl.readById(id);
        return Response.success(entity);
    }

    /**
     * 获取未读消息数
     *
     * @return 返回数据的列表
     */
    @GetMapping("/unread/count")
    @Operation(summary = "获取未读消息数", description = "")
    public Response<Integer> getUnreadCount() {
        // 获取未读消息数
        int count = messageServiceImpl.selectUnreadCount();
        return Response.success(count);
    }

    /**
     * 读取用户未读消息前5条
     *
     * @return 返回数据的列表
     */
    @GetMapping("/unread/list")
    @Operation(summary = "查询消息信息表列表数据", description = "")
    public Response<List<MessageVO>> list() {
        List<MessageVO> cdList = messageServiceImpl.listTop5();
        return Response.success(cdList);
    }

    /**
     * 分页查询消息信息表列表数据
     *
     * @param queryInfo 查询参数封装
     * @return 返回数据的列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询消息信息表列表数据", description = "")
    public Response<PageDTO<MessageVO>> page(MessageQueryDTO queryInfo) {
        PageDTO<MessageVO> page = messageServiceImpl.page(queryInfo);
        return Response.success(page);
    }
}