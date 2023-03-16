package com.zhouj.rpc.idle;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author zhouj
 * @since 2023-03-10
 */
public class IdleHandler extends ChannelDuplexHandler {

    private static final ByteBuf PING = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("ping", CharsetUtil.UTF_8));

    Logger logger = LoggerFactory.getLogger(IdleHandler.class);


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState idleState = ((IdleStateEvent) evt).state();
            if (idleState.equals(IdleState.READER_IDLE)) {
                logger.info("read 空闲,发送心跳!!!:{}", ctx.channel().remoteAddress());
                ctx.writeAndFlush(PING);
            }

        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("read msg:", msg);
        ctx.fireChannelRead(msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        logger.info("write msg:", msg);
        ctx.write(msg);
    }
}
