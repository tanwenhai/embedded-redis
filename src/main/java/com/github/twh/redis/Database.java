package com.github.twh.redis;

import java.util.Map;

/**
 * @author wenhai.tan
 * @date 2021/12/8
 */
public class Database {

    private final int index;

    private Map<byte[], byte[]> data;

    private Map<byte[], byte[]> ttlData;

    public Database(int index) {
        this.index = index;
    }
}
