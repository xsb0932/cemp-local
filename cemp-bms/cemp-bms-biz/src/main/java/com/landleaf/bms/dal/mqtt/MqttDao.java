package com.landleaf.bms.dal.mqtt;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONUtil;
import com.landleaf.comm.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.landleaf.bms.domain.enums.ErrorCodeConstants.MQTT_HTTP_FAILED;

/**
 * MqttDao
 *
 * @author 张力方
 * @since 2023/8/16
 **/
@Service
@Slf4j
public class MqttDao {
    @Value("${mqtt.httpUrl:http://127.0.0.1:1883}")
    private String httpUrl;
    @Value("${mqtt.username:admin}")
    private String username;
    @Value("${mqtt.password:public}")
    private String password;

    public MqttDao() {
    }

    public void addUser(AuthRequest request) {
        log.info("创建mqtt用户 {}", request);
        HttpResponse response = HttpUtil.createRequest(Method.POST, httpUrl + MqttApiConstants.AUTH_USERNAME_URL)
                .basicAuth(username, password)
                .body(JSONUtil.toJsonStr(request), ContentType.JSON.getValue())
                .execute();
        if (!response.isOk() || !StrUtil.equals("0", JSONUtil.parseObj(response.body()).getStr("code"))) {
            log.error("创建mqtt用户失败 {}", response.body());
            throw new ServiceException(MQTT_HTTP_FAILED);
        }
    }

    public void deleteUser(AuthRequest request) {
        log.info("删除mqtt用户 {}", request);
        HttpResponse response = HttpUtil.createRequest(Method.DELETE, httpUrl + MqttApiConstants.AUTH_USERNAME_URL + "/" + request.getUsername())
                .basicAuth(username, password)
                .execute();
        if (!response.isOk() || !StrUtil.equals("0", JSONUtil.parseObj(response.body()).getStr("code"))) {
            log.error("创建mqtt用户失败 {}", response.body());
//            throw new ServiceException(MQTT_HTTP_FAILED);
        }
    }

    public void addAclUsernameRule(List<AclUsernameRequest> requests) {
        log.info("创建mqtt用户acl {}", requests);
        HttpResponse response = HttpUtil.createRequest(Method.POST, httpUrl + MqttApiConstants.ACL_URL)
                .basicAuth(username, password)
                .body(JSONUtil.toJsonStr(requests), ContentType.JSON.getValue())
                .execute();
        if (!response.isOk() || !StrUtil.equals("0", JSONUtil.parseObj(response.body()).getStr("code"))) {
            log.error("创建mqtt用户失败 {}", response.body());
            throw new ServiceException(MQTT_HTTP_FAILED);
        }
    }

    public void deleteAclUsernameRule(List<AclUsernameRequest> requests) {
        log.info("删除mqtt用户acl {}", requests);
        for (AclUsernameRequest request : requests) {
            HttpResponse response = HttpUtil.createRequest(Method.DELETE, httpUrl + MqttApiConstants.ACL_URL + "/username/" + request.getUsername() + "/topic/" + URLEncoder.encode(request.getTopic(), StandardCharsets.UTF_8))
                    .basicAuth(username, password)
                    .execute();
            if (!response.isOk() || !StrUtil.equals("0", JSONUtil.parseObj(response.body()).getStr("code"))) {
                log.error("删除mqtt用户失败 {}", response.body());
//                throw new ServiceException(MQTT_HTTP_FAILED);
            }
        }
    }
}
