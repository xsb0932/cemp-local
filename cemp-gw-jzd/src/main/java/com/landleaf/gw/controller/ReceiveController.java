package com.landleaf.gw.controller;


import com.landleaf.gw.service.PinnengerRemoteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 品联平台对接
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "用于接收品联平台数据入口", description = "用于接收品联平台数据入口")
@Slf4j
public class ReceiveController {

    private final PinnengerRemoteService pinnengerRemoteService;

    /**
     *
     */
    @PostMapping("/pinnenger/login")
    public String receive() {
        return pinnengerRemoteService.login();
    }

    /**
     *
     */
    @GetMapping("/pinnenger/test")
    public String test() {
        log.info("pinnenger test print");
        return "Success";
    }

    /**
     *
     */
    @PostMapping("/pinnenger/getDevRealKpi")
    public String getDevRealKpi() {
        pinnengerRemoteService.getDevRealKpi(false);
        return "Success";
    }
}
