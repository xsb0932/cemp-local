package com.landleaf.operatelog.core.context;

/**
 * 操作日志上下文
 *
 * @author yue lin
 * @since 2023/6/13 17:03
 */
public class OperateLogContext {

    private static final ThreadLocal<String> CONTENT = new ThreadLocal<>();

    /**
     * 设置上下文
     * @param content 参数
     */
    public static void setContent(String content) {
        CONTENT.set(content);
    }

    /**
     * 获取上下文
     */
    public static String getContent() {
        return CONTENT.get();
    }

    /**
     * 释放上下文
     */
    public static void release() {
        CONTENT.remove();
    }

}
