package com.landleaf.comm.tenant;

import java.util.function.Supplier;

/**
 * 多租户上下文
 *
 * @author yue lin
 * @since 2023/5/30 17:14
 */
public class TenantContext {

    /**
     * 多租户id
     */
    private static final ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();

    /**
     * 是否全局忽略
     */
    private static final ThreadLocal<Boolean> IGNORE = new ThreadLocal<>();


    /**
     * 获取当前上下文多租户ID
     *
     * @return 多租户ID
     */
    public static Long getTenantId() {
        return TENANT_ID.get();
    }

    /**
     * 设置多租户ID到当前上下文中
     *
     * @param tenantId 多租户ID
     */
    public static void setTenantId(Long tenantId) {
        TENANT_ID.set(tenantId);
    }

    /**
     * 当前是否忽略多租户
     *
     * @return 是否忽略多租户
     */
    public static boolean isIgnore() {
        return Boolean.TRUE.equals(IGNORE.get());
    }

    /**
     * 设置是否忽略多租户
     *
     * @param ignore 忽略状态
     */
    public static void setIgnore(boolean ignore) {
        IGNORE.set(ignore);
    }

    /**
     * 清除当前上下文中的多租户相关信息
     */
    public static void release() {
        TENANT_ID.remove();
        IGNORE.remove();
    }

    /**
     * 单独为函数内业务执行忽略/不忽略的操作，之后恢复到之前的状态
     *
     * @param isIgnore 是否忽略
     * @param supplier 业务函数
     * @return 结果
     */
    public static <T> T operate(boolean isIgnore, Supplier<T> supplier) {
        Boolean status = IGNORE.get();
        try {
            IGNORE.set(isIgnore);
            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IGNORE.set(status);
        }
    }

    /**
     * 单独为函数内业务执行忽略/不忽略的操作，之后恢复到之前的状态
     *
     * @param isIgnore 是否忽略
     * @param runnable 业务函数
     */
    public static void operate(boolean isIgnore, Runnable runnable) {
        Boolean status = IGNORE.get();
        try {
            IGNORE.set(isIgnore);
            runnable.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IGNORE.set(status);
        }
    }

}
