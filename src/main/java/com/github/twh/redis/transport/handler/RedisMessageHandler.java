package com.github.twh.redis.transport.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.redis.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * @author wenhai.tan
 * @date 2021/11/9
 */
@ChannelHandler.Sharable
public class RedisMessageHandler extends SimpleChannelInboundHandler<RedisMessage> {

    private static final Logger log = LoggerFactory.getLogger(RedisMessageHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RedisMessage msg) {
        if (msg instanceof DefaultLastBulkStringRedisContent) {
            String cmd = ((DefaultLastBulkStringRedisContent) msg).content().toString(StandardCharsets.UTF_8);
            if ("ping".equals(cmd)) {
                ctx.writeAndFlush(FixedRedisMessagePool.INSTANCE.getSimpleString(FixedRedisMessagePool.RedisReplyKey.PONG));
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("redis error", cause);
        ctx.writeAndFlush(FixedRedisMessagePool.INSTANCE.getError(FixedRedisMessagePool.RedisErrorKey.ERR));
    }
}
