package com.landleaf.influx.util;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 支持序列化的 Function
 *
 * @author eason
 */
@FunctionalInterface
public interface SFunction<T, R> extends Function<T, R>, Serializable {
}
