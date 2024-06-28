package com.landleaf.bms.controller;

import com.landleaf.bms.service.AlarmPushConditionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 告警推送条件的控制层接口定义
 *
 * @author hebin
 * @since 2024-05-31
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/alarm-push-condition")
@Tag(name = "告警推送条件的控制层接口定义", description = "告警推送条件的控制层接口定义")
public class AlarmPushConditionController {
    private final AlarmPushConditionService alarmPushConditionService;

}