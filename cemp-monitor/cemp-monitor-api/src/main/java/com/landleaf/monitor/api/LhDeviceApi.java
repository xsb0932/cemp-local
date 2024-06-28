package com.landleaf.monitor.api;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.monitor.api.dto.LightBoardCircuitDTO;
import com.landleaf.monitor.api.dto.LightBoardEngineDTO;
import com.landleaf.monitor.api.dto.LightBoardFreshAirDTO;
import com.landleaf.monitor.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 绿慧设备相关接口")
public interface LhDeviceApi {

    @PostMapping(ApiConstants.PREFIX + "/lh/light-board/list-engine")
    @Operation(summary = "根据bizDeviceIdList,获取主机列表信息")
    Response<List<LightBoardEngineDTO>> listEngine(@RequestBody List<String> bizProjectIds);

    @PostMapping(ApiConstants.PREFIX + "/lh/light-board/list-fresh-air")
    @Operation(summary = "根据bizDeviceIdList,获取新风列表信息")
    Response<List<LightBoardFreshAirDTO>> listFreshAir(@RequestBody List<String> bizProjectIds);

    @PostMapping(ApiConstants.PREFIX + "/lh/light-board/list-circuit")
    @Operation(summary = "根据bizDeviceIdList,获取回路&泵列表信息")
    Response<LightBoardCircuitDTO> listCircuit(@RequestBody List<String> bizProjectIds);
}
