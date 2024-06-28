package com.landleaf.mqtt.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lokiy
 * @date 2022/12/21
 * @description mqtt配置类
 */
@ConfigurationProperties(prefix = MqttProperties.PREFIX)
public class MqttProperties {

    public static final String PREFIX = "mqtt";

    /**
     * 是否开始使用mqtt标识
     */
    private boolean enable = false;

    /**
     * 地址（带端口）
     * 例如:tcp://xxx.xxx.xxx.xxx:1883
     */
    private String serverUrl = "tcp://127.0.0.1:1883";

    /**
     * HTTP API 地址
     * 例如:http://xxx.xxx.xxx.xxx:1883
     */
    private String httpUrl = "http://127.0.0.1:1883";

    /**
     * 用户名
     */
    private String username = "admin";

    /**
     * 密码
     */
    private String password = "public";

    /**
     * 客户端Id，同一台服务器下，不允许出现重复的客户端id
     */
    private String clientId = "default";

    /**
     * 默认连接主题
     */
    private String defaultTopic = "/default";

    /**
     * 超时时间
     */
    private int timeout = 10;

    /**
     * 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端
     * 发送个消息判断客户端是否在线，但这个方法并没有重连的机制
     */
    private int keepAlive = 60;

    /**
     * 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连
     * 接记录，这里设置为true表示每次连接到服务器都以新的身份连接
     */
    private boolean cleanSession = true;

    /**
     * 是否断线重连
     */
    private boolean reconnect = true;

    /**
     * 连接方式
     */
    private int qos = 1;

    /**
     * 发送客户端最大值
     */
    private int sendClientMaxSize = 16;


    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getDefaultTopic() {
        return defaultTopic;
    }

    public void setDefaultTopic(String defaultTopic) {
        this.defaultTopic = defaultTopic;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(int keepAlive) {
        this.keepAlive = keepAlive;
    }

    public boolean isCleanSession() {
        return cleanSession;
    }

    public void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    public boolean isReconnect() {
        return reconnect;
    }

    public void setReconnect(boolean reconnect) {
        this.reconnect = reconnect;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public int getSendClientMaxSize() {
        return sendClientMaxSize;
    }

    public void setSendClientMaxSize(int sendClientMaxSize) {
        this.sendClientMaxSize = sendClientMaxSize;
    }
}
