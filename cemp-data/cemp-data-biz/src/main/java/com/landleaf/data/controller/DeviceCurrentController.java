package com.landleaf.data.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name =  "数据平台 - 获取设备实时数据")
@RestController
@RequestMapping("/data")
@Validated
@Slf4j
public class DeviceCurrentController {
}
