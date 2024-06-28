package com.landleaf.bms.controller;

import com.landleaf.bms.domain.request.AddressQueryRequest;
import com.landleaf.bms.domain.response.AddressResponse;
import com.landleaf.bms.service.AddressService;
import com.landleaf.comm.base.pojo.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 行政区域的控制层接口定义
 *
 * @author hebin
 * @since 2023-06-25
 */
@RestController
@AllArgsConstructor
@RequestMapping("/address")
@Tag(name = "行政区域的控制层接口定义", description = "行政区域的控制层接口定义")
public class AddressController {
    /**
     * 行政区域的相关逻辑操作句柄
     */
    private final AddressService addressService;

    /**
     * 查询地址信息列表数据
     *
     * @param queryRequest 查询参数封装
     * @return 返回数据的列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询地址信息列表数据")
    public Response<List<AddressResponse>> list(AddressQueryRequest queryRequest) {
        List<AddressResponse> cdList = addressService.list(queryRequest);
        return Response.success(cdList);
    }
}