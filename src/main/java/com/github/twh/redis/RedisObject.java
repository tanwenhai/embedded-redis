package com.github.twh.redis;

/**
 * @author wenhai.tan
 * @date 2021/11/23
 */
public abstract class RedisObject {
    private final int type;

    private final int encoding;

    public RedisObject(int type, int encoding) {
        this.type = type;
        this.encoding = encoding;
    }
}
