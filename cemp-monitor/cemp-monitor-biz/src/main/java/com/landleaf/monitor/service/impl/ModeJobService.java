package com.landleaf.monitor.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.landleaf.bms.api.weather.ProjectWeatherApi;
import com.landleaf.bms.api.weather.dto.ProjectCityWeatherDTO;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.exception.enums.GlobalErrorCodeConstants;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.data.api.weather.WeatherApi;
import com.landleaf.data.api.weather.dto.WeatherHistoryDTO;
import com.landleaf.kafka.conf.TopicDefineConst;
import com.landleaf.kafka.sender.KafkaSender;
import com.landleaf.monitor.dal.mapper.DeviceModeMapper;
import com.landleaf.monitor.dal.redis.WeatherCacheRedisDAO;
import com.landleaf.monitor.domain.dto.DeviceIntelligenceControlDTO;
import com.landleaf.monitor.domain.dto.WeatherDTO;
import com.landleaf.monitor.domain.entity.DeviceModeEntity;
import com.landleaf.monitor.domain.entity.DeviceMonitorEntity;
import com.landleaf.monitor.domain.enums.ModeTypeEnum;
import com.landleaf.monitor.service.DeviceModeService;
import com.landleaf.redis.RedisUtils;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.landleaf.redis.constance.KeyConstance.DEVICE_CURRENT_STATUS;
import static com.landleaf.redis.constance.KeyConstance.DEVICE_POWER_ON_STATUS;

/**
 * @author xushibai
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ModeJobService {

    @Resource
    protected RedisUtils redisUtils;

    @Resource
    private DeviceModeService deviceModeService;

    @Resource
    private KafkaSender kafkaSender;

    @Resource
    private Executor modeSyncHandleExecutePool;

    private final String PROJECT_JJ = "PJ00000001";
    private static Map<String,String> emeterMap = new HashMap<>();

    static{
        emeterMap.put("D000000000048","D000000000043");
        emeterMap.put("D000000000049","D000000000041");
        emeterMap.put("D000000000050","D000000000042");
        emeterMap.put("D000000000051","D000000000040");
        emeterMap.put("D000000000052","D000000000039");

        emeterMap.put("D000000000053","D000000000027");
        emeterMap.put("D000000000054","D000000000027");
        emeterMap.put("D000000000055","D000000000025");
        emeterMap.put("D000000000056","D000000000025");
        emeterMap.put("D000000000057","D000000000029");
        emeterMap.put("D000000000058","D000000000029");

    }

    private String[] guestDevices = new String[]{"D000000000048","D000000000049","D000000000050","D000000000051","D000000000052"};
    private String[] lobbyDevices = new String[]{"D000000000059"};
    private String[] aisleDevices = new String[]{"D000000000053","D000000000054","D000000000055","D000000000056","D000000000057","D000000000058"};

    private List<String> getTotalDeviceByType(int type){
        if(type <3  ){
            return Arrays.asList(guestDevices);
        }else if(type < 7){
            return Arrays.asList(lobbyDevices);
        }else if(type < 9){
            return Arrays.asList(aisleDevices);
        }
        return null;
    }

    public static void main(String[] args) {
        ModeJobService service = new ModeJobService();
        service.canSync("2023-09-14 19:27:00",15,LocalDateTime.now(),"1");
    }

    boolean canSync(String lastTimeStr , int interval,LocalDateTime now ,String syncTag ){
        if(StringUtils.equals("1",syncTag)){
            return false;
        }
        boolean isExceed = false;
        LocalDateTime lastTime =  LocalDateTime.parse(lastTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Long minutes = LocalDateTimeUtil.between(lastTime,now, ChronoUnit.MINUTES);
        if(minutes >= interval)
            isExceed = true;

        return isExceed;
    }
    private void syncCmd(String bizDeviceId,int type,int runningStatus, long sleepTime){
        DeviceIntelligenceControlDTO cmd = new DeviceIntelligenceControlDTO(UUID.randomUUID().toString(),bizDeviceId,type,runningStatus,System.currentTimeMillis());
        log.info("设备:{},模式和指令类型{},第一次下发{}", bizDeviceId, type,JSONUtil.toJsonStr(cmd));
        kafkaSender.send(TopicDefineConst.JJ_DEVICE_INTELLIGENCE_WRITE_TOPIC + PROJECT_JJ , JSONUtil.toJsonStr(cmd));
        try {Thread.sleep(sleepTime);} catch (InterruptedException e) {}
        log.info("设备:{},模式和指令类型{},第二次下发", bizDeviceId, type);
        kafkaSender.send(TopicDefineConst.JJ_DEVICE_INTELLIGENCE_WRITE_TOPIC + PROJECT_JJ , JSONUtil.toJsonStr(cmd));
    }

    private void syncCmd(String bizDeviceId,int type,int runningStatus ){
        DeviceIntelligenceControlDTO cmd = new DeviceIntelligenceControlDTO(UUID.randomUUID().toString(),bizDeviceId,type,runningStatus,System.currentTimeMillis());
        log.info("设备:{},模式和指令类型{},第一次下发{}", bizDeviceId, type,JSONUtil.toJsonStr(cmd));
        kafkaSender.send(TopicDefineConst.JJ_DEVICE_INTELLIGENCE_WRITE_TOPIC + PROJECT_JJ , JSONUtil.toJsonStr(cmd));
    }

    public int getRunningStatus(String bizDeviceId){
        int runningStatus = 0;
        if("D000000000048".equals(bizDeviceId)||"D000000000049".equals(bizDeviceId)||"D000000000050".equals(bizDeviceId)||"D000000000051".equals(bizDeviceId)||"D000000000052".equals(bizDeviceId)){
            Map<Object, Object> currentStatus = redisUtils.hmget(DEVICE_CURRENT_STATUS + emeterMap.get(bizDeviceId));
            if(String.valueOf(currentStatus.get("P")).compareTo("0.02") > 0){
                runningStatus = 1;
            }
        }else if("D000000000059".equals(bizDeviceId)){
            String[] emeters = new String[]{"D000000000030","D000000000031","D000000000032","D000000000033","D000000000034","D000000000035"};
            BigDecimal sum = BigDecimal.ZERO;
            for (String emeterId : Arrays.asList(emeters)) {
                Map<Object, Object> currentStatus = redisUtils.hmget(DEVICE_CURRENT_STATUS + emeterId);
                sum = sum.add(new BigDecimal(String.valueOf(currentStatus.get("P"))));
            }
            if(NumberUtil.div(sum,6).compareTo(BigDecimal.valueOf(0.05)) > 0){
                runningStatus = 1;
            }
        }else if("D000000000053".equals(bizDeviceId)){
            Map<Object, Object> currentStatus = redisUtils.hmget(DEVICE_CURRENT_STATUS + emeterMap.get(bizDeviceId));
            if(String.valueOf(currentStatus.get("Ic")).compareTo("0.3") > 0){
                runningStatus = 1;
            }
        }else if("D000000000055".equals(bizDeviceId)||"D000000000057".equals(bizDeviceId)){
            Map<Object, Object> currentStatus = redisUtils.hmget(DEVICE_CURRENT_STATUS + emeterMap.get(bizDeviceId));
            if(String.valueOf(currentStatus.get("Ib")).compareTo("0.3") > 0){
                runningStatus = 1;
            }
        }else if("D000000000056".equals(bizDeviceId)||"D000000000058".equals(bizDeviceId)){
            Map<Object, Object> currentStatus = redisUtils.hmget(DEVICE_CURRENT_STATUS + emeterMap.get(bizDeviceId));
            BigDecimal sum = NumberUtil.add(String.valueOf(currentStatus.get("Ia")),String.valueOf(currentStatus.get("Ic")));
            if(sum.compareTo(BigDecimal.valueOf(0.6)) > 0){
                runningStatus = 1;
            }
        }else if("D000000000054".equals(bizDeviceId)){
            Map<Object, Object> currentStatus = redisUtils.hmget(DEVICE_CURRENT_STATUS + emeterMap.get(bizDeviceId));
            BigDecimal sum = NumberUtil.add(String.valueOf(currentStatus.get("Ia")),String.valueOf(currentStatus.get("Ib")));
            if(sum.compareTo(BigDecimal.valueOf(0.6)) > 0){
                runningStatus = 1;
            }
        }

        return runningStatus;
    }

    /**
     *
     * @param type
     */
    public void sync(int type) throws InterruptedException {
        TenantContext.setIgnore(true);
        LocalDateTime currentTime = LocalDateTime.now();
        // 查询该模式下所有设备
        List<String> totalDevices = getTotalDeviceByType(type);
        List<DeviceModeEntity> modes = deviceModeService.getModeByCode(ModeTypeEnum.MODE_2.getCode(),totalDevices);
        List<String> devices = modes.stream().map(DeviceModeEntity::getBizDeviceId).collect(Collectors.toList());
        long sleepTime = 3000L;
        if(type > 2)
            sleepTime = 30000L;

        if(1 == type){
            for (String bizDeviceId : devices) {
                if(isGuest(bizDeviceId)){
                    // 获取上一次开机时间
                    Map<Object, Object> openStatus = redisUtils.hmget(DEVICE_POWER_ON_STATUS + bizDeviceId);
                    String lastTime = String.valueOf(openStatus.get("time"));
                    String syncTag = String.valueOf(openStatus.get("syncTag"));
                    String powerOnTag = String.valueOf(openStatus.get("powerOnTag"));
                    // 当前开机判断
                    Map<Object, Object> currentStatus = redisUtils.hmget(DEVICE_CURRENT_STATUS + emeterMap.get(bizDeviceId));
                    if(String.valueOf(currentStatus.get("P")).compareTo("0.02") > 0){   //开机
                        if("1".equals(powerOnTag)){
                            //上一个测点是开机
                            if(canSync(lastTime,14,currentTime,syncTag)){   //判断是否已经超过15分钟 并切已经下发过指令
                                //下发指令
                                modeSyncHandleExecutePool.execute(() -> syncCmd(bizDeviceId,type,1));
                                //syncCmd(bizDeviceId,type,sleepTime);
                                //更新下发状态
                                openStatus.put("syncTag","1");
                                Map<String, Object> currentMapNew = openStatus.entrySet().stream().collect(Collectors.toMap(k -> (String)k.getKey(), v -> String.valueOf(v.getValue())));
                                redisUtils.hmset(DEVICE_POWER_ON_STATUS  + bizDeviceId,currentMapNew);
                            }
                        }else{
                            //上一个测点是关机 - 更新开机状态
                            openStatus.put("powerOnTag","1");
                            openStatus.put("syncTag","0");
                            openStatus.put("time",currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                            Map<String, Object> currentMapNew = openStatus.entrySet().stream().collect(Collectors.toMap(k -> (String)k.getKey(), v -> String.valueOf(v.getValue())));
                            redisUtils.hmset(DEVICE_POWER_ON_STATUS  + bizDeviceId,currentMapNew);
                        }
                    }else{
                        //关机 -更新关机状态
                        openStatus.put("powerOnTag","0");
                        openStatus.put("syncTag","0");
                        openStatus.put("time",currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        Map<String, Object> currentMapNew = openStatus.entrySet().stream().collect(Collectors.toMap(k -> (String)k.getKey(), v -> String.valueOf(v.getValue())));
                        redisUtils.hmset(DEVICE_POWER_ON_STATUS  + bizDeviceId,currentMapNew);
                    }
                }
            }
        }else{
            //定时任务准点正常下发
            for (String bizDeviceId : devices) {
                modeSyncHandleExecutePool.execute(() -> syncCmd(bizDeviceId,type, getRunningStatus(bizDeviceId)));
                //syncCmd(bizDeviceId,type,30000L);
            }
        }
    }

    private boolean isGuest(String bizDeviceId){
        return Arrays.asList(guestDevices).contains(bizDeviceId);
    }

}
