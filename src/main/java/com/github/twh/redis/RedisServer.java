package com.github.twh.redis;

import com.github.twh.redis.transport.TcpServer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.Future;

/**
 * @author wenhai.tan
 * @date 2021/11/9
 */
public class RedisServer {

    private TcpServer netServer;

    private final Database[] databases;

    private final RedisConfig config;

    public static RedisServer getInstance() {
        return InstanceHolder.instance;
    }

    private RedisServer() {
        config = RedisConfig.getConfig();
        databases = new Database[config.getPort()];
    }

    public Future<Void> start() {
        printBanner();
        init();
        netServer = new TcpServer(this);
        return netServer.start();
    }

    public void init() {
        // 加载持久化数据
        for (int i = 0; i < databases.length; i++) {
            databases[i] = new Database(i);
        }
    }

    public void stop() {
        if (netServer != null) {
            netServer.stop();
        }
    }

    @SuppressWarnings("squid:S106")
    private void printBanner() {
        try {
            System.out.println(new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getResource("/banner.txt")).toURI())), StandardCharsets.UTF_8));
        } catch (Exception ignored) {
        }
    }

    public Database[] getDatabases() {
        return databases;
    }

    public RedisConfig getConfig() {
        return config;
    }

    public void awaitStop() {
        netServer.awaitStop();
    }

    private static class InstanceHolder {
        static RedisServer instance = new RedisServer();
    }
}
