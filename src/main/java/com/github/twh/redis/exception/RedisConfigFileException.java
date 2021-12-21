package com.github.twh.redis.exception;

/**
 * @author wenhai.tan
 * @date 2021/11/23
 */
public class RedisConfigFileException extends RuntimeException {
    public RedisConfigFileException(String message) {
        super(message);
    }

    public RedisConfigFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedisConfigFileException(Throwable cause) {
        super(cause);
    }
}
