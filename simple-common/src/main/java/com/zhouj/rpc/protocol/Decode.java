package com.zhouj.rpc.protocol;

import com.zhouj.rpc.util.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

import java.util.List;

/**
 * @author zhouj
 * @since 2020-08-04
 */
public class Decode extends DelimiterBasedFrameDecoder {

    public Decode(int maxFrameLength, ByteBuf delimiter, Class<?> generateClass) {
        super(maxFrameLength, delimiter);
        this.generateClass = generateClass;
    }

    private Class<?> generateClass;


    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        ByteBuf buff = (ByteBuf) super.decode(ctx, buffer);
        int length = buff.readableBytes();
        byte[] bytes = new byte[length];
        buff.readBytes(bytes);
        return ProtostuffUtil.deserialize(bytes, generateClass);
    }

}
