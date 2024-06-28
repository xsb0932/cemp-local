package com.landleaf.comm.license;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.landleaf.comm.util.encrypt.RsaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.time.LocalDate;

@Component
@Slf4j
public class LicenseCheck {

    @Value("${license:}")
    private String licenseFile;

    @Value("${pri-key:}")
    private String priKey;

    private static LicenseInfo licenseInfo;

    @Resource
    private ConfigurableApplicationContext ctx;

    @PostConstruct
    private void init() {
        licenseInfo = new LicenseInfo();
        checkTask();
    }

    /**
     * 每天执行一次check
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkTask() {
        if (!StringUtils.hasText(licenseFile) || !StringUtils.hasText(priKey)) {
            // licenseFile文件木有，凉凉
            licenseInfo.setLegal(false);
            return;
        }
        // 解析license
        try {
            String licenseContent = RsaUtil.privateDecrypt(licenseFile, RsaUtil.string2PrivateKey(priKey));
            log.info("============================================================={}", licenseContent);
            JSONObject licenseObj = JSON.parseObject(licenseContent);
            // checkMac,存在就判断，否则就不判断
            if (StringUtils.hasText(licenseObj.getString("mac"))) {
                // 判断mac地址是否相同
                String mac = getMac(licenseObj.getString("interfaceName"));
                if (!mac.equals(licenseObj.getString("mac"))) {
                    licenseInfo.setLegal(false);
                    return;
                }
            }
            if (StringUtils.hasText(licenseObj.getString("endTime"))) {
                licenseInfo.setEndTime(licenseObj.getString("endTime"));
                LocalDate now = LocalDate.now();
                String nowStr = now.toString();
                if (nowStr.compareTo(licenseObj.getString("endTime")) < 0) {
                    // check time error.
                    licenseInfo.setLegal(false);
                    return;
                }
            }
            licenseInfo.setLegal(true);
            licenseInfo.setDeviceLimit(null == licenseObj.getInteger("deviceLimit") ? 0 : licenseObj.getInteger("deviceLimit"));
            licenseInfo.setUserLimit(null == licenseObj.getInteger("userLimit") ? 0 : licenseObj.getInteger("userLimit"));
            licenseInfo.setProjLimit(null == licenseObj.getInteger("projLimit") ? 0 : licenseObj.getInteger("projLimit"));
        } catch (Exception e) {
            log.info("error", e);
            licenseInfo.setLegal(false);
            return;
        }
    }

    private String getMac(String interfaceName) {
        try {
            NetworkInterface networkInterface = NetworkInterface.getByName(interfaceName);
            if (networkInterface != null) {
                byte[] macAddressBytes = networkInterface.getHardwareAddress();
                if (macAddressBytes != null) {
                    StringBuilder macAddressBuilder = new StringBuilder();
                    for (int i = 0; i < macAddressBytes.length; i++) {
                        macAddressBuilder.append(String.format("%02X%s", macAddressBytes[i], (i < macAddressBytes.length - 1) ? "-" : ""));
                    }
                    log.info("宿主机的MAC地址： " + macAddressBuilder.toString());
                    return macAddressBuilder.toString();
                } else {
                    log.info("无法获取宿主机的MAC地址");
                }
            } else {
                log.info("无法找到名为'" + interfaceName + "'的网络接口");
            }
        } catch (SocketException e) {
            log.error("获取mac地址失败", e);
        }
        return null;
    }

    public String getEndTime() {
        return licenseInfo.getEndTime();
    }

    public boolean isLegal() {
        return licenseInfo.isLegal();
    }

    public int getUserLimit() {
        return licenseInfo.getUserLimit();
    }

    public int getDeviceLimit() {
        return licenseInfo.getDeviceLimit();
    }

    public int getProjLimit() {
        return licenseInfo.getProjLimit();
    }
}
