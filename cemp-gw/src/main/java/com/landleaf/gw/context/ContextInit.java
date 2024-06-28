package com.landleaf.gw.context;

import cn.hutool.core.util.StrUtil;
import com.landleaf.mqtt.configure.MqttListenerContainerConfiguration;
import com.landleaf.script.CempScriptUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.script.ScriptException;

/**
 * @author Yang
 */
@Slf4j
@Component
@AllArgsConstructor
public class ContextInit implements ApplicationRunner {
    private GwContext gwContext;
    private CempScriptUtil scriptUtil;
    private MqttListenerContainerConfiguration mqttListenerContainerConfiguration;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("初始化上下文环境");
        // read file
        gwContext.init();
        // init jsEngine
        if (StrUtil.isNotBlank(gwContext.getUpJs())) {
            try {
                log.info("eval up js");
                scriptUtil.evalUpJs(gwContext.getUpJs());
            } catch (ScriptException e) {
                log.error("eval up js failed", e);
            }
        }
        if (StrUtil.isNotBlank(gwContext.getDownJs())) {
            try {
                log.info("eval down js");
                scriptUtil.evalDownJs(gwContext.getDownJs());
            } catch (ScriptException e) {
                log.error("eval down js failed", e);
            }
        }
        // init mqtt listener
        mqttListenerContainerConfiguration.initContainer();
    }
}
