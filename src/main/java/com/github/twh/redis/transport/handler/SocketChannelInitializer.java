package com.github.twh.redis.transport.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.handler.codec.redis.RedisEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.LoggerFactory;

/**
 * @author wenhai.tan
 * @date 2021/12/22
 */
public class SocketChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static final RedisMessageHandler REDIS_MESSAGE_HANDLER = new RedisMessageHandler();

    private static final InlineCommandMessageHandler INLINE_COMMAND_MESSAGE_HANDLER = new InlineCommandMessageHandler();

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        if (LoggerFactory.getLogger(LoggingHandler.class).isDebugEnabled()) {
            pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
        }
        pipeline/*.addLast(new MyRedisDecoder())*/
            .addLast(new RedisDecoder(true))
            .addLast(new RedisEncoder())
            .addLast(INLINE_COMMAND_MESSAGE_HANDLER)
            .addLast(REDIS_MESSAGE_HANDLER);
    }
}
