package com.github.twh.redis.transport.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.redis.FixedRedisMessagePool;
import io.netty.handler.codec.redis.InlineCommandRedisMessage;

/**
 * @author wenhai.tan
 * @date 2021/12/22
 */
@ChannelHandler.Sharable
public class InlineCommandMessageHandler extends SimpleChannelInboundHandler<InlineCommandRedisMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InlineCommandRedisMessage msg) {
        String cmd = msg.content();
        if ("ping".equals(cmd)) {
            ctx.writeAndFlush(FixedRedisMessagePool.INSTANCE.getSimpleString(FixedRedisMessagePool.RedisReplyKey.PONG));
        }
    }
}
