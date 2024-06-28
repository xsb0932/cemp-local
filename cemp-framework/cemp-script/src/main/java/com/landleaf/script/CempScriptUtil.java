package com.landleaf.script;

import cn.hutool.script.JavaScriptEngine;
import cn.hutool.script.ScriptUtil;
import lombok.Synchronized;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;
import org.springframework.stereotype.Component;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yang
 */
@Component
public class CempScriptUtil {
    private final JavaScriptEngine upJsEngine;
    private final JavaScriptEngine downJsEngine;

    public CempScriptUtil() {
        this.upJsEngine = JavaScriptEngine.instance();
        this.downJsEngine = JavaScriptEngine.instance();
    }

    public JavaScriptEngine getUpJsEngine() {
        return upJsEngine;
    }

    public JavaScriptEngine getDownJsEngine() {
        return downJsEngine;
    }

    public void evalUpJs(String upJs) throws ScriptException {
        upJsEngine.eval(upJs);
    }

    public void evalDownJs(String downJs) throws ScriptException {
        downJsEngine.eval(downJs);
    }

    @Synchronized
    public Object handleUp(String topic, String payload) throws ScriptException, NoSuchMethodException {
        Object o = upJsEngine.invokeFunction(ScriptConstant.UP_FUNCTION, topic, payload);
        o = dealArray(o);
        return o;
    }

    @Synchronized
    public Object handleDown(String cmd) throws ScriptException, NoSuchMethodException {
        Object o = downJsEngine.invokeFunction(ScriptConstant.DOWN_FUNCTION, cmd);
        o = dealArray(o);
        return o;
    }

    public Object simulateGatewayUpJs(String script, String topic, String payload) throws ScriptException, NoSuchMethodException {
        // 获取非单例的jsEngine
        JavaScriptEngine javaScriptEngine = ScriptUtil.getJavaScriptEngine();
        javaScriptEngine.eval(script);
        Object o = javaScriptEngine.invokeFunction(ScriptConstant.UP_FUNCTION, topic, payload);
        o = dealArray(o);
        return o;
    }

    public Object simulateGatewayDownJs(String script, String cmd) throws ScriptException, NoSuchMethodException {
        // 获取非单例的jsEngine
        JavaScriptEngine javaScriptEngine = ScriptUtil.getJavaScriptEngine();
        javaScriptEngine.eval(script);
        Object o = javaScriptEngine.invokeFunction(ScriptConstant.DOWN_FUNCTION, cmd);
        o = dealArray(o);
        return o;
    }

    private Object dealArray(Object obj) {
        if (obj instanceof ScriptObjectMirror scriptObject) {
            // 递归处理查询结果的数组
            recursionEencapsulation(scriptObject);
            Boolean isArray = isArray(scriptObject);
            if (isArray) {
                obj = parseArray(scriptObject);
            }
        }
        return obj;
    }

    /**
     * 递归封装数组
     *
     * @param scriptObject
     */
    private void recursionEencapsulation(ScriptObjectMirror scriptObject) {
        for (String key : scriptObject.keySet()) {
            Object mirror = scriptObject.get(key);
            if (mirror instanceof ScriptObjectMirror o) {
                recursionEencapsulation(o);
                Boolean isArray = isArray(o);
                if (isArray) {
                    List<Object> list = parseArray(o);
                    scriptObject.put(key, list);
                }
            }
        }
    }

    /**
     * 判断是否为数组
     *
     * @param scriptObject
     * @return
     */
    public static Boolean isArray(ScriptObjectMirror scriptObject) {
        boolean isArray = scriptObject.isArray();
        int index = 0;
        for (String key : scriptObject.keySet()) {
            if (!key.equals(index + "")) {
                isArray = false;
            }
            index++;
        }
        return isArray;
    }

    /**
     * 将数组转为ArrayList
     *
     * @param scriptObject
     * @return
     */
    public static List<Object> parseArray(ScriptObjectMirror scriptObject) {
        List<Object> arrayList = new ArrayList<>();
        for (String key : scriptObject.keySet()) {
            Object obj = scriptObject.get(key);
            arrayList.add(obj);
        }
        return arrayList;
    }

}
