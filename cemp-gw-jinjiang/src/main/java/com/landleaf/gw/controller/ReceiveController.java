package com.landleaf.gw.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import com.landleaf.gw.conf.JjConstance;
import com.landleaf.gw.domain.GaugesCallback;
import com.landleaf.gw.domain.GaugesCallbackResult;
import com.landleaf.gw.service.DeviceRelationService;
import com.landleaf.gw.util.JjDateUtil;
import com.landleaf.influx.core.InfluxdbTemplate;
import com.landleaf.influx.util.MeasurementFindUtil;
import com.landleaf.redis.RedisUtils;
import com.landleaf.redis.constance.KeyConstance;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.Point;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用于接收其他平台设备状态变更的通知
 */
@RestController
@Tag(name = "用于接收其他平台设备状态变更的通知的入口", description = "用于接收其他平台设备状态变更的通知")
@Slf4j
public class ReceiveController {

    private String GAUGES_SUCCESS_CODE = "0";

    @Resource
    private DeviceRelationService deviceRelationServiceImpl;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private InfluxdbTemplate influxdbTemplate;

    /**
     * 表计状态回调
     *
     * @param gaugesStatus
     * @return
     */
    @PostMapping("/gauges-status/callback")
    public String receive(GaugesCallback gaugesStatus) {
        log.info("Receive gagues status from other platform, info is : {}", JSONUtil.toJsonStr(gaugesStatus));
        // 获取deviceId

        String outerDeviceId = gaugesStatus.getDevName();

        String bizDeviceId = deviceRelationServiceImpl.getBizDeviceIdBySupplierAndOuterId(JjConstance.GAUGES_SUPPLIER_ID, outerDeviceId);
        String bizProdId = deviceRelationServiceImpl.getBizProdIdByBizDeviceId(bizDeviceId);

        Map<String, String> tags = Maps.newHashMap();
        tags.put("biz_device_id", bizDeviceId);
        tags.put("biz_tenant_id", JjConstance.BIZ_TENANT_ID);
        tags.put("biz_project_id", JjConstance.BIZ_PROJECT_ID);
        tags.put("biz_node_id", JjConstance.BIZ_NODE_ID);

        Map<String, Object> valMap = Maps.newHashMap();

        GaugesCallbackResult result = JSONUtil.toBean(gaugesStatus.getResult(), GaugesCallbackResult.class);
        if (!GAUGES_SUCCESS_CODE.equals(result.getOutputState())) {
            // 采集失败，记录下
            log.error("表计采集失败，内容为: {}", JSONUtil.toJsonStr(gaugesStatus));

            valMap.put("CST", JjConstance.CST_OFFLINE);
            // 记录下线
        } else {
            BigDecimal currentVal = BigDecimal.ZERO;
            // 表计全是单值，此处直接取值
            if (gaugesStatus.getIsPointerMeter().equalsIgnoreCase("true")) {
                // 指针型，直接获取值
                currentVal = result.getOutputReadingValue();
            } else {
                // 单区域则相加
                if (gaugesStatus.getIsMultiRegionMeter().equalsIgnoreCase("true")) {
                    if (!StringUtils.hasText(result.getOutputStrInt())) {
                        currentVal = BigDecimal.ZERO;
                        String[] values = result.getOutputStrInt().split(StrUtil.COMMA);
                        if (StringUtils.hasText(values[0])) {
                            currentVal = new BigDecimal(values[0]);
                        }
                    }
                } else {
                    String integerPart = result.getOutputStrInt();
                    String fractionalPart = result.getOutputStrDec();
                    if (!StringUtils.hasText(integerPart)) {
                        integerPart = "0";
                    } else if (integerPart.equals(StrUtil.C_DOT)) {
                        integerPart = "0";
                    } else if (integerPart.endsWith(String.valueOf(StrUtil.C_DOT))) {
                        integerPart = integerPart.substring(0, integerPart.length() - 1);
                    }
                    integerPart += StrUtil.C_DOT;
                    if (!StringUtils.hasText(fractionalPart)) {
                        fractionalPart = "0";
                    } else if (integerPart.equals(StrUtil.C_DOT)) {
                        fractionalPart = "0";
                    } else if (integerPart.startsWith(String.valueOf(StrUtil.C_DOT))) {
                        fractionalPart = integerPart.substring(1);
                    }
                    integerPart += fractionalPart;
                    currentVal = new BigDecimal(integerPart);
                }
            }
            log.info("current val is : {}", currentVal);

            if (bizProdId.equals("PK00000001")) {
                // 电表
                // valMap.put("Epimp", currentVal); modify:因为现场图采问题，表计最后一位小数采集不准，故直接舍弃
                valMap.put("Epimp", currentVal);
            } else if (bizProdId.equals("PK00000002")) {
                valMap.put("Gascons", currentVal);
            }
        }
        redisUtils.hmset(KeyConstance.DEVICE_CURRENT_STATUS + bizDeviceId, valMap);
        influxdbTemplate.insert(Point.measurement(MeasurementFindUtil.getDeviceStatusMeasurementByProdCode(bizProdId)).tag(tags)
                .fields(valMap).time(JjDateUtil.parseTimeStr2Long(gaugesStatus.getCreateTime()), TimeUnit.SECONDS)
                .build());
        return "SUCCESS";
    }
}
