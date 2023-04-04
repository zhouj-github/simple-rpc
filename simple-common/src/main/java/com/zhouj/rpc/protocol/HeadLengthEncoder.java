package com.zhouj.rpc.protocol;

import com.zhouj.rpc.util.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * head指定body长度编码
 * @author zhouj
 * @since 2023-04-04
 */
public class HeadLengthEncoder extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        byte[] bytes = ProtostuffUtil.serialize(msg);
        //添加分割字符
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
