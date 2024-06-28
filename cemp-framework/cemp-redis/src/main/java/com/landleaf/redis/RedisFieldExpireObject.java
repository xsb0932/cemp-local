package com.landleaf.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author wenyilu
 * redis Hash结构 field过期基类对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RedisFieldExpireObject implements Serializable {

    private long expireTime;

    /**
     * 过期时间
     */
    private long ttl;

    /**
     * 存储的具体时间
     */
    private Object value;
}
