package com.landleaf.bms.controller;

import com.landleaf.bms.domain.request.ProjectSpaceRequest;
import com.landleaf.bms.domain.response.ProjectSpaceTreeResponse;
import com.landleaf.bms.service.ProjectSpaceService;
import com.landleaf.comm.base.pojo.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 空间管理接口
 *
 * @author yue lin
 * @since 2023/7/12 14:33
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/project/space")
@Tag(name = "空间管理接口")
public class ProjectSpaceController {

    private final ProjectSpaceService projectSpaceService;

    /**
     * 查询项目下的区域树状结构数据
     *
     * @param projectId 项目ID
     * @return 结果集
     */
    @Operation(summary = "查询项目下的区域树状结构数据")
    @GetMapping("/spaces/{projectId}")
    public Response<List<ProjectSpaceTreeResponse>> searchSpaces(@PathVariable Long projectId) {
        return Response.success(projectSpaceService.searchSpaces(projectId));
    }

    /**
     * 创建区域
     *
     * @param request 参数
     * @return 结果集
     */
    @Operation(summary = "创建区域")
    @PostMapping
    public Response<Long> createSpace(@RequestBody @Validated ProjectSpaceRequest.Create request) {
        return Response.success(projectSpaceService.createSpace(request));
    }

    /**
     * 更新区域
     *
     * @param request 参数
     * @return 结果集
     */
    @Operation(summary = "更新区域")
    @PutMapping
    public Response<Long> updateSpace(@RequestBody @Validated ProjectSpaceRequest.Update request) {
        return Response.success(projectSpaceService.updateSpace(request));
    }

    /**
     * 删除区域
     *
     * @param spaceId 区域ID
     * @return 结果集
     */
    @Operation(summary = "删除区域")
    @DeleteMapping("/{spaceId}")
    public Response<Void> deleteSpace(@PathVariable Long spaceId) {
        projectSpaceService.deleteSpace(spaceId);
        return Response.success();
    }

    /**
     * 验证区域名称是否可用（true可用false不可）
     *
     * @param spaceId 区域Id
     * @param projectId 区域ID
     * @param spaceName 名称
     * @return 结果集
     */
    @Operation(summary = "验证区域名称是否可用（true可用false不可）")
    @GetMapping("/check-name")
    public Response<Boolean> checkSpaceName(@RequestParam(required = false) Long spaceId,
                                            @RequestParam Long projectId,
                                            @RequestParam String spaceName) {
        return Response.success(projectSpaceService.checkSpaceName(spaceId, projectId, spaceName));
    }

}
