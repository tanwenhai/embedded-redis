package com.github.twh.redis.transport.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.LoggerFactory;

/**
 * @author wenhai.tan
 * @date 2021/12/22
 */
public class ServerSocketChannelInitializer extends ChannelInitializer<ServerSocketChannel> {

    @Override
    protected void initChannel(ServerSocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        if (LoggerFactory.getLogger(LoggingHandler.class).isDebugEnabled()) {
            pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
        }
    }
}
