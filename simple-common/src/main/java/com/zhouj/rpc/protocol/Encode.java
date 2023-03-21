package com.zhouj.rpc.protocol;

import com.zhouj.rpc.util.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author zhouj
 * @since 2020-08-04
 */
public class Encode extends MessageToByteEncoder {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) {
        byte[] bytes = ProtostuffUtil.serialize(o);
        byteBuf.writeBytes(bytes);
    }
}
