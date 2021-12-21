package com.github.twh.redis;

/**
 * @author wenhai.tan
 * @date 2021/11/23
 */
public class RedisApplication {

    public static void main(String[] args) {
        RedisServer server = new RedisServer();
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

        server.start();
        server.awaitStop();
        System.out.println("bye!");
    }
}
