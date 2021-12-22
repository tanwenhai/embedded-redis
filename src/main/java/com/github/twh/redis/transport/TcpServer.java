package com.github.twh.redis.transport;

import com.github.twh.redis.RedisServer;
import com.github.twh.redis.transport.handler.ServerSocketChannelInitializer;
import com.github.twh.redis.transport.handler.SocketChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

/**
 * @author wenhai.tan
 * @date 2021/11/9
 */
public class TcpServer {

    private static final Logger log = LoggerFactory.getLogger(TcpServer.class);

    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workGroup = new NioEventLoopGroup(1);

    private ChannelFuture startFuture;

    private final RedisServer redisServer;

    public TcpServer(RedisServer redisServer) {
        this.redisServer = redisServer;
    }

    public Future<Void> start() {
        bossGroup = new NioEventLoopGroup(1);
        workGroup = new NioEventLoopGroup(1);

        startFuture = new ServerBootstrap()
            .group(bossGroup, workGroup)
            .channel(NioServerSocketChannel.class)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .handler(new ServerSocketChannelInitializer())
            .childHandler(new SocketChannelInitializer())
            .bind(redisServer.getConfig().getHost(), redisServer.getConfig().getPort())
            .addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("server start at {}", future.channel().localAddress());
                }
            });

        return startFuture.syncUninterruptibly();
    }

    public void stop() {
        log.info("Redis Server Close bye bye!");
        startFuture.channel().closeFuture().syncUninterruptibly();
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
        startFuture.channel().close();
    }

    public void awaitStop() {
        startFuture.channel().closeFuture().syncUninterruptibly();
    }
}
