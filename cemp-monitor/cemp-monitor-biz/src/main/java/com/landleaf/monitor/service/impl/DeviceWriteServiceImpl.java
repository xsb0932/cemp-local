package com.landleaf.monitor.service.impl;

import cn.hutool.json.JSONUtil;
import com.landleaf.bms.api.UserProjectApi;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.exception.enums.GlobalErrorCodeConstants;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.kafka.conf.TopicDefineConst;
import com.landleaf.kafka.sender.KafkaSender;
import com.landleaf.monitor.domain.dto.DeviceControlAckDTO;
import com.landleaf.monitor.domain.dto.DeviceControlDTO;
import com.landleaf.monitor.domain.entity.DeviceMonitorEntity;
import com.landleaf.monitor.service.DeviceMonitorService;
import com.landleaf.monitor.service.DeviceWriteService;
import com.landleaf.redis.RedisUtils;
import com.landleaf.redis.constance.KeyConstance;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class DeviceWriteServiceImpl implements DeviceWriteService {

    @Resource
    private DeviceMonitorService deviceMonitorServiceImpl;

    @Resource
    private UserProjectApi userProjectApi;

    @Resource
    private ModeJobService modeJobService;

    @Resource
    private KafkaSender kafkaSender;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 用于存储当前发送的消息
     */
    private Map<String, String> keyMap = new ConcurrentHashMap<>();

    @Override
    public boolean writeCmd(DeviceControlDTO cmd) {
        DeviceMonitorEntity deviceInfo = deviceMonitorServiceImpl.selectByBizDeviceId(cmd.getBizDeviceId());
        if (!hasPrivilege(deviceInfo)) {
            // 无权限，返回失败
            throw new BusinessException(GlobalErrorCodeConstants.ERROR_PRIVILEGE.getCode(), GlobalErrorCodeConstants.ERROR_PRIVILEGE.getMsg());
        }
        String msgId = UUID.randomUUID().toString();
        keyMap.put(msgId, msgId);
        cmd.setMsgId(msgId);
        // 当前开机状态
        cmd.setRunningStatus(modeJobService.getRunningStatus(cmd.getBizDeviceId()));
        cmd.setTs(System.currentTimeMillis());
        log.info("========下发指令====" + JSONUtil.toJsonStr(cmd));
        // 推送kafka控制
        kafkaSender.send(TopicDefineConst.DEVICE_WRITE_TOPIC + deviceInfo.getBizProjectId(), JSONUtil.toJsonStr(cmd));

        // 等待通知
        synchronized (msgId) {
            try {
                msgId.wait(3 * 1000);
                // 超时返回失败
                boolean hasResult = redisUtils.hasKey(KeyConstance.CMD_EXEC_RESULT + msgId);
                keyMap.remove(msgId);
                if (!hasResult) {
                    log.info("操作失败。操作超时，内容为：{}", JSONUtil.toJsonStr(cmd));
                    throw new BusinessException(GlobalErrorCodeConstants.CMD_TIMEOUT.getCode(), GlobalErrorCodeConstants.CMD_TIMEOUT.getMsg());
                } else {
                    // 有返回，判断
                    Object result = redisUtils.get(KeyConstance.CMD_EXEC_RESULT + msgId);
                    DeviceControlAckDTO resp = JSONUtil.toBean(String.valueOf(result), DeviceControlAckDTO.class);
                    if (resp.isSuccess()) {
                        return resp.isSuccess();
                    } else {
                        // 记录异常
                        throw new BusinessException(resp.getErrorCode(), resp.getErrorMsg());
                    }
                }
            } catch (InterruptedException e) {
                log.error("等待控制返回失败", e);
            } finally {
                keyMap.remove(msgId);
            }
        }

        return true;
    }

    @Override
    public void cmdAck(String msgId) {
        msgId = keyMap.get(msgId);
        if (null == msgId) {
            return;
        }
        synchronized (msgId) {
            msgId.notifyAll();
        }
    }

    /**
     * @return
     */
    private boolean hasPrivilege(DeviceMonitorEntity deviceInfo) {
        return true;
//        Long userId = LoginUserUtil.getLoginUserId();
//        Long tenantId = LoginUserUtil.getLoginTenantId();
//        Response<List<String>> projectIdsResult = userProjectApi.getUserProjectBizIds(userId);
//        if (!projectIdsResult.isSuccess()) {
//            List<String> projectIds = projectIdsResult.getResult();
//            return projectIds.stream().filter(i -> i.equals(deviceInfo.getBizProjectId())).findFirst().isPresent();
//        }
//        return false;
    }
}
