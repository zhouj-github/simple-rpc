package com.zhouj.rpc.protocol;

import com.zhouj.rpc.util.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author zhouj
 * @since 2020-08-04
 */
public class Decode extends ByteToMessageDecoder {

    private Class<?> generateClass;

    public Decode(Class<?> generateClass) {
        this.generateClass = generateClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        int length = byteBuf.readableBytes();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        Object o = ProtostuffUtil.deserialize(bytes,generateClass);
        list.add(o);
    }
}
