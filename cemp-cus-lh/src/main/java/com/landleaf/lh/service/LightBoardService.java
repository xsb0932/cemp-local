package com.landleaf.lh.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.landleaf.bms.api.UserProjectApi;
import com.landleaf.bms.api.dto.UserProjectDTO;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.data.api.device.DeviceCurrentApi;
import com.landleaf.data.api.device.dto.DeviceCurrentDTO;
import com.landleaf.lh.domain.response.*;
import com.landleaf.monitor.api.LhDeviceApi;
import com.landleaf.monitor.api.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LightBoardService {
    private final UserProjectApi userProjectApi;
    private final LhDeviceApi lhDeviceApi;
    private final DeviceCurrentApi deviceCurrentApi;

    /**
     * CST-通讯状态
     * plr-主机实时负荷率
     * pumpRST-主机运行状态
     * condensingInTemp-冷凝器进水温度
     * condensingOutTemp-冷凝器出水温度
     * evaporatingInTemp-蒸发器进水温度
     * evaporatingOutTemp-蒸发器出水温度
     */
    private static final List<String> engineAttrCodeList = CollUtil.newArrayList(
            "CST", "plr", "pumpRST",
            "condensingInTemp", "condensingOutTemp",
            "evaporatingInTemp", "evaporatingOutTemp");

    /**
     * valveOpenedFlag-阀门开到位
     */
    private static final List<String> valveAttrCodeList = CollUtil.newArrayList("valveOpenedFlag");

    /**
     * CST-通讯状态
     * supplyAirHumidity-送风湿度
     * supplyAirTemp-送风温度
     */
    private static final List<String> freshAirAttrCodeList = CollUtil.newArrayList("CST", "supplyAirHumidity", "supplyAirTemp");

    /**
     * CST-通讯状态
     * returnWaterPressure-回水压力
     * supplyWaterPressure-供水压力
     */
    private static final List<String> circuitNormalAttrCodeList = CollUtil.newArrayList("CST", "returnWaterPressure", "supplyWaterPressure");

    /**
     * CST-通讯状态
     * supplyWaterTemp-供水温度
     * supplyWaterPressure-供水压力
     */
    private static final List<String> circuitHotWaterAttrCodeList = CollUtil.newArrayList("CST", "supplyWaterTemp", "supplyWaterPressure");

    /**
     * CST-通讯状态
     * pumbRST-泵运行状态
     * F-频率
     */
    private static final List<String> pumpAttrCodeList = CollUtil.newArrayList("CST", "pumbRST", "F");

    /**
     * 校验用户项目权限
     *
     * @param ids 项目业务id集合
     * @return Map<bizProjectId, projectName>
     */
    public LinkedHashMap<String, String> checkProject(List<String> ids) {
        if (CollUtil.isEmpty(ids)) {
            throw new ServiceException("1103000000", "项目权限异常");
        }
        LinkedHashMap<String, String> idNameMap = userProjectApi.getUserProjectList(LoginUserUtil.getLoginUserId()).getCheckedData()
                .stream()
                .sorted(Comparator.comparing(UserProjectDTO::getId))
                .collect(Collectors.toMap(UserProjectDTO::getBizProjectId, UserProjectDTO::getName, (o1, o2) -> o1, LinkedHashMap::new));
        if (!CollUtil.containsAll(idNameMap.keySet(), ids)) {
            throw new ServiceException("1103000000", "项目权限异常");
        }
        return idNameMap;
    }

    private void setStringVal(Consumer<String> action, Object val) {
        if (null != val) {
            action.accept(new BigDecimal(val.toString()).setScale(1, RoundingMode.DOWN).toString());
        }
    }

    /**
     * 主机光字牌
     *
     * @param ids        项目业务id集合
     * @param projectMap 项目idNameMap
     * @return List<LightBoardEngineProjectResponse>
     */
    public List<LightBoardEngineProjectResponse> engine(List<String> ids, Map<String, String> projectMap) {
        List<LightBoardEngineProjectResponse> result = new ArrayList<>();
        Map<String, LightBoardEngineProjectResponse> projectResultMap = new HashMap<>();
        for (String bizProjectId : ids) {
            LightBoardEngineProjectResponse projectResult = new LightBoardEngineProjectResponse();
            projectResult.setBizProjectId(bizProjectId)
                    .setProjectName(projectMap.get(bizProjectId))
                    .setEngineList(new ArrayList<>());
            projectResultMap.put(bizProjectId, projectResult);
            result.add(projectResult);
        }
        List<LightBoardEngineDTO> engineList = lhDeviceApi.listEngine(ids).getCheckedData();
        if (engineList.isEmpty()) {
            return result;
        }

        Map<String, List<String>> deviceCurrentQueryParam = new HashMap<>();
        for (LightBoardEngineDTO engineDTO : engineList) {
            String bizProjectId = engineDTO.getBizProjectId();
            LightBoardEngineProjectResponse projectResult = projectResultMap.get(bizProjectId);
            deviceCurrentQueryParam.put(engineDTO.getBizDeviceId(), engineAttrCodeList);
            Optional.ofNullable(engineDTO.getZfBizDeviceId()).ifPresent(id -> deviceCurrentQueryParam.put(id, valveAttrCodeList));
            Optional.ofNullable(engineDTO.getLnBizDeviceId()).ifPresent(id -> deviceCurrentQueryParam.put(id, valveAttrCodeList));

            LightBoardEngineResponse engineResult = new LightBoardEngineResponse();
            projectResult.getEngineList().add(engineResult);
            BeanUtil.copyProperties(engineDTO, engineResult);
        }

        Map<String, Map<String, Object>> currentMap = deviceCurrentApi.getDeviceAttrCurrent(deviceCurrentQueryParam).getCheckedData()
                .stream()
                .collect(Collectors.toMap(DeviceCurrentDTO::getBizDeviceId, DeviceCurrentDTO::getCurrent, (o1, o2) -> o1));

        result.forEach(project -> project.getEngineList().forEach(engine -> {
            Map<String, Object> engineCurrentMap = currentMap.get(engine.getBizDeviceId());
            if (MapUtil.isNotEmpty(engineCurrentMap)) {
                Object cst = engineCurrentMap.get("CST");
                engine.setCst(null != cst ? Integer.parseInt(cst.toString()) : 0);
                setStringVal(engine::setPlr, engineCurrentMap.get("plr"));
                Object pumpRST = engineCurrentMap.get("pumpRST");
                if (null != pumpRST) {
                    engine.setPumpRST(String.valueOf(new BigDecimal(pumpRST.toString()).intValue()));
                }
                setStringVal(engine::setCondensingInTemp, engineCurrentMap.get("condensingInTemp"));
                setStringVal(engine::setCondensingOutTemp, engineCurrentMap.get("condensingOutTemp"));
                setStringVal(engine::setEvaporatingInTemp, engineCurrentMap.get("evaporatingInTemp"));
                setStringVal(engine::setEvaporatingOutTemp, engineCurrentMap.get("evaporatingOutTemp"));
            }
            Optional.ofNullable(engine.getZfBizDeviceId()).ifPresent(id -> {
                Map<String, Object> valveCurrentMap = currentMap.get(id);
                if (MapUtil.isNotEmpty(valveCurrentMap)) {
                    Object valveOpenedFlag = valveCurrentMap.get("valveOpenedFlag");
                    if (null != valveOpenedFlag) {
                        String valveOpenedFlagStr = valveOpenedFlag.toString();
                        if (StrUtil.equals("TRUE", valveOpenedFlagStr)) {
                            engine.setEvaporatingValveOpenedFlag(String.valueOf(1));
                        } else if (StrUtil.equals("FALSE", valveOpenedFlagStr)) {
                            engine.setEvaporatingValveOpenedFlag(String.valueOf(0));
                        } else if (NumberUtil.isLong(valveOpenedFlagStr) || NumberUtil.isDouble(valveOpenedFlagStr)) {
                            engine.setEvaporatingValveOpenedFlag(String.valueOf(new BigDecimal(valveOpenedFlag.toString()).intValue()));
                        }
                    }
                }
            });
            Optional.ofNullable(engine.getLnBizDeviceId()).ifPresent(id -> {
                Map<String, Object> valveCurrentMap = currentMap.get(id);
                if (MapUtil.isNotEmpty(valveCurrentMap)) {
                    Object valveOpenedFlag = valveCurrentMap.get("valveOpenedFlag");
                    if (null != valveOpenedFlag) {
                        String valveOpenedFlagStr = valveOpenedFlag.toString();
                        if (StrUtil.equals("TRUE", valveOpenedFlagStr)) {
                            engine.setCondensingValveOpenedFlag(String.valueOf(1));
                        } else if (StrUtil.equals("FALSE", valveOpenedFlagStr)) {
                            engine.setCondensingValveOpenedFlag(String.valueOf(0));
                        } else if (NumberUtil.isLong(valveOpenedFlagStr) || NumberUtil.isDouble(valveOpenedFlagStr)) {
                            engine.setCondensingValveOpenedFlag(String.valueOf(new BigDecimal(valveOpenedFlag.toString()).intValue()));
                        }
                    }
                }
            });
        }));
        return result;
    }

    /**
     * 回路光字牌
     *
     * @param ids        项目业务id集合
     * @param projectMap 项目idNameMap
     * @return List<LightBoardCircuitProjectResponse>
     */
    public List<LightBoardCircuitProjectResponse> circuit(List<String> ids, LinkedHashMap<String, String> projectMap) {
        List<LightBoardCircuitProjectResponse> result = new ArrayList<>();
        Map<String, LightBoardCircuitProjectResponse> projectResultMap = new HashMap<>();
        for (String bizProjectId : ids) {
            LightBoardCircuitProjectResponse projectResult = new LightBoardCircuitProjectResponse()
                    .setBizProjectId(bizProjectId)
                    .setProjectName(projectMap.get(bizProjectId));
            result.add(projectResult);
            projectResultMap.put(bizProjectId, projectResult);
        }
        LightBoardCircuitDTO circuitDTO = lhDeviceApi.listCircuit(ids).getCheckedData();
        if (null == circuitDTO) {
            return result;
        }
        Map<String, List<String>> deviceCurrentQueryParam = new HashMap<>();
        for (Map.Entry<String, String> projectEntry : projectMap.entrySet()) {
            String bizProjectId = projectEntry.getKey();
            if (!ids.contains(bizProjectId)) {
                continue;
            }
            LightBoardCircuitProjectResponse projectResult = projectResultMap.get(bizProjectId);

            List<LightBoardCircuitNormalDTO> normalDTOList = circuitDTO.getNormalList().get(bizProjectId);
            if (CollUtil.isNotEmpty(normalDTOList)) {
                List<LightBoardCircuitNormalResponse> normalList = new ArrayList<>();
                projectResult.setNormalList(normalList);
                for (LightBoardCircuitNormalDTO normalDTO : normalDTOList) {
                    LightBoardCircuitNormalResponse normal = new LightBoardCircuitNormalResponse();
                    BeanUtil.copyProperties(normalDTO, normal);
                    normalList.add(normal);
                    deviceCurrentQueryParam.put(normalDTO.getBizDeviceId(), circuitNormalAttrCodeList);

                    List<LightBoardCircuitPumpDTO> pumpDTOList = normalDTO.getPumpList();
                    if (CollUtil.isNotEmpty(pumpDTOList)) {
                        List<LightBoardCircuitPumpResponse> pumpList = new ArrayList<>();
                        normal.setPumpList(pumpList);
                        for (LightBoardCircuitPumpDTO pumpDTO : pumpDTOList) {
                            LightBoardCircuitPumpResponse pump = new LightBoardCircuitPumpResponse();
                            BeanUtil.copyProperties(pumpDTO, pump);
                            pumpList.add(pump);
                            deviceCurrentQueryParam.put(pumpDTO.getBizDeviceId(), pumpAttrCodeList);
                        }
                    }
                }
            }

            List<LightBoardCircuitHotWaterSpecialDTO> hotWaterSpecialDTOList = circuitDTO.getHotWaterSpecialList().get(bizProjectId);
            if (CollUtil.isNotEmpty(hotWaterSpecialDTOList)) {
                List<LightBoardCircuitHotWaterSpecialResponse> hotWaterSpecialList = new ArrayList<>();
                projectResult.setHotWaterList1(hotWaterSpecialList);
                for (LightBoardCircuitHotWaterSpecialDTO specialDTO : hotWaterSpecialDTOList) {
                    LightBoardCircuitHotWaterSpecialResponse special = new LightBoardCircuitHotWaterSpecialResponse();
                    BeanUtil.copyProperties(specialDTO, special);
                    hotWaterSpecialList.add(special);

                    List<LightBoardCircuitPumpDTO> pumpDTOList = specialDTO.getPumpList();
                    if (CollUtil.isNotEmpty(pumpDTOList)) {
                        List<LightBoardCircuitPumpResponse> pumpList = new ArrayList<>();
                        special.setPumpList(pumpList);
                        for (LightBoardCircuitPumpDTO pumpDTO : pumpDTOList) {
                            LightBoardCircuitPumpResponse pump = new LightBoardCircuitPumpResponse();
                            BeanUtil.copyProperties(pumpDTO, pump);
                            pumpList.add(pump);
                            deviceCurrentQueryParam.put(pumpDTO.getBizDeviceId(), pumpAttrCodeList);
                        }
                    }
                }
            }

            List<LightBoardCircuitHotWaterDTO> hotWaterDTOList = circuitDTO.getHotWaterList().get(bizProjectId);
            if (CollUtil.isNotEmpty(hotWaterDTOList)) {
                List<LightBoardCircuitHotWaterResponse> hotWaterList = new ArrayList<>();
                projectResult.setHotWaterList2(hotWaterList);
                for (LightBoardCircuitHotWaterDTO hotWaterDTO : hotWaterDTOList) {
                    LightBoardCircuitHotWaterResponse hotWater = new LightBoardCircuitHotWaterResponse();
                    BeanUtil.copyProperties(hotWaterDTO, hotWater);
                    hotWaterList.add(hotWater);
                    deviceCurrentQueryParam.put(hotWaterDTO.getBizDeviceId(), circuitHotWaterAttrCodeList);

                    List<LightBoardCircuitPumpDTO> pumpDTOList = hotWaterDTO.getPumpList();
                    if (CollUtil.isNotEmpty(pumpDTOList)) {
                        List<LightBoardCircuitPumpResponse> pumpList = new ArrayList<>();
                        hotWater.setPumpList(pumpList);
                        for (LightBoardCircuitPumpDTO pumpDTO : pumpDTOList) {
                            LightBoardCircuitPumpResponse pump = new LightBoardCircuitPumpResponse();
                            BeanUtil.copyProperties(pumpDTO, pump);
                            pumpList.add(pump);
                            deviceCurrentQueryParam.put(pumpDTO.getBizDeviceId(), pumpAttrCodeList);
                        }
                    }
                }
            }
        }

        Map<String, Map<String, Object>> currentMap = deviceCurrentApi.getDeviceAttrCurrent(deviceCurrentQueryParam).getCheckedData()
                .stream()
                .collect(Collectors.toMap(DeviceCurrentDTO::getBizDeviceId, DeviceCurrentDTO::getCurrent, (o1, o2) -> o1));

        result.forEach(project -> {
            Optional.ofNullable(project.getNormalList())
                    .ifPresent(list -> list.forEach(circuit -> {
                        Map<String, Object> circuitCurrentMap = currentMap.get(circuit.getBizDeviceId());
                        if (MapUtil.isNotEmpty(circuitCurrentMap)) {
                            Object cst = circuitCurrentMap.get("CST");
                            circuit.setCst(null != cst ? Integer.parseInt(cst.toString()) : 0);
                            setStringVal(circuit::setReturnWaterPressure, circuitCurrentMap.get("returnWaterPressure"));
                            setStringVal(circuit::setSupplyWaterPressure, circuitCurrentMap.get("supplyWaterPressure"));
                        }
                        Optional.ofNullable(circuit.getPumpList())
                                .ifPresent(pumpList -> pumpList.forEach(p -> {
                                    Map<String, Object> pumpCurrentMap = currentMap.get(p.getBizDeviceId());
                                    if (MapUtil.isNotEmpty(pumpCurrentMap)) {
                                        Object cst = pumpCurrentMap.get("CST");
                                        p.setCst(null != cst ? Integer.parseInt(cst.toString()) : 0);
                                        Object pumbRST = pumpCurrentMap.get("pumbRST");
                                        if (null != pumbRST) {
                                            p.setPumpRST(String.valueOf(new BigDecimal(pumbRST.toString()).intValue()));
                                        }
                                        setStringVal(p::setF, pumpCurrentMap.get("F"));
                                    }
                                }));
                    }));
            Optional.ofNullable(project.getHotWaterList1())
                    .ifPresent(list -> list.forEach(circuit -> {
                        Optional.ofNullable(circuit.getPumpList())
                                .ifPresent(pumpList -> pumpList.forEach(p -> {
                                    Map<String, Object> pumpCurrentMap = currentMap.get(p.getBizDeviceId());
                                    if (MapUtil.isNotEmpty(pumpCurrentMap)) {
                                        Object cst = pumpCurrentMap.get("CST");
                                        p.setCst(null != cst ? Integer.parseInt(cst.toString()) : 0);
                                        Object pumbRST = pumpCurrentMap.get("pumbRST");
                                        if (null != pumbRST) {
                                            p.setPumpRST(String.valueOf(new BigDecimal(pumbRST.toString()).intValue()));
                                        }
                                        setStringVal(p::setF, pumpCurrentMap.get("F"));
                                    }
                                }));
                        circuit.setCst(null != circuit.getPumpList() && circuit.getPumpList().stream().anyMatch(o -> o.getCst() == 1) ? 1 : 0);
                    }));
            Optional.ofNullable(project.getHotWaterList2())
                    .ifPresent(list -> list.forEach(circuit -> {
                        Map<String, Object> circuitCurrentMap = currentMap.get(circuit.getBizDeviceId());
                        if (MapUtil.isNotEmpty(circuitCurrentMap)) {
                            Object cst = circuitCurrentMap.get("CST");
                            circuit.setCst(null != cst ? Integer.parseInt(cst.toString()) : 0);
                            setStringVal(circuit::setSupplyWaterTemp, circuitCurrentMap.get("supplyWaterTemp"));
                            setStringVal(circuit::setSupplyWaterPressure, circuitCurrentMap.get("supplyWaterPressure"));
                        }
                        Optional.ofNullable(circuit.getPumpList())
                                .ifPresent(pumpList -> pumpList.forEach(p -> {
                                    Map<String, Object> pumpCurrentMap = currentMap.get(p.getBizDeviceId());
                                    if (MapUtil.isNotEmpty(pumpCurrentMap)) {
                                        Object cst = pumpCurrentMap.get("CST");
                                        p.setCst(null != cst ? Integer.parseInt(cst.toString()) : 0);
                                        Object pumbRST = pumpCurrentMap.get("pumbRST");
                                        if (null != pumbRST) {
                                            p.setPumpRST(String.valueOf(new BigDecimal(pumbRST.toString()).intValue()));
                                        }
                                        setStringVal(p::setF, pumpCurrentMap.get("F"));
                                    }
                                }));
                    }));
        });
        return result;
    }

    /**
     * 新风光字牌
     *
     * @param ids        项目业务id集合
     * @param projectMap 项目idNameMap
     * @return List<LightBoardFreshAirProjectResponse>
     */
    public List<LightBoardFreshAirProjectResponse> freshAir(List<String> ids, Map<String, String> projectMap) {
        List<LightBoardFreshAirProjectResponse> result = new ArrayList<>();
        Map<String, LightBoardFreshAirProjectResponse> projectResultMap = new HashMap<>();
        for (String bizProjectId : ids) {
            LightBoardFreshAirProjectResponse projectResult = new LightBoardFreshAirProjectResponse();
            projectResult.setBizProjectId(bizProjectId)
                    .setProjectName(projectMap.get(bizProjectId))
                    .setFreshAirList(new ArrayList<>());
            projectResultMap.put(bizProjectId, projectResult);
            result.add(projectResult);
        }
        List<LightBoardFreshAirDTO> freshAirList = lhDeviceApi.listFreshAir(ids).getCheckedData();
        if (freshAirList.isEmpty()) {
            return result;
        }

        Map<String, List<String>> deviceCurrentQueryParam = new HashMap<>();
        for (LightBoardFreshAirDTO freshAirDTO : freshAirList) {
            String bizProjectId = freshAirDTO.getBizProjectId();
            LightBoardFreshAirProjectResponse projectResult = projectResultMap.get(bizProjectId);
            deviceCurrentQueryParam.put(freshAirDTO.getBizDeviceId(), freshAirAttrCodeList);

            LightBoardFreshAirResponse freshAirResult = new LightBoardFreshAirResponse();
            projectResult.getFreshAirList().add(freshAirResult);
            BeanUtil.copyProperties(freshAirDTO, freshAirResult);
        }

        Map<String, Map<String, Object>> currentMap = deviceCurrentApi.getDeviceAttrCurrent(deviceCurrentQueryParam).getCheckedData()
                .stream()
                .collect(Collectors.toMap(DeviceCurrentDTO::getBizDeviceId, DeviceCurrentDTO::getCurrent, (o1, o2) -> o1));

        result.forEach(project -> project.getFreshAirList().forEach(freshAir -> {
            Map<String, Object> engineCurrentMap = currentMap.get(freshAir.getBizDeviceId());
            if (MapUtil.isNotEmpty(engineCurrentMap)) {
                Object cst = engineCurrentMap.get("CST");
                freshAir.setCst(null != cst ? Integer.parseInt(cst.toString()) : 0);
                setStringVal(freshAir::setSupplyAirHumidity, engineCurrentMap.get("supplyAirHumidity"));
                setStringVal(freshAir::setSupplyAirTemp, engineCurrentMap.get("supplyAirTemp"));
            }
        }));
        return result;
    }
}
