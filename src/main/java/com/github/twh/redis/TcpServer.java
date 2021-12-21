package com.github.twh.redis;

import com.github.twh.redis.handler.RedisServerHandler;
import com.github.twh.redis.handler.RedisMessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.handler.codec.redis.RedisEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

import static com.github.twh.redis.Constant.REDIS_SERVER;

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
        RedisMessageHandler redisMessageHandler = new RedisMessageHandler();
        startFuture = new ServerBootstrap()
            .group(bossGroup, workGroup)
            .channel(NioServerSocketChannel.class)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ch.attr(REDIS_SERVER).set(redisServer);
                    ChannelPipeline pipeline = ch.pipeline();
                    if (log.isDebugEnabled()) {
                        pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
                    }
                    pipeline.addLast(new RedisDecoder(true))
                        .addLast(new RedisEncoder())
                        .addLast(redisMessageHandler);
                }
            })
            .handler(new ChannelInitializer<ServerSocketChannel>() {
                @Override
                protected void initChannel(ServerSocketChannel serverSocketChannel) {
                    serverSocketChannel.pipeline().addLast(new RedisServerHandler());
                }
            })
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
