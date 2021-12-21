package com.github.twh.redis;

import io.netty.util.AttributeKey;

/**
 * @author wenhai.tan
 * @date 2021/12/8
 */
public abstract class Constant {
    public static final AttributeKey<RedisServer> REDIS_SERVER = AttributeKey.valueOf("REDIS_SERVER");
}
