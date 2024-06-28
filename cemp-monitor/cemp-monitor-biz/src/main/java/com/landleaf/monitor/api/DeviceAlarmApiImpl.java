package com.landleaf.monitor.api;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.data.api.device.DeviceCurrentApi;
import com.landleaf.data.api.device.dto.DeviceAlarmDTO;
import com.landleaf.monitor.dal.mapper.CurrentAlarmMapper;
import com.landleaf.monitor.domain.dto.DevConfirmedAlarmCountDTO;
import com.landleaf.monitor.dto.DeviceAlarmSummaryResponse;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备告警
 */
@RestController
@RequiredArgsConstructor
public class DeviceAlarmApiImpl implements DeviceAlarmApi {

    @Resource
    private DeviceCurrentApi deviceCurrentApi;

    @Resource
    private CurrentAlarmMapper currentAlarmMapper;

    @Override
    public Response<List<DeviceAlarmSummaryResponse>> query(List<String> bizDeviceIds) {
        Response<List<DeviceAlarmDTO>> resp = deviceCurrentApi.getDeviceCurrentAlarm(bizDeviceIds);
        if (!resp.isSuccess()) {
            Response.error(resp.getErrorCode(), resp.getMessage());
        }
        TenantContext.setIgnore(true);
        List<DeviceAlarmSummaryResponse> result = new ArrayList<>();
        DeviceAlarmSummaryResponse temp = null;
        Map<String, DeviceAlarmSummaryResponse> map = new HashMap<>();
        for (DeviceAlarmDTO deviceAlarmDTO : resp.getResult()) {
            temp = new DeviceAlarmSummaryResponse();
            temp.setBizDeviceId(deviceAlarmDTO.getBizDeviceId());
            temp.setDevAlarmCount(deviceAlarmDTO.getDevAlarmCode().size());
            temp.setRuleAlarmCount(deviceAlarmDTO.getRuleAlarmCode().size());
            temp.setDevTotalAlarmCount(temp.getDevAlarmCount() + temp.getRuleAlarmCount());
            temp.setUnconfirmedCount(temp.getDevAlarmCount() + temp.getRuleAlarmCount());
            map.put(deviceAlarmDTO.getBizDeviceId(), temp);
            result.add(temp);
        }

        // 获取当前未确认告警数,填充
        List<DevConfirmedAlarmCountDTO> deviceConfirmedCount = currentAlarmMapper.selectConfirmedCountByCode(bizDeviceIds, null);
        for (DevConfirmedAlarmCountDTO devConfirmedAlarmCountDTO : deviceConfirmedCount) {
            // 未确认告警数=总告警-已确认
            if (map.containsKey(devConfirmedAlarmCountDTO.getBizDeviceId())) {
                map.get(devConfirmedAlarmCountDTO.getBizDeviceId()).setUnconfirmedCount(
                        map.get(devConfirmedAlarmCountDTO.getBizDeviceId()).getDevTotalAlarmCount() - devConfirmedAlarmCountDTO.getCount());
            } else {
                temp = new DeviceAlarmSummaryResponse();
                temp.setBizDeviceId(devConfirmedAlarmCountDTO.getBizDeviceId());
                temp.setDevAlarmCount(0);
                temp.setRuleAlarmCount(0);
                temp.setDevTotalAlarmCount(temp.getDevAlarmCount() + temp.getRuleAlarmCount());
                map.put(devConfirmedAlarmCountDTO.getBizDeviceId(), temp);
                result.add(temp);
                map.get(devConfirmedAlarmCountDTO.getBizDeviceId()).setUnconfirmedCount(map.get(devConfirmedAlarmCountDTO.getBizDeviceId()).getDevTotalAlarmCount());
            }
        }
        return Response.success(result);
    }

    @Override
    public Response<List<DeviceAlarmSummaryResponse>> query(List<String> bizDeviceIds, List<String> codePrefix) {
        Response<List<DeviceAlarmDTO>> resp = deviceCurrentApi.getDeviceCurrentAlarm(bizDeviceIds);
        if (!resp.isSuccess()) {
            Response.error(resp.getErrorCode(), resp.getMessage());
        }
        TenantContext.setIgnore(true);
        List<DeviceAlarmSummaryResponse> result = new ArrayList<>();
        DeviceAlarmSummaryResponse temp = null;
        Map<String, DeviceAlarmSummaryResponse> map = new HashMap<>();
        for (DeviceAlarmDTO deviceAlarmDTO : resp.getResult()) {
            temp = new DeviceAlarmSummaryResponse();
            temp.setBizDeviceId(deviceAlarmDTO.getBizDeviceId());
            temp.setDevAlarmCount((int) deviceAlarmDTO.getDevAlarmCode().stream().filter(i -> checkIsTargetAlarm(i, codePrefix)).count());
            temp.setRuleAlarmCount((int) deviceAlarmDTO.getRuleAlarmCode().stream().filter(i -> checkIsTargetAlarm(i, codePrefix)).count());
            temp.setDevTotalAlarmCount(temp.getDevAlarmCount() + temp.getRuleAlarmCount());
            temp.setUnconfirmedCount(temp.getDevAlarmCount() + temp.getRuleAlarmCount());
            map.put(deviceAlarmDTO.getBizDeviceId(), temp);
            result.add(temp);
        }

        // 获取当前未确认告警数,填充
        List<DevConfirmedAlarmCountDTO> deviceConfirmedCount = currentAlarmMapper.selectConfirmedCountByCode(bizDeviceIds, codePrefix);
        for (DevConfirmedAlarmCountDTO devConfirmedAlarmCountDTO : deviceConfirmedCount) {
            if (map.containsKey(devConfirmedAlarmCountDTO.getBizDeviceId())) {
                map.get(devConfirmedAlarmCountDTO.getBizDeviceId()).setUnconfirmedCount(map.get(devConfirmedAlarmCountDTO.getBizDeviceId()).getDevTotalAlarmCount() - devConfirmedAlarmCountDTO.getCount());
            } else {
                // 加一层保护， 防止data组件没有对应的设备信息返回，理论上，为null的时候，就是0
                temp = new DeviceAlarmSummaryResponse();
                temp.setBizDeviceId(devConfirmedAlarmCountDTO.getBizDeviceId());
                temp.setDevAlarmCount(0);
                temp.setRuleAlarmCount(0);
                temp.setDevTotalAlarmCount(temp.getDevAlarmCount() + temp.getRuleAlarmCount());
                map.put(devConfirmedAlarmCountDTO.getBizDeviceId(), temp);
                result.add(temp);
                map.get(devConfirmedAlarmCountDTO.getBizDeviceId()).setUnconfirmedCount(map.get(devConfirmedAlarmCountDTO.getBizDeviceId()).getDevTotalAlarmCount());
            }
        }
        return Response.success(result);
    }

    private boolean checkIsTargetAlarm(String code, List<String> codePrefix) {
        for (String prefix : codePrefix) {
            if (code.contains(prefix)) {
                return true;
            }
        }
        return false;
    }
}
