package com.landleaf.bms.api;

import com.landleaf.bms.api.dto.ProjectSpaceApiRequest;
import com.landleaf.bms.api.dto.ProjectSpaceTreeApiResponse;
import com.landleaf.bms.api.enums.ApiConstants;
import com.landleaf.comm.base.pojo.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Feign 服务 - 项目空间相关
 *
 * @author 张力方
 * @since 2023/7/19
 **/
@Tag(name = "Feign 服务 - 项目空间相关")
@FeignClient(name = ApiConstants.NAME)
public interface ProjectSpaceApi {

    /**
     * 查询项目下的区域树状结构数据
     *
     * @param projectId 项目ID
     * @return 结果集
     */
    @Operation(summary = "查询项目下的区域树状结构数据")
    @GetMapping(ApiConstants.PREFIX + "/spaces/{projectId}")
    Response<List<ProjectSpaceTreeApiResponse>> searchSpaces(@PathVariable("projectId") Long projectId);

    /**
     * 查询项目下的区域平铺结构数据
     *
     * @param projectId 项目ID
     * @return 结果集
     */
    @Operation(summary = "查询项目下的区域平铺结构数据")
    @GetMapping(ApiConstants.PREFIX + "/spaces/plane")
    Response<List<ProjectSpaceTreeApiResponse>> getPlaneSpaces(@RequestParam(required = false, name = "projectId") Long projectId,
                                                               @RequestParam("isroot") boolean isroot);

    /**
     * 创建区域
     *
     * @param request 参数
     * @return 结果集
     */
    @Operation(summary = "创建区域")
    @PostMapping(ApiConstants.PREFIX + "/space")
    Response<Long> createSpace(@RequestBody ProjectSpaceApiRequest.Create request);

    /**
     * 更新区域
     *
     * @param request 参数
     * @return 结果集
     */
    @Operation(summary = "更新区域")
    @PutMapping(ApiConstants.PREFIX + "/space")
    Response<Long> updateSpace(@RequestBody ProjectSpaceApiRequest.Update request);

    /**
     * 删除区域
     *
     * @param spaceId 区域ID
     * @return 结果集
     */
    @Operation(summary = "删除区域")
    @DeleteMapping(ApiConstants.PREFIX + "/space/{spaceId}")
    Response<Void> deleteSpace(@PathVariable("spaceId") Long spaceId);

    /**
     * 验证区域名称是否可用（true可用false不可）
     *
     * @param spaceId   区域Id
     * @param projectId 区域ID
     * @param spaceName 名称
     * @return 结果集
     */
    @Operation(summary = "验证区域名称是否可用（true可用false不可）")
    @GetMapping(ApiConstants.PREFIX + "/space/check-name")
    Response<Boolean> checkSpaceName(@RequestParam(required = false, name = "spaceId") Long spaceId,
                                     @RequestParam("projectId") Long projectId,
                                     @RequestParam("spaceName") String spaceName);

    /**
     * @param ids 空间 id
     * @return
     */
    @GetMapping(ApiConstants.PREFIX + "/product/getByIds")
    @Operation(summary = "根据产品业务id查询")
    Response<List<ProjectSpaceTreeApiResponse>> getByIds(@RequestParam("ids") List<String> ids);

    /**
     * 根据id查询空间
     *
     * @param id 空间 id
     * @return
     */
    @GetMapping(ApiConstants.PREFIX + "/product/getspace")
    @Operation(summary = "根据id查询空间")
    Response<ProjectSpaceTreeApiResponse> getById(@RequestParam("ids") Long id);

    @GetMapping(ApiConstants.PREFIX + "/get-space-name-by-id")
    @Operation(summary = "根据id查询空间名称")
    Response<String> getSpaceNameById(@RequestParam("id") Long id);
}
