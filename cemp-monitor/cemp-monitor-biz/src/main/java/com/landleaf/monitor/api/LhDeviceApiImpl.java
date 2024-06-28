package com.landleaf.monitor.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.landleaf.bms.api.CategoryApi;
import com.landleaf.bms.api.ProductApi;
import com.landleaf.bms.api.dto.ProductDeviceAttrMapResponse;
import com.landleaf.bms.api.dto.ValueDescriptionResponse;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.monitor.api.dto.*;
import com.landleaf.monitor.dal.mapper.DeviceMonitorMapper;
import com.landleaf.monitor.domain.entity.DeviceMonitorEntity;
import com.landleaf.monitor.dto.DeviceAlarmSummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LhDeviceApiImpl implements LhDeviceApi {
    private final DeviceMonitorMapper deviceMonitorMapper;
    private final CategoryApi categoryApi;
    private final ProductApi productApi;
    private final DeviceAlarmApiImpl deviceAlarmApi;

    private String getUnit(List<ValueDescriptionResponse> valueDescription) {
        Optional<ValueDescriptionResponse> option = valueDescription.stream()
                .filter(item -> "UNIT".equals(item.getKey()))
                .findAny();
        return option.map(ValueDescriptionResponse::getValue).orElse(null);
    }

    @Override
    public Response<List<LightBoardEngineDTO>> listEngine(List<String> bizProjectIds) {
        TenantContext.setIgnore(true);
        List<LightBoardEngineDTO> result = new ArrayList<>();
        // 2024-04-26 根据产品建议，使用冷热源主机的品类获取项目下主机，再根据主机code和阀门code规则获取阀门
        String bizCategoryId = categoryApi.getBizCategoryId("LRYZJ").getCheckedData();
        List<DeviceMonitorEntity> engineList = deviceMonitorMapper.selectList(
                new LambdaQueryWrapper<DeviceMonitorEntity>()
                        .in(DeviceMonitorEntity::getBizProjectId, bizProjectIds)
                        .eq(DeviceMonitorEntity::getBizCategoryId, bizCategoryId)
                        .select(DeviceMonitorEntity::getBizDeviceId,
                                DeviceMonitorEntity::getName,
                                DeviceMonitorEntity::getCode,
                                DeviceMonitorEntity::getBizProjectId,
                                DeviceMonitorEntity::getBizProductId)
                        .orderByAsc(DeviceMonitorEntity::getCode)
        );
        if (engineList.isEmpty()) {
            return Response.success(result);
        }
        List<String> engineIds = new ArrayList<>();
        List<String> engineBizProductIdList = new ArrayList<>();
        List<String> valveCodeList = new ArrayList<>();
        for (DeviceMonitorEntity engine : engineList) {
            engineIds.add(engine.getBizDeviceId());
            if (!engineBizProductIdList.contains(engine.getBizProductId())) {
                engineBizProductIdList.add(engine.getBizProductId());
            }
            valveCodeList.add(engine.getCode() + "-ZF");
            valveCodeList.add(engine.getCode() + "-LN");
        }

        Map<String, DeviceMonitorEntity> valveMap = deviceMonitorMapper.selectList(
                        new LambdaQueryWrapper<DeviceMonitorEntity>()
                                .in(DeviceMonitorEntity::getCode, valveCodeList)
                                .select(DeviceMonitorEntity::getBizDeviceId,
                                        DeviceMonitorEntity::getName,
                                        DeviceMonitorEntity::getCode,
                                        DeviceMonitorEntity::getBizProductId))
                .stream()
                .collect(Collectors.toMap(DeviceMonitorEntity::getCode, o -> o, (o1, o2) -> o1));

        Map<String, List<ProductDeviceAttrMapResponse>> engineAttrMap = productApi.getProductAttrsMapByProdId(engineBizProductIdList).getCheckedData();

        Map<String, DeviceAlarmSummaryResponse> engineAlarmMap = deviceAlarmApi.query(engineIds).getCheckedData()
                .stream().collect(Collectors.toMap(DeviceAlarmSummaryResponse::getBizDeviceId, o -> o, (o1, o2) -> o1));

        for (DeviceMonitorEntity engine : engineList) {
            LightBoardEngineDTO dto = new LightBoardEngineDTO()
                    .setBizDeviceId(engine.getBizDeviceId())
                    .setDeviceName(engine.getName())
                    .setCode(engine.getCode())
                    .setBizProjectId(engine.getBizProjectId());
            result.add(dto);

            List<ProductDeviceAttrMapResponse> attrList = engineAttrMap.get(engine.getBizProductId());
            if (CollUtil.isNotEmpty(attrList)) {
                for (ProductDeviceAttrMapResponse attr : attrList) {
                    if (StrUtil.equals("evaporatingInTemp", attr.getIdentifier()) && CollUtil.isNotEmpty(attr.getValueDescription())) {
                        dto.setEvaporatingInTempUnit(getUnit(attr.getValueDescription()));
                    }
                    if (StrUtil.equals("evaporatingOutTemp", attr.getIdentifier()) && CollUtil.isNotEmpty(attr.getValueDescription())) {
                        dto.setEvaporatingOutTempUnit(getUnit(attr.getValueDescription()));
                    }
                    if (StrUtil.equals("condensingInTemp", attr.getIdentifier()) && CollUtil.isNotEmpty(attr.getValueDescription())) {
                        dto.setCondensingInTempUnit(getUnit(attr.getValueDescription()));
                    }
                    if (StrUtil.equals("condensingOutTemp", attr.getIdentifier()) && CollUtil.isNotEmpty(attr.getValueDescription())) {
                        dto.setCondensingOutTempUnit(getUnit(attr.getValueDescription()));
                    }
                }
            }

            Optional.ofNullable(valveMap.get(engine.getCode() + "-ZF"))
                    .ifPresent(o -> dto.setZfBizDeviceId(o.getBizDeviceId())
                            .setZfName(o.getName())
                            .setZfCode(o.getCode()));

            Optional.ofNullable(valveMap.get(engine.getCode() + "-LN"))
                    .ifPresent(o -> dto.setLnBizDeviceId(o.getBizDeviceId())
                            .setLnName(o.getName())
                            .setLnCode(o.getCode()));

            Optional.ofNullable(engineAlarmMap.get(engine.getBizDeviceId()))
                    .ifPresentOrElse(alarm -> {
                        dto.setHasAlarm(alarm.getDevTotalAlarmCount() > 0);
                        dto.setHasUCAlarm(alarm.getUnconfirmedCount() > 0);
                    }, () -> dto.setHasUCAlarm(Boolean.FALSE).setHasAlarm(Boolean.FALSE));
        }
        return Response.success(result);
    }

    @Override
    public Response<List<LightBoardFreshAirDTO>> listFreshAir(List<String> bizProjectIds) {
        TenantContext.setIgnore(true);
        List<LightBoardFreshAirDTO> result = new ArrayList<>();
        // 2024-04-28 询问过产品，使用组合新风机的品类获取项目下新风机
        String bizCategoryId = categoryApi.getBizCategoryId("ZHXFJ").getCheckedData();
        List<DeviceMonitorEntity> freshAirList = deviceMonitorMapper.selectList(
                new LambdaQueryWrapper<DeviceMonitorEntity>()
                        .in(DeviceMonitorEntity::getBizProjectId, bizProjectIds)
                        .eq(DeviceMonitorEntity::getBizCategoryId, bizCategoryId)
                        .select(DeviceMonitorEntity::getBizDeviceId,
                                DeviceMonitorEntity::getName,
                                DeviceMonitorEntity::getCode,
                                DeviceMonitorEntity::getBizProjectId,
                                DeviceMonitorEntity::getBizProductId)
                        .orderByAsc(DeviceMonitorEntity::getCode)
        );
        if (freshAirList.isEmpty()) {
            return Response.success(result);
        }
        List<String> freshAirIds = new ArrayList<>();
        List<String> freshAirBizProductIdList = new ArrayList<>();
        for (DeviceMonitorEntity freshAir : freshAirList) {
            freshAirIds.add(freshAir.getBizDeviceId());
            if (!freshAirBizProductIdList.contains(freshAir.getBizProductId())) {
                freshAirBizProductIdList.add(freshAir.getBizProductId());
            }
        }

        Map<String, List<ProductDeviceAttrMapResponse>> freshAirAttrMap = productApi.getProductAttrsMapByProdId(freshAirBizProductIdList).getCheckedData();

        Map<String, DeviceAlarmSummaryResponse> freshAirAlarmMap = deviceAlarmApi.query(freshAirIds, CollUtil.newArrayList("supply_temp")).getCheckedData()
                .stream().collect(Collectors.toMap(DeviceAlarmSummaryResponse::getBizDeviceId, o -> o, (o1, o2) -> o1));
        Map<String, DeviceAlarmSummaryResponse> freshAirAlarmMap2 = deviceAlarmApi.query(freshAirIds, CollUtil.newArrayList("supply_humidity")).getCheckedData()
                .stream().collect(Collectors.toMap(DeviceAlarmSummaryResponse::getBizDeviceId, o -> o, (o1, o2) -> o1));

        for (DeviceMonitorEntity freshAir : freshAirList) {
            LightBoardFreshAirDTO dto = new LightBoardFreshAirDTO()
                    .setBizDeviceId(freshAir.getBizDeviceId())
                    .setDeviceName(freshAir.getName())
                    .setCode(freshAir.getCode())
                    .setBizProjectId(freshAir.getBizProjectId());
            result.add(dto);

            List<ProductDeviceAttrMapResponse> attrList = freshAirAttrMap.get(freshAir.getBizProductId());
            if (CollUtil.isNotEmpty(attrList)) {
                for (ProductDeviceAttrMapResponse attr : attrList) {
                    if (StrUtil.equals("supplyAirHumidity", attr.getIdentifier()) && CollUtil.isNotEmpty(attr.getValueDescription())) {
                        dto.setSupplyAirHumidityUnit(getUnit(attr.getValueDescription()));
                    }
                    if (StrUtil.equals("supplyAirTemp", attr.getIdentifier()) && CollUtil.isNotEmpty(attr.getValueDescription())) {
                        dto.setSupplyAirTempUnit(getUnit(attr.getValueDescription()));
                    }
                }
            }

            Optional.ofNullable(freshAirAlarmMap.get(freshAir.getBizDeviceId()))
                    .ifPresentOrElse(alarm -> {
                        dto.setSupplyAirTempHasAlarm(alarm.getDevTotalAlarmCount() > 0);
                        dto.setSupplyAirTempHasUCAlarm(alarm.getUnconfirmedCount() > 0);
                    }, () -> dto.setSupplyAirTempHasAlarm(Boolean.FALSE).setSupplyAirTempHasUCAlarm(Boolean.FALSE));

            Optional.ofNullable(freshAirAlarmMap2.get(freshAir.getBizDeviceId()))
                    .ifPresentOrElse(alarm -> {
                        dto.setSupplyAirHumidityHasAlarm(alarm.getDevTotalAlarmCount() > 0);
                        dto.setSupplyAirHumidityHasUCAlarm(alarm.getUnconfirmedCount() > 0);
                    }, () -> dto.setSupplyAirHumidityHasAlarm(Boolean.FALSE).setSupplyAirHumidityHasUCAlarm(Boolean.FALSE));
        }
        return Response.success(result);
    }

    @Override
    public Response<LightBoardCircuitDTO> listCircuit(List<String> bizProjectIds) {
        // 2024-04-28 询问过产品，可以先通过循环泵和系统回路品类获取到所有的泵和设备，根据code进行分组，单独处理下热水一二次的泵所属的回路名称（因为没有回路）。会存在回路下没有泵的情况，这时候不处理泵数据就行。
        TenantContext.setIgnore(true);
        String circuitBizCategoryId = categoryApi.getBizCategoryId("XTHL").getCheckedData();
        String pumpBizCategoryId = categoryApi.getBizCategoryId("XHB").getCheckedData();

        List<DeviceMonitorEntity> circuitList = deviceMonitorMapper.selectList(
                new LambdaQueryWrapper<DeviceMonitorEntity>()
                        .in(DeviceMonitorEntity::getBizProjectId, bizProjectIds)
                        .eq(DeviceMonitorEntity::getBizCategoryId, circuitBizCategoryId)
                        .select(DeviceMonitorEntity::getBizDeviceId,
                                DeviceMonitorEntity::getName,
                                DeviceMonitorEntity::getCode,
                                DeviceMonitorEntity::getBizProjectId,
                                DeviceMonitorEntity::getBizProductId)
                        .orderByAsc(DeviceMonitorEntity::getCode)
        );
        List<DeviceMonitorEntity> pumpList = deviceMonitorMapper.selectList(
                new LambdaQueryWrapper<DeviceMonitorEntity>()
                        .in(DeviceMonitorEntity::getBizProjectId, bizProjectIds)
                        .eq(DeviceMonitorEntity::getBizCategoryId, pumpBizCategoryId)
                        .select(DeviceMonitorEntity::getBizDeviceId,
                                DeviceMonitorEntity::getName,
                                DeviceMonitorEntity::getCode,
                                DeviceMonitorEntity::getBizProjectId,
                                DeviceMonitorEntity::getBizProductId)
                        .orderByAsc(DeviceMonitorEntity::getCode)
        );
        if (circuitList.isEmpty() && pumpList.isEmpty()) {
            return Response.success();
        }
        LightBoardCircuitDTO result = new LightBoardCircuitDTO();
        Map<String, LinkedHashMap<String, LightBoardCircuitNormalDTO>> projectNormalMap = new HashMap<>();
        Map<String, LinkedHashMap<String, LightBoardCircuitHotWaterDTO>> projectHotWaterMap = new HashMap<>();
        Map<String, LinkedHashMap<String, LightBoardCircuitHotWaterSpecialDTO>> projectHotWaterSpecialMap = new HashMap<>();

        List<String> bizProductIds = new ArrayList<>();
        List<String> pumpIds = new ArrayList<>();
        Map<String, List<LightBoardCircuitPumpDTO>> circuitPumpMap = new HashMap<>();

        for (DeviceMonitorEntity pump : pumpList) {
            String circuitCode = StrUtil.subBefore(pump.getCode(), "-", true);
            List<LightBoardCircuitPumpDTO> circuitPumpList = circuitPumpMap.computeIfAbsent(circuitCode, k -> new ArrayList<>());
            // 特殊处理热水一/二次泵
            if (StrUtil.contains(circuitCode, "RSYC") || StrUtil.contains(circuitCode, "RSEC")) {
                String circuitNum = StrUtil.subAfter(circuitCode, "-", true);
                LinkedHashMap<String, LightBoardCircuitHotWaterSpecialDTO> hotWaterSpecialMap = projectHotWaterSpecialMap.computeIfAbsent(pump.getBizProjectId(), k -> new LinkedHashMap<>());
                if (!hotWaterSpecialMap.containsKey(circuitCode)) {
                    LightBoardCircuitHotWaterSpecialDTO hotWaterSpecialDTO = new LightBoardCircuitHotWaterSpecialDTO()
                            .setBizProjectId(pump.getBizProjectId())
                            .setBizDeviceId(circuitCode)
                            .setDeviceName((StrUtil.contains(circuitCode, "RSYC") ? "热水一次回路" : "热水二次回路") + circuitNum)
                            .setCode(circuitCode);
                    hotWaterSpecialMap.put(circuitCode, hotWaterSpecialDTO);
                    hotWaterSpecialDTO.setPumpList(circuitPumpList);
                }
            }
            LightBoardCircuitPumpDTO pumpDTO = new LightBoardCircuitPumpDTO()
                    .setBizProjectId(pump.getBizProjectId())
                    .setBizDeviceId(pump.getBizDeviceId())
                    .setDeviceName(pump.getName())
                    .setCode(pump.getCode())
                    .setBizProductId(pump.getBizProductId());
            circuitPumpList.add(pumpDTO);

            if (!bizProductIds.contains(pump.getBizProductId())) {
                bizProductIds.add(pump.getBizProductId());
            }
            pumpIds.add(pumpDTO.getBizDeviceId());
        }

        for (DeviceMonitorEntity circuit : circuitList) {
            String circuitCode = circuit.getCode();
            if (StrUtil.contains(circuitCode, "RSHS")) {
                // 热水回水回路
                LinkedHashMap<String, LightBoardCircuitHotWaterDTO> hotWaterMap = projectHotWaterMap.computeIfAbsent(circuit.getBizProjectId(), k -> new LinkedHashMap<>());
                LightBoardCircuitHotWaterDTO normalDTO = new LightBoardCircuitHotWaterDTO()
                        .setBizProjectId(circuit.getBizProjectId())
                        .setBizDeviceId(circuit.getBizDeviceId())
                        .setDeviceName(circuit.getName())
                        .setCode(circuitCode)
                        .setBizProductId(circuit.getBizProductId())
                        .setPumpList(circuitPumpMap.get(circuitCode));
                hotWaterMap.put(circuitCode, normalDTO);
            } else {
                // 普通回路
                LinkedHashMap<String, LightBoardCircuitNormalDTO> normalMap = projectNormalMap.computeIfAbsent(circuit.getBizProjectId(), k -> new LinkedHashMap<>());
                LightBoardCircuitNormalDTO normalDTO = new LightBoardCircuitNormalDTO()
                        .setBizProjectId(circuit.getBizProjectId())
                        .setBizDeviceId(circuit.getBizDeviceId())
                        .setDeviceName(circuit.getName())
                        .setCode(circuitCode)
                        .setBizProductId(circuit.getBizProductId())
                        .setPumpList(circuitPumpMap.get(circuitCode));
                normalMap.put(circuitCode, normalDTO);
            }
            if (!bizProductIds.contains(circuit.getBizProductId())) {
                bizProductIds.add(circuit.getBizProductId());
            }
        }

        Map<String, List<ProductDeviceAttrMapResponse>> productAttrMap = productApi.getProductAttrsMapByProdId(bizProductIds).getCheckedData();

        Map<String, DeviceAlarmSummaryResponse> pumpAlarmMap = deviceAlarmApi.query(pumpIds).getCheckedData()
                .stream().collect(Collectors.toMap(DeviceAlarmSummaryResponse::getBizDeviceId, o -> o, (o1, o2) -> o1));

        // 按项目组装数据
        for (String bizProjectId : bizProjectIds) {
            LinkedHashMap<String, LightBoardCircuitNormalDTO> normalMap = projectNormalMap.get(bizProjectId);
            if (MapUtil.isNotEmpty(normalMap)) {
                List<LightBoardCircuitNormalDTO> list = new ArrayList<>();
                for (Map.Entry<String, LightBoardCircuitNormalDTO> entry : normalMap.entrySet()) {
                    LightBoardCircuitNormalDTO dto = entry.getValue();
                    list.add(dto);
                    List<ProductDeviceAttrMapResponse> attrList = productAttrMap.get(dto.getBizProductId());
                    if (CollUtil.isNotEmpty(attrList)) {
                        for (ProductDeviceAttrMapResponse attr : attrList) {
                            if (StrUtil.equals("returnWaterPressure", attr.getIdentifier()) && CollUtil.isNotEmpty(attr.getValueDescription())) {
                                dto.setReturnWaterPressureUnit(getUnit(attr.getValueDescription()));
                            }
                            if (StrUtil.equals("supplyWaterPressure", attr.getIdentifier()) && CollUtil.isNotEmpty(attr.getValueDescription())) {
                                dto.setSupplyWaterPressureUnit(getUnit(attr.getValueDescription()));
                            }
                        }
                    }
                    Optional.ofNullable(dto.getPumpList()).ifPresent(o -> o.forEach(p -> {
                        List<ProductDeviceAttrMapResponse> attrList2 = productAttrMap.get(p.getBizProductId());
                        if (CollUtil.isNotEmpty(attrList2)) {
                            for (ProductDeviceAttrMapResponse attr : attrList2) {
                                if (StrUtil.equals("F", attr.getIdentifier()) && CollUtil.isNotEmpty(attr.getValueDescription())) {
                                    p.setFUnit(getUnit(attr.getValueDescription()));
                                }
                            }
                        }
                        Optional.ofNullable(pumpAlarmMap.get(p.getBizDeviceId()))
                                .ifPresentOrElse(alarm -> {
                                    p.setHasAlarm(alarm.getDevTotalAlarmCount() > 0);
                                    p.setHasUCAlarm(alarm.getUnconfirmedCount() > 0);
                                }, () -> p.setHasAlarm(Boolean.FALSE).setHasUCAlarm(Boolean.FALSE));
                    }));
                }
                result.getNormalList().put(bizProjectId, list);
            }

            LinkedHashMap<String, LightBoardCircuitHotWaterDTO> hotWaterMap = projectHotWaterMap.get(bizProjectId);
            if (MapUtil.isNotEmpty(hotWaterMap)) {
                List<LightBoardCircuitHotWaterDTO> list = new ArrayList<>();
                for (Map.Entry<String, LightBoardCircuitHotWaterDTO> entry : hotWaterMap.entrySet()) {
                    LightBoardCircuitHotWaterDTO dto = entry.getValue();
                    list.add(dto);
                    List<ProductDeviceAttrMapResponse> attrList = productAttrMap.get(dto.getBizProductId());
                    if (CollUtil.isNotEmpty(attrList)) {
                        for (ProductDeviceAttrMapResponse attr : attrList) {
                            if (StrUtil.equals("supplyWaterTemp", attr.getIdentifier()) && CollUtil.isNotEmpty(attr.getValueDescription())) {
                                dto.setSupplyWaterTempUnit(getUnit(attr.getValueDescription()));
                            }
                            if (StrUtil.equals("supplyWaterPressure", attr.getIdentifier()) && CollUtil.isNotEmpty(attr.getValueDescription())) {
                                dto.setSupplyWaterPressureUnit(getUnit(attr.getValueDescription()));
                            }
                        }
                    }
                    Optional.ofNullable(dto.getPumpList()).ifPresent(o -> o.forEach(p -> {
                        List<ProductDeviceAttrMapResponse> attrList2 = productAttrMap.get(p.getBizProductId());
                        if (CollUtil.isNotEmpty(attrList2)) {
                            for (ProductDeviceAttrMapResponse attr : attrList2) {
                                if (StrUtil.equals("F", attr.getIdentifier()) && CollUtil.isNotEmpty(attr.getValueDescription())) {
                                    p.setFUnit(getUnit(attr.getValueDescription()));
                                }
                            }
                        }
                        Optional.ofNullable(pumpAlarmMap.get(p.getBizDeviceId()))
                                .ifPresentOrElse(alarm -> {
                                    p.setHasAlarm(alarm.getDevTotalAlarmCount() > 0);
                                    p.setHasUCAlarm(alarm.getUnconfirmedCount() > 0);
                                }, () -> p.setHasAlarm(Boolean.FALSE).setHasUCAlarm(Boolean.FALSE));
                    }));
                }
                result.getHotWaterList().put(bizProjectId, list);
            }

            LinkedHashMap<String, LightBoardCircuitHotWaterSpecialDTO> hotWaterSpecialMap = projectHotWaterSpecialMap.get(bizProjectId);
            if (MapUtil.isNotEmpty(hotWaterSpecialMap)) {
                List<LightBoardCircuitHotWaterSpecialDTO> list = new ArrayList<>();
                for (Map.Entry<String, LightBoardCircuitHotWaterSpecialDTO> entry : hotWaterSpecialMap.entrySet()) {
                    LightBoardCircuitHotWaterSpecialDTO dto = entry.getValue();
                    list.add(dto);
                    Optional.ofNullable(dto.getPumpList()).ifPresent(o -> o.forEach(p -> {
                        List<ProductDeviceAttrMapResponse> attrList2 = productAttrMap.get(p.getBizProductId());
                        if (CollUtil.isNotEmpty(attrList2)) {
                            for (ProductDeviceAttrMapResponse attr : attrList2) {
                                if (StrUtil.equals("F", attr.getIdentifier()) && CollUtil.isNotEmpty(attr.getValueDescription())) {
                                    p.setFUnit(getUnit(attr.getValueDescription()));
                                }
                            }
                        }
                        Optional.ofNullable(pumpAlarmMap.get(p.getBizDeviceId()))
                                .ifPresentOrElse(alarm -> {
                                    p.setHasAlarm(alarm.getDevTotalAlarmCount() > 0);
                                    p.setHasUCAlarm(alarm.getUnconfirmedCount() > 0);
                                }, () -> p.setHasAlarm(Boolean.FALSE).setHasUCAlarm(Boolean.FALSE));
                    }));
                }
                result.getHotWaterSpecialList().put(bizProjectId, list);
            }
        }
        return Response.success(result);
    }
}
