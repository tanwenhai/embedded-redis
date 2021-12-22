package com.github.twh.redis.transport.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.redis.RedisMessageType;
import io.netty.util.ByteProcessor;

/**
 * inline command \n => \r\n
 * @author wenhai.tan
 * @date 2021/12/21
 */
public class MyRedisDecoder extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {
        if (!in.isReadable()) {
            ctx.fireChannelRead(in);
            return;
        }
        RedisMessageType type = RedisMessageType.readFrom(in.retain(), true);
        if (!type.isInline()) {
            ctx.fireChannelRead(in);
            return;
        }
        int lfIndex = in.forEachByte(ByteProcessor.FIND_LF);
        if (lfIndex < 0) {
            ctx.fireChannelRead(in);
            return;
        }
        int crIndex = in.forEachByte(ByteProcessor.FIND_CR);
        // 有\n但是没有\r，添加一个
        if (crIndex > -1) {
            ctx.fireChannelRead(in);
            return;
        }
        ByteBuf data = Unpooled.wrappedBuffer(in.readBytes(lfIndex - in.readerIndex()), Unpooled.wrappedBuffer(new byte[]{'\r', '\n'}));
        in.skipBytes(1);
        // ping\r
        // \n
        ctx.fireChannelRead(data);
    }
}
