package com.github.twh.redis.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.redis.DefaultLastBulkStringRedisContent;
import io.netty.handler.codec.redis.FixedRedisMessagePool;
import io.netty.handler.codec.redis.RedisMessage;

import java.nio.charset.StandardCharsets;

/**
 * @author wenhai.tan
 * @date 2021/11/9
 */
@ChannelHandler.Sharable
public class RedisMessageHandler extends SimpleChannelInboundHandler<RedisMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RedisMessage msg) throws Exception {
        if (msg instanceof DefaultLastBulkStringRedisContent) {
            String cmd = ((DefaultLastBulkStringRedisContent) msg).content().toString(StandardCharsets.UTF_8);
            if ("ping".equals(cmd)) {
                ctx.writeAndFlush(FixedRedisMessagePool.INSTANCE.getSimpleString(FixedRedisMessagePool.RedisReplyKey.PONG));
            }
        }
    }
}
