package com.landleaf.monitor.controller;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.constance.CommonConstant;
import com.landleaf.monitor.domain.dto.DeviceMonitorAddDTO;
import com.landleaf.monitor.domain.dto.DeviceMonitorCurrentDTO;
import com.landleaf.monitor.domain.dto.DeviceMonitorQueryDTO;
import com.landleaf.monitor.domain.entity.DeviceMonitorEntity;
import com.landleaf.monitor.domain.request.TableLabelShowRequest;
import com.landleaf.monitor.domain.request.TableLabelSortRequest;
import com.landleaf.monitor.domain.request.TableLabelWidthRequest;
import com.landleaf.monitor.domain.response.NodeProjectDeviceTreeResponse;
import com.landleaf.monitor.domain.response.NodeSpaceDeviceTreeResponse;
import com.landleaf.monitor.domain.vo.DeviceMonitorTableLabelVo;
import com.landleaf.monitor.domain.vo.DeviceMonitorVO;
import com.landleaf.monitor.domain.vo.ProjectBizCategoryVO;
import com.landleaf.monitor.domain.wrapper.DeviceMonitorWrapper;
import com.landleaf.monitor.service.DeviceMonitorService;
import com.landleaf.web.util.ValidatorUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 设备-监测平台的控制层接口定义
 *
 * @author hebin
 * @since 2023-06-05
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/device-monitor")
@Tag(name = "设备-监测平台的控制层接口定义", description = "设备-监测平台的控制层接口定义")
public class DeviceMonitorController {
    /**
     * 设备-监测平台的相关逻辑操作句柄
     */
    private final DeviceMonitorService deviceMonitorService;

    /**
     * 新增或修改设备-监测平台数据
     *
     * @param addInfo 新增或修改的对象实体封装
     * @return 成功后返回保存的实体信息
     */
    @PostMapping("/save")
    @Operation(summary = "新增", description = "传入DeviceMonitorAddDTO")
    public Response<DeviceMonitorAddDTO> save(@RequestBody @Valid DeviceMonitorAddDTO addInfo) {
        ValidatorUtil.validate(addInfo, DeviceMonitorAddDTO.AddGroup.class);
        deviceMonitorService.update(addInfo);
        return Response.success(addInfo);
    }

    /**
     * 根据编号，删除设备-监测平台数据（逻辑删除）
     *
     * @param ids 要删除的ids的编号
     * @return 成功返回true
     */
    @PostMapping("/remove")
    @Operation(summary = "根据编号，删除设备-监测平台信息", description = "传入ids,多个以逗号分隔")
    public Response<Boolean> update(@Parameter(description = "需要删除的id，多个以逗号分隔") @RequestParam("id") String ids) {
        deviceMonitorService.updateIsDeleted(ids, CommonConstant.DELETED_FLAG_DELETED);
        return Response.success(true);
    }

    /**
     * 根据编号，查询设备-监测平台详情数据
     *
     * @param id 要查询的id编号
     * @return 成功返回true
     */
    @GetMapping("/detail")
    @Operation(summary = "根据id查询设备-监测平台详情", description = "传入ids,多个以逗号分隔")
    public Response<DeviceMonitorVO> get(@Parameter(description = "需要查询的id") @RequestParam("id") Long id) {
        DeviceMonitorEntity entity = deviceMonitorService.selectById(id);
        return Response.success(DeviceMonitorWrapper.builder().entity2VO(entity));
    }

    /**
     * 查询设备-监测平台列表数据
     *
     * @param queryInfo 查询参数封装
     * @return 返回数据的列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询设备-监测平台列表数据", description = "")
    public Response<List<DeviceMonitorVO>> list(DeviceMonitorQueryDTO queryInfo) {
        List<DeviceMonitorEntity> cdList = deviceMonitorService.list(queryInfo);
        return Response.success(DeviceMonitorWrapper.builder().listEntity2VO(cdList));
    }

    /**
     * 分页查询设备-监测平台列表数据
     *
     * @param queryInfo 查询参数封装
     * @return 返回数据的列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询设备-监测平台列表数据", description = "")
    public Response<PageDTO<DeviceMonitorVO>> page(DeviceMonitorQueryDTO queryInfo) {
        IPage<DeviceMonitorEntity> page = deviceMonitorService.page(queryInfo);
        return Response.success(DeviceMonitorWrapper.builder().pageEntity2VO(page));
    }

    /**
     * 分页查询设备-根据项目id查询设备
     *
     * @param queryInfo
     * @return
     */
    @PostMapping("/listByProject")
    @Operation(summary = "根据项目id查询设备", description = "根据项目id查询设备")
    public Response<IPage<DeviceMonitorVO>> listByProject(@RequestBody DeviceMonitorQueryDTO queryInfo) {
        IPage<DeviceMonitorVO> listByProject = deviceMonitorService.listByProject(queryInfo);
        return Response.success(listByProject);
    }

    /**
     * 更新设备
     *
     * @return
     */
    @PostMapping("/edit")
    @Operation(summary = "更新设备", description = "更新设备")
    public Response edit(@RequestBody @Valid DeviceMonitorVO editInfo) {
        deviceMonitorService.edit(editInfo);
        return Response.success();
    }

    /**
     * getCategory
     *
     * @return 品类列表
     */
    @PostMapping("/getCategory")
    @Operation(summary = "根据项目id查询所有品类", description = "根据项目id查询所有品类")
    public Response<List<ProjectBizCategoryVO>> getCategory(@RequestBody DeviceMonitorCurrentDTO qry) {
        List<ProjectBizCategoryVO> result = deviceMonitorService.getProjectBizCategory(qry);
        return Response.success(result);
    }

    /**
     * 设备列表
     *
     * @return 设备列表
     */
    @PostMapping("/current/list")
    @Operation(summary = "分页查询设备-监测平台列表数据", description = "")
    public Response<IPage<JSONObject>> currentList2(@RequestBody DeviceMonitorCurrentDTO qry, HttpServletRequest request) {
        IPage<JSONObject> page = deviceMonitorService.getcurrent(qry);
        return Response.success(page);
    }

    /**
     * 设备树结构
     *
     * @return 设备树结构
     */
    @GetMapping("/device-tree")
    @Operation(summary = "设备树结构", description = "")
    public Response<NodeProjectDeviceTreeResponse> deviceTree() {
        NodeProjectDeviceTreeResponse result = deviceMonitorService.getDeviceTree();
        return Response.success(result);
    }

    /**
     * 设备树结构-根据空间分组
     *
     * @return 设备树结构-根据空间分组
     */
    @GetMapping("/device-tree/space")
    @Operation(summary = "设备树结构-根据空间分组", description = "")
    public Response<NodeProjectDeviceTreeResponse> deviceTreeSpace() {
        NodeProjectDeviceTreeResponse result = deviceMonitorService.getDeviceTreeSpace();
        return Response.success(result);
    }

    /**
     * 设备树结构-根据空间分组
     *
     * @return 设备树结构-根据空间分组
     */
    @GetMapping("/device-tree/v1/space")
    @Operation(summary = "设备树结构-根据空间分组", description = "")
    public Response<NodeProjectDeviceTreeResponse> deviceTreeSpaceV1(@RequestParam(value = "bizProjId", required = false) String bizProjId) {
        NodeProjectDeviceTreeResponse result = deviceMonitorService.getDeviceTreeV1Space(bizProjId);
        return Response.success(result);
    }

    @GetMapping("/node-space-device-tree")
    @Operation(description = "设备报表-设备树")
    public Response<NodeSpaceDeviceTreeResponse> nodeSpaceDeviceTree(@RequestParam("bizCategoryId") String bizCategoryId) {
        NodeSpaceDeviceTreeResponse result = deviceMonitorService.nodeSpaceDeviceTree(bizCategoryId);
        return Response.success(result);
    }

    /**
     * 设备树结构-根据产品分组
     *
     * @return 设备树结构-根据产品分组
     */
    @GetMapping("/device-tree/product")
    @Operation(summary = "设备树结构-根据产品分组", description = "")
    public Response<NodeProjectDeviceTreeResponse> deviceTreeProduct() {
        NodeProjectDeviceTreeResponse result = deviceMonitorService.getDeviceTreeProduct();
        return Response.success(result);
    }

    /**
     * 设备树结构-根据产品分组
     *
     * @return 设备树结构-根据产品分组
     */
    @GetMapping("/device-tree/v1/product")
    @Operation(summary = "设备树结构-根据产品分组", description = "")
    public Response<NodeProjectDeviceTreeResponse> deviceTreeProductV1(@RequestParam(value = "bizProjId", required = false) String bizProjId) {
        NodeProjectDeviceTreeResponse result = deviceMonitorService.getDeviceTreeV1Product(bizProjId);
        return Response.success(result);
    }

    /**
     * 查询品类的动态表头
     *
     * @param categoryBizId 品类业务ID
     * @return 结果
     */
    @Parameter(name = "categoryBizId", required = true, description = "品类业务ID")
    @Operation(summary = "查询品类的动态表头")
    @GetMapping("/table/labels")
    public Response<List<DeviceMonitorTableLabelVo>> searchDeviceTableLabels(@RequestParam(value = "categoryBizId") String categoryBizId) {
        return Response.success(deviceMonitorService.searchTableLabelList(categoryBizId));
    }

    /**
     * 变更表头列是否显示
     *
     * @param request 请求
     */
    @Operation(summary = "变更表头列是否显示")
    @PutMapping("/table/label/show")
    public Response<Void> tableLabelShow(@Validated @RequestBody TableLabelShowRequest request) {
        deviceMonitorService.tableLabelShow(request);
        return Response.success();
    }

    /**
     * 变更表头列顺序
     *
     * @param request 请求
     */
    @Operation(summary = "变更表头列顺序")
    @PutMapping("/table/label/sort")
    public Response<Void> tableLabelSort(@Validated @RequestBody TableLabelSortRequest request) {
        deviceMonitorService.tableLabelSort(request);
        return Response.success();
    }

    @Operation(summary = "变更表头列宽度")
    @PutMapping("/table/label/width")
    public Response<Void> tableLabelWidth(@Validated @RequestBody TableLabelWidthRequest request) {
        deviceMonitorService.tableLabelWidth(request);
        return Response.success();
    }

}
