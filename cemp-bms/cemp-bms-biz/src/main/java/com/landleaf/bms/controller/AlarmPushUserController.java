package com.landleaf.bms.controller;

import com.landleaf.bms.service.AlarmPushUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * AlarmPushUserEntity对象的控制层接口定义
 *
 * @author hebin
 * @since 2024-05-31
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/alarm-push-user")
@Tag(name = "AlarmPushUserEntity对象的控制层接口定义", description = "AlarmPushUserEntity对象的控制层接口定义")
public class AlarmPushUserController {
    private final AlarmPushUserService alarmPushUserService;
    
}