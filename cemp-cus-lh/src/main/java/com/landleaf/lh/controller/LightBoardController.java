package com.landleaf.lh.controller;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.lh.domain.response.LightBoardCircuitProjectResponse;
import com.landleaf.lh.domain.response.LightBoardEngineProjectResponse;
import com.landleaf.lh.domain.response.LightBoardFreshAirProjectResponse;
import com.landleaf.lh.service.LightBoardService;
import com.landleaf.redis.RedisUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/light-board")
@Tag(name = "绿慧光字牌接口", description = "绿慧光字牌接口")
public class LightBoardController {
    private final RedisUtils redisUtils;
    private final LightBoardService lightBoardService;

    @GetMapping("/engine")
    @Operation(summary = "主机", description = "主机")
    public Response<List<LightBoardEngineProjectResponse>> engine(@RequestParam("id") List<String> ids) {
//        Object obj = redisUtils.get("mock.engine");
//        if (null != obj) {
//            return Response.success(JSONUtil.toList(obj.toString(), LightBoardEngineProjectResponse.class));
//        }
        Map<String, String> projectMap = lightBoardService.checkProject(ids);
        return Response.success(lightBoardService.engine(ids, projectMap));
    }

    @GetMapping("/circuit")
    @Operation(summary = "回路", description = "回路")
    public Response<List<LightBoardCircuitProjectResponse>> circuit(@RequestParam("id") List<String> ids) {
//        Object obj = redisUtils.get("mock.circuit");
//        if (null != obj) {
//            return Response.success(JSONUtil.toList(obj.toString(), LightBoardCircuitProjectResponse.class));
//        }
        LinkedHashMap<String, String> projectMap = lightBoardService.checkProject(ids);
        return Response.success(lightBoardService.circuit(ids, projectMap));
    }

    @GetMapping("/fresh-air")
    @Operation(summary = "新风", description = "新风")
    public Response<List<LightBoardFreshAirProjectResponse>> freshAir(@RequestParam("id") List<String> ids) {
//        Object obj = redisUtils.get("mock.freshAir");
//        if (null != obj) {
//            return Response.success(JSONUtil.toList(obj.toString(), LightBoardFreshAirProjectResponse.class));
//        }
        Map<String, String> projectMap = lightBoardService.checkProject(ids);
        return Response.success(lightBoardService.freshAir(ids, projectMap));
    }
}
